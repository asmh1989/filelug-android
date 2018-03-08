package com.filelug.android.provider.downloadgroup;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.BaseModel;

/**
 * Data model for the {@code download_group} table.
 */
public interface DownloadGroupModel extends BaseModel {

    /**
     * Get the {@code group_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getGroupId();

    /**
     * Get the {@code from_another_app} value.
     */
    boolean getFromAnotherApp();

    /**
     * Get the {@code start_timestamp} value.
     */
    long getStartTimestamp();

    /**
     * Get the {@code local_path} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getLocalPath();

    /**
     * Get the {@code subdirectory_type} value.
     */
    int getSubdirectoryType();

    /**
     * Get the {@code subdirectory_value} value.
     * Can be {@code null}.
     */
    @Nullable
    String getSubdirectoryValue();

    /**
     * Get the {@code description_type} value.
     */
    int getDescriptionType();

    /**
     * Get the {@code description_value} value.
     * Can be {@code null}.
     */
    @Nullable
    String getDescriptionValue();

    /**
     * Get the {@code notification_type} value.
     */
    int getNotificationType();

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
