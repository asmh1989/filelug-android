package com.filelug.android.provider.assetfile;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.filelug.android.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code asset_file} table.
 */
public class AssetFileContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return AssetFileColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable AssetFileSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public AssetFileContentValues putGroupId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("groupId must not be null");
        mContentValues.put(AssetFileColumns.GROUP_ID, value);
        return this;
    }


    public AssetFileContentValues putTransferKey(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("transferKey must not be null");
        mContentValues.put(AssetFileColumns.TRANSFER_KEY, value);
        return this;
    }


    public AssetFileContentValues putAssetUrl(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("assetUrl must not be null");
        mContentValues.put(AssetFileColumns.ASSET_URL, value);
        return this;
    }


    public AssetFileContentValues putServerFileName(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("serverFileName must not be null");
        mContentValues.put(AssetFileColumns.SERVER_FILE_NAME, value);
        return this;
    }


    public AssetFileContentValues putCacheFileName(@Nullable String value) {
        mContentValues.put(AssetFileColumns.CACHE_FILE_NAME, value);
        return this;
    }

    public AssetFileContentValues putCacheFileNameNull() {
        mContentValues.putNull(AssetFileColumns.CACHE_FILE_NAME);
        return this;
    }


    public AssetFileContentValues putContentType(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("contentType must not be null");
        mContentValues.put(AssetFileColumns.CONTENT_TYPE, value);
        return this;
    }


    public AssetFileContentValues putLastModifiedTimestamp(@NonNull Long value) {
        if (value == null) throw new IllegalArgumentException("lastModifiedTimestamp must not be null");
        mContentValues.put(AssetFileColumns.LAST_MODIFIED_TIMESTAMP, value);
        return this;
    }

    public AssetFileContentValues putStatus(@NonNull UploadStatusType value) {
        if (value == null) throw new IllegalArgumentException("status must not be null");
        mContentValues.put(AssetFileColumns.STATUS, value.ordinal());
        return this;
    }


    public AssetFileContentValues putStartTimestamp(@Nullable Long value) {
        mContentValues.put(AssetFileColumns.START_TIMESTAMP, value);
        return this;
    }

    public AssetFileContentValues putStartTimestampNull() {
        mContentValues.putNull(AssetFileColumns.START_TIMESTAMP);
        return this;
    }

    public AssetFileContentValues putEndTimestamp(@Nullable Long value) {
        mContentValues.put(AssetFileColumns.END_TIMESTAMP, value);
        return this;
    }

    public AssetFileContentValues putEndTimestampNull() {
        mContentValues.putNull(AssetFileColumns.END_TIMESTAMP);
        return this;
    }

    public AssetFileContentValues putTotalSize(@Nullable Long value) {
        mContentValues.put(AssetFileColumns.TOTAL_SIZE, value);
        return this;
    }

    public AssetFileContentValues putTotalSizeNull() {
        mContentValues.putNull(AssetFileColumns.TOTAL_SIZE);
        return this;
    }

    public AssetFileContentValues putTransferredSize(@Nullable Long value) {
        mContentValues.put(AssetFileColumns.TRANSFERRED_SIZE, value);
        return this;
    }

    public AssetFileContentValues putTransferredSizeNull() {
        mContentValues.putNull(AssetFileColumns.TRANSFERRED_SIZE);
        return this;
    }

    public AssetFileContentValues putWaitToConfirm(boolean value) {
        mContentValues.put(AssetFileColumns.WAIT_TO_CONFIRM, value);
        return this;
    }


    public AssetFileContentValues putUserId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("userId must not be null");
        mContentValues.put(AssetFileColumns.USER_ID, value);
        return this;
    }


    public AssetFileContentValues putComputerId(int value) {
        mContentValues.put(AssetFileColumns.COMPUTER_ID, value);
        return this;
    }

}
