package com.filelug.android.ui.activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.BaseResponseError;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.messaging.GCMRegistrationIntentService;
import com.filelug.android.messaging.PushService;
import com.filelug.android.ui.model.ComputerObject;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.FileCache;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.NetworkUtils;
import com.filelug.android.util.PermissionsHelper;
import com.filelug.android.util.PrefUtils;
import com.filelug.android.util.TransferDBHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Vincent Chang on 2017/2/24.
 * Copyright (c) 2017 Filelug. All rights reserved.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private BroadcastReceiver mGCMRegistrationBroadcastReceiver;
    private BroadcastReceiver mBaiduRegistrationBroadcastReceiver;
    protected Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		if (Constants.DEBUG) Log.d(TAG, "onCreate()");
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
//                if (Constants.DEBUG) Log.d(TAG, "mHandler.handleMessage(): msg=" + msg);
                loginOrConnectStatusChanged(msg.what);
            }
        };
    }

    // accountLogin() ==>
    // connectToAccountDefaultComputer() --> AccountUtils.connectToComputer() --> ResultCallback.result() ==>
    // beforeFindAvailableComputers() --> getAuthToken() --> AuthTokenCallback.onError() ==>
    // findAvailableComputers() --> findAvailableComputers3() --> Response.Listener.onResponse() ==>
    // findAvailableComputers() --> findAvailableComputers3() --> BaseResponseError.afterShowErrorMessage() ==>
    // doComputerChanged() --> AccountUtils.connectToComputer() --> ResultCallback.result() ==>
    // doComputerChanged() --> AccountUtils.connectToComputer() --> ResultCallback.result() ==>
    // showComputerSelectionDialog() --> OnCancelListener.onCancel() ==>
    protected void noticeLoginOrConnectStatusChanged(int status) {
//        if (Constants.DEBUG) Log.d(TAG, "noticeLoginOrConnectStatusChanged(), status=" + status);
        Message msg = new Message();
        msg.what = status;
        mHandler.sendMessage(msg);
    }

    // MainActivity2.loginOrConnectStatusChanged() ==>
    protected void initUIObjects(int status) {
    }

    // onCreate().mHandler.handleMessage() ==>
    public abstract void loginOrConnectStatusChanged(int status);

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//		if (Constants.DEBUG) Log.d(TAG, "onPostCreate()");
        registerPushServices();
    }

    // onPostCreate() ==>
    private void registerPushServices() {
//		if (Constants.DEBUG) Log.d(TAG, "registerPushServices()");
        int pushServiceType = PushService.PUSH_SERVICE_TYPE_NONE;

        if ( checkPlayServices() ) {
            pushServiceType = PushService.PUSH_SERVICE_TYPE_GCM;
//		} else {
//			if ( checkBaiduServices() ) {
//				pushServiceType = PushService.PUSH_SERVICE_TYPE_BAIDU;
//			}
        }

        if ( PrefUtils.getPushServiceType() != pushServiceType ) {
            PrefUtils.setPushServiceType(pushServiceType);
            if ( PrefUtils.getPushServiceType() != PushService.PUSH_SERVICE_TYPE_NONE ) {
                PrefUtils.setPushServiceToken(UUID.randomUUID().toString());
            }
        }

//        if (Constants.DEBUG) Log.d(TAG, "registerPushServices(), pushServiceType=" + pushServiceType);

        if ( pushServiceType == PushService.PUSH_SERVICE_TYPE_GCM ) {
            mGCMRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean systemChangeDeviceToken = intent.getBooleanExtra(Constants.EXT_PARAM_SYSTEM_CHANGE_DEVICE_TOKEN, false);
                    boolean gcmTokenChanged = PrefUtils.getGCMTokenChanged();
//					if (Constants.DEBUG) Log.d(TAG, "onCreate().onReceive(), systemChangeDeviceToken="+systemChangeDeviceToken+", gcmTokenChanged="+gcmTokenChanged);
                    Account activeAccount = AccountUtils.getActiveAccount();
                    if ( activeAccount != null && ( systemChangeDeviceToken || gcmTokenChanged ) ) { // Device Token 被系統更換
                        beforeDoCreateOrUpdatePushServiceToken();
                    } else { // App 開啟取得 Device Token
                        beforeInitAccount();
                    }
                }
            };
            Intent intent = new Intent(this, GCMRegistrationIntentService.class);
            startService(intent);
