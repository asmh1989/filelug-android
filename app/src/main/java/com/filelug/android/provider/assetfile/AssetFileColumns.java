package com.filelug.android.provider.assetfile;

import android.net.Uri;
import android.provider.BaseColumns;

import com.filelug.android.provider.RepositoryContentProvider;
import com.filelug.android.provider.uploadgroup.UploadGroupColumns;

/**
 * Columns for the {@code asset_file} table.
 */
public class AssetFileColumns implements BaseColumns {
    public static final String TABLE_NAME = "asset_file";
    public static final Uri CONTENT_URI = Uri.parse(RepositoryContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String GROUP_ID = "group_id";

    public static final String TRANSFER_KEY = "transfer_key";

    public static final String ASSET_URL = "asset_url";

    public static final String SERVER_FILE_NAME = "server_file_name";

    public static final String CACHE_FILE_NAME = "cache_file_name";

    public static final String CONTENT_TYPE = "content_type";

    public static final String LAST_MODIFIED_TIMESTAMP = "last_modified_timestamp";

    public static final String STATUS = "status";

    public static final String START_TIMESTAMP = "start_timestamp";

    public static final String END_TIMESTAMP = "end_timestamp";

    public static final String TOTAL_SIZE = "total_size";

    public static final String TRANSFERRED_SIZE = "transferred_size";

    public static final String WAIT_TO_CONFIRM = "wait_to_confirm";

    public static final String USER_ID = "user_id";

    public static final String COMPUTER_ID = "computer_id";

    public static final String ALIAS_FROM_ANOTHER_APP = "ug_from_another_app";
    public static final String UG_FROM_ANOTHER_APP = UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.FROM_ANOTHER_APP;
    public static final String UG_FROM_ANOTHER_APP_WITH_ALIAS = UG_FROM_ANOTHER_APP + " AS " + ALIAS_FROM_ANOTHER_APP;

    public static final String ALIAS_START_TIMESTAMP = "ug_start_timestamp";
    public static final String UG_START_TIMESTAMP = UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.START_TIMESTAMP;
    public static final String UG_START_TIMESTAMP_WITH_ALIAS = UG_START_TIMESTAMP + " AS " + ALIAS_START_TIMESTAMP;


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            GROUP_ID,
            TRANSFER_KEY,
            ASSET_URL,
            SERVER_FILE_NAME,
            CACHE_FILE_NAME,
            CONTENT_TYPE,
            LAST_MODIFIED_TIMESTAMP,
            STATUS,
            START_TIMESTAMP,
            END_TIMESTAMP,
            TOTAL_SIZE,
            TRANSFERRED_SIZE,
            WAIT_TO_CONFIRM,
            USER_ID,
            COMPUTER_ID,
            UG_FROM_ANOTHER_APP_WITH_ALIAS,
            UG_START_TIMESTAMP_WITH_ALIAS
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(GROUP_ID) || c.contains("." + GROUP_ID)) return true;
            if (c.equals(TRANSFER_KEY) || c.contains("." + TRANSFER_KEY)) return true;
            if (c.equals(ASSET_URL) || c.contains("." + ASSET_URL)) return true;
            if (c.equals(SERVER_FILE_NAME) || c.contains("." + SERVER_FILE_NAME)) return true;
            if (c.equals(CACHE_FILE_NAME) || c.contains("." + CACHE_FILE_NAME)) return true;
            if (c.equals(CONTENT_TYPE) || c.contains("." + CONTENT_TYPE)) return true;
            if (c.equals(LAST_MODIFIED_TIMESTAMP) || c.contains("." + LAST_MODIFIED_TIMESTAMP)) return true;
            if (c.equals(STATUS) || c.contains("." + STATUS)) return true;
            if (c.equals(START_TIMESTAMP) || c.contains("." + START_TIMESTAMP)) return true;
            if (c.equals(END_TIMESTAMP) || c.contains("." + END_TIMESTAMP)) return true;
            if (c.equals(TOTAL_SIZE) || c.contains("." + TOTAL_SIZE)) return true;
            if (c.equals(TRANSFERRED_SIZE) || c.contains("." + TRANSFERRED_SIZE)) return true;
            if (c.equals(WAIT_TO_CONFIRM) || c.contains("." + WAIT_TO_CONFIRM)) return true;
            if (c.equals(USER_ID) || c.contains("." + USER_ID)) return true;
            if (c.equals(COMPUTER_ID) || c.contains("." + COMPUTER_ID)) return true;
            if (c.equals(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.FROM_ANOTHER_APP) || c.contains(ALIAS_FROM_ANOTHER_APP)) return true;
            if (c.equals(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.START_TIMESTAMP) || c.contains(ALIAS_START_TIMESTAMP)) return true;
        }
        return false;
    }

    public static boolean hasUploadGroupColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.FROM_ANOTHER_APP) || c.contains(ALIAS_FROM_ANOTHER_APP)) return true;
            if (c.equals(UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.START_TIMESTAMP) || c.contains(ALIAS_START_TIMESTAMP)) return true;
        }
        return false;
    }

}
