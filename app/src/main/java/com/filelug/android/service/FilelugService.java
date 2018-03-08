package com.filelug.android.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.filelug.android.Constants;
import com.filelug.android.MainApplication;
import com.filelug.android.R;
import com.filelug.android.util.AccountUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Vincent Chang on 2015/8/23.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class FilelugService extends IntentService {

	private static final String TAG = FilelugService.class.getSimpleName();

	protected static final int BUFFER_SIZE;

	protected static String NAMESPACE = "com.filelug.android";
	protected static final String PARAM_NOTIFICATION_CONFIG = "notificationConfig";
	public static final String PARAM_USER_ID = "userId";
	public static final String PARAM_COMPUTER_ID = "computerId";
	public static final String PARAM_GROUP_ID = "groupId";
	public static final String PARAM_TRANSFER_KEY = "transferKey";
	public static final String PARAM_ROW_ID = "rowId";
	public static final String PARAM_LUG_SERVER_ID = "lugServerId";
	public static final String PARAM_AUTH_TOKEN = "authToken";
	public static final String PARAM_NOTIFICATION_TYPE = "notificationType";
	public static final String PARAM_FILE = "file";
	public static final String PARAM_FILE_NAME = "fileName";
	public static final String PARAM_CACHE_FILE_NAME = "cacheFileName";
	public static final String PARAM_PROGRESS = "progress";
	public static final String PARAM_TRANSFERRED_BYTES = "transferredBytes";
	public static final String PARAM_TOTAL_BYTES = "totalBytes";
	public static final String PARAM_TRANSFERRED_SIZE = "transferredSize";
	protected static final String PARAM_REQUEST_HEADERS = "requestHeaders";
	protected static final String PARAM_REQUEST_PARAMETERS = "requestParameters";
	protected static final String PARAM_CUSTOM_USER_AGENT = "customUserAgent";
	public static final String PARAM_LOCAL_DIR = "localDir";
	public static final String PARAM_SAVED_FILE_NAME = "savedFileName";
	public static final String PARAM_CONFIRM_STATUS = "confirmStatus";
	public static final String PARAM_FROM_ANOTHER_APP = "fromAnotherApp";
	public static final String PARAM_IS_RESUME = "isResume";
	public static final String PARAM_STATUS = "status";
	public static final int STATUS_PREPARE = 1;
	public static final int STATUS_START = 2;
	public static final int STATUS_PING_ERROR = 3;
	public static final int STATUS_PROGRESSING = 4;
	public static final int STATUS_TRANSFER_COMPLETED = 5;
	public static final int STATUS_TRANSFER_ERROR = 6;
	public static final int STATUS_RESPONSE_ERROR = 7;
	public static final int STATUS_CANCEL = 8;
	public static final int STATUS_CONFIRMED = 9;
//	public static final int STATUS_SERVICE_DESTROY = 999;
	protected static final String ERROR_EXCEPTION = "errorException";
	protected static final String ERROR_MESSAGE = "errorMessage";
	protected static final String SERVER_RESPONSE_CODE = "serverResponseCode";
	protected static final String SERVER_RESPONSE_MESSAGE = "serverResponseMessage";
	protected static final String GROUP_KEY_SEPARATOR = "@@@";

	private static final String ACTION_UPLOAD_SUFFIX = ".service.upload";
	private static final String ACTION_UPLOAD_BROADCAST_SUFFIX = ".service.upload.status";
	private static final String ACTION_DOWNLOAD_SUFFIX = ".service.download";
	private static final String ACTION_DOWNLOAD_BROADCAST_SUFFIX = ".service.download.status";

	protected NotificationManager notificationManager;
	protected NotificationCompat.Builder notificationBuilder;
	protected PowerManager.WakeLock wakeLock;
	protected NotificationConfig notificationConfig;
	protected int mLastPublishedProgress;
	protected long mTotalBytes;
	protected long mTransferredBytes;

	static {
		Context context = MainApplication.getInstance().getApplicationContext();
		BUFFER_SIZE = context.getResources().getInteger(R.integer.bufferSize);
	}

	public FilelugService(String name) {
		super(name);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		AccountUtils.noticeDesktopConnectionChanged(Constants.MESSAGE_FILELUG_SERVICE_DESTROY);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		mLastPublishedProgress = 0;
		mTotalBytes = 0L;
		mTransferredBytes = 0L;
	}

	public static String getActionUpload() {
		return NAMESPACE + ACTION_UPLOAD_SUFFIX;
	}

	public static String getActionUploadBroadcast() {
		return NAMESPACE + ACTION_UPLOAD_BROADCAST_SUFFIX;
	}

	public static String getActionDownload() {
		return NAMESPACE + ACTION_DOWNLOAD_SUFFIX;
	}

	public static String getActionDownloadBroadcast() {
		return NAMESPACE + ACTION_DOWNLOAD_BROADCAST_SUFFIX;
	}

	protected void setRequestHeaders(HttpURLConnection conn, ArrayList<NameValue> requestHeaders) {
//		String logStr = "setRequestHeaders(), requestHeaders=[";
		if (!requestHeaders.isEmpty()) {
			for (NameValue param : requestHeaders) {
				String name = param.getName();
				String value = param.getValue();
				conn.setRequestProperty(name, value);
//				logStr += name + ": " + value + ", ";
			}
		}
//		logStr += "]";
//		if ( Constants.DEBUG ) Log.d(TAG, logStr);
	}

	protected void closeInputStream(InputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (Exception exc) {
			}
		}
	}

	protected void closeOutputStream(OutputStream stream) {
		if (stream != null) {
			try {
				stream.flush();
				stream.close();
			} catch (Exception exc) {
			}
		}
	}

	protected void closeConnection(HttpURLConnection connection) {
		if (connection != null) {
			try {
				connection.disconnect();
			} catch (Exception exc) {
			}
		}
	}

	protected void broadcastPrepare(String actionBroadcast, boolean fromAnotherApp, long rowId, String fileName, boolean isResume) {
//		if ( Constants.DEBUG ) Log.d(TAG, "broadcastPrepare(), fileName=" + fileName + ", isResume=" + isResume);

		Intent intent = new Intent(actionBroadcast);
		intent.putExtra(PARAM_STATUS, STATUS_PREPARE);
		intent.putExtra(PARAM_FROM_ANOTHER_APP, fromAnotherApp);
//		intent.putExtra(PARAM_USER_ID, userId);
//		intent.putExtra(PARAM_COMPUTER_ID, computerId);
//		intent.putExtra(PARAM_GROUP_ID, groupId);
//		intent.putExtra(PARAM_TRANSFER_KEY, transferKey);
		intent.putExtra(PARAM_ROW_ID, rowId);
		intent.putExtra(PARAM_FILE_NAME, fileName);
		intent.putExtra(PARAM_IS_RESUME, isResume);
		sendBroadcast(intent);
	}

	protected void broadcastStart(String actionBroadcast, boolean fromAnotherApp, long rowId, String fileName, long totalBytes, long transferredSize) {
//		if ( Constants.DEBUG ) Log.d(TAG, "broadcastStart(), fileName=" + fileName + ", totalBytes=" + totalBytes + ", transferredSize=" + transferredSize);
		mTotalBytes = totalBytes;
		mTransferredBytes = transferredSize;

		Intent intent = new Intent(actionBroadcast);
		intent.putExtra(PARAM_STATUS, STATUS_START);
		intent.putExtra(PARAM_FROM_ANOTHER_APP, fromAnotherApp);
//		intent.putExtra(PARAM_USER_ID, userId);
//		intent.putExtra(PARAM_COMPUTER_ID, computerId);
//		intent.putExtra(PARAM_GROUP_ID, groupId);
//		intent.putExtra(PARAM_TRANSFER_KEY, transferKey);
		intent.putExtra(PARAM_ROW_ID, rowId);
		intent.putExtra(PARAM_FILE_NAME, fileName);
		intent.putExtra(PARAM_TOTAL_BYTES, totalBytes);
		intent.putExtra(PARAM_TRANSFERRED_SIZE, transferredSize);
		sendBroadcast(intent);
	}

	protected void broadcastPingError(String actionBroadcast, boolean fromAnotherApp, long rowId, String fileName, String errorMessage) {
//		if ( Constants.DEBUG ) Log.d(TAG, "broadcastPingError(), fileName=" + fileName + ", errorMessage=" + errorMessage);

		Intent intent = new Intent(actionBroadcast);
		intent.putExtra(PARAM_STATUS, STATUS_PING_ERROR);
		intent.putExtra(PARAM_FROM_ANOTHER_APP, fromAnotherApp);
//		intent.putExtra(PARAM_USER_ID, userId);
//		intent.putExtra(PARAM_COMPUTER_ID, computerId);
//		intent.putExtra(PARAM_GROUP_ID, groupId);
//		intent.putExtra(PARAM_TRANSFER_KEY, transferKey);
		intent.putExtra(PARAM_ROW_ID, rowId);
		intent.putExtra(PARAM_FILE_NAME, fileName);
		intent.putExtra(ERROR_MESSAGE, errorMessage);
		sendBroadcast(intent);
		if ( wakeLock !=null && wakeLock.isHeld() ) {
			wakeLock.release();
		}
	}

	protected void broadcastProgressing(String actionBroadcast, boolean fromAnotherApp, long rowId, String fileName, long transferredBytes) {
//		if ( Constants.DEBUG ) Log.d(TAG, "broadcastProgressing(), fileName=" + fileName + ", transferredBytes=" + transferredBytes + ", mTotalBytes=" + mTotalBytes);
		mTransferredBytes = transferredBytes;
		int progress = (int) (mTransferredBytes * 100 / mTotalBytes);
		if (progress <= mLastPublishedProgress)
			return;
		mLastPublishedProgress = progress;

		Intent intent = new Intent(actionBroadcast);
		intent.putExtra(PARAM_STATUS, STATUS_PROGRESSING);
		intent.putExtra(PARAM_FROM_ANOTHER_APP, fromAnotherApp);
//		intent.putExtra(PARAM_USER_ID, userId);
//		intent.putExtra(PARAM_COMPUTER_ID, computerId);
//		intent.putExtra(PARAM_GROUP_ID, groupId);
//		intent.putExtra(PARAM_TRANSFER_KEY, transferKey);
		intent.putExtra(PARAM_ROW_ID, rowId);
		intent.putExtra(PARAM_FILE_NAME, fileName);
		intent.putExtra(PARAM_TRANSFERRED_BYTES, mTransferredBytes);
		intent.putExtra(PARAM_PROGRESS, progress);
		sendBroadcast(intent);
	}

	protected void broadcastTransferCompleted(String actionBroadcast, boolean fromAnotherApp, long rowId, String fileName, String savedFileName, int responseCode, String responseMessage, boolean weakLockRelease) {
//		if ( Constants.DEBUG ) Log.d(TAG, "broadcastTransferCompleted(), fileName=" + fileName + ", mTransferredBytes=" + mTransferredBytes + ", responseCode=" + responseCode + ", responseMessage=" + responseMessage);

		Intent intent = new Intent(actionBroadcast);
		intent.putExtra(PARAM_STATUS, STATUS_TRANSFER_COMPLETED);
		intent.putExtra(PARAM_FROM_ANOTHER_APP, fromAnotherApp);
//		intent.putExtra(PARAM_USER_ID, userId);
//		intent.putExtra(PARAM_COMPUTER_ID, computerId);
//		intent.putExtra(PARAM_GROUP_ID, groupId);
//		intent.putExtra(PARAM_TRANSFER_KEY, transferKey);
		intent.putExtra(PARAM_ROW_ID, rowId);
		intent.putExtra(PARAM_FILE_NAME, fileName);
		intent.putExtra(PARAM_TRANSFERRED_BYTES, mTransferredBytes);
		intent.putExtra(SERVER_RESPONSE_CODE, responseCode);
		intent.putExtra(SERVER_RESPONSE_MESSAGE, responseMessage);
		if ( !TextUtils.isEmpty(savedFileName) ) {
			intent.putExtra(PARAM_SAVED_FILE_NAME, savedFileName);
		}
		sendBroadcast(intent);
		if ( weakLockRelease ) {
			if ( wakeLock !=null && wakeLock.isHeld() ) {
				wakeLock.release();
			}
		}
	}

	protected void broadcastTransferError(String actionBroadcast, boolean fromAnotherApp, long rowId, String fileName, Exception exception) {
//		if ( Constants.DEBUG ) Log.d(TAG, "broadcastTransferError(), fileName=" + fileName + ", mTransferredBytes=" + mTransferredBytes + ", exception=" + exception.getMessage());

		Intent intent = new Intent(actionBroadcast);
		intent.setAction(actionBroadcast);
		intent.putExtra(PARAM_STATUS, STATUS_TRANSFER_ERROR);
		intent.putExtra(PARAM_FROM_ANOTHER_APP, fromAnotherApp);
//		intent.putExtra(PARAM_USER_ID, userId);
//		intent.putExtra(PARAM_COMPUTER_ID, computerId);
//		intent.putExtra(PARAM_GROUP_ID, groupId);
//		intent.putExtra(PARAM_TRANSFER_KEY, transferKey);
		intent.putExtra(PARAM_ROW_ID, rowId);
		intent.putExtra(PARAM_FILE_NAME, fileName);
		intent.putExtra(PARAM_TRANSFERRED_BYTES, mTransferredBytes);
		intent.putExtra(ERROR_EXCEPTION, exception);
		sendBroadcast(intent);
		if ( wakeLock !=null && wakeLock.isHeld() ) {
			wakeLock.release();
		}
	}

	protected void broadcastResponseError(String actionBroadcast, boolean fromAnotherApp, long rowId, String fileName, int responseCode, String errorMessage) {
//		if ( Constants.DEBUG ) Log.d(TAG, "broadcastResponseError(), fileName=" + fileName + ", mTransferredBytes=" + mTransferredBytes + ", responseCode=" + responseCode + ", errorMessage=" + errorMessage);

		Intent intent = new Intent(actionBroadcast);
		intent.setAction(actionBroadcast);
		intent.putExtra(PARAM_STATUS, STATUS_RESPONSE_ERROR);
		intent.putExtra(PARAM_FROM_ANOTHER_APP, fromAnotherApp);
//		intent.putExtra(PARAM_USER_ID, userId);
//		intent.putExtra(PARAM_COMPUTER_ID, computerId);
//		intent.putExtra(PARAM_GROUP_ID, groupId);
//		intent.putExtra(PARAM_TRANSFER_KEY, transferKey);
		intent.putExtra(PARAM_ROW_ID, rowId);
		intent.putExtra(PARAM_FILE_NAME, fileName);
		intent.putExtra(PARAM_TRANSFERRED_BYTES, mTransferredBytes);
		intent.putExtra(SERVER_RESPONSE_CODE, responseCode);
		intent.putExtra(ERROR_MESSAGE, errorMessage);
		sendBroadcast(intent);
		if ( wakeLock !=null && wakeLock.isHeld() ) {
			wakeLock.release();
		}
	}

	protected void broadcastCancel(String actionBroadcast, boolean fromAnotherApp, long rowId, String fileName) {
//		if ( Constants.DEBUG ) Log.d(TAG, "broadcastCancel(), fileName=" + fileName);

		Intent intent = new Intent(actionBroadcast);
		intent.putExtra(PARAM_STATUS, STATUS_CANCEL);
		intent.putExtra(PARAM_FROM_ANOTHER_APP, fromAnotherApp);
//		intent.putExtra(PARAM_USER_ID, userId);
//		intent.putExtra(PARAM_COMPUTER_ID, computerId);
//		intent.putExtra(PARAM_GROUP_ID, groupId);
//		intent.putExtra(PARAM_TRANSFER_KEY, transferKey);
		intent.putExtra(PARAM_ROW_ID, rowId);
		intent.putExtra(PARAM_FILE_NAME, fileName);
		intent.putExtra(PARAM_TRANSFERRED_BYTES, mTransferredBytes);
		sendBroadcast(intent);
		if ( wakeLock !=null && wakeLock.isHeld() ) {
			wakeLock.release();
		}
	}

	protected void broadcastConfirmed(String actionBroadcast, boolean fromAnotherApp, int notificationType, long rowId, String fileName, String confirmStatus) {
//		if ( Constants.DEBUG ) Log.d(TAG, "broadcastConfirmed(), fileName=" + fileName);

		Intent intent = new Intent(actionBroadcast);
		intent.putExtra(PARAM_STATUS, STATUS_CONFIRMED);
		intent.putExtra(PARAM_FROM_ANOTHER_APP, fromAnotherApp);
		intent.putExtra(PARAM_NOTIFICATION_TYPE, notificationType);
//		intent.putExtra(PARAM_USER_ID, userId);
//		intent.putExtra(PARAM_COMPUTER_ID, computerId);
//		intent.putExtra(PARAM_GROUP_ID, groupId);
//		intent.putExtra(PARAM_TRANSFER_KEY, transferKey);
		intent.putExtra(PARAM_ROW_ID, rowId);
		intent.putExtra(PARAM_FILE_NAME, fileName);
		intent.putExtra(PARAM_CONFIRM_STATUS, confirmStatus);
		sendBroadcast(intent);
		if ( wakeLock !=null && wakeLock.isHeld() ) {
			wakeLock.release();
		}
	}

//	protected void broadcastServiceDestroy(String actionBroadcast) {
//		if ( Constants.DEBUG ) Log.d(TAG, "broadcastServiceDestroy()");
//
//		Intent intent = new Intent(actionBroadcast);
//		intent.putExtra(PARAM_STATUS, STATUS_SERVICE_DESTROY);
//		sendBroadcast(intent);
//		wakeLock.release();
//	}

}
