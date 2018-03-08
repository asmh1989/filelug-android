package com.filelug.android.provider;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.filelug.android.provider.assetfile.AssetFileColumns;
import com.filelug.android.provider.base.BaseContentProvider;
import com.filelug.android.provider.downloadgroup.DownloadGroupColumns;
import com.filelug.android.provider.downloadhistory.DownloadHistoryColumns;
import com.filelug.android.provider.filetransfer.FileTransferColumns;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelColumns;
import com.filelug.android.provider.remoteroot.RemoteRootColumns;
import com.filelug.android.provider.uploadgroup.UploadGroupColumns;
import com.filelug.android.provider.uploadhistory.UploadHistoryColumns;
import com.filelug.android.provider.usercomputer.UserComputerColumns;

public class RepositoryContentProvider extends BaseContentProvider {
    private static final String TAG = RepositoryContentProvider.class.getSimpleName();

//    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final boolean DEBUG = false;

    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

    public static final String AUTHORITY = "com.filelug.android.provider";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;

    private static final int URI_TYPE_ASSET_FILE = 0;
    private static final int URI_TYPE_ASSET_FILE_ID = 1;

    private static final int URI_TYPE_DOWNLOAD_GROUP = 4;
    private static final int URI_TYPE_DOWNLOAD_GROUP_ID = 5;

    private static final int URI_TYPE_DOWNLOAD_HISTORY = 6;
    private static final int URI_TYPE_DOWNLOAD_HISTORY_ID = 7;

    private static final int URI_TYPE_FILE_TRANSFER = 8;
    private static final int URI_TYPE_FILE_TRANSFER_ID = 9;

    private static final int URI_TYPE_REMOTE_HIERARCHICAL_MODEL = 10;
    private static final int URI_TYPE_REMOTE_HIERARCHICAL_MODEL_ID = 11;

    private static final int URI_TYPE_REMOTE_ROOT_DIRECTORY = 12;
    private static final int URI_TYPE_REMOTE_ROOT_DIRECTORY_ID = 13;

    private static final int URI_TYPE_UPLOAD_GROUP = 14;
    private static final int URI_TYPE_UPLOAD_GROUP_ID = 15;

    private static final int URI_TYPE_UPLOAD_HISTORY = 16;
    private static final int URI_TYPE_UPLOAD_HISTORY_ID = 17;

