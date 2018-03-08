package com.filelug.android.provider.filetransfer;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code file_transfer} table.
 */
public class FileTransferContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return FileTransferColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable FileTransferSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public FileTransferContentValues putGroupId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("groupId must not be null");
        mContentValues.put(FileTransferColumns.GROUP_ID, value);
        return this;
    }


    public FileTransferContentValues putTransferKey(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("transferKey must not be null");
        mContentValues.put(FileTransferColumns.TRANSFER_KEY, value);
        return this;
    }


    public FileTransferContentValues putType(@NonNull RemoteObjectType value) {
        if (value == null) throw new IllegalArgumentException("type must not be null");
        mContentValues.put(FileTransferColumns.TYPE, value.ordinal());
        return this;
    }


    public FileTransferContentValues putServerPath(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("serverPath must not be null");
        mContentValues.put(FileTransferColumns.SERVER_PATH, value);
        return this;
    }


    public FileTransferContentValues putRealServerPath(@Nullable String value) {
        mContentValues.put(FileTransferColumns.REAL_SERVER_PATH, value);
        return this;
    }

    public FileTransferContentValues putRealServerPathNull() {
        mContentValues.putNull(FileTransferColumns.REAL_SERVER_PATH);
        return this;
    }


    public FileTransferContentValues putLocalFileName(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("localFileName must not be null");
        mContentValues.put(FileTransferColumns.LOCAL_FILE_NAME, value);
        return this;
    }


    public FileTransferContentValues putRealLocalFileName(@Nullable String value) {
        mContentValues.put(FileTransferColumns.REAL_LOCAL_FILE_NAME, value);
        return this;
    }

    public FileTransferContentValues putRealLocalFileNameNull() {
        mContentValues.putNull(FileTransferColumns.REAL_LOCAL_FILE_NAME);
        return this;
    }


    public FileTransferContentValues putSavedFileName(@Nullable String value) {
        mContentValues.put(FileTransferColumns.SAVED_FILE_NAME, value);
        return this;
    }

    public FileTransferContentValues putSavedFileNameNull() {
        mContentValues.putNull(FileTransferColumns.SAVED_FILE_NAME);
        return this;
    }


    public FileTransferContentValues putFileInCache(boolean value) {
        mContentValues.put(FileTransferColumns.FILE_IN_CACHE, value);
        return this;
    }


    public FileTransferContentValues putContentType(@Nullable String value) {
        mContentValues.put(FileTransferColumns.CONTENT_TYPE, value);
        return this;
    }

    public FileTransferContentValues putContentTypeNull() {
        mContentValues.putNull(FileTransferColumns.CONTENT_TYPE);
        return this;
    }

    public FileTransferContentValues putLastModified(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("lastModified must not be null");
        mContentValues.put(FileTransferColumns.LAST_MODIFIED, value);
        return this;
    }


    public FileTransferContentValues putStatus(@NonNull DownloadStatusType value) {
        if (value == null) throw new IllegalArgumentException("status must not be null");
        mContentValues.put(FileTransferColumns.STATUS, value.ordinal());
        return this;
    }


    public FileTransferContentValues putStartTimestamp(@Nullable Long value) {
        mContentValues.put(FileTransferColumns.START_TIMESTAMP, value);
        return this;
    }

    public FileTransferContentValues putStartTimestampNull() {
        mContentValues.putNull(FileTransferColumns.START_TIMESTAMP);
        return this;
    }

    public FileTransferContentValues putEndTimestamp(@Nullable Long value) {
        mContentValues.put(FileTransferColumns.END_TIMESTAMP, value);
        return this;
    }

    public FileTransferContentValues putEndTimestampNull() {
        mContentValues.putNull(FileTransferColumns.END_TIMESTAMP);
        return this;
    }

    public FileTransferContentValues putTotalSize(@Nullable Long value) {
        mContentValues.put(FileTransferColumns.TOTAL_SIZE, value);
        return this;
    }

    public FileTransferContentValues putTotalSizeNull() {
        mContentValues.putNull(FileTransferColumns.TOTAL_SIZE);
        return this;
    }

    public FileTransferContentValues putTransferredSize(@Nullable Long value) {
        mContentValues.put(FileTransferColumns.TRANSFERRED_SIZE, value);
        return this;
    }

    public FileTransferContentValues putTransferredSizeNull() {
        mContentValues.putNull(FileTransferColumns.TRANSFERRED_SIZE);
        return this;
    }


    public FileTransferContentValues putWaitToConfirm(boolean value) {
        mContentValues.put(FileTransferColumns.WAIT_TO_CONFIRM, value);
        return this;
    }


    public FileTransferContentValues putActionsAfterDownload(@Nullable String value) {
        mContentValues.put(FileTransferColumns.ACTIONS_AFTER_DOWNLOAD, value);
        return this;
    }

    public FileTransferContentValues putActionsAfterDownloadNull() {
        mContentValues.putNull(FileTransferColumns.ACTIONS_AFTER_DOWNLOAD);
        return this;
    }


    public FileTransferContentValues putUserId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("userId must not be null");
        mContentValues.put(FileTransferColumns.USER_ID, value);
        return this;
    }


    public FileTransferContentValues putComputerId(int value) {
        mContentValues.put(FileTransferColumns.COMPUTER_ID, value);
        return this;
    }

}
