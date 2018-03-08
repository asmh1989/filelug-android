package com.filelug.android.provider.downloadhistory;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.BaseModel;

/**
 * Data model for the {@code download_history} table.
 */
public interface DownloadHistoryModel extends BaseModel {

    /**
     * Get the {@code computer_group} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getComputerGroup();

    /**
     * Get the {@code computer_name} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getComputerName();

    /**
     * Get the {@code file_size} value.
     */
    long getFileSize();

    /**
     * Get the {@code end_timestamp} value.
     */
    long getEndTimestamp();

    /**
     * Get the {@code file_name} value.
     * Can be {@code null}.
     */
    @Nullable
    String getFileName();

    /**
     * Get the {@code user_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getUserId();

    /**
     * Get the {@code computer_id} value.
     */
    int getComputerId();
}
