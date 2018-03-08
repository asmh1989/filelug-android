package com.filelug.android.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.BaseResponseError;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.crepo.RepositoryErrorObject;
import com.filelug.android.crepo.RepositoryUtility;
import com.filelug.android.provider.downloadgroup.DownloadGroupContentValues;
import com.filelug.android.provider.downloadgroup.DownloadGroupCursor;
import com.filelug.android.provider.downloadgroup.DownloadGroupSelection;
import com.filelug.android.provider.filetransfer.DownloadStatusType;
import com.filelug.android.provider.filetransfer.FileTransferColumns;
import com.filelug.android.provider.filetransfer.FileTransferContentValues;
import com.filelug.android.provider.filetransfer.FileTransferCursor;
import com.filelug.android.provider.filetransfer.FileTransferSelection;
import com.filelug.android.provider.filetransfer.RemoteObjectType;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelColumns;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelContentValues;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelCursor;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelSelection;
import com.filelug.android.provider.remoteroot.RemoteRootType;
import com.filelug.android.provider.usercomputer.UserComputerColumns;
import com.filelug.android.provider.usercomputer.UserComputerCursor;
import com.filelug.android.provider.usercomputer.UserComputerSelection;
import com.filelug.android.service.ContentType;
import com.filelug.android.service.DownloadRequest;
import com.filelug.android.service.DownloadService;
import com.filelug.android.ui.model.RemoteFile;
import com.filelug.android.ui.model.RemoteFileObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vincent Chang on 2015/8/2.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class RemoteFileUtils {

	private static final String TAG = RemoteFileUtils.class.getSimpleName();

	public interface FileArrayCallback {
		public void loaded(boolean success, RemoteFile[] files);
	}
	public interface FileObjectListCallback {
		public void loaded(boolean success, List<RemoteFile> remoteFileList);
	}
	public interface FileDownloadSummaryCallback {
		public void created(String downloadGroupId);
		public void failed(String message);
	}

	public static RemoteFileObject getRemoteRoot(Context context) {
		return new RemoteFileObject(RemoteFile.FileType.ROOT, context.getResources().getString(R.string.fileType_desktop_root));
	}

	public static void findRemoteFileObjectList(final Context context, RemoteFile folder, final boolean showFoldersOnly, final String acceptedType, final RemoteFileUtils.FileObjectListCallback callback) {

		Account activeAccount = AccountUtils.getActiveAccount();
		AccountManager accountManager = AccountManager.get(context);
		final String lugServerId = accountManager.getUserData(activeAccount, Constants.PARAM_LUG_SERVER_ID);
		final String userId = accountManager.getUserData(activeAccount, Constants.EXT_PARAM_FILELUG_ACCOUNT);
		final String computerId = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_ID);
		final String computerName = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_NAME);

		AccountUtils.AuthTokenCallback authTokenCallback = null;
		RemoteFile.FileType type = folder.getType();

		if ( type == RemoteFile.FileType.ROOT ) {

			authTokenCallback = new AccountUtils.AuthTokenCallback() {
				@Override
				public void onError(String errorMessage) {
					MsgUtils.showWarningMessage(context, errorMessage);
					if ( callback != null ) {
						callback.loaded(false, null);
					}
				}
				@Override
				public void onSuccess(String authToken) {
					// 取得 JSON 後, 進 Local DB 再取出
					/*
					RepositoryUtility.DataListCallback dataListCallback = new RepositoryUtility.DataListCallback() {
						@Override
						public void loaded(boolean success) {
							if ( callback != null ) {
								if ( success ) {
									callback.loaded(true, RepositoryUtility.getRootDirectories(userId, computerId));
								} else {
									callback.loaded(false, null);
								}
							}
						}
					};
					RepositoryUtility.loadRootDirectories(context, authToken, lugServerId, userId, computerId, dataListCallback);
					*/
					// 取得 JSON 後, 直接送出
					//
					RepositoryUtility.DataListCallback2 dataListCallback = new RepositoryUtility.DataListCallback2() {
						@Override
						public void loaded(boolean success, List<RemoteFile> result) {
							if ( callback != null ) {
								if ( success ) {
									callback.loaded(true, result);
								} else {
									callback.loaded(false, null);
								}
							}
						}
					};
					RepositoryUtility.listRoots(context, authToken, lugServerId, userId, computerId, dataListCallback);
					//
				}
			};

		} else if ( type.isDirectory() ) {

			String tmpFullName = null;
			if ( folder.isSymlink() ) {
				tmpFullName = folder.getFullRealName();
			} else {
				tmpFullName = folder.getFullName();
			}

			final String fullName = tmpFullName;

			authTokenCallback = new AccountUtils.AuthTokenCallback() {
				@Override
				public void onError(String errorMessage) {
					MsgUtils.showWarningMessage(context, errorMessage);
					if ( callback != null ) {
						callback.loaded(false, null);
					}
				}
				@Override
				public void onSuccess(String authToken) {
					// 取得 JSON 後, 進 Local DB 再取出
					/*
					RepositoryUtility.DataListCallback dataListCallback = new RepositoryUtility.DataListCallback() {
						@Override
						public void loaded(boolean success) {
							if ( callback != null ) {
								if ( success ) {
									callback.loaded(true, RepositoryUtility.getDirectoryList(userId, computerId, fullName));
								} else {
									callback.loaded(false, null);
								}
							}
						}
					};
					RepositoryUtility.loadDirectoryList(context, authToken, lugServerId, userId, computerId, fullName, dataListCallback);
					*/
					// 取得 JSON 後, 直接送出
					RepositoryUtility.DataListCallback2 dataListCallback = new RepositoryUtility.DataListCallback2() {
						@Override
						public void loaded(boolean success, List<RemoteFile> result) {
							if ( callback != null ) {
								if ( success ) {
									callback.loaded(true, result);
								} else {
									callback.loaded(false, null);
								}
							}
						}
					};
					RepositoryUtility.loadDirectoryList2(context, authToken, lugServerId, userId, computerId, fullName, showFoldersOnly, acceptedType, dataListCallback);
				}
			};
		}

		AccountUtils.getAuthToken((Activity)context, authTokenCallback);
	}

	public static void downloadFiles(Context context, String userId, int computerId, String lugServerId, String authToken, String groupId, int notificationType, String downDir, List<Bundle> remoteFiles, boolean fromAnotherApp) {
		for ( Bundle downloadBundle : remoteFiles ) {
			String transferKey = downloadBundle.getString(Constants.PARAM_TRANSFER_KEY);
			RemoteFileObject remoteFile = (RemoteFileObject)downloadBundle.getParcelable(Constants.EXT_PARAM_REMOTE_FILE_OBJECT);
			boolean fileInCache = downloadBundle.getBoolean(Constants.EXT_PARAM_FILE_IN_CACHE, false);

			String filePath = remoteFile.getParent();
			String fileName = remoteFile.getName();
			String fullName = remoteFile.getFullName();
			String fileRealPath = remoteFile.getRealParent();
			String fileRealName = remoteFile.getRealName();
			String fullRealName = remoteFile.getFullRealName();
			long fileSize = remoteFile.getSize();
			String fileLastModified = remoteFile.getLastModified();
			String contentType = remoteFile.getContentType();
			if ( TextUtils.isEmpty(contentType) ) {
				String extension = MiscUtils.getExtension(fileRealName);
				MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
				if ( mimeTypeMap.hasExtension(extension) ) {
					contentType = mimeTypeMap.getMimeTypeFromExtension(extension);
				} else {
					contentType = ContentType.APPLICATION_OCTET_STREAM;
				}
			}
			RemoteObjectType objectType = RemoteObjectType.FILE;
			RemoteFile.FileType fileType = remoteFile.getType();
			if ( fileType.equals(RemoteFile.FileType.REMOTE_WINDOWS_SHORTCUT_FILE)  ) {
				objectType = RemoteObjectType.WINDOWS_SHORTCUT_FILE;
			} else if ( fileType.equals(RemoteFile.FileType.REMOTE_UNIX_SYMBOLIC_LINK_FILE)  ) {
				objectType = RemoteObjectType.UNIX_SYMBOLIC_LINK_FILE;
			} else if ( fileType.equals(RemoteFile.FileType.REMOTE_MAC_ALIAS_FILE)  ) {
				objectType = RemoteObjectType.MAC_ALIAS_FILE;
			}

			FileTransferContentValues values = new FileTransferContentValues()
				.putUserId(userId)
				.putComputerId(computerId)
				.putGroupId(groupId)
				.putTransferKey(transferKey)
				.putType(objectType)
				.putServerPath(fullName)
				.putRealServerPath(fullRealName)
				.putLocalFileName(fileName)
				.putRealLocalFileName(fileRealName)
				.putTotalSize(fileSize)
				.putContentType(contentType)
				.putLastModified(fileLastModified)
				.putStatus(DownloadStatusType.wait)
				.putWaitToConfirm(false)
				.putFileInCache(fileInCache);

			if ( fileInCache ) {
				long currentTime = new Date().getTime();
				values.putStatus(DownloadStatusType.success)
					.putStartTimestamp(currentTime)
					.putEndTimestamp(currentTime)
					.putTransferredSize(fileSize)
					.insert(context.getContentResolver());
				continue;
			} else {
				values.putStatus(DownloadStatusType.wait)
					.putTransferredSize(0l)
					.insert(context.getContentResolver());
			}

			DownloadRequest request = new DownloadRequest(context, userId, computerId, groupId, transferKey, lugServerId, authToken, notificationType, fromAnotherApp);
			request.addFileToDownload(filePath, fileName, fullName, fileRealPath, fileRealName, fullRealName, fileSize, fileLastModified, downDir, false, 0l);
			request.setNotificationConfig(fileName);
			request.addHeader(Constants.HTTP_HEADER_AUTHORIZATION, authToken);
			String requestBody = null;
			try {
				requestBody = URLEncoder.encode(transferKey, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
			request.addParameter(Constants.PARAM_T, requestBody);

			// if you comment the following line, the system default user-agent will be used
			request.setCustomUserAgent("FilelugDownloadService-Android/1.0");

			try {
//				if ( Constants.DEBUG ) Log.d(TAG, "DownloadService.startDownload(request), fileName=" + fileName + ", fileRealName=" + fileRealName + ", transferKey=" + transferKey);
				DownloadService.startDownload(request);
			} catch (Exception exc) {
				Toast.makeText(context, "Malformed download request. " + exc.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			}

		}
	}

	public static void downloadFile(Context context, String userId, int computerId, String lugServerId, String authToken, String groupId, String transferKey, boolean fromAnotherApp) {
		DownloadGroupSelection downloadGroupSelection = new DownloadGroupSelection();
		downloadGroupSelection
			.userId(userId).and()
			.computerId(computerId).and()
			.groupId(groupId);
		DownloadGroupCursor c1 = downloadGroupSelection.query(context.getContentResolver(), null, null);
		if ( !c1.moveToFirst() ) {
			c1.close();
			Log.e(TAG, "downloadFile(), Group ID: " + groupId + " not found!");
			return;
		}

		String downloadDir = c1.getLocalPath();
		int notificationType =  c1.getNotificationType();
		c1.close();

		FileTransferSelection fileTransferSelection = new FileTransferSelection();
		fileTransferSelection
			.userId(userId).and()
			.computerId(computerId).and()
			.groupId(groupId).and()
			.transferKey(transferKey);
		FileTransferCursor c2 = fileTransferSelection.query(context.getContentResolver(), null, null);
		if ( !c2.moveToFirst() ) {
			c2.close();
			Log.e(TAG, "downloadFile(), Transfer key: " + transferKey + " not found!");
			return;
		}
		long totalSize = c2.getTotalSize();
		String fileName = c2.getLocalFileName();
		String fileRealName = c2.getRealLocalFileName();
		String fullName = c2.getServerPath();
		String fullRealName = c2.getRealServerPath();
		String fileLastModified = c2.getLastModified();
		c2.close();

		FileTransferContentValues values = new FileTransferContentValues();
		values.putWaitToConfirm(false)
			.putTotalSize(totalSize)
			.putTransferredSize(0l)
			.putStartTimestampNull()
			.putEndTimestampNull()
			.putWaitToConfirm(false)
			.putStatus(DownloadStatusType.wait);
		values.update(context.getContentResolver(), fileTransferSelection);

		DownloadRequest request = new DownloadRequest(context, userId, computerId, groupId, transferKey, lugServerId, authToken, notificationType, fromAnotherApp);
		request.addFileToDownload(null, fileName, fullName, null, fileRealName, fullRealName, totalSize, fileLastModified, downloadDir, false, 0l);
		request.setNotificationConfig(fileName);
		request.addHeader(Constants.HTTP_HEADER_AUTHORIZATION, authToken);
		String requestBody = null;
		try {
			requestBody = URLEncoder.encode(transferKey, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		request.addParameter(Constants.PARAM_T, requestBody);

		// if you comment the following line, the system default user-agent will be used
		request.setCustomUserAgent("FilelugDownloadService-Android/1.0");

		try {
//			if ( Constants.DEBUG ) Log.d(TAG, "DownloadService.startDownload(request), fileName=" + fileName + ", transferKey=" + transferKey);
			DownloadService.startDownload(request);
		} catch (Exception exc) {
			Toast.makeText(context, "Malformed download request. " + exc.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	public static void downloadFile_resume(Context context, String userId, int computerId, String lugServerId, String authToken, String groupId, String transferKey, boolean fromAnotherApp) {
		DownloadGroupSelection downloadGroupSelection = new DownloadGroupSelection();
		downloadGroupSelection
			.userId(userId).and()
			.computerId(computerId).and()
			.groupId(groupId);
		DownloadGroupCursor c1 = downloadGroupSelection.query(context.getContentResolver(), null, null);
		if ( !c1.moveToFirst() ) {
			c1.close();
			Log.e(TAG, "downloadFile(), Group ID: " + groupId + " not found!");
			return;
		}

		String downloadDir = c1.getLocalPath();
		int notificationType =  c1.getNotificationType();
		c1.close();

		FileTransferSelection fileTransferSelection = new FileTransferSelection();
		fileTransferSelection
			.userId(userId).and()
			.computerId(computerId).and()
			.groupId(groupId).and()
			.transferKey(transferKey);
		FileTransferCursor c2 = fileTransferSelection.query(context.getContentResolver(), null, null);
		if ( !c2.moveToFirst() ) {
			c2.close();
			Log.e(TAG, "downloadFile(), Transfer key: " + transferKey + " not found!");
			return;
		}
		long totalSize = c2.getTotalSize();
		String fileName = c2.getLocalFileName();
		String fileRealName = c2.getRealLocalFileName();
		String fullName = c2.getServerPath();
		String fullRealName = c2.getRealServerPath();
		String lastModifiedStr = c2.getLastModified();
		long lastModified = FormatUtils.convertRemoteFileLastModifiedToTimestamp(context, lastModifiedStr);
		c2.close();

		boolean isResume = false;
		long tmpCachedFileSize = 0L;
		String cachedFileName = fileRealName + "." + totalSize + "." + lastModified + Constants.DOWNLOAD_FILENAME_SUFFIX;
		File cachedFile = new File(downloadDir + File.separator + cachedFileName);
		if ( cachedFile.exists() && cachedFile.canRead() ) {
			isResume = true;
			tmpCachedFileSize = cachedFile.length();
		}

		FileTransferContentValues values = new FileTransferContentValues()
			.putEndTimestampNull()
			.putStatus(DownloadStatusType.wait)
			.putWaitToConfirm(false)
			.putTransferredSize(tmpCachedFileSize);
		values.update(context.getContentResolver(), fileTransferSelection);

		DownloadRequest request = new DownloadRequest(context, userId, computerId, groupId, transferKey, lugServerId, authToken, notificationType, fromAnotherApp);
		request.addFileToDownload(null, fileName, fullName, null, fileRealName, fullRealName, totalSize, lastModifiedStr, downloadDir, isResume, tmpCachedFileSize);
		request.setNotificationConfig(fileName);
		request.addHeader(Constants.HTTP_HEADER_AUTHORIZATION, authToken);
		if ( isResume ) {
			String ifRange = FormatUtils.convertTimestampToDownloadRequestIfRange(context, lastModified);
			request.addHeader(Constants.HTTP_HEADER_IF_RANGE, ifRange);
			request.addHeader(Constants.HTTP_HEADER_RANGE, "bytes=" + String.valueOf(tmpCachedFileSize)+"-");
		}
		String requestBody = null;
		try {
			requestBody = URLEncoder.encode(transferKey, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		request.addParameter(Constants.PARAM_T, requestBody);

		// if you comment the following line, the system default user-agent will be used
		request.setCustomUserAgent("FilelugDownloadService-Android/1.0");

		try {
//			if ( Constants.DEBUG ) Log.d(TAG, "DownloadService.startDownload(request), fileName=" + fileName + ", transferKey=" + transferKey);
			DownloadService.startDownload(request);
		} catch (Exception exc) {
			Toast.makeText(context, "Malformed download request. " + exc.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	// ConfirmDownloadActivity.doDownload() -->
	// FLFileDocumentCursor.downloadCacheFile() -->
	// OpenFromFilelugActivity.doDownload() -->
	// RemoteFilesProvider.downloadCacheFile() -->
	public static String createFileDownloadSummary(final Context context, final String userId, final int computerId, final String lugServerId, final String authToken, final RemoteFile[] selectedFiles, final String downloadToFolder, final int subDirType, final String customizedSubDirName, final int descriptionType, final String customizedDescription, final int notificationType, final FileDownloadSummaryCallback callback, final boolean fromAnotherApp) throws Exception {
		String lineSeparator = "\n";

		List<Bundle> tmpDownloadFiles = new LinkedList<Bundle>();
		Map<String, String> keyPaths = new LinkedHashMap<String, String>();
		String fileListStr = context.getResources().getString(R.string.content_header_file_download_list) + lineSeparator;
		String fileNameCombineStr = "";
		for ( RemoteFile downloadFile : selectedFiles ) {
			String name = downloadFile.getRealName();
			String fullRealName = downloadFile.getFullRealName();
			String transferKey = RepositoryUtility.generateDownloadTransferKey(userId, name);
			keyPaths.put(transferKey, fullRealName);
			boolean fileInCache = false;
			if ( fromAnotherApp ) {
				fileInCache = isFileInCache(context, userId, computerId, downloadFile);
			}
			Bundle downloadBundle = new Bundle();
			downloadBundle.putString(Constants.PARAM_TRANSFER_KEY, transferKey);
			downloadBundle.putParcelable(Constants.EXT_PARAM_REMOTE_FILE_OBJECT, (RemoteFileObject)downloadFile);
			downloadBundle.putBoolean(Constants.EXT_PARAM_FILE_IN_CACHE, fileInCache);
			tmpDownloadFiles.add(downloadBundle);
			if ( !fileInCache ) {
				fileListStr += name + lineSeparator;
				fileNameCombineStr += name;
			}
		}
		final List<Bundle> downloadFiles = tmpDownloadFiles;

		final String subDirValue = MiscUtils.getCustomizedSubDirName(context, subDirType, customizedSubDirName);
		final String descriptionValue = MiscUtils.getCustomizedDescription(context, descriptionType, customizedDescription, fileListStr, lineSeparator);
		final String downloadGroupId = RepositoryUtility.generateDownloadGroupId(fileNameCombineStr);

		final File downloadPath;
		final File descriptionFile;
		try {
			downloadPath = createOrGetLocalPath(context, downloadToFolder, subDirType, subDirValue);
			descriptionFile = writeDescription(context, downloadPath, descriptionType, descriptionValue);
		} catch (Exception e) {
			if ( callback != null ) {
				callback.failed(e.getMessage());
				return null;
			} else {
				throw e;
			}
		}

		final DownloadGroupContentValues values = new DownloadGroupContentValues()
			.putUserId(userId)
			.putComputerId(computerId)
			.putGroupId(downloadGroupId)
			.putFromAnotherApp(fromAnotherApp)
			.putStartTimestamp(new Date().getTime())
			.putLocalPath(downloadPath.getPath())
			.putSubdirectoryType(subDirType)
			.putSubdirectoryValue(subDirValue)
			.putDescriptionType(descriptionType)
			.putDescriptionValue(descriptionValue)
			.putNotificationType(notificationType);

		String locale = context.getResources().getConfiguration().locale.toString();

		// Call from ConfirmDownloadActivity.doDownload() or OpenFromFilelugActivity.doDownload()
		if ( callback != null ) {
			Response.Listener<String> listener = new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					values.insert(context.getContentResolver());
					downloadFiles(context, userId, computerId, lugServerId, authToken, downloadGroupId, notificationType, downloadPath.getPath(), downloadFiles, fromAnotherApp);
					callback.created(downloadGroupId);
				}
			};
			Response.ErrorListener errorListener = new BaseResponseError(false, context) {
				@Override
				public void onErrorResponse(VolleyError volleyError) {
					super.onErrorResponse(volleyError);
					if ( descriptionType != 0 ) {
						descriptionFile.delete();
					}
					if ( subDirType != 0 && descriptionFile != null ) {
						descriptionFile.getParentFile().delete();
					}
					callback.failed(getMessage(volleyError));
				}
			};
			RepositoryClient.getInstance().createFileDownloadSummary(authToken, lugServerId, downloadGroupId, keyPaths, downloadPath.getPath(), subDirType, customizedSubDirName, descriptionType, customizedDescription, notificationType, locale, listener, errorListener);

			return null;
		}

		// Call from FLFileDocumentCursor.downloadCacheFile() or RemoteFilesProvider.downloadCacheFile()
		RequestFuture<String> future = RequestFuture.newFuture();
		int timeOut = context.getResources().getInteger(R.integer.sync_timeout_a);
		RepositoryClient.getInstance().createFileDownloadSummary(authToken, lugServerId, downloadGroupId, keyPaths, downloadPath.getPath(), subDirType, customizedSubDirName, descriptionType, customizedDescription, notificationType, locale, future, future);
		String response = null;
		RepositoryErrorObject errorObject = null;

		try {
			response = future.get(timeOut, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			Account account = AccountUtils.getAccountByUserId(userId);
			errorObject = MiscUtils.getErrorObject(context, e, account);
		}

		if ( errorObject != null ) {
			if ( descriptionType != 0 ) {
				descriptionFile.delete();
			}
			if ( subDirType != 0 && descriptionFile != null ) {
				descriptionFile.getParentFile().delete();
			}
			throw new Exception(errorObject.getMessage());
		}

		values.insert(context.getContentResolver());
		downloadFiles(context, userId, computerId, lugServerId, authToken, downloadGroupId, notificationType, downloadPath.getPath(), downloadFiles, fromAnotherApp);

		return downloadGroupId;
	}

	public static File createOrGetLocalPath(Context context, String downloadToFolder, int subDirType, String subDirValue) throws Exception {
		File downloadPath;

		if ( subDirType != 0 ) {
			int i = 1;
			File subFolder = new File(downloadToFolder, subDirValue);
			while ( subFolder.exists() && subFolder.isDirectory() ) {
				String fName = subDirValue + "-" + i;
				subFolder = new File(downloadToFolder, fName);
				i++;
			}
			downloadPath = subFolder;
		} else {
			downloadPath = new File(downloadToFolder);
		}

		return downloadPath;
	}

	public static File writeDescription(Context context, @NonNull File downloadPath, int descriptionType, @NonNull String descriptionText) throws Exception {
		if ( !downloadPath.exists() ) {
			if ( !downloadPath.mkdir() ) {
				throw new Exception(context.getString(R.string.message_could_not_create_subdirectory));
			}
		}

		if ( descriptionType == 0 ) {
			return null;
		}

		String fileBaseName = context.getString(R.string.file_download_group_description_base_name);

		int i = 1;
		File descriptionFile = new File(downloadPath, fileBaseName + ".txt");
		while ( descriptionFile.exists() && !descriptionFile.isDirectory() ) {
			String fName = fileBaseName + "-" + i + ".txt";
			descriptionFile = new File(downloadPath, fName);
			i++;
		}

		try {
			FileWriter writer = new FileWriter(descriptionFile);
			writer.append(descriptionText);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			throw new Exception(context.getString(R.string.message_could_not_create_description_file));
		}

		return descriptionFile;
	}

	public static void updateRemoteHierarchicalModelLastModified(Context context, String userId, int computerId, String parent, String name, long lastModified, long size) {
		RemoteHierarchicalModelSelection selection = new RemoteHierarchicalModelSelection()
			.userId(userId).and()
			.computerId(computerId).and()
			.parent(parent).and()
			.name(name);
		RemoteHierarchicalModelContentValues values = new RemoteHierarchicalModelContentValues()
			.putLocalLastModified(lastModified)
			.putLocalSize(size);
		values.update(context.getContentResolver(), selection);
	}

	public static void updateRemoteHierarchicalModelLastAccess(Context context, long rowId, long lastAccess) {
		RemoteHierarchicalModelSelection selection = new RemoteHierarchicalModelSelection()
			.id(rowId);
		RemoteHierarchicalModelContentValues values = new RemoteHierarchicalModelContentValues()
			.putLocalLastAccess(lastAccess);
		values.update(context.getContentResolver(), selection);
	}

	private static boolean isFileInCache(Context context, String userId, int computerId, RemoteFile downloadFile) {
		String remoteRealParent = downloadFile.getRealParent();
		String remoteRealName = downloadFile.getRealName();
		String remoteParent = downloadFile.getParent();
		String remoteName = downloadFile.getName();
		String remoteLastModified = downloadFile.getLastModified();
		long remoteSize = downloadFile.getSize();
		boolean needToUpdateOrDownload = false;

		RemoteHierarchicalModelContentValues values = new RemoteHierarchicalModelContentValues();

		RemoteHierarchicalModelSelection selection = new RemoteHierarchicalModelSelection()
			.userId(userId).and()
			.computerId(computerId).and()
			.parent(remoteParent).and()
			.name(remoteName);
		RemoteHierarchicalModelCursor c = selection.query(context.getContentResolver(), RemoteHierarchicalModelColumns.ALL_COLUMNS);

		if ( c.moveToFirst() ) {

			String dbLastModified = c.getLastModified();
			long dbSize = c.getSize();
			Long dbLocalLastModified = c.getLocalLastModified();
			Long dbLocalSize = c.getLocalSize();
			com.filelug.android.provider.remotehierarchicalmodel.RemoteObjectType objectType = RemoteFileUtils.convertRemoteFileObject(downloadFile.getType());

			values.putSymlink(downloadFile.isSymlink())
				.putReadable(downloadFile.isReadable())
				.putWritable(downloadFile.isWritable())
				.putHidden(downloadFile.isHidden())
				.putType(objectType)
				.putContentType(downloadFile.getContentType())
				.putRealParent(remoteRealParent)
				.putRealName(remoteRealName);

			// Check DB data
			if ( !TextUtils.equals(remoteLastModified, dbLastModified) ) {
				needToUpdateOrDownload = true;
				values.putLastModified(remoteLastModified);
				values.putLocalLastAccessNull();
			}
			if ( remoteSize != dbSize ) {
				needToUpdateOrDownload = true;
				values.putSize(remoteSize);
			}

			// Check Cache Dir
			String cacheDirName = FileCache.createDirInActiveAccountCache(remoteRealParent);
			if ( TextUtils.isEmpty(cacheDirName) ) {
				needToUpdateOrDownload = true;
				values.putLocalLastModifiedNull();
				values.putLocalSizeNull();
				values.putLocalLastAccessNull();
			} else {
				File cacheFile = new File(cacheDirName + File.separator + remoteRealName);
				if ( cacheFile.exists() ) {
					long fileLastModified = cacheFile.lastModified();
					if ( dbLocalLastModified == null || dbLocalLastModified.longValue() != fileLastModified ) {
						needToUpdateOrDownload = true;
						values.putLocalLastModifiedNull();
						values.putLocalLastAccessNull();
					}
					long fileSize = cacheFile.length();
					if ( dbLocalSize == null ) {
						needToUpdateOrDownload = true;
						values.putLocalSizeNull();
					} else {
						if ( dbLocalSize.longValue() != fileSize || dbLocalSize.longValue() != dbSize ) {
							needToUpdateOrDownload = true;
							values.putLocalSizeNull();
						}
					}
				} else {
					needToUpdateOrDownload = true;
					values.putLocalLastModifiedNull();
					values.putLocalSizeNull();
					values.putLocalLastAccessNull();
				}
			}

			values.update(context.getContentResolver(), selection);

		} else {

			needToUpdateOrDownload = true;
			com.filelug.android.provider.remotehierarchicalmodel.RemoteObjectType objectType = RemoteFileUtils.convertRemoteFileObject(downloadFile.getType());

			values.putUserId(userId)
				.putComputerId(computerId)
				.putParent(remoteParent)
				.putName(remoteName)
				.putSymlink(downloadFile.isSymlink())
				.putReadable(downloadFile.isReadable())
				.putWritable(downloadFile.isWritable())
				.putHidden(downloadFile.isHidden())
				.putType(objectType)
				.putLastModified(downloadFile.getLastModified())
				.putContentType(downloadFile.getContentType())
				.putSize(downloadFile.getSize())
				.putRealParent(remoteRealParent)
				.putRealName(remoteRealName)
				.putLocalLastModifiedNull()
				.putLocalSizeNull()
				.putLocalLastAccessNull();
			values.insert(context.getContentResolver());

		}

		c.close();

		return !needToUpdateOrDownload;
	}

	public static File getFileInCache(Context context, String userId, int computerId, RemoteHierarchicalModelCursor cursor) {
//		if ( Constants.DEBUG ) Log.d(TAG, "getFileInCache() ------> Arguments: userId=" + userId + ", computerId=" + computerId);

		String dbRealParent = cursor.getRealParent();
		String dbRealName = cursor.getRealName();
		String dbParent = cursor.getParent();
		String dbName = cursor.getName();
		String dbLastModified = cursor.getLastModified();
		Long dbSize = cursor.getSize();
		Long dbLocalLastModified = cursor.getLocalLastModified();
		Long dbLocalSize = cursor.getLocalSize();
		boolean needToUpdateOrDownload = false;
		File cacheFile = null;

//		if ( Constants.DEBUG ) Log.d(TAG, "getFileInCache(), dbRealParent=" + dbRealParent + ", dbRealName=" + dbRealName +
//				", dbParent=" + dbParent + ", dbName=" + dbName + ", dbLastModified=" + dbLastModified +
//				", dbSize=" + dbSize + ", dbLocalLastModified=" + dbLocalLastModified + ", dbLocalSize=" + dbLocalSize);

		RemoteHierarchicalModelContentValues values = new RemoteHierarchicalModelContentValues();

		// Check Cache Dir
		String cacheDirName = FileCache.createDirInActiveAccountCache(dbRealParent);
//		if ( Constants.DEBUG ) Log.d(TAG, "getFileInCache(), cacheDirName=" + cacheDirName);
		if ( TextUtils.isEmpty(cacheDirName) ) {
			needToUpdateOrDownload = true;
			values.putLocalLastModifiedNull();
			values.putLocalSizeNull();
			values.putLocalLastAccessNull();
		} else {
			cacheFile = new File(cacheDirName + File.separator + dbRealName);
			if ( cacheFile.exists() ) {
				long fileLastModified = cacheFile.lastModified();
//				if ( Constants.DEBUG ) Log.d(TAG, "getFileInCache(), fileLastModified=" + fileLastModified + ", dbLocalLastModified=" + dbLocalLastModified );
				if ( dbLocalLastModified == null || dbLocalLastModified.longValue() != fileLastModified ) {
					needToUpdateOrDownload = true;
					values.putLocalLastModifiedNull();
					values.putLocalLastAccessNull();
				}
				long fileSize = cacheFile.length();
//				if ( Constants.DEBUG ) Log.d(TAG, "getFileInCache(), fileSize=" + fileSize + ", dbLocalSize=" + dbLocalSize );
				if ( dbLocalSize == null ) {
					needToUpdateOrDownload = true;
					values.putLocalSizeNull();
				} else {
					if ( dbLocalSize.longValue() != fileSize || dbLocalSize.longValue() != dbSize ) {
						needToUpdateOrDownload = true;
						values.putLocalSizeNull();
					}
				}
			} else {
//				if ( Constants.DEBUG ) Log.d(TAG, "getFileInCache(), cacheFile not exists!");
				needToUpdateOrDownload = true;
				values.putLocalLastModifiedNull();
				values.putLocalSizeNull();
				values.putLocalLastAccessNull();
			}
		}

//		if ( Constants.DEBUG ) Log.d(TAG, "getFileInCache(), needToUpdateOrDownload=" + needToUpdateOrDownload);
		if ( needToUpdateOrDownload ) {
			RemoteHierarchicalModelSelection selection = new RemoteHierarchicalModelSelection()
				.userId(userId).and()
				.computerId(computerId).and()
				.parent(dbParent).and()
				.name(dbName);
			values.update(context.getContentResolver(), selection);
			cacheFile = null;
		}

		return cacheFile;
	}

	public static com.filelug.android.provider.remotehierarchicalmodel.RemoteObjectType convertRemoteFileObject(RemoteFile.FileType fileType) {
		com.filelug.android.provider.remotehierarchicalmodel.RemoteObjectType objectType = null;
		if ( fileType.equals(RemoteFile.FileType.REMOTE_DIR)  ) {
			objectType = com.filelug.android.provider.remotehierarchicalmodel.RemoteObjectType.DIRECTORY;
		} else if ( fileType.equals(RemoteFile.FileType.REMOTE_FILE)  ) {
			objectType = com.filelug.android.provider.remotehierarchicalmodel.RemoteObjectType.FILE;
		} else if ( fileType.equals(RemoteFile.FileType.REMOTE_WINDOWS_SHORTCUT_DIR)  ) {
			objectType = com.filelug.android.provider.remotehierarchicalmodel.RemoteObjectType.WINDOWS_SHORTCUT_DIRECTORY;
		} else if ( fileType.equals(RemoteFile.FileType.REMOTE_WINDOWS_SHORTCUT_FILE)  ) {
			objectType = com.filelug.android.provider.remotehierarchicalmodel.RemoteObjectType.WINDOWS_SHORTCUT_FILE;
		} else if ( fileType.equals(RemoteFile.FileType.REMOTE_UNIX_SYMBOLIC_LINK_DIR)  ) {
			objectType = com.filelug.android.provider.remotehierarchicalmodel.RemoteObjectType.UNIX_SYMBOLIC_LINK_DIRECTORY;
		} else if ( fileType.equals(RemoteFile.FileType.REMOTE_UNIX_SYMBOLIC_LINK_FILE)  ) {
			objectType = com.filelug.android.provider.remotehierarchicalmodel.RemoteObjectType.UNIX_SYMBOLIC_LINK_FILE;
		} else if ( fileType.equals(RemoteFile.FileType.REMOTE_MAC_ALIAS_DIR)  ) {
			objectType = com.filelug.android.provider.remotehierarchicalmodel.RemoteObjectType.MAC_ALIAS_DIRECTORY;
		} else if ( fileType.equals(RemoteFile.FileType.REMOTE_MAC_ALIAS_FILE)  ) {
			objectType = com.filelug.android.provider.remotehierarchicalmodel.RemoteObjectType.MAC_ALIAS_FILE;
		}
		return objectType;
	}

	public static String getComputerName(Context context, int computerId) {
		String computerName = null;

		UserComputerSelection userComputerSelection = new UserComputerSelection()
			.computerId(computerId);
		UserComputerCursor c1 = userComputerSelection.query(
			context.getContentResolver(),
			new String[] { UserComputerColumns.COMPUTER_NAME },
			null
		);
		if ( c1.moveToFirst() ) {
			computerName = c1.getComputerName();
		}
		c1.close();

		return computerName;
	}

	public static void stopDownloadTask(Context context, long rowId) {
		String[] projection = new String[] { FileTransferColumns.USER_ID, FileTransferColumns.COMPUTER_ID, FileTransferColumns.GROUP_ID, FileTransferColumns.TRANSFER_KEY };
		FileTransferSelection selection = new FileTransferSelection()
			.id(rowId);

		FileTransferCursor c = selection.query(context.getContentResolver(), projection);
		if ( !c.moveToFirst() ) {
			c.close();
			return;
		}
		String userId = c.getUserId();
		int computerId = c.getComputerId();
		String groupId = c.getGroupId();
		String transferKey = c.getTransferKey();
		c.close();

		DownloadService.stopDownload(userId, computerId, groupId, transferKey);
	}

	public static RemoteFile.FileType convertRemoteRoot(RemoteRootType type) {
		RemoteFile.FileType fileType = RemoteFile.FileType.UNKNOWN;
		if ( RemoteRootType.USER_HOME.equals(type) ) {
			fileType = RemoteFile.FileType.REMOTE_ROOT_USER_HOME;
		} else if ( RemoteRootType.LOCAL_DISK.equals(type) ) {
			fileType = RemoteFile.FileType.REMOTE_ROOT_LOCAL_DISK;
		} else if ( RemoteRootType.DVD_PLAYER.equals(type) ) {
			fileType = RemoteFile.FileType.REMOTE_ROOT_DVD_PLAYER;
		} else if ( RemoteRootType.NETWORK_DISK.equals(type) ) {
			fileType = RemoteFile.FileType.REMOTE_ROOT_NETWORK_DISK;
		} else if ( RemoteRootType.EXTERNAL_DISK.equals(type) ) {
			fileType = RemoteFile.FileType.REMOTE_ROOT_EXTERNAL_DISK;
		} else if ( RemoteRootType.TIME_MACHINE.equals(type) ) {
			fileType = RemoteFile.FileType.REMOTE_ROOT_TIME_MACHINE;
		} else if ( RemoteRootType.DIRECTORY.equals(type) ) {
			fileType = RemoteFile.FileType.REMOTE_ROOT_DIRECTORY;
		} else if ( RemoteRootType.WINDOWS_SHORTCUT_DIRECTORY.equals(type) ) {
			fileType = RemoteFile.FileType.REMOTE_ROOT_WINDOWS_SHORTCUT_DIRECTORY;
		} else if ( RemoteRootType.UNIX_SYMBOLIC_LINK_DIRECTORY.equals(type) ) {
			fileType = RemoteFile.FileType.REMOTE_ROOT_UNIX_SYMBOLIC_LINK_DIRECTORY;
		} else if ( RemoteRootType.MAC_ALIAS_DIRECTORY.equals(type) ) {
			fileType = RemoteFile.FileType.REMOTE_ROOT_MAC_ALIAS_DIRECTORY;
		}
		return fileType;
	}

}
