package com.filelug.android.docsprovider.cursor;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.DocumentsContract.Document;

import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.docsprovider.DocumentIdUtils;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelColumns;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelCursor;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelSelection;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.FormatUtils;
import com.filelug.android.util.MiscUtils;

/**
 * Created by Vincent Chang on 2016/05/23.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class RecentDocumentsCursor extends DocumentCursor {

    private static final String TAG = RecentDocumentsCursor.class.getSimpleName();

    public RecentDocumentsCursor(Context context, String rootId, String[] projection) {
        super(projection);
        findRecentDocuments(context, rootId, this.getColumnNames());
    }

    private void findRecentDocuments(Context context, String rootId, String[] projection) {
//        if ( Constants.DEBUG ) Log.d(TAG, "findRecentDocuments(), Arguments: rootId=" + rootId +", projection=" + (projection == null ? null : TextUtils.join(",", projection)) + ", cursor");

        String accountName = DocumentIdUtils.getAccountName(rootId);
//        if ( Constants.DEBUG ) Log.d(TAG, "findRecentDocuments(), accountName=" + accountName);

        Account account = AccountUtils.getAccount(accountName);
        if ( account == null ) {
            String errorMsg = context.getResources().getString(R.string.message_account_not_created);
            setErrorInformation(errorMsg);
            return;
        }

        AccountManager accountManager = AccountManager.get(context);
        String userId = accountManager.getUserData(account, Constants.EXT_PARAM_FILELUG_ACCOUNT);

        RemoteHierarchicalModelSelection remoteHierarchicalModelSelection = new RemoteHierarchicalModelSelection()
            .userId(userId).and()
            .localLastAccessNot(0l)
            .limit(20);
        RemoteHierarchicalModelCursor c = remoteHierarchicalModelSelection.query(
            context.getContentResolver(),
            new String[] { RemoteHierarchicalModelColumns._ID, RemoteHierarchicalModelColumns.COMPUTER_ID, RemoteHierarchicalModelColumns.NAME, RemoteHierarchicalModelColumns.CONTENT_TYPE, RemoteHierarchicalModelColumns.LAST_MODIFIED, RemoteHierarchicalModelColumns.SIZE, RemoteHierarchicalModelColumns.LOCAL_LAST_ACCESS, RemoteHierarchicalModelColumns.WRITABLE },
            RemoteHierarchicalModelColumns.LOCAL_LAST_ACCESS + " DESC"
        );

        while ( c.moveToNext() ) {
            String fileDocumentId = DocumentIdUtils.getFileDocumentId(accountName, c.getComputerId(), c.getId());
            String name = c.getName();
            String contentType = c.getContentType();
            String lastModifiedStr = c.getLastModified();
            long lastModifiedTime = FormatUtils.convertRemoteFileLastModifiedToTimestamp(context, lastModifiedStr);
            long localLastAccess = c.getLocalLastAccess();
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

//            addDocumentRow(fileDocumentId, projection, name, flags, contentType, iconRes, lastModifiedTime, size, null);
            addDocumentRow(fileDocumentId, projection, name, flags, contentType, iconRes, localLastAccess, size, null);
        }
        c.close();
    }

}
