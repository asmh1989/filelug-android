package com.filelug.android.crepo;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.filelug.android.Constants;
import com.filelug.android.MainApplication;
import com.filelug.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RepositoryClient {

	private static final String TAG = RepositoryClient.class.getSimpleName();

	private static RepositoryClient sInstance;

	private RepositoryClient() {
	}

	public static final RepositoryClient getInstance() {
		if (sInstance == null) sInstance = new RepositoryClient();
		return sInstance;
	}

	private RetryPolicy _getDefaultRetryPolicy(int initialTimeoutMs) {
		Context context = MainApplication.getInstance().getApplicationContext();
		return new DefaultRetryPolicy(
			initialTimeoutMs,
			context.getResources().getInteger(R.integer.max_retries),
			DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
		);
	}

	private RetryPolicy getDefaultRetryPolicyA() {
		Context context = MainApplication.getInstance().getApplicationContext();
		return _getDefaultRetryPolicy(context.getResources().getInteger(R.integer.sync_timeout_a));
	}

	private RetryPolicy getDefaultRetryPolicyB() {
		Context context = MainApplication.getInstance().getApplicationContext();
		return _getDefaultRetryPolicy(context.getResources().getInteger(R.integer.sync_timeout_b));
	}

	private RetryPolicy getDefaultRetryPolicyC() {
		Context context = MainApplication.getInstance().getApplicationContext();
		return _getDefaultRetryPolicy(context.getResources().getInteger(R.integer.sync_timeout_c));
	}

	public void findAvailableComputers3(String authToken, String locale,
									   Response.Listener<JSONArray> listener,
									   Response.ErrorListener errorListener) {

		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_LOCALE, locale);

		JSONObject jso = new JSONObject(parameters);
		JSARequest request = new JSARequest(Request.Method.POST, Constants.FIND_AVAILABLE_COMPUTERS_3_URI, jso, listener, errorListener);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);

	}

	public void exchangeAccessTokenWithAuthorizationCode(String authorizationCode, String verification,
														 String locale, Response.Listener<JSONObject> listener,
														 Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_CODE, authorizationCode);
		parameters.put(Constants.PARAM_VERIFICATION, verification);
		parameters.put(Constants.PARAM_LOCALE, locale);

		JSONObject jso = new JSONObject(parameters);
		JSORequest request = new JSORequest(Request.Method.POST, Constants.EXCHANGE_ACCESS_TOKEN_WITH_AUTH_CODE_URI, jso, listener, errorListener);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void loginWithAuthorizationCode(String authorizationCode, String notificationType,
										   String deviceToken, String deviceVersion,
										   String filelugVersion, String filelugBuild, String locale,
										   Response.Listener<JSONObject> listener,
										   Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_CODE, authorizationCode);
		parameters.put(Constants.PARAM_LOCALE, locale);

		if ( !TextUtils.isEmpty(notificationType) ) {
			try {
				JSONObject deviceTokenObj = new JSONObject();
				deviceTokenObj.put(Constants.PARAM_DEVICE_TOKEN, deviceToken);
				deviceTokenObj.put(Constants.PARAM_NOTIFICATION_TYPE, notificationType);
				deviceTokenObj.put(Constants.PARAM_DEVICE_TYPE, "ANDROID");
				deviceTokenObj.put(Constants.PARAM_DEVICE_VERSION_2, deviceVersion);
				deviceTokenObj.put(Constants.PARAM_FILELUG_VERSION, filelugVersion);
				deviceTokenObj.put(Constants.PARAM_FILELUG_BUILD, filelugBuild);
				parameters.put(Constants.PARAM_DEVICE_TOKEN, deviceTokenObj);
			} catch (JSONException e) {
			}
		}

		JSONObject jso = new JSONObject(parameters);
		JSORequest request = new JSORequest(Request.Method.POST, Constants.LOGIN_WITH_AUTHORIZATION_CODE_URI, jso, listener, errorListener);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void loginWithSessionId(String sessionId, String verification, String notificationType,
								   String deviceToken, String deviceVersion, String filelugVersion,
								   String filelugBuild, String locale, Response.Listener<JSONObject> listener,
								   Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_SESSION_ID, sessionId);
		parameters.put(Constants.PARAM_VERIFICATION, verification);
		parameters.put(Constants.PARAM_LOCALE, locale);

		if ( !TextUtils.isEmpty(notificationType) ) {
			try {
				JSONObject deviceTokenObj = new JSONObject();
				deviceTokenObj.put(Constants.PARAM_DEVICE_TOKEN, deviceToken);
				deviceTokenObj.put(Constants.PARAM_NOTIFICATION_TYPE, notificationType);
				deviceTokenObj.put(Constants.PARAM_DEVICE_TYPE, "ANDROID");
				deviceTokenObj.put(Constants.PARAM_DEVICE_VERSION_2, deviceVersion);
				deviceTokenObj.put(Constants.PARAM_FILELUG_VERSION, filelugVersion);
				deviceTokenObj.put(Constants.PARAM_FILELUG_BUILD, filelugBuild);
				parameters.put(Constants.PARAM_DEVICE_TOKEN, deviceTokenObj);
			} catch (JSONException e) {
			}
		}

		JSONObject jso = new JSONObject(parameters);
		JSORequest request = new JSORequest(Request.Method.POST, Constants.LOGIN_WITH_SESSION_ID_URI, jso, listener, errorListener);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void createOrUpdateUserProfile(String authToken, String email, String nickname, String locale,
										  Response.Listener<String> listener,
										  Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_EMAIL, email);
		parameters.put(Constants.PARAM_NICKNAME, nickname);
		parameters.put(Constants.PARAM_LOCALE, locale);

		JSONObject jso = new JSONObject(parameters);
		StrRequest request = new StrRequest(Request.Method.POST, Constants.CREATE_OR_UPDATE_USER_PROFILE_URI, jso, listener, errorListener);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void connectToComputer(String authToken, int computerId, boolean showHidden,
								  String deviceToken, String notificationType, String deviceVersion,
								  String filelugVersion, String filelugBuild, String locale,
								  Response.Listener<JSONObject> listener,
								  Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_COMPUTER_ID, computerId);
		parameters.put(Constants.PARAM_SHOW_HIDDEN, showHidden);
		parameters.put(Constants.PARAM_LOCALE, locale);

		if ( !TextUtils.isEmpty(notificationType) ) {
			try {
				JSONObject deviceTokenObj = new JSONObject();
				deviceTokenObj.put(Constants.PARAM_DEVICE_TOKEN, deviceToken);
				deviceTokenObj.put(Constants.PARAM_NOTIFICATION_TYPE, notificationType);
				deviceTokenObj.put(Constants.PARAM_DEVICE_TYPE, "ANDROID");
				deviceTokenObj.put(Constants.PARAM_DEVICE_VERSION_2, deviceVersion);
				deviceTokenObj.put(Constants.PARAM_FILELUG_VERSION, filelugVersion);
				deviceTokenObj.put(Constants.PARAM_FILELUG_BUILD, filelugBuild);
				parameters.put(Constants.PARAM_DEVICE_TOKEN, deviceTokenObj);
			} catch (JSONException e) {
			}
		}

		JSONObject jso = new JSONObject(parameters);
		JSORequest request = new JSORequest(Request.Method.POST, Constants.CONNECT_TO_COMPUTER_URI, jso, listener, errorListener);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void listRoots( String authToken, String lugServerId, String locale,
						   Response.Listener<JSONArray> listener, Response.ErrorListener errorListener ) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_LOCALE, locale);

		String lugServerURL = null;
		if ( Constants.PROTOCOL.startsWith(Constants.PROTOCOL_HTTPS) ) {
			lugServerURL = String.format(Constants.LUG_LIST_ROOTS_URI, lugServerId);
		} else {
			lugServerURL = Constants.LUG_LIST_ROOTS_URI;
		}

		JSONObject jso = new JSONObject(parameters);
		JSARequest request = new JSARequest(Request.Method.POST, lugServerURL, jso, listener, errorListener);
