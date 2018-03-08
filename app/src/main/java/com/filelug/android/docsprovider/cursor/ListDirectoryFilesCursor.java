package com.filelug.android.docsprovider.cursor;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
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
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelColumns;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelContentValues;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelCursor;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelSelection;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteObjectType;
import com.filelug.android.provider.remoteroot.RemoteRootColumns;
import com.filelug.android.provider.remoteroot.RemoteRootCursor;
import com.filelug.android.provider.remoteroot.RemoteRootSelection;
import com.filelug.android.service.ContentType;
import com.filelug.android.ui.model.RemoteFileObject;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.FilelugUtils;
import com.filelug.android.util.FormatUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.PrefUtils;
import com.filelug.android.util.RemoteFileUtils;

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
public class ListDirectoryFilesCursor extends RemoteListCursor {

    private static final String TAG = ListDirectoryFilesCursor.class.getSimpleName();

    public ListDirectoryFilesCursor(Context context, String parentDocumentId, String[] projection, String sortOrder) {
        super(context, parentDocumentId, projection, sortOrder);
    }

    @Override
    protected void findDocuments(Context context, String parentDocumentId, String[] projection, String sortOrder) {
//        if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), Arguments: parentDocumentId=" + parentDocumentId + ", projection=" + (projection == null ? null : TextUtils.join(",", projection)) + ", sortOrder=" + sortOrder + ", cursor");

        String userId = null;
        String filePath = null;
        String fileName = null;
        String[] idParts = null;
        String accountName = null;
        int computerId = -1;

        if ( DocumentIdUtils.isRootDirectory(parentDocumentId) ) {
            idParts = DocumentIdUtils.getRootDirectoryRowIdAndParents(parentDocumentId);
            accountName = idParts[0];
            computerId = Integer.valueOf(idParts[1]).intValue();
            long rootDirRowId = Long.valueOf(idParts[2]).longValue();

//            if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), Is rootDir! accountName=" + accountName + ", computerId=" + computerId + ", rootDirRowId=" + rootDirRowId);

            RemoteRootSelection remoteRootSelection = new RemoteRootSelection()
                .id(rootDirRowId);
            RemoteRootCursor c1 = remoteRootSelection.query(
                context.getContentResolver(),
                new String[] { RemoteRootColumns.USER_ID, RemoteRootColumns.REAL_PATH},
                null
            );

            if ( c1.moveToFirst() ) {
                userId = c1.getUserId();
                filePath = c1.getRealPath();
//                if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), Is rootDir! userId=" + userId + ", filePath=" + filePath);
            }
            c1.close();
        } else {
            idParts = DocumentIdUtils.getFileRowIdAndParents(parentDocumentId);
            accountName = idParts[0];
            computerId = Integer.valueOf(idParts[1]).intValue();
            long fileRowId = Long.valueOf(idParts[2]).longValue();

//            if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), Is dir! accountName=" + accountName + ", computerId=" + computerId + ", fileRowId=" + fileRowId);

            RemoteHierarchicalModelSelection remoteHierarchicalModelSelection = new RemoteHierarchicalModelSelection()
                .id(fileRowId);
            RemoteHierarchicalModelCursor c1 = remoteHierarchicalModelSelection.query(
                context.getContentResolver(),
                new String[] { RemoteHierarchicalModelColumns.USER_ID, RemoteHierarchicalModelColumns.REAL_PARENT, RemoteHierarchicalModelColumns.REAL_NAME },
                null
            );

            if ( c1.moveToFirst() ) {
                userId = c1.getUserId();
                filePath = c1.getRealParent();
                fileName = c1.getRealName();
//                if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), Is dir! userId=" + userId + ", filePath=" + filePath + ", fileName=" + fileName);
            }
            c1.close();
        }

        Account account = AccountUtils.getAccountByUserId(userId);
        if ( account == null ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), Account is null!");
            String errorMsg = context.getResources().getString(R.string.message_account_not_created);
            setErrorInformation(errorMsg);
            return;
        }

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

