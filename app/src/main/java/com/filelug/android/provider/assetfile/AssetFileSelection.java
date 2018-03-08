package com.filelug.android.provider.assetfile;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.filelug.android.provider.base.AbstractSelection;

/**
 * Selection for the {@code asset_file} table.
 */
public class AssetFileSelection extends AbstractSelection<AssetFileSelection> {
    @Override
    protected Uri baseUri() {
        return AssetFileColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code AssetFileCursor} object, which is positioned before the first entry, or null.
     */
    public AssetFileCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new AssetFileCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null)}.
     */
    public AssetFileCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null)}.
     */
    public AssetFileCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public AssetFileSelection id(long... value) {
        addEquals(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns._ID, toObjectArray(value));
        return this;
    }

    public AssetFileSelection groupId(String... value) {
        addEquals(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.GROUP_ID, value);
        return this;
    }

    public AssetFileSelection groupIdNot(String... value) {
        addNotEquals(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.GROUP_ID, value);
        return this;
    }

    public AssetFileSelection groupIdLike(String... value) {
        addLike(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.GROUP_ID, value);
        return this;
    }

    public AssetFileSelection groupIdContains(String... value) {
        addContains(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.GROUP_ID, value);
        return this;
    }

    public AssetFileSelection groupIdStartsWith(String... value) {
        addStartsWith(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.GROUP_ID, value);
        return this;
    }

    public AssetFileSelection groupIdEndsWith(String... value) {
        addEndsWith(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.GROUP_ID, value);
        return this;
    }

    public AssetFileSelection transferKey(String... value) {
        addEquals(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.TRANSFER_KEY, value);
        return this;
    }

    public AssetFileSelection transferKeyNot(String... value) {
        addNotEquals(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.TRANSFER_KEY, value);
        return this;
    }

    public AssetFileSelection transferKeyLike(String... value) {
        addLike(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.TRANSFER_KEY, value);
        return this;
    }

    public AssetFileSelection transferKeyContains(String... value) {
        addContains(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.TRANSFER_KEY, value);
        return this;
    }

    public AssetFileSelection transferKeyStartsWith(String... value) {
        addStartsWith(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.TRANSFER_KEY, value);
        return this;
    }

    public AssetFileSelection transferKeyEndsWith(String... value) {
        addEndsWith(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.TRANSFER_KEY, value);
        return this;
    }

    public AssetFileSelection assetUrl(String... value) {
        addEquals(AssetFileColumns.ASSET_URL, value);
        return this;
    }

    public AssetFileSelection assetUrlNot(String... value) {
        addNotEquals(AssetFileColumns.ASSET_URL, value);
        return this;
    }

    public AssetFileSelection assetUrlLike(String... value) {
        addLike(AssetFileColumns.ASSET_URL, value);
        return this;
    }

    public AssetFileSelection assetUrlContains(String... value) {
        addContains(AssetFileColumns.ASSET_URL, value);
        return this;
    }

    public AssetFileSelection assetUrlStartsWith(String... value) {
        addStartsWith(AssetFileColumns.ASSET_URL, value);
        return this;
    }

    public AssetFileSelection assetUrlEndsWith(String... value) {
        addEndsWith(AssetFileColumns.ASSET_URL, value);
        return this;
    }

    public AssetFileSelection serverFileName(String... value) {
        addEquals(AssetFileColumns.SERVER_FILE_NAME, value);
        return this;
    }

    public AssetFileSelection serverFileNameNot(String... value) {
        addNotEquals(AssetFileColumns.SERVER_FILE_NAME, value);
        return this;
    }

    public AssetFileSelection serverFileNameLike(String... value) {
        addLike(AssetFileColumns.SERVER_FILE_NAME, value);
        return this;
    }

    public AssetFileSelection serverFileNameContains(String... value) {
        addContains(AssetFileColumns.SERVER_FILE_NAME, value);
        return this;
    }

    public AssetFileSelection serverFileNameStartsWith(String... value) {
        addStartsWith(AssetFileColumns.SERVER_FILE_NAME, value);
        return this;
    }

    public AssetFileSelection serverFileNameEndsWith(String... value) {
        addEndsWith(AssetFileColumns.SERVER_FILE_NAME, value);
        return this;
    }

    public AssetFileSelection cacheFileName(String... value) {
        addEquals(AssetFileColumns.CACHE_FILE_NAME, value);
        return this;
    }

    public AssetFileSelection cacheFileNameNot(String... value) {
        addNotEquals(AssetFileColumns.CACHE_FILE_NAME, value);
        return this;
    }

    public AssetFileSelection cacheFileNameLike(String... value) {
        addLike(AssetFileColumns.CACHE_FILE_NAME, value);
        return this;
    }

    public AssetFileSelection cacheFileNameContains(String... value) {
        addContains(AssetFileColumns.CACHE_FILE_NAME, value);
        return this;
    }

    public AssetFileSelection cacheFileNameStartsWith(String... value) {
        addStartsWith(AssetFileColumns.CACHE_FILE_NAME, value);
        return this;
    }

    public AssetFileSelection cacheFileNameEndsWith(String... value) {
        addEndsWith(AssetFileColumns.CACHE_FILE_NAME, value);
        return this;
    }

    public AssetFileSelection contentType(String... value) {
        addEquals(AssetFileColumns.CONTENT_TYPE, value);
        return this;
    }

    public AssetFileSelection contentTypeNot(String... value) {
        addNotEquals(AssetFileColumns.CONTENT_TYPE, value);
        return this;
    }

    public AssetFileSelection contentTypeLike(String... value) {
        addLike(AssetFileColumns.CONTENT_TYPE, value);
        return this;
    }

    public AssetFileSelection contentTypeContains(String... value) {
        addContains(AssetFileColumns.CONTENT_TYPE, value);
        return this;
    }

    public AssetFileSelection contentTypeStartsWith(String... value) {
        addStartsWith(AssetFileColumns.CONTENT_TYPE, value);
        return this;
    }

    public AssetFileSelection contentTypeEndsWith(String... value) {
        addEndsWith(AssetFileColumns.CONTENT_TYPE, value);
        return this;
    }

    public AssetFileSelection lastModifiedTimestamp(Long... value) {
        addEquals(AssetFileColumns.LAST_MODIFIED_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection lastModifiedTimestampNot(Long... value) {
        addNotEquals(AssetFileColumns.LAST_MODIFIED_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection lastModifiedTimestampGt(long value) {
        addGreaterThan(AssetFileColumns.LAST_MODIFIED_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection lastModifiedTimestampGtEq(long value) {
        addGreaterThanOrEquals(AssetFileColumns.LAST_MODIFIED_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection lastModifiedTimestampLt(long value) {
        addLessThan(AssetFileColumns.LAST_MODIFIED_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection lastModifiedTimestampLtEq(long value) {
        addLessThanOrEquals(AssetFileColumns.LAST_MODIFIED_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection status(UploadStatusType... value) {
        addEquals(AssetFileColumns.STATUS, value);
        return this;
    }

    public AssetFileSelection statusNot(UploadStatusType... value) {
        addNotEquals(AssetFileColumns.STATUS, value);
        return this;
    }


    public AssetFileSelection startTimestamp(Long... value) {
        addEquals(AssetFileColumns.START_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection startTimestampNot(Long... value) {
        addNotEquals(AssetFileColumns.START_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection startTimestampGt(long value) {
        addGreaterThan(AssetFileColumns.START_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection startTimestampGtEq(long value) {
        addGreaterThanOrEquals(AssetFileColumns.START_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection startTimestampLt(long value) {
        addLessThan(AssetFileColumns.START_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection startTimestampLtEq(long value) {
        addLessThanOrEquals(AssetFileColumns.START_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection endTimestamp(Long... value) {
        addEquals(AssetFileColumns.END_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection endTimestampNot(Long... value) {
        addNotEquals(AssetFileColumns.END_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection endTimestampGt(long value) {
        addGreaterThan(AssetFileColumns.END_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection endTimestampGtEq(long value) {
        addGreaterThanOrEquals(AssetFileColumns.END_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection endTimestampLt(long value) {
        addLessThan(AssetFileColumns.END_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection endTimestampLtEq(long value) {
        addLessThanOrEquals(AssetFileColumns.END_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection totalSize(Long... value) {
        addEquals(AssetFileColumns.TOTAL_SIZE, value);
        return this;
    }

    public AssetFileSelection totalSizeNot(Long... value) {
        addNotEquals(AssetFileColumns.TOTAL_SIZE, value);
        return this;
    }

    public AssetFileSelection totalSizeGt(long value) {
        addGreaterThan(AssetFileColumns.TOTAL_SIZE, value);
        return this;
    }

    public AssetFileSelection totalSizeGtEq(long value) {
        addGreaterThanOrEquals(AssetFileColumns.TOTAL_SIZE, value);
        return this;
    }

    public AssetFileSelection totalSizeLt(long value) {
        addLessThan(AssetFileColumns.TOTAL_SIZE, value);
        return this;
    }

    public AssetFileSelection totalSizeLtEq(long value) {
        addLessThanOrEquals(AssetFileColumns.TOTAL_SIZE, value);
        return this;
    }

    public AssetFileSelection transferredSize(Long... value) {
        addEquals(AssetFileColumns.TRANSFERRED_SIZE, value);
        return this;
    }

    public AssetFileSelection transferredSizeNot(Long... value) {
        addNotEquals(AssetFileColumns.TRANSFERRED_SIZE, value);
        return this;
    }

    public AssetFileSelection transferredSizeGt(long value) {
        addGreaterThan(AssetFileColumns.TRANSFERRED_SIZE, value);
        return this;
    }

    public AssetFileSelection transferredSizeGtEq(long value) {
        addGreaterThanOrEquals(AssetFileColumns.TRANSFERRED_SIZE, value);
        return this;
    }

    public AssetFileSelection transferredSizeLt(long value) {
        addLessThan(AssetFileColumns.TRANSFERRED_SIZE, value);
        return this;
    }

    public AssetFileSelection transferredSizeLtEq(long value) {
        addLessThanOrEquals(AssetFileColumns.TRANSFERRED_SIZE, value);
        return this;
    }

    public AssetFileSelection waitToConfirm(boolean value) {
        addEquals(AssetFileColumns.WAIT_TO_CONFIRM, toObjectArray(value));
        return this;
    }

    public AssetFileSelection userId(String... value) {
        addEquals(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.USER_ID, value);
        return this;
    }

    public AssetFileSelection userIdNot(String... value) {
        addNotEquals(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.USER_ID, value);
        return this;
    }

    public AssetFileSelection userIdLike(String... value) {
        addLike(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.USER_ID, value);
        return this;
    }

    public AssetFileSelection userIdContains(String... value) {
        addContains(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.USER_ID, value);
        return this;
    }

    public AssetFileSelection userIdStartsWith(String... value) {
        addStartsWith(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.USER_ID, value);
        return this;
    }

    public AssetFileSelection userIdEndsWith(String... value) {
        addEndsWith(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.USER_ID, value);
        return this;
    }

    public AssetFileSelection computerId(int... value) {
        addEquals(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public AssetFileSelection computerIdNot(int... value) {
        addNotEquals(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public AssetFileSelection computerIdGt(int value) {
        addGreaterThan(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.COMPUTER_ID, value);
        return this;
    }

    public AssetFileSelection computerIdGtEq(int value) {
        addGreaterThanOrEquals(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.COMPUTER_ID, value);
        return this;
    }

    public AssetFileSelection computerIdLt(int value) {
        addLessThan(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.COMPUTER_ID, value);
        return this;
    }

    public AssetFileSelection computerIdLtEq(int value) {
        addLessThanOrEquals(AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.COMPUTER_ID, value);
        return this;
    }

    public AssetFileSelection ugFromAnotherApp(boolean value) {
        addEquals(AssetFileColumns.UG_FROM_ANOTHER_APP, toObjectArray(value));
        return this;
    }


    public AssetFileSelection ugStartTimestamp(Long... value) {
        addEquals(AssetFileColumns.UG_START_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection ugStartTimestampNot(Long... value) {
        addNotEquals(AssetFileColumns.UG_START_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection ugStartTimestampGt(long value) {
        addGreaterThan(AssetFileColumns.UG_START_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection ugStartTimestampGtEq(long value) {
        addGreaterThanOrEquals(AssetFileColumns.UG_START_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection ugStartTimestampLt(long value) {
        addLessThan(AssetFileColumns.UG_START_TIMESTAMP, value);
        return this;
    }

    public AssetFileSelection ugStartTimestampLtEq(long value) {
        addLessThanOrEquals(AssetFileColumns.UG_START_TIMESTAMP, value);
        return this;
    }
}