/*
		request.addHeader(Constants.REQ_HEADER_ACCEPT, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_ACCEPT_CHARSET, Constants.UTF8);
		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
		request.addHeader(Constants.REQ_HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
*/
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyC());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void changeNickname(String authToken, String newNickname, String locale,
							   Response.Listener<String> listener, Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_NEW_NICKNAME, newNickname);
		parameters.put(Constants.PARAM_LOCALE, locale);

		JSONObject jso = new JSONObject(parameters);
		StrRequest request = new StrRequest(Request.Method.POST, Constants.CHANGE_NICKNAME_URI, jso, listener, errorListener);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void sendChangeUserEmailSecurityCode(String authToken, String newEmail, String locale,
												Response.Listener<String> listener,
												Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_NEW_EMAIL, newEmail);
		parameters.put(Constants.PARAM_LOCALE, locale);

		JSONObject jso = new JSONObject(parameters);
		StrRequest request = new StrRequest(Request.Method.POST, Constants.SEND_CHANGE_USER_EMAIL_SECURITY_CODE_URI, jso, listener, errorListener);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void changeUserEmail(String authToken, String code, String newEmail, String locale,
								Response.Listener<String> listener, Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_CODE, code);
		parameters.put(Constants.PARAM_NEW_EMAIL, newEmail);
		parameters.put(Constants.PARAM_LOCALE, locale);

		JSONObject jso = new JSONObject(parameters);
		StrRequest request = new StrRequest(Request.Method.POST, Constants.CHANGE_EMAIL_URI, jso, listener, errorListener);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void checkUserDeletable(String authToken, String account, String locale,
								   Response.Listener<String> listener, Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_ACCOUNT, account);
		parameters.put(Constants.PARAM_LOCALE, locale);

		JSONObject jso = new JSONObject(parameters);
		StrRequest request = new StrRequest(Request.Method.POST, Constants.CHECK_USER_DELETABLE_URI, jso, listener, errorListener);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void deleteUser2(String authToken, String account, String verification, String locale,
						   Response.Listener<String> listener, Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_ACCOUNT, account);
		parameters.put(Constants.PARAM_VERIFICATION, verification);
		parameters.put(Constants.PARAM_LOCALE, locale);

		JSONObject jso = new JSONObject(parameters);
		StrRequest request = new StrRequest(Request.Method.POST, Constants.DELETE_USER_2_URI, jso, listener, errorListener);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void findAllFileDownloaded( String authToken, String lugServerId, int type, String locale,
									   Response.Listener<JSONArray> listener,
									   Response.ErrorListener errorListener ) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_TYPE, type);
		parameters.put(Constants.PARAM_LOCALE, locale);

		String lugServerURL = null;
		if ( Constants.PROTOCOL.startsWith(Constants.PROTOCOL_HTTPS) ) {
			lugServerURL = String.format(Constants.FIND_ALL_FILE_DOWNLOADED_URI, lugServerId);
		} else {
			lugServerURL = Constants.FIND_ALL_FILE_DOWNLOADED_URI;
		}

		JSONObject jso = new JSONObject(parameters);
		JSARequest request = new JSARequest(Request.Method.POST, lugServerURL, jso, listener, errorListener);
		request.addHeader(Constants.REQ_HEADER_ACCEPT, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_ACCEPT_CHARSET, Constants.UTF8);
		request.addHeader(Constants.REQ_HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyC());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void findAllFileUploaded( String authToken, String lugServerId, int type, String locale,
									 Response.Listener<JSONArray> listener,
									 Response.ErrorListener errorListener ) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_TYPE, type);
		parameters.put(Constants.PARAM_LOCALE, locale);

		String lugServerURL = null;
		if ( Constants.PROTOCOL.startsWith(Constants.PROTOCOL_HTTPS) ) {
			lugServerURL = String.format(Constants.FIND_ALL_FILE_UPLOADED_URI, lugServerId);
		} else {
			lugServerURL = Constants.FIND_ALL_FILE_UPLOADED_URI;
		}

		JSONObject jso = new JSONObject(parameters);
		JSARequest request = new JSARequest(Request.Method.POST, lugServerURL, jso, listener, errorListener);
		request.addHeader(Constants.REQ_HEADER_ACCEPT, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_ACCEPT_CHARSET, Constants.UTF8);
		request.addHeader(Constants.REQ_HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyC());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void list( String authToken, String lugServerId, String path, boolean showHidden, String locale,
					  Response.Listener<JSONArray> listener, Response.ErrorListener errorListener ) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_PATH, path);
		parameters.put(Constants.PARAM_SHOW_HIDDEN, showHidden);
		parameters.put(Constants.PARAM_LOCALE, locale);

		String lugServerURL = null;
		if ( Constants.PROTOCOL.startsWith(Constants.PROTOCOL_HTTPS) ) {
			lugServerURL = String.format(Constants.LUG_LIST_URI, lugServerId);
		} else {
			lugServerURL = Constants.LUG_LIST_URI;
		}

		JSONObject jso = new JSONObject(parameters);
		JSARequest request = new JSARequest(Request.Method.POST, lugServerURL, jso, listener, errorListener);
		request.addHeader(Constants.REQ_HEADER_ACCEPT, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_ACCEPT_CHARSET, Constants.UTF8);
		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
		request.addHeader(Constants.REQ_HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyC());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

