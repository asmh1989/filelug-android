package com.filelug.android.docsprovider.cursor;

import android.annotation.TargetApi;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

/**
 * Created by Vincent Chang on 2016/05/23.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class DocumentCursor extends BaseCursor {

    private static final String TAG = DocumentCursor.class.getSimpleName();

    protected static final String COLUMN_MS_OFFICE_DOC_TYPE = "com_microsoft_office_doctype";
    protected static final String COLUMN_MS_OFFICE_TERMS_OF_USE = "com_microsoft_office_termsofuse";
    protected static final String COLUMN_MS_OFFICE_SERVICE_NAME = "com_microsoft_office_servicename";

    private static final String[] DEFAULT_PROJECTION = new String[] {
        DocumentsContract.Document.COLUMN_DOCUMENT_ID,
        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
        DocumentsContract.Document.COLUMN_FLAGS,
        DocumentsContract.Document.COLUMN_MIME_TYPE,
        DocumentsContract.Document.COLUMN_ICON,
        DocumentsContract.Document.COLUMN_LAST_MODIFIED,
        DocumentsContract.Document.COLUMN_SIZE,
        DocumentsContract.Document.COLUMN_SUMMARY
    };

    private static final String PROJECTS_STRING_DEFAULT = TextUtils.join(",", DEFAULT_PROJECTION);

    public DocumentCursor(String[] projection) {
        super(projection == null ? DEFAULT_PROJECTION : projection);
    }

    protected void addDocumentRow(String document_id, String[] projection, String name, int flags, String contentType, Integer iconRes, Long lastModified, Long size, String summary) {
        if ( projection == null || projection.length == 0 ) return;

        String projectionString = TextUtils.join(",", projection);
        RowBuilder newRow = newRow();

        if ( PROJECTS_STRING_DEFAULT.equals(projectionString) ) {
            newRow.add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, document_id);
            newRow.add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, name);
            newRow.add(DocumentsContract.Document.COLUMN_FLAGS, flags);
            newRow.add(DocumentsContract.Document.COLUMN_MIME_TYPE, contentType);
            newRow.add(DocumentsContract.Document.COLUMN_ICON, iconRes == null ? null : iconRes.intValue());
            newRow.add(DocumentsContract.Document.COLUMN_LAST_MODIFIED, lastModified);
            newRow.add(DocumentsContract.Document.COLUMN_SIZE, size);
            newRow.add(DocumentsContract.Document.COLUMN_SUMMARY, summary);
            return;
        }

        if ( projectionString.contains(DocumentsContract.Document.COLUMN_DOCUMENT_ID) ) {
            newRow.add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, document_id);
        }
        if ( projectionString.contains(DocumentsContract.Document.COLUMN_DISPLAY_NAME) ) {
            newRow.add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, name);
        }
        if ( projectionString.contains(DocumentsContract.Document.COLUMN_FLAGS) ) {
            newRow.add(DocumentsContract.Document.COLUMN_FLAGS, flags);
        }
        if ( projectionString.contains(DocumentsContract.Document.COLUMN_MIME_TYPE) ) {
            newRow.add(DocumentsContract.Document.COLUMN_MIME_TYPE, contentType);
        }
        if ( projectionString.contains(DocumentsContract.Document.COLUMN_ICON) ) {
            newRow.add(DocumentsContract.Document.COLUMN_ICON, iconRes == null ? null : iconRes.intValue());
        }
        if ( projectionString.contains(DocumentsContract.Document.COLUMN_LAST_MODIFIED) ) {
            newRow.add(DocumentsContract.Document.COLUMN_LAST_MODIFIED, lastModified);
        }
        if ( projectionString.contains(DocumentsContract.Document.COLUMN_SIZE) ) {
            newRow.add(DocumentsContract.Document.COLUMN_SIZE, size);
        }
        if ( projectionString.contains(DocumentsContract.Document.COLUMN_SUMMARY) ) {
            newRow.add(DocumentsContract.Document.COLUMN_SUMMARY, summary);
        }
        if ( projectionString.contains(MediaStore.MediaColumns.DATA) ) {
            newRow.add(MediaStore.MediaColumns.DATA, name);
        }
        if ( projectionString.contains(COLUMN_MS_OFFICE_DOC_TYPE) ) {
            newRow.add(COLUMN_MS_OFFICE_DOC_TYPE, "<consumer>");
        }
        if ( projectionString.contains(COLUMN_MS_OFFICE_TERMS_OF_USE) ) {
            newRow.add(COLUMN_MS_OFFICE_TERMS_OF_USE, "<I agree to the terms located at http://go.microsoft.com/fwlink/p/?LinkId=528381>");
        }
        if ( projectionString.contains(COLUMN_MS_OFFICE_SERVICE_NAME) ) {
            newRow.add(COLUMN_MS_OFFICE_SERVICE_NAME, "Filelug");
        }

//        if ( Constants.DEBUG ) Log.d(TAG, "addDocumentRow(), add document row, document_id=" + document_id + ", name=" + name + ", contentType=" + contentType + ", iconRes=" + iconRes + ", lastModified=" + lastModified + ", size=" + size + ", summary=" + summary);
    }

}
