package com.filelug.android.provider.downloadhistory;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code download_history} table.
 */
public class DownloadHistoryCursor extends AbstractCursor implements DownloadHistoryModel {
    public DownloadHistoryCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(DownloadHistoryColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code computer_group} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getComputerGroup() {
        String res = getStringOrNull(DownloadHistoryColumns.COMPUTER_GROUP);
        if (res == null)
            throw new NullPointerException("The value of 'computer_group' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code computer_name} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getComputerName() {
        String res = getStringOrNull(DownloadHistoryColumns.COMPUTER_NAME);
        if (res == null)
            throw new NullPointerException("The value of 'computer_name' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code file_size} value.
     */
    public long getFileSize() {
        Long res = getLongOrNull(DownloadHistoryColumns.FILE_SIZE);
        if (res == null)
            throw new NullPointerException("The value of 'file_size' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code end_timestamp} value.
     */
    public long getEndTimestamp() {
        Long res = getLongOrNull(DownloadHistoryColumns.END_TIMESTAMP);
        if (res == null)
            throw new NullPointerException("The value of 'end_timestamp' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code file_name} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getFileName() {
        String res = getStringOrNull(DownloadHistoryColumns.FILE_NAME);
        return res;
    }

    /**
     * Get the {@code user_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getUserId() {
        String res = getStringOrNull(DownloadHistoryColumns.USER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'user_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code computer_id} value.
     */
    public int getComputerId() {
        Integer res = getIntegerOrNull(DownloadHistoryColumns.COMPUTER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'computer_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
