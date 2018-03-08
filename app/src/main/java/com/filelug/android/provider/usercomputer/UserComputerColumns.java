package com.filelug.android.provider.usercomputer;

import android.net.Uri;
import android.provider.BaseColumns;

import com.filelug.android.provider.RepositoryContentProvider;

/**
 * Columns for the {@code user_computer} table.
 */
public class UserComputerColumns implements BaseColumns {
    public static final String TABLE_NAME = "user_computer";
    public static final Uri CONTENT_URI = Uri.parse(RepositoryContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String COMPUTER_ID = "computer_id";

    public static final String COMPUTER_NAME = "computer_name";

    public static final String COMPUTER_GROUP = "computer_group";

    public static final String COMPUTER_ADMIN_ID = "computer_admin_id";

    public static final String USER_ID = "user_id";

    public static final String USER_COMPUTER_ID = "user_computer_id";

    public static final String LUG_SERVER_ID = "lug_server_id";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            COMPUTER_ID,
            COMPUTER_NAME,
            COMPUTER_GROUP,
            COMPUTER_ADMIN_ID,
            USER_ID,
            USER_COMPUTER_ID,
            LUG_SERVER_ID
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(COMPUTER_ID) || c.contains("." + COMPUTER_ID)) return true;
            if (c.equals(COMPUTER_NAME) || c.contains("." + COMPUTER_NAME)) return true;
            if (c.equals(COMPUTER_GROUP) || c.contains("." + COMPUTER_GROUP)) return true;
            if (c.equals(COMPUTER_ADMIN_ID) || c.contains("." + COMPUTER_ADMIN_ID)) return true;
            if (c.equals(USER_ID) || c.contains("." + USER_ID)) return true;
            if (c.equals(USER_COMPUTER_ID) || c.contains("." + USER_COMPUTER_ID)) return true;
            if (c.equals(LUG_SERVER_ID) || c.contains("." + LUG_SERVER_ID)) return true;
        }
        return false;
    }

}
