package com.filelug.android.provider.assetfile;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code asset_file} table.
 */
public class AssetFileCursor extends AbstractCursor implements AssetFileModel {
    public AssetFileCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(AssetFileColumns._ID);
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
        String res = getStringOrNull(AssetFileColumns.GROUP_ID);
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
        String res = getStringOrNull(AssetFileColumns.TRANSFER_KEY);
        if (res == null)
            throw new NullPointerException("The value of 'transfer_key' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code asset_url} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getAssetUrl() {
        String res = getStringOrNull(AssetFileColumns.ASSET_URL);
        if (res == null)
            throw new NullPointerException("The value of 'asset_url' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code server_file_name} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getServerFileName() {
        String res = getStringOrNull(AssetFileColumns.SERVER_FILE_NAME);
        if (res == null)
            throw new NullPointerException("The value of 'server_file_name' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code cache_file_name} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getCacheFileName() {
        String res = getStringOrNull(AssetFileColumns.CACHE_FILE_NAME);
        return res;
    }

    /**
     * Get the {@code content_type} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getContentType() {
        String res = getStringOrNull(AssetFileColumns.CONTENT_TYPE);
        if (res == null)
            throw new NullPointerException("The value of 'content_type' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code last_modified_timestamp} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public Long getLastModifiedTimestamp() {
        Long res = getLongOrNull(AssetFileColumns.LAST_MODIFIED_TIMESTAMP);
        if (res == null)
            throw new NullPointerException("The value of 'last_modified_timestamp' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code status} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public UploadStatusType getStatus() {
        Integer intValue = getIntegerOrNull(AssetFileColumns.STATUS);
        if (intValue == null)
            throw new NullPointerException("The value of 'status' in the database was null, which is not allowed according to the model definition");
        return UploadStatusType.values()[intValue];
    }

    /**
     * Get the {@code start_timestamp} value.
     * Can be {@code null}.
     */
    @Nullable
    public Long getStartTimestamp() {
        Long res = getLongOrNull(AssetFileColumns.START_TIMESTAMP);
        return res;
    }

    /**
     * Get the {@code end_timestamp} value.
     * Can be {@code null}.
     */
    @Nullable
    public Long getEndTimestamp() {
        Long res = getLongOrNull(AssetFileColumns.END_TIMESTAMP);
        return res;
    }

    /**
     * Get the {@code total_size} value.
     * Can be {@code null}.
     */
    @Nullable
    public Long getTotalSize() {
        Long res = getLongOrNull(AssetFileColumns.TOTAL_SIZE);
        return res;
    }

    /**
     * Get the {@code transferred_size} value.
     * Can be {@code null}.
     */
    @Nullable
    public Long getTransferredSize() {
        Long res = getLongOrNull(AssetFileColumns.TRANSFERRED_SIZE);
        return res;
    }

    /**
     * Get the {@code wait_to_confirm} value.
     */
    public boolean getWaitToConfirm() {
        Boolean res = getBooleanOrNull(AssetFileColumns.WAIT_TO_CONFIRM);
        if (res == null)
            throw new NullPointerException("The value of 'wait_to_confirm' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code user_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getUserId() {
        String res = getStringOrNull(AssetFileColumns.USER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'user_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code computer_id} value.
     */
    public int getComputerId() {
        Integer res = getIntegerOrNull(AssetFileColumns.COMPUTER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'computer_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code ug_from_another_app} value.
     */
    public boolean getUGFromAnotherApp() {
        Boolean res = getBooleanOrNull(AssetFileColumns.ALIAS_FROM_ANOTHER_APP);
        if (res == null)
            throw new NullPointerException("The value of 'ug_from_another_app' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code ug_start_timestamp} value.
     * Can be {@code null}.
     */
    @Nullable
    public Long getUGStartTimestamp() {
        Long res = getLongOrNull(AssetFileColumns.ALIAS_START_TIMESTAMP);
        return res;
    }
}
