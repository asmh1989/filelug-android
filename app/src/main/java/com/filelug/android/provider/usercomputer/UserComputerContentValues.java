package com.filelug.android.provider.usercomputer;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code user_computer} table.
 */
public class UserComputerContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return UserComputerColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable UserComputerSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public UserComputerContentValues putComputerId(int value) {
        mContentValues.put(UserComputerColumns.COMPUTER_ID, value);
        return this;
    }


    public UserComputerContentValues putComputerName(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("computerName must not be null");
        mContentValues.put(UserComputerColumns.COMPUTER_NAME, value);
        return this;
    }


    public UserComputerContentValues putComputerGroup(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("computerGroup must not be null");
        mContentValues.put(UserComputerColumns.COMPUTER_GROUP, value);
        return this;
    }


    public UserComputerContentValues putComputerAdminId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("computerAdminId must not be null");
        mContentValues.put(UserComputerColumns.COMPUTER_ADMIN_ID, value);
        return this;
    }


    public UserComputerContentValues putUserId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("userId must not be null");
        mContentValues.put(UserComputerColumns.USER_ID, value);
        return this;
    }


    public UserComputerContentValues putUserComputerId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("userComputerId must not be null");
        mContentValues.put(UserComputerColumns.USER_COMPUTER_ID, value);
        return this;
    }


    public UserComputerContentValues putLugServerId(@Nullable String value) {
        mContentValues.put(UserComputerColumns.LUG_SERVER_ID, value);
        return this;
    }

    public UserComputerContentValues putLugServerIdNull() {
        mContentValues.putNull(UserComputerColumns.LUG_SERVER_ID);
        return this;
    }
}
