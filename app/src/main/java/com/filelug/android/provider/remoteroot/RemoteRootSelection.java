package com.filelug.android.provider.remoteroot;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.filelug.android.provider.base.AbstractSelection;

/**
 * Selection for the {@code remote_root} table.
 */
public class RemoteRootSelection extends AbstractSelection<RemoteRootSelection> {
    @Override
    protected Uri baseUri() {
        return RemoteRootColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code RemoteRootCursor} object, which is positioned before the first entry, or null.
     */
    public RemoteRootCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new RemoteRootCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null)}.
     */
    public RemoteRootCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null)}.
     */
    public RemoteRootCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public RemoteRootSelection id(long... value) {
        addEquals(RemoteRootColumns.TABLE_NAME + "." + RemoteRootColumns._ID, toObjectArray(value));
        return this;
    }

    public RemoteRootSelection label(String... value) {
        addEquals(RemoteRootColumns.LABEL, value);
        return this;
    }

    public RemoteRootSelection labelNot(String... value) {
        addNotEquals(RemoteRootColumns.LABEL, value);
        return this;
    }

    public RemoteRootSelection labelLike(String... value) {
        addLike(RemoteRootColumns.LABEL, value);
        return this;
    }

    public RemoteRootSelection labelContains(String... value) {
        addContains(RemoteRootColumns.LABEL, value);
        return this;
    }

    public RemoteRootSelection labelStartsWith(String... value) {
        addStartsWith(RemoteRootColumns.LABEL, value);
        return this;
    }

    public RemoteRootSelection labelEndsWith(String... value) {
        addEndsWith(RemoteRootColumns.LABEL, value);
        return this;
    }

    public RemoteRootSelection path(String... value) {
        addEquals(RemoteRootColumns.PATH, value);
        return this;
    }

    public RemoteRootSelection pathNot(String... value) {
        addNotEquals(RemoteRootColumns.PATH, value);
        return this;
    }

    public RemoteRootSelection pathLike(String... value) {
        addLike(RemoteRootColumns.PATH, value);
        return this;
    }

    public RemoteRootSelection pathContains(String... value) {
        addContains(RemoteRootColumns.PATH, value);
        return this;
    }

    public RemoteRootSelection pathStartsWith(String... value) {
        addStartsWith(RemoteRootColumns.PATH, value);
        return this;
    }

    public RemoteRootSelection pathEndsWith(String... value) {
        addEndsWith(RemoteRootColumns.PATH, value);
        return this;
    }

    public RemoteRootSelection realPath(String... value) {
        addEquals(RemoteRootColumns.REAL_PATH, value);
        return this;
    }

    public RemoteRootSelection realPathNot(String... value) {
        addNotEquals(RemoteRootColumns.REAL_PATH, value);
        return this;
    }

    public RemoteRootSelection realPathLike(String... value) {
        addLike(RemoteRootColumns.REAL_PATH, value);
        return this;
    }

    public RemoteRootSelection realPathContains(String... value) {
        addContains(RemoteRootColumns.REAL_PATH, value);
        return this;
    }

    public RemoteRootSelection realPathStartsWith(String... value) {
        addStartsWith(RemoteRootColumns.REAL_PATH, value);
        return this;
    }

    public RemoteRootSelection realPathEndsWith(String... value) {
        addEndsWith(RemoteRootColumns.REAL_PATH, value);
        return this;
    }

    public RemoteRootSelection type(RemoteRootType... value) {
        addEquals(RemoteRootColumns.TYPE, value);
        return this;
    }

    public RemoteRootSelection typeNot(RemoteRootType... value) {
        addNotEquals(RemoteRootColumns.TYPE, value);
        return this;
    }


    public RemoteRootSelection userId(String... value) {
        addEquals(RemoteRootColumns.USER_ID, value);
        return this;
    }

    public RemoteRootSelection userIdNot(String... value) {
        addNotEquals(RemoteRootColumns.USER_ID, value);
        return this;
    }

    public RemoteRootSelection userIdLike(String... value) {
        addLike(RemoteRootColumns.USER_ID, value);
        return this;
    }

    public RemoteRootSelection userIdContains(String... value) {
        addContains(RemoteRootColumns.USER_ID, value);
        return this;
    }

    public RemoteRootSelection userIdStartsWith(String... value) {
        addStartsWith(RemoteRootColumns.USER_ID, value);
        return this;
    }

    public RemoteRootSelection userIdEndsWith(String... value) {
        addEndsWith(RemoteRootColumns.USER_ID, value);
        return this;
    }

    public RemoteRootSelection computerId(int... value) {
        addEquals(RemoteRootColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public RemoteRootSelection computerIdNot(int... value) {
        addNotEquals(RemoteRootColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public RemoteRootSelection computerIdGt(int value) {
        addGreaterThan(RemoteRootColumns.COMPUTER_ID, value);
        return this;
    }

    public RemoteRootSelection computerIdGtEq(int value) {
        addGreaterThanOrEquals(RemoteRootColumns.COMPUTER_ID, value);
        return this;
    }

    public RemoteRootSelection computerIdLt(int value) {
        addLessThan(RemoteRootColumns.COMPUTER_ID, value);
        return this;
    }

    public RemoteRootSelection computerIdLtEq(int value) {
        addLessThanOrEquals(RemoteRootColumns.COMPUTER_ID, value);
        return this;
    }
}
