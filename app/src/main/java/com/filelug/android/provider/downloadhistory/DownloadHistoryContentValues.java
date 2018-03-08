package com.filelug.android.provider.downloadhistory;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code download_history} table.
 */
public class DownloadHistoryContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return DownloadHistoryColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable DownloadHistorySelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public DownloadHistoryContentValues putComputerGroup(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("computerGroup must not be null");
        mContentValues.put(DownloadHistoryColumns.COMPUTER_GROUP, value);
        return this;
    }


    public DownloadHistoryContentValues putComputerName(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("computerName must not be null");
        mContentValues.put(DownloadHistoryColumns.COMPUTER_NAME, value);
        return this;
    }


    public DownloadHistoryContentValues putFileSize(long value) {
        mContentValues.put(DownloadHistoryColumns.FILE_SIZE, value);
        return this;
    }


    public DownloadHistoryContentValues putEndTimestamp(long value) {
        mContentValues.put(DownloadHistoryColumns.END_TIMESTAMP, value);
        return this;
    }


    public DownloadHistoryContentValues putFileName(@Nullable String value) {
        mContentValues.put(DownloadHistoryColumns.FILE_NAME, value);
        return this;
    }

    public DownloadHistoryContentValues putFileNameNull() {
        mContentValues.putNull(DownloadHistoryColumns.FILE_NAME);
        return this;
    }

    public DownloadHistoryContentValues putUserId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("userId must not be null");
        mContentValues.put(DownloadHistoryColumns.USER_ID, value);
        return this;
    }


    public DownloadHistoryContentValues putComputerId(int value) {
        mContentValues.put(DownloadHistoryColumns.COMPUTER_ID, value);
        return this;
    }

}