/*
	public void findByPath( String authToken, String lugServerId, String path, String locale,
							Response.Listener<JSONArray> listener, Response.ErrorListener errorListener ) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_PATH, path);
		parameters.put(Constants.PARAM_CALCULATE_SIZE, true);
		parameters.put(Constants.PARAM_LOCALE, locale);

		String lugServerURL = null;
		if ( Constants.PROTOCOL.startsWith(Constants.PROTOCOL_HTTPS) ) {
			lugServerURL = String.format(Constants.LUG_FIND_BY_PATH_URI, lugServerId);
		} else {
			lugServerURL = Constants.LUG_LIST_URI;
		}

		JSONObject jso = new JSONObject(parameters);
		JSARequest request = new JSARequest(Request.Method.POST, lugServerURL, jso, listener, errorListener);
		request.addHeader(Constants.REQ_HEADER_ACCEPT, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_ACCEPT_CHARSET, Constants.UTF8);
		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
		request.addHeader(Constants.REQ_HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyC());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}
*/

	public void createFileUploadSummary(String authToken, String lugServerId, String uploadGroupId,
										String[] uploadKeyArray, String uploadDir,
										int subDirType, String subDirValue, int descriptionType,
										String descriptionValue, int notificationType,
										String locale, Response.Listener<String> listener,
										Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_UPLOAD_GROUP_ID, uploadGroupId);
		//parameters.put(Constants.PARAM_UPLOAD_KEYS, uploadKeyList); // SDK 版本小於 18(含) 時, 這一行會錯
		parameters.put(Constants.PARAM_UPLOAD_DIR, uploadDir);
		parameters.put(Constants.PARAM_SUB_DIR_TYPE, subDirType);
		if ( subDirType > 0 ) {
			parameters.put(Constants.PARAM_SUB_DIR_VALUE, subDirValue);
		}
		parameters.put(Constants.PARAM_DESCRIPTION_TYPE, descriptionType);
		if ( descriptionType > 0 ) {
			parameters.put(Constants.PARAM_DESCRIPTION_VALUE, descriptionValue);
		}
		parameters.put(Constants.PARAM_NOTIFICATION_TYPE, notificationType);
		//parameters.put(Constants.PARAM_LOCALE, locale); // Repository 有 Bug, 加了這個會出錯

		String lugServerURL = null;
		if ( Constants.PROTOCOL.startsWith(Constants.PROTOCOL_HTTPS) ) {
			lugServerURL = String.format(Constants.LUG_CREATE_FILE_UPLOAD_SUMMARY_2_URI, lugServerId);
		} else {
			lugServerURL = Constants.LUG_CREATE_FILE_UPLOAD_SUMMARY_2_URI;
		}

		JSONObject jso = new JSONObject(parameters);

		// 因 SDK 版本小於 18(含) 時會有錯誤, 故改以此寫法避開錯誤
		//////
		JSONArray jsoArray = new JSONArray();
		for ( String uploadKey : uploadKeyArray ) {
			jsoArray.put(uploadKey);
		}
		try {
			jso.put(Constants.PARAM_UPLOAD_KEYS, jsoArray);
		} catch (JSONException e) {
		}
		//////

		StrRequest request = new StrRequest(Request.Method.POST, lugServerURL, jso, listener, errorListener);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void confirmUploadFileFromDevice2( String authToken, String lugServerId, String transferKey, String status,
											  String locale, Response.Listener<JSONArray> listener,
											  Response.ErrorListener errorListener ) {
		try {
			JSONArray jsonArray = new JSONArray();
			JSONObject jso = new JSONObject();
			jso.put(Constants.PARAM_TRANSFER_KEY, transferKey);
			jso.put(Constants.PARAM_STATUS, status);
			jsonArray.put(jso);
			confirmUploadFileFromDevice2(authToken, lugServerId, jsonArray, locale, listener, errorListener);
		} catch (JSONException jsoe) {
		}
	}

	public void confirmUploadFileFromDevice2( String authToken, String lugServerId, JSONArray keyStatusArray,
											  String locale, Response.Listener<JSONArray> listener,
											  Response.ErrorListener errorListener ) {
		String lugServerURL = null;
		if ( Constants.PROTOCOL.startsWith(Constants.PROTOCOL_HTTPS) ) {
			lugServerURL = String.format(Constants.LUG_CONFIRM_UPLOAD_FILE_2_URI, lugServerId);
		} else {
			lugServerURL = Constants.LUG_CONFIRM_UPLOAD_FILE_2_URI;
		}

		JSA2Request request = new JSA2Request(Request.Method.POST, lugServerURL, keyStatusArray, listener, errorListener);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT, Constants.APPLICATION_JSON);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT_CHARSET, Constants.UTF8);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
