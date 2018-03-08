package com.filelug.android.docsprovider.cursor;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract.Document;
import android.text.TextUtils;

import com.android.volley.toolbox.RequestFuture;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.crepo.RepositoryErrorObject;
import com.filelug.android.docsprovider.DocumentIdUtils;
import com.filelug.android.provider.remoteroot.RemoteRootColumns;
import com.filelug.android.provider.remoteroot.RemoteRootCursor;
import com.filelug.android.provider.remoteroot.RemoteRootSelection;
import com.filelug.android.provider.remoteroot.RemoteRootType;
import com.filelug.android.ui.model.RemoteFile;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.FilelugUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.RemoteFileUtils;
import com.filelug.android.util.TransferDBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vincent Chang on 2016/05/23.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class ListRootDirectoriesCursor extends RemoteListCursor {

    private static final String TAG = ListRootDirectoriesCursor.class.getSimpleName();
    private static final String SEPARATOR = "::";

    public ListRootDirectoriesCursor(Context context, String parentDocumentId, String[] projection, String sortOrder) {
        super(context, parentDocumentId, projection, sortOrder);
    }

    @Override
    protected void findDocuments(Context context, String parentDocumentId, String[] projection, String sortOrder) {

//        if ( Constants.DEBUG ) Log.d(TAG, "findDocuments() --> Arguments: parentDocumentId=" + parentDocumentId + ", projection=" + (projection == null ? null : TextUtils.join(",", projection)) + ", sortOrder=" + sortOrder + ", cursor");

        String[] idParts = DocumentIdUtils.getComputerIdAndParents(parentDocumentId);
        String accountName = idParts[0];
        int computerId = Integer.valueOf(idParts[1]).intValue();
//        if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), accountName=" + accountName + ", computerId=" + computerId);

        Account account = AccountUtils.getAccount(accountName);
        if ( account == null ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), Account is null!");
            String errorMsg = context.getResources().getString(R.string.message_account_not_created);
            setErrorInformation(errorMsg);
            return;
        }

        AccountManager accountManager = AccountManager.get(context);
        String userId = accountManager.getUserData(account, Constants.EXT_PARAM_FILELUG_ACCOUNT);

//        if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), userId=" + userId + ", computerId=" + computerId);

        Bundle connResult = AccountUtils.connectToComputer2(context, account, computerId);
        int errorCode = connResult.getInt(AccountManager.KEY_ERROR_CODE);
        String errorMessage = connResult.getString(AccountManager.KEY_ERROR_MESSAGE);

//        if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), errorCode=" + errorCode + ", errorMessage=" + errorMessage);

        if ( !TextUtils.isEmpty(errorMessage) ) {
            if ( errorCode == Constants.HTTP_STATUS_CODE_SERVICE_UNAVAILABLE ) {
                FilelugUtils.actionWhen503(context, account, false);
                setErrorInformation(context.getResources().getString(R.string.message_server_error_503));
            } else {
                setErrorInformation(errorMessage);
            }
            return;
        }

        String authToken = connResult.getString(AccountManager.KEY_AUTHTOKEN);
        String lugServerId = connResult.getString(Constants.PARAM_LUG_SERVER_ID);

//        if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), authToken=" + authToken + ", lugServerId=" + lugServerId);

        if ( TextUtils.isEmpty(lugServerId) ) {
            FilelugUtils.actionWhen503(context, account, false);
            setErrorInformation(context.getResources().getString(R.string.message_server_error_503));
            return;
        }

        String locale = context.getResources().getConfiguration().locale.toString();
        int timeOut = context.getResources().getInteger(R.integer.sync_timeout_c);

        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        RepositoryClient.getInstance().listRoots(
            authToken,
            lugServerId,
            locale,
            future,
            future
        );

        List<String> rootDirList = new ArrayList<String>();

        RepositoryErrorObject errorObject = null;

        try {
            JSONArray response = future.get(timeOut, TimeUnit.MILLISECONDS);

            for ( int i=0; i<response.length(); i++ ) {
                JSONObject jso = response.getJSONObject(i);
                String label = jso.getString(Constants.PARAM_LABEL);
                String path = jso.getString(Constants.PARAM_PATH);
                String realPath = jso.getString(Constants.PARAM_REAL_PATH);
                String type = jso.getString(Constants.PARAM_TYPE);

//                if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), label=" + label + ", path=" + path + ", realPath=" + realPath + ", type=" + type);

                TransferDBHelper.createOrUpdateRemoteRoot(userId, computerId, path, realPath, label, type);

                rootDirList.add(path + SEPARATOR + realPath);
            }
        } catch (Exception e) {
            errorObject = MiscUtils.getErrorObject(context, e, account);
        }

        if ( errorObject != null ) {
//            if (Constants.DEBUG) Log.d(TAG, "findDocuments(), ListRoots Error!\n" + errorObject.getMessage());
            setErrorInformation(errorObject.getMessage());
            return;
        }

        if ( rootDirList.size() > 0 ) {
            TransferDBHelper.checkRemovedRemoteRoot(userId, computerId, rootDirList, SEPARATOR);
        }

        String orderBy = "";
        if ( !TextUtils.isEmpty(sortOrder) ) {
            if ( sortOrder.contains(Document.COLUMN_DISPLAY_NAME) ) {
                orderBy = sortOrder.replace(Document.COLUMN_DISPLAY_NAME, RemoteRootColumns.LABEL);
            }
        }
//        if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), orderBy=" + orderBy);

        RemoteRootSelection remoteRootSelection3 = new RemoteRootSelection()
            .userId(userId).and()
            .computerId(computerId);
        RemoteRootCursor c3 = remoteRootSelection3.query(
            context.getContentResolver(),
            new String[] { RemoteRootColumns._ID, RemoteRootColumns.LABEL, RemoteRootColumns.PATH, RemoteRootColumns.TYPE},
            orderBy
        );
        while( c3.moveToNext() ) {
            String rootDirDocumentId = DocumentIdUtils.getRootDirectoryDocumentId(accountName, computerId, c3.getId());
            String label = c3.getLabel();
            String path = c3.getPath();
            RemoteRootType rootType = c3.getType();
            RemoteFile.FileType fileType = RemoteFileUtils.convertRemoteRoot(rootType);
            int iconResourceId = MiscUtils.getIconResourceIdByRemoteFileType(fileType);
            addDocumentRow(rootDirDocumentId, projection, label, 0, Document.MIME_TYPE_DIR, iconResourceId, null, null, null/*path*/);
//            addDocumentRow(rootDirDocumentId, projection, label, Document.FLAG_DIR_SUPPORTS_CREATE, Document.MIME_TYPE_DIR, iconResourceId, null, null, path);
        }
        c3.close();
    }

}
