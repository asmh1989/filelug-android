package com.filelug.android.crepo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.filelug.android.Constants;
import com.filelug.android.MainApplication;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelSelection;
import com.filelug.android.provider.remoteroot.RemoteRootCursor;
import com.filelug.android.provider.remoteroot.RemoteRootSelection;
import com.filelug.android.provider.remoteroot.RemoteRootType;
import com.filelug.android.ui.model.RemoteFile;
import com.filelug.android.ui.model.RemoteFileObject;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.NetworkUtils;
import com.filelug.android.util.PrefUtils;
import com.filelug.android.util.RemoteFileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RepositoryUtility {

	private static final String TAG = RepositoryUtility.class.getSimpleName();

	private static byte[] getHash(String algorithm, String input) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(algorithm);
		digest.reset();
		return digest.digest(input.getBytes());
	}

	private static String bin2hex(byte[] data) {
		return String.format("%0" + (data.length * 2) + "x", new BigInteger(1, data));
	}

	public static String encrypt2Sha256(String strIn) {
		String str = null;
		try {
			str = bin2hex(getHash("SHA-256", strIn));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String encrypt2MD5(String strIn) {
		String str = null;
		try {
			str = bin2hex(getHash("MD5", strIn));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String encrypt2Base64(String strIn) {
		//return encrypt2Base64(strIn, Base64.DEFAULT);
		return encrypt2Base64(strIn, Base64.NO_WRAP);
	}

	public static String encrypt2Base64(String strIn, int flags) {
		String str = null;
		try {
			byte[] data = strIn.getBytes("UTF-8");
			str = Base64.encodeToString(data, flags);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String generateLoginVerification(String userId, String countryId, String phoneNumber) {
		String v = encrypt2Sha256(userId + "|" + countryId + ":" + phoneNumber) + encrypt2MD5(phoneNumber + "==" + countryId);
//		if ( Constants.DEBUG ) Log.d(TAG, "generateVerification(), generateVerification(), userId=" + userId + ", password=" + password + ", nickname=" + nickname + "\t\t --> code=" + v );
		return v;
	}

	public static String generateVerificationForExchangeAccessToken(String authorizationCode, String locale) {
		String authorizationCodeUpperCase = authorizationCode.toUpperCase();
		String authorizationCodeLowerCase = authorizationCode.toLowerCase();
		String hash = encrypt2MD5(authorizationCodeUpperCase + "==" + locale);
		return encrypt2Sha256(authorizationCodeLowerCase + "|" + hash + ":" + locale + "_" + hash);
	}

	public static String generateVerificationForAccessTokenSecurityCode(String countryId, String phoneNumber) {
		String defaultUserId = "9413";
		return encrypt2Sha256(defaultUserId + "|" + countryId + ":" + phoneNumber) + encrypt2MD5(phoneNumber + "==" + countryId);
	}

	public static String generateDeleteUserVerification(String userId, String nickname, String sessionToken) {
		return encrypt2Sha256(userId + "|" + sessionToken + ":" + nickname) + encrypt2MD5(nickname + "==" + userId);
	}

	public static String generateUploadTransferKey(String sessionId, String uploadFileName) {
		return generateTransferKey(sessionId, uploadFileName, "up");
	}

	public static String generateDownloadTransferKey(String sessionId, String downloadFileName) {
		return generateTransferKey(sessionId, downloadFileName, "down");
	}

	private static String generateTransferKey(String sessionId, String fileName, String upOrDown) {
		String v = sessionId + "+" + encrypt2MD5(fileName) + "+" + upOrDown + "+" + UUID.randomUUID().toString();
//		if ( Constants.DEBUG ) Log.d(TAG, "generateTransferKey(), sessionId=" + sessionId + ", fileName=" + fileName + "\t\t --> code=" + v );
		return v;
	}

	public static String generateUploadGroupId(String str) {
		String v = encrypt2MD5(str+UUID.randomUUID().toString());
//		if ( Constants.DEBUG ) Log.d(TAG, "generateUploadGroupId(), str=" + str + "\t\t --> code=" + v );
		return v;
	}

	public static String generateDownloadGroupId(String str) {
		String v = encrypt2MD5(str+UUID.randomUUID().toString());
//		if ( Constants.DEBUG ) Log.d(TAG, "generateDownloadGroupId(), str=" + str + "\t\t --> code=" + v );
		return v;
	}

	public static String generateVerificationToDeleteComputer(String userId, int computerId) {
		String hash = encrypt2MD5(userId + "==" + computerId);
		return encrypt2Sha256(userId + "|" + hash + ":" + computerId + "_" + hash);
	}

	public interface DataListCallback {
		public void loaded(boolean success);
	}

	public interface DataListCallback2 {
		public void loaded(boolean success, List<RemoteFile> result);
	}

	public static void removeRootDirectories(Context context, String userId, String computerId) {
		RemoteRootSelection remoteRootSelection = new RemoteRootSelection();
		remoteRootSelection
			.userId(userId)
			.and()
			.computerId(Integer.valueOf(computerId));
		remoteRootSelection.delete(context.getContentResolver());
	}

/*
	public static void loadRootDirectories(final Context context, String authToken, String lugServerId, final String userId, final String computerId, final DataListCallback callback) {
		if ( !NetworkUtils.isNetworkAvailable(context) ) {
			if ( callback != null ) {
				callback.loaded(false);
			}
			return;
		}

		String locale = context.getResources().getConfiguration().locale.toString();
		RepositoryClient.getInstance().findAllRootDirectories(
			authToken,
			lugServerId,
			locale,
			new Response.Listener<JSONArray>() {
				@Override
				public void onResponse(JSONArray response) {
					removeRootDirectories(context, userId, computerId);

					try {
						for (int i = 0; i < response.length(); i++) {
							JSONObject jso = response.getJSONObject(i);
							String directoryId = jso.getString(Constants.PARAM_ID);
							String directoryLabel = jso.getString(Constants.PARAM_LABEL);
							String directoryPath = jso.getString(Constants.PARAM_PATH);
							String directoryRealPath = jso.getString(Constants.PARAM_REAL_PATH);
							String type = jso.getString(Constants.PARAM_TYPE);

							RemoteRootContentValues values = new RemoteRootContentValues();
							values.putUserId(userId);
							values.putComputerId(Integer.valueOf(computerId));
							values.putDirectoryId(directoryId);
							values.putLabel(directoryLabel);
							values.putPath(directoryPath);
							values.putRealPath(directoryRealPath);
							values.putType(RootType.valueOf(type));

							Uri uri = values.insert(context.getContentResolver());
							long _id = ContentUris.parseId(uri);

						}
					} catch (JSONException e) {
//						if (Constants.DEBUG) Log.d(TAG, "loadRootDirectories(), RemoteRootDirectory json object parsing error!");
					}

					if ( callback != null ) {
						callback.loaded(true);
					}
				}
			},
			new BaseResponseError(true, context, BaseResponseError.MESSAGE_TYPE_ERROR_MESSAGE) {
				@Override
				protected void afterShowErrorMessage(VolleyError volleyError) {
					if ( callback != null ) {
						callback.loaded(false);
					}
				}
			}
		);
	}
*/

	public static void listRoots(final Context context, String authToken, String lugServerId, final String userId, final String computerId, final DataListCallback2 callback) {
		if ( !NetworkUtils.isNetworkAvailable(context) ) {
			if ( callback != null ) {
				callback.loaded(false, null);
			}
			return;
		}

		String locale = context.getResources().getConfiguration().locale.toString();
		RepositoryClient.getInstance().listRoots(
			authToken,
			lugServerId,
			locale,
			new Response.Listener<JSONArray>() {
				@Override
				public void onResponse(JSONArray response) {
					List<RemoteFile> list = new ArrayList<RemoteFile>();

					try {
						for (int i = 0; i < response.length(); i++) {
							JSONObject jso = response.getJSONObject(i);
							String type = jso.getString(Constants.PARAM_TYPE);
							RemoteRootType rootType = RemoteRootType.valueOf(type);
							RemoteFile.FileType fileType = RemoteFileUtils.convertRemoteRoot(rootType);
							String label = jso.getString(Constants.PARAM_LABEL);
							String path = jso.getString(Constants.PARAM_PATH);
							String realPath = jso.getString(Constants.PARAM_REAL_PATH);

							list.add(new RemoteFileObject(fileType, label, path, realPath));
						}
					} catch (JSONException e) {
						Log.e(TAG, "listRoots(), RemoteRoot json object parsing error!");
					}

					if (callback != null) {
						callback.loaded(true, list);
					}
				}
			},
			new BaseResponseError(true, context, BaseResponseError.MESSAGE_TYPE_ERROR_MESSAGE) {
				@Override
				protected void afterShowErrorMessage(VolleyError volleyError) {
					if (callback != null) {
						callback.loaded(false, null);
					}
				}
			}
		);
	}

	public static List<RemoteFile> getRootDirectories(String userId, String computerId) {
		Context ctx = MainApplication.getInstance().getBaseContext();
		List<RemoteFile> list = new ArrayList<RemoteFile>();
//		String orderBy = RemoteRootColumns.LABEL + " asc";
		String orderBy = null;
		RemoteRootSelection remoteRootSelection = new RemoteRootSelection();
		remoteRootSelection
			.userId(userId)
			.and()
			.computerId(Integer.valueOf(computerId));
		RemoteRootCursor c = remoteRootSelection.query(ctx.getContentResolver(), null, orderBy);
		while (c.moveToNext()) {
			String label = c.getLabel();
			String path = c.getPath();
			String realPath = c.getRealPath();
			RemoteRootType rootType = c.getType();
			RemoteFile.FileType fileType = RemoteFileUtils.convertRemoteRoot(rootType);
			list.add(new RemoteFileObject(fileType, label, path, realPath));
		}
		c.close();
		return list;
	}

/*
	public static List<RemoteFile> getRootDirectories2(final Context context, String authToken, String lugServerId, final String userId, final String computerId, final DataListCallback callback) {
		List<RemoteFile> rootDirectories = getRootDirectories(userId, computerId);
		loadRootDirectories(context, authToken, lugServerId, userId, computerId, callback);
		return rootDirectories;
	}
*/

	public static void removeDirectoryList(Context context, String userId, String computerId, String path) {
		RemoteHierarchicalModelSelection remoteHierarchicalModelSelection = new RemoteHierarchicalModelSelection();
		remoteHierarchicalModelSelection
			.userId(userId).and()
			.computerId(Integer.valueOf(computerId)).and()
			.parent(path);
		remoteHierarchicalModelSelection.delete(context.getContentResolver());
	}

/*
	public static void loadDirectoryList(final Context context, String authToken, String lugServerId, final String userId, final String computerId, final String path, final DataListCallback callback) {
		if ( !NetworkUtils.isNetworkAvailable(context) ) {
			if ( callback != null ) {
				callback.loaded(false);
			}
			return;
		}

		final boolean showHidden = PrefUtils.isShowHiddenFiles();
		final String locale = context.getResources().getConfiguration().locale.toString();

		RepositoryClient.getInstance().list(
			authToken,
			lugServerId,
			path,
			locale,
			new Response.Listener<JSONArray>() {
				@Override
				public void onResponse(JSONArray response) {
					removeDirectoryList(context, userId, computerId, path);

					try {
						for (int i = 0; i < response.length(); i++) {
							JSONObject jso = response.getJSONObject(i);

							boolean symlink = jso.getBoolean(Constants.PARAM_SYMLINK);
							String name = jso.getString(Constants.PARAM_NAME);
							String parent = jso.getString(Constants.PARAM_PARENT);
							boolean readable = jso.getBoolean(Constants.PARAM_READABLE);
							boolean writable = jso.getBoolean(Constants.PARAM_WRITABLE);
							boolean hidden = jso.getBoolean(Constants.PARAM_HIDDEN);
							String lastModified = jso.getString(Constants.PARAM_LAST_MODIFIED);
							String type = jso.getString(Constants.PARAM_TYPE);
							String contentType = jso.getString(Constants.PARAM_CONTENT_TYPE);
							long sizeInBytes = jso.getLong(Constants.PARAM_SIZE_IN_BYTES);
							String realParent = jso.getString(Constants.PARAM_REAL_PARENT);
							String realName = jso.getString(Constants.PARAM_REAL_NAME);

							RemoteHierarchicalModelContentValues values = new RemoteHierarchicalModelContentValues();
							values.putUserId(userId);
							values.putComputerId(Integer.valueOf(computerId));
							values.putParent(parent);
							values.putName(name);
							values.putSymlink(symlink);
							values.putReadable(readable);
							values.putWritable(writable);
							values.putHidden(hidden);
							values.putLastModified(lastModified);
							values.putType(RemoteObjectType.valueOf(type));
							values.putContentType(contentType);
							values.putSize(sizeInBytes);
							values.putRealParent(realParent);
							values.putRealName(realName);

							Uri uri = values.insert(context.getContentResolver());
							long _id = ContentUris.parseId(uri);

						}
					} catch (JSONException e) {
//						if (Constants.DEBUG) Log.d(TAG, "loadDirectoryList(), RemoteHierarchicalModel json object parsing error!");
					}

					if (callback != null) {
						callback.loaded(true);
					}
				}
			},
			new BaseResponseError(true, context, BaseResponseError.MESSAGE_TYPE_ERROR_MESSAGE) {
				@Override
				protected void afterShowErrorMessage(VolleyError volleyError) {
					if (callback != null) {
						callback.loaded(false);
					}
				}
			}
		);
	}
*/

	public static void loadDirectoryList2(final Context context, String authToken, String lugServerId, final String userId, final String computerId, final String path, final boolean showFoldersOnly, final String acceptedType, final DataListCallback2 callback) {
		if ( !NetworkUtils.isNetworkAvailable(context) ) {
			if ( callback != null ) {
				callback.loaded(false, null);
			}
			return;
		}

		Account activeAccount = AccountUtils.getActiveAccount();
		AccountManager accountManager = AccountManager.get(context);
		final String fileSeparator = accountManager.getUserData(activeAccount, Constants.PARAM_FILE_SEPARATOR);
		final boolean showHidden = PrefUtils.isShowHiddenFiles();

		final String locale = context.getResources().getConfiguration().locale.toString();
		RepositoryClient.getInstance().list(
				authToken,
				lugServerId,
				path,
				showHidden,
				locale,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						List<RemoteFile> list = new ArrayList<RemoteFile>();

						try {
							for (int i = 0; i < response.length(); i++) {
								JSONObject jso = response.getJSONObject(i);
								RemoteFileObject fileObject = new RemoteFileObject(jso, fileSeparator);
								if ( !fileObject.getType().isDirectory() ) {
									if ( showFoldersOnly ) continue;
									if ( !MiscUtils.isAcceptedContentType(acceptedType, fileObject.getContentType()) ) {
										fileObject.setSelectable(false);
									}
								}
								boolean isHiddenFile = fileObject.isHidden() || fileObject.getName().startsWith(".");
								if ( !showHidden && isHiddenFile ) {
									continue;
								}
								list.add(fileObject);
							}
						} catch (JSONException e) {
//							if (Constants.DEBUG) Log.d(TAG, "loadDirectoryList2(), RemoteHierarchicalModel json object parsing error!");
						}

						if (callback != null) {
							callback.loaded(true, list);
						}
					}
				},
				new BaseResponseError(true, context, BaseResponseError.MESSAGE_TYPE_ERROR_MESSAGE) {
					@Override
					protected void afterShowErrorMessage(VolleyError volleyError) {
						if (callback != null) {
							callback.loaded(false, null);
						}
					}
				}
		);
	}

/*
	public static List<RemoteFile> getDirectoryList(String userId, String computerId, String path, boolean showFoldersOnly) {
		Context ctx = MainApplication.getInstance().getBaseContext();
		Account activeAccount = AccountUtils.getActiveAccount(ctx);
		AccountManager accountManager = AccountManager.get(ctx);
		String fileSeparator = accountManager.getUserData(activeAccount, Constants.PARAM_FILE_SEPARATOR);
		boolean showHidden = PrefUtils.isShowHiddenFiles();
		List<RemoteFile> list = new ArrayList<RemoteFile>();
//		String orderBy = RemoteHierarchicalModelColumns.PARENT + " asc, " + RemoteHierarchicalModelColumns.NAME + " asc";
		String orderBy = null;
		RemoteHierarchicalModelSelection remoteHierarchicalModelSelection = new RemoteHierarchicalModelSelection();
		remoteHierarchicalModelSelection
			.userId(userId)
			.and()
			.computerId(Integer.valueOf(computerId))
			.and()
			.parent(path);
		RemoteHierarchicalModelCursor c = remoteHierarchicalModelSelection.query(ctx.getContentResolver(), null, orderBy);
		while (c.moveToNext()) {
			RemoteFileObject fileObject = new RemoteFileObject(c, fileSeparator);
			if ( !showFoldersOnly || ( showFoldersOnly && fileObject.getType().isDirectory() ) ) {
				boolean isHiddenFile = fileObject.isHidden() || fileObject.getName().startsWith(".");
				if ( showHidden || ( !showHidden && !isHiddenFile ) )
					list.add(fileObject);
			}
		}
		c.close();
		return list;
	}
*/

/*
	public static void findByPath(final Context context, String authToken, String lugServerId, final String userId, final String computerId, final String path, final DataListCallback callback) {
		if ( !NetworkUtils.isNetworkAvailable(context) ) {
			if ( callback != null ) {
				callback.loaded(false);
			}
			return;
		}

		String locale = context.getResources().getConfiguration().locale.toString();
		RepositoryClient.getInstance().list(
				authToken,
				lugServerId,
				path,
				locale,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						removeDirectoryList(context, userId, computerId, path);

						try {
							for (int i = 0; i < response.length(); i++) {
								JSONObject jso = response.getJSONObject(i);

								boolean symlink = jso.getBoolean(Constants.PARAM_SYMLINK);
								String name = jso.getString(Constants.PARAM_NAME);
								String parent = jso.getString(Constants.PARAM_PARENT);
								boolean readable = jso.getBoolean(Constants.PARAM_READABLE);
								boolean writable = jso.getBoolean(Constants.PARAM_WRITABLE);
								boolean hidden = jso.getBoolean(Constants.PARAM_HIDDEN);
								String lastModified = jso.getString(Constants.PARAM_LAST_MODIFIED);
								String type = jso.getString(Constants.PARAM_TYPE);
								String contentType = jso.getString(Constants.PARAM_CONTENT_TYPE);
								long sizeInBytes = jso.getLong(Constants.PARAM_SIZE_IN_BYTES);
								String realParent = jso.getString(Constants.PARAM_REAL_PARENT);
								String realName = jso.getString(Constants.PARAM_REAL_NAME);

								RemoteHierarchicalModelContentValues values = new RemoteHierarchicalModelContentValues();
								values.putUserId(userId);
								values.putComputerId(Integer.valueOf(computerId));
								values.putParent(parent);
								values.putName(name);
								values.putSymlink(symlink);
								values.putReadable(readable);
								values.putWritable(writable);
								values.putHidden(hidden);
								values.putLastModified(lastModified);
								values.putType(RemoteObjectType.valueOf(type));
								values.putContentType(contentType);
								values.putSize(sizeInBytes);
								values.putRealParent(realParent);
								values.putRealName(realName);

								Uri uri = values.insert(context.getContentResolver());
								long _id = ContentUris.parseId(uri);

							}
						} catch (JSONException e) {
//							if (Constants.DEBUG) Log.d(TAG, "findByPath(), RemoteHierarchicalModel json object parsing error!");
						}

						if (callback != null) {
							callback.loaded(true);
						}
					}
				},
				new BaseResponseError(true, context, BaseResponseError.MESSAGE_TYPE_ERROR_MESSAGE) {
					@Override
					protected void afterShowErrorMessage(VolleyError volleyError) {
						if (callback != null) {
							callback.loaded(false);
						}
					}
				}
		);
	}
*/

}
