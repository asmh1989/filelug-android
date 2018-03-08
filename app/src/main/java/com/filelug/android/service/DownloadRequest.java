package com.filelug.android.service;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent Chang on 2015/10/7.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class DownloadRequest {

    private NotificationConfig notificationConfig;
    private final Context context;
    private String customUserAgent;
    private final String userId;
    private final int computerId;
    private final String groupId;
    private final String transferKey;
    private final String lugServerId;
    private final String authToken;
    private final int notificationType;
    private final boolean fromAnotherApp;
    private FileToDownload fileToDownload;
    private final ArrayList<NameValue> headers;
    private final ArrayList<NameValue> parameters;

    /**
     * Creates a new download request.
     *
     * @param context application context
     * @param userId User ID
     * @param computerId Computer ID
     * @param groupId Group ID
     * @param transferKey unique ID to assign to this download request. It's used in the broadcast receiver when receiving updates.
     * @param lugServerId Lug-Server ID
     * @param authToken Authetication Token
     * @param notificationType Notification Type
     * @param fromAnotherApp From Another App
     */
    public DownloadRequest(final Context context, final String userId, final int computerId, final String groupId, final String transferKey, final String lugServerId, final String authToken, final int notificationType, final boolean fromAnotherApp) {
        this.context = context;
        this.userId = userId;
        this.computerId = computerId;
        this.groupId = groupId;
        this.transferKey = transferKey;
        this.lugServerId = lugServerId;
        this.authToken = authToken;
        this.notificationType = notificationType;
        this.fromAnotherApp = fromAnotherApp;
        notificationConfig = null;
        fileToDownload = null;
        headers = new ArrayList<NameValue>();
        parameters = new ArrayList<NameValue>();
    }

    /**
     * Sets custom notification configuration.
     *
     * @param fileName Download file name
     */
    public void setNotificationConfig(final String fileName) {
        notificationConfig = new NotificationConfig(fileName);
    }

    /**
     * Adds a file to this download request.
     *
     * @param filePath Parent path to the file that you want to download
     * @param fileName File name seen by the server side script
     * @param fullName Absolute path to the file that you want to download
     * @param fileRealPath Parent real path to the file that you want to download
     * @param fileRealName File real name seen by the server side script
     * @param realFullName Absolute real path to the file that you want to download
     * @param fileSize Content length of the file
     * @param lastModified Last modified date of the file
     * @param localDir Save to directory
     * @param isResume is resume to upload file
     * @param cacheFileSize Size of the cache file
     */
    public void addFileToDownload(final String filePath, final String fileName, final String fullName, final String fileRealPath, final String fileRealName, final String realFullName, final long fileSize, final String lastModified, final String localDir, boolean isResume, long cacheFileSize) {
        fileToDownload = new FileToDownload(filePath, fileName, fullName, fileRealPath, fileRealName, realFullName, fileSize, lastModified, localDir, isResume, cacheFileSize);
    }

    /**
     * Adds a header to this download request.
     *
     * @param headerName header name
     * @param headerValue header value
     */
    public void addHeader(final String headerName, final String headerValue) {
        headers.add(new NameValue(headerName, headerValue));
    }

    /**
     * Adds a parameter to this download request.
     *
     * @param paramName parameter name
     * @param paramValue parameter value
     */
    public void addParameter(final String paramName, final String paramValue) {
        parameters.add(new NameValue(paramName, paramValue));
    }

    /**
     * Adds a parameter with multiple values to this download request.
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
     * Adds a parameter with multiple values to this download request.
     *
     * @param paramName parameter name
     * @param list values
     */
    public void addArrayParameter(final String paramName, final List<String> list) {
        for (String value : list) {
            parameters.add(new NameValue(paramName, value));
        }
    }

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

    protected int getNotificationType() {
        return notificationType;
    }

    protected boolean isFromAnotherApp() {
        return fromAnotherApp;
    }

    /**
     * Gets the file that have to be downloaded.
     *
     * @return
     */
    protected FileToDownload getFileToDownload() {
        return fileToDownload;
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
     * Gets the download notification configuration.
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
     * Gets the custom user agent defined for this download request.
     *
     * @return string representing the user agent or null if it's not defined
     */
    public final String getCustomUserAgent() {
        return customUserAgent;
    }

    /**
     * Sets the custom user agent to use for this download request. Note! If you set the "User-Agent" header by using the
     * "addHeader" method, that setting will be overwritten by the value set with this method.
     *
     * @param customUserAgent custom user agent string
     */
    public final void setCustomUserAgent(String customUserAgent) {
        this.customUserAgent = customUserAgent;
    }

    /**
     * Sets the intent to be executed when the user taps on the download progress notification.
     *
     * @param intent
     */
    public final void setNotificationClickIntent(Intent intent) {
        notificationConfig.setClickIntent(intent);
    }

}
