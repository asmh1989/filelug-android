package com.filelug.android.provider.filetransfer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.BaseModel;

/**
 * Data model for the {@code file_transfer} table.
 */
public interface FileTransferModel extends BaseModel {

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
     * Get the {@code type} value.
     * Cannot be {@code null}.
     */
    @NonNull
    RemoteObjectType getType();

    /**
     * Get the {@code server_path} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getServerPath();

    /**
     * Get the {@code real_server_path} value.
     * Can be {@code null}.
     */
    @Nullable
    String getRealServerPath();

    /**
     * Get the {@code local_file_name} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getLocalFileName();

    /**
     * Get the {@code real_local_file_name} value.
     * Can be {@code null}.
     */
    @Nullable
    String getRealLocalFileName();

    /**
     * Get the {@code saved_file_name} value.
     * Can be {@code null}.
     */
    @Nullable
    String getSavedFileName();

    /**
     * Get the {@code file_in_cache} value.
     */
    boolean getFileInCache();

    /**
     * Get the {@code content_type} value.
     * Can be {@code null}.
     */
    @Nullable
    String getContentType();

    /**
     * Get the {@code last_modified} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getLastModified();

    /**
     * Get the {@code status} value.
     * Cannot be {@code null}.
     */
    @NonNull
    DownloadStatusType getStatus();

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
     * Get the {@code actions_after_download} value.
     * Can be {@code null}.
     */
    @Nullable
    String getActionsAfterDownload();

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
     * Get the {@code dg_local_path} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getDGLocalPath();

    /**
     * Get the {@code dg_from_another_app} value.
     */
    boolean getDGFromAnotherApp();

    /**
     * Get the {@code dg_notification_type} value.
     */
    int getDGNotificationType();

    /**
     * Get the {@code dg_start_timestamp} value.
     * Can be {@code null}.
     */
    @Nullable
    Long getDGStartTimestamp();
}
