package com.filelug.android.provider.uploadgroup;

import android.net.Uri;
import android.provider.BaseColumns;

import com.filelug.android.provider.RepositoryContentProvider;

/**
 * Columns for the {@code upload_group} table.
 */
public class UploadGroupColumns implements BaseColumns {
    public static final String TABLE_NAME = "upload_group";
    public static final Uri CONTENT_URI = Uri.parse(RepositoryContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String GROUP_ID = "group_id";

    public static final String FROM_ANOTHER_APP = "from_another_app";

    public static final String START_TIMESTAMP = "start_timestamp";

    public static final String UPLOAD_DIRECTORY = "upload_directory";

    public static final String SUBDIRECTORY_TYPE = "subdirectory_type";

    public static final String SUBDIRECTORY_VALUE = "subdirectory_value";

    public static final String DESCRIPTION_TYPE = "description_type";

    public static final String DESCRIPTION_VALUE = "description_value";

    public static final String NOTIFICATION_TYPE = "notification_type";

    public static final String USER_ID = "user_id";

    public static final String COMPUTER_ID = "computer_id";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            GROUP_ID,
            FROM_ANOTHER_APP,
            START_TIMESTAMP,
            UPLOAD_DIRECTORY,
            SUBDIRECTORY_TYPE,
            SUBDIRECTORY_VALUE,
            DESCRIPTION_TYPE,
            DESCRIPTION_VALUE,
            NOTIFICATION_TYPE,
            USER_ID,
            COMPUTER_ID
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(GROUP_ID) || c.contains("." + GROUP_ID)) return true;
            if (c.equals(FROM_ANOTHER_APP) || c.contains("." + FROM_ANOTHER_APP)) return true;
            if (c.equals(START_TIMESTAMP) || c.contains("." + START_TIMESTAMP)) return true;
            if (c.equals(UPLOAD_DIRECTORY) || c.contains("." + UPLOAD_DIRECTORY)) return true;
            if (c.equals(SUBDIRECTORY_TYPE) || c.contains("." + SUBDIRECTORY_TYPE)) return true;
            if (c.equals(SUBDIRECTORY_VALUE) || c.contains("." + SUBDIRECTORY_VALUE)) return true;
            if (c.equals(DESCRIPTION_TYPE) || c.contains("." + DESCRIPTION_TYPE)) return true;
            if (c.equals(DESCRIPTION_VALUE) || c.contains("." + DESCRIPTION_VALUE)) return true;
            if (c.equals(NOTIFICATION_TYPE) || c.contains("." + NOTIFICATION_TYPE)) return true;
            if (c.equals(USER_ID) || c.contains("." + USER_ID)) return true;
            if (c.equals(COMPUTER_ID) || c.contains("." + COMPUTER_ID)) return true;
        }
        return false;
    }

}
