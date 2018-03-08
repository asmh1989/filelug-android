package com.filelug.android.provider.remotehierarchicalmodel;

import android.net.Uri;
import android.provider.BaseColumns;

import com.filelug.android.provider.RepositoryContentProvider;

/**
 * Columns for the {@code remote_hierarchical_model} table.
 */
public class RemoteHierarchicalModelColumns implements BaseColumns {
    public static final String TABLE_NAME = "remote_hierarchical_model";
    public static final Uri CONTENT_URI = Uri.parse(RepositoryContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String SYMLINK = "symlink";

    public static final String PARENT = "parent";

    public static final String NAME = "name";

    public static final String READABLE = "readable";

    public static final String WRITABLE = "writable";

    public static final String HIDDEN = "hidden";

    public static final String LAST_MODIFIED = "last_modified";

    public static final String TYPE = "type";

    public static final String CONTENT_TYPE = "content_type";

    public static final String SIZE = "size";

    public static final String REAL_PARENT = "real_parent";

    public static final String REAL_NAME = "real_name";

    public static final String LOCAL_LAST_MODIFIED = "local_last_modified";

    public static final String LOCAL_SIZE = "local_size";

    public static final String LOCAL_LAST_ACCESS = "local_last_access";

    public static final String USER_ID = "user_id";

    public static final String COMPUTER_ID = "computer_id";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            SYMLINK,
            PARENT,
            NAME,
            READABLE,
            WRITABLE,
            HIDDEN,
            LAST_MODIFIED,
            TYPE,
            CONTENT_TYPE,
            SIZE,
            REAL_PARENT,
            REAL_NAME,
            LOCAL_LAST_MODIFIED,
            LOCAL_SIZE,
            LOCAL_LAST_ACCESS,
            USER_ID,
            COMPUTER_ID
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(SYMLINK) || c.contains("." + SYMLINK)) return true;
            if (c.equals(PARENT) || c.contains("." + PARENT)) return true;
            if (c.equals(NAME) || c.contains("." + NAME)) return true;
            if (c.equals(READABLE) || c.contains("." + READABLE)) return true;
            if (c.equals(WRITABLE) || c.contains("." + WRITABLE)) return true;
            if (c.equals(HIDDEN) || c.contains("." + HIDDEN)) return true;
            if (c.equals(LAST_MODIFIED) || c.contains("." + LAST_MODIFIED)) return true;
            if (c.equals(TYPE) || c.contains("." + TYPE)) return true;
            if (c.equals(CONTENT_TYPE) || c.contains("." + CONTENT_TYPE)) return true;
            if (c.equals(SIZE) || c.contains("." + SIZE)) return true;
            if (c.equals(REAL_PARENT) || c.contains("." + REAL_PARENT)) return true;
            if (c.equals(REAL_NAME) || c.contains("." + REAL_NAME)) return true;
            if (c.equals(LOCAL_LAST_MODIFIED) || c.contains("." + LOCAL_LAST_MODIFIED)) return true;
            if (c.equals(LOCAL_SIZE) || c.contains("." + LOCAL_SIZE)) return true;
            if (c.equals(LOCAL_LAST_ACCESS) || c.contains("." + LOCAL_LAST_ACCESS)) return true;
            if (c.equals(USER_ID) || c.contains("." + USER_ID)) return true;
            if (c.equals(COMPUTER_ID) || c.contains("." + COMPUTER_ID)) return true;
        }
        return false;
    }

}
