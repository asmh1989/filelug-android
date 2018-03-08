package com.filelug.android.provider.usercomputer;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code user_computer} table.
 */
public class UserComputerCursor extends AbstractCursor implements UserComputerModel {
    public UserComputerCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(UserComputerColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code computer_id} value.
     */
    public int getComputerId() {
        Integer res = getIntegerOrNull(UserComputerColumns.COMPUTER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'computer_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code computer_name} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getComputerName() {
        String res = getStringOrNull(UserComputerColumns.COMPUTER_NAME);
        if (res == null)
            throw new NullPointerException("The value of 'computer_name' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code computer_group} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getComputerGroup() {
        String res = getStringOrNull(UserComputerColumns.COMPUTER_GROUP);
        if (res == null)
            throw new NullPointerException("The value of 'computer_group' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code computer_admin_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getComputerAdminId() {
        String res = getStringOrNull(UserComputerColumns.COMPUTER_ADMIN_ID);
        if (res == null)
            throw new NullPointerException("The value of 'computer_admin_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code user_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getUserId() {
        String res = getStringOrNull(UserComputerColumns.USER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'user_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code user_computer_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getUserComputerId() {
        String res = getStringOrNull(UserComputerColumns.USER_COMPUTER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'user_computer_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code lug_server_id} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getLugServerId() {
        String res = getStringOrNull(UserComputerColumns.LUG_SERVER_ID);
        return res;
    }
}
