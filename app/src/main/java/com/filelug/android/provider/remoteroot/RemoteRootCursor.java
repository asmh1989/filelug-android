package com.filelug.android.provider.remoteroot;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.filelug.android.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code remote_root} table.
 */
public class RemoteRootCursor extends AbstractCursor implements RemoteRootModel {
    public RemoteRootCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(RemoteRootColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code label} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getLabel() {
        String res = getStringOrNull(RemoteRootColumns.LABEL);
        if (res == null)
            throw new NullPointerException("The value of 'label' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code path} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getPath() {
        String res = getStringOrNull(RemoteRootColumns.PATH);
        if (res == null)
            throw new NullPointerException("The value of 'path' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code real_path} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getRealPath() {
        String res = getStringOrNull(RemoteRootColumns.REAL_PATH);
        if (res == null)
            throw new NullPointerException("The value of 'real_path' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code type} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public RemoteRootType getType() {
        Integer intValue = getIntegerOrNull(RemoteRootColumns.TYPE);
        if (intValue == null)
            throw new NullPointerException("The value of 'type' in the database was null, which is not allowed according to the model definition");
        return RemoteRootType.values()[intValue];
    }

    /**
     * Get the {@code user_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getUserId() {
        String res = getStringOrNull(RemoteRootColumns.USER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'user_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code computer_id} value.
     */
    public int getComputerId() {
        Integer res = getIntegerOrNull(RemoteRootColumns.COMPUTER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'computer_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
