package com.filelug.android.provider.downloadhistory;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.filelug.android.provider.base.AbstractSelection;

/**
 * Selection for the {@code download_history} table.
 */
public class DownloadHistorySelection extends AbstractSelection<DownloadHistorySelection> {
    @Override
    protected Uri baseUri() {
        return DownloadHistoryColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code DownloadHistoryCursor} object, which is positioned before the first entry, or null.
     */
    public DownloadHistoryCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new DownloadHistoryCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null)}.
     */
    public DownloadHistoryCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null)}.
     */
    public DownloadHistoryCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public DownloadHistorySelection id(long... value) {
        addEquals(DownloadHistoryColumns.TABLE_NAME + "." + DownloadHistoryColumns._ID, toObjectArray(value));
        return this;
    }

    public DownloadHistorySelection computerGroup(String... value) {
        addEquals(DownloadHistoryColumns.COMPUTER_GROUP, value);
        return this;
    }

    public DownloadHistorySelection computerGroupNot(String... value) {
        addNotEquals(DownloadHistoryColumns.COMPUTER_GROUP, value);
        return this;
    }

    public DownloadHistorySelection computerGroupLike(String... value) {
        addLike(DownloadHistoryColumns.COMPUTER_GROUP, value);
        return this;
    }

    public DownloadHistorySelection computerGroupContains(String... value) {
        addContains(DownloadHistoryColumns.COMPUTER_GROUP, value);
        return this;
    }

    public DownloadHistorySelection computerGroupStartsWith(String... value) {
        addStartsWith(DownloadHistoryColumns.COMPUTER_GROUP, value);
        return this;
    }

    public DownloadHistorySelection computerGroupEndsWith(String... value) {
        addEndsWith(DownloadHistoryColumns.COMPUTER_GROUP, value);
        return this;
    }

    public DownloadHistorySelection computerName(String... value) {
        addEquals(DownloadHistoryColumns.COMPUTER_NAME, value);
        return this;
    }

    public DownloadHistorySelection computerNameNot(String... value) {
        addNotEquals(DownloadHistoryColumns.COMPUTER_NAME, value);
        return this;
    }

    public DownloadHistorySelection computerNameLike(String... value) {
        addLike(DownloadHistoryColumns.COMPUTER_NAME, value);
        return this;
    }

    public DownloadHistorySelection computerNameContains(String... value) {
        addContains(DownloadHistoryColumns.COMPUTER_NAME, value);
        return this;
    }

    public DownloadHistorySelection computerNameStartsWith(String... value) {
        addStartsWith(DownloadHistoryColumns.COMPUTER_NAME, value);
        return this;
    }

    public DownloadHistorySelection computerNameEndsWith(String... value) {
        addEndsWith(DownloadHistoryColumns.COMPUTER_NAME, value);
        return this;
    }

    public DownloadHistorySelection fileSize(long... value) {
        addEquals(DownloadHistoryColumns.FILE_SIZE, toObjectArray(value));
        return this;
    }

    public DownloadHistorySelection fileSizeNot(long... value) {
        addNotEquals(DownloadHistoryColumns.FILE_SIZE, toObjectArray(value));
        return this;
    }

    public DownloadHistorySelection fileSizeGt(long value) {
        addGreaterThan(DownloadHistoryColumns.FILE_SIZE, value);
        return this;
    }

    public DownloadHistorySelection fileSizeGtEq(long value) {
        addGreaterThanOrEquals(DownloadHistoryColumns.FILE_SIZE, value);
        return this;
    }

    public DownloadHistorySelection fileSizeLt(long value) {
        addLessThan(DownloadHistoryColumns.FILE_SIZE, value);
        return this;
    }

    public DownloadHistorySelection fileSizeLtEq(long value) {
        addLessThanOrEquals(DownloadHistoryColumns.FILE_SIZE, value);
        return this;
    }

    public DownloadHistorySelection endTimestamp(long... value) {
        addEquals(DownloadHistoryColumns.END_TIMESTAMP, toObjectArray(value));
        return this;
    }

    public DownloadHistorySelection endTimestampNot(long... value) {
        addNotEquals(DownloadHistoryColumns.END_TIMESTAMP, toObjectArray(value));
        return this;
    }

    public DownloadHistorySelection endTimestampGt(long value) {
        addGreaterThan(DownloadHistoryColumns.END_TIMESTAMP, value);
        return this;
    }

    public DownloadHistorySelection endTimestampGtEq(long value) {
        addGreaterThanOrEquals(DownloadHistoryColumns.END_TIMESTAMP, value);
        return this;
    }

    public DownloadHistorySelection endTimestampLt(long value) {
        addLessThan(DownloadHistoryColumns.END_TIMESTAMP, value);
        return this;
    }

    public DownloadHistorySelection endTimestampLtEq(long value) {
        addLessThanOrEquals(DownloadHistoryColumns.END_TIMESTAMP, value);
        return this;
    }

    public DownloadHistorySelection fileName(String... value) {
        addEquals(DownloadHistoryColumns.FILE_NAME, value);
        return this;
    }

    public DownloadHistorySelection fileNameNot(String... value) {
        addNotEquals(DownloadHistoryColumns.FILE_NAME, value);
        return this;
    }

    public DownloadHistorySelection fileNameLike(String... value) {
        addLike(DownloadHistoryColumns.FILE_NAME, value);
        return this;
    }

    public DownloadHistorySelection fileNameContains(String... value) {
        addContains(DownloadHistoryColumns.FILE_NAME, value);
        return this;
    }

    public DownloadHistorySelection fileNameStartsWith(String... value) {
        addStartsWith(DownloadHistoryColumns.FILE_NAME, value);
        return this;
    }

    public DownloadHistorySelection fileNameEndsWith(String... value) {
        addEndsWith(DownloadHistoryColumns.FILE_NAME, value);
        return this;
    }

    public DownloadHistorySelection userId(String... value) {
        addEquals(DownloadHistoryColumns.USER_ID, value);
        return this;
    }

    public DownloadHistorySelection userIdNot(String... value) {
        addNotEquals(DownloadHistoryColumns.USER_ID, value);
        return this;
    }

    public DownloadHistorySelection userIdLike(String... value) {
        addLike(DownloadHistoryColumns.USER_ID, value);
        return this;
    }

    public DownloadHistorySelection userIdContains(String... value) {
        addContains(DownloadHistoryColumns.USER_ID, value);
        return this;
    }

    public DownloadHistorySelection userIdStartsWith(String... value) {
        addStartsWith(DownloadHistoryColumns.USER_ID, value);
        return this;
    }

    public DownloadHistorySelection userIdEndsWith(String... value) {
        addEndsWith(DownloadHistoryColumns.USER_ID, value);
        return this;
    }

    public DownloadHistorySelection computerId(int... value) {
        addEquals(DownloadHistoryColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public DownloadHistorySelection computerIdNot(int... value) {
        addNotEquals(DownloadHistoryColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public DownloadHistorySelection computerIdGt(int value) {
        addGreaterThan(DownloadHistoryColumns.COMPUTER_ID, value);
        return this;
    }

    public DownloadHistorySelection computerIdGtEq(int value) {
        addGreaterThanOrEquals(DownloadHistoryColumns.COMPUTER_ID, value);
        return this;
    }

    public DownloadHistorySelection computerIdLt(int value) {
        addLessThan(DownloadHistoryColumns.COMPUTER_ID, value);
        return this;
    }

    public DownloadHistorySelection computerIdLtEq(int value) {
        addLessThanOrEquals(DownloadHistoryColumns.COMPUTER_ID, value);
        return this;
    }
}