//		request.addHeader(Constants.REQ_HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void pingDesktop( String authToken, String account, String locale,
							 Response.Listener<JSONObject> listener,
							 Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_ACCOUNT, account);
		parameters.put(Constants.PARAM_LOCALE, locale);

		JSONObject jso = new JSONObject(parameters);
		JSORequest request = new JSORequest(Request.Method.POST, Constants.PING_DESKTOP_URI, jso, listener, errorListener);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT, Constants.APPLICATION_JSON);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT_CHARSET, Constants.UTF8);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
//		request.addHeader(Constants.REQ_HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyA());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void createOrUpdateDeviceToken(String authToken, String[] sessionArray,
										  String notificationType, String deviceToken,
										  String deviceVersion, String locale,
										  Response.Listener<String> listener,
										  Response.ErrorListener errorListener) {
		JSONObject deviceTokenObj = new JSONObject();
		try {
			deviceTokenObj.put(Constants.PARAM_DEVICE_TOKEN, deviceToken);
			deviceTokenObj.put(Constants.PARAM_NOTIFICATION_TYPE, notificationType);
			deviceTokenObj.put(Constants.PARAM_DEVICE_TYPE, "ANDROID");
			deviceTokenObj.put(Constants.PARAM_DEVICE_VERSION_2, deviceVersion);
		} catch (JSONException e) {
		}

		HashMap<String, Object> parameters = new HashMap<String, Object>();
		//parameters.put(Constants.PARAM_SESSIONS, sessionList); // SDK 版本小於 18(含) 時, 這一行會錯
		parameters.put(Constants.PARAM_DEVICE_TOKEN, deviceTokenObj);
		parameters.put(Constants.PARAM_LOCALE, locale);

		JSONObject jso = new JSONObject(parameters);

		// 因 SDK 版本小於 18(含) 時會有錯誤, 故改以此寫法避開錯誤
		//////
		JSONArray jsoArray = new JSONArray();
		for ( String sessionKey : sessionArray ) {
			jsoArray.put(sessionKey);
		}
		try {
			jso.put(Constants.PARAM_SESSIONS, jsoArray);
		} catch (JSONException e) {
		}
		//////

		StrRequest request = new StrRequest(Request.Method.POST, Constants.CREATE_OR_UPDATE_DEVICE_TOKEN_URI, jso, listener, errorListener);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void requestConnection(String authToken, String locale,
								  Response.Listener<JSONObject> listener,
								  Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_LOCALE, locale);

		Context context = MainApplication.getInstance().getApplicationContext();

		JSONObject jso = new JSONObject(parameters);

		JSORequest request = new JSORequest(Request.Method.POST, Constants.REQUEST_CONNECTION_URI, jso, listener, errorListener);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		RetryPolicy retryPolicy = new DefaultRetryPolicy(
			context.getResources().getInteger(R.integer.req_connect_timeout),
			context.getResources().getInteger(R.integer.max_retries),
			DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
		);
		request.setRetryPolicy(retryPolicy);

		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void replaceFileUpload(String authToken, String lugServerId, String oldTransferKey,
								  String newTransferKey, String locale,
								  Response.Listener<String> listener,
								  Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_OLD_TRANSFER_KEY, oldTransferKey);
		parameters.put(Constants.PARAM_NEW_TRANSFER_KEY, newTransferKey);
		parameters.put(Constants.PARAM_LOCALE, locale);

		String lugServerURL = null;
		if ( Constants.PROTOCOL.startsWith(Constants.PROTOCOL_HTTPS) ) {
			lugServerURL = String.format(Constants.LUG_REPLACE_FILE_UPLOAD_URI, lugServerId);
		} else {
			lugServerURL = Constants.LUG_REPLACE_FILE_UPLOAD_URI;
		}

		JSONObject jso = new JSONObject(parameters);
		StrRequest request = new StrRequest(Request.Method.POST, lugServerURL, jso, listener, errorListener);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT, Constants.APPLICATION_JSON);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT_CHARSET, Constants.UTF8);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
