package com.filelug.android.provider.uploadgroup;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code upload_group} table.
 */
public class UploadGroupCursor extends AbstractCursor implements UploadGroupModel {
    public UploadGroupCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(UploadGroupColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code group_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getGroupId() {
        String res = getStringOrNull(UploadGroupColumns.GROUP_ID);
        if (res == null)
            throw new NullPointerException("The value of 'group_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code from_another_app} value.
     */
    public boolean getFromAnotherApp() {
        Boolean res = getBooleanOrNull(UploadGroupColumns.FROM_ANOTHER_APP);
        if (res == null)
            throw new NullPointerException("The value of 'from_another_app' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code start_timestamp} value.
     */
    public long getStartTimestamp() {
        Long res = getLongOrNull(UploadGroupColumns.START_TIMESTAMP);
        if (res == null)
            throw new NullPointerException("The value of 'start_timestamp' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code upload_directory} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getUploadDirectory() {
        String res = getStringOrNull(UploadGroupColumns.UPLOAD_DIRECTORY);
        if (res == null)
            throw new NullPointerException("The value of 'upload_directory' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code subdirectory_type} value.
     */
    public int getSubdirectoryType() {
        Integer res = getIntegerOrNull(UploadGroupColumns.SUBDIRECTORY_TYPE);
        if (res == null)
            throw new NullPointerException("The value of 'subdirectory_type' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code subdirectory_value} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getSubdirectoryValue() {
        String res = getStringOrNull(UploadGroupColumns.SUBDIRECTORY_VALUE);
        return res;
    }

    /**
     * Get the {@code description_type} value.
     */
    public int getDescriptionType() {
        Integer res = getIntegerOrNull(UploadGroupColumns.DESCRIPTION_TYPE);
        if (res == null)
            throw new NullPointerException("The value of 'description_type' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code description_value} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getDescriptionValue() {
        String res = getStringOrNull(UploadGroupColumns.DESCRIPTION_VALUE);
        return res;
    }

    /**
     * Get the {@code notification_type} value.
     */
    public int getNotificationType() {
        Integer res = getIntegerOrNull(UploadGroupColumns.NOTIFICATION_TYPE);
        if (res == null)
            throw new NullPointerException("The value of 'notification_type' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code user_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getUserId() {
        String res = getStringOrNull(UploadGroupColumns.USER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'user_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code computer_id} value.
     */
    public int getComputerId() {
        Integer res = getIntegerOrNull(UploadGroupColumns.COMPUTER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'computer_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
