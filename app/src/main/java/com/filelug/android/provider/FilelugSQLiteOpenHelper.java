package com.filelug.android.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.filelug.android.provider.assetfile.AssetFileColumns;
import com.filelug.android.provider.downloadgroup.DownloadGroupColumns;
import com.filelug.android.provider.downloadhistory.DownloadHistoryColumns;
import com.filelug.android.provider.filetransfer.FileTransferColumns;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelColumns;
import com.filelug.android.provider.remoteroot.RemoteRootColumns;
import com.filelug.android.provider.uploadgroup.UploadGroupColumns;
import com.filelug.android.provider.uploadhistory.UploadHistoryColumns;
import com.filelug.android.provider.usercomputer.UserComputerColumns;

public class FilelugSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = FilelugSQLiteOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "filelug.db";
    private static final int DATABASE_VERSION = 6;
    private static FilelugSQLiteOpenHelper sInstance;
    private final Context mContext;
    private final FilelugSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:off

    /*
    // v4 Origin
    public static final String SQL_CREATE_TABLE_ASSET_FILE = "CREATE TABLE IF NOT EXISTS "
            + AssetFileColumns.TABLE_NAME + " ( "
            + AssetFileColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AssetFileColumns.GROUP_ID + " TEXT NOT NULL, "
            + AssetFileColumns.TRANSFER_KEY + " TEXT NOT NULL, "
            + AssetFileColumns.ASSET_URL + " TEXT NOT NULL, "
            + AssetFileColumns.SERVER_FILE_NAME + " TEXT NOT NULL, "
            + AssetFileColumns.CONTENT_TYPE + " TEXT NOT NULL, "
            + AssetFileColumns.PARAM_STATUS + " INTEGER NOT NULL, "
            + AssetFileColumns.START_TIMESTAMP + " INTEGER, "
            + AssetFileColumns.END_TIMESTAMP + " INTEGER, "
            + AssetFileColumns.TOTAL_SIZE + " INTEGER, "
            + AssetFileColumns.TRANSFERRED_SIZE + " INTEGER, "
            + AssetFileColumns.WAIT_TO_CONFIRM + " INTEGER NOT NULL DEFAULT 0, "
            + AssetFileColumns.USER_ID + " TEXT NOT NULL, "
            + AssetFileColumns.COMPUTER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_af_user_computer_group_id_transfer_key UNIQUE (user_id, computer_id, group_id, transfer_key) ON CONFLICT REPLACE"
            + " );";
    */
    // v5
    public static final String SQL_CREATE_TABLE_ASSET_FILE = "CREATE TABLE IF NOT EXISTS "
            + AssetFileColumns.TABLE_NAME + " ( "
            + AssetFileColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AssetFileColumns.GROUP_ID + " TEXT NOT NULL, "
            + AssetFileColumns.TRANSFER_KEY + " TEXT NOT NULL, "
            + AssetFileColumns.ASSET_URL + " TEXT NOT NULL, "
            + AssetFileColumns.SERVER_FILE_NAME + " TEXT NOT NULL, "
            + AssetFileColumns.CACHE_FILE_NAME + " TEXT, " // v5 add this column! //
            + AssetFileColumns.CONTENT_TYPE + " TEXT NOT NULL, "
            + AssetFileColumns.LAST_MODIFIED_TIMESTAMP + " INTEGER NOT NULL DEFAULT 0, " // v5 add this column! //
            + AssetFileColumns.STATUS + " INTEGER NOT NULL, "
            + AssetFileColumns.START_TIMESTAMP + " INTEGER, "
            + AssetFileColumns.END_TIMESTAMP + " INTEGER, "
            + AssetFileColumns.TOTAL_SIZE + " INTEGER, "
            + AssetFileColumns.TRANSFERRED_SIZE + " INTEGER, "
            + AssetFileColumns.WAIT_TO_CONFIRM + " INTEGER NOT NULL DEFAULT 0, "
            + AssetFileColumns.USER_ID + " TEXT NOT NULL, "
            + AssetFileColumns.COMPUTER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_af_user_computer_group_id_transfer_key UNIQUE (user_id, computer_id, group_id, transfer_key) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_ASSET_FILE_GROUP_ID = "CREATE INDEX IDX_ASSET_FILE_GROUP_ID "
            + " ON " + AssetFileColumns.TABLE_NAME + " ( " + AssetFileColumns.GROUP_ID + " );";

    public static final String SQL_CREATE_INDEX_ASSET_FILE_TRANSFER_KEY = "CREATE INDEX IDX_ASSET_FILE_TRANSFER_KEY "
            + " ON " + AssetFileColumns.TABLE_NAME + " ( " + AssetFileColumns.TRANSFER_KEY + " );";

    public static final String SQL_CREATE_INDEX_ASSET_FILE_STATUS = "CREATE INDEX IDX_ASSET_FILE_STATUS "
            + " ON " + AssetFileColumns.TABLE_NAME + " ( " + AssetFileColumns.STATUS + " );";

    public static final String SQL_CREATE_INDEX_ASSET_FILE_WAIT_TO_CONFIRM = "CREATE INDEX IDX_ASSET_FILE_WAIT_TO_CONFIRM "
            + " ON " + AssetFileColumns.TABLE_NAME + " ( " + AssetFileColumns.WAIT_TO_CONFIRM + " );";

    public static final String SQL_CREATE_INDEX_ASSET_FILE_USER_ID = "CREATE INDEX IDX_ASSET_FILE_USER_ID "
            + " ON " + AssetFileColumns.TABLE_NAME + " ( " + AssetFileColumns.USER_ID + " );";

    public static final String SQL_CREATE_INDEX_ASSET_FILE_COMPUTER_ID = "CREATE INDEX IDX_ASSET_FILE_COMPUTER_ID "
            + " ON " + AssetFileColumns.TABLE_NAME + " ( " + AssetFileColumns.COMPUTER_ID + " );";

    /*
    // v5 Origin
    public static final String SQL_CREATE_TABLE_COUNTRY = "CREATE TABLE IF NOT EXISTS "
            + CountryColumns.TABLE_NAME + " ( "
            + CountryColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CountryColumns.COUNTRY_ID + " TEXT NOT NULL, "
            + CountryColumns.COUNTRY_CODE + " TEXT NOT NULL, "
            + CountryColumns.COUNTRY_NAME + " TEXT NOT NULL, "
            + CountryColumns.PHONE_SAMPLE + " TEXT "
            + ", CONSTRAINT unique_c_country UNIQUE (country_id) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_COUNTRY_COUNTRY_ID = "CREATE INDEX IDX_COUNTRY_COUNTRY_ID "
            + " ON " + CountryColumns.TABLE_NAME + " ( " + CountryColumns.COUNTRY_ID + " );";

    public static final String SQL_CREATE_INDEX_COUNTRY_COUNTRY_CODE = "CREATE INDEX IDX_COUNTRY_COUNTRY_CODE "
            + " ON " + CountryColumns.TABLE_NAME + " ( " + CountryColumns.COUNTRY_CODE + " );";
    */

    /*
    // v2 Origin
    public static final String SQL_CREATE_TABLE_DOWNLOAD_GROUP = "CREATE TABLE IF NOT EXISTS "
            + DownloadGroupColumns.TABLE_NAME + " ( "
            + DownloadGroupColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DownloadGroupColumns.GROUP_ID + " TEXT NOT NULL, "
            + DownloadGroupColumns.START_TIMESTAMP + " INTEGER NOT NULL, "
            + DownloadGroupColumns.LOCAL_PATH + " TEXT NOT NULL, "
            + DownloadGroupColumns.SUBDIRECTORY_TYPE + " INTEGER NOT NULL DEFAULT 0, "
            + DownloadGroupColumns.SUBDIRECTORY_VALUE + " TEXT, "
            + DownloadGroupColumns.DESCRIPTION_TYPE + " INTEGER NOT NULL DEFAULT 0, "
            + DownloadGroupColumns.DESCRIPTION_VALUE + " TEXT, "
            + DownloadGroupColumns.NOTIFICATION_TYPE + " INTEGER NOT NULL DEFAULT 0, "
            + DownloadGroupColumns.USER_ID + " TEXT NOT NULL, "
            + DownloadGroupColumns.COMPUTER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_dg_user_computer_group_id UNIQUE (user_id, computer_id, group_id) ON CONFLICT REPLACE"
            + " );";
    */
    // v3
    public static final String SQL_CREATE_TABLE_DOWNLOAD_GROUP = "CREATE TABLE IF NOT EXISTS "
            + DownloadGroupColumns.TABLE_NAME + " ( "
            + DownloadGroupColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DownloadGroupColumns.GROUP_ID + " TEXT NOT NULL, "
            + DownloadGroupColumns.FROM_ANOTHER_APP + " INTEGER NOT NULL DEFAULT 0, " // v3 add this column! //
            + DownloadGroupColumns.START_TIMESTAMP + " INTEGER NOT NULL, "
            + DownloadGroupColumns.LOCAL_PATH + " TEXT NOT NULL, "
            + DownloadGroupColumns.SUBDIRECTORY_TYPE + " INTEGER NOT NULL DEFAULT 0, "
            + DownloadGroupColumns.SUBDIRECTORY_VALUE + " TEXT, "
            + DownloadGroupColumns.DESCRIPTION_TYPE + " INTEGER NOT NULL DEFAULT 0, "
            + DownloadGroupColumns.DESCRIPTION_VALUE + " TEXT, "
            + DownloadGroupColumns.NOTIFICATION_TYPE + " INTEGER NOT NULL DEFAULT 0, "
            + DownloadGroupColumns.USER_ID + " TEXT NOT NULL, "
            + DownloadGroupColumns.COMPUTER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_dg_user_computer_group_id UNIQUE (user_id, computer_id, group_id) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_DOWNLOAD_GROUP_GROUP_ID = "CREATE INDEX IDX_DOWNLOAD_GROUP_GROUP_ID "
            + " ON " + DownloadGroupColumns.TABLE_NAME + " ( " + DownloadGroupColumns.GROUP_ID + " );";

    public static final String SQL_CREATE_INDEX_DOWNLOAD_GROUP_START_TIMESTAMP = "CREATE INDEX IDX_DOWNLOAD_GROUP_START_TIMESTAMP "
            + " ON " + DownloadGroupColumns.TABLE_NAME + " ( " + DownloadGroupColumns.START_TIMESTAMP + " );";

    public static final String SQL_CREATE_INDEX_DOWNLOAD_GROUP_USER_ID = "CREATE INDEX IDX_DOWNLOAD_GROUP_USER_ID "
            + " ON " + DownloadGroupColumns.TABLE_NAME + " ( " + DownloadGroupColumns.USER_ID + " );";

    public static final String SQL_CREATE_INDEX_DOWNLOAD_GROUP_COMPUTER_ID = "CREATE INDEX IDX_DOWNLOAD_GROUP_COMPUTER_ID "
            + " ON " + DownloadGroupColumns.TABLE_NAME + " ( " + DownloadGroupColumns.COMPUTER_ID + " );";

    public static final String SQL_CREATE_TABLE_DOWNLOAD_HISTORY = "CREATE TABLE IF NOT EXISTS "
            + DownloadHistoryColumns.TABLE_NAME + " ( "
            + DownloadHistoryColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DownloadHistoryColumns.COMPUTER_GROUP + " TEXT NOT NULL, "
            + DownloadHistoryColumns.COMPUTER_NAME + " TEXT NOT NULL, "
            + DownloadHistoryColumns.FILE_SIZE + " INTEGER NOT NULL, "
            + DownloadHistoryColumns.END_TIMESTAMP + " INTEGER NOT NULL, "
            + DownloadHistoryColumns.FILE_NAME + " TEXT, "
            + DownloadHistoryColumns.USER_ID + " TEXT NOT NULL, "
            + DownloadHistoryColumns.COMPUTER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_dh_user_computer_end_timestamp UNIQUE (user_id, computer_id, end_timestamp) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_DOWNLOAD_HISTORY_END_TIMESTAMP = "CREATE INDEX IDX_DOWNLOAD_HISTORY_END_TIMESTAMP "
            + " ON " + DownloadHistoryColumns.TABLE_NAME + " ( " + DownloadHistoryColumns.END_TIMESTAMP + " );";

    /*
    // v1 Origin
    public static final String SQL_CREATE_TABLE_FILE_TRANSFER = "CREATE TABLE IF NOT EXISTS "
            + FileTransferColumns.TABLE_NAME + " ( "
            + FileTransferColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FileTransferColumns.GROUP_ID + " TEXT NOT NULL, "
            + FileTransferColumns.TRANSFER_KEY + " TEXT NOT NULL, "
            + FileTransferColumns.TYPE + " INTEGER NOT NULL, "
            + FileTransferColumns.SERVER_PATH + " TEXT NOT NULL, "
            + FileTransferColumns.REAL_SERVER_PATH + " TEXT, "
            + FileTransferColumns.LOCAL_FILE_NAME + " TEXT NOT NULL, "
            + FileTransferColumns.CONTENT_TYPE + " TEXT, "
            + FileTransferColumns.LAST_MODIFIED + " TEXT NOT NULL, "
            + FileTransferColumns.PARAM_STATUS + " INTEGER NOT NULL, "
            + FileTransferColumns.START_TIMESTAMP + " INTEGER, "
            + FileTransferColumns.END_TIMESTAMP + " INTEGER, "
            + FileTransferColumns.TOTAL_SIZE + " INTEGER, "
            + FileTransferColumns.TRANSFERRED_SIZE + " INTEGER, "
            + FileTransferColumns.WAIT_TO_CONFIRM + " INTEGER NOT NULL DEFAULT 0, "
            + FileTransferColumns.ACTIONS_AFTER_DOWNLOAD + " TEXT, "
            + FileTransferColumns.USER_ID + " TEXT NOT NULL, "
            + FileTransferColumns.COMPUTER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_ft_user_computer_group_id_transfer_key UNIQUE (user_id, computer_id, group_id, transfer_key) ON CONFLICT REPLACE"
            + " );";
    */
    //
    /* v2 Origin
    public static final String SQL_CREATE_TABLE_FILE_TRANSFER = "CREATE TABLE IF NOT EXISTS "
            + FileTransferColumns.TABLE_NAME + " ( "
            + FileTransferColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FileTransferColumns.GROUP_ID + " TEXT NOT NULL, "
            + FileTransferColumns.TRANSFER_KEY + " TEXT NOT NULL, "
            + FileTransferColumns.TYPE + " INTEGER NOT NULL, "
            + FileTransferColumns.SERVER_PATH + " TEXT NOT NULL, "
            + FileTransferColumns.REAL_SERVER_PATH + " TEXT, "
            + FileTransferColumns.LOCAL_FILE_NAME + " TEXT NOT NULL, "
            + FileTransferColumns.SAVED_FILE_NAME + " TEXT, " // v2 add this column! //
            + FileTransferColumns.CONTENT_TYPE + " TEXT, "
            + FileTransferColumns.LAST_MODIFIED + " TEXT NOT NULL, "
            + FileTransferColumns.PARAM_STATUS + " INTEGER NOT NULL, "
            + FileTransferColumns.START_TIMESTAMP + " INTEGER, "
            + FileTransferColumns.END_TIMESTAMP + " INTEGER, "
            + FileTransferColumns.TOTAL_SIZE + " INTEGER, "
            + FileTransferColumns.TRANSFERRED_SIZE + " INTEGER, "
            + FileTransferColumns.WAIT_TO_CONFIRM + " INTEGER NOT NULL DEFAULT 0, "
            + FileTransferColumns.ACTIONS_AFTER_DOWNLOAD + " TEXT, "
            + FileTransferColumns.USER_ID + " TEXT NOT NULL, "
            + FileTransferColumns.COMPUTER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_ft_user_computer_group_id_transfer_key UNIQUE (user_id, computer_id, group_id, transfer_key) ON CONFLICT REPLACE"
            + " );";
    */
    // v3
    public static final String SQL_CREATE_TABLE_FILE_TRANSFER = "CREATE TABLE IF NOT EXISTS "
            + FileTransferColumns.TABLE_NAME + " ( "
            + FileTransferColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FileTransferColumns.GROUP_ID + " TEXT NOT NULL, "
            + FileTransferColumns.TRANSFER_KEY + " TEXT NOT NULL, "
            + FileTransferColumns.TYPE + " INTEGER NOT NULL, "
            + FileTransferColumns.FILE_IN_CACHE + " INTEGER NOT NULL DEFAULT 0, " // v3 add this column! //
            + FileTransferColumns.SERVER_PATH + " TEXT NOT NULL, "
            + FileTransferColumns.REAL_SERVER_PATH + " TEXT, "
            + FileTransferColumns.LOCAL_FILE_NAME + " TEXT NOT NULL, "
            + FileTransferColumns.REAL_LOCAL_FILE_NAME + " TEXT, " // v3 add this column! //
            + FileTransferColumns.SAVED_FILE_NAME + " TEXT, "
            + FileTransferColumns.CONTENT_TYPE + " TEXT, "
            + FileTransferColumns.LAST_MODIFIED + " TEXT NOT NULL, "
            + FileTransferColumns.STATUS + " INTEGER NOT NULL, "
            + FileTransferColumns.START_TIMESTAMP + " INTEGER, "
            + FileTransferColumns.END_TIMESTAMP + " INTEGER, "
            + FileTransferColumns.TOTAL_SIZE + " INTEGER, "
            + FileTransferColumns.TRANSFERRED_SIZE + " INTEGER, "
            + FileTransferColumns.WAIT_TO_CONFIRM + " INTEGER NOT NULL DEFAULT 0, "
            + FileTransferColumns.ACTIONS_AFTER_DOWNLOAD + " TEXT, "
            + FileTransferColumns.USER_ID + " TEXT NOT NULL, "
            + FileTransferColumns.COMPUTER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_ft_user_computer_group_id_transfer_key UNIQUE (user_id, computer_id, group_id, transfer_key) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_FILE_TRANSFER_GROUP_ID = "CREATE INDEX IDX_FILE_TRANSFER_GROUP_ID "
            + " ON " + FileTransferColumns.TABLE_NAME + " ( " + FileTransferColumns.GROUP_ID + " );";

    public static final String SQL_CREATE_INDEX_FILE_TRANSFER_TRANSFER_KEY = "CREATE INDEX IDX_FILE_TRANSFER_TRANSFER_KEY "
            + " ON " + FileTransferColumns.TABLE_NAME + " ( " + FileTransferColumns.TRANSFER_KEY + " );";

    public static final String SQL_CREATE_INDEX_FILE_TRANSFER_STATUS = "CREATE INDEX IDX_FILE_TRANSFER_STATUS "
            + " ON " + FileTransferColumns.TABLE_NAME + " ( " + FileTransferColumns.STATUS + " );";

    public static final String SQL_CREATE_INDEX_FILE_TRANSFER_WAIT_TO_CONFIRM = "CREATE INDEX IDX_FILE_TRANSFER_WAIT_TO_CONFIRM "
            + " ON " + FileTransferColumns.TABLE_NAME + " ( " + FileTransferColumns.WAIT_TO_CONFIRM + " );";

    public static final String SQL_CREATE_INDEX_FILE_TRANSFER_USER_ID = "CREATE INDEX IDX_FILE_TRANSFER_USER_ID "
            + " ON " + FileTransferColumns.TABLE_NAME + " ( " + FileTransferColumns.USER_ID + " );";

    public static final String SQL_CREATE_INDEX_FILE_TRANSFER_COMPUTER_ID = "CREATE INDEX IDX_FILE_TRANSFER_COMPUTER_ID "
            + " ON " + FileTransferColumns.TABLE_NAME + " ( " + FileTransferColumns.COMPUTER_ID + " );";

    /*
    // v2 Origin
    public static final String SQL_CREATE_TABLE_REMOTE_HIERARCHICAL_MODEL = "CREATE TABLE IF NOT EXISTS "
            + RemoteHierarchicalModelColumns.TABLE_NAME + " ( "
            + RemoteHierarchicalModelColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RemoteHierarchicalModelColumns.SYMLINK + " INTEGER NOT NULL DEFAULT 0, "
            + RemoteHierarchicalModelColumns.PARENT + " TEXT NOT NULL, "
            + RemoteHierarchicalModelColumns.NAME + " TEXT NOT NULL, "
            + RemoteHierarchicalModelColumns.READABLE + " INTEGER NOT NULL DEFAULT 0, "
            + RemoteHierarchicalModelColumns.WRITABLE + " INTEGER NOT NULL DEFAULT 0, "
            + RemoteHierarchicalModelColumns.HIDDEN + " INTEGER NOT NULL DEFAULT 0, "
            + RemoteHierarchicalModelColumns.LAST_MODIFIED + " TEXT NOT NULL, "
            + RemoteHierarchicalModelColumns.TYPE + " INTEGER NOT NULL, "
            + RemoteHierarchicalModelColumns.CONTENT_TYPE + " TEXT NOT NULL, "
            + RemoteHierarchicalModelColumns.SIZE + " INTEGER NOT NULL DEFAULT 0, "
            + RemoteHierarchicalModelColumns.REAL_PARENT + " TEXT, "
            + RemoteHierarchicalModelColumns.REAL_NAME + " TEXT, "
            + RemoteHierarchicalModelColumns.USER_ID + " TEXT NOT NULL, "
            + RemoteHierarchicalModelColumns.COMPUTER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_rhm_user_computer_directory UNIQUE (user_id, computer_id, parent, name) ON CONFLICT REPLACE"
            + " );";
    */
    /*
    // v3
    public static final String SQL_CREATE_TABLE_REMOTE_HIERARCHICAL_MODEL = "CREATE TABLE IF NOT EXISTS "
            + RemoteHierarchicalModelColumns.TABLE_NAME + " ( "
            + RemoteHierarchicalModelColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RemoteHierarchicalModelColumns.SYMLINK + " INTEGER NOT NULL DEFAULT 0, "
            + RemoteHierarchicalModelColumns.PARENT + " TEXT NOT NULL, "
            + RemoteHierarchicalModelColumns.NAME + " TEXT NOT NULL, "
            + RemoteHierarchicalModelColumns.READABLE + " INTEGER NOT NULL DEFAULT 0, "
            + RemoteHierarchicalModelColumns.WRITABLE + " INTEGER NOT NULL DEFAULT 0, "
            + RemoteHierarchicalModelColumns.HIDDEN + " INTEGER NOT NULL DEFAULT 0, "
            + RemoteHierarchicalModelColumns.LAST_MODIFIED + " TEXT NOT NULL, "
            + RemoteHierarchicalModelColumns.TYPE + " INTEGER NOT NULL, "
            + RemoteHierarchicalModelColumns.CONTENT_TYPE + " TEXT NOT NULL, "
            + RemoteHierarchicalModelColumns.SIZE + " INTEGER NOT NULL DEFAULT 0, "
            + RemoteHierarchicalModelColumns.REAL_PARENT + " TEXT, "
            + RemoteHierarchicalModelColumns.REAL_NAME + " TEXT, "
            + RemoteHierarchicalModelColumns.LOCAL_LAST_MODIFIED + " INTEGER, " // v3 add this column! //
            + RemoteHierarchicalModelColumns.LOCAL_SIZE + " INTEGER, " // v3 add this column! //
            + RemoteHierarchicalModelColumns.USER_ID + " TEXT NOT NULL, "
            + RemoteHierarchicalModelColumns.COMPUTER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_rhm_user_computer_directory UNIQUE (user_id, computer_id, parent, name) ON CONFLICT REPLACE"
            + " );";
    */
    // v4
    public static final String SQL_CREATE_TABLE_REMOTE_HIERARCHICAL_MODEL = "CREATE TABLE IF NOT EXISTS "
            + RemoteHierarchicalModelColumns.TABLE_NAME + " ( "
            + RemoteHierarchicalModelColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RemoteHierarchicalModelColumns.SYMLINK + " INTEGER NOT NULL DEFAULT 0, "
            + RemoteHierarchicalModelColumns.PARENT + " TEXT NOT NULL, "
            + RemoteHierarchicalModelColumns.NAME + " TEXT NOT NULL, "
            + RemoteHierarchicalModelColumns.READABLE + " INTEGER NOT NULL DEFAULT 0, "
            + RemoteHierarchicalModelColumns.WRITABLE + " INTEGER NOT NULL DEFAULT 0, "
            + RemoteHierarchicalModelColumns.HIDDEN + " INTEGER NOT NULL DEFAULT 0, "
            + RemoteHierarchicalModelColumns.LAST_MODIFIED + " TEXT NOT NULL, "
            + RemoteHierarchicalModelColumns.TYPE + " INTEGER NOT NULL, "
            + RemoteHierarchicalModelColumns.CONTENT_TYPE + " TEXT NOT NULL, "
            + RemoteHierarchicalModelColumns.SIZE + " INTEGER NOT NULL DEFAULT 0, "
            + RemoteHierarchicalModelColumns.REAL_PARENT + " TEXT, "
            + RemoteHierarchicalModelColumns.REAL_NAME + " TEXT, "
            + RemoteHierarchicalModelColumns.LOCAL_LAST_MODIFIED + " INTEGER, "
            + RemoteHierarchicalModelColumns.LOCAL_SIZE + " INTEGER, "
            + RemoteHierarchicalModelColumns.LOCAL_LAST_ACCESS + " INTEGER, " // v4 add this column! //
            + RemoteHierarchicalModelColumns.USER_ID + " TEXT NOT NULL, "
            + RemoteHierarchicalModelColumns.COMPUTER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_rhm_user_computer_directory UNIQUE (user_id, computer_id, parent, name) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_REMOTE_HIERARCHICAL_MODEL_PARENT = "CREATE INDEX IDX_REMOTE_HIERARCHICAL_MODEL_PARENT "
            + " ON " + RemoteHierarchicalModelColumns.TABLE_NAME + " ( " + RemoteHierarchicalModelColumns.PARENT + " );";

    public static final String SQL_CREATE_INDEX_REMOTE_HIERARCHICAL_MODEL_NAME = "CREATE INDEX IDX_REMOTE_HIERARCHICAL_MODEL_NAME "
            + " ON " + RemoteHierarchicalModelColumns.TABLE_NAME + " ( " + RemoteHierarchicalModelColumns.NAME + " );";

    public static final String SQL_CREATE_INDEX_REMOTE_HIERARCHICAL_MODEL_TYPE = "CREATE INDEX IDX_REMOTE_HIERARCHICAL_MODEL_TYPE "
            + " ON " + RemoteHierarchicalModelColumns.TABLE_NAME + " ( " + RemoteHierarchicalModelColumns.TYPE + " );";

    public static final String SQL_CREATE_INDEX_REMOTE_HIERARCHICAL_MODEL_USER_ID = "CREATE INDEX IDX_REMOTE_HIERARCHICAL_MODEL_USER_ID "
            + " ON " + RemoteHierarchicalModelColumns.TABLE_NAME + " ( " + RemoteHierarchicalModelColumns.USER_ID + " );";

    public static final String SQL_CREATE_INDEX_REMOTE_HIERARCHICAL_MODEL_COMPUTER_ID = "CREATE INDEX IDX_REMOTE_HIERARCHICAL_MODEL_COMPUTER_ID "
            + " ON " + RemoteHierarchicalModelColumns.TABLE_NAME + " ( " + RemoteHierarchicalModelColumns.COMPUTER_ID + " );";

    /*
    // v5 Origin
    public static final String SQL_CREATE_TABLE_REMOTE_ROOT_DIRECTORY = "CREATE TABLE IF NOT EXISTS "
            + RemoteRootDirectoryColumns.TABLE_NAME + " ( "
            + RemoteRootDirectoryColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RemoteRootDirectoryColumns.DIRECTORY_ID + " TEXT NOT NULL, " /////////////
            + RemoteRootDirectoryColumns.DIRECTORY_LABEL + " TEXT NOT NULL, "
            + RemoteRootDirectoryColumns.DIRECTORY_PATH + " TEXT NOT NULL, "
            + RemoteRootDirectoryColumns.DIRECTORY_REAL_PATH + " TEXT NOT NULL, " /////////////
            + RemoteRootDirectoryColumns.DIRECTORY_TYPE + " INTEGER NOT NULL, "
            + RemoteRootDirectoryColumns.USER_ID + " TEXT NOT NULL, "
            + RemoteRootDirectoryColumns.COMPUTER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_rrd_user_computer_directory UNIQUE (user_id, computer_id, directory_id) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_REMOTE_ROOT_DIRECTORY_DIRECTORY_ID = "CREATE INDEX IDX_REMOTE_ROOT_DIRECTORY_DIRECTORY_ID "
            + " ON " + RemoteRootDirectoryColumns.TABLE_NAME + " ( " + RemoteRootDirectoryColumns.DIRECTORY_ID + " );";

    public static final String SQL_CREATE_INDEX_REMOTE_ROOT_DIRECTORY_USER_ID = "CREATE INDEX IDX_REMOTE_ROOT_DIRECTORY_USER_ID "
            + " ON " + RemoteRootDirectoryColumns.TABLE_NAME + " ( " + RemoteRootDirectoryColumns.USER_ID + " );";

    public static final String SQL_CREATE_INDEX_REMOTE_ROOT_DIRECTORY_COMPUTER_ID = "CREATE INDEX IDX_REMOTE_ROOT_DIRECTORY_COMPUTER_ID "
            + " ON " + RemoteRootDirectoryColumns.TABLE_NAME + " ( " + RemoteRootDirectoryColumns.COMPUTER_ID + " );";
    */
    // v6, Start ==>
    public static final String SQL_CREATE_TABLE_REMOTE_ROOT = "CREATE TABLE IF NOT EXISTS "
            + RemoteRootColumns.TABLE_NAME + " ( "
            + RemoteRootColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RemoteRootColumns.LABEL + " TEXT NOT NULL, "
            + RemoteRootColumns.PATH + " TEXT NOT NULL, "
            + RemoteRootColumns.REAL_PATH + " TEXT NOT NULL, "
            + RemoteRootColumns.TYPE + " INTEGER NOT NULL, "
            + RemoteRootColumns.USER_ID + " TEXT NOT NULL, "
            + RemoteRootColumns.COMPUTER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_rr_user_computer_path_real_path UNIQUE (user_id, computer_id, path, real_path) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_REMOTE_ROOT_LABEL = "CREATE INDEX IDX_REMOTE_ROOT_LABEL "
            + " ON " + RemoteRootColumns.TABLE_NAME + " ( " + RemoteRootColumns.LABEL + " );";

    public static final String SQL_CREATE_INDEX_REMOTE_ROOT_PATH = "CREATE INDEX IDX_REMOTE_ROOT_PATH "
            + " ON " + RemoteRootColumns.TABLE_NAME + " ( " + RemoteRootColumns.PATH + " );";

    public static final String SQL_CREATE_INDEX_REMOTE_ROOT_USER_COMPUTER_LABEL = "CREATE INDEX IDX_REMOTE_ROOT_USER_COMPUTER_LABEL "
            + " ON " + RemoteRootColumns.TABLE_NAME + " ( " + RemoteRootColumns.USER_ID + ", " + RemoteRootColumns.COMPUTER_ID + ", " + RemoteRootColumns.LABEL + " );";

    public static final String SQL_CREATE_INDEX_REMOTE_ROOT_USER_COMPUTER_PATH = "CREATE INDEX IDX_REMOTE_ROOT_USER_COMPUTER_PATH "
            + " ON " + RemoteRootColumns.TABLE_NAME + " ( " + RemoteRootColumns.USER_ID + ", " + RemoteRootColumns.COMPUTER_ID + ", " + RemoteRootColumns.PATH + " );";
    // v6, End <==

    /*
    // v2 Origin
    public static final String SQL_CREATE_TABLE_UPLOAD_GROUP = "CREATE TABLE IF NOT EXISTS "
            + UploadGroupColumns.TABLE_NAME + " ( "
            + UploadGroupColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + UploadGroupColumns.GROUP_ID + " TEXT NOT NULL, "
            + UploadGroupColumns.START_TIMESTAMP + " INTEGER NOT NULL, "
            + UploadGroupColumns.UPLOAD_DIRECTORY + " TEXT NOT NULL, "
            + UploadGroupColumns.SUBDIRECTORY_TYPE + " INTEGER NOT NULL DEFAULT 0, "
            + UploadGroupColumns.SUBDIRECTORY_VALUE + " TEXT, "
            + UploadGroupColumns.DESCRIPTION_TYPE + " INTEGER NOT NULL DEFAULT 0, "
            + UploadGroupColumns.DESCRIPTION_VALUE + " TEXT, "
            + UploadGroupColumns.NOTIFICATION_TYPE + " INTEGER NOT NULL DEFAULT 0, "
            + UploadGroupColumns.USER_ID + " TEXT NOT NULL, "
            + UploadGroupColumns.COMPUTER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_ug_user_computer_group_id UNIQUE (user_id, computer_id, group_id) ON CONFLICT REPLACE"
            + " );";
    */
    // v3
    public static final String SQL_CREATE_TABLE_UPLOAD_GROUP = "CREATE TABLE IF NOT EXISTS "
            + UploadGroupColumns.TABLE_NAME + " ( "
            + UploadGroupColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + UploadGroupColumns.GROUP_ID + " TEXT NOT NULL, "
            + UploadGroupColumns.FROM_ANOTHER_APP + " INTEGER NOT NULL DEFAULT 0, " // v3 add this column! //
            + UploadGroupColumns.START_TIMESTAMP + " INTEGER NOT NULL, "
            + UploadGroupColumns.UPLOAD_DIRECTORY + " TEXT NOT NULL, "
            + UploadGroupColumns.SUBDIRECTORY_TYPE + " INTEGER NOT NULL DEFAULT 0, "
            + UploadGroupColumns.SUBDIRECTORY_VALUE + " TEXT, "
            + UploadGroupColumns.DESCRIPTION_TYPE + " INTEGER NOT NULL DEFAULT 0, "
            + UploadGroupColumns.DESCRIPTION_VALUE + " TEXT, "
            + UploadGroupColumns.NOTIFICATION_TYPE + " INTEGER NOT NULL DEFAULT 0, "
            + UploadGroupColumns.USER_ID + " TEXT NOT NULL, "
            + UploadGroupColumns.COMPUTER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_ug_user_computer_group_id UNIQUE (user_id, computer_id, group_id) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_UPLOAD_GROUP_GROUP_ID = "CREATE INDEX IDX_UPLOAD_GROUP_GROUP_ID "
            + " ON " + UploadGroupColumns.TABLE_NAME + " ( " + UploadGroupColumns.GROUP_ID + " );";

    public static final String SQL_CREATE_INDEX_UPLOAD_GROUP_START_TIMESTAMP = "CREATE INDEX IDX_UPLOAD_GROUP_START_TIMESTAMP "
            + " ON " + UploadGroupColumns.TABLE_NAME + " ( " + UploadGroupColumns.START_TIMESTAMP + " );";

    public static final String SQL_CREATE_INDEX_UPLOAD_GROUP_USER_ID = "CREATE INDEX IDX_UPLOAD_GROUP_USER_ID "
            + " ON " + UploadGroupColumns.TABLE_NAME + " ( " + UploadGroupColumns.USER_ID + " );";

    public static final String SQL_CREATE_INDEX_UPLOAD_GROUP_COMPUTER_ID = "CREATE INDEX IDX_UPLOAD_GROUP_COMPUTER_ID "
            + " ON " + UploadGroupColumns.TABLE_NAME + " ( " + UploadGroupColumns.COMPUTER_ID + " );";

    public static final String SQL_CREATE_TABLE_UPLOAD_HISTORY = "CREATE TABLE IF NOT EXISTS "
            + UploadHistoryColumns.TABLE_NAME + " ( "
            + UploadHistoryColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + UploadHistoryColumns.COMPUTER_GROUP + " TEXT NOT NULL, "
            + UploadHistoryColumns.COMPUTER_NAME + " TEXT NOT NULL, "
            + UploadHistoryColumns.FILE_SIZE + " INTEGER NOT NULL, "
            + UploadHistoryColumns.END_TIMESTAMP + " INTEGER NOT NULL, "
            + UploadHistoryColumns.FILE_NAME + " TEXT, "
            + UploadHistoryColumns.USER_ID + " TEXT NOT NULL, "
            + UploadHistoryColumns.COMPUTER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_uh_user_computer_end_timestamp UNIQUE (user_id, computer_id, end_timestamp) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_UPLOAD_HISTORY_END_TIMESTAMP = "CREATE INDEX IDX_UPLOAD_HISTORY_END_TIMESTAMP "
            + " ON " + UploadHistoryColumns.TABLE_NAME + " ( " + UploadHistoryColumns.END_TIMESTAMP + " );";

    public static final String SQL_CREATE_INDEX_UPLOAD_HISTORY_USER_ID = "CREATE INDEX IDX_UPLOAD_HISTORY_USER_ID "
            + " ON " + UploadHistoryColumns.TABLE_NAME + " ( " + UploadHistoryColumns.USER_ID + " );";

    public static final String SQL_CREATE_INDEX_UPLOAD_HISTORY_COMPUTER_ID = "CREATE INDEX IDX_UPLOAD_HISTORY_COMPUTER_ID "
            + " ON " + UploadHistoryColumns.TABLE_NAME + " ( " + UploadHistoryColumns.COMPUTER_ID + " );";

    public static final String SQL_CREATE_TABLE_USER_COMPUTER = "CREATE TABLE IF NOT EXISTS "
            + UserComputerColumns.TABLE_NAME + " ( "
            + UserComputerColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + UserComputerColumns.COMPUTER_ID + " INTEGER NOT NULL, "
            + UserComputerColumns.COMPUTER_NAME + " TEXT NOT NULL, "
            + UserComputerColumns.COMPUTER_GROUP + " TEXT NOT NULL, "
            + UserComputerColumns.COMPUTER_ADMIN_ID + " TEXT NOT NULL, "
            + UserComputerColumns.USER_ID + " TEXT NOT NULL, "
            + UserComputerColumns.USER_COMPUTER_ID + " TEXT NOT NULL, "
            + UserComputerColumns.LUG_SERVER_ID + " TEXT "
            + ", CONSTRAINT unique_uc_user_computer UNIQUE (user_id, computer_id) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_USER_COMPUTER_COMPUTER_ID = "CREATE INDEX IDX_USER_COMPUTER_COMPUTER_ID "
            + " ON " + UserComputerColumns.TABLE_NAME + " ( " + UserComputerColumns.COMPUTER_ID + " );";

    public static final String SQL_CREATE_INDEX_USER_COMPUTER_USER_ID = "CREATE INDEX IDX_USER_COMPUTER_USER_ID "
            + " ON " + UserComputerColumns.TABLE_NAME + " ( " + UserComputerColumns.USER_ID + " );";

    public static final String SQL_CREATE_INDEX_USER_COMPUTER_USER_COMPUTER_ID = "CREATE INDEX IDX_USER_COMPUTER_USER_COMPUTER_ID "
            + " ON " + UserComputerColumns.TABLE_NAME + " ( " + UserComputerColumns.USER_COMPUTER_ID + " );";

    // @formatter:on

    public static FilelugSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static FilelugSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */
    private static FilelugSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new FilelugSQLiteOpenHelper(context);
    }

    private FilelugSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new FilelugSQLiteOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static FilelugSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new FilelugSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private FilelugSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new FilelugSQLiteOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
