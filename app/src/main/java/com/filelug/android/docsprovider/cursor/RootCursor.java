package com.filelug.android.docsprovider.cursor;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.DocumentsContract;

import com.filelug.android.R;
import com.filelug.android.docsprovider.DocumentIdUtils;

/**
 * Created by Vincent Chang on 2016/05/23.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class RootCursor extends BaseCursor {

    private static final String TAG = RootCursor.class.getSimpleName();

    private static final String[] DEFAULT_PROJECTION = new String[]{
            DocumentsContract.Root.COLUMN_DOCUMENT_ID,
            DocumentsContract.Root.COLUMN_FLAGS,
            DocumentsContract.Root.COLUMN_ICON,
            DocumentsContract.Root.COLUMN_MIME_TYPES,
            DocumentsContract.Root.COLUMN_ROOT_ID,
            DocumentsContract.Root.COLUMN_TITLE,
            DocumentsContract.Root.COLUMN_SUMMARY,
            DocumentsContract.Root.COLUMN_AVAILABLE_BYTES
    };

    public RootCursor(Context context, String[] projection, Account[] accounts) {
        super(projection == null ? DEFAULT_PROJECTION : projection);

        if ( accounts == null || accounts.length == 0 ) {
            String document_id = DocumentIdUtils.getAccountNotSetDocumentId();
            String title = context.getString(R.string.app_name);
            String summary = context.getResources().getString(R.string.message_not_set);
            addAccountRow(document_id, title, 0, summary);
            return;
        }

        for ( int i=0; i<accounts.length; i++ ) {
            Account account = accounts[i];
            String document_id = DocumentIdUtils.getAccountDocumentId(account.name);
            String title = context.getString(R.string.app_name);
            String summary = account.name;
            addAccountRow(document_id, title, DocumentsContract.Root.FLAG_SUPPORTS_RECENTS, summary);
        }
    }

    private void addAccountRow(String documentId, String title, int flags, String summary) {
        newRow().add(DocumentsContract.Root.COLUMN_ROOT_ID, documentId)
                .add(DocumentsContract.Root.COLUMN_SUMMARY, summary)
                .add(DocumentsContract.Root.COLUMN_FLAGS, flags)
                .add(DocumentsContract.Root.COLUMN_TITLE, title)
                .add(DocumentsContract.Root.COLUMN_DOCUMENT_ID, documentId)
                .add(DocumentsContract.Root.COLUMN_ICON, R.drawable.ic_launcher)
                .add(DocumentsContract.Root.COLUMN_MIME_TYPES, null)
                .add(DocumentsContract.Root.COLUMN_AVAILABLE_BYTES, null);
//        if ( Constants.DEBUG ) Log.d(TAG, "addAccountRow(), add root, " + DocumentsContract.Root.COLUMN_ROOT_ID + "=" + documentId + ", " + DocumentsContract.Root.COLUMN_DOCUMENT_ID + "=" + documentId + ", title=" + title + ", summary=" + summary);
    }

}
