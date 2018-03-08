package com.filelug.android.provider.remotehierarchicalmodel;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code remote_hierarchical_model} table.
 */
public class RemoteHierarchicalModelContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return RemoteHierarchicalModelColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable RemoteHierarchicalModelSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public RemoteHierarchicalModelContentValues putSymlink(boolean value) {
        mContentValues.put(RemoteHierarchicalModelColumns.SYMLINK, value);
        return this;
    }


    public RemoteHierarchicalModelContentValues putParent(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("parent must not be null");
        mContentValues.put(RemoteHierarchicalModelColumns.PARENT, value);
        return this;
    }


    public RemoteHierarchicalModelContentValues putName(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("name must not be null");
        mContentValues.put(RemoteHierarchicalModelColumns.NAME, value);
        return this;
    }


    public RemoteHierarchicalModelContentValues putReadable(boolean value) {
        mContentValues.put(RemoteHierarchicalModelColumns.READABLE, value);
        return this;
    }


    public RemoteHierarchicalModelContentValues putWritable(boolean value) {
        mContentValues.put(RemoteHierarchicalModelColumns.WRITABLE, value);
        return this;
    }


    public RemoteHierarchicalModelContentValues putHidden(boolean value) {
        mContentValues.put(RemoteHierarchicalModelColumns.HIDDEN, value);
        return this;
    }


    public RemoteHierarchicalModelContentValues putLastModified(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("lastModified must not be null");
        mContentValues.put(RemoteHierarchicalModelColumns.LAST_MODIFIED, value);
        return this;
    }


    public RemoteHierarchicalModelContentValues putType(@NonNull RemoteObjectType value) {
        if (value == null) throw new IllegalArgumentException("type must not be null");
        mContentValues.put(RemoteHierarchicalModelColumns.TYPE, value.ordinal());
        return this;
    }


    public RemoteHierarchicalModelContentValues putContentType(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("contentType must not be null");
        mContentValues.put(RemoteHierarchicalModelColumns.CONTENT_TYPE, value);
        return this;
    }


    public RemoteHierarchicalModelContentValues putSize(long value) {
        mContentValues.put(RemoteHierarchicalModelColumns.SIZE, value);
        return this;
    }


    public RemoteHierarchicalModelContentValues putRealParent(@Nullable String value) {
        mContentValues.put(RemoteHierarchicalModelColumns.REAL_PARENT, value);
        return this;
    }

    public RemoteHierarchicalModelContentValues putRealParentNull() {
        mContentValues.putNull(RemoteHierarchicalModelColumns.REAL_PARENT);
        return this;
    }

    public RemoteHierarchicalModelContentValues putRealName(@Nullable String value) {
        mContentValues.put(RemoteHierarchicalModelColumns.REAL_NAME, value);
        return this;
    }

    public RemoteHierarchicalModelContentValues putRealNameNull() {
        mContentValues.putNull(RemoteHierarchicalModelColumns.REAL_NAME);
        return this;
    }


    public RemoteHierarchicalModelContentValues putLocalLastModified(@Nullable Long value) {
        mContentValues.put(RemoteHierarchicalModelColumns.LOCAL_LAST_MODIFIED, value);
        return this;
    }

    public RemoteHierarchicalModelContentValues putLocalLastModifiedNull() {
        mContentValues.putNull(RemoteHierarchicalModelColumns.LOCAL_LAST_MODIFIED);
        return this;
    }


    public RemoteHierarchicalModelContentValues putLocalSize(@Nullable Long value) {
        mContentValues.put(RemoteHierarchicalModelColumns.LOCAL_SIZE, value);
        return this;
    }

    public RemoteHierarchicalModelContentValues putLocalSizeNull() {
        mContentValues.putNull(RemoteHierarchicalModelColumns.LOCAL_SIZE);
        return this;
    }


    public RemoteHierarchicalModelContentValues putLocalLastAccess(@Nullable Long value) {
        mContentValues.put(RemoteHierarchicalModelColumns.LOCAL_LAST_ACCESS, value);
        return this;
    }

    public RemoteHierarchicalModelContentValues putLocalLastAccessNull() {
        mContentValues.putNull(RemoteHierarchicalModelColumns.LOCAL_LAST_ACCESS);
        return this;
    }


    public RemoteHierarchicalModelContentValues putUserId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("userId must not be null");
        mContentValues.put(RemoteHierarchicalModelColumns.USER_ID, value);
        return this;
    }


    public RemoteHierarchicalModelContentValues putComputerId(int value) {
        mContentValues.put(RemoteHierarchicalModelColumns.COMPUTER_ID, value);
        return this;
    }

}
