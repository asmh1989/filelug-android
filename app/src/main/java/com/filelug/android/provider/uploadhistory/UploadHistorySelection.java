package com.filelug.android.provider.uploadhistory;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.filelug.android.provider.base.AbstractSelection;

/**
 * Selection for the {@code upload_history} table.
 */
public class UploadHistorySelection extends AbstractSelection<UploadHistorySelection> {
    @Override
    protected Uri baseUri() {
        return UploadHistoryColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code UploadHistoryCursor} object, which is positioned before the first entry, or null.
     */
    public UploadHistoryCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new UploadHistoryCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null)}.
     */
    public UploadHistoryCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null)}.
     */
    public UploadHistoryCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public UploadHistorySelection id(long... value) {
        addEquals(UploadHistoryColumns.TABLE_NAME + "." + UploadHistoryColumns._ID, toObjectArray(value));
        return this;
    }

    public UploadHistorySelection computerGroup(String... value) {
        addEquals(UploadHistoryColumns.COMPUTER_GROUP, value);
        return this;
    }

    public UploadHistorySelection computerGroupNot(String... value) {
        addNotEquals(UploadHistoryColumns.COMPUTER_GROUP, value);
        return this;
    }

    public UploadHistorySelection computerGroupLike(String... value) {
        addLike(UploadHistoryColumns.COMPUTER_GROUP, value);
        return this;
    }

    public UploadHistorySelection computerGroupContains(String... value) {
        addContains(UploadHistoryColumns.COMPUTER_GROUP, value);
        return this;
    }

    public UploadHistorySelection computerGroupStartsWith(String... value) {
        addStartsWith(UploadHistoryColumns.COMPUTER_GROUP, value);
        return this;
    }

    public UploadHistorySelection computerGroupEndsWith(String... value) {
        addEndsWith(UploadHistoryColumns.COMPUTER_GROUP, value);
        return this;
    }

    public UploadHistorySelection computerName(String... value) {
        addEquals(UploadHistoryColumns.COMPUTER_NAME, value);
        return this;
    }

    public UploadHistorySelection computerNameNot(String... value) {
        addNotEquals(UploadHistoryColumns.COMPUTER_NAME, value);
        return this;
    }

    public UploadHistorySelection computerNameLike(String... value) {
        addLike(UploadHistoryColumns.COMPUTER_NAME, value);
        return this;
    }

    public UploadHistorySelection computerNameContains(String... value) {
        addContains(UploadHistoryColumns.COMPUTER_NAME, value);
        return this;
    }

    public UploadHistorySelection computerNameStartsWith(String... value) {
        addStartsWith(UploadHistoryColumns.COMPUTER_NAME, value);
        return this;
    }

    public UploadHistorySelection computerNameEndsWith(String... value) {
        addEndsWith(UploadHistoryColumns.COMPUTER_NAME, value);
        return this;
    }

    public UploadHistorySelection fileSize(long... value) {
        addEquals(UploadHistoryColumns.FILE_SIZE, toObjectArray(value));
        return this;
    }

    public UploadHistorySelection fileSizeNot(long... value) {
        addNotEquals(UploadHistoryColumns.FILE_SIZE, toObjectArray(value));
        return this;
    }

    public UploadHistorySelection fileSizeGt(long value) {
        addGreaterThan(UploadHistoryColumns.FILE_SIZE, value);
        return this;
    }

    public UploadHistorySelection fileSizeGtEq(long value) {
        addGreaterThanOrEquals(UploadHistoryColumns.FILE_SIZE, value);
        return this;
    }

    public UploadHistorySelection fileSizeLt(long value) {
        addLessThan(UploadHistoryColumns.FILE_SIZE, value);
        return this;
    }

    public UploadHistorySelection fileSizeLtEq(long value) {
        addLessThanOrEquals(UploadHistoryColumns.FILE_SIZE, value);
        return this;
    }

    public UploadHistorySelection endTimestamp(long... value) {
        addEquals(UploadHistoryColumns.END_TIMESTAMP, toObjectArray(value));
        return this;
    }

    public UploadHistorySelection endTimestampNot(long... value) {
        addNotEquals(UploadHistoryColumns.END_TIMESTAMP, toObjectArray(value));
        return this;
    }

    public UploadHistorySelection endTimestampGt(long value) {
        addGreaterThan(UploadHistoryColumns.END_TIMESTAMP, value);
        return this;
    }

    public UploadHistorySelection endTimestampGtEq(long value) {
        addGreaterThanOrEquals(UploadHistoryColumns.END_TIMESTAMP, value);
        return this;
    }

    public UploadHistorySelection endTimestampLt(long value) {
        addLessThan(UploadHistoryColumns.END_TIMESTAMP, value);
        return this;
    }

    public UploadHistorySelection endTimestampLtEq(long value) {
        addLessThanOrEquals(UploadHistoryColumns.END_TIMESTAMP, value);
        return this;
    }

    public UploadHistorySelection fileName(String... value) {
        addEquals(UploadHistoryColumns.FILE_NAME, value);
        return this;
    }

    public UploadHistorySelection fileNameNot(String... value) {
        addNotEquals(UploadHistoryColumns.FILE_NAME, value);
        return this;
    }

    public UploadHistorySelection fileNameLike(String... value) {
        addLike(UploadHistoryColumns.FILE_NAME, value);
        return this;
    }

    public UploadHistorySelection fileNameContains(String... value) {
        addContains(UploadHistoryColumns.FILE_NAME, value);
        return this;
    }

    public UploadHistorySelection fileNameStartsWith(String... value) {
        addStartsWith(UploadHistoryColumns.FILE_NAME, value);
        return this;
    }

    public UploadHistorySelection fileNameEndsWith(String... value) {
        addEndsWith(UploadHistoryColumns.FILE_NAME, value);
        return this;
    }

    public UploadHistorySelection userId(String... value) {
        addEquals(UploadHistoryColumns.USER_ID, value);
        return this;
    }

    public UploadHistorySelection userIdNot(String... value) {
        addNotEquals(UploadHistoryColumns.USER_ID, value);
        return this;
    }

    public UploadHistorySelection userIdLike(String... value) {
        addLike(UploadHistoryColumns.USER_ID, value);
        return this;
    }

    public UploadHistorySelection userIdContains(String... value) {
        addContains(UploadHistoryColumns.USER_ID, value);
        return this;
    }

    public UploadHistorySelection userIdStartsWith(String... value) {
        addStartsWith(UploadHistoryColumns.USER_ID, value);
        return this;
    }

    public UploadHistorySelection userIdEndsWith(String... value) {
        addEndsWith(UploadHistoryColumns.USER_ID, value);
        return this;
    }

    public UploadHistorySelection computerId(int... value) {
        addEquals(UploadHistoryColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public UploadHistorySelection computerIdNot(int... value) {
        addNotEquals(UploadHistoryColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public UploadHistorySelection computerIdGt(int value) {
        addGreaterThan(UploadHistoryColumns.COMPUTER_ID, value);
        return this;
    }

    public UploadHistorySelection computerIdGtEq(int value) {
        addGreaterThanOrEquals(UploadHistoryColumns.COMPUTER_ID, value);
        return this;
    }

    public UploadHistorySelection computerIdLt(int value) {
        addLessThan(UploadHistoryColumns.COMPUTER_ID, value);
        return this;
    }

    public UploadHistorySelection computerIdLtEq(int value) {
        addLessThanOrEquals(UploadHistoryColumns.COMPUTER_ID, value);
        return this;
    }
}
