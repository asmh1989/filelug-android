package com.filelug.android.docsprovider.cursor;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract.Document;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.android.volley.toolbox.RequestFuture;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.crepo.RepositoryErrorObject;
import com.filelug.android.docsprovider.DocumentIdUtils;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelColumns;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelCursor;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelSelection;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteObjectType;
import com.filelug.android.ui.model.RemoteFile;
import com.filelug.android.ui.model.RemoteFileObject;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.FileCache;
import com.filelug.android.util.FilelugUtils;
import com.filelug.android.util.FormatUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.RemoteFileUtils;
import com.filelug.android.util.TransferDBHelper;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vincent Chang on 2016/05/23.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class FLFileDocumentCursor extends DocumentCursor {

    private static final String TAG = FLFileDocumentCursor.class.getSimpleName();

    public FLFileDocumentCursor(Context context, String authority, String documentId, String[] projection) {
        super(projection);

        String[] idParts = DocumentIdUtils.getFileRowIdAndParents(documentId);
        String accountName = idParts[0];
        int computerId = Integer.valueOf(idParts[1]).intValue();
        long fileRowId = Long.valueOf(idParts[2]).longValue();

        String[] newProjection = this.getColumnNames();
        String projectionString = TextUtils.join(",", newProjection);
        if ( MediaStore.MediaColumns.DATA.equals(projectionString) ) {
            findDocumentData(context, accountName, computerId, fileRowId, newProjection);
        } else {
            findDocument(context, accountName, computerId, fileRowId, newProjection);
        }
    }

    private void findDocument(Context context, String accountName, int computerId, long fileRowId, String[] projection) {
//        if ( Constants.DEBUG ) Log.d(TAG, "findDocument(), Arguments: accountName=" + accountName + ", computerId=" + computerId + ", fileRowId=" + fileRowId + ", projection=" + (projection == null ? null : TextUtils.join(",", projection)));

        RemoteHierarchicalModelSelection remoteHierarchicalModelSelection = new RemoteHierarchicalModelSelection()
            .id(fileRowId).and()
            .computerId(computerId);
        RemoteHierarchicalModelCursor c = remoteHierarchicalModelSelection.query(
            context.getContentResolver(),
            RemoteHierarchicalModelColumns.ALL_COLUMNS,
            null
        );

        if ( c.moveToFirst() ) {

            String fileDocumentId = DocumentIdUtils.getFileDocumentId(accountName, computerId, c.getId());
            String name = c.getName();
            String contentType = c.getContentType();
            String lastModifiedStr = c.getLastModified();
            long lastModifiedTime = FormatUtils.convertRemoteFileLastModifiedToTimestamp(context, lastModifiedStr);
            boolean writable = c.getWritable();

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
                size = c.getSize();
            }

            RemoteObjectType type = c.getType();
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
        c.close();
    }

    private void findDocumentData(Context context, String accountId, int computerId, long fileRowId, String[] projection) {
//        if ( Constants.DEBUG ) Log.d(TAG, "findDocumentData(), Arguments: accountId=" + accountId + ", computerId=" + computerId + ", fileRowId=" + fileRowId + ", projection=" + (projection == null ? null : TextUtils.join(",", projection)));

        RemoteHierarchicalModelSelection remoteHierarchicalModelSelection = new RemoteHierarchicalModelSelection()
            .id(fileRowId).and()
            .computerId(computerId);
        RemoteHierarchicalModelCursor c = remoteHierarchicalModelSelection.query(
            context.getContentResolver(),
            RemoteHierarchicalModelColumns.ALL_COLUMNS,
            null
        );

        String fileDocumentId = null;
        String userId = null;
        File cacheFile = null;
        RemoteFileObject remoteFileObject = null;
        if ( c.moveToFirst() ) {
            fileDocumentId = DocumentIdUtils.getFileDocumentId(accountId, computerId, c.getId());
            userId = c.getUserId();
            cacheFile = RemoteFileUtils.getFileInCache(context, userId, computerId, c);
//            if ( Constants.DEBUG ) Log.d(TAG, "findDocumentData(), userId=" + userId + ", computerId=" + computerId + ", cacheFile=" + (cacheFile != null ? cacheFile.getAbsolutePath() : ""));
            remoteFileObject = new RemoteFileObject(c, "");
        }
        c.close();

        if ( cacheFile != null ) {
            String _data = cacheFile.getAbsolutePath();
            addDocumentRow(fileDocumentId, projection, _data, 0, null, null, null, null, null);
            return;
        }

        try {
            String _data = downloadCacheFile(context, userId, computerId, remoteFileObject);
            addDocumentRow(fileDocumentId, projection, _data, 0, null, null, null, null, null);
        } catch (FileNotFoundException e) {
            setErrorInformation(e.getMessage());
        }

        return;
    }

    private synchronized String downloadCacheFile(Context context, String userId, int computerId, RemoteFileObject selectedFile) throws FileNotFoundException {
//        if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), userId=" + userId + ", computerId=" + computerId + ", selectedFile=" + selectedFile);
        Account account = AccountUtils.getAccountByUserId(userId);
        if ( account == null ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), Account is null!");
            String errorMsg = context.getResources().getString(R.string.message_account_not_created);
            throw new FileNotFoundException(errorMsg);
        }

        Bundle connResult = AccountUtils.connectToComputer2(context, account, computerId);
        int errorCode = connResult.getInt(AccountManager.KEY_ERROR_CODE);
        String errorMessage = connResult.getString(AccountManager.KEY_ERROR_MESSAGE);

//        if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), errorCode=" + errorCode + ", errorMessage=" + errorMessage);

        if ( !TextUtils.isEmpty(errorMessage) ) {
            if ( errorCode == Constants.HTTP_STATUS_CODE_SERVICE_UNAVAILABLE ) {
                FilelugUtils.actionWhen503(context, account, false);
                throw new FileNotFoundException(context.getResources().getString(R.string.message_server_error_503));
            } else {
                throw new FileNotFoundException(errorMessage);
            }
        }

        String authToken = connResult.getString(AccountManager.KEY_AUTHTOKEN);
        String lugServerId = connResult.getString(Constants.PARAM_LUG_SERVER_ID);

//        if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), authToken=" + authToken + ", lugServerId=" + lugServerId);

        if ( TextUtils.isEmpty(lugServerId) ) {
            FilelugUtils.actionWhen503(context, account, false);
            throw new FileNotFoundException(context.getResources().getString(R.string.message_server_error_503));
        }

        AccountManager accountManager = AccountManager.get(context);
        String fileSeparator = accountManager.getUserData(account, Constants.PARAM_FILE_SEPARATOR);
        selectedFile.setFileSeparator(fileSeparator);

//        if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), authToken=" + authToken + ", file=" + selectedFile.getFullName());

        String errorMsg = null;

        String locale = context.getResources().getConfiguration().locale.toString();
        int timeOut = context.getResources().getInteger(R.integer.sync_timeout_a);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        RepositoryClient.getInstance().pingDesktop(
            authToken,
            userId,
            locale,
            future,
            future
        );

        try {
            JSONObject response = future.get(timeOut, TimeUnit.MILLISECONDS);
            long uploadSizeLimit = response.optLong(Constants.PARAM_UPLOAD_SIZE_LIMIT);
            long downloadSizeLimit = response.optLong(Constants.PARAM_DOWNLOAD_SIZE_LIMIT);
            long fileSize = selectedFile.getSize();
//            if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), pingDesktop(): uploadSizeLimit=" + uploadSizeLimit + ", downloadSizeLimit=" + downloadSizeLimit + ", fileSize=" + fileSize);
            if ( downloadSizeLimit < fileSize ) {
                errorMsg = String.format(context.getResources().getString(R.string.message_exceed_download_size_limit), selectedFile.getName());
            }
        } catch (Exception e) {
            RepositoryErrorObject errorObject = MiscUtils.getErrorObject(context, e, account);
            errorMsg = errorObject.getMessage();
        }

        if ( errorMsg != null ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), Ping desktop error!\n" + errorMsg);
            throw new FileNotFoundException(errorMsg);
        }

        File accountCacheDir = FileCache.getActiveAccountInCacheDir();
        String cacheDirName = accountCacheDir != null ? accountCacheDir.getAbsolutePath() : FileCache.IN_CACHE_DIR_NAME;
