package com.filelug.android.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.filelug.android.Constants;
import com.filelug.android.MainApplication;
import com.filelug.android.R;
import com.filelug.android.crepo.BaseResponseError;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.crepo.RepositoryErrorObject;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * Created by Vincent Chang on 2016/2/22.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public class FilelugUtils {

    private static final String TAG = FilelugUtils.class.getSimpleName();
    private static Context mContext = MainApplication.getInstance().getApplicationContext();

    public interface Callback {
        void onError(int errorCode, String errorMessage);
        void onSuccess(Bundle result);
    };

    // DownloadService.onHandleIntent() ==>
    // UploadService.onHandleIntent() ==>
    public static Bundle pingDesktopA(final Context context, String authToken, String userId) {
//        if (Constants.DEBUG) Log.d(TAG, "pingDesktopA(), authToken=" + authToken + ", userId=" + userId);

        Account account = AccountUtils.getAccountByUserId(userId);

        String locale = context.getResources().getConfiguration().locale.toString();
        int timeOut = context.getResources().getInteger(R.integer.sync_timeout_a);
        boolean socketConntected = false;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        RepositoryClient.getInstance().pingDesktop(
            authToken,
            userId,
            locale,
            future,
            future
        );

        Bundle result = new Bundle();
        RepositoryErrorObject errorObject = null;

        try {
            JSONObject res = future.get(timeOut, TimeUnit.MILLISECONDS);
            long uploadSizeLimit = res.optLong(Constants.PARAM_UPLOAD_SIZE_LIMIT);
            long downloadSizeLimit = res.optLong(Constants.PARAM_DOWNLOAD_SIZE_LIMIT);
            result.putLong(Constants.PARAM_UPLOAD_SIZE_LIMIT, uploadSizeLimit);
            result.putLong(Constants.PARAM_DOWNLOAD_SIZE_LIMIT, downloadSizeLimit);
            socketConntected = true;
        } catch (Exception e) {
            errorObject = MiscUtils.getErrorObject(context, e, account);
        }

        if ( errorObject != null ) {
//            if (Constants.DEBUG) Log.d(TAG, "pingDesktopA(), errorObject=" + errorObject);
            if ( errorObject.getCode() > -1 ) {
                result.putInt(Constants.EXT_PARAM_ERROR_CODE, errorObject.getCode());
            }
            result.putString(Constants.EXT_PARAM_ERROR_MESSAGE, errorObject.getMessage());
        }

        if ( account != null ) {
            boolean originSocketConnected = AccountUtils.isSocketConnected(account);
            if ( socketConntected != originSocketConnected ) {
                AccountUtils.setSocketConnected(account, socketConntected);
                if ( socketConntected ) {
                    AccountUtils.noticeDesktopConnectionChanged(Constants.MESSAGE_PING_DESKTOP_SUCCESS);
                } else {
                    AccountUtils.noticeDesktopConnectionChanged(Constants.MESSAGE_PING_DESKTOP_ERROR);
                }
            }
        }

        return result;
    }

    // ConfirmDownloadActivity.pingDesktop() ==>
    // ConfirmUploadActivity.pingDesktop() ==>
    // ConfirmUploadActivity.changeUploadToFolder() ==>
    // OpenFromFilelugActivity.downloadFilesToCache() ==>
    // RemoteFilesLayout.pingDesktop() ==>
    // RemoteFolderPreference.showDialog() ==>
    // TransferFragment.checkTransferStatus() ==>
    // TransferFragment.downloadAgain() ==>
    // TransferFragment.resumeDownloadFile() ==>
    // TransferFragment.uploadAgain() ==>
    // TransferFragment.resumeUploadFile() ==>
    public static boolean pingDesktopB(final Context context, final Callback callback) {
//        if (Constants.DEBUG) Log.d(TAG, "pingDesktopB()");

        final AccountManager accountManager = AccountManager.get(context);
        final Account activeAccount = AccountUtils.getActiveAccount();
        if ( activeAccount == null ) {
            return false;
        }

        if ( !NetworkUtils.isNetworkAvailable(context) ) {
            return false;
        }

        AccountUtils.AuthTokenCallback authTokenCallback = new AccountUtils.AuthTokenCallback() {

            @Override
            public void onError(final String errorMessage) {
//                if (Constants.DEBUG) Log.d(TAG, "pingDesktopB() --> authTokenCallback.onError(): errorMessage=" + errorMessage);
                MsgUtils.showWarningMessage(context, errorMessage);
            }

            @Override
            public void onSuccess(final String authToken) {
//                if (Constants.DEBUG) Log.d(TAG, "pingDesktopB() --> authTokenCallback.onSuccess(): authToken=" + authToken);

                final String userId = accountManager.getUserData(activeAccount, Constants.EXT_PARAM_FILELUG_ACCOUNT);
                String lugServerId = accountManager.getUserData(activeAccount, Constants.PARAM_LUG_SERVER_ID);
                if ( !TextUtils.isEmpty(lugServerId) ) {
                    _pingDesktop(context, authToken, userId, callback);
                    return;
                }

//                if (Constants.DEBUG) Log.d(TAG, "pingDesktopB() --> authTokenCallback.onSuccess(): lugServerId is empty!");

                String reqConMessage = null;
                String computerName = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_NAME);
                if ( !TextUtils.isEmpty(computerName) ) {
                    reqConMessage = String.format(context.getString(R.string.message_request_connection_2), computerName);
                } else {
                    reqConMessage = context.getString(R.string.message_request_connection_1);
                }

                Callback reqConnCallback = new Callback() {
                    @Override
                    public void onError(int errorCode, String errorMessage) {
//                        if (Constants.DEBUG) Log.d(TAG, "pingDesktopB() --> reqConnCallback.onError(): errorMessage=" + errorMessage);
                        MsgUtils.showWarningMessage(context, errorMessage);
                        if ( callback != null ) {
                            callback.onError(errorCode, errorMessage);
                        }
                    }
                    @Override
                    public void onSuccess(Bundle result) {
//                        if (Constants.DEBUG) Log.d(TAG, "pingDesktopB() --> reqConnCallback.onSuccess(): result=" + MiscUtils.convertBundleToString(result));
                        _pingDesktop(context, authToken, userId, callback);
                    }
                };
                _requestConnection(context, activeAccount, authToken, reqConMessage, true, reqConnCallback);
            }
        };

        AccountUtils.getAuthToken2(context, authTokenCallback);

        return true;
    }

    // pingDesktopB() --> authTokenCallback.onSuccess ==>
    // pingDesktopB() --> reqConnCallback.onSuccess ==>
    private static void _pingDesktop(final Context context, final String authToken, final String account, final Callback callback) {
//        if (Constants.DEBUG) Log.d(TAG, "_pingDesktop(), authToken=" + authToken + ", account=" + account);

        String locale = context.getResources().getConfiguration().locale.toString();

        RepositoryClient.getInstance().pingDesktop(
            authToken,
            account,
            locale,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    long uploadSizeLimit = response.optLong(Constants.PARAM_UPLOAD_SIZE_LIMIT);
                    long downloadSizeLimit = response.optLong(Constants.PARAM_DOWNLOAD_SIZE_LIMIT);

//                    if (Constants.DEBUG) Log.d(TAG, "_pingDesktop() --> pingDesktop().onResponse(), uploadSizeLimit=" + uploadSizeLimit + ", downloadSizeLimit=" + downloadSizeLimit);

                    Account activeAccount = AccountUtils.getActiveAccount();
                    if (activeAccount != null) {
                        if ( !AccountUtils.isSocketConnected(activeAccount) ) {
                            AccountUtils.setSocketConnected(activeAccount, true);
                            AccountUtils.noticeDesktopConnectionChanged(Constants.MESSAGE_PING_DESKTOP_SUCCESS);
                        }
                    }

                    if (callback != null) {
                        Bundle result = new Bundle();
                        result.putLong(Constants.PARAM_UPLOAD_SIZE_LIMIT, uploadSizeLimit);
                        result.putLong(Constants.PARAM_DOWNLOAD_SIZE_LIMIT, downloadSizeLimit);
                        result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                        callback.onSuccess(result);
                    }
                }
            },
            new BaseResponseError(false, context) {
                @Override
                protected void beforeShowErrorMessage(VolleyError volleyError) {
//                    if (Constants.DEBUG) Log.d(TAG, "_pingDesktop() --> pingDesktop().beforeShowErrorMessage(), errorMsg=" + MiscUtils.getVolleyErrorMessage(volleyError));
                    if ( callback != null ) {
                        int errorCode = MiscUtils.getStatusCode(volleyError);
                        String errorMsg = MiscUtils.getVolleyErrorMessage(volleyError);
                        callback.onError(errorCode, errorMsg);
                    }
                }
            }
        );
    }


    // pingDesktopB() --> authTokenCallback.onSuccess ==>
    // requestConnectionB() --> authTokenCallback.onSuccess ==>
    private static void _requestConnection(final Context context, final Account account, final String authToken, final String message, boolean showProgressDialog, final Callback callback) {
//        if (Constants.DEBUG) Log.d(TAG, "_requestConnection(), context=" + context.getClass().getName() + ", account=" + account.toString() + ", authToken=" + authToken + ", message=" + message + ", showProgressDialog=" + showProgressDialog);

        String locale = context.getResources().getConfiguration().locale.toString();
        String dialogTitle = context.getResources().getString(R.string.title_unable_to_connect_to_computer);

//        if ( showProgressDialog ) {
//            if ( !(context instanceof Activity) ) {
//                showProgressDialog = false;
//            }
//        }
        final boolean showDialog = showProgressDialog;

        MaterialDialog tempDialog = null;
        if ( showDialog ) {
            tempDialog = DialogUtils.createProgressDialog(context, dialogTitle, message);
        }
        final MaterialDialog dialog = tempDialog;

        if ( showDialog ) {
            dialog.show();
        } else {
            MsgUtils.showToast(context, message);
        }

        RepositoryClient.getInstance().requestConnection(
            authToken,
            locale,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    String userComputerId = response.optString(Constants.PARAM_USER_COMPUTER_ID);
                    String userId = response.optString(Constants.PARAM_USER_ID);
                    int computerId = response.optInt(Constants.PARAM_COMPUTER_ID);
                    String computerGroup = response.optString(Constants.PARAM_COMPUTER_GROUP);
                    String computerName = response.optString(Constants.PARAM_COMPUTER_NAME);
                    String lugServerId = response.optString(Constants.PARAM_LUG_SERVER_ID);

//                    if (Constants.DEBUG) Log.d(TAG, "_requestConnection() --> requestConnection().onResponse(), userComputerId=" + userComputerId + ", userId=" + userId + ", computerId=" + computerId + ", computerGroup=" + computerGroup + ", computerName=" + computerName + ", lugServerId=" + lugServerId);

                    AccountManager accountManager = AccountManager.get(context);
                    if (account != null) {
                        accountManager.setUserData(account, Constants.PARAM_SOCKET_CONNECTED, Boolean.TRUE.toString());
                        accountManager.setUserData(account, Constants.PARAM_LUG_SERVER_ID, lugServerId);
                    }
                    AccountUtils.noticeDesktopConnectionChanged(Constants.MESSAGE_REQUEST_CONNECTION_SUCCESS);

                    if (callback != null) {
                        Bundle result = new Bundle();
                        result.putString(Constants.PARAM_USER_COMPUTER_ID, userComputerId);
                        result.putString(Constants.PARAM_USER_ID, userId);
                        result.putInt(Constants.PARAM_COMPUTER_ID, computerId);
                        result.putString(Constants.PARAM_COMPUTER_GROUP, computerGroup);
                        result.putString(Constants.PARAM_COMPUTER_NAME, computerName);
                        result.putString(Constants.PARAM_LUG_SERVER_ID, lugServerId);
                        callback.onSuccess(result);
                    }

                    if ( showDialog ) {
                        dialog.dismiss();
                    }
                }
            },
            new BaseResponseError(false, context) {
                @Override
                protected void beforeShowErrorMessage(VolleyError volleyError) {
                    super.beforeShowErrorMessage(volleyError);
                    AccountManager accountManager = AccountManager.get(context);
                    String computerName = null;
                    if (account != null) {
                        computerName = accountManager.getUserData(account, Constants.PARAM_COMPUTER_NAME);
                    }
                    int errorCode = MiscUtils.getStatusCode(volleyError);
                    String errorMsg = null;
                    if ( ( volleyError instanceof TimeoutError) ||
                         ( ( volleyError instanceof NoConnectionError) &&
                           ((NoConnectionError)volleyError).getMessage().contains("javax.net.ssl.SSLException: Connection closed by peer") ) ) {
                        if ( showDialog ) {
                            String msg_prefix = null;
                            if ( computerName == null ) {
                                msg_prefix = context.getResources().getString(R.string.message_request_connection_failed_1);
                            } else {
                                msg_prefix = String.format(context.getResources().getString(R.string.message_request_connection_failed_2), computerName);
                            }
                            errorMsg = msg_prefix + "\n\n" + context.getResources().getString(R.string.message_request_connection_failed_details);
                        } else {
                            if ( computerName == null ) {
                                errorMsg = context.getResources().getString(R.string.message_request_connection_failed_1);
                            } else {
                                errorMsg = String.format(context.getResources().getString(R.string.message_request_connection_failed_2), computerName);
                            }
                        }
                    } else {
                        errorMsg = getMessage(volleyError);
                    }
//                    if (Constants.DEBUG) Log.d(TAG, "_requestConnection() --> requestConnection().onErrorResponse(), errorCode=" + errorCode + ", errorMsg=" + errorMsg);
                    if ( callback != null ) {
                        callback.onError(errorCode, errorMsg);
                    }
                    if ( showDialog ) {
                        dialog.dismiss();
                    }
                }
            }
        );
    }

    // actionWhen503() -->
    private static void requestConnectionB(final Context context, final Account account, final boolean showProgressDialog) {
//        if ( Constants.DEBUG ) Log.d(TAG, "requestConnectionB()");

        AccountUtils.setSocketConnected(account, false);

        AccountUtils.AuthTokenCallback authTokenCallback = new AccountUtils.AuthTokenCallback() {

            @Override
            public void onError(String errorMessage) {
//                if ( Constants.DEBUG ) Log.d(TAG, "requestConnectionB() --> authTokenCallback.onError(): errorMessage=" + errorMessage);
                AccountUtils.noticeDesktopConnectionChanged(Constants.MESSAGE_REQUEST_CONNECTION_GET_AUTH_TOKEN_ERROR);
            }

            @Override
            public void onSuccess(final String authToken) {
//                if ( Constants.DEBUG ) Log.d(TAG, "requestConnectionB() --> authTokenCallback.onSuccess(): authToken=" + authToken);
                AccountManager accountManager = AccountManager.get(context);
                String computerName = accountManager.getUserData(account, Constants.PARAM_COMPUTER_NAME);
                String reqConMessage = null;
                if ( !TextUtils.isEmpty(computerName) ) {
                    reqConMessage = String.format(context.getString(R.string.message_request_connection_2), computerName);
                } else {
                    reqConMessage = context.getString(R.string.message_request_connection_1);
                }

                Callback callback = new Callback() {

                    @Override
                    public void onError(int errorCode, String errorMessage) {
//                        if ( Constants.DEBUG ) Log.d(TAG, "requestConnectionB() --> authTokenCallback.onSuccess() --> callback.onError(): errorCode=" + errorCode + ", errorMessage=" + errorMessage );
                        AccountUtils.noticeDesktopConnectionChanged(Constants.MESSAGE_REQUEST_CONNECTION_ERROR);
                        if ( showProgressDialog ) {
                            MsgUtils.showWarningMessage(context, errorMessage);
                        } else {
                            MsgUtils.showToast(context, errorMessage);
                        }
                    }

                    @Override
                    public void onSuccess(Bundle result) {
//                        if ( Constants.DEBUG ) Log.d(TAG, "requestConnectionB() --> authTokenCallback.onSuccess() --> callback.onSuccess(): result=" + MiscUtils.convertBundleToString(result));
                        AccountUtils.setSocketConnected(account, true);
                        AccountUtils.noticeDesktopConnectionChanged(Constants.MESSAGE_REQUEST_CONNECTION_SUCCESS);
                    }

                };

                _requestConnection(context, account, authToken, reqConMessage, showProgressDialog, callback);
            }

        };

        AccountUtils.getAuthToken(context, account, authTokenCallback);
    }

    public static void actionWhen403() {
//        if ( Constants.DEBUG ) Log.d(TAG, "actionWhen403()");
        Account activeAccount = AccountUtils.getActiveAccount();
        actionWhen403(activeAccount);
    }

    public static void actionWhen403(Account account) {
//        if ( Constants.DEBUG ) Log.d(TAG, "actionWhen403(), account=" + ( account==null ? null : account.name ));
        if ( account == null ) {
            return;
        }

        AccountUtils.setLoggedIn(account, false);
        AccountUtils.removeAuthToken(account);
        AccountUtils.noticeDesktopConnectionChanged(Constants.MESSAGE_RESPONSE_403_SESSION_ID_NOT_EXIST);
    }

    public static void actionWhen501() {
//        if ( Constants.DEBUG ) Log.d(TAG, "actionWhen501()");
        Account activeAccount = AccountUtils.getActiveAccount();
        actionWhen501(activeAccount);
    }

    public static void actionWhen501(Account account) {
//        if ( Constants.DEBUG ) Log.d(TAG, "actionWhen501(), account=" + ( account==null ? null : account.name ));
        if ( account == null ) {
            return;
        }

        AccountManager accountManager = AccountManager.get(mContext);
        accountManager.setUserData(account, Constants.PARAM_COMPUTER_ID, null);
        accountManager.setUserData(account, Constants.PARAM_COMPUTER_NAME, null);
        AccountUtils.setSocketConnected(account, false);
        AccountUtils.noticeDesktopConnectionChanged(Constants.MESSAGE_RESPONSE_501_COMPUTER_NOT_EXIST);
    }

    public static void actionWhen503(Context context) {
//        if ( Constants.DEBUG ) Log.d(TAG, "actionWhen503()");
        Account activeAccount = AccountUtils.getActiveAccount();
        actionWhen503(context, activeAccount, true);
    }

    public static void actionWhen503(Context context, Account account, boolean showProgressDialog) {
//        if ( Constants.DEBUG ) Log.d(TAG, "actionWhen503(), showProgressDialog=" + showProgressDialog + ", account=" + ( account==null ? null : account.name ));
        if ( account == null ) {
            return;
        }

        requestConnectionB(context, account, showProgressDialog);
    }

}
