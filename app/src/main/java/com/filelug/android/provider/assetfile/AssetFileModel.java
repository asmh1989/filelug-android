package com.filelug.android.provider.assetfile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.BaseModel;

/**
 * Data model for the {@code asset_file} table.
 */
public interface AssetFileModel extends BaseModel {

    /**
     * Get the {@code group_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getGroupId();

    /**
     * Get the {@code transfer_key} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getTransferKey();

    /**
     * Get the {@code asset_url} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getAssetUrl();

    /**
     * Get the {@code server_file_name} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getServerFileName();

    /**
     * Get the {@code cache_file_name} value.
     * Can be {@code null}.
     */
    @Nullable
    String getCacheFileName();

    /**
     * Get the {@code content_type} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getContentType();

    /**
     * Get the {@code last_modified_timestamp} value.
     * Cannot be {@code null}.
     */
    @NonNull
    Long getLastModifiedTimestamp();

    /**
     * Get the {@code status} value.
     * Cannot be {@code null}.
     */
    @NonNull
    UploadStatusType getStatus();

    /**
     * Get the {@code start_timestamp} value.
     * Can be {@code null}.
     */
    @Nullable
    Long getStartTimestamp();

    /**
     * Get the {@code end_timestamp} value.
     * Can be {@code null}.
     */
    @Nullable
    Long getEndTimestamp();

    /**
     * Get the {@code total_size} value.
     * Can be {@code null}.
     */
    @Nullable
    Long getTotalSize();

    /**
     * Get the {@code transferred_size} value.
     * Can be {@code null}.
     */
    @Nullable
    Long getTransferredSize();

    /**
     * Get the {@code wait_to_confirm} value.
     */
    boolean getWaitToConfirm();

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

    /**
     * Get the {@code ug_from_another_app} value.
     */
    boolean getUGFromAnotherApp();

    /**
     * Get the {@code ug_start_timestamp} value.
     * Can be {@code null}.
     */
    @Nullable
    Long getUGStartTimestamp();
}
