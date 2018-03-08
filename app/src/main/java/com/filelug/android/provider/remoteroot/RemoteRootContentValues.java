package com.filelug.android.provider.remoteroot;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code remote_root} table.
 */
public class RemoteRootContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return RemoteRootColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable RemoteRootSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public RemoteRootContentValues putLabel(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("label must not be null");
        mContentValues.put(RemoteRootColumns.LABEL, value);
        return this;
    }


    public RemoteRootContentValues putPath(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("path must not be null");
        mContentValues.put(RemoteRootColumns.PATH, value);
        return this;
    }


    public RemoteRootContentValues putRealPath(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("realPath must not be null");
        mContentValues.put(RemoteRootColumns.REAL_PATH, value);
        return this;
    }


    public RemoteRootContentValues putType(@NonNull RemoteRootType value) {
        if (value == null) throw new IllegalArgumentException("type must not be null");
        mContentValues.put(RemoteRootColumns.TYPE, value.ordinal());
        return this;
    }


    public RemoteRootContentValues putUserId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("userId must not be null");
        mContentValues.put(RemoteRootColumns.USER_ID, value);
        return this;
    }


    public RemoteRootContentValues putComputerId(int value) {
        mContentValues.put(RemoteRootColumns.COMPUTER_ID, value);
        return this;
    }

}