    private static final int URI_TYPE_USER_COMPUTER = 18;
    private static final int URI_TYPE_USER_COMPUTER_ID = 19;



    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, AssetFileColumns.TABLE_NAME, URI_TYPE_ASSET_FILE);
        URI_MATCHER.addURI(AUTHORITY, AssetFileColumns.TABLE_NAME + "/#", URI_TYPE_ASSET_FILE_ID);
        URI_MATCHER.addURI(AUTHORITY, DownloadGroupColumns.TABLE_NAME, URI_TYPE_DOWNLOAD_GROUP);
        URI_MATCHER.addURI(AUTHORITY, DownloadGroupColumns.TABLE_NAME + "/#", URI_TYPE_DOWNLOAD_GROUP_ID);
        URI_MATCHER.addURI(AUTHORITY, DownloadHistoryColumns.TABLE_NAME, URI_TYPE_DOWNLOAD_HISTORY);
        URI_MATCHER.addURI(AUTHORITY, DownloadHistoryColumns.TABLE_NAME + "/#", URI_TYPE_DOWNLOAD_HISTORY_ID);
        URI_MATCHER.addURI(AUTHORITY, FileTransferColumns.TABLE_NAME, URI_TYPE_FILE_TRANSFER);
        URI_MATCHER.addURI(AUTHORITY, FileTransferColumns.TABLE_NAME + "/#", URI_TYPE_FILE_TRANSFER_ID);
        URI_MATCHER.addURI(AUTHORITY, RemoteHierarchicalModelColumns.TABLE_NAME, URI_TYPE_REMOTE_HIERARCHICAL_MODEL);
        URI_MATCHER.addURI(AUTHORITY, RemoteHierarchicalModelColumns.TABLE_NAME + "/#", URI_TYPE_REMOTE_HIERARCHICAL_MODEL_ID);
        URI_MATCHER.addURI(AUTHORITY, RemoteRootColumns.TABLE_NAME, URI_TYPE_REMOTE_ROOT_DIRECTORY);
        URI_MATCHER.addURI(AUTHORITY, RemoteRootColumns.TABLE_NAME + "/#", URI_TYPE_REMOTE_ROOT_DIRECTORY_ID);
        URI_MATCHER.addURI(AUTHORITY, UploadGroupColumns.TABLE_NAME, URI_TYPE_UPLOAD_GROUP);
        URI_MATCHER.addURI(AUTHORITY, UploadGroupColumns.TABLE_NAME + "/#", URI_TYPE_UPLOAD_GROUP_ID);
        URI_MATCHER.addURI(AUTHORITY, UploadHistoryColumns.TABLE_NAME, URI_TYPE_UPLOAD_HISTORY);
        URI_MATCHER.addURI(AUTHORITY, UploadHistoryColumns.TABLE_NAME + "/#", URI_TYPE_UPLOAD_HISTORY_ID);
        URI_MATCHER.addURI(AUTHORITY, UserComputerColumns.TABLE_NAME, URI_TYPE_USER_COMPUTER);
        URI_MATCHER.addURI(AUTHORITY, UserComputerColumns.TABLE_NAME + "/#", URI_TYPE_USER_COMPUTER_ID);
    }

    @Override
    protected SQLiteOpenHelper createSqLiteOpenHelper() {
        return FilelugSQLiteOpenHelper.getInstance(getContext());
    }

    @Override
    protected boolean hasDebug() {
        return DEBUG;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_ASSET_FILE:
                return TYPE_CURSOR_DIR + AssetFileColumns.TABLE_NAME;
            case URI_TYPE_ASSET_FILE_ID:
                return TYPE_CURSOR_ITEM + AssetFileColumns.TABLE_NAME;

            case URI_TYPE_DOWNLOAD_GROUP:
                return TYPE_CURSOR_DIR + DownloadGroupColumns.TABLE_NAME;
            case URI_TYPE_DOWNLOAD_GROUP_ID:
                return TYPE_CURSOR_ITEM + DownloadGroupColumns.TABLE_NAME;

            case URI_TYPE_DOWNLOAD_HISTORY:
                return TYPE_CURSOR_DIR + DownloadHistoryColumns.TABLE_NAME;
            case URI_TYPE_DOWNLOAD_HISTORY_ID:
                return TYPE_CURSOR_ITEM + DownloadHistoryColumns.TABLE_NAME;

            case URI_TYPE_FILE_TRANSFER:
                return TYPE_CURSOR_DIR + FileTransferColumns.TABLE_NAME;
            case URI_TYPE_FILE_TRANSFER_ID:
                return TYPE_CURSOR_ITEM + FileTransferColumns.TABLE_NAME;

            case URI_TYPE_REMOTE_HIERARCHICAL_MODEL:
                return TYPE_CURSOR_DIR + RemoteHierarchicalModelColumns.TABLE_NAME;
            case URI_TYPE_REMOTE_HIERARCHICAL_MODEL_ID:
                return TYPE_CURSOR_ITEM + RemoteHierarchicalModelColumns.TABLE_NAME;

            case URI_TYPE_REMOTE_ROOT_DIRECTORY:
                return TYPE_CURSOR_DIR + RemoteRootColumns.TABLE_NAME;
            case URI_TYPE_REMOTE_ROOT_DIRECTORY_ID:
                return TYPE_CURSOR_ITEM + RemoteRootColumns.TABLE_NAME;

            case URI_TYPE_UPLOAD_GROUP:
                return TYPE_CURSOR_DIR + UploadGroupColumns.TABLE_NAME;
            case URI_TYPE_UPLOAD_GROUP_ID:
                return TYPE_CURSOR_ITEM + UploadGroupColumns.TABLE_NAME;

            case URI_TYPE_UPLOAD_HISTORY:
                return TYPE_CURSOR_DIR + UploadHistoryColumns.TABLE_NAME;
            case URI_TYPE_UPLOAD_HISTORY_ID:
                return TYPE_CURSOR_ITEM + UploadHistoryColumns.TABLE_NAME;

            case URI_TYPE_USER_COMPUTER:
                return TYPE_CURSOR_DIR + UserComputerColumns.TABLE_NAME;
            case URI_TYPE_USER_COMPUTER_ID:
                return TYPE_CURSOR_ITEM + UserComputerColumns.TABLE_NAME;

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
//        if (DEBUG) Log.d(TAG, "insert uri=" + uri + " values=" + values);
        return super.insert(uri, values);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
//        if (DEBUG) Log.d(TAG, "bulkInsert uri=" + uri + " values.length=" + values.length);
        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//        if (DEBUG) Log.d(TAG, "update uri=" + uri + " values=" + values + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.update(uri, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
//        if (DEBUG) Log.d(TAG, "delete uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.delete(uri, selection, selectionArgs);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//        if (DEBUG)
//            Log.d(TAG, "query uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs) + " sortOrder=" + sortOrder
//                    + " groupBy=" + uri.getQueryParameter(QUERY_GROUP_BY) + " having=" + uri.getQueryParameter(QUERY_HAVING) + " limit=" + uri.getQueryParameter(QUERY_LIMIT));
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = URI_MATCHER.match(uri);
        switch (matchedId) {
            case URI_TYPE_ASSET_FILE:
            case URI_TYPE_ASSET_FILE_ID:
                res.table = AssetFileColumns.TABLE_NAME;
                res.idColumn = AssetFileColumns._ID;
                res.tablesWithJoins = AssetFileColumns.TABLE_NAME;
                if ( AssetFileColumns.hasUploadGroupColumns(projection) ) {
                    res.tablesWithJoins += " LEFT OUTER JOIN " + UploadGroupColumns.TABLE_NAME + " ON " + AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.USER_ID + " = " + UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.USER_ID +
                            " AND " + AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.COMPUTER_ID + " = " + UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.COMPUTER_ID +
                            " AND " + AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.GROUP_ID + " = " + UploadGroupColumns.TABLE_NAME + "." + UploadGroupColumns.GROUP_ID;
                }
                res.orderBy = AssetFileColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_DOWNLOAD_GROUP:
            case URI_TYPE_DOWNLOAD_GROUP_ID:
                res.table = DownloadGroupColumns.TABLE_NAME;
                res.idColumn = DownloadGroupColumns._ID;
                res.tablesWithJoins = DownloadGroupColumns.TABLE_NAME;
                res.orderBy = DownloadGroupColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_DOWNLOAD_HISTORY:
            case URI_TYPE_DOWNLOAD_HISTORY_ID:
                res.table = DownloadHistoryColumns.TABLE_NAME;
                res.idColumn = DownloadHistoryColumns._ID;
                res.tablesWithJoins = DownloadHistoryColumns.TABLE_NAME;
                res.orderBy = DownloadHistoryColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_FILE_TRANSFER:
            case URI_TYPE_FILE_TRANSFER_ID:
                res.table = FileTransferColumns.TABLE_NAME;
                res.idColumn = FileTransferColumns._ID;
                res.tablesWithJoins = FileTransferColumns.TABLE_NAME;
                if ( FileTransferColumns.hasDownloadGroupColumns(projection) ) {
                    res.tablesWithJoins += " LEFT OUTER JOIN " + DownloadGroupColumns.TABLE_NAME + " ON " + FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.USER_ID + " = " + DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.USER_ID +
                            " AND " + FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.COMPUTER_ID + " = " + DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.COMPUTER_ID +
                            " AND " + FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.GROUP_ID + " = " + DownloadGroupColumns.TABLE_NAME + "." + DownloadGroupColumns.GROUP_ID;
                }
                res.orderBy = FileTransferColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_REMOTE_HIERARCHICAL_MODEL:
            case URI_TYPE_REMOTE_HIERARCHICAL_MODEL_ID:
                res.table = RemoteHierarchicalModelColumns.TABLE_NAME;
                res.idColumn = RemoteHierarchicalModelColumns._ID;
                res.tablesWithJoins = RemoteHierarchicalModelColumns.TABLE_NAME;
                res.orderBy = RemoteHierarchicalModelColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_REMOTE_ROOT_DIRECTORY:
            case URI_TYPE_REMOTE_ROOT_DIRECTORY_ID:
                res.table = RemoteRootColumns.TABLE_NAME;
                res.idColumn = RemoteRootColumns._ID;
                res.tablesWithJoins = RemoteRootColumns.TABLE_NAME;
                res.orderBy = RemoteRootColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_UPLOAD_GROUP:
            case URI_TYPE_UPLOAD_GROUP_ID:
                res.table = UploadGroupColumns.TABLE_NAME;
                res.idColumn = UploadGroupColumns._ID;
                res.tablesWithJoins = UploadGroupColumns.TABLE_NAME;
                res.orderBy = UploadGroupColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_UPLOAD_HISTORY:
            case URI_TYPE_UPLOAD_HISTORY_ID:
                res.table = UploadHistoryColumns.TABLE_NAME;
                res.idColumn = UploadHistoryColumns._ID;
                res.tablesWithJoins = UploadHistoryColumns.TABLE_NAME;
                res.orderBy = UploadHistoryColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_USER_COMPUTER:
            case URI_TYPE_USER_COMPUTER_ID:
                res.table = UserComputerColumns.TABLE_NAME;
                res.idColumn = UserComputerColumns._ID;
                res.tablesWithJoins = UserComputerColumns.TABLE_NAME;
                res.orderBy = UserComputerColumns.DEFAULT_ORDER;
                break;

            default:
                throw new IllegalArgumentException("The uri '" + uri + "' is not supported by this ContentProvider");
        }

        switch (matchedId) {
            case URI_TYPE_ASSET_FILE_ID:
            case URI_TYPE_DOWNLOAD_GROUP_ID:
            case URI_TYPE_DOWNLOAD_HISTORY_ID:
            case URI_TYPE_FILE_TRANSFER_ID:
            case URI_TYPE_REMOTE_HIERARCHICAL_MODEL_ID:
            case URI_TYPE_REMOTE_ROOT_DIRECTORY_ID:
            case URI_TYPE_UPLOAD_GROUP_ID:
            case URI_TYPE_UPLOAD_HISTORY_ID:
            case URI_TYPE_USER_COMPUTER_ID:
                id = uri.getLastPathSegment();
        }
        if (id != null) {
            if (selection != null) {
                res.selection = res.table + "." + res.idColumn + "=" + id + " and (" + selection + ")";
            } else {
                res.selection = res.table + "." + res.idColumn + "=" + id;
            }
        } else {
            res.selection = selection;
        }
        return res;
    }
}