//        if (Constants.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_ASSET_FILE);
        db.execSQL(SQL_CREATE_INDEX_ASSET_FILE_GROUP_ID);
        db.execSQL(SQL_CREATE_INDEX_ASSET_FILE_TRANSFER_KEY);
        db.execSQL(SQL_CREATE_INDEX_ASSET_FILE_STATUS);
        db.execSQL(SQL_CREATE_INDEX_ASSET_FILE_WAIT_TO_CONFIRM);
        db.execSQL(SQL_CREATE_INDEX_ASSET_FILE_USER_ID);
        db.execSQL(SQL_CREATE_INDEX_ASSET_FILE_COMPUTER_ID);
        db.execSQL(SQL_CREATE_TABLE_DOWNLOAD_GROUP);
        db.execSQL(SQL_CREATE_INDEX_DOWNLOAD_GROUP_GROUP_ID);
        db.execSQL(SQL_CREATE_INDEX_DOWNLOAD_GROUP_START_TIMESTAMP);
        db.execSQL(SQL_CREATE_INDEX_DOWNLOAD_GROUP_USER_ID);
        db.execSQL(SQL_CREATE_INDEX_DOWNLOAD_GROUP_COMPUTER_ID);
        db.execSQL(SQL_CREATE_TABLE_DOWNLOAD_HISTORY);
        db.execSQL(SQL_CREATE_INDEX_DOWNLOAD_HISTORY_END_TIMESTAMP);
        db.execSQL(SQL_CREATE_TABLE_FILE_TRANSFER);
        db.execSQL(SQL_CREATE_INDEX_FILE_TRANSFER_GROUP_ID);
        db.execSQL(SQL_CREATE_INDEX_FILE_TRANSFER_TRANSFER_KEY);
        db.execSQL(SQL_CREATE_INDEX_FILE_TRANSFER_STATUS);
        db.execSQL(SQL_CREATE_INDEX_FILE_TRANSFER_WAIT_TO_CONFIRM);
        db.execSQL(SQL_CREATE_INDEX_FILE_TRANSFER_USER_ID);
        db.execSQL(SQL_CREATE_INDEX_FILE_TRANSFER_COMPUTER_ID);
        db.execSQL(SQL_CREATE_TABLE_REMOTE_HIERARCHICAL_MODEL);
        db.execSQL(SQL_CREATE_INDEX_REMOTE_HIERARCHICAL_MODEL_PARENT);
        db.execSQL(SQL_CREATE_INDEX_REMOTE_HIERARCHICAL_MODEL_NAME);
        db.execSQL(SQL_CREATE_INDEX_REMOTE_HIERARCHICAL_MODEL_TYPE);
        db.execSQL(SQL_CREATE_INDEX_REMOTE_HIERARCHICAL_MODEL_USER_ID);
        db.execSQL(SQL_CREATE_INDEX_REMOTE_HIERARCHICAL_MODEL_COMPUTER_ID);
        db.execSQL(SQL_CREATE_TABLE_REMOTE_ROOT);
        db.execSQL(SQL_CREATE_INDEX_REMOTE_ROOT_LABEL);
        db.execSQL(SQL_CREATE_INDEX_REMOTE_ROOT_PATH);
        db.execSQL(SQL_CREATE_INDEX_REMOTE_ROOT_USER_COMPUTER_LABEL);
        db.execSQL(SQL_CREATE_INDEX_REMOTE_ROOT_USER_COMPUTER_PATH);
        db.execSQL(SQL_CREATE_TABLE_UPLOAD_GROUP);
        db.execSQL(SQL_CREATE_INDEX_UPLOAD_GROUP_GROUP_ID);
        db.execSQL(SQL_CREATE_INDEX_UPLOAD_GROUP_START_TIMESTAMP);
        db.execSQL(SQL_CREATE_INDEX_UPLOAD_GROUP_USER_ID);
        db.execSQL(SQL_CREATE_INDEX_UPLOAD_GROUP_COMPUTER_ID);
        db.execSQL(SQL_CREATE_TABLE_UPLOAD_HISTORY);
        db.execSQL(SQL_CREATE_INDEX_UPLOAD_HISTORY_END_TIMESTAMP);
        db.execSQL(SQL_CREATE_INDEX_UPLOAD_HISTORY_USER_ID);
        db.execSQL(SQL_CREATE_INDEX_UPLOAD_HISTORY_COMPUTER_ID);
        db.execSQL(SQL_CREATE_TABLE_USER_COMPUTER);
        db.execSQL(SQL_CREATE_INDEX_USER_COMPUTER_COMPUTER_ID);
        db.execSQL(SQL_CREATE_INDEX_USER_COMPUTER_USER_ID);
        db.execSQL(SQL_CREATE_INDEX_USER_COMPUTER_USER_COMPUTER_ID);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