//        if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), cacheDirName=" + cacheDirName);
        String mDownloadGroupId = null;
        try {
            mDownloadGroupId = RemoteFileUtils.createFileDownloadSummary(context, userId, computerId, lugServerId, authToken, new RemoteFile[] {selectedFile}, cacheDirName, 0, null, 0, null, 0, null, true);
//            if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), mDownloadGroupId=" + mDownloadGroupId);
        } catch (Exception e) {
//            if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), Create download summary error!\n" + e.getMessage());
            throw new FileNotFoundException(e.getMessage());
        }

        while (true) {
            if ( !MiscUtils.isUploadOrDownloadOrNotificationServiceRunning() && !TransferDBHelper.isGroupFileDownloading(userId, computerId, mDownloadGroupId) ) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        Bundle result = TransferDBHelper.getDownloadFileUris(userId, computerId, mDownloadGroupId);
        Uri[] uris = (Uri[])result.getParcelableArray(Constants.EXT_PARAM_OFF_RESULT_URIS);
        String[] errors = result.getStringArray(Constants.EXT_PARAM_OFF_RESULT_ERRORS);

        if ( uris != null && uris.length > 0 ) {
            String path = uris[0].getPath();
//            if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), uris[0]=" + path);
            File cacheFile = new File(path);
            return cacheFile.getAbsolutePath();
        } else if ( errors != null && errors.length > 0 ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), errors[0]=" + errors[0]);
            throw new FileNotFoundException(errors[0]);
        }

//        if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), Return null");
        return null;
    }

}
