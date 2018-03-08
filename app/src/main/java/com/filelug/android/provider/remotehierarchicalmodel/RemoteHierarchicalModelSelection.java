package com.filelug.android.provider.remotehierarchicalmodel;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.filelug.android.provider.base.AbstractSelection;

/**
 * Selection for the {@code remote_hierarchical_model} table.
 */
public class RemoteHierarchicalModelSelection extends AbstractSelection<RemoteHierarchicalModelSelection> {
    @Override
    protected Uri baseUri() {
        return RemoteHierarchicalModelColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code RemoteHierarchicalModelCursor} object, which is positioned before the first entry, or null.
     */
    public RemoteHierarchicalModelCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new RemoteHierarchicalModelCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null)}.
     */
    public RemoteHierarchicalModelCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null)}.
     */
    public RemoteHierarchicalModelCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public RemoteHierarchicalModelSelection id(long... value) {
        addEquals(RemoteHierarchicalModelColumns.TABLE_NAME + "." + RemoteHierarchicalModelColumns._ID, toObjectArray(value));
        return this;
    }

    public RemoteHierarchicalModelSelection symlink(boolean value) {
        addEquals(RemoteHierarchicalModelColumns.SYMLINK, toObjectArray(value));
        return this;
    }

    public RemoteHierarchicalModelSelection parent(String... value) {
        addEquals(RemoteHierarchicalModelColumns.PARENT, value);
        return this;
    }

    public RemoteHierarchicalModelSelection parentNot(String... value) {
        addNotEquals(RemoteHierarchicalModelColumns.PARENT, value);
        return this;
    }

    public RemoteHierarchicalModelSelection parentLike(String... value) {
        addLike(RemoteHierarchicalModelColumns.PARENT, value);
        return this;
    }

    public RemoteHierarchicalModelSelection parentContains(String... value) {
        addContains(RemoteHierarchicalModelColumns.PARENT, value);
        return this;
    }

    public RemoteHierarchicalModelSelection parentStartsWith(String... value) {
        addStartsWith(RemoteHierarchicalModelColumns.PARENT, value);
        return this;
    }

    public RemoteHierarchicalModelSelection parentEndsWith(String... value) {
        addEndsWith(RemoteHierarchicalModelColumns.PARENT, value);
        return this;
    }

    public RemoteHierarchicalModelSelection name(String... value) {
        addEquals(RemoteHierarchicalModelColumns.NAME, value);
        return this;
    }

    public RemoteHierarchicalModelSelection nameNot(String... value) {
        addNotEquals(RemoteHierarchicalModelColumns.NAME, value);
        return this;
    }

    public RemoteHierarchicalModelSelection nameLike(String... value) {
        addLike(RemoteHierarchicalModelColumns.NAME, value);
        return this;
    }

    public RemoteHierarchicalModelSelection nameContains(String... value) {
        addContains(RemoteHierarchicalModelColumns.NAME, value);
        return this;
    }

    public RemoteHierarchicalModelSelection nameStartsWith(String... value) {
        addStartsWith(RemoteHierarchicalModelColumns.NAME, value);
        return this;
    }

    public RemoteHierarchicalModelSelection nameEndsWith(String... value) {
        addEndsWith(RemoteHierarchicalModelColumns.NAME, value);
        return this;
    }

    public RemoteHierarchicalModelSelection readable(boolean value) {
        addEquals(RemoteHierarchicalModelColumns.READABLE, toObjectArray(value));
        return this;
    }

    public RemoteHierarchicalModelSelection writable(boolean value) {
        addEquals(RemoteHierarchicalModelColumns.WRITABLE, toObjectArray(value));
        return this;
    }

    public RemoteHierarchicalModelSelection hidden(boolean value) {
        addEquals(RemoteHierarchicalModelColumns.HIDDEN, toObjectArray(value));
        return this;
    }

    public RemoteHierarchicalModelSelection lastModified(String... value) {
        addEquals(RemoteHierarchicalModelColumns.LAST_MODIFIED, value);
        return this;
    }

    public RemoteHierarchicalModelSelection lastModifiedNot(String... value) {
        addNotEquals(RemoteHierarchicalModelColumns.LAST_MODIFIED, value);
        return this;
    }

    public RemoteHierarchicalModelSelection lastModifiedLike(String... value) {
        addLike(RemoteHierarchicalModelColumns.LAST_MODIFIED, value);
        return this;
    }

    public RemoteHierarchicalModelSelection lastModifiedContains(String... value) {
        addContains(RemoteHierarchicalModelColumns.LAST_MODIFIED, value);
        return this;
    }

    public RemoteHierarchicalModelSelection lastModifiedStartsWith(String... value) {
        addStartsWith(RemoteHierarchicalModelColumns.LAST_MODIFIED, value);
        return this;
    }

    public RemoteHierarchicalModelSelection lastModifiedEndsWith(String... value) {
        addEndsWith(RemoteHierarchicalModelColumns.LAST_MODIFIED, value);
        return this;
    }

    public RemoteHierarchicalModelSelection type(RemoteObjectType... value) {
        addEquals(RemoteHierarchicalModelColumns.TYPE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection typeNot(RemoteObjectType... value) {
        addNotEquals(RemoteHierarchicalModelColumns.TYPE, value);
        return this;
    }


    public RemoteHierarchicalModelSelection contentType(String... value) {
        addEquals(RemoteHierarchicalModelColumns.CONTENT_TYPE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection contentTypeNot(String... value) {
        addNotEquals(RemoteHierarchicalModelColumns.CONTENT_TYPE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection contentTypeLike(String... value) {
        addLike(RemoteHierarchicalModelColumns.CONTENT_TYPE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection contentTypeContains(String... value) {
        addContains(RemoteHierarchicalModelColumns.CONTENT_TYPE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection contentTypeStartsWith(String... value) {
        addStartsWith(RemoteHierarchicalModelColumns.CONTENT_TYPE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection contentTypeEndsWith(String... value) {
        addEndsWith(RemoteHierarchicalModelColumns.CONTENT_TYPE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection size(long... value) {
        addEquals(RemoteHierarchicalModelColumns.SIZE, toObjectArray(value));
        return this;
    }

    public RemoteHierarchicalModelSelection sizeNot(long... value) {
        addNotEquals(RemoteHierarchicalModelColumns.SIZE, toObjectArray(value));
        return this;
    }

    public RemoteHierarchicalModelSelection sizeGt(long value) {
        addGreaterThan(RemoteHierarchicalModelColumns.SIZE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection sizeGtEq(long value) {
        addGreaterThanOrEquals(RemoteHierarchicalModelColumns.SIZE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection sizeLt(long value) {
        addLessThan(RemoteHierarchicalModelColumns.SIZE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection sizeLtEq(long value) {
        addLessThanOrEquals(RemoteHierarchicalModelColumns.SIZE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection realParent(String... value) {
        addEquals(RemoteHierarchicalModelColumns.REAL_PARENT, value);
        return this;
    }

    public RemoteHierarchicalModelSelection realParentNot(String... value) {
        addNotEquals(RemoteHierarchicalModelColumns.REAL_PARENT, value);
        return this;
    }

    public RemoteHierarchicalModelSelection realParentLike(String... value) {
        addLike(RemoteHierarchicalModelColumns.REAL_PARENT, value);
        return this;
    }

    public RemoteHierarchicalModelSelection realParentContains(String... value) {
        addContains(RemoteHierarchicalModelColumns.REAL_PARENT, value);
        return this;
    }

    public RemoteHierarchicalModelSelection realParentStartsWith(String... value) {
        addStartsWith(RemoteHierarchicalModelColumns.REAL_PARENT, value);
        return this;
    }

    public RemoteHierarchicalModelSelection realParentEndsWith(String... value) {
        addEndsWith(RemoteHierarchicalModelColumns.REAL_PARENT, value);
        return this;
    }

    public RemoteHierarchicalModelSelection realName(String... value) {
        addEquals(RemoteHierarchicalModelColumns.REAL_NAME, value);
        return this;
    }

    public RemoteHierarchicalModelSelection realNameNot(String... value) {
        addNotEquals(RemoteHierarchicalModelColumns.REAL_NAME, value);
        return this;
    }

    public RemoteHierarchicalModelSelection realNameLike(String... value) {
        addLike(RemoteHierarchicalModelColumns.REAL_NAME, value);
        return this;
    }

    public RemoteHierarchicalModelSelection realNameContains(String... value) {
        addContains(RemoteHierarchicalModelColumns.REAL_NAME, value);
        return this;
    }

    public RemoteHierarchicalModelSelection realNameStartsWith(String... value) {
        addStartsWith(RemoteHierarchicalModelColumns.REAL_NAME, value);
        return this;
    }

    public RemoteHierarchicalModelSelection realNameEndsWith(String... value) {
        addEndsWith(RemoteHierarchicalModelColumns.REAL_NAME, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localLastModified(Long... value) {
        addEquals(RemoteHierarchicalModelColumns.LOCAL_LAST_MODIFIED, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localLastModifiedNot(Long... value) {
        addNotEquals(RemoteHierarchicalModelColumns.LOCAL_LAST_MODIFIED, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localLastModifiedGt(long value) {
        addGreaterThan(RemoteHierarchicalModelColumns.LOCAL_LAST_MODIFIED, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localLastModifiedGtEq(long value) {
        addGreaterThanOrEquals(RemoteHierarchicalModelColumns.LOCAL_LAST_MODIFIED, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localLastModifiedLt(long value) {
        addLessThan(RemoteHierarchicalModelColumns.LOCAL_LAST_MODIFIED, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localLastModifiedLtEq(long value) {
        addLessThanOrEquals(RemoteHierarchicalModelColumns.LOCAL_LAST_MODIFIED, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localSize(Long... value) {
        addEquals(RemoteHierarchicalModelColumns.LOCAL_SIZE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localSizeNot(Long... value) {
        addNotEquals(RemoteHierarchicalModelColumns.LOCAL_SIZE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localSizeGt(long value) {
        addGreaterThan(RemoteHierarchicalModelColumns.LOCAL_SIZE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localSizeGtEq(long value) {
        addGreaterThanOrEquals(RemoteHierarchicalModelColumns.LOCAL_SIZE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localSizeLt(long value) {
        addLessThan(RemoteHierarchicalModelColumns.LOCAL_SIZE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localSizeLtEq(long value) {
        addLessThanOrEquals(RemoteHierarchicalModelColumns.LOCAL_SIZE, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localLastAccess(Long... value) {
        addEquals(RemoteHierarchicalModelColumns.LOCAL_LAST_ACCESS, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localLastAccessNot(Long... value) {
        addNotEquals(RemoteHierarchicalModelColumns.LOCAL_LAST_ACCESS, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localLastAccessGt(long value) {
        addGreaterThan(RemoteHierarchicalModelColumns.LOCAL_LAST_ACCESS, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localLastAccessGtEq(long value) {
        addGreaterThanOrEquals(RemoteHierarchicalModelColumns.LOCAL_LAST_ACCESS, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localLastAccessLt(long value) {
        addLessThan(RemoteHierarchicalModelColumns.LOCAL_LAST_ACCESS, value);
        return this;
    }

    public RemoteHierarchicalModelSelection localLastAccessLtEq(long value) {
        addLessThanOrEquals(RemoteHierarchicalModelColumns.LOCAL_LAST_ACCESS, value);
        return this;
    }

    public RemoteHierarchicalModelSelection userId(String... value) {
        addEquals(RemoteHierarchicalModelColumns.USER_ID, value);
        return this;
    }

    public RemoteHierarchicalModelSelection userIdNot(String... value) {
        addNotEquals(RemoteHierarchicalModelColumns.USER_ID, value);
        return this;
    }

    public RemoteHierarchicalModelSelection userIdLike(String... value) {
        addLike(RemoteHierarchicalModelColumns.USER_ID, value);
        return this;
    }

    public RemoteHierarchicalModelSelection userIdContains(String... value) {
        addContains(RemoteHierarchicalModelColumns.USER_ID, value);
        return this;
    }

    public RemoteHierarchicalModelSelection userIdStartsWith(String... value) {
        addStartsWith(RemoteHierarchicalModelColumns.USER_ID, value);
        return this;
    }

    public RemoteHierarchicalModelSelection userIdEndsWith(String... value) {
        addEndsWith(RemoteHierarchicalModelColumns.USER_ID, value);
        return this;
    }

    public RemoteHierarchicalModelSelection computerId(int... value) {
        addEquals(RemoteHierarchicalModelColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public RemoteHierarchicalModelSelection computerIdNot(int... value) {
        addNotEquals(RemoteHierarchicalModelColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public RemoteHierarchicalModelSelection computerIdGt(int value) {
        addGreaterThan(RemoteHierarchicalModelColumns.COMPUTER_ID, value);
        return this;
    }

    public RemoteHierarchicalModelSelection computerIdGtEq(int value) {
        addGreaterThanOrEquals(RemoteHierarchicalModelColumns.COMPUTER_ID, value);
        return this;
    }

    public RemoteHierarchicalModelSelection computerIdLt(int value) {
        addLessThan(RemoteHierarchicalModelColumns.COMPUTER_ID, value);
        return this;
    }

    public RemoteHierarchicalModelSelection computerIdLtEq(int value) {
        addLessThanOrEquals(RemoteHierarchicalModelColumns.COMPUTER_ID, value);
        return this;
    }
}
