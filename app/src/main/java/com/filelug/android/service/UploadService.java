package com.filelug.android.service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.toolbox.RequestFuture;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.crepo.RepositoryErrorObject;
import com.filelug.android.provider.assetfile.UploadStatusType;
import com.filelug.android.util.FileCache;
import com.filelug.android.util.FilelugUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.TransferDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UploadService extends FilelugService {

    private static final String TAG = UploadService.class.getSimpleName();
    private static final String SERVICE_NAME = UploadService.class.getName();

    private static volatile boolean stopAll = false;
    private static volatile boolean stopThis = false;
    private static volatile List<String> filesToBeCancel;

    private static String currentUploadGroupAndKey = null;

    @Override
    public void onCreate() {
        super.onCreate();
//        if ( Constants.DEBUG ) Log.d(TAG, "onCreate()");

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(this);
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, SERVICE_NAME);
        stopAll = false;
        filesToBeCancel = new ArrayList<String>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if ( Constants.DEBUG ) Log.d(TAG, "onDestroy()");
//        broadcastServiceDestroy(getActionUploadBroadcast());
    }

    // LocalFileUtils.uploadFiles() -->
    // LocalFileUtils.uploadFile() -->
    // LocalFileUtils.uploadFile_resume() -->
    public static void startUpload(final UploadRequest task) throws IllegalArgumentException, MalformedURLException {
//        if ( Constants.DEBUG ) Log.d(TAG, "startUpload(), TransferKey=" + task.getTransferKey());
        if (task == null) {
            throw new IllegalArgumentException("Can't pass an empty task!");
        } else {
            final Intent intent = new Intent(task.getContext(), UploadService.class);
            intent.setAction(getActionUpload());
            intent.putExtra(PARAM_NOTIFICATION_CONFIG, task.getNotificationConfig());
            intent.putExtra(PARAM_USER_ID, task.getUserId());
            intent.putExtra(PARAM_COMPUTER_ID, task.getComputerId());
            intent.putExtra(PARAM_GROUP_ID, task.getGroupId());
            intent.putExtra(PARAM_TRANSFER_KEY, task.getTransferKey());
            intent.putExtra(PARAM_LUG_SERVER_ID, task.getLugServerId());
            intent.putExtra(PARAM_AUTH_TOKEN, task.getAuthToken());
            intent.putExtra(PARAM_FROM_ANOTHER_APP, task.isFromAnotherApp());
            intent.putExtra(PARAM_CUSTOM_USER_AGENT, task.getCustomUserAgent());
            intent.putExtra(PARAM_FILE, task.getFileToUpload());
            intent.putParcelableArrayListExtra(PARAM_REQUEST_HEADERS, task.getHeaders());
            intent.putParcelableArrayListExtra(PARAM_REQUEST_PARAMETERS, task.getParameters());
            task.getContext().startService(intent);
        }
    }

    /**
     * Stop all upload tasks.
     */
    public static void stopAllTasks() {
        stopAll = true;
    }

    /**
     * Stop this upload task.
     */
    public static void stopUpload(String userId, int computerId, String groupId, String transferKey) {
        String key = userId + GROUP_KEY_SEPARATOR + computerId + GROUP_KEY_SEPARATOR + groupId + GROUP_KEY_SEPARATOR + transferKey;
        filesToBeCancel.add(key);
        if ( currentUploadGroupAndKey.equals(key) ) {
            stopThis = true;
        } else {
            stopThis = false;
        }
    }

    /**
     * Cancel this upload task.
     */
    public static void cancelUpload(String userId, int computerId, String groupId, String transferKey, long rowId) {
        String key = userId + GROUP_KEY_SEPARATOR + computerId + GROUP_KEY_SEPARATOR + groupId + GROUP_KEY_SEPARATOR + transferKey;
        filesToBeCancel.add(key);
        if ( currentUploadGroupAndKey.equals(key) ) {
            stopThis = true;
        } else {
            stopThis = false;
            TransferDBHelper.writeUploadCancelStatus(rowId);
        }
    }

    public UploadService() {
        super(SERVICE_NAME);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
//        String action = rootIntent.getAction();
//        if (getActionUpload().equals(action)) {
//            final FileToUpload file = rootIntent.getParcelableExtra(PARAM_FILE);
//            NotificationUtils.noticeFileUploadFailed(getApplicationContext(), file.getFileName());
//        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
//        if ( Constants.DEBUG ) Log.d(TAG, "onHandleIntent()");

        if (intent != null) {
            final String action = intent.getAction();

            if (getActionUpload().equals(action)) {
                notificationConfig = intent.getParcelableExtra(PARAM_NOTIFICATION_CONFIG);
                final String userId = intent.getStringExtra(PARAM_USER_ID);
                final int computerId = intent.getIntExtra(PARAM_COMPUTER_ID, 0);
                final String groupId = intent.getStringExtra(PARAM_GROUP_ID);
                final String transferKey = intent.getStringExtra(PARAM_TRANSFER_KEY);
                final String lugServerId = intent.getStringExtra(PARAM_LUG_SERVER_ID);
                final String authToken = intent.getStringExtra(PARAM_AUTH_TOKEN);
                final boolean fromAnotherApp = intent.getBooleanExtra(PARAM_FROM_ANOTHER_APP, false);
                final String customUserAgent = intent.getStringExtra(PARAM_CUSTOM_USER_AGENT);
                final FileToUpload fileToUpload = intent.getParcelableExtra(PARAM_FILE);
                final ArrayList<NameValue> headers = intent.getParcelableArrayListExtra(PARAM_REQUEST_HEADERS);
                final ArrayList<NameValue> parameters = intent.getParcelableArrayListExtra(PARAM_REQUEST_PARAMETERS);
                final long rowId = TransferDBHelper.getUploadRowId(userId, computerId, groupId, transferKey);
                final String fileName = fileToUpload.getFileName();

                currentUploadGroupAndKey = userId + GROUP_KEY_SEPARATOR + computerId + GROUP_KEY_SEPARATOR + groupId + GROUP_KEY_SEPARATOR + transferKey;
                if ( filesToBeCancel.contains(currentUploadGroupAndKey) ) {
                    stopThis = true;
                } else {
                    stopThis = false;
                }

                wakeLock.acquire();

                if ( stopAll ) {
//                    if ( Constants.DEBUG ) Log.d(TAG, "onHandleIntent(), stopAll=true, fileName="+fileName + ", status=canceling");
                    broadcastCancel(getActionUploadBroadcast(), fromAnotherApp, rowId, fileName);
                    return;
                } else {
                    broadcastPrepare(getActionUploadBroadcast(), fromAnotherApp, rowId, fileName, fileToUpload.isResume());
                }

                Context context = getApplicationContext();
                Bundle result = FilelugUtils.pingDesktopA(context, authToken, userId);
                String pingErrMsg = result.getString(Constants.EXT_PARAM_ERROR_MESSAGE, null);
                if ( pingErrMsg != null ) {
//                    if ( Constants.DEBUG ) Log.d(TAG, "onHandleIntent(), fileName="+fileName + ", Ping desktop failed!");
                    broadcastPingError(getActionUploadBroadcast(), fromAnotherApp, rowId, fileName, pingErrMsg);
                    return;
                }

                try {
                    doFileUpload(fromAnotherApp, transferKey, rowId, lugServerId, authToken, fileToUpload, headers, parameters, customUserAgent);
                } catch (Exception ex) {
                    Log.e(TAG, "doFileUpload() --> Error: " + ex.getMessage());
                    broadcastTransferError(getActionUploadBroadcast(), fromAnotherApp, rowId, fileName, ex);
                }
            }
        }
    }

    private void doFileUpload(final boolean fromAnotherApp, final String transferKey, final long rowId, final String lugServerId,
                              final String authToken, final FileToUpload fileToUpload,
                              final ArrayList<NameValue> requestHeaders,
                              final ArrayList<NameValue> requestParameters,
                              final String customUserAgent) throws IOException {

//        if ( Constants.DEBUG ) Log.d(TAG, "doFileUpload()");

        HttpURLConnection conn = null;
        OutputStream requestStream = null;
        InputStream responseStream = null;
        int serverResponseCode = -1;
        String serverResponseMessage = null;

        try {
            // get the content length
            final long bodyLength = fileToUpload.getFileSize() - fileToUpload.getCacheFileSize();

            if (android.os.Build.VERSION.SDK_INT < 19 && bodyLength > Integer.MAX_VALUE)
                throw new IOException("You need Android API version 19 or newer to "
                        + "upload more than 2GB in a single request using "
                        + "fixed size content length. Try switching to "
                        + "chunked mode instead, but make sure your server side supports it!");

            String url = null;
            if ( Constants.PROTOCOL.startsWith(Constants.PROTOCOL_HTTPS) ) {
                url = String.format(Constants.LUG_UPLOAD_FILE_4_URI, lugServerId);
            } else {
                url = Constants.LUG_UPLOAD_FILE_4_URI;
            }
            conn = getHttpURLConnection(url);

            String contentType = fileToUpload.getContentType();
            if ( TextUtils.isEmpty(contentType) ) {
                contentType = ContentType.APPLICATION_OCTET_STREAM;
            }
            requestHeaders.add(new NameValue(Constants.HTTP_HEADER_CONTENT_TYPE, contentType));

            if (customUserAgent != null && !customUserAgent.equals("")) {
                requestHeaders.add(new NameValue(Constants.HTTP_HEADER_USER_AGENT, customUserAgent));
            }

            setRequestHeaders(conn, requestHeaders);

            if (android.os.Build.VERSION.SDK_INT >= 19) {
                conn.setFixedLengthStreamingMode(bodyLength);
            } else {
                conn.setFixedLengthStreamingMode((int) bodyLength);
            }

            String fileName = fileToUpload.getFileName();
            long fileSize = fileToUpload.getFileSize();
            long cachedSize = fileToUpload.getCacheFileSize();

            broadcastStart(getActionUploadBroadcast(), fromAnotherApp, rowId, fileName, fileSize, cachedSize);

            requestStream = conn.getOutputStream();
            uploadFile(fromAnotherApp, rowId, requestStream, fileToUpload);
            serverResponseCode = conn.getResponseCode();

            if ( stopAll || stopThis ) {
                broadcastCancel(getActionUploadBroadcast(), fromAnotherApp, rowId, fileName);
            } else if (serverResponseCode / 100 == 2) {
                responseStream = conn.getInputStream();
                serverResponseMessage = getResponseBodyAsString(responseStream);
                broadcastTransferCompleted(getActionUploadBroadcast(), fromAnotherApp, rowId, fileName, null, serverResponseCode, serverResponseMessage, true);
                confirmUpload(authToken, lugServerId, transferKey, rowId, fileName, fileToUpload.getCacheFileName(), UploadStatusType.processing.toString());
            } else { // getErrorStream if the response code is not 2xx
                responseStream = conn.getErrorStream();
                if ( responseStream != null ) {
                    serverResponseMessage = getResponseBodyAsString(responseStream);
                } else {
                    Context context = getApplicationContext();
                    serverResponseMessage = context.getResources().getString(R.string.message_server_error);
                }
                broadcastResponseError(getActionUploadBroadcast(), fromAnotherApp, rowId, fileName, serverResponseCode, serverResponseMessage);
            }
        } finally {
            closeOutputStream(requestStream);
            closeInputStream(responseStream);
            closeConnection(conn);
        }
    }

    private String getResponseBodyAsString(final InputStream inputStream) {
        StringBuilder outString = new StringBuilder();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                outString.append(line);
            }
        } catch (Exception exc) {
            try {
                if (reader != null)
                    reader.close();
            } catch (Exception readerExc) {
            }
        }

        return outString.toString();
    }

    private void confirmUpload(String authToken, String lugServerId, String transferKey, long rowId,
                               String fileName, String cacheFileName, String status) {
//        if ( Constants.DEBUG ) Log.d(TAG, "confirmUpload(), fileName=" + fileName);

        Context context = getApplicationContext();
        int timeOut = getApplicationContext().getResources().getInteger(R.integer.sync_timeout_a);
        String locale = getApplicationContext().getResources().getConfiguration().locale.toString();
        int maxTry = 5;
        boolean confirmed = false;

        for (int i = 0; i < maxTry; i++) {

            RequestFuture<JSONArray> future = RequestFuture.newFuture();
            RepositoryClient.getInstance().confirmUploadFileFromDevice2(authToken, lugServerId, transferKey, status, locale, future, future);
            JSONArray response = null;
            RepositoryErrorObject errorObject = null;

            try {
                response = future.get(timeOut, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                errorObject = MiscUtils.getErrorObject(context, e, null);
            }

            if ( errorObject != null ) {
                Log.e(TAG, "confirm(), " + errorObject.getMessage());
                sleep();
                continue;
            }

            if ( response.length() <= 0 ) {
                Log.e(TAG, "confirm(), Response error!");
                sleep();
                continue;
            }

            String tmpTransferKey = null;
            String tmpStatus = null;

            try {
                JSONObject jso = response.getJSONObject(0);
                tmpTransferKey = jso.getString(Constants.PARAM_TRANSFER_KEY);
                tmpStatus = jso.getString(Constants.PARAM_STATUS);
            } catch (JSONException e) {
                Log.e(TAG, "confirm(), Upload confirm json object parsing error!");
            }

//            if ( Constants.DEBUG ) Log.d(TAG, "confirmUpload(), fileName=" + fileName + ", tmpStatus=" + tmpStatus + ", cacheFileName=" + cacheFileName);
            if ( UploadStatusType.processing.name().equals(tmpStatus) ||
                 UploadStatusType.device_uploaded_but_unconfirmed.name().equals(tmpStatus) ) {
                i--;
                sleep();
                continue;
            }

            if ( UploadStatusType.success.name().equals(tmpStatus) ) {
                if ( !TextUtils.isEmpty(cacheFileName) ) {
                    FileCache.deleteFile(cacheFileName);
                }
            }

            broadcastConfirmed(getActionUploadBroadcast(), false, -1, rowId, fileName, tmpStatus);

            confirmed = true;
            break;
        }

        if ( !confirmed ) {
//            NotificationUtils.removeUploadNotification(context, rowId);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            Log.e(TAG, "sleep(), Sleep... " + e.getMessage());
        }

    }

    private HttpURLConnection getHttpURLConnection(final String url) throws IOException {
        final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

        Context context = getApplicationContext();
        conn.setDoInput(true);
        conn.setDoOutput(true);
//        conn.setConnectTimeout(5000);
        conn.setReadTimeout(context.getResources().getInteger(R.integer.upload_read_timeout));
        conn.setConnectTimeout(context.getResources().getInteger(R.integer.transfer_connection_timeout));
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "close");
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");

        return conn;
    }

    private void uploadFile(final boolean fromAnotherApp, final long rowId, final OutputStream requestStream, final FileToUpload fileToUpload) throws UnsupportedEncodingException, IOException, FileNotFoundException {
        String fileName = fileToUpload.getFileName();
        long cachedSize = fileToUpload.getCacheFileSize();
        long writeBytes = cachedSize;

//        if ( Constants.DEBUG ) Log.d(TAG, "uploadFile(), rowId=" + rowId + ", fileName=" + fileName + ", cachedSize=" + cachedSize);

        final InputStream stream = fileToUpload.getStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        try {
            stream.skip(cachedSize);
            while ((bytesRead = stream.read(buffer, 0, buffer.length)) > 0) {
                if ( stopAll || stopThis ) break;
                requestStream.write(buffer, 0, bytesRead);
                writeBytes += bytesRead;
                broadcastProgressing(getActionUploadBroadcast(), fromAnotherApp, rowId, fileName, writeBytes);
            }
        } finally {
            closeInputStream(stream);
        }
    }

}
