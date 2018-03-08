package com.filelug.android.provider.uploadhistory;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code upload_history} table.
 */
public class UploadHistoryContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return UploadHistoryColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable UploadHistorySelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public UploadHistoryContentValues putComputerGroup(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("computerGroup must not be null");
        mContentValues.put(UploadHistoryColumns.COMPUTER_GROUP, value);
        return this;
    }


    public UploadHistoryContentValues putComputerName(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("computerName must not be null");
        mContentValues.put(UploadHistoryColumns.COMPUTER_NAME, value);
        return this;
    }


    public UploadHistoryContentValues putFileSize(long value) {
        mContentValues.put(UploadHistoryColumns.FILE_SIZE, value);
        return this;
    }


    public UploadHistoryContentValues putEndTimestamp(long value) {
        mContentValues.put(UploadHistoryColumns.END_TIMESTAMP, value);
        return this;
    }


    public UploadHistoryContentValues putFileName(@Nullable String value) {
        mContentValues.put(UploadHistoryColumns.FILE_NAME, value);
        return this;
    }

    public UploadHistoryContentValues putFileNameNull() {
        mContentValues.putNull(UploadHistoryColumns.FILE_NAME);
        return this;
    }

    public UploadHistoryContentValues putUserId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("userId must not be null");
        mContentValues.put(UploadHistoryColumns.USER_ID, value);
        return this;
    }


    public UploadHistoryContentValues putComputerId(int value) {
        mContentValues.put(UploadHistoryColumns.COMPUTER_ID, value);
        return this;
    }

}
