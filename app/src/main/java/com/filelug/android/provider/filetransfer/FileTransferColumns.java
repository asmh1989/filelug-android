package com.filelug.android.provider.filetransfer;

import android.net.Uri;
import android.provider.BaseColumns;

import com.filelug.android.provider.RepositoryContentProvider;
import com.filelug.android.provider.downloadgroup.DownloadGroupColumns;

/**
 * Columns for the {@code file_transfer} table.
 */
public class FileTransferColumns implements BaseColumns {
    public static final String TABLE_NAME = "file_transfer";
    public static final Uri CONTENT_URI = Uri.parse(RepositoryContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String GROUP_ID = "group_id";

    public static final String TRANSFER_KEY = "transfer_key";

    public static final String TYPE = "type";

    public static final String SERVER_PATH = "server_path";

    public static final String REAL_SERVER_PATH = "real_server_path";

    public static final String LOCAL_FILE_NAME = "local_file_name";

    public static final String REAL_LOCAL_FILE_NAME = "real_local_file_name";

    public static final String SAVED_FILE_NAME = "saved_file_name";

    public static final String FILE_IN_CACHE = "file_in_cache";

    public static final String CONTENT_TYPE = "content_type";

    public static final String LAST_MODIFIED = "last_modified";

    public static final String STATUS = "status";

    public static final String START_TIMESTAMP = "start_timestamp";

    public static final String END_TIMESTAMP = "end_timestamp";

    public static final String TOTAL_SIZE = "total_size";

    public static final String TRANSFERRED_SIZE = "transferred_size";

    public static final String WAIT_TO_CONFIRM = "wait_to_confirm";

    public static final String ACTIONS_AFTER_DOWNLOAD = "actions_after_download";

    public static final String USER_ID = "user_id";

    public static final String COMPUTER_ID = "computer_id";

    public static final String ALIAS_LOCAL_PATH = "dg_local_path";
    public static final String DG_LOCAL_PATH = DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.LOCAL_PATH;
    public static final String DG_LOCAL_PATH_WITH_ALIAS = DG_LOCAL_PATH + " AS " + ALIAS_LOCAL_PATH;

    public static final String ALIAS_FROM_ANOTHER_APP = "dg_from_another_app";
    public static final String DG_FROM_ANOTHER_APP = DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.FROM_ANOTHER_APP;
    public static final String DG_FROM_ANOTHER_APP_WITH_ALIAS = DG_FROM_ANOTHER_APP + " AS " + ALIAS_FROM_ANOTHER_APP;

    public static final String ALIAS_NOTIFICATION_TYPE = "dg_notification_type";
    public static final String DG_NOTIFICATION_TYPE = DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.NOTIFICATION_TYPE;
    public static final String DG_NOTIFICATION_TYPE_WITH_ALIAS = DG_NOTIFICATION_TYPE + " AS " + ALIAS_NOTIFICATION_TYPE;

    public static final String ALIAS_START_TIMESTAMP = "dg_start_timestamp";
    public static final String DG_START_TIMESTAMP = DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.START_TIMESTAMP;
    public static final String DG_START_TIMESTAMP_WITH_ALIAS = DG_START_TIMESTAMP + " AS " + ALIAS_START_TIMESTAMP;


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            GROUP_ID,
            TRANSFER_KEY,
            TYPE,
            SERVER_PATH,
            REAL_SERVER_PATH,
            LOCAL_FILE_NAME,
            REAL_LOCAL_FILE_NAME,
            SAVED_FILE_NAME,
            FILE_IN_CACHE,
            CONTENT_TYPE,
            LAST_MODIFIED,
            STATUS,
            START_TIMESTAMP,
            END_TIMESTAMP,
            TOTAL_SIZE,
            TRANSFERRED_SIZE,
            WAIT_TO_CONFIRM,
            ACTIONS_AFTER_DOWNLOAD,
            USER_ID,
            COMPUTER_ID,
            DG_LOCAL_PATH_WITH_ALIAS,
            DG_FROM_ANOTHER_APP_WITH_ALIAS,
            DG_NOTIFICATION_TYPE_WITH_ALIAS,
            DG_START_TIMESTAMP_WITH_ALIAS
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(GROUP_ID) || c.contains("." + GROUP_ID)) return true;
            if (c.equals(TRANSFER_KEY) || c.contains("." + TRANSFER_KEY)) return true;
            if (c.equals(TYPE) || c.contains("." + TYPE)) return true;
            if (c.equals(SERVER_PATH) || c.contains("." + SERVER_PATH)) return true;
            if (c.equals(REAL_SERVER_PATH) || c.contains("." + REAL_SERVER_PATH)) return true;
            if (c.equals(LOCAL_FILE_NAME) || c.contains("." + LOCAL_FILE_NAME)) return true;
            if (c.equals(REAL_LOCAL_FILE_NAME) || c.contains("." + REAL_LOCAL_FILE_NAME)) return true;
            if (c.equals(SAVED_FILE_NAME) || c.contains("." + SAVED_FILE_NAME)) return true;
            if (c.equals(FILE_IN_CACHE) || c.contains("." + FILE_IN_CACHE)) return true;
            if (c.equals(CONTENT_TYPE) || c.contains("." + CONTENT_TYPE)) return true;
            if (c.equals(LAST_MODIFIED) || c.contains("." + LAST_MODIFIED)) return true;
            if (c.equals(STATUS) || c.contains("." + STATUS)) return true;
            if (c.equals(START_TIMESTAMP) || c.contains("." + START_TIMESTAMP)) return true;
            if (c.equals(END_TIMESTAMP) || c.contains("." + END_TIMESTAMP)) return true;
            if (c.equals(TOTAL_SIZE) || c.contains("." + TOTAL_SIZE)) return true;
            if (c.equals(TRANSFERRED_SIZE) || c.contains("." + TRANSFERRED_SIZE)) return true;
            if (c.equals(WAIT_TO_CONFIRM) || c.contains("." + WAIT_TO_CONFIRM)) return true;
            if (c.equals(ACTIONS_AFTER_DOWNLOAD) || c.contains("." + ACTIONS_AFTER_DOWNLOAD)) return true;
            if (c.equals(USER_ID) || c.contains("." + USER_ID)) return true;
            if (c.equals(COMPUTER_ID) || c.contains("." + COMPUTER_ID)) return true;
            if (c.equals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.LOCAL_PATH) || c.contains(ALIAS_LOCAL_PATH)) return true;
            if (c.equals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.FROM_ANOTHER_APP) || c.contains(ALIAS_FROM_ANOTHER_APP)) return true;
            if (c.equals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.NOTIFICATION_TYPE) || c.contains(ALIAS_NOTIFICATION_TYPE)) return true;
            if (c.equals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.START_TIMESTAMP) || c.contains(ALIAS_START_TIMESTAMP)) return true;
        }
        return false;
    }

    public static boolean hasDownloadGroupColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.LOCAL_PATH) || c.contains(ALIAS_LOCAL_PATH)) return true;
            if (c.equals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.FROM_ANOTHER_APP) || c.contains(ALIAS_FROM_ANOTHER_APP)) return true;
            if (c.equals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.NOTIFICATION_TYPE) || c.contains(ALIAS_NOTIFICATION_TYPE)) return true;
            if (c.equals(DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.START_TIMESTAMP) || c.contains(ALIAS_START_TIMESTAMP)) return true;
        }
        return false;
    }

}
