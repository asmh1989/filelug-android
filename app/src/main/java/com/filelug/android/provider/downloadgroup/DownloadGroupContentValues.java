package com.filelug.android.provider.downloadgroup;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code download_group} table.
 */
public class DownloadGroupContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return DownloadGroupColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable DownloadGroupSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public DownloadGroupContentValues putGroupId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("groupId must not be null");
        mContentValues.put(DownloadGroupColumns.GROUP_ID, value);
        return this;
    }

    public DownloadGroupContentValues putFromAnotherApp(boolean value) {
        mContentValues.put(DownloadGroupColumns.FROM_ANOTHER_APP, value);
        return this;
    }

    public DownloadGroupContentValues putStartTimestamp(long value) {
        mContentValues.put(DownloadGroupColumns.START_TIMESTAMP, value);
        return this;
    }


    public DownloadGroupContentValues putLocalPath(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("localPath must not be null");
        mContentValues.put(DownloadGroupColumns.LOCAL_PATH, value);
        return this;
    }


    public DownloadGroupContentValues putSubdirectoryType(int value) {
        mContentValues.put(DownloadGroupColumns.SUBDIRECTORY_TYPE, value);
        return this;
    }


    public DownloadGroupContentValues putSubdirectoryValue(@Nullable String value) {
        mContentValues.put(DownloadGroupColumns.SUBDIRECTORY_VALUE, value);
        return this;
    }

    public DownloadGroupContentValues putSubdirectoryValueNull() {
        mContentValues.putNull(DownloadGroupColumns.SUBDIRECTORY_VALUE);
        return this;
    }

    public DownloadGroupContentValues putDescriptionType(int value) {
        mContentValues.put(DownloadGroupColumns.DESCRIPTION_TYPE, value);
        return this;
    }


    public DownloadGroupContentValues putDescriptionValue(@Nullable String value) {
        mContentValues.put(DownloadGroupColumns.DESCRIPTION_VALUE, value);
        return this;
    }

    public DownloadGroupContentValues putDescriptionValueNull() {
        mContentValues.putNull(DownloadGroupColumns.DESCRIPTION_VALUE);
        return this;
    }

    public DownloadGroupContentValues putNotificationType(int value) {
        mContentValues.put(DownloadGroupColumns.NOTIFICATION_TYPE, value);
        return this;
    }


    public DownloadGroupContentValues putUserId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("userId must not be null");
        mContentValues.put(DownloadGroupColumns.USER_ID, value);
        return this;
    }


    public DownloadGroupContentValues putComputerId(int value) {
        mContentValues.put(DownloadGroupColumns.COMPUTER_ID, value);
        return this;
    }

}