        AccountManager accountManager = AccountManager.get(context);
        String fileSeparator = accountManager.getUserData(account, Constants.PARAM_FILE_SEPARATOR);
        boolean showHidden = PrefUtils.isShowHiddenFiles();
        String path = filePath + ( TextUtils.isEmpty(fileName) ? "" : ( fileSeparator + fileName ) );

//        if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), fileSeparator=" + fileSeparator + ", showHidden=" + showHidden + ", path=" + path);

        String locale = context.getResources().getConfiguration().locale.toString();
        int timeOut = context.getResources().getInteger(R.integer.sync_timeout_c);

        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        RepositoryClient.getInstance().list(
            authToken,
            lugServerId,
            path,
            showHidden,
            locale,
            future,
            future
        );

        List<String> fileNameList = new ArrayList<String>();

        RepositoryErrorObject errorObject = null;

        try {
            JSONArray response = future.get(timeOut, TimeUnit.MILLISECONDS);

            for ( int i=0; i<response.length(); i++ ) {
                JSONObject jso = response.getJSONObject(i);
                RemoteFileObject fileObject = new RemoteFileObject(jso, fileSeparator);
                RemoteObjectType objectType = RemoteFileUtils.convertRemoteFileObject(fileObject.getType());
                String contentType = fileObject.getContentType();
                if ( fileObject.getType().isDirectory() ) {
                    contentType = Document.MIME_TYPE_DIR;
                } else if ( TextUtils.isEmpty(contentType) ) {
                    contentType = ContentType.APPLICATION_OCTET_STREAM;
                }

                RemoteHierarchicalModelSelection remoteHierarchicalModelSelection = new RemoteHierarchicalModelSelection()
                    .userId(userId).and()
                    .computerId(computerId).and()
                    .parent(fileObject.getParent()).and()
                    .name(fileObject.getName());
                RemoteHierarchicalModelContentValues values = new RemoteHierarchicalModelContentValues()
                    .putSymlink(fileObject.isSymlink())
                    .putReadable(fileObject.isReadable())
                    .putWritable(fileObject.isWritable())
                    .putHidden(fileObject.isHidden())
                    .putType(objectType)
                    .putContentType(contentType)
                    .putRealParent(fileObject.getRealParent())
                    .putRealName(fileObject.getRealName())
                    .putLastModified(fileObject.getLastModified())
                    .putSize(fileObject.getSize());
                int updatedCount = values.update(context.getContentResolver(), remoteHierarchicalModelSelection);
                if ( updatedCount == 0 ) {
                    values.putUserId(userId)
                        .putComputerId(computerId)
                        .putParent(fileObject.getParent())
                        .putName(fileObject.getName())
                        .putLocalLastModifiedNull()
                        .putLocalSizeNull()
                        .putLocalLastAccessNull();
                    Uri uri = values.insert(context.getContentResolver());
//                    if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), insert into DB, uri=" + uri.toString() + ", userId=" + userId + ", computerId=" + computerId + ", parent=" + fileObject.getParent() + ", name=" + fileObject.getName());
                } else {
//                    if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), update DB, userId=" + userId + ", computerId=" + computerId + ", parent=" + fileObject.getParent() + ", name=" + fileObject.getName());
                }

                fileNameList.add(fileObject.getName());
            }
        } catch (Exception e) {
            errorObject = MiscUtils.getErrorObject(context, e, account);
        }

        if ( errorObject != null ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), List error!\n" + errorObject.getMessage());
            setErrorInformation(errorObject.getMessage());
            return;
        } else {
            if ( fileNameList.size() > 0 ) {
                String[] names = fileNameList.toArray(new String[0]);
                RemoteHierarchicalModelSelection deleteSelection = new RemoteHierarchicalModelSelection()
                    .userId(userId).and()
                    .computerId(computerId).and()
                    .parent(path).and()
                    .nameNot(names);
                int deleteCount = deleteSelection.delete(context.getContentResolver());
//                if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), delete " + deleteCount + " rows! userId=" + userId + ", computerId=" + computerId + ", path=" + path + ", name not in (" + TextUtils.join(",", names) + ")");
            }
        }

        String orderBy = "";
        if ( !TextUtils.isEmpty(sortOrder) ) {
            if ( sortOrder.contains(Document.COLUMN_DISPLAY_NAME) ) {
                orderBy = sortOrder.replace(Document.COLUMN_DISPLAY_NAME, RemoteHierarchicalModelColumns.NAME);
            }
            if ( sortOrder.contains(Document.COLUMN_LAST_MODIFIED) ) {
                orderBy = sortOrder.replace(Document.COLUMN_LAST_MODIFIED, RemoteHierarchicalModelColumns.LAST_MODIFIED);
            }
            if ( sortOrder.contains(Document.COLUMN_SIZE) ) {
                orderBy = sortOrder.replace(Document.COLUMN_SIZE, RemoteHierarchicalModelColumns.SIZE);
            }
        }
