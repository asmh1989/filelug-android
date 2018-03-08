package com.filelug.android.provider.uploadgroup;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.filelug.android.provider.base.AbstractSelection;

/**
 * Selection for the {@code upload_group} table.
 */
public class UploadGroupSelection extends AbstractSelection<UploadGroupSelection> {
    @Override
    protected Uri baseUri() {
        return UploadGroupColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code UploadGroupCursor} object, which is positioned before the first entry, or null.
     */
    public UploadGroupCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new UploadGroupCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null)}.
     */
    public UploadGroupCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null)}.
     */
    public UploadGroupCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public UploadGroupSelection id(long... value) {
        addEquals(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns._ID, toObjectArray(value));
        return this;
    }

    public UploadGroupSelection groupId(String... value) {
        addEquals(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.GROUP_ID, value);
        return this;
    }

    public UploadGroupSelection groupIdNot(String... value) {
        addNotEquals(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.GROUP_ID, value);
        return this;
    }

    public UploadGroupSelection groupIdLike(String... value) {
        addLike(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.GROUP_ID, value);
        return this;
    }

    public UploadGroupSelection groupIdContains(String... value) {
        addContains(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.GROUP_ID, value);
        return this;
    }

    public UploadGroupSelection groupIdStartsWith(String... value) {
        addStartsWith(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.GROUP_ID, value);
        return this;
    }

    public UploadGroupSelection groupIdEndsWith(String... value) {
        addEndsWith(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.GROUP_ID, value);
        return this;
    }

    public UploadGroupSelection fromAnotherApp(boolean value) {
        addEquals(UploadGroupColumns.FROM_ANOTHER_APP, toObjectArray(value));
        return this;
    }

    public UploadGroupSelection startTimestamp(long... value) {
        addEquals(UploadGroupColumns.START_TIMESTAMP, toObjectArray(value));
        return this;
    }

    public UploadGroupSelection startTimestampNot(long... value) {
        addNotEquals(UploadGroupColumns.START_TIMESTAMP, toObjectArray(value));
        return this;
    }

    public UploadGroupSelection startTimestampGt(long value) {
        addGreaterThan(UploadGroupColumns.START_TIMESTAMP, value);
        return this;
    }

    public UploadGroupSelection startTimestampGtEq(long value) {
        addGreaterThanOrEquals(UploadGroupColumns.START_TIMESTAMP, value);
        return this;
    }

    public UploadGroupSelection startTimestampLt(long value) {
        addLessThan(UploadGroupColumns.START_TIMESTAMP, value);
        return this;
    }

    public UploadGroupSelection startTimestampLtEq(long value) {
        addLessThanOrEquals(UploadGroupColumns.START_TIMESTAMP, value);
        return this;
    }

    public UploadGroupSelection uploadDirectory(String... value) {
        addEquals(UploadGroupColumns.UPLOAD_DIRECTORY, value);
        return this;
    }

    public UploadGroupSelection uploadDirectoryNot(String... value) {
        addNotEquals(UploadGroupColumns.UPLOAD_DIRECTORY, value);
        return this;
    }

    public UploadGroupSelection uploadDirectoryLike(String... value) {
        addLike(UploadGroupColumns.UPLOAD_DIRECTORY, value);
        return this;
    }

    public UploadGroupSelection uploadDirectoryContains(String... value) {
        addContains(UploadGroupColumns.UPLOAD_DIRECTORY, value);
        return this;
    }

    public UploadGroupSelection uploadDirectoryStartsWith(String... value) {
        addStartsWith(UploadGroupColumns.UPLOAD_DIRECTORY, value);
        return this;
    }

    public UploadGroupSelection uploadDirectoryEndsWith(String... value) {
        addEndsWith(UploadGroupColumns.UPLOAD_DIRECTORY, value);
        return this;
    }

    public UploadGroupSelection subdirectoryType(int... value) {
        addEquals(UploadGroupColumns.SUBDIRECTORY_TYPE, toObjectArray(value));
        return this;
    }

    public UploadGroupSelection subdirectoryTypeNot(int... value) {
        addNotEquals(UploadGroupColumns.SUBDIRECTORY_TYPE, toObjectArray(value));
        return this;
    }

    public UploadGroupSelection subdirectoryTypeGt(int value) {
        addGreaterThan(UploadGroupColumns.SUBDIRECTORY_TYPE, value);
        return this;
    }

    public UploadGroupSelection subdirectoryTypeGtEq(int value) {
        addGreaterThanOrEquals(UploadGroupColumns.SUBDIRECTORY_TYPE, value);
        return this;
    }

    public UploadGroupSelection subdirectoryTypeLt(int value) {
        addLessThan(UploadGroupColumns.SUBDIRECTORY_TYPE, value);
        return this;
    }

    public UploadGroupSelection subdirectoryTypeLtEq(int value) {
        addLessThanOrEquals(UploadGroupColumns.SUBDIRECTORY_TYPE, value);
        return this;
    }

    public UploadGroupSelection subdirectoryValue(String... value) {
        addEquals(UploadGroupColumns.SUBDIRECTORY_VALUE, value);
        return this;
    }

    public UploadGroupSelection subdirectoryValueNot(String... value) {
        addNotEquals(UploadGroupColumns.SUBDIRECTORY_VALUE, value);
        return this;
    }

    public UploadGroupSelection subdirectoryValueLike(String... value) {
        addLike(UploadGroupColumns.SUBDIRECTORY_VALUE, value);
        return this;
    }

    public UploadGroupSelection subdirectoryValueContains(String... value) {
        addContains(UploadGroupColumns.SUBDIRECTORY_VALUE, value);
        return this;
    }

    public UploadGroupSelection subdirectoryValueStartsWith(String... value) {
        addStartsWith(UploadGroupColumns.SUBDIRECTORY_VALUE, value);
        return this;
    }

    public UploadGroupSelection subdirectoryValueEndsWith(String... value) {
        addEndsWith(UploadGroupColumns.SUBDIRECTORY_VALUE, value);
        return this;
    }

    public UploadGroupSelection descriptionType(int... value) {
        addEquals(UploadGroupColumns.DESCRIPTION_TYPE, toObjectArray(value));
        return this;
    }

    public UploadGroupSelection descriptionTypeNot(int... value) {
        addNotEquals(UploadGroupColumns.DESCRIPTION_TYPE, toObjectArray(value));
        return this;
    }

    public UploadGroupSelection descriptionTypeGt(int value) {
        addGreaterThan(UploadGroupColumns.DESCRIPTION_TYPE, value);
        return this;
    }

    public UploadGroupSelection descriptionTypeGtEq(int value) {
        addGreaterThanOrEquals(UploadGroupColumns.DESCRIPTION_TYPE, value);
        return this;
    }

    public UploadGroupSelection descriptionTypeLt(int value) {
        addLessThan(UploadGroupColumns.DESCRIPTION_TYPE, value);
        return this;
    }

    public UploadGroupSelection descriptionTypeLtEq(int value) {
        addLessThanOrEquals(UploadGroupColumns.DESCRIPTION_TYPE, value);
        return this;
    }

    public UploadGroupSelection descriptionValue(String... value) {
        addEquals(UploadGroupColumns.DESCRIPTION_VALUE, value);
        return this;
    }

    public UploadGroupSelection descriptionValueNot(String... value) {
        addNotEquals(UploadGroupColumns.DESCRIPTION_VALUE, value);
        return this;
    }

    public UploadGroupSelection descriptionValueLike(String... value) {
        addLike(UploadGroupColumns.DESCRIPTION_VALUE, value);
        return this;
    }

    public UploadGroupSelection descriptionValueContains(String... value) {
        addContains(UploadGroupColumns.DESCRIPTION_VALUE, value);
        return this;
    }

    public UploadGroupSelection descriptionValueStartsWith(String... value) {
        addStartsWith(UploadGroupColumns.DESCRIPTION_VALUE, value);
        return this;
    }

    public UploadGroupSelection descriptionValueEndsWith(String... value) {
        addEndsWith(UploadGroupColumns.DESCRIPTION_VALUE, value);
        return this;
    }

    public UploadGroupSelection notificationType(int... value) {
        addEquals(UploadGroupColumns.NOTIFICATION_TYPE, toObjectArray(value));
        return this;
    }

    public UploadGroupSelection notificationTypeNot(int... value) {
        addNotEquals(UploadGroupColumns.NOTIFICATION_TYPE, toObjectArray(value));
        return this;
    }

    public UploadGroupSelection notificationTypeGt(int value) {
        addGreaterThan(UploadGroupColumns.NOTIFICATION_TYPE, value);
        return this;
    }

    public UploadGroupSelection notificationTypeGtEq(int value) {
        addGreaterThanOrEquals(UploadGroupColumns.NOTIFICATION_TYPE, value);
        return this;
    }

    public UploadGroupSelection notificationTypeLt(int value) {
        addLessThan(UploadGroupColumns.NOTIFICATION_TYPE, value);
        return this;
    }

    public UploadGroupSelection notificationTypeLtEq(int value) {
        addLessThanOrEquals(UploadGroupColumns.NOTIFICATION_TYPE, value);
        return this;
    }

    public UploadGroupSelection userId(String... value) {
        addEquals(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.USER_ID, value);
        return this;
    }

    public UploadGroupSelection userIdNot(String... value) {
        addNotEquals(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.USER_ID, value);
        return this;
    }

    public UploadGroupSelection userIdLike(String... value) {
        addLike(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.USER_ID, value);
        return this;
    }

    public UploadGroupSelection userIdContains(String... value) {
        addContains(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.USER_ID, value);
        return this;
    }

    public UploadGroupSelection userIdStartsWith(String... value) {
        addStartsWith(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.USER_ID, value);
        return this;
    }

    public UploadGroupSelection userIdEndsWith(String... value) {
        addEndsWith(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.USER_ID, value);
        return this;
    }

    public UploadGroupSelection computerId(int... value) {
        addEquals(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public UploadGroupSelection computerIdNot(int... value) {
        addNotEquals(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public UploadGroupSelection computerIdGt(int value) {
        addGreaterThan(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.COMPUTER_ID, value);
        return this;
    }

    public UploadGroupSelection computerIdGtEq(int value) {
        addGreaterThanOrEquals(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.COMPUTER_ID, value);
        return this;
    }

    public UploadGroupSelection computerIdLt(int value) {
        addLessThan(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.COMPUTER_ID, value);
        return this;
    }

    public UploadGroupSelection computerIdLtEq(int value) {
        addLessThanOrEquals(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.COMPUTER_ID, value);
        return this;
    }
}
