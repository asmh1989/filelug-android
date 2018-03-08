package com.filelug.android.docsprovider.cursor;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.DocumentsContract;

import com.filelug.android.R;
import com.filelug.android.docsprovider.DocumentIdUtils;
import com.filelug.android.provider.usercomputer.UserComputerColumns;
import com.filelug.android.provider.usercomputer.UserComputerCursor;
import com.filelug.android.provider.usercomputer.UserComputerSelection;

/**
 * Created by Vincent Chang on 2016/6/7.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class FLComputerDocumentCursor extends DocumentCursor {

    private static final String TAG = FLRootDirectoryDocumentCursor.class.getSimpleName();

    public FLComputerDocumentCursor(Context context, String authority, String documentId, String[] projection) {
        super(projection);
        String[] idParts = DocumentIdUtils.getComputerIdAndParents(documentId);
        String accountName = idParts[0];
        int computerId = Integer.valueOf(idParts[1]).intValue();
        findDocument(context, accountName, computerId, this.getColumnNames());
    }

    private void findDocument(Context context, String accountName, int computerId, String[] projection) {
//        if ( Constants.DEBUG ) Log.d(TAG, "findDocument(), Arguments: accountName=" + accountName +", computerId=" + computerId +", projection=" + (projection == null ? null : TextUtils.join(",", projection)));

        UserComputerSelection userComputerSelection = new UserComputerSelection()
            .computerId(computerId);
        UserComputerCursor c = userComputerSelection.query(
            context.getContentResolver(),
            new String[] { UserComputerColumns.COMPUTER_ID, UserComputerColumns.COMPUTER_NAME },
            null
        );

        if ( c.moveToFirst() ) {
            String computerDocumentId = DocumentIdUtils.getComputerDocumentId(accountName, c.getComputerId());
            String computerName = c.getComputerName();
            addDocumentRow(computerDocumentId, projection, computerName, 0, DocumentsContract.Document.MIME_TYPE_DIR, R.drawable.ic_laptop, null, null, null);
//            addDocumentRow(computerDocumentId, projection, computerName, Document.FLAG_DIR_SUPPORTS_CREATE, Document.MIME_TYPE_DIR, R.drawable.ic_laptop, null, null, null);
        }
        c.close();
    }

}