//		} else if ( pushServiceType == PushService.PUSH_SERVICE_TYPE_BAIDU ) {
//			mBaiduRegistrationBroadcastReceiver = ...;
//			Intent intent = new Intent(this, BaiduRegistrationIntentService.class);
//			startService(intent);
        } else {
            beforeInitAccount();
        }
    }

    // registerPushServices() ==>
    private boolean checkPlayServices() {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
//			GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), this, 0);
            Log.e("RepairableException", "Google Play Services is not installed, up-to-date, or enabled.");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                String errorString = apiAvailability.getErrorString(resultCode);
                String format = getResources().getString(R.string.message_google_play_services_not_available);
                String msg = String.format(format, resultCode, errorString);
                MsgUtils.showWarningMessage(BaseActivity.this, msg);
//				apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
//				if (Constants.DEBUG) Log.i(TAG, "checkPlayServices(), This device is not supported.");
            }
            return false;
        }

        return true;
    }

    // beforeDoCreateOrUpdatePushServiceToken() ==>
    // beforeFindAvailableComputers() ==>
    // beforeDoComputerChanged() ==>
    protected void getAuthToken(AccountUtils.AuthTokenCallback callback) {
        if ( !NetworkUtils.isNetworkAvailable(BaseActivity.this) ) {
            return;
        }
        AccountUtils.getAuthToken(BaseActivity.this, callback);
    }

    // registerPushServices().mGCMRegistrationBroadcastReceiver.onReceive() ==>
    private void beforeDoCreateOrUpdatePushServiceToken() {
        getAuthToken(new AccountUtils.AuthTokenCallback() {
            @Override
            public void onError(String errorMessage) {
                String msg = String.format(getResources().getString(R.string.message_failed_to_update_device_token), errorMessage);
                MsgUtils.showToast(BaseActivity.this, msg);
//                if (Constants.DEBUG) Log.d(TAG, "beforeDoCreateOrUpdatePushServiceToken(), errorMessage=" + errorMessage);
            }
            @Override
            public void onSuccess(String authToken) {
                doCreateOrUpdatePushServiceToken(authToken);
            }
        });
    }

    // beforeDoCreateOrUpdatePushServiceToken() ==>
    private void doCreateOrUpdatePushServiceToken(final String authToken) {
        String[] sessionArray = AccountUtils.getUserSessions(BaseActivity.this);
        String pushServiceTypeStr = PrefUtils.getPushServiceTypeString();
        final String deviceToken = PrefUtils.getPushServiceToken();
        String deviceVersion = String.valueOf(Build.VERSION.SDK_INT);
        String locale = getResources().getConfiguration().locale.toString();
        RepositoryClient.getInstance().createOrUpdateDeviceToken(
            authToken,
            sessionArray,
            pushServiceTypeStr,
            deviceToken,
            deviceVersion,
            locale,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
//					if (Constants.DEBUG) Log.d(TAG, "doCreateOrUpdatePushServiceToken(), Token updated!");
                }
            },
            new BaseResponseError(true, BaseActivity.this, BaseResponseError.MESSAGE_TYPE_WARNING_MESSAGE) {
                @Override
                protected String getMessage(VolleyError volleyError) {
                    String message = MiscUtils.getVolleyErrorMessage(volleyError);
//                    if (Constants.DEBUG) Log.d(TAG, "doCreateOrUpdatePushServiceToken(), errorMessage=" + message);
                    return String.format(getResources().getString(R.string.message_failed_to_update_device_token), message);
                }
            }
        );
    }

    // registerPushServices ==>
    private void beforeInitAccount() {
        List<String> permissionList = new ArrayList<String>();
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED ) {
            permissionList.add(Manifest.permission.GET_ACCOUNTS);
//            if (Constants.DEBUG) Log.d(TAG, "beforeInitAccount(), permissionList.add " + Manifest.permission.GET_ACCOUNTS);
        }
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//            if (Constants.DEBUG) Log.d(TAG, "beforeInitAccount(), permissionList.add " + Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if ( permissionList.size() > 0 ) {
            showCurrentPermissionDialog(permissionList);
            return;
        }
        initAccount();
    }

    private void showCurrentPermissionDialog(final List<String> permissionList) {
        MaterialDialog.SingleButtonCallback positiveButtonCallback = new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                ActivityCompat.requestPermissions(BaseActivity.this, permissionList.toArray(new String[0]), Constants.REQUEST_PERMISSION_ACCOUNTS_AND_STORAGE);
            }
        };
        MaterialDialog.SingleButtonCallback neutralButtonCallback = new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                alertAndExit();
            }
        };
        MaterialDialog mPermissionsDialog = DialogUtils.createPermissionsDialog(this, R.string.title_permissions_dialog, -1, R.string.btn_label_go_to_settings, R.string.btn_label_deny, positiveButtonCallback, neutralButtonCallback);
        PermissionsHelper.refreshPermissionsState(this, mPermissionsDialog, false);
        mPermissionsDialog.show();
    }

    // beforeInitAccount ==>
    // onRequestPermissionsResult ==>
    private void initAccount() {
        Account[] accounts = AccountUtils.getFilelugAccounts();

        String activeAccountName = PrefUtils.getActiveAccount();
//		if (Constants.DEBUG) Log.d(TAG, "initAccount(), activeAccountName="+activeAccountName);
        if ( accounts == null || accounts.length == 0 ) {
            if ( !TextUtils.isEmpty(activeAccountName) ) {
                FileCache.cleanActiveAccountCache(activeAccountName);
                PrefUtils.cleanActiveInfo(activeAccountName);
            }
            checkShowInitPage();
        } else {
            checkOldAccountName(accounts);
        }
    }

    private void checkOldAccountName(Account[] accounts) {
//        Log.d(TAG, "checkOldAccountName(): accounts.length=" + accounts.length);
        ArrayList<Account> accountsToRemove = new ArrayList<Account>();
        for ( Account account : accounts ) {
            if ( account.name.endsWith(")") ) {
                accountsToRemove.add(account);
//                Log.d(TAG, "checkOldAccountName(): accountsToRemove.add(" + account.name + ")");
            }
        }

        if ( accountsToRemove.size() > 0 ) {
            removeOldAccount(accountsToRemove.toArray(new Account[0]));
        } else {
            checkShowInitPage();
        }
    }

    private void removeOldAccount(final Account[] accounts) {
//        Log.d(TAG, "removeOldAccount()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                AccountManager accountManager = AccountManager.get(BaseActivity.this);
                for ( Account account : accounts ) {
//                    Log.d(TAG, "removeOldAccount(): Remove account " + account.name);
                    boolean booleanResult = false;
                    try {
                        if ( Build.VERSION.SDK_INT >= 22 ) {
                            AccountManagerFuture<Bundle> future = accountManager.removeAccount(account, BaseActivity.this, null, null);
                            Bundle result = future.getResult();
                            booleanResult = result.getBoolean("booleanResult");
//                            Log.d(TAG, "removeOldAccount(): SDK=" + Build.VERSION.SDK_INT + ", booleanResult=" + booleanResult);
                        } else {
                            AccountManagerFuture<Boolean> future = accountManager.removeAccount(account, null, null);
                            Boolean result = future.getResult();
                            booleanResult = result.booleanValue();
//                            Log.d(TAG, "removeOldAccount(): SDK=" + Build.VERSION.SDK_INT + ", booleanResult=" + booleanResult);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "removeOldAccount(): Remove old account error! account=" + account + ", " + e.getMessage());
                    }
                    if ( booleanResult ) {
                        PrefUtils.generateAccounts();
                    }
                }
                checkShowInitPage();
            }
        }).start();
    }

    private void checkShowInitPage() {
//        if (Constants.DEBUG) Log.d(TAG, "initAccount()");
        if ( PrefUtils.isShowInitialPage() ) {
//            if (Constants.DEBUG) Log.d(TAG, "initAccount(), ShowInitialPage");
            Intent intent = new Intent(this, InitialPageActivity.class);
            intent.putExtra(Constants.EXT_PARAM_REQUEST_FROM_ACTIVITY, BaseActivity.class.getName());
            startActivityForResult(intent, Constants.REQUEST_INITIAL);
        } else {
            checkActiveAccount();
        }
    }

    // initAccount() ==>
    private void checkActiveAccount() {
        Account activeAccount = AccountUtils.getActiveAccount();
//		if (Constants.DEBUG) Log.d(TAG, "checkActiveAccount(), activeAccount="+activeAccount);
        if ( activeAccount != null ) {
            accountLogin(activeAccount, true);
        } else {
            Account[] accounts = AccountUtils.getFilelugAccounts();
            showAccountSelectionDialog(accounts);
        }
    }

    // checkActiveAccount() ==>
    // showAccountSelectionDialog.callback.onSelection ==>
    // onActivityResult( requestCode == Constants.REQUEST_INITIAL ) ==>
    // ConfirmUploadActivity.onActivityResult( requestCode == Constants.REQUEST_CHANGE_ACCOUNT ) ==>
    // MainActivity.doLoginToOtherAccount() ==>
    // MainActivity.onActivityResult( requestCode == Constants.REQUEST_CHANGE_ACCOUNT ) ==>
    // OpenFromFilelugActivity.onActivityResult( requestCode == Constants.REQUEST_CHANGE_ACCOUNT ) ==>
    protected void accountLogin(final Account account, boolean forceRelogin) {
//        if (Constants.DEBUG) Log.d(TAG, "accountLogin(), account=" + account + ", forceRelogin=" + forceRelogin);
        AccountUtils.login(BaseActivity.this, account, forceRelogin, new AccountUtils.ResultCallback() {
            @Override
            public void result(Bundle res) {
                String errorMessage = res.getString(AccountManager.KEY_ERROR_MESSAGE, null);
                if ( TextUtils.isEmpty(errorMessage) ) {
                    String authToken = res.getString(AccountManager.KEY_AUTHTOKEN);
//                    if (Constants.DEBUG) Log.d(TAG, "accountLogin().result(), authToken=" + authToken);
                    connectToAccountDefaultComputer(account, authToken);
                } else {
                    MsgUtils.showToast(BaseActivity.this, errorMessage);
//                    if (Constants.DEBUG) Log.d(TAG, "accountLogin().result(), errorMessage=" + errorMessage);
                    noticeLoginOrConnectStatusChanged(Constants.MESSAGE_LOGIN_FAILED);
                }
            }
        });
    }

    // accountLogin ==>
    private void connectToAccountDefaultComputer(final Account account, String authToken) {
        AccountManager accountManager = AccountManager.get(BaseActivity.this);

        String computerIdStr = accountManager.getUserData(account, Constants.PARAM_COMPUTER_ID);
//        if (Constants.DEBUG) Log.d(TAG, "connectToAccountDefaultComputer(): account=" + account.name + ", authToken=" + authToken + ", computerId=" + computerIdStr);
        if ( TextUtils.isEmpty(computerIdStr) ) {
            beforeFindAvailableComputers(account);
            return;
        }

        AccountUtils.connectToComputer(BaseActivity.this, account, authToken, Integer.valueOf(computerIdStr).intValue(), new AccountUtils.ResultCallback() {
            @Override
            public void result(Bundle res) {
                String errorMessage = res.getString(AccountManager.KEY_ERROR_MESSAGE, null);
                int status = -1;
//                if (Constants.DEBUG) Log.d(TAG, "connectToAccountDefaultComputer().result(): errorMessage=" + errorMessage);
                if ( TextUtils.isEmpty(errorMessage) ) {
                    boolean socketConnected = res.getBoolean(Constants.PARAM_SOCKET_CONNECTED);
                    if ( socketConnected ) {
                        status = Constants.MESSAGE_LOGGED_IN_AND_SOCKET_CONNECTED;
                        errorMessage = String.format(getResources().getString(R.string.message_account_logged_in), account.name);
                    } else {
                        status = Constants.MESSAGE_LOGGED_IN_BUT_COMPUTER_NOT_CONNECTED;
                        errorMessage = String.format(getResources().getString(R.string.message_account_logged_in_but_computer_not_connected), account.name);
                    }
                } else {
                    status = Constants.MESSAGE_LOGGED_IN_BUT_CONNECTION_FAILED;
                }
//                if (Constants.DEBUG) Log.d(TAG, "connectToAccountDefaultComputer().result(): status=" + status + ", errorMessage=" + errorMessage);
                MsgUtils.showToast(BaseActivity.this, errorMessage);
                noticeLoginOrConnectStatusChanged(status);
            }
        });
    }

    // connectToAccountDefaultComputer() ==>
    private void beforeFindAvailableComputers(final Account account) {
        getAuthToken(new AccountUtils.AuthTokenCallback() {
            @Override
            public void onError(String errorMessage) {
//                if (Constants.DEBUG) Log.d(TAG, "beforeFindAvailableComputers().onError(): errorMessage=" + errorMessage);
                noticeLoginOrConnectStatusChanged(Constants.MESSAGE_FIND_AVAILABLE_COMPUTERS_GET_AUTH_TOKEN_ERROR);
                MsgUtils.showWarningMessage(BaseActivity.this, errorMessage);
            }
            @Override
            public void onSuccess(String authToken) {
                findAvailableComputers(account, authToken);
            }
        });
    }

    // beforeFindAvailableComputers() ==>
    private void findAvailableComputers(final Account account, String authToken) {
//        if (Constants.DEBUG) Log.d(TAG, "findAvailableComputers(): account=" + account + ", authToken=" + authToken);
        String locale = getResources().getConfiguration().locale.toString();
        RepositoryClient.getInstance().findAvailableComputers3(
            authToken,
            locale,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    List<ComputerObject> computerList = new ArrayList<ComputerObject>();
                    if (response != null && response.length() > 0) {
                        try {
                            for ( int i=0; i<response.length(); i++ ) {
                                JSONObject jso = response.getJSONObject(i);
                                String userComputerId = jso.getString(Constants.PARAM_USER_COMPUTER_ID);
                                if ( TextUtils.isEmpty(userComputerId) ) {
                                    continue;
                                }
                                String userId = jso.getString(Constants.PARAM_USER_ID);
                                int computerId = jso.getInt(Constants.PARAM_COMPUTER_ID);
                                String computerGroup = jso.getString(Constants.PARAM_COMPUTER_GROUP);
                                String computerName = jso.getString(Constants.PARAM_COMPUTER_NAME);
                                String computerAdminId = jso.getString(Constants.PARAM_COMPUTER_ADMIN_ID);
                                String lugServerId = jso.optString(Constants.PARAM_LUG_SERVER_ID, null);
                                ComputerObject computerObject = new ComputerObject(userComputerId, userId, computerId, computerGroup, computerName, computerAdminId);
                                computerList.add(computerObject);
                                TransferDBHelper.createOrUpdateUserComputer(userId, computerId, userComputerId, computerGroup, computerName, computerAdminId, lugServerId);
//                                if (Constants.DEBUG) Log.d(TAG, "findAvailableComputers().onResponse(): computerId=" + computerId + ", computerName=" + computerName);
                            }
                        } catch (JSONException e) {
                        }
                    }

                    int numberOfComputers = computerList.size();
                    if ( numberOfComputers == 0 ) {
                        String message = String.format(getResources().getString(R.string.message_account_logged_in_but_computer_not_set), account.name);
                        MsgUtils.showToast(BaseActivity.this, message);
                        noticeLoginOrConnectStatusChanged(Constants.MESSAGE_LOGGED_IN_BUT_COMPUTER_NOT_SET);
                    } else if ( numberOfComputers == 1 ) {
                        ComputerObject computerObject = computerList.get(0);
                        int computerId = computerObject.getComputerId();
                        String computerName = computerObject.getComputerName();

                        Account activeAccount = AccountUtils.getActiveAccount();
                        AccountManager accountManager = AccountManager.get(BaseActivity.this);
                        accountManager.setUserData(activeAccount, Constants.PARAM_COMPUTER_ID, Integer.toString(computerId));
                        accountManager.setUserData(activeAccount, Constants.PARAM_COMPUTER_NAME, computerName);
                        accountManager.setUserData(activeAccount, Constants.PARAM_SOCKET_CONNECTED, Boolean.FALSE.toString());

                        beforeDoComputerChanged(activeAccount, computerId, computerName);
                    } else {
                        showComputerSelectionDialog(account, computerList);
                    }
                }
            },
            new BaseResponseError(true, BaseActivity.this, BaseResponseError.MESSAGE_TYPE_ERROR_MESSAGE) {
                @Override
                protected void afterShowErrorMessage(VolleyError volleyError) {
//                    if (Constants.DEBUG) Log.d(TAG, "findAvailableComputers().afterShowErrorMessage()");
                    noticeLoginOrConnectStatusChanged(Constants.MESSAGE_LOGGED_IN_BUT_COMPUTER_LIST_NOT_FOUND);
                }
            }
        );
    }

    // checkActiveAccount ==>
    private void showAccountSelectionDialog(final Account[] accounts) {
        String[] accountNames = new String[accounts.length];
        for ( int i=0; i<accounts.length; i++ ) {
            Account account = accounts[i];
            accountNames[i] = account.name;
//            if (Constants.DEBUG) Log.d(TAG, "showAccountSelectionDialog(): account=" + account.name);
        }
        MaterialDialog.ListCallbackSingleChoice callback = new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                if (Constants.DEBUG) Log.d(TAG, "showAccountSelectionDialog().onSelection(): which=" + which + ", text=" + text);
                Account item = accounts[which];
                dialog.dismiss();
                PrefUtils.setActiveAccount(item.name);
                accountLogin(item, true);
                return true; // allow selection
            }
        };
        DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
