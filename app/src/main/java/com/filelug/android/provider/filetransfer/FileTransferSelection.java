package com.filelug.android.provider.filetransfer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.filelug.android.provider.base.AbstractSelection;

/**
 * Selection for the {@code file_transfer} table.
 */
public class FileTransferSelection extends AbstractSelection<FileTransferSelection> {
    @Override
    protected Uri baseUri() {
        return FileTransferColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code FileTransferCursor} object, which is positioned before the first entry, or null.
     */
    public FileTransferCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new FileTransferCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null)}.
     */
    public FileTransferCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null)}.
     */
    public FileTransferCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public FileTransferSelection id(long... value) {
        addEquals(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns._ID, toObjectArray(value));
        return this;
    }

    public FileTransferSelection groupId(String... value) {
        addEquals(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.GROUP_ID, value);
        return this;
    }

    public FileTransferSelection groupIdNot(String... value) {
        addNotEquals(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.GROUP_ID, value);
        return this;
    }

    public FileTransferSelection groupIdLike(String... value) {
        addLike(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.GROUP_ID, value);
        return this;
    }

    public FileTransferSelection groupIdContains(String... value) {
        addContains(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.GROUP_ID, value);
        return this;
    }

    public FileTransferSelection groupIdStartsWith(String... value) {
        addStartsWith(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.GROUP_ID, value);
        return this;
    }

    public FileTransferSelection groupIdEndsWith(String... value) {
        addEndsWith(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.GROUP_ID, value);
        return this;
    }

    public FileTransferSelection transferKey(String... value) {
        addEquals(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.TRANSFER_KEY, value);
        return this;
    }

    public FileTransferSelection transferKeyNot(String... value) {
        addNotEquals(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.TRANSFER_KEY, value);
        return this;
    }

    public FileTransferSelection transferKeyLike(String... value) {
        addLike(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.TRANSFER_KEY, value);
        return this;
    }

    public FileTransferSelection transferKeyContains(String... value) {
        addContains(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.TRANSFER_KEY, value);
        return this;
    }

    public FileTransferSelection transferKeyStartsWith(String... value) {
        addStartsWith(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.TRANSFER_KEY, value);
        return this;
    }

    public FileTransferSelection transferKeyEndsWith(String... value) {
        addEndsWith(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.TRANSFER_KEY, value);
        return this;
    }

    public FileTransferSelection type(RemoteObjectType... value) {
        addEquals(FileTransferColumns.TYPE, value);
        return this;
    }

    public FileTransferSelection typeNot(RemoteObjectType... value) {
        addNotEquals(FileTransferColumns.TYPE, value);
        return this;
    }


    public FileTransferSelection serverPath(String... value) {
        addEquals(FileTransferColumns.SERVER_PATH, value);
        return this;
    }

    public FileTransferSelection serverPathNot(String... value) {
        addNotEquals(FileTransferColumns.SERVER_PATH, value);
        return this;
    }

    public FileTransferSelection serverPathLike(String... value) {
        addLike(FileTransferColumns.SERVER_PATH, value);
        return this;
    }

    public FileTransferSelection serverPathContains(String... value) {
        addContains(FileTransferColumns.SERVER_PATH, value);
        return this;
    }

    public FileTransferSelection serverPathStartsWith(String... value) {
        addStartsWith(FileTransferColumns.SERVER_PATH, value);
        return this;
    }

    public FileTransferSelection serverPathEndsWith(String... value) {
        addEndsWith(FileTransferColumns.SERVER_PATH, value);
        return this;
    }

    public FileTransferSelection realServerPath(String... value) {
        addEquals(FileTransferColumns.REAL_SERVER_PATH, value);
        return this;
    }

    public FileTransferSelection realServerPathNot(String... value) {
        addNotEquals(FileTransferColumns.REAL_SERVER_PATH, value);
        return this;
    }

    public FileTransferSelection realServerPathLike(String... value) {
        addLike(FileTransferColumns.REAL_SERVER_PATH, value);
        return this;
    }

    public FileTransferSelection realServerPathContains(String... value) {
        addContains(FileTransferColumns.REAL_SERVER_PATH, value);
        return this;
    }

    public FileTransferSelection realServerPathStartsWith(String... value) {
        addStartsWith(FileTransferColumns.REAL_SERVER_PATH, value);
        return this;
    }

    public FileTransferSelection realServerPathEndsWith(String... value) {
        addEndsWith(FileTransferColumns.REAL_SERVER_PATH, value);
        return this;
    }

    public FileTransferSelection localFileName(String... value) {
        addEquals(FileTransferColumns.LOCAL_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection localFileNameNot(String... value) {
        addNotEquals(FileTransferColumns.LOCAL_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection localFileNameLike(String... value) {
        addLike(FileTransferColumns.LOCAL_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection localFileNameContains(String... value) {
        addContains(FileTransferColumns.LOCAL_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection localFileNameStartsWith(String... value) {
        addStartsWith(FileTransferColumns.LOCAL_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection localFileNameEndsWith(String... value) {
        addEndsWith(FileTransferColumns.LOCAL_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection realLocalFileName(String... value) {
        addEquals(FileTransferColumns.REAL_LOCAL_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection realLocalFileNameNot(String... value) {
        addNotEquals(FileTransferColumns.REAL_LOCAL_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection realLocalFileNameLike(String... value) {
        addLike(FileTransferColumns.REAL_LOCAL_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection realLocalFileNameContains(String... value) {
        addContains(FileTransferColumns.REAL_LOCAL_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection realLocalFileNameStartsWith(String... value) {
        addStartsWith(FileTransferColumns.REAL_LOCAL_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection realLocalFileNameEndsWith(String... value) {
        addEndsWith(FileTransferColumns.REAL_LOCAL_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection savedFileName(String... value) {
        addEquals(FileTransferColumns.SAVED_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection savedFileNameNot(String... value) {
        addNotEquals(FileTransferColumns.SAVED_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection savedFileNameLike(String... value) {
        addLike(FileTransferColumns.SAVED_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection savedFileNameContains(String... value) {
        addContains(FileTransferColumns.SAVED_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection savedFileNameStartsWith(String... value) {
        addStartsWith(FileTransferColumns.SAVED_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection savedFileNameEndsWith(String... value) {
        addEndsWith(FileTransferColumns.SAVED_FILE_NAME, value);
        return this;
    }

    public FileTransferSelection fileInCache(boolean value) {
        addEquals(FileTransferColumns.FILE_IN_CACHE, toObjectArray(value));
        return this;
    }

    public FileTransferSelection contentType(String... value) {
        addEquals(FileTransferColumns.CONTENT_TYPE, value);
        return this;
    }

    public FileTransferSelection contentTypeNot(String... value) {
        addNotEquals(FileTransferColumns.CONTENT_TYPE, value);
        return this;
    }

    public FileTransferSelection contentTypeLike(String... value) {
        addLike(FileTransferColumns.CONTENT_TYPE, value);
        return this;
    }

    public FileTransferSelection contentTypeContains(String... value) {
        addContains(FileTransferColumns.CONTENT_TYPE, value);
        return this;
    }

    public FileTransferSelection contentTypeStartsWith(String... value) {
        addStartsWith(FileTransferColumns.CONTENT_TYPE, value);
        return this;
    }

    public FileTransferSelection contentTypeEndsWith(String... value) {
        addEndsWith(FileTransferColumns.CONTENT_TYPE, value);
        return this;
    }

    public FileTransferSelection lastModified(String... value) {
        addEquals(FileTransferColumns.LAST_MODIFIED, value);
        return this;
    }

    public FileTransferSelection lastModifiedNot(String... value) {
        addNotEquals(FileTransferColumns.LAST_MODIFIED, value);
        return this;
    }

    public FileTransferSelection lastModifiedLike(String... value) {
        addLike(FileTransferColumns.LAST_MODIFIED, value);
        return this;
    }

    public FileTransferSelection lastModifiedContains(String... value) {
        addContains(FileTransferColumns.LAST_MODIFIED, value);
        return this;
    }

    public FileTransferSelection lastModifiedStartsWith(String... value) {
        addStartsWith(FileTransferColumns.LAST_MODIFIED, value);
        return this;
    }

    public FileTransferSelection lastModifiedEndsWith(String... value) {
        addEndsWith(FileTransferColumns.LAST_MODIFIED, value);
        return this;
    }

    public FileTransferSelection status(DownloadStatusType... value) {
        addEquals(FileTransferColumns.STATUS, value);
        return this;
    }

    public FileTransferSelection statusNot(DownloadStatusType... value) {
        addNotEquals(FileTransferColumns.STATUS, value);
        return this;
    }


    public FileTransferSelection startTimestamp(Long... value) {
        addEquals(FileTransferColumns.START_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection startTimestampNot(Long... value) {
        addNotEquals(FileTransferColumns.START_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection startTimestampGt(long value) {
        addGreaterThan(FileTransferColumns.START_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection startTimestampGtEq(long value) {
        addGreaterThanOrEquals(FileTransferColumns.START_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection startTimestampLt(long value) {
        addLessThan(FileTransferColumns.START_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection startTimestampLtEq(long value) {
        addLessThanOrEquals(FileTransferColumns.START_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection endTimestamp(Long... value) {
        addEquals(FileTransferColumns.END_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection endTimestampNot(Long... value) {
        addNotEquals(FileTransferColumns.END_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection endTimestampGt(long value) {
        addGreaterThan(FileTransferColumns.END_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection endTimestampGtEq(long value) {
        addGreaterThanOrEquals(FileTransferColumns.END_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection endTimestampLt(long value) {
        addLessThan(FileTransferColumns.END_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection endTimestampLtEq(long value) {
        addLessThanOrEquals(FileTransferColumns.END_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection totalSize(Long... value) {
        addEquals(FileTransferColumns.TOTAL_SIZE, value);
        return this;
    }

    public FileTransferSelection totalSizeNot(Long... value) {
        addNotEquals(FileTransferColumns.TOTAL_SIZE, value);
        return this;
    }

    public FileTransferSelection totalSizeGt(long value) {
        addGreaterThan(FileTransferColumns.TOTAL_SIZE, value);
        return this;
    }

    public FileTransferSelection totalSizeGtEq(long value) {
        addGreaterThanOrEquals(FileTransferColumns.TOTAL_SIZE, value);
        return this;
    }

    public FileTransferSelection totalSizeLt(long value) {
        addLessThan(FileTransferColumns.TOTAL_SIZE, value);
        return this;
    }

    public FileTransferSelection totalSizeLtEq(long value) {
        addLessThanOrEquals(FileTransferColumns.TOTAL_SIZE, value);
        return this;
    }

    public FileTransferSelection transferredSize(Long... value) {
        addEquals(FileTransferColumns.TRANSFERRED_SIZE, value);
        return this;
    }

    public FileTransferSelection transferredSizeNot(Long... value) {
        addNotEquals(FileTransferColumns.TRANSFERRED_SIZE, value);
        return this;
    }

    public FileTransferSelection transferredSizeGt(long value) {
        addGreaterThan(FileTransferColumns.TRANSFERRED_SIZE, value);
        return this;
    }

    public FileTransferSelection transferredSizeGtEq(long value) {
        addGreaterThanOrEquals(FileTransferColumns.TRANSFERRED_SIZE, value);
        return this;
    }

    public FileTransferSelection transferredSizeLt(long value) {
        addLessThan(FileTransferColumns.TRANSFERRED_SIZE, value);
        return this;
    }

    public FileTransferSelection transferredSizeLtEq(long value) {
        addLessThanOrEquals(FileTransferColumns.TRANSFERRED_SIZE, value);
        return this;
    }

    public FileTransferSelection waitToConfirm(boolean value) {
        addEquals(FileTransferColumns.WAIT_TO_CONFIRM, toObjectArray(value));
        return this;
    }

    public FileTransferSelection actionsAfterDownload(String... value) {
        addEquals(FileTransferColumns.ACTIONS_AFTER_DOWNLOAD, value);
        return this;
    }

    public FileTransferSelection actionsAfterDownloadNot(String... value) {
        addNotEquals(FileTransferColumns.ACTIONS_AFTER_DOWNLOAD, value);
        return this;
    }

    public FileTransferSelection actionsAfterDownloadLike(String... value) {
        addLike(FileTransferColumns.ACTIONS_AFTER_DOWNLOAD, value);
        return this;
    }

    public FileTransferSelection actionsAfterDownloadContains(String... value) {
        addContains(FileTransferColumns.ACTIONS_AFTER_DOWNLOAD, value);
        return this;
    }

    public FileTransferSelection actionsAfterDownloadStartsWith(String... value) {
        addStartsWith(FileTransferColumns.ACTIONS_AFTER_DOWNLOAD, value);
        return this;
    }

    public FileTransferSelection actionsAfterDownloadEndsWith(String... value) {
        addEndsWith(FileTransferColumns.ACTIONS_AFTER_DOWNLOAD, value);
        return this;
    }

    public FileTransferSelection userId(String... value) {
        addEquals(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.USER_ID, value);
        return this;
    }

    public FileTransferSelection userIdNot(String... value) {
        addNotEquals(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.USER_ID, value);
        return this;
    }

    public FileTransferSelection userIdLike(String... value) {
        addLike(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.USER_ID, value);
        return this;
    }

    public FileTransferSelection userIdContains(String... value) {
        addContains(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.USER_ID, value);
        return this;
    }

    public FileTransferSelection userIdStartsWith(String... value) {
        addStartsWith(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.USER_ID, value);
        return this;
    }

    public FileTransferSelection userIdEndsWith(String... value) {
        addEndsWith(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.USER_ID, value);
        return this;
    }

    public FileTransferSelection computerId(int... value) {
        addEquals(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public FileTransferSelection computerIdNot(int... value) {
        addNotEquals(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public FileTransferSelection computerIdGt(int value) {
        addGreaterThan(FileTransferColumns.COMPUTER_ID, value);
        return this;
    }

    public FileTransferSelection computerIdGtEq(int value) {
        addGreaterThanOrEquals(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.COMPUTER_ID, value);
        return this;
    }

    public FileTransferSelection computerIdLt(int value) {
        addLessThan(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.COMPUTER_ID, value);
        return this;
    }

    public FileTransferSelection computerIdLtEq(int value) {
        addLessThanOrEquals(FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.COMPUTER_ID, value);
        return this;
    }

    public FileTransferSelection dgLocalPath(String... value) {
        addEquals(FileTransferColumns.DG_LOCAL_PATH, value);
        return this;
    }

    public FileTransferSelection dgLocalPathNot(String... value) {
        addNotEquals(FileTransferColumns.DG_LOCAL_PATH, value);
        return this;
    }

    public FileTransferSelection dgLocalPathLike(String... value) {
        addLike(FileTransferColumns.DG_LOCAL_PATH, value);
        return this;
    }

    public FileTransferSelection dgLocalPathContains(String... value) {
        addContains(FileTransferColumns.DG_LOCAL_PATH, value);
        return this;
    }

    public FileTransferSelection dgLocalPathStartsWith(String... value) {
        addStartsWith(FileTransferColumns.DG_LOCAL_PATH, value);
        return this;
    }

    public FileTransferSelection dgLocalPathEndsWith(String... value) {
        addEndsWith(FileTransferColumns.DG_LOCAL_PATH, value);
        return this;
    }

    public FileTransferSelection dgFromAnotherApp(boolean value) {
        addEquals(FileTransferColumns.DG_FROM_ANOTHER_APP, toObjectArray(value));
        return this;
    }

    public FileTransferSelection dgNotificationType(int... value) {
        addEquals(FileTransferColumns.DG_NOTIFICATION_TYPE, toObjectArray(value));
        return this;
    }

    public FileTransferSelection dgNotificationTypeNot(int... value) {
        addNotEquals(FileTransferColumns.DG_NOTIFICATION_TYPE, toObjectArray(value));
        return this;
    }

    public FileTransferSelection dgNotificationTypeGt(int value) {
        addGreaterThan(FileTransferColumns.DG_NOTIFICATION_TYPE, value);
        return this;
    }

    public FileTransferSelection dgNotificationTypeGtEq(int value) {
        addGreaterThanOrEquals(FileTransferColumns.DG_NOTIFICATION_TYPE, value);
        return this;
    }

    public FileTransferSelection dgNotificationTypeLt(int value) {
        addLessThan(FileTransferColumns.DG_NOTIFICATION_TYPE, value);
        return this;
    }

    public FileTransferSelection dgNotificationTypeLtEq(int value) {
        addLessThanOrEquals(FileTransferColumns.DG_NOTIFICATION_TYPE, value);
        return this;
    }


    public FileTransferSelection dgStartTimestamp(Long... value) {
        addEquals(FileTransferColumns.DG_START_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection dgStartTimestampNot(Long... value) {
        addNotEquals(FileTransferColumns.DG_START_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection dgStartTimestampGt(long value) {
        addGreaterThan(FileTransferColumns.DG_START_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection dgStartTimestampGtEq(long value) {
        addGreaterThanOrEquals(FileTransferColumns.DG_START_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection dgStartTimestampLt(long value) {
        addLessThan(FileTransferColumns.DG_START_TIMESTAMP, value);
        return this;
    }

    public FileTransferSelection dgStartTimestampLtEq(long value) {
        addLessThanOrEquals(FileTransferColumns.DG_START_TIMESTAMP, value);
        return this;
    }
}
