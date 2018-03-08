package com.filelug.android.provider.downloadgroup;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.filelug.android.provider.base.AbstractSelection;

/**
 * Selection for the {@code download_group} table.
 */
public class DownloadGroupSelection extends AbstractSelection<DownloadGroupSelection> {
    @Override
    protected Uri baseUri() {
        return DownloadGroupColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code DownloadGroupCursor} object, which is positioned before the first entry, or null.
     */
    public DownloadGroupCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new DownloadGroupCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null)}.
     */
    public DownloadGroupCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null)}.
     */
    public DownloadGroupCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public DownloadGroupSelection id(long... value) {
        addEquals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns._ID, toObjectArray(value));
        return this;
    }

    public DownloadGroupSelection groupId(String... value) {
        addEquals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.GROUP_ID, value);
        return this;
    }

    public DownloadGroupSelection groupIdNot(String... value) {
        addNotEquals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.GROUP_ID, value);
        return this;
    }

    public DownloadGroupSelection groupIdLike(String... value) {
        addLike(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.GROUP_ID, value);
        return this;
    }

    public DownloadGroupSelection groupIdContains(String... value) {
        addContains(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.GROUP_ID, value);
        return this;
    }

    public DownloadGroupSelection groupIdStartsWith(String... value) {
        addStartsWith(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.GROUP_ID, value);
        return this;
    }

    public DownloadGroupSelection groupIdEndsWith(String... value) {
        addEndsWith(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.GROUP_ID, value);
        return this;
    }

    public DownloadGroupSelection fromAnotherApp(boolean value) {
        addEquals(DownloadGroupColumns.FROM_ANOTHER_APP, toObjectArray(value));
        return this;
    }

    public DownloadGroupSelection startTimestamp(long... value) {
        addEquals(DownloadGroupColumns.START_TIMESTAMP, toObjectArray(value));
        return this;
    }

    public DownloadGroupSelection startTimestampNot(long... value) {
        addNotEquals(DownloadGroupColumns.START_TIMESTAMP, toObjectArray(value));
        return this;
    }

    public DownloadGroupSelection startTimestampGt(long value) {
        addGreaterThan(DownloadGroupColumns.START_TIMESTAMP, value);
        return this;
    }

    public DownloadGroupSelection startTimestampGtEq(long value) {
        addGreaterThanOrEquals(DownloadGroupColumns.START_TIMESTAMP, value);
        return this;
    }

    public DownloadGroupSelection startTimestampLt(long value) {
        addLessThan(DownloadGroupColumns.START_TIMESTAMP, value);
        return this;
    }

    public DownloadGroupSelection startTimestampLtEq(long value) {
        addLessThanOrEquals(DownloadGroupColumns.START_TIMESTAMP, value);
        return this;
    }

    public DownloadGroupSelection localPath(String... value) {
        addEquals(DownloadGroupColumns.LOCAL_PATH, value);
        return this;
    }

    public DownloadGroupSelection localPathNot(String... value) {
        addNotEquals(DownloadGroupColumns.LOCAL_PATH, value);
        return this;
    }

    public DownloadGroupSelection localPathLike(String... value) {
        addLike(DownloadGroupColumns.LOCAL_PATH, value);
        return this;
    }

    public DownloadGroupSelection localPathContains(String... value) {
        addContains(DownloadGroupColumns.LOCAL_PATH, value);
        return this;
    }

    public DownloadGroupSelection localPathStartsWith(String... value) {
        addStartsWith(DownloadGroupColumns.LOCAL_PATH, value);
        return this;
    }

    public DownloadGroupSelection localPathEndsWith(String... value) {
        addEndsWith(DownloadGroupColumns.LOCAL_PATH, value);
        return this;
    }

    public DownloadGroupSelection subdirectoryType(int... value) {
        addEquals(DownloadGroupColumns.SUBDIRECTORY_TYPE, toObjectArray(value));
        return this;
    }

    public DownloadGroupSelection subdirectoryTypeNot(int... value) {
        addNotEquals(DownloadGroupColumns.SUBDIRECTORY_TYPE, toObjectArray(value));
        return this;
    }

    public DownloadGroupSelection subdirectoryTypeGt(int value) {
        addGreaterThan(DownloadGroupColumns.SUBDIRECTORY_TYPE, value);
        return this;
    }

    public DownloadGroupSelection subdirectoryTypeGtEq(int value) {
        addGreaterThanOrEquals(DownloadGroupColumns.SUBDIRECTORY_TYPE, value);
        return this;
    }

    public DownloadGroupSelection subdirectoryTypeLt(int value) {
        addLessThan(DownloadGroupColumns.SUBDIRECTORY_TYPE, value);
        return this;
    }

    public DownloadGroupSelection subdirectoryTypeLtEq(int value) {
        addLessThanOrEquals(DownloadGroupColumns.SUBDIRECTORY_TYPE, value);
        return this;
    }

    public DownloadGroupSelection subdirectoryValue(String... value) {
        addEquals(DownloadGroupColumns.SUBDIRECTORY_VALUE, value);
        return this;
    }

    public DownloadGroupSelection subdirectoryValueNot(String... value) {
        addNotEquals(DownloadGroupColumns.SUBDIRECTORY_VALUE, value);
        return this;
    }

    public DownloadGroupSelection subdirectoryValueLike(String... value) {
        addLike(DownloadGroupColumns.SUBDIRECTORY_VALUE, value);
        return this;
    }

    public DownloadGroupSelection subdirectoryValueContains(String... value) {
        addContains(DownloadGroupColumns.SUBDIRECTORY_VALUE, value);
        return this;
    }

    public DownloadGroupSelection subdirectoryValueStartsWith(String... value) {
        addStartsWith(DownloadGroupColumns.SUBDIRECTORY_VALUE, value);
        return this;
    }

    public DownloadGroupSelection subdirectoryValueEndsWith(String... value) {
        addEndsWith(DownloadGroupColumns.SUBDIRECTORY_VALUE, value);
        return this;
    }

    public DownloadGroupSelection descriptionType(int... value) {
        addEquals(DownloadGroupColumns.DESCRIPTION_TYPE, toObjectArray(value));
        return this;
    }

    public DownloadGroupSelection descriptionTypeNot(int... value) {
        addNotEquals(DownloadGroupColumns.DESCRIPTION_TYPE, toObjectArray(value));
        return this;
    }

    public DownloadGroupSelection descriptionTypeGt(int value) {
        addGreaterThan(DownloadGroupColumns.DESCRIPTION_TYPE, value);
        return this;
    }

    public DownloadGroupSelection descriptionTypeGtEq(int value) {
        addGreaterThanOrEquals(DownloadGroupColumns.DESCRIPTION_TYPE, value);
        return this;
    }

    public DownloadGroupSelection descriptionTypeLt(int value) {
        addLessThan(DownloadGroupColumns.DESCRIPTION_TYPE, value);
        return this;
    }

    public DownloadGroupSelection descriptionTypeLtEq(int value) {
        addLessThanOrEquals(DownloadGroupColumns.DESCRIPTION_TYPE, value);
        return this;
    }

    public DownloadGroupSelection descriptionValue(String... value) {
        addEquals(DownloadGroupColumns.DESCRIPTION_VALUE, value);
        return this;
    }

    public DownloadGroupSelection descriptionValueNot(String... value) {
        addNotEquals(DownloadGroupColumns.DESCRIPTION_VALUE, value);
        return this;
    }

    public DownloadGroupSelection descriptionValueLike(String... value) {
        addLike(DownloadGroupColumns.DESCRIPTION_VALUE, value);
        return this;
    }

    public DownloadGroupSelection descriptionValueContains(String... value) {
        addContains(DownloadGroupColumns.DESCRIPTION_VALUE, value);
        return this;
    }

    public DownloadGroupSelection descriptionValueStartsWith(String... value) {
        addStartsWith(DownloadGroupColumns.DESCRIPTION_VALUE, value);
        return this;
    }

    public DownloadGroupSelection descriptionValueEndsWith(String... value) {
        addEndsWith(DownloadGroupColumns.DESCRIPTION_VALUE, value);
        return this;
    }

    public DownloadGroupSelection notificationType(int... value) {
        addEquals(DownloadGroupColumns.NOTIFICATION_TYPE, toObjectArray(value));
        return this;
    }

    public DownloadGroupSelection notificationTypeNot(int... value) {
        addNotEquals(DownloadGroupColumns.NOTIFICATION_TYPE, toObjectArray(value));
        return this;
    }

    public DownloadGroupSelection notificationTypeGt(int value) {
        addGreaterThan(DownloadGroupColumns.NOTIFICATION_TYPE, value);
        return this;
    }

    public DownloadGroupSelection notificationTypeGtEq(int value) {
        addGreaterThanOrEquals(DownloadGroupColumns.NOTIFICATION_TYPE, value);
        return this;
    }

    public DownloadGroupSelection notificationTypeLt(int value) {
        addLessThan(DownloadGroupColumns.NOTIFICATION_TYPE, value);
        return this;
    }

    public DownloadGroupSelection notificationTypeLtEq(int value) {
        addLessThanOrEquals(DownloadGroupColumns.NOTIFICATION_TYPE, value);
        return this;
    }

    public DownloadGroupSelection userId(String... value) {
        addEquals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.USER_ID, value);
        return this;
    }

    public DownloadGroupSelection userIdNot(String... value) {
        addNotEquals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.USER_ID, value);
        return this;
    }

    public DownloadGroupSelection userIdLike(String... value) {
        addLike(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.USER_ID, value);
        return this;
    }

    public DownloadGroupSelection userIdContains(String... value) {
        addContains(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.USER_ID, value);
        return this;
    }

    public DownloadGroupSelection userIdStartsWith(String... value) {
        addStartsWith(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.USER_ID, value);
        return this;
    }

    public DownloadGroupSelection userIdEndsWith(String... value) {
        addEndsWith(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.USER_ID, value);
        return this;
    }

    public DownloadGroupSelection computerId(int... value) {
        addEquals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public DownloadGroupSelection computerIdNot(int... value) {
        addNotEquals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.COMPUTER_ID, toObjectArray(value));
        return this;
    }

    public DownloadGroupSelection computerIdGt(int value) {
        addGreaterThan(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.COMPUTER_ID, value);
        return this;
    }

    public DownloadGroupSelection computerIdGtEq(int value) {
        addGreaterThanOrEquals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.COMPUTER_ID, value);
        return this;
    }

    public DownloadGroupSelection computerIdLt(int value) {
        addLessThan(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.COMPUTER_ID, value);
        return this;
    }

    public DownloadGroupSelection computerIdLtEq(int value) {
        addLessThanOrEquals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.COMPUTER_ID, value);
        return this;
    }
}
