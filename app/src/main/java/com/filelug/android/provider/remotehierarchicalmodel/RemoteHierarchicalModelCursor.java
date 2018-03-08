package com.filelug.android.provider.remotehierarchicalmodel;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code remote_hierarchical_model} table.
 */
public class RemoteHierarchicalModelCursor extends AbstractCursor implements RemoteHierarchicalModelModel {
    public RemoteHierarchicalModelCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(RemoteHierarchicalModelColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code symlink} value.
     */
    public boolean getSymlink() {
        Boolean res = getBooleanOrNull(RemoteHierarchicalModelColumns.SYMLINK);
        if (res == null)
            throw new NullPointerException("The value of 'symlink' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code parent} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getParent() {
        String res = getStringOrNull(RemoteHierarchicalModelColumns.PARENT);
        if (res == null)
            throw new NullPointerException("The value of 'parent' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code name} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getName() {
        String res = getStringOrNull(RemoteHierarchicalModelColumns.NAME);
        if (res == null)
            throw new NullPointerException("The value of 'name' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code readable} value.
     */
    public boolean getReadable() {
        Boolean res = getBooleanOrNull(RemoteHierarchicalModelColumns.READABLE);
        if (res == null)
            throw new NullPointerException("The value of 'readable' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code writable} value.
     */
    public boolean getWritable() {
        Boolean res = getBooleanOrNull(RemoteHierarchicalModelColumns.WRITABLE);
        if (res == null)
            throw new NullPointerException("The value of 'writable' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code hidden} value.
     */
    public boolean getHidden() {
        Boolean res = getBooleanOrNull(RemoteHierarchicalModelColumns.HIDDEN);
        if (res == null)
            throw new NullPointerException("The value of 'hidden' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code last_modified} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getLastModified() {
        String res = getStringOrNull(RemoteHierarchicalModelColumns.LAST_MODIFIED);
        if (res == null)
            throw new NullPointerException("The value of 'last_modified' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code type} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public RemoteObjectType getType() {
        Integer intValue = getIntegerOrNull(RemoteHierarchicalModelColumns.TYPE);
        if (intValue == null)
            throw new NullPointerException("The value of 'type' in the database was null, which is not allowed according to the model definition");
        return RemoteObjectType.values()[intValue];
    }

    /**
     * Get the {@code content_type} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getContentType() {
        String res = getStringOrNull(RemoteHierarchicalModelColumns.CONTENT_TYPE);
        if (res == null)
            throw new NullPointerException("The value of 'content_type' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code size} value.
     */
    public long getSize() {
        Long res = getLongOrNull(RemoteHierarchicalModelColumns.SIZE);
        if (res == null)
            throw new NullPointerException("The value of 'size' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code real_parent} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getRealParent() {
        String res = getStringOrNull(RemoteHierarchicalModelColumns.REAL_PARENT);
        return res;
    }

    /**
     * Get the {@code real_name} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getRealName() {
        String res = getStringOrNull(RemoteHierarchicalModelColumns.REAL_NAME);
        return res;
    }

    /**
     * Get the {@code local_last_modified} value.
     * Can be {@code null}.
     */
    @Nullable
    public Long getLocalLastModified() {
        Long res = getLongOrNull(RemoteHierarchicalModelColumns.LOCAL_LAST_MODIFIED);
        return res;
    }

    /**
     * Get the {@code local_size} value.
     * Can be {@code null}.
     */
    @Nullable
    public Long getLocalSize() {
        Long res = getLongOrNull(RemoteHierarchicalModelColumns.LOCAL_SIZE);
        return res;
    }

    /**
     * Get the {@code local_last_access} value.
     * Can be {@code null}.
     */
    @Nullable
    public Long getLocalLastAccess() {
        Long res = getLongOrNull(RemoteHierarchicalModelColumns.LOCAL_LAST_ACCESS);
        return res;
    }

    /**
     * Get the {@code user_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getUserId() {
        String res = getStringOrNull(RemoteHierarchicalModelColumns.USER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'user_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code computer_id} value.
     */
    public int getComputerId() {
        Integer res = getIntegerOrNull(RemoteHierarchicalModelColumns.COMPUTER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'computer_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