//                if (Constants.DEBUG) Log.d(TAG, "showAccountSelectionDialog().onCancel()");
                noticeLoginOrConnectStatusChanged(Constants.MESSAGE_ACCOUNT_NOT_SET);
            }
        };
        DialogUtils.createSingleChoiceDialog(
            BaseActivity.this,
            R.string.label_choose_account,
            R.drawable.ic_human,
            accountNames,
            0,
            callback,
            cancelListener
        ).show();
    }

    // findAvailableComputers() --> findAvailableComputers3 --> Response.Listener.onResponse() ==>
    // showComputerSelectionDialog() --> ListCallbackSingleChoice.onSelection() ==>
    protected void beforeDoComputerChanged(final Account account, final int computerId, final String computerName) {
        getAuthToken(new AccountUtils.AuthTokenCallback() {
            @Override
            public void onError(String errorMessage) {
//                if (Constants.DEBUG) Log.d(TAG, "beforeDoComputerChanged().onError(): errorMessage=" + errorMessage);
                MsgUtils.showToast(BaseActivity.this, errorMessage);
                noticeLoginOrConnectStatusChanged(Constants.MESSAGE_CHANGE_COMPUTER_GET_AUTH_TOKEN_ERROR);
            }
            @Override
            public void onSuccess(String authToken) {
//                if (Constants.DEBUG) Log.d(TAG, "beforeDoComputerChanged().onSuccess(): authToken=" + authToken);
                doComputerChanged(account, authToken, computerId, computerName);
            }
        });
    }

    // beforeDoComputerChanged ==>
    private void doComputerChanged(final Account account, String authToken, final int computerId, final String computerName) {
//        if (Constants.DEBUG) Log.d(TAG, "connectToComputer(): account=" + account.name + ", authToken=" + authToken + ", computerId=" + computerId + ", computerName=" + computerName);
        AccountUtils.connectToComputer(BaseActivity.this, account, authToken, computerId, new AccountUtils.ResultCallback() {
            @Override
            public void result(Bundle res) {
                String errorMessage = res.getString(AccountManager.KEY_ERROR_MESSAGE, null);
                int status = -1;
//                if (Constants.DEBUG) Log.d(TAG, "connectToComputer(): errorMessage=" + errorMessage);
                if ( TextUtils.isEmpty(errorMessage) ) {
                    boolean socketConnected = res.getBoolean(Constants.PARAM_SOCKET_CONNECTED);
//                    if (Constants.DEBUG) Log.d(TAG, "connectToComputer(): socketConnected=" + socketConnected);
                    if ( socketConnected ) {
                        String message = String.format(getResources().getString(R.string.message_computer_has_been_connected), computerName);
                        MsgUtils.showToast(BaseActivity.this, message);
                        status = Constants.MESSAGE_COMPUTER_CHANGED_AND_SOCKET_CONNECTED;
                        noticeLoginOrConnectStatusChanged(status);
                        return;
                    } else {
                        String msg_prefix = String.format(getResources().getString(R.string.message_request_connection_failed_2), computerName);
                        errorMessage = msg_prefix + "\n\n" + getResources().getString(R.string.message_request_connection_failed_details);
                        status = Constants.MESSAGE_COMPUTER_CHANGED_BUT_NOT_CONNECTED;
                    }
                } else {
                    status = Constants.MESSAGE_COMPUTER_CHANGE_ERROR;
                }

                final int _status = status;
                MaterialDialog.SingleButtonCallback buttonCallback = new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        noticeLoginOrConnectStatusChanged(_status);
                    }
                };
                MsgUtils.showWarningMessage(BaseActivity.this, errorMessage, buttonCallback);
            }
        });
    }

    // findAvailableComputers() --> findAvailableComputers3 --> Response.Listener.onResponse() ==>
    private void showComputerSelectionDialog(final Account account, final List<ComputerObject> computerList) {
        String[] computerNames = new String[computerList.size()];
        for ( int i=0; i<computerList.size(); i++ ) {
            ComputerObject computerObject = computerList.get(i);
            computerNames[i] = computerObject.getComputerName();
//            if (Constants.DEBUG) Log.d(TAG, "showComputerSelectionDialog(): computerName=" + computerNames[i]);
        }
        MaterialDialog.ListCallbackSingleChoice callback = new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                dialog.dismiss();

                ComputerObject computerObject = computerList.get(which);
                int computerId = computerObject.getComputerId();
                String computerName = computerObject.getComputerName();

//                if (Constants.DEBUG) Log.d(TAG, "showComputerSelectionDialog().onSelection(): computerId=" + computerId + ", computerName=" + computerName);

                Account activeAccount = AccountUtils.getActiveAccount();
                AccountManager accountManager = AccountManager.get(BaseActivity.this);
                accountManager.setUserData(activeAccount, Constants.PARAM_COMPUTER_ID, Integer.toString(computerId));
                accountManager.setUserData(activeAccount, Constants.PARAM_COMPUTER_NAME, computerName);
                accountManager.setUserData(activeAccount, Constants.PARAM_SOCKET_CONNECTED, Boolean.FALSE.toString());

                beforeDoComputerChanged(activeAccount, computerId, computerName);

                return true; // allow selection
            }
        };
        DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
