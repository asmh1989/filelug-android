package com.filelug.android.service;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class UploadRequest {

    private NotificationConfig notificationConfig;
//    private String method = "POST";
    private final Context context;
    private String customUserAgent;
//    private int maxRetries;
    private final String userId;
    private final int computerId;
    private final String groupId;
    private final String transferKey;
    private final String lugServerId;
    private final String authToken;
    private final boolean fromAnotherApp;
    private FileToUpload fileToUpload;
    private final ArrayList<NameValue> headers;
    private final ArrayList<NameValue> parameters;

    /**
     * Creates a new upload request.
     * 
     * @param context application context
     * @param userId User ID
     * @param computerId Computer ID
     * @param groupId Upload group ID
     * @param transferKey unique ID to assign to this upload request. It's used in the broadcast receiver when receiving updates.
     * @param lugServerId Lug-Server ID
     * @param authToken Authetication Token
     * @param fromAnotherApp From Another App
     */
    public UploadRequest(final Context context, final String userId, final int computerId, final String groupId, final String transferKey, final String lugServerId, final String authToken, boolean fromAnotherApp) {
        this.context = context;
        this.userId = userId;
        this.groupId = groupId;
        this.computerId = computerId;
        this.transferKey = transferKey;
        this.lugServerId = lugServerId;
        this.authToken = authToken;
        this.fromAnotherApp = fromAnotherApp;
//        notificationConfig = NotificationConfig.UPLOAD;
        notificationConfig = null;
        fileToUpload = null;
        headers = new ArrayList<NameValue>();
        parameters = new ArrayList<NameValue>();
//        maxRetries = 0;
    }

    /**
     * Sets custom notification configuration.
     *
     * @param fileName Upload file name
     */
    public void setNotificationConfig(final String fileName) {
        notificationConfig = new NotificationConfig(fileName);
    }

    /**
     * Adds a file to this upload request.
     *
     * @param path Absolute path to the file that you want to upload
     * @param fileName File name seen by the server side script
     * @param cacheFileName Cache file name.
     * @param contentType Content type of the file. Set this to null if you don't want to set a content type.
     * @param fileSize File size.
     * @param isResume Is resume to upload file.
     * @param cacheFileSize Cache file size.
     */
    public void addFileToUpload(final String path, final String fileName, final String cacheFileName,
                                final String contentType, final long fileSize,
                                final boolean isResume, final long cacheFileSize) {
        fileToUpload = new FileToUpload(path, fileName, cacheFileName, contentType, fileSize, isResume, cacheFileSize);
    }

    /**
     * Adds a header to this upload request.
     * 
     * @param headerName header name
     * @param headerValue header value
     */
    public void addHeader(final String headerName, final String headerValue) {
        headers.add(new NameValue(headerName, headerValue));
    }

    /**
     * Adds a parameter to this upload request.
     * 
     * @param paramName parameter name
     * @param paramValue parameter value
     */
    public void addParameter(final String paramName, final String paramValue) {
        parameters.add(new NameValue(paramName, paramValue));
    }

    /**
     * Adds a parameter with multiple values to this upload request.
     * 
     * @param paramName parameter name
     * @param array values
     */
    public void addArrayParameter(final String paramName, final String... array) {
        for (String value : array) {
            parameters.add(new NameValue(paramName, value));
        }
    }

    /**
     * Adds a parameter with multiple values to this upload request.
     * 
     * @param paramName parameter name
     * @param list values
     */
    public void addArrayParameter(final String paramName, final List<String> list) {
        for (String value : list) {
            parameters.add(new NameValue(paramName, value));
        }
    }

//    /**
//     * Sets the HTTP method to use. By default it's set to POST.
//     *
//     * @param method new HTTP method to use
//     */
//    public void setMethod(final String method) {
//        if (method != null && method.length() > 0)
//            this.method = method;
//    }
//
//    /**
//     * Gets the HTTP method to use.
//     *
//     * @return
//     */
//    protected String getMethod() {
//        return method;
//    }

    protected String getUserId() {
        return userId;
    }

    protected int getComputerId() {
        return computerId;
    }

    protected String getGroupId() {
        return groupId;
    }

    protected String getTransferKey() {
        return transferKey;
    }

    protected String getLugServerId() {
        return lugServerId;
    }

    protected String getAuthToken() {
        return authToken;
    }

    protected boolean isFromAnotherApp() {
        return fromAnotherApp;
    }

    /**
     * Gets the file that have to be uploaded.
     *
     * @return
     */
    protected FileToUpload getFileToUpload() {
        return fileToUpload;
    }


    /**
     * Gets the list of the headers.
     * 
     * @return
     */
    protected ArrayList<NameValue> getHeaders() {
        return headers;
    }

    /**
     * Gets the list of the parameters.
     * 
     * @return
     */
    protected ArrayList<NameValue> getParameters() {
        return parameters;
    }

    /**
     * Gets the upload notification configuration.
     * 
     * @return
     */
    protected NotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    /**
     * Gets the application context.
     * 
     * @return
     */
    protected Context getContext() {
        return context;
    }

    /**
     * Gets the custom user agent defined for this upload request.
     * 
     * @return string representing the user agent or null if it's not defined
     */
    public final String getCustomUserAgent() {
        return customUserAgent;
    }

    /**
     * Sets the custom user agent to use for this upload request. Note! If you set the "User-Agent" header by using the
     * "addHeader" method, that setting will be overwritten by the value set with this method.
     * 
     * @param customUserAgent custom user agent string
     */
    public final void setCustomUserAgent(String customUserAgent) {
        this.customUserAgent = customUserAgent;
    }

    /**
     * Sets the intent to be executed when the user taps on the upload progress notification.
     * 
     * @param intent
     */
    public final void setNotificationClickIntent(Intent intent) {
        notificationConfig.setClickIntent(intent);
    }

//    /**
//     * Get the maximum number of retries that the library will do if an error occurs, before returning an error.
//     *
//     * @return
//     */
//    public final int getMaxRetries() {
//        return maxRetries;
//    }

//    /**
//     * Sets the maximum number of retries that the library will do if an error occurs, before returning an error.
//     *
//     * @param maxRetries
//     */
//    public final void setMaxRetries(int maxRetries) {
//        if (maxRetries < 0)
//            this.maxRetries = 0;
//        else
//            this.maxRetries = maxRetries;
//    }

}
