package com.filelug.android.provider.remoteroot;

import android.support.annotation.NonNull;

import com.filelug.android.provider.base.BaseModel;

/**
 * Data model for the {@code remote_root} table.
 */
public interface RemoteRootModel extends BaseModel {

    /**
     * Get the {@code label} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getLabel();

    /**
     * Get the {@code path} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getPath();

    /**
     * Get the {@code real_path} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getRealPath();

    /**
     * Get the {@code type} value.
     * Cannot be {@code null}.
     */
    @NonNull
    RemoteRootType getType();

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