//		request.addHeader(Constants.REQ_HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void findFileUploadedByTransferKey(String authToken, String lugServerId,
											  String transferKey, String locale,
											  Response.Listener<JSONObject> listener,
											  Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_TRANSFER_KEY, transferKey);
		parameters.put(Constants.PARAM_LOCALE, locale);

		String lugServerURL = null;
		if ( Constants.PROTOCOL.startsWith(Constants.PROTOCOL_HTTPS) ) {
			lugServerURL = String.format(Constants.LUG_FIND_FILE_UPLOADED_BY_TRANSFER_KEY_URI, lugServerId);
		} else {
			lugServerURL = Constants.LUG_FIND_FILE_UPLOADED_BY_TRANSFER_KEY_URI;
		}

		JSONObject jso = new JSONObject(parameters);
		JSORequest request = new JSORequest(Request.Method.POST, lugServerURL, jso, listener, errorListener);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT, Constants.APPLICATION_JSON);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT_CHARSET, Constants.UTF8);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
//		request.addHeader(Constants.REQ_HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void createFileDownloadSummary(String authToken, String lugServerId, String downloadGroupId,
										  Map<String, String> downloadKeyPathsArray, String downloadDir,
										  int subDirType, String subDirValue, int descriptionType,
										  String descriptionValue, int notificationType,
										  String locale, Response.Listener<String> listener,
										  Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_DOWNLOAD_GROUP_ID, downloadGroupId);
		parameters.put(Constants.PARAM_DOWNLOAD_DIR, downloadDir);
		parameters.put(Constants.PARAM_SUB_DIR_TYPE, subDirType);
		if ( subDirType > 0 ) {
			parameters.put(Constants.PARAM_SUB_DIR_VALUE, subDirValue);
		}
		parameters.put(Constants.PARAM_DESCRIPTION_TYPE, descriptionType);
		if ( descriptionType > 0 ) {
			parameters.put(Constants.PARAM_DESCRIPTION_VALUE, descriptionValue);
		}
		parameters.put(Constants.PARAM_NOTIFICATION_TYPE, notificationType);

		String lugServerURL = null;
		if ( Constants.PROTOCOL.startsWith(Constants.PROTOCOL_HTTPS) ) {
			lugServerURL = String.format(Constants.LUG_CREATE_FILE_DOWNLOAD_SUMMARY_2_URI, lugServerId);
		} else {
			lugServerURL = Constants.LUG_CREATE_FILE_DOWNLOAD_SUMMARY_2_URI;
		}

		JSONObject jso = new JSONObject(parameters);
		JSONObject keyPathsObject = new JSONObject();
		try {
			for ( String key : downloadKeyPathsArray.keySet() ) {
				String path = downloadKeyPathsArray.get(key);
				keyPathsObject.put(key, path);
			}
			jso.put(Constants.PARAM_DOWNLOAD_KEY_PATHS, keyPathsObject);
		} catch (JSONException e) {
		}

		StrRequest request = new StrRequest(Request.Method.POST, lugServerURL, jso, listener, errorListener);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void confirmDownloadFileFromDevice(String authToken, String lugServerId, String transferKey,
											  String status, long fileSize, String locale,
											  Response.Listener<String> listener,
											  Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_TRANSFER_KEY, transferKey);
		parameters.put(Constants.PARAM_STATUS, status);
		parameters.put(Constants.PARAM_FILE_SIZE, fileSize);
		parameters.put(Constants.PARAM_LOCALE, locale);

		String lugServerURL = null;
		if ( Constants.PROTOCOL.startsWith(Constants.PROTOCOL_HTTPS) ) {
			lugServerURL = String.format(Constants.LUG_CONFIRM_DOWNLOAD_FILE_URI, lugServerId);
		} else {
			lugServerURL = Constants.LUG_CONFIRM_DOWNLOAD_FILE_URI;
		}

		JSONObject jso = new JSONObject(parameters);
		StrRequest request = new StrRequest(Request.Method.POST, lugServerURL, jso, listener, errorListener);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT, Constants.APPLICATION_JSON);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT_CHARSET, Constants.UTF8);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
