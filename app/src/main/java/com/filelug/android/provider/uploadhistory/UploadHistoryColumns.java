package com.filelug.android.provider.uploadhistory;

import android.net.Uri;
import android.provider.BaseColumns;

import com.filelug.android.provider.RepositoryContentProvider;

/**
 * Columns for the {@code upload_history} table.
 */
public class UploadHistoryColumns implements BaseColumns {
    public static final String TABLE_NAME = "upload_history";
    public static final Uri CONTENT_URI = Uri.parse(RepositoryContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String COMPUTER_GROUP = "computer_group";

    public static final String COMPUTER_NAME = "computer_name";

    public static final String FILE_SIZE = "file_size";

    public static final String END_TIMESTAMP = "end_timestamp";

    public static final String FILE_NAME = "file_name";

    public static final String USER_ID = "user_id";

    public static final String COMPUTER_ID = "computer_id";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            COMPUTER_GROUP,
            COMPUTER_NAME,
            FILE_SIZE,
            END_TIMESTAMP,
            FILE_NAME,
            USER_ID,
            COMPUTER_ID
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(COMPUTER_GROUP) || c.contains("." + COMPUTER_GROUP)) return true;
            if (c.equals(COMPUTER_NAME) || c.contains("." + COMPUTER_NAME)) return true;
            if (c.equals(FILE_SIZE) || c.contains("." + FILE_SIZE)) return true;
            if (c.equals(END_TIMESTAMP) || c.contains("." + END_TIMESTAMP)) return true;
            if (c.equals(FILE_NAME) || c.contains("." + FILE_NAME)) return true;
            if (c.equals(USER_ID) || c.contains("." + USER_ID)) return true;
            if (c.equals(COMPUTER_ID) || c.contains("." + COMPUTER_ID)) return true;
        }
        return false;
    }

}
