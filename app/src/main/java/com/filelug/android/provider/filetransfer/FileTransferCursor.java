package com.filelug.android.provider.filetransfer;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code file_transfer} table.
 */
public class FileTransferCursor extends AbstractCursor implements FileTransferModel {
    public FileTransferCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(FileTransferColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code group_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getGroupId() {
        String res = getStringOrNull(FileTransferColumns.GROUP_ID);
        if (res == null)
            throw new NullPointerException("The value of 'group_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code transfer_key} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getTransferKey() {
        String res = getStringOrNull(FileTransferColumns.TRANSFER_KEY);
        if (res == null)
            throw new NullPointerException("The value of 'transfer_key' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code type} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public RemoteObjectType getType() {
        Integer intValue = getIntegerOrNull(FileTransferColumns.TYPE);
        if (intValue == null)
            throw new NullPointerException("The value of 'type' in the database was null, which is not allowed according to the model definition");
        return RemoteObjectType.values()[intValue];
    }

    /**
     * Get the {@code server_path} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getServerPath() {
        String res = getStringOrNull(FileTransferColumns.SERVER_PATH);
        if (res == null)
            throw new NullPointerException("The value of 'server_path' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code real_server_path} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getRealServerPath() {
        String res = getStringOrNull(FileTransferColumns.REAL_SERVER_PATH);
        return res;
    }

    /**
     * Get the {@code local_file_name} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getLocalFileName() {
        String res = getStringOrNull(FileTransferColumns.LOCAL_FILE_NAME);
        if (res == null)
            throw new NullPointerException("The value of 'local_file_name' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code real_local_file_name} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getRealLocalFileName() {
        String res = getStringOrNull(FileTransferColumns.REAL_LOCAL_FILE_NAME);
        return res;
    }

    /**
     * Get the {@code saved_file_name} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getSavedFileName() {
        String res = getStringOrNull(FileTransferColumns.SAVED_FILE_NAME);
        return res;
    }

    /**
     * Get the {@code file_in_cache} value.
     */
    public boolean getFileInCache() {
        Boolean res = getBooleanOrNull(FileTransferColumns.FILE_IN_CACHE);
        if (res == null)
            throw new NullPointerException("The value of 'file_in_cache' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code content_type} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getContentType() {
        String res = getStringOrNull(FileTransferColumns.CONTENT_TYPE);
        return res;
    }

    /**
     * Get the {@code last_modified} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getLastModified() {
        String res = getStringOrNull(FileTransferColumns.LAST_MODIFIED);
        if (res == null)
            throw new NullPointerException("The value of 'last_modified' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code status} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public DownloadStatusType getStatus() {
        Integer intValue = getIntegerOrNull(FileTransferColumns.STATUS);
        if (intValue == null)
            throw new NullPointerException("The value of 'status' in the database was null, which is not allowed according to the model definition");
        return DownloadStatusType.values()[intValue];
    }

    /**
     * Get the {@code start_timestamp} value.
     * Can be {@code null}.
     */
    @Nullable
    public Long getStartTimestamp() {
        Long res = getLongOrNull(FileTransferColumns.START_TIMESTAMP);
        return res;
    }

    /**
     * Get the {@code end_timestamp} value.
     * Can be {@code null}.
     */
    @Nullable
    public Long getEndTimestamp() {
        Long res = getLongOrNull(FileTransferColumns.END_TIMESTAMP);
        return res;
    }

    /**
     * Get the {@code total_size} value.
     * Can be {@code null}.
     */
    @Nullable
    public Long getTotalSize() {
        Long res = getLongOrNull(FileTransferColumns.TOTAL_SIZE);
        return res;
    }

    /**
     * Get the {@code transferred_size} value.
     * Can be {@code null}.
     */
    @Nullable
    public Long getTransferredSize() {
        Long res = getLongOrNull(FileTransferColumns.TRANSFERRED_SIZE);
        return res;
    }

    /**
     * Get the {@code wait_to_confirm} value.
     */
    public boolean getWaitToConfirm() {
        Boolean res = getBooleanOrNull(FileTransferColumns.WAIT_TO_CONFIRM);
        if (res == null)
            throw new NullPointerException("The value of 'wait_to_confirm' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code actions_after_download} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getActionsAfterDownload() {
        String res = getStringOrNull(FileTransferColumns.ACTIONS_AFTER_DOWNLOAD);
        return res;
    }

    /**
     * Get the {@code user_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getUserId() {
        String res = getStringOrNull(FileTransferColumns.USER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'user_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code computer_id} value.
     */
    public int getComputerId() {
        Integer res = getIntegerOrNull(FileTransferColumns.COMPUTER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'computer_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code dg_local_path} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getDGLocalPath() {
        String res = getStringOrNull(FileTransferColumns.ALIAS_LOCAL_PATH);
        if (res == null)
            throw new NullPointerException("The value of 'dg_local_path' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code dg_from_another_app} value.
     */
    public boolean getDGFromAnotherApp() {
        Boolean res = getBooleanOrNull(FileTransferColumns.ALIAS_FROM_ANOTHER_APP);
        if (res == null)
            throw new NullPointerException("The value of 'dg_from_another_app' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code dg_notification_type} value.
     */
    public int getDGNotificationType() {
        Integer res = getIntegerOrNull(FileTransferColumns.ALIAS_NOTIFICATION_TYPE);
        if (res == null)
            throw new NullPointerException("The value of 'dg_notification_type' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code dg_start_timestamp} value.
     * Can be {@code null}.
     */
    @Nullable
    public Long getDGStartTimestamp() {
        Long res = getLongOrNull(FileTransferColumns.ALIAS_START_TIMESTAMP);
        return res;
    }

}