//		request.addHeader(Constants.REQ_HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void cancelDownloadFileFromDevice(String authToken, String lugServerId,
											 String transferKey, String locale,
											 Response.Listener<String> listener,
											 Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_TRANSFER_KEY, transferKey);
		parameters.put(Constants.PARAM_LOCALE, locale);

		String lugServerURL = null;
		if ( Constants.PROTOCOL.startsWith(Constants.PROTOCOL_HTTPS) ) {
			lugServerURL = String.format(Constants.LUG_CANCEL_DOWNLOAD_FILE_FROM_DEVICE_URI, lugServerId);
		} else {
			lugServerURL = Constants.LUG_CANCEL_DOWNLOAD_FILE_FROM_DEVICE_URI;
		}

		JSONObject jso = new JSONObject(parameters);
		StrRequest request = new StrRequest(Request.Method.POST, lugServerURL, jso, listener, errorListener);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT, Constants.APPLICATION_JSON);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT_CHARSET, Constants.UTF8);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
//		request.addHeader(Constants.REQ_HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void changeUserComputerProfiles(String authToken, HashMap<String, Object> profiles, String locale,
										   Response.Listener<String> listener,
										   Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_LOCALE, locale);

		for ( String key : profiles.keySet() ) {
			Object value = profiles.get(key);
			parameters.put(key, value);
		}

		JSONObject jso = new JSONObject(parameters);
		StrRequest request = new StrRequest(Request.Method.POST, Constants.CHANGE_USER_COMPUTER_PROFILES_URI, jso, listener, errorListener);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT, Constants.APPLICATION_JSON);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT_CHARSET, Constants.UTF8);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
