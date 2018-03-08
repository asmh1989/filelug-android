package com.filelug.android.ui.adapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.filelug.android.Constants;
import com.filelug.android.provider.assetfile.AssetFileColumns;
import com.filelug.android.provider.assetfile.AssetFileSelection;
import com.filelug.android.provider.filetransfer.FileTransferColumns;
import com.filelug.android.provider.filetransfer.FileTransferSelection;
import com.filelug.android.util.AccountUtils;

/**
 * Created by Vincent Chang on 2016/4/28.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public class TransferLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context mContext = null;
    private SectionRecyclerViewCursorAdapter mAdapter = null;
    private int mTransferType = -1;
    private String mGroupId = null;

    public TransferLoader(Context context, SectionRecyclerViewCursorAdapter adapter, int transferType) {
        this.mContext = context;
        this.mAdapter = adapter;
        this.mTransferType = transferType;
    }

    public TransferLoader(Context context, SectionRecyclerViewCursorAdapter adapter, String groupId) {
        this.mContext = context;
        this.mAdapter = adapter;
        this.mGroupId = groupId;
        this.mTransferType = Constants.TRANSFER_TYPE_OPEN_FROM;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Account activeAccount = AccountUtils.getActiveAccount();
        AccountManager accountManager = AccountManager.get(mContext);
        String userId = accountManager.getUserData(activeAccount, Constants.EXT_PARAM_FILELUG_ACCOUNT);
        String computerId = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_ID);

        Uri baseUri = null;
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        if ( mTransferType == Constants.TRANSFER_TYPE_DOWNLOAD) {

            FileTransferSelection fileTransferSelection = new FileTransferSelection();
            baseUri = fileTransferSelection.uri();
            projection = new String[] {
                FileTransferColumns._ID,
                FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.USER_ID + " AS " + FileTransferColumns.USER_ID,
                FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.COMPUTER_ID + " AS " + FileTransferColumns.COMPUTER_ID,
                FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.GROUP_ID + " AS " + FileTransferColumns.GROUP_ID,
                FileTransferColumns.TRANSFER_KEY,
                FileTransferColumns.LOCAL_FILE_NAME,
                FileTransferColumns.SAVED_FILE_NAME,
                FileTransferColumns.LAST_MODIFIED,
                FileTransferColumns.STATUS,
                FileTransferColumns.TOTAL_SIZE,
                FileTransferColumns.TRANSFERRED_SIZE,
                FileTransferColumns.WAIT_TO_CONFIRM,
                FileTransferColumns.END_TIMESTAMP,
                FileTransferColumns.CONTENT_TYPE,
                FileTransferColumns.DG_LOCAL_PATH_WITH_ALIAS,
                FileTransferColumns.DG_FROM_ANOTHER_APP_WITH_ALIAS,
                FileTransferColumns.DG_START_TIMESTAMP_WITH_ALIAS
            };
            selection = FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.USER_ID + " = ? AND " + FileTransferColumns.DG_FROM_ANOTHER_APP + " = ? ";
            selectionArgs = new String[] { userId, "0" };
            sortOrder = FileTransferColumns._ID + " DESC";

        } else if ( mTransferType == Constants.TRANSFER_TYPE_UPLOAD) {

            AssetFileSelection assetFileSelection = new AssetFileSelection();
            baseUri = assetFileSelection.uri();
            projection = new String[] {
                AssetFileColumns._ID,
                AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.USER_ID + " AS " + AssetFileColumns.USER_ID,
                AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.COMPUTER_ID + " AS " + AssetFileColumns.COMPUTER_ID,
                AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.GROUP_ID + " AS " + AssetFileColumns.GROUP_ID,
                AssetFileColumns.TRANSFER_KEY,
                AssetFileColumns.SERVER_FILE_NAME,
                AssetFileColumns.LAST_MODIFIED_TIMESTAMP,
                AssetFileColumns.STATUS,
                AssetFileColumns.TOTAL_SIZE,
                AssetFileColumns.TRANSFERRED_SIZE,
                AssetFileColumns.WAIT_TO_CONFIRM,
                AssetFileColumns.END_TIMESTAMP,
                AssetFileColumns.UG_FROM_ANOTHER_APP_WITH_ALIAS,
                AssetFileColumns.UG_START_TIMESTAMP_WITH_ALIAS
            };
            selection = AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.USER_ID + " = ? ";
            selectionArgs = new String[] { userId };
            sortOrder = AssetFileColumns._ID + " DESC";

        } else if ( mTransferType == Constants.TRANSFER_TYPE_OPEN_FROM) {

            FileTransferSelection fileTransferSelection = new FileTransferSelection();
            baseUri = fileTransferSelection.uri();
            projection = new String[] {
                FileTransferColumns._ID,
                FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.USER_ID + " AS " + FileTransferColumns.USER_ID,
                FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.COMPUTER_ID + " AS " + FileTransferColumns.COMPUTER_ID,
                FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.GROUP_ID + " AS " + FileTransferColumns.GROUP_ID,
                FileTransferColumns.TRANSFER_KEY,
                FileTransferColumns.LOCAL_FILE_NAME,
                FileTransferColumns.SAVED_FILE_NAME,
                FileTransferColumns.STATUS,
                FileTransferColumns.TOTAL_SIZE,
                FileTransferColumns.TRANSFERRED_SIZE,
                FileTransferColumns.WAIT_TO_CONFIRM,
                FileTransferColumns.END_TIMESTAMP,
                FileTransferColumns.DG_LOCAL_PATH_WITH_ALIAS,
                FileTransferColumns.DG_FROM_ANOTHER_APP_WITH_ALIAS,
                FileTransferColumns.DG_START_TIMESTAMP_WITH_ALIAS
            };
            selection = FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.USER_ID + " = ? AND " + FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.GROUP_ID + " = ? AND " + FileTransferColumns.DG_FROM_ANOTHER_APP + " = ? ";
            selectionArgs = new String[] { userId, mGroupId, "1" };
            sortOrder = FileTransferColumns._ID;

        }

        return new CursorLoader(mContext, baseUri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.mAdapter.swapCursor(null);
    }

}
