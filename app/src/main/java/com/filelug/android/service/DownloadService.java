package com.filelug.android.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.BaseResponseError;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.crepo.RepositoryErrorObject;
import com.filelug.android.provider.filetransfer.DownloadStatusType;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.FileCache;
import com.filelug.android.util.FilelugUtils;
import com.filelug.android.util.MediaScannerClient;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.RemoteFileUtils;
import com.filelug.android.util.TransferDBHelper;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vincent Chang on 2015/9/18.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class DownloadService extends FilelugService {

    private static final String TAG = DownloadService.class.getSimpleName();
    private static final String SERVICE_NAME = DownloadService.class.getName();

    private static volatile boolean stopAll = false;
    private static volatile boolean stopThis = false;
    private static volatile List<String> downloadedFiles;
    private static volatile List<String> filesToBeCancel;

    private static String currentDownloadGroupAndKey = null;

    @Override
    public void onCreate() {
        super.onCreate();
//        if ( Constants.DEBUG ) Log.d(TAG, "onCreate()");

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(this);
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, SERVICE_NAME);
        stopAll = false;
        downloadedFiles = new ArrayList<String>();
        filesToBeCancel = new ArrayList<String>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if ( Constants.DEBUG ) Log.d(TAG, "onDestroy(), downloadedFiles=" + downloadedFiles.size());
        Context context = getApplicationContext();
        if ( downloadedFiles != null && downloadedFiles.size() > 0 ) {
            new MediaScannerClient(context, downloadedFiles);
        }
//        DownloadNotificationService.beginStartingService(context);
    }

    // RemoteFileUtils.downloadFiles() -->
    // RemoteFileUtils.downloadFile() -->
    // RemoteFileUtils.downloadFile_resume() -->
    public static void startDownload(final DownloadRequest task) throws IllegalArgumentException, MalformedURLException {
//        if ( Constants.DEBUG ) Log.d(TAG, "startDownload()");
        if (task == null) {
            throw new IllegalArgumentException("Can't pass an empty task!");
        } else {
            final Intent intent = new Intent(task.getContext(), DownloadService.class);
            intent.setAction(getActionDownload());
            intent.putExtra(PARAM_NOTIFICATION_CONFIG, task.getNotificationConfig());
            intent.putExtra(PARAM_USER_ID, task.getUserId());
            intent.putExtra(PARAM_COMPUTER_ID, task.getComputerId());
            intent.putExtra(PARAM_GROUP_ID, task.getGroupId());
            intent.putExtra(PARAM_TRANSFER_KEY, task.getTransferKey());
            intent.putExtra(PARAM_LUG_SERVER_ID, task.getLugServerId());
            intent.putExtra(PARAM_AUTH_TOKEN, task.getAuthToken());
            intent.putExtra(PARAM_NOTIFICATION_TYPE, task.getNotificationType());
            intent.putExtra(PARAM_FROM_ANOTHER_APP, task.isFromAnotherApp());
            intent.putExtra(PARAM_CUSTOM_USER_AGENT, task.getCustomUserAgent());
            intent.putExtra(PARAM_FILE, task.getFileToDownload());
            intent.putParcelableArrayListExtra(PARAM_REQUEST_HEADERS, task.getHeaders());
            intent.putParcelableArrayListExtra(PARAM_REQUEST_PARAMETERS, task.getParameters());
            task.getContext().startService(intent);
        }
    }

    /**
     * Stop all download tasks.
     */
    public static void stopAllTasks() {
        stopAll = true;
    }

    /**
     * Stop this download task.
     */
    public static void stopDownload(String userId, int computerId, String groupId, String transferKey) {
        String key = userId + GROUP_KEY_SEPARATOR + computerId + GROUP_KEY_SEPARATOR + groupId + GROUP_KEY_SEPARATOR + transferKey;
        filesToBeCancel.add(key);
        if ( currentDownloadGroupAndKey.equals(key) ) {
            stopThis = true;
        } else {
            stopThis = false;
        }
    }

    /**
     * Cancel this download task.
     */
    public static void cancelDownload(String userId, int computerId, String groupId, String transferKey, long rowId) {
        String key = userId + GROUP_KEY_SEPARATOR + computerId + GROUP_KEY_SEPARATOR + groupId + GROUP_KEY_SEPARATOR + transferKey;
        filesToBeCancel.add(key);
        if ( currentDownloadGroupAndKey.equals(key) ) {
            stopThis = true;
        } else {
            stopThis = false;
            TransferDBHelper.writeDownloadCancelStatus(rowId);
        }
    }

    public DownloadService() {
        super(SERVICE_NAME);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
//        if ( Constants.DEBUG ) Log.d(TAG, "onTaskRemoved()");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
//        if ( Constants.DEBUG ) Log.d(TAG, "onHandleIntent()");

        if (intent != null) {
            final String action = intent.getAction();

            if (getActionDownload().equals(action)) {
                notificationConfig = intent.getParcelableExtra(PARAM_NOTIFICATION_CONFIG);
                final String userId = intent.getStringExtra(PARAM_USER_ID);
                final int computerId = intent.getIntExtra(PARAM_COMPUTER_ID, 0);
                final String groupId = intent.getStringExtra(PARAM_GROUP_ID);
                final String transferKey = intent.getStringExtra(PARAM_TRANSFER_KEY);
                final String lugServerId = intent.getStringExtra(PARAM_LUG_SERVER_ID);
                final String authToken = intent.getStringExtra(PARAM_AUTH_TOKEN);
                final int notificationType = intent.getIntExtra(PARAM_NOTIFICATION_TYPE, 0);
                final boolean fromAnotherApp = intent.getBooleanExtra(PARAM_FROM_ANOTHER_APP, false);
                final String customUserAgent = intent.getStringExtra(PARAM_CUSTOM_USER_AGENT);
                final FileToDownload fileToDownload = intent.getParcelableExtra(PARAM_FILE);
                final ArrayList<NameValue> headers = intent.getParcelableArrayListExtra(PARAM_REQUEST_HEADERS);
                final ArrayList<NameValue> parameters = intent.getParcelableArrayListExtra(PARAM_REQUEST_PARAMETERS);
                final long rowId = TransferDBHelper.getDownloadRowId(userId, computerId, groupId, transferKey);
                final String fileName = fileToDownload.getFileName();

                if ( notificationType == 2 ) {
                    DownloadNotificationService.addDownloadedGroupID(userId, computerId, groupId);
                }

                currentDownloadGroupAndKey = userId + GROUP_KEY_SEPARATOR + computerId + GROUP_KEY_SEPARATOR + groupId + GROUP_KEY_SEPARATOR + transferKey;
                if ( filesToBeCancel.contains(currentDownloadGroupAndKey) ) {
                    stopThis = true;
                } else {
                    stopThis = false;
                }

                wakeLock.acquire();

                if ( stopAll || stopThis ) {
//                    if ( Constants.DEBUG ) Log.d(TAG, "onHandleIntent(), stopAll=true, fileName="+fileName + ", status=canceling");
                    if ( notificationType == 2 ) {
                        DownloadNotificationService.removeDownloadedGroupID(userId, computerId, groupId);
                    }
                    cancelDownloadFile(fromAnotherApp, transferKey, rowId, fileName, lugServerId, authToken);
                    return;
                } else {
                    broadcastPrepare(getActionDownloadBroadcast(), fromAnotherApp, rowId, fileName, fileToDownload.isResume());
                }

                Context context = getApplicationContext();
                Bundle result = FilelugUtils.pingDesktopA(context, authToken, userId);
                String pingErrMsg = result.getString(Constants.EXT_PARAM_ERROR_MESSAGE, null);
                if ( pingErrMsg != null ) {
//                    if ( Constants.DEBUG ) Log.d(TAG, "onHandleIntent(), fileName="+fileName + ", Ping desktop failed!");
                    if ( notificationType == 2 ) {
                        DownloadNotificationService.removeDownloadedGroupID(userId, computerId, groupId);
                    }
                    broadcastPingError(getActionDownloadBroadcast(), fromAnotherApp, rowId, fileName, pingErrMsg);
                    return;
                }

                try {
                    doFileDownload(fromAnotherApp, userId, computerId, groupId, transferKey, rowId, lugServerId, authToken, fileToDownload, notificationType, headers, parameters, customUserAgent);
                } catch (Exception ex) {
                    Log.e(TAG, "doFileDownload() --> Error: " + ex.getMessage());
                    if ( notificationType == 2 ) {
                        DownloadNotificationService.removeDownloadedGroupID(userId, computerId, groupId);
                    }
                    broadcastTransferError(getActionDownloadBroadcast(), fromAnotherApp, rowId, fileName, ex);
                    confirmDownload(fromAnotherApp, authToken, lugServerId, groupId, transferKey, rowId, fileName, DownloadStatusType.failure.toString(), mTransferredBytes, notificationType);
                }
            }
        }
    }

    private void doFileDownload(final boolean fromAnotherApp, final String userId, final int computerId,
                                final String groupId, final String transferKey,
                                final long rowId, final String lugServerId, final String authToken,
                                final FileToDownload fileToDownload, final int notificationType,
                                final ArrayList<NameValue> requestHeaders,
                                final ArrayList<NameValue> requestParameters,
                                final String customUserAgent) throws IOException, JSONException {

//        if ( Constants.DEBUG ) Log.d(TAG, "doFileDownload()");

        HttpURLConnection conn = null;
        OutputStream requestStream = null;
        InputStream responseStream = null;
        String status = DownloadStatusType.failure.toString();
        int serverResponseCode = -1;
        String serverResponseMessage = null;

        try {
            // get the content length of the request body
//            final long bodyLength = fileToDownload.length();
//
//            if (android.os.Build.VERSION.SDK_INT < 19 && bodyLength > Integer.MAX_VALUE)
//                throw new IOException("You need Android API version 19 or newer to "
//                        + "download more than 2GB in a single request using "
//                        + "fixed size content length. Try switching to "
//                        + "chunked mode instead, but make sure your server side supports it!");

            String url = null;
            if ( Constants.PROTOCOL.startsWith(Constants.PROTOCOL_HTTPS) ) {
                url = String.format(Constants.LUG_DOWNLOAD_FILE_3_URI, lugServerId);
            } else {
                url = Constants.LUG_DOWNLOAD_FILE_3_URI;
            }

            String queryStr = null;
            for (final NameValue param : requestParameters) {
                String name = param.getName();
                String value = param.getValue();
                if ( queryStr == null ) {
                    queryStr = "?" + name + "=" + value;
                } else {
                    queryStr = "&" + name + "=" + value;
                }
            }
            if ( queryStr != null ) {
                url += queryStr;
            }

            conn = getHttpURLConnection(url);
            if (customUserAgent != null && !customUserAgent.equals("")) {
                requestHeaders.add(new NameValue(Constants.HTTP_HEADER_USER_AGENT, customUserAgent));
            }
            setRequestHeaders(conn, requestHeaders);

            serverResponseCode = conn.getResponseCode();
//            if ( Constants.DEBUG ) Log.d(TAG, "doFileDownload(), responseCode=" + serverResponseCode);

            String fileName = fileToDownload.getFileName();
            String fileRealName = fileToDownload.getFileRealName();
            String localDir = fileToDownload.getLocalDir();
            long fileSize = fileToDownload.length();
            long fileLastModified = fileToDownload.getLastModified();
            String savedFileName = null;

            if (serverResponseCode / 100 == 2) {
                if ( fromAnotherApp ) {
                    savedFileName = fileRealName;
                    long totalBytes = Long.parseLong(conn.getHeaderField(Constants.HTTP_HEADER_CONTENT_LENGTH));
                    broadcastStart(getActionDownloadBroadcast(), fromAnotherApp, rowId, fileName, totalBytes, 0);
                    String filePath = fileToDownload.getFilePath();
                    String fileRealPath = fileToDownload.getFileRealPath();
                    downloadFileToCache(rowId, conn, filePath, fileName, fileRealPath, savedFileName);
                } else {
                    savedFileName = getSavedName(localDir, fileRealName);
                    String responseLastModified = conn.getHeaderField(Constants.HTTP_HEADER_LAST_MODIFIED);
                    long responseLastModifiedDate = conn.getHeaderFieldDate(Constants.HTTP_HEADER_LAST_MODIFIED, -1l);
                    long responseContentLength = Long.parseLong(conn.getHeaderField(Constants.HTTP_HEADER_CONTENT_LENGTH));
                    String responseAcceptRanges = conn.getHeaderField(Constants.HTTP_HEADER_ACCEPT_RANGES);
                    String responseContentRange = conn.getHeaderField(Constants.HTTP_HEADER_CONTENT_RANGE);
                    Bundle contentRangeInfo = null;
                    if ( responseContentRange != null ) { // Resume download
                        contentRangeInfo = convertContentRangeInfo(responseContentRange);
                    }
                    long totalBytes = 0L;
                    long startIndex = 0L;
                    long endIndex = 0L;
                    if ( contentRangeInfo == null ) {
                        totalBytes = responseContentLength;
                    } else {
                        totalBytes = contentRangeInfo.getLong("totalBytes");
                        startIndex = contentRangeInfo.getLong("startIndex");
                        endIndex = contentRangeInfo.getLong("endIndex");
                    }

                    String cachedFileName = localDir + File.separator + fileRealName + "." + fileSize + "." + fileLastModified + Constants.DOWNLOAD_FILENAME_SUFFIX;
                    File cachedFile = new File(cachedFileName);
                    String downloadTempFileName = null;
                    if ( fileSize != totalBytes || fileLastModified != responseLastModifiedDate ) {
                        if ( cachedFile.exists() ) {
                            cachedFile.delete();
                        }
                        downloadTempFileName = fileRealName + "." + totalBytes + "." + responseLastModifiedDate + Constants.DOWNLOAD_FILENAME_SUFFIX;
                    } else {
                        if ( cachedFile.exists() ) {
                            startIndex = cachedFile.length();
                        } else {
                            startIndex = 0L;
                        }
                        downloadTempFileName = fileRealName + "." + fileSize + "." + fileLastModified + Constants.DOWNLOAD_FILENAME_SUFFIX;
                    }

                    broadcastStart(getActionDownloadBroadcast(), fromAnotherApp, rowId, fileName, totalBytes, startIndex);
                    downloadFile(rowId, conn, fileName, localDir, savedFileName, downloadTempFileName, startIndex, totalBytes);
                }

                if ( stopAll || stopThis ) {
//                    broadcastCancel(getActionDownloadBroadcast(), fromAnotherApp, rowId, fileName);
                    broadcastTransferError(getActionDownloadBroadcast(), fromAnotherApp, rowId, fileName, null);
                } else {
                    broadcastTransferCompleted(getActionDownloadBroadcast(), fromAnotherApp, rowId, fileName, savedFileName, serverResponseCode, transferKey, false);
                    status = DownloadStatusType.success.toString();
                }

                responseStream = conn.getInputStream();
            } else { // getErrorStream if the response code is not 2xx
                responseStream = conn.getErrorStream();
                if ( responseStream != null ) {
                    serverResponseMessage = getResponseBodyAsString(responseStream);
                } else {
                    Context context = getApplicationContext();
                    serverResponseMessage = context.getResources().getString(R.string.message_server_error);
                }
                broadcastResponseError(getActionDownloadBroadcast(), fromAnotherApp, rowId, fileName, serverResponseCode, serverResponseMessage);
            }

            if ( DownloadStatusType.failure.toString().equals(status) ) {
                if ( notificationType == 2 ) {
                    DownloadNotificationService.removeDownloadedGroupID(userId, computerId, groupId);
                }
            }
            confirmDownload(fromAnotherApp, authToken, lugServerId, groupId, transferKey, rowId, fileName, status, mTransferredBytes, notificationType);

        } finally {
            closeOutputStream(requestStream);
            closeInputStream(responseStream);
            closeConnection(conn);
        }
    }

    private Bundle convertContentRangeInfo(String contentRange) {
        if ( TextUtils.isEmpty(contentRange) ) {
            return null;
        }
        String[] tmpArray1 = contentRange.split(" ");
        if ( tmpArray1 == null || tmpArray1.length != 2 ) {
            return null;
        }
        String tmpString1 = tmpArray1[1];
        String[] tmpArray2 = tmpString1.split("/");
        if ( tmpArray2 == null || tmpArray2.length != 2 ) {
            return null;
        }
        String totalSizeStr = tmpArray2[1];
        String tmpString2 = tmpArray2[0];
        String[] tmpArray3 = tmpString2.split("-");
        if ( tmpArray3 == null || tmpArray3.length != 2 ) {
            return null;
        }
        String startIndexStr = tmpArray3[0];
        String endIndexStr = tmpArray3[1];

        Long totalBytes = null;
        Long startIndex = null;
        Long endIndex = null;
        try {
            totalBytes = Long.decode(totalSizeStr);
            startIndex = Long.decode(startIndexStr);
            endIndex = Long.decode(endIndexStr);
        } catch ( NumberFormatException nfe ) {
            Log.e(TAG, "convertContentRangeInfo(), Content-Range: [" + contentRange + "] parsing error!");
        }

        if ( totalBytes == null || startIndex == null || endIndex == null ) {
            return null;
        }

        Bundle result = new Bundle();
        result.putLong("totalBytes", totalBytes);
        result.putLong("startIndex", startIndex);
        result.putLong("endIndex", endIndex);

        return result;
    }

    private HttpURLConnection getHttpURLConnection(final String url) throws IOException {
        final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

        Context context = getApplicationContext();
        conn.setDoInput(true);
        conn.setReadTimeout(context.getResources().getInteger(R.integer.download_read_timeout));
        conn.setConnectTimeout(context.getResources().getInteger(R.integer.transfer_connection_timeout));
        conn.setUseCaches(false);
        conn.setRequestMethod("GET");
        conn.setRequestProperty(Constants.HTTP_HEADER_CONNECTION, "keep-alive");
        conn.setRequestProperty(Constants.HTTP_HEADER_ACCEPT, "*/*");
        conn.setRequestProperty(Constants.HTTP_HEADER_ACCEPT_LANGUAGE, "en-us");
        conn.setRequestProperty(Constants.HTTP_HEADER_ACCEPT_ENCODING, "gzip, deflate");

        return conn;
    }

    private String getSavedName(final String path, final String fileName) {
        String primaryName = null;
        String fileExtension = MiscUtils.getExtension(fileName, false);
        if ( fileExtension == null ) {
            primaryName = fileName;
        } else {
            primaryName = fileName.substring(0, fileName.length()-fileExtension.length()-1);
        }

        int i = 1;
        File savedFile = new File(path, fileName);
        while ( savedFile.exists() && !savedFile.isDirectory() ) {
            String fName = primaryName + "-" + i;
            if ( fileExtension != null ) {
                fName += "." + fileExtension;
            }
            savedFile = new File(path, fName);
            i++;
        }

        return savedFile.getName();
    }

    private long downloadFile(final long rowId, final HttpURLConnection conn, final String fileName, final String localDir, final String savedFileName, final String downloadTempFileName, final long cachedSize, final long totalSize) throws IOException {
        long downloadedBytes = cachedSize;

//        if ( Constants.DEBUG ) Log.d(TAG, "downloadFile(), rowId=" + rowId + ", fileName=" + fileName + ", localDir=" + localDir + ", savedFileName=" + savedFileName + ", downloadTempFileName=" + downloadTempFileName);

        boolean isAppend = cachedSize > 0l ? true : false;
        final InputStream stream = conn.getInputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        boolean downloaded = false;

        File file = new File(localDir + File.separator + downloadTempFileName);
        FileOutputStream fileOutput = new FileOutputStream(file, isAppend);

        try {
            while ( (bytesRead = stream.read(buffer, 0, buffer.length)) > 0 ) {
                if ( stopAll || stopThis ) break;
                fileOutput.write(buffer, 0, bytesRead);
                downloadedBytes += bytesRead;
                broadcastProgressing(getActionDownloadBroadcast(), false, rowId, fileName, downloadedBytes);
            }
            if ( !(stopAll || stopThis) ) {
                downloaded = true;
            }
        } finally {
            closeOutputStream(fileOutput);
        }

        if ( downloaded ) {
            renameAndScanFile(localDir, downloadTempFileName, savedFileName);
        }

        return downloadedBytes;
    }

    private void renameAndScanFile(String path, String fromName, String toName) {
//        if ( Constants.DEBUG ) Log.d(TAG, "renameAndScanFile(), path=" + path + ", fromName=" + fromName + ", toName=" + toName);
        // Rename
        File savedPath = new File(path);
        File from = new File(savedPath, fromName);
        File to = new File(savedPath, toName);
        from.renameTo(to);
        // Tag file need scan
        String filePath = to.getAbsolutePath();
        if ( !downloadedFiles.contains(filePath) ) {
            downloadedFiles.add(filePath);
        }
    }

    private long downloadFileToCache(final long rowId, final HttpURLConnection conn, final String filePath, final String fileName, final String fileRealPath, final String savedFileName) throws IOException {
        long downloadedBytes = 0;

//        if ( Constants.DEBUG ) Log.d(TAG, "downloadFile(), rowId=" + rowId + ", fileName=" + fileName + ", fileRealPath=" + fileRealPath + ", savedFileName=" + savedFileName);

        final InputStream stream = conn.getInputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        boolean downloaded = false;

        String localDir = FileCache.createDirInActiveAccountCache(fileRealPath);
        File file = new File(localDir + File.separator + savedFileName);
        FileOutputStream fileOutput = new FileOutputStream(file);

        try {
            while ( (bytesRead = stream.read(buffer, 0, buffer.length)) > 0 ) {
                if ( stopAll || stopThis ) break;
                fileOutput.write(buffer, 0, bytesRead);
                downloadedBytes += bytesRead;
                broadcastProgressing(getActionDownloadBroadcast(), true, rowId, fileName, downloadedBytes);
            }
            downloaded = true;
        } finally {
            closeOutputStream(fileOutput);
        }

        if ( downloaded ) {
            Context context = getApplicationContext();
            Account activeAccount = AccountUtils.getActiveAccount();
            AccountManager accountManager = AccountManager.get(context);
            String userId = accountManager.getUserData(activeAccount, Constants.EXT_PARAM_FILELUG_ACCOUNT);
            String computerId = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_ID);
            long lastModified = file.lastModified();
            RemoteFileUtils.updateRemoteHierarchicalModelLastModified(context, userId, Integer.valueOf(computerId), filePath, fileName, lastModified, file.length());
            RemoteFileUtils.updateRemoteHierarchicalModelLastModified(context, userId, Integer.valueOf(computerId), fileRealPath, savedFileName, lastModified, file.length());
        }

        return downloadedBytes;
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

    private void confirmDownload(boolean fromAnotherApp, String authToken, String lugServerId,
                                 String groupId, String transferKey, long rowId, String fileName,
                                 String status, long transferredSize, int notificationType) {
//        if ( Constants.DEBUG ) Log.d(TAG, "confirmDownload(), fileName=" + fileName);

        final Context context = getApplicationContext();
        int timeOut = context.getResources().getInteger(R.integer.sync_timeout_a);
        String locale = context.getResources().getConfiguration().locale.toString();
        int maxTry = 5;

        DownloadNotificationService.beginStartingService(context);

        for ( int i=0; i<maxTry; i++ ) {

            RequestFuture<String> future = RequestFuture.newFuture();
            RepositoryClient.getInstance().confirmDownloadFileFromDevice(authToken, lugServerId, transferKey, status, transferredSize, locale, future, future);
            String response = null;
            RepositoryErrorObject errorObject = null;

            try {
                response = future.get(timeOut, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                errorObject = MiscUtils.getErrorObject(context, e, null);
            }

            if ( errorObject != null ) {
                Log.e(TAG, "confirmDownload(), " + errorObject.getMessage());
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    Log.e(TAG, "confirmDownload(), Sleep... " + e.getMessage());
                }
                continue;
            }

            broadcastConfirmed(getActionDownloadBroadcast(), fromAnotherApp, notificationType, rowId, fileName, status);

            break;

        }
    }

    private void cancelDownloadFile(final boolean fromAnotherApp, String transferKey,
                                    final long rowId, final String fileName,
                                    String lugServerId, String authToken) {
//        if ( Constants.DEBUG ) Log.d(TAG, "cancelDownload(), rowId=" + rowId + ", fileName=" + fileName);

        RepositoryClient.getInstance().cancelDownloadFileFromDevice(
            authToken,
            lugServerId,
            transferKey,
            getResources().getConfiguration().locale.toString(),
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
//                    if ( Constants.DEBUG ) Log.d(TAG, "cancelDownloadFile().onResponse, transferKey=" + response.toString());
                    broadcastCancel(getActionDownloadBroadcast(), fromAnotherApp, rowId, fileName);
                }
            },
            new BaseResponseError(true, getApplicationContext()) {
                @Override
                protected void afterShowErrorMessage(VolleyError volleyError) {
                    if ( wakeLock !=null && wakeLock.isHeld() ) {
                        wakeLock.release();
                    }
                }
            }
        );
    }

}