//		request.addHeader(Constants.REQ_HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void createComputerWithQRCode(String authToken, String qrCode, String locale,
										   Response.Listener<JSONObject> listener,
										   Response.ErrorListener errorListener) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_QR_CODE, qrCode);
		parameters.put(Constants.PARAM_LOCALE, locale);

		JSONObject jso = new JSONObject(parameters);
		JSORequest request = new JSORequest(Request.Method.POST, Constants.CREATE_COMPUTER_WITH_QR_CODE_URI, jso, listener, errorListener);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT, Constants.APPLICATION_JSON);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT_CHARSET, Constants.UTF8);
//		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
//		request.addHeader(Constants.REQ_HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyB());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void deleteComputerFromDevice(String authToken, String lugServerId, int computerId,
										 String verification, String locale,
										 Response.Listener<String> listener,
										 Response.ErrorListener errorListener ) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_COMPUTER_ID, computerId);
		parameters.put(Constants.PARAM_VERIFICATION, verification);
		parameters.put(Constants.PARAM_LOCALE, locale);

		String lugServerURL = null;
		if ( TextUtils.isEmpty(lugServerId) ) {
			lugServerURL = Constants.AA_DELETE_COMPUTER_FROM_DEVICE_URI;
		} else if ( Constants.PROTOCOL.startsWith(Constants.PROTOCOL_HTTPS) ) {
			lugServerURL = String.format(Constants.LUG_DELETE_COMPUTER_FROM_DEVICE_URI, lugServerId);
		} else {
			lugServerURL = Constants.LUG_DELETE_COMPUTER_FROM_DEVICE_URI;
		}

		JSONObject jso = new JSONObject(parameters);
		StrRequest request = new StrRequest(Request.Method.POST, lugServerURL, jso, listener, errorListener);
