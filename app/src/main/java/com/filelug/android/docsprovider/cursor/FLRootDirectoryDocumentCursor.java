package com.filelug.android.docsprovider.cursor;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.DocumentsContract;

import com.filelug.android.docsprovider.DocumentIdUtils;
import com.filelug.android.provider.remoteroot.RemoteRootColumns;
import com.filelug.android.provider.remoteroot.RemoteRootCursor;
import com.filelug.android.provider.remoteroot.RemoteRootSelection;
import com.filelug.android.provider.remoteroot.RemoteRootType;
import com.filelug.android.ui.model.RemoteFile;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.RemoteFileUtils;

/**
 * Created by Vincent Chang on 2016/6/7.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class FLRootDirectoryDocumentCursor extends DocumentCursor {

    private static final String TAG = FLRootDirectoryDocumentCursor.class.getSimpleName();

    public FLRootDirectoryDocumentCursor(Context context, String authority, String documentId, String[] projection) {
        super(projection);
        String[] idParts = DocumentIdUtils.getRootDirectoryRowIdAndParents(documentId);
        String accountName = idParts[0];
        int computerId = Integer.valueOf(idParts[1]).intValue();
        long rootDirRowId = Long.valueOf(idParts[2]).longValue();
        findDocument(context, accountName, computerId, rootDirRowId, this.getColumnNames());
    }

    private void findDocument(Context context, String accountName, int computerId, long rootDirRowId, String[] projection) {
//        if ( Constants.DEBUG ) Log.d(TAG, "findDocument(), Arguments: accountName=" + accountName + ", computerId=" + computerId + ", rootDirRowId=" + rootDirRowId + ", projection=" + (projection == null ? null : TextUtils.join(",", projection)));

        RemoteRootSelection remoteRootSelection = new RemoteRootSelection()
            .id(rootDirRowId);
        RemoteRootCursor c = remoteRootSelection.query(
            context.getContentResolver(),
            new String[] { RemoteRootColumns._ID, RemoteRootColumns.LABEL, RemoteRootColumns.PATH, RemoteRootColumns.TYPE },
            null
        );

        if ( c.moveToFirst() ) {
            String rootDirDocumentId = DocumentIdUtils.getRootDirectoryDocumentId(accountName, computerId, c.getId());
            String label = c.getLabel();
            String path = c.getPath();
            RemoteRootType rootType = c.getType();
            RemoteFile.FileType fileType = RemoteFileUtils.convertRemoteRoot(rootType);
            int iconResourceId = MiscUtils.getIconResourceIdByRemoteFileType(fileType);
            addDocumentRow(rootDirDocumentId, projection, label, 0, DocumentsContract.Document.MIME_TYPE_DIR, iconResourceId, null, null, path);
//            addDocumentRow(rootDirDocumentId, projection, directoryLabel, Document.FLAG_DIR_SUPPORTS_CREATE, Document.MIME_TYPE_DIR, iconResourceId, null, null, directoryPath);
        }
        c.close();
    }

}
