package com.filelug.android.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

class NotificationConfig implements Parcelable {

    private static final String FILE_NAME = "File Name";

    private final String fileName;
    private Intent clickIntent;

   public NotificationConfig(final String fileName) throws IllegalArgumentException {

        if (fileName == null ) {
            throw new IllegalArgumentException("You can't provide null parameters");
        }

        this.fileName = fileName;
    }

    public final String getFileName() {
        return fileName;
    }

    public final PendingIntent getPendingIntent(Context context) {
        if (clickIntent == null) {
            return PendingIntent.getBroadcast(context, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return PendingIntent.getActivity(context, 1, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public final void setClickIntent(Intent clickIntent) {
        this.clickIntent = clickIntent;
    }

    // This is used to regenerate the object.
    // All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<NotificationConfig> CREATOR = new Creator<NotificationConfig>() {
        @Override
        public NotificationConfig createFromParcel(final Parcel in) {
            return new NotificationConfig(in);
        }

        @Override
        public NotificationConfig[] newArray(final int size) {
            return new NotificationConfig[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int arg1) {
        parcel.writeString(fileName);
        parcel.writeParcelable(clickIntent, 0);
    }

    private NotificationConfig(Parcel in) {
        fileName = in.readString();
        clickIntent = in.readParcelable(Intent.class.getClassLoader());
    }
}
/*
class NotificationConfig implements Parcelable {

    private static final int UPLOADING_ICON_RESOURCE_ID = R.drawable.animation_upload;
    private static final int UPLOADED_ICON_RESOURCE_ID = R.drawable.notification_ic_uploading0;
    private static final String UPLOAD_TITLE = "File Upload";
    private static final String UPLOAD_MESSAGE = "uploading in progress";
    private static final String UPLOAD_COMPLETED = "upload completed successfully!";
    private static final String UPLOAD_ERROR = "error during upload";

    private static final int DOWNLOADING_ICON_RESOURCE_ID = R.drawable.animation_download;
    private static final int DOWNLOADED_ICON_RESOURCE_ID = R.drawable.notification_ic_downloading0;
    private static final String DOWNLOAD_TITLE = "File Download";
    private static final String DOWNLOAD_MESSAGE = "downloading in progress";
    private static final String DOWNLOAD_COMPLETED = "download completed successfully!";
    private static final String DOWNLOAD_ERROR = "error during download";

    private final String title;
    private final int messageIconResourceID;
    private final String message;
    private final int completedIconResourceID;
    private final String completed;
    private final int errorIconResourceID;
    private final String error;
    private final boolean autoClearOnSuccess;
    private Intent clickIntent;

    public static NotificationConfig UPLOAD = new NotificationConfig(
            UPLOAD_TITLE,
            UPLOADING_ICON_RESOURCE_ID,
            UPLOAD_MESSAGE,
            UPLOADED_ICON_RESOURCE_ID,
            UPLOAD_COMPLETED,
            UPLOADED_ICON_RESOURCE_ID,
            UPLOAD_ERROR,
            false
    );

    public static NotificationConfig DOWNLOAD = new NotificationConfig(
            DOWNLOAD_TITLE,
            DOWNLOADING_ICON_RESOURCE_ID,
            DOWNLOAD_MESSAGE,
            DOWNLOADED_ICON_RESOURCE_ID,
            DOWNLOAD_COMPLETED,
            DOWNLOADED_ICON_RESOURCE_ID,
            DOWNLOAD_ERROR,
            false
    );

    public NotificationConfig( final String title,
                               final int messageIconResourceID,
                               final String message,
                               final int completedIconResourceID,
                               final String completed,
                               final int errorIconResourceID,
                               final String error,
                               final boolean autoClearOnSuccess) throws IllegalArgumentException {

        if (title == null || message == null || completed == null || error == null) {
            throw new IllegalArgumentException("You can't provide null parameters");
        }

        this.title = title;
        this.messageIconResourceID = messageIconResourceID;
        this.message = message;
        this.completedIconResourceID = completedIconResourceID;
        this.completed = completed;
        this.errorIconResourceID = errorIconResourceID;
        this.error = error;
        this.autoClearOnSuccess = autoClearOnSuccess;
    }

    public final String getTitle() {
        return title;
    }

    public final int getMessageIconResourceID() {
        return messageIconResourceID;
    }

    public final String getMessage() {
        return message;
    }

    public final int getCompletedIconResourceID() {
        return completedIconResourceID;
    }

    public final String getCompleted() {
        return completed;
    }

    public final int getErrorIconResourceID() {
        return errorIconResourceID;
    }

    public final String getError() {
        return error;
    }

    public final boolean isAutoClearOnSuccess() {
        return autoClearOnSuccess;
    }

    public final PendingIntent getPendingIntent(Context context) {
        if (clickIntent == null) {
            return PendingIntent.getBroadcast(context, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return PendingIntent.getActivity(context, 1, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public final void setClickIntent(Intent clickIntent) {
        this.clickIntent = clickIntent;
    }

    // This is used to regenerate the object.
    // All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<NotificationConfig> CREATOR = new Creator<NotificationConfig>() {
        @Override
        public NotificationConfig createFromParcel(final Parcel in) {
            return new NotificationConfig(in);
        }

        @Override
        public NotificationConfig[] newArray(final int size) {
            return new NotificationConfig[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int arg1) {
        parcel.writeString(title);
        parcel.writeInt(messageIconResourceID);
        parcel.writeString(message);
        parcel.writeInt(completedIconResourceID);
        parcel.writeString(completed);
        parcel.writeInt(errorIconResourceID);
        parcel.writeString(error);
        parcel.writeByte((byte) (autoClearOnSuccess ? 1 : 0));
        parcel.writeParcelable(clickIntent, 0);
    }

    private NotificationConfig(Parcel in) {
        title = in.readString();
        messageIconResourceID = in.readInt();
        message = in.readString();
        completedIconResourceID = in.readInt();
        completed = in.readString();
        errorIconResourceID = in.readInt();
        error = in.readString();
        autoClearOnSuccess = in.readByte() == 1;
        clickIntent = in.readParcelable(Intent.class.getClassLoader());
    }
}
*/