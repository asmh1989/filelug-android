package com.filelug.android.provider.usercomputer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.filelug.android.provider.base.AbstractSelection;

/**
 * Selection for the {@code user_computer} table.
 */
public class UserComputerSelection extends AbstractSelection<UserComputerSelection> {
    @Override
    protected Uri baseUri() {
        return UserComputerColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code UserComputerCursor} object, which is positioned before the first entry, or null.
     */
    public UserComputerCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new UserComputerCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null)}.
     */
    public UserComputerCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null)}.
     */
    public UserComputerCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public UserComputerSelection id(long... value) {
        addEquals(UserComputerColumns.TABLE_NAME + "." + UserComputerColumns._ID, toObjectArray(value));
        return this;
    }

    public UserComputerSelection computerId(int... value) {
        addEquals(UserComputerColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public UserComputerSelection computerIdNot(int... value) {
        addNotEquals(UserComputerColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public UserComputerSelection computerIdGt(int value) {
        addGreaterThan(UserComputerColumns.COMPUTER_ID, value);
        return this;
    }

    public UserComputerSelection computerIdGtEq(int value) {
        addGreaterThanOrEquals(UserComputerColumns.COMPUTER_ID, value);
        return this;
    }

    public UserComputerSelection computerIdLt(int value) {
        addLessThan(UserComputerColumns.COMPUTER_ID, value);
        return this;
    }

    public UserComputerSelection computerIdLtEq(int value) {
        addLessThanOrEquals(UserComputerColumns.COMPUTER_ID, value);
        return this;
    }

    public UserComputerSelection computerName(String... value) {
        addEquals(UserComputerColumns.COMPUTER_NAME, value);
        return this;
    }

    public UserComputerSelection computerNameNot(String... value) {
        addNotEquals(UserComputerColumns.COMPUTER_NAME, value);
        return this;
    }

    public UserComputerSelection computerNameLike(String... value) {
        addLike(UserComputerColumns.COMPUTER_NAME, value);
        return this;
    }

    public UserComputerSelection computerNameContains(String... value) {
        addContains(UserComputerColumns.COMPUTER_NAME, value);
        return this;
    }

    public UserComputerSelection computerNameStartsWith(String... value) {
        addStartsWith(UserComputerColumns.COMPUTER_NAME, value);
        return this;
    }

    public UserComputerSelection computerNameEndsWith(String... value) {
        addEndsWith(UserComputerColumns.COMPUTER_NAME, value);
        return this;
    }

    public UserComputerSelection computerGroup(String... value) {
        addEquals(UserComputerColumns.COMPUTER_GROUP, value);
        return this;
    }

    public UserComputerSelection computerGroupNot(String... value) {
        addNotEquals(UserComputerColumns.COMPUTER_GROUP, value);
        return this;
    }

    public UserComputerSelection computerGroupLike(String... value) {
        addLike(UserComputerColumns.COMPUTER_GROUP, value);
        return this;
    }

    public UserComputerSelection computerGroupContains(String... value) {
        addContains(UserComputerColumns.COMPUTER_GROUP, value);
        return this;
    }

    public UserComputerSelection computerGroupStartsWith(String... value) {
        addStartsWith(UserComputerColumns.COMPUTER_GROUP, value);
        return this;
    }

    public UserComputerSelection computerGroupEndsWith(String... value) {
        addEndsWith(UserComputerColumns.COMPUTER_GROUP, value);
        return this;
    }

    public UserComputerSelection computerAdminId(String... value) {
        addEquals(UserComputerColumns.COMPUTER_ADMIN_ID, value);
        return this;
    }

    public UserComputerSelection computerAdminIdNot(String... value) {
        addNotEquals(UserComputerColumns.COMPUTER_ADMIN_ID, value);
        return this;
    }

    public UserComputerSelection computerAdminIdLike(String... value) {
        addLike(UserComputerColumns.COMPUTER_ADMIN_ID, value);
        return this;
    }

    public UserComputerSelection computerAdminIdContains(String... value) {
        addContains(UserComputerColumns.COMPUTER_ADMIN_ID, value);
        return this;
    }

    public UserComputerSelection computerAdminIdStartsWith(String... value) {
        addStartsWith(UserComputerColumns.COMPUTER_ADMIN_ID, value);
        return this;
    }

    public UserComputerSelection computerAdminIdEndsWith(String... value) {
        addEndsWith(UserComputerColumns.COMPUTER_ADMIN_ID, value);
        return this;
    }

    public UserComputerSelection userId(String... value) {
        addEquals(UserComputerColumns.USER_ID, value);
        return this;
    }

    public UserComputerSelection userIdNot(String... value) {
        addNotEquals(UserComputerColumns.USER_ID, value);
        return this;
    }

    public UserComputerSelection userIdLike(String... value) {
        addLike(UserComputerColumns.USER_ID, value);
        return this;
    }

    public UserComputerSelection userIdContains(String... value) {
        addContains(UserComputerColumns.USER_ID, value);
        return this;
    }

    public UserComputerSelection userIdStartsWith(String... value) {
        addStartsWith(UserComputerColumns.USER_ID, value);
        return this;
    }

    public UserComputerSelection userIdEndsWith(String... value) {
        addEndsWith(UserComputerColumns.USER_ID, value);
        return this;
    }

    public UserComputerSelection userComputerId(String... value) {
        addEquals(UserComputerColumns.USER_COMPUTER_ID, value);
        return this;
    }

    public UserComputerSelection userComputerIdNot(String... value) {
        addNotEquals(UserComputerColumns.USER_COMPUTER_ID, value);
        return this;
    }

    public UserComputerSelection userComputerIdLike(String... value) {
        addLike(UserComputerColumns.USER_COMPUTER_ID, value);
        return this;
    }

    public UserComputerSelection userComputerIdContains(String... value) {
        addContains(UserComputerColumns.USER_COMPUTER_ID, value);
        return this;
    }

    public UserComputerSelection userComputerIdStartsWith(String... value) {
        addStartsWith(UserComputerColumns.USER_COMPUTER_ID, value);
        return this;
    }

    public UserComputerSelection userComputerIdEndsWith(String... value) {
        addEndsWith(UserComputerColumns.USER_COMPUTER_ID, value);
        return this;
    }

    public UserComputerSelection lugServerId(String... value) {
        addEquals(UserComputerColumns.LUG_SERVER_ID, value);
        return this;
    }

    public UserComputerSelection lugServerIdNot(String... value) {
        addNotEquals(UserComputerColumns.LUG_SERVER_ID, value);
        return this;
    }

    public UserComputerSelection lugServerIdLike(String... value) {
        addLike(UserComputerColumns.LUG_SERVER_ID, value);
        return this;
    }

    public UserComputerSelection lugServerIdContains(String... value) {
        addContains(UserComputerColumns.LUG_SERVER_ID, value);
        return this;
    }

    public UserComputerSelection lugServerIdStartsWith(String... value) {
        addStartsWith(UserComputerColumns.LUG_SERVER_ID, value);
        return this;
    }

    public UserComputerSelection lugServerIdEndsWith(String... value) {
        addEndsWith(UserComputerColumns.LUG_SERVER_ID, value);
        return this;
    }
}