//                if (Constants.DEBUG) Log.d(TAG, "showComputerSelectionDialog().onCancel()");
                String message = String.format(getResources().getString(R.string.message_account_logged_in_but_computer_not_set), account.name);
                MsgUtils.showToast(BaseActivity.this, message);
                noticeLoginOrConnectStatusChanged(Constants.MESSAGE_LOGGED_IN_BUT_COMPUTER_NOT_SET);
            }
        };
        DialogUtils.createSingleChoiceDialog(
            BaseActivity.this,
            R.string.label_choose_computer_name,
            R.drawable.menu_ic_computer,
            computerNames,
            0,
            callback,
            cancelListener
        ).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (Constants.DEBUG) Log.d(TAG, "onActivityResult(): requestCode=" + requestCode + ", resultCode=" + resultCode);
        if ( resultCode == RESULT_OK ) {
            Bundle extras = data.getExtras();
            String message = null;
            if ( requestCode == Constants.REQUEST_INITIAL ) {
                if (extras != null) {
                    String accountName = extras.getString(Constants.PARAM_ACCOUNT);
//                    if (Constants.DEBUG) Log.d(TAG, "onActivityResult() --> REQUEST_INITIAL: accountName=" + accountName);
                    PrefUtils.setActiveAccount(accountName);
                    if (!TextUtils.isEmpty(accountName)) {
                        Account account = AccountUtils.getAccount(accountName);
                        accountLogin(account, false);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if ( requestCode == Constants.REQUEST_PERMISSION_ACCOUNTS_AND_STORAGE ) {
            if ( grantResults.length > 0 ) {
                boolean denied = false;
                for ( int result : grantResults ) {
                    if ( result == PackageManager.PERMISSION_DENIED ) {
                        denied = true;
                        break;
                    }
                }
//                if (Constants.DEBUG) Log.d(TAG, "onRequestPermissionsResult(): requestCode=" + requestCode + ", denied=" + denied);
                if ( denied ) {
                    alertAndExit();
                    return;
                }
            }
            initAccount();
        }
    }

    private void alertAndExit() {
        MsgUtils.showWarningMessage(
            this,
            R.string.message_filelug_permission_denied,
            new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    finish();
                }
            }
        );
    }

    @Override
    protected void onResume() {
//        if (Constants.DEBUG) Log.d(TAG, "onResume()");
        if ( mGCMRegistrationBroadcastReceiver != null ) {
            LocalBroadcastManager.getInstance(this).registerReceiver( mGCMRegistrationBroadcastReceiver,
                new IntentFilter(Constants.LOCAL_BROADCAST_REGISTRATION_COMPLETE) );
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (Constants.DEBUG) Log.d(TAG, "onPause()");
        if ( mGCMRegistrationBroadcastReceiver != null ) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mGCMRegistrationBroadcastReceiver);
        }
    }

}
