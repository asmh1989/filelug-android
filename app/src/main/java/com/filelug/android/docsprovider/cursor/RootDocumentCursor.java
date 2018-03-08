package com.filelug.android.docsprovider.cursor;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.DocumentsContract.Document;

import com.filelug.android.R;
import com.filelug.android.docsprovider.DocumentIdUtils;

/**
 * Created by Vincent Chang on 2016/05/23.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class RootDocumentCursor extends DocumentCursor {

    private static final String TAG = RootDocumentCursor.class.getSimpleName();

    public RootDocumentCursor(Context context, String documentId, String[] projection) {
        super(projection);
        String title = context.getString(R.string.app_name);
        String summary = DocumentIdUtils.isNotSet(documentId) ? documentId : DocumentIdUtils.getAccountName(documentId);
        addDocumentRow(documentId, this.getColumnNames(), title, 0, Document.MIME_TYPE_DIR, null, null, null, summary);
    }

}
