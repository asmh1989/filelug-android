package com.filelug.android.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.filelug.android.provider.assetfile.AssetFileColumns;
import com.filelug.android.provider.downloadgroup.DownloadGroupColumns;
import com.filelug.android.provider.filetransfer.FileTransferColumns;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelColumns;
import com.filelug.android.provider.remoteroot.RemoteRootColumns;
import com.filelug.android.provider.uploadgroup.UploadGroupColumns;

/**
 * Implement your custom database creation or upgrade code here.
 *
 * This file will not be overwritten if you re-run the content provider generator.
 */
public class FilelugSQLiteOpenHelperCallbacks {
    private static final String TAG = FilelugSQLiteOpenHelperCallbacks.class.getSimpleName();

    private static final String V2_FILE_TRANSFER_ADD_SAVED_FILE_NAME = "ALTER TABLE "
            + FileTransferColumns.TABLE_NAME + " ADD COLUMN " + FileTransferColumns.SAVED_FILE_NAME + " TEXT;";

    private static final String V3_DOWNLOAD_GROUP_ADD_FROM_ANOTHER_APP = "ALTER TABLE "
            + DownloadGroupColumns.TABLE_NAME + " ADD COLUMN " + DownloadGroupColumns.FROM_ANOTHER_APP + " INTEGER NOT NULL DEFAULT 0;";
    private static final String V3_UPLOAD_GROUP_ADD_FROM_ANOTHER_APP = "ALTER TABLE "
            + UploadGroupColumns.TABLE_NAME + " ADD COLUMN " + UploadGroupColumns.FROM_ANOTHER_APP + " INTEGER NOT NULL DEFAULT 0;";
    private static final String V3_FILE_TRANSFER_ADD_REAL_LOCAL_FILE_NAME = "ALTER TABLE "
            + FileTransferColumns.TABLE_NAME + " ADD COLUMN " + FileTransferColumns.REAL_LOCAL_FILE_NAME + " TEXT;";
    private static final String V3_FILE_TRANSFER_ADD_FILE_IN_CACHE = "ALTER TABLE "
            + FileTransferColumns.TABLE_NAME + " ADD COLUMN " + FileTransferColumns.FILE_IN_CACHE + " INTEGER NOT NULL DEFAULT 0;";
    private static final String V3_REMOTE_HIERARCHICAL_MODEL_ADD_LOCAL_LAST_MODIFIED = "ALTER TABLE "
            + RemoteHierarchicalModelColumns.TABLE_NAME + " ADD COLUMN " + RemoteHierarchicalModelColumns.LOCAL_LAST_MODIFIED + " INTEGER;";
    private static final String V3_REMOTE_HIERARCHICAL_MODEL_ADD_LOCAL_SIZE = "ALTER TABLE "
            + RemoteHierarchicalModelColumns.TABLE_NAME + " ADD COLUMN " + RemoteHierarchicalModelColumns.LOCAL_SIZE + " INTEGER;";

    private static final String V4_REMOTE_HIERARCHICAL_MODEL_ADD_LOCAL_LAST_ACCESS = "ALTER TABLE "
            + RemoteHierarchicalModelColumns.TABLE_NAME + " ADD COLUMN " + RemoteHierarchicalModelColumns.LOCAL_LAST_ACCESS + " INTEGER;";

    private static final String V5_ASSET_FILE_ADD_CACHE_FILE_NAME = "ALTER TABLE "
            + AssetFileColumns.TABLE_NAME + " ADD COLUMN " + AssetFileColumns.CACHE_FILE_NAME + " TEXT;";

    private static final String V5_ASSET_FILE_ADD_LAST_MODIFIED_TIMESTAMP = "ALTER TABLE "
            + AssetFileColumns.TABLE_NAME + " ADD COLUMN " + AssetFileColumns.LAST_MODIFIED_TIMESTAMP + " INTEGER NOT NULL DEFAULT 0;";

    public static final String V6_DROP_INDEX_REMOTE_ROOT_DIRECTORY_DIRECTORY_ID = "DROP INDEX IDX_REMOTE_ROOT_DIRECTORY_DIRECTORY_ID; ";

    public static final String V6_DROP_INDEX_REMOTE_ROOT_DIRECTORY_USER_ID = "DROP INDEX IDX_REMOTE_ROOT_DIRECTORY_USER_ID; ";

