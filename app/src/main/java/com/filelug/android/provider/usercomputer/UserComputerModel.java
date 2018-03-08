package com.filelug.android.provider.usercomputer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.BaseModel;

/**
 * Data model for the {@code user_computer} table.
 */
public interface UserComputerModel extends BaseModel {

    /**
     * Get the {@code computer_id} value.
     */
    int getComputerId();

    /**
     * Get the {@code computer_name} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getComputerName();

    /**
     * Get the {@code computer_group} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getComputerGroup();

    /**
     * Get the {@code computer_admin_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getComputerAdminId();

    /**
     * Get the {@code user_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getUserId();

    /**
     * Get the {@code user_computer_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getUserComputerId();

    /**
     * Get the {@code lug_server_id} value.
     * Can be {@code null}.
     */
    @Nullable
    String getLugServerId();
}
