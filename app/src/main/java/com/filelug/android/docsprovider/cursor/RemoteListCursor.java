package com.filelug.android.docsprovider.cursor;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

/**
 * Created by Vincent Chang on 2016/05/23.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public abstract class RemoteListCursor extends DocumentCursor {

    private static final String TAG = RemoteListCursor.class.getSimpleName();

    public RemoteListCursor(Context context, String parentDocumentId, String[] projection, String sortOrder) {
        super(projection);
        findDocuments(context, parentDocumentId, this.getColumnNames(), sortOrder);
    }

    abstract void findDocuments(Context context, String parentDocumentId, String[] projection, String sortOrder);

}