    public static final String V6_DROP_INDEX_REMOTE_ROOT_DIRECTORY_COMPUTER_ID = "DROP INDEX IDX_REMOTE_ROOT_DIRECTORY_COMPUTER_ID; ";

    public static final String V6_DROP_TABLE_REMOTE_ROOT_DIRECTORY = "DROP TABLE "
            + RemoteRootColumns.TABLE_NAME_V5 + "; ";

    public static final String V6_DROP_INDEX_COUNTRY_COUNTRY_ID = "DROP INDEX IDX_COUNTRY_COUNTRY_ID; ";

    public static final String V6_DROP_INDEX_COUNTRY_COUNTRY_CODE = "DROP INDEX IDX_COUNTRY_COUNTRY_CODE; ";

    public static final String V6_DROP_TABLE_COUNTRY = "DROP TABLE country; ";


    public void onOpen(final Context context, final SQLiteDatabase db) {
//        if (BuildConfig.DEBUG) Log.d(TAG, "onOpen");
        // Insert your db open code here.
    }

    public void onPreCreate(final Context context, final SQLiteDatabase db) {
//        if (BuildConfig.DEBUG) Log.d(TAG, "onPreCreate");
        // Insert your db creation code here. This is called before your tables are created.
    }

    public void onPostCreate(final Context context, final SQLiteDatabase db) {
//        if (BuildConfig.DEBUG) Log.d(TAG, "onPostCreate");
        // Insert your db creation code here. This is called after your tables are created.
    }

    public void onUpgrade(final Context context, final SQLiteDatabase db, final int oldVersion, final int newVersion) {
//        if (BuildConfig.DEBUG) Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        if (oldVersion < 2) {
            db.execSQL(V2_FILE_TRANSFER_ADD_SAVED_FILE_NAME);
        }
        if (oldVersion < 3) {
            db.execSQL(V3_DOWNLOAD_GROUP_ADD_FROM_ANOTHER_APP);
            db.execSQL(V3_UPLOAD_GROUP_ADD_FROM_ANOTHER_APP);
            db.execSQL(V3_FILE_TRANSFER_ADD_REAL_LOCAL_FILE_NAME);
            db.execSQL(V3_FILE_TRANSFER_ADD_FILE_IN_CACHE);
            db.execSQL(V3_REMOTE_HIERARCHICAL_MODEL_ADD_LOCAL_LAST_MODIFIED);
            db.execSQL(V3_REMOTE_HIERARCHICAL_MODEL_ADD_LOCAL_SIZE);
        }
        if (oldVersion < 4) {
            db.execSQL(V4_REMOTE_HIERARCHICAL_MODEL_ADD_LOCAL_LAST_ACCESS);
        }
        if (oldVersion < 5) {
            db.execSQL(V5_ASSET_FILE_ADD_CACHE_FILE_NAME);
            db.execSQL(V5_ASSET_FILE_ADD_LAST_MODIFIED_TIMESTAMP);
        }
        if (oldVersion < 6) {
            db.execSQL(FilelugSQLiteOpenHelper.SQL_CREATE_TABLE_REMOTE_ROOT);
            db.execSQL(FilelugSQLiteOpenHelper.SQL_CREATE_INDEX_REMOTE_ROOT_LABEL);
            db.execSQL(FilelugSQLiteOpenHelper.SQL_CREATE_INDEX_REMOTE_ROOT_PATH);
            db.execSQL(FilelugSQLiteOpenHelper.SQL_CREATE_INDEX_REMOTE_ROOT_USER_COMPUTER_LABEL);
            db.execSQL(FilelugSQLiteOpenHelper.SQL_CREATE_INDEX_REMOTE_ROOT_USER_COMPUTER_PATH);

            db.execSQL(V6_DROP_INDEX_REMOTE_ROOT_DIRECTORY_DIRECTORY_ID);
            db.execSQL(V6_DROP_INDEX_REMOTE_ROOT_DIRECTORY_USER_ID);
            db.execSQL(V6_DROP_INDEX_REMOTE_ROOT_DIRECTORY_COMPUTER_ID);
            db.execSQL(V6_DROP_TABLE_REMOTE_ROOT_DIRECTORY);

            db.execSQL(V6_DROP_INDEX_COUNTRY_COUNTRY_ID);
            db.execSQL(V6_DROP_INDEX_COUNTRY_COUNTRY_CODE);
            db.execSQL(V6_DROP_TABLE_COUNTRY);
        }
    }
}
