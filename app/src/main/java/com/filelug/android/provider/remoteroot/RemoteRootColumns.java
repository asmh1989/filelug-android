package com.filelug.android.provider.remoteroot;

import android.net.Uri;
import android.provider.BaseColumns;

import com.filelug.android.provider.RepositoryContentProvider;

/**
 * Columns for the {@code remote_root} table.
 */
public class RemoteRootColumns implements BaseColumns {
    public static final String TABLE_NAME_V5 = "remote_root_directory";
    public static final String TABLE_NAME = "remote_root";
    public static final Uri CONTENT_URI = Uri.parse(RepositoryContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String LABEL = "label";

    public static final String PATH = "path";

    public static final String REAL_PATH = "real_path";

    public static final String TYPE = "type";

    public static final String USER_ID = "user_id";

    public static final String COMPUTER_ID = "computer_id";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            LABEL,
            PATH,
            REAL_PATH,
            TYPE,
            USER_ID,
            COMPUTER_ID
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(LABEL) || c.contains("." + LABEL)) return true;
            if (c.equals(PATH) || c.contains("." + PATH)) return true;
            if (c.equals(REAL_PATH) || c.contains("." + REAL_PATH)) return true;
            if (c.equals(TYPE) || c.contains("." + TYPE)) return true;
            if (c.equals(USER_ID) || c.contains("." + USER_ID)) return true;
            if (c.equals(COMPUTER_ID) || c.contains("." + COMPUTER_ID)) return true;
        }
        return false;
    }

}