/*
		request.addHeader(Constants.REQ_HEADER_ACCEPT, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_ACCEPT_CHARSET, Constants.UTF8);
		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
		request.addHeader(Constants.REQ_HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
*/
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyC());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

	public void changeComputerName(String authToken, String lugServerId, int computerId,
								   String newComputerName, String newComputerGroup, String locale,
								   Response.Listener<JSONObject> listener,
								   Response.ErrorListener errorListener ) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(Constants.PARAM_COMPUTER_ID, computerId);
		parameters.put(Constants.PARAM_NEW_COMPUTER_NAME, newComputerName);
		parameters.put(Constants.PARAM_NEW_COMPUTER_GROUP, newComputerGroup);
		parameters.put(Constants.PARAM_LOCALE, locale);

		String lugServerURL = null;
		if ( TextUtils.isEmpty(lugServerId) ) {
			lugServerURL = Constants.AA_CHANGE_COMPUTER_NAME_URI;
		} else if ( Constants.PROTOCOL.startsWith(Constants.PROTOCOL_HTTPS) ) {
			lugServerURL = String.format(Constants.LUG_CHANGE_COMPUTER_NAME_URI, lugServerId);
		} else {
			lugServerURL = Constants.LUG_CHANGE_COMPUTER_NAME_URI;
		}

		JSONObject jso = new JSONObject(parameters);
		JSORequest request = new JSORequest(Request.Method.POST, lugServerURL, jso, listener, errorListener);
/*
		request.addHeader(Constants.REQ_HEADER_ACCEPT, Constants.APPLICATION_JSON);
		request.addHeader(Constants.REQ_HEADER_ACCEPT_CHARSET, Constants.UTF8);
		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
		request.addHeader(Constants.REQ_HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
*/
		request.addHeader(Constants.REQ_HEADER_AUTHORIZATION, authToken);
		request.setRetryPolicy(getDefaultRetryPolicyC());
		RequestQueue mRequestQueue = MainApplication.getInstance().getRequestQueue();

		mRequestQueue.add(request);
	}

}
