package com.filelug.android.docsprovider.cursor;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.DocumentsContract.Document;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.RequestFuture;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.crepo.RepositoryErrorObject;
import com.filelug.android.docsprovider.DocumentIdUtils;
import com.filelug.android.provider.usercomputer.UserComputerColumns;
import com.filelug.android.provider.usercomputer.UserComputerCursor;
import com.filelug.android.provider.usercomputer.UserComputerSelection;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.NetworkUtils;
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
public class ListAvailableComputersCursor extends RemoteListCursor {

    private static final String TAG = ListAvailableComputersCursor.class.getSimpleName();

    public ListAvailableComputersCursor(Context context, String parentDocumentId, String[] projection, String sortOrder) {
        super(context, parentDocumentId, projection, sortOrder);
    }

    @Override
    protected void findDocuments(Context context, String parentDocumentId, String[] projection, String sortOrder) {

        if ( DocumentIdUtils.isNotSet(parentDocumentId) ) {
            String errorMsg = context.getResources().getString(R.string.message_account_not_created);
            setErrorInformation(errorMsg);
            return;
        }

//        if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), Arguments: parentDocumentId=" + parentDocumentId + ", projection=" + (projection == null ? null : TextUtils.join(",", projection)) + ", sortOrder=" + sortOrder + ", cursor");

        String accountName = DocumentIdUtils.getAccountName(parentDocumentId);
//        if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), accountName=" + accountName);

        Account account = AccountUtils.getAccount(accountName);
        if ( account == null ) {
            String errorMsg = context.getResources().getString(R.string.message_account_not_created);
            setErrorInformation(errorMsg);
            return;
        }

        if ( !NetworkUtils.isNetworkAvailable(context, null) ) {
            String errorMsg = context.getResources().getString(R.string.message_network_error);
            setErrorInformation(errorMsg);
            return;
        }

        AccountManager accountManager = AccountManager.get(context);
        String filelugAccount = accountManager.getUserData(account, Constants.EXT_PARAM_FILELUG_ACCOUNT);
        String authToken = null;
        try {
            authToken = AccountUtils.getAuthToken3(context, account);
//            if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), authToken=" + authToken);
        } catch (AuthFailureError afe) {
            String errorMsg = afe.getMessage();
//            if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), errorMsg=" + errorMsg);
            setErrorInformation(errorMsg);
            return;
        }

//        if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(),  filelugAccount=" + filelugAccount);

        String locale = context.getResources().getConfiguration().locale.toString();
        int timeOut = context.getResources().getInteger(R.integer.sync_timeout_b);

        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        RepositoryClient.getInstance().findAvailableComputers3(
            authToken,
            locale,
            future,
            future
        );

        List<Integer> computerIdList = new ArrayList<Integer>();

        RepositoryErrorObject errorObject = null;

        try {
            JSONArray response = future.get(timeOut, TimeUnit.MILLISECONDS);

            for ( int i=0; i<response.length(); i++ ) {
                JSONObject jso = response.getJSONObject(i);

                String logStr = "findDocuments(), Insert row i=" + i;

                if ( !jso.has(Constants.PARAM_USER_COMPUTER_ID) ||
                     jso.optString(Constants.PARAM_USER_COMPUTER_ID).equals("") ||
                     jso.optString(Constants.PARAM_USER_COMPUTER_ID).equals("null") ) {
//                    if ( Constants.DEBUG ) Log.d(TAG, logStr);
                    continue;
                }

                String userId = jso.getString(Constants.PARAM_USER_ID);
                int computerId = jso.getInt(Constants.PARAM_COMPUTER_ID);
                String userComputerId = jso.getString(Constants.PARAM_USER_COMPUTER_ID);
                String computerName = jso.getString(Constants.PARAM_COMPUTER_NAME);
                String computerGroup = jso.getString(Constants.PARAM_COMPUTER_GROUP);
                String computerAdminId = jso.getString(Constants.PARAM_COMPUTER_ADMIN_ID);
                String lugServerId = jso.optString(Constants.PARAM_LUG_SERVER_ID, null);

                logStr += ", userId=" + userId + ", computerId=" + computerId + ", userComputerId=" + userComputerId + ", computerName=" + computerName + ", computerGroup=" + computerGroup + ", computerAdminId=" + computerAdminId + ", lugServerId=" + lugServerId;
//                if ( Constants.DEBUG ) Log.d(TAG, logStr);

                TransferDBHelper.createOrUpdateUserComputer(userId, computerId, userComputerId, computerGroup, computerName, computerAdminId, lugServerId);

                computerIdList.add(computerId);
            }
        } catch (Exception e) {
            errorObject= MiscUtils.getErrorObject(context, e, null);
        }

        if ( errorObject != null ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), FindAvailableComputers error!\n" + errorObject.getMessage());
            setErrorInformation(errorObject.getMessage());
            return;
        }

        if ( computerIdList.size() == 0 ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), Not available computers!");
            setErrorInformation(context.getResources().getString(R.string.message_registered_computer_not_found));
            return;
        }

        int[] ids = MiscUtils.integerArrayToIntegerArray(computerIdList.toArray(new Integer[0]));
        UserComputerSelection deleteSelection = new UserComputerSelection()
            .userId(filelugAccount).and()
            .computerIdNot(ids);
        int deleteCount = deleteSelection.delete(context.getContentResolver());
//        if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), Delete " + deleteCount + " rows! userId=" + filelugAccount + ", id not in (" + TextUtils.join(",", computerIdList) + ")");

        String orderBy = "";
        if ( !TextUtils.isEmpty(sortOrder) ) {
            if ( sortOrder.contains(Document.COLUMN_DISPLAY_NAME) ) {
                orderBy = sortOrder.replace(Document.COLUMN_DISPLAY_NAME, UserComputerColumns.COMPUTER_NAME);
            }
        }
//        if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), orderBy=" + orderBy);

        UserComputerSelection userComputerSelection = new UserComputerSelection()
            .userId(filelugAccount);
        UserComputerCursor c = userComputerSelection.query(
            context.getContentResolver(),
            new String[] { UserComputerColumns.COMPUTER_ID, UserComputerColumns.COMPUTER_NAME },
            orderBy
        );
        while( c.moveToNext() ) {
            int computerId = c.getComputerId();
            String computerName = c.getComputerName();
            String computerDocumentId = DocumentIdUtils.getComputerDocumentId(accountName, computerId);
//            if ( Constants.DEBUG ) Log.d(TAG, "findDocuments(), Add row! computerId=" + computerId + ", computerName=" + computerName + ", computerDocumentId=" + computerDocumentId);
            addDocumentRow(computerDocumentId, projection, computerName, 0, Document.MIME_TYPE_DIR, R.drawable.ic_laptop, null, null, null);
//            addDocumentRow(computerDocumentId, projection, computerName, Document.FLAG_DIR_SUPPORTS_CREATE, Document.MIME_TYPE_DIR, R.drawable.ic_laptop, null, null, null);
        }
        c.close();
    }

}
