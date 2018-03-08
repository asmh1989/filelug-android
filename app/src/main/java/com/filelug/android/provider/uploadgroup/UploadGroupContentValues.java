package com.filelug.android.provider.uploadgroup;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code upload_group} table.
 */
public class UploadGroupContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return UploadGroupColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable UploadGroupSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public UploadGroupContentValues putGroupId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("groupId must not be null");
        mContentValues.put(UploadGroupColumns.GROUP_ID, value);
        return this;
    }

    public UploadGroupContentValues putFromAnotherApp(boolean value) {
        mContentValues.put(UploadGroupColumns.FROM_ANOTHER_APP, value);
        return this;
    }


    public UploadGroupContentValues putStartTimestamp(long value) {
        mContentValues.put(UploadGroupColumns.START_TIMESTAMP, value);
        return this;
    }


    public UploadGroupContentValues putUploadDirectory(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("uploadDirectory must not be null");
        mContentValues.put(UploadGroupColumns.UPLOAD_DIRECTORY, value);
        return this;
    }


    public UploadGroupContentValues putSubdirectoryType(int value) {
        mContentValues.put(UploadGroupColumns.SUBDIRECTORY_TYPE, value);
        return this;
    }


    public UploadGroupContentValues putSubdirectoryValue(@Nullable String value) {
        mContentValues.put(UploadGroupColumns.SUBDIRECTORY_VALUE, value);
        return this;
    }

    public UploadGroupContentValues putSubdirectoryValueNull() {
        mContentValues.putNull(UploadGroupColumns.SUBDIRECTORY_VALUE);
        return this;
    }

    public UploadGroupContentValues putDescriptionType(int value) {
        mContentValues.put(UploadGroupColumns.DESCRIPTION_TYPE, value);
        return this;
    }


    public UploadGroupContentValues putDescriptionValue(@Nullable String value) {
        mContentValues.put(UploadGroupColumns.DESCRIPTION_VALUE, value);
        return this;
    }

    public UploadGroupContentValues putDescriptionValueNull() {
        mContentValues.putNull(UploadGroupColumns.DESCRIPTION_VALUE);
        return this;
    }

    public UploadGroupContentValues putNotificationType(int value) {
        mContentValues.put(UploadGroupColumns.NOTIFICATION_TYPE, value);
        return this;
    }


    public UploadGroupContentValues putUserId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("userId must not be null");
        mContentValues.put(UploadGroupColumns.USER_ID, value);
        return this;
    }


    public UploadGroupContentValues putComputerId(int value) {
        mContentValues.put(UploadGroupColumns.COMPUTER_ID, value);
        return this;
    }

}
