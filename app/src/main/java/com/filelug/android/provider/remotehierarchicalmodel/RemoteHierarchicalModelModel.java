package com.filelug.android.provider.remotehierarchicalmodel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.BaseModel;

/**
 * Data model for the {@code remote_hierarchical_model} table.
 */
public interface RemoteHierarchicalModelModel extends BaseModel {

    /**
     * Get the {@code symlink} value.
     */
    boolean getSymlink();

    /**
     * Get the {@code parent} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getParent();

    /**
     * Get the {@code name} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getName();

    /**
     * Get the {@code readable} value.
     */
    boolean getReadable();

    /**
     * Get the {@code writable} value.
     */
    boolean getWritable();

    /**
     * Get the {@code hidden} value.
     */
    boolean getHidden();

    /**
     * Get the {@code last_modified} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getLastModified();

    /**
     * Get the {@code type} value.
     * Cannot be {@code null}.
     */
    @NonNull
    RemoteObjectType getType();

    /**
     * Get the {@code content_type} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getContentType();

    /**
     * Get the {@code size} value.
     */
    long getSize();

    /**
     * Get the {@code real_parent} value.
     * Can be {@code null}.
     */
    @Nullable
    String getRealParent();

    /**
     * Get the {@code real_name} value.
     * Can be {@code null}.
     */
    @Nullable
    String getRealName();

    /**
     * Get the {@code local_last_modified} value.
     * Can be {@code null}.
     */
    @Nullable
    Long getLocalLastModified();

    /**
     * Get the {@code local_size} value.
     * Can be {@code null}.
     */
    @Nullable
    Long getLocalSize();

    /**
     * Get the {@code local_last_access} value.
     * Can be {@code null}.
     */
    @Nullable
    Long getLocalLastAccess();

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