//        if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), orderBy=" + orderBy);

        RemoteHierarchicalModelSelection remoteHierarchicalModelSelection = new RemoteHierarchicalModelSelection()
            .userId(userId).and()
            .computerId(computerId).and()
            .parent(path);
        RemoteHierarchicalModelCursor c2 = remoteHierarchicalModelSelection.query(
            context.getContentResolver(),
            new String[] { RemoteHierarchicalModelColumns._ID, RemoteHierarchicalModelColumns.NAME, RemoteHierarchicalModelColumns.CONTENT_TYPE, RemoteHierarchicalModelColumns.LAST_MODIFIED, RemoteHierarchicalModelColumns.SIZE, RemoteHierarchicalModelColumns.HIDDEN, RemoteHierarchicalModelColumns.TYPE, RemoteHierarchicalModelColumns.WRITABLE },
            orderBy
        );
        while( c2.moveToNext() ) {
            String fileDocumentId = DocumentIdUtils.getFileDocumentId(accountName, computerId, c2.getId());
            String name = c2.getName();
            String contentType = c2.getContentType();
            String lastModifiedStr = c2.getLastModified();
            long lastModifiedTime = FormatUtils.convertRemoteFileLastModifiedToTimestamp(context, lastModifiedStr);
            boolean writable = c2.getWritable();

            boolean hidden = c2.getHidden();
            boolean isHiddenFile = hidden || name.startsWith(".");
//            if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), isHiddenFile=" + isHiddenFile);
            if ( !showHidden && isHiddenFile ) {
                continue;
            }

            int iconRes = -1;
            int flags = 0;
            if ( Document.MIME_TYPE_DIR.equals(contentType) ) {
                iconRes = R.drawable.ic_folder;
//                if ( writable ) {
//                    flags |= Document.FLAG_DIR_SUPPORTS_CREATE;
//                }
            } else {
                String extension = MiscUtils.getExtension(name);
                iconRes = MiscUtils.getIconResourceIdByExtension(extension);
//                if ( writable ) {
//                    flags |= Document.FLAG_SUPPORTS_WRITE;
//                    flags |= Document.FLAG_SUPPORTS_DELETE;
//                }
//                if ( contentType.startsWith("image/") ) {
////                    flags |= Document.FLAG_SUPPORTS_THUMBNAIL;
//                }
            }

            Long size = null;
            if ( !Document.MIME_TYPE_DIR.equals(contentType) ) {
                size = c2.getSize();
            }

            RemoteObjectType type = c2.getType();
            String summaryText = null;
            if ( type == RemoteObjectType.WINDOWS_SHORTCUT_DIRECTORY || type == RemoteObjectType.WINDOWS_SHORTCUT_FILE ) {
                summaryText = context.getResources().getString(R.string.fileType_windows_shortcut);
            } else if ( type == RemoteObjectType.MAC_ALIAS_DIRECTORY || type == RemoteObjectType.MAC_ALIAS_FILE ) {
                summaryText = context.getResources().getString(R.string.fileType_mac_alias);
            } else if ( type == RemoteObjectType.UNIX_SYMBOLIC_LINK_DIRECTORY || type == RemoteObjectType.UNIX_SYMBOLIC_LINK_FILE ) {
                summaryText = context.getResources().getString(R.string.fileType_unix_symbolic_link);
            }

            addDocumentRow(fileDocumentId, projection, name, flags, contentType, iconRes, lastModifiedTime, size, summaryText);
        }
        c2.close();
    }

}
