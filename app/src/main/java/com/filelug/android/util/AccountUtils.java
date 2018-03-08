package com.filelug.android.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.filelug.android.Constants;
import com.filelug.android.MainApplication;
import com.filelug.android.R;
import com.filelug.android.crepo.BaseResponseError;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.crepo.RepositoryErrorObject;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AccountUtils {

    private static final String TAG = AccountUtils.class.getSimpleName();

    private static Context mContext = MainApplication.getInstance().getApplicationContext();
    public static final String SEPARATOR_ACCOUNTS = ";";

    public static Account[] getFilelugAccounts() {
        AccountManager accountManager = AccountManager.get(mContext);
        return accountManager.getAccountsByType(Constants.ACCOUNT_TYPE_FILELUG);
    }

    public static void addAccount(Account account, String authTokenType, String token, Bundle userData) {
        AccountManager accountManager = AccountManager.get(mContext);
        accountManager.addAccountExplicitly(account, null, userData);
        accountManager.setAuthToken(account, authTokenType, token);
        resetDefaultPreference(account.name, userData, true);
    }

    private static void resetDefaultPreference(String accountName, Bundle userData, boolean saveToRepo) {
        String upload_directory = userData.getString(Constants.PARAM_UPLOAD_DIRECTORY, null);
        String upload_subdirectory_type = userData.getString(Constants.PARAM_UPLOAD_SUB_DIRECTORY_TYPE, null);
        String upload_subdirectory_value = userData.getString(Constants.PARAM_UPLOAD_SUB_DIRECTORY_VALUE, null);
        String upload_description_type = userData.getString(Constants.PARAM_UPLOAD_DESCRIPTION_TYPE, null);
        String upload_description_value = userData.getString(Constants.PARAM_UPLOAD_DESCRIPTION_VALUE, null);
        String upload_notification_type = userData.getString(Constants.PARAM_UPLOAD_NOTIFICATION_TYPE, null);
        String download_directory = userData.getString(Constants.PARAM_DOWNLOAD_DIRECTORY, null);
        String download_subdirectory_type = userData.getString(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_TYPE, null);
        String download_subdirectory_value = userData.getString(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_VALUE, null);
        String download_description_type = userData.getString(Constants.PARAM_DOWNLOAD_DESCRIPTION_TYPE, null);
        String download_description_value = userData.getString(Constants.PARAM_DOWNLOAD_DESCRIPTION_VALUE, null);
        String download_notification_type = userData.getString(Constants.PARAM_DOWNLOAD_NOTIFICATION_TYPE, null);

        // Check device directory
        if ( !TextUtils.isEmpty(download_directory) ) {
            if ( !LocalFileUtils.isAvailableDir(new File(download_directory)) ) {
                download_directory = null;
            }
        }

        String _uploadSubdirectoryType = TextUtils.isEmpty(upload_subdirectory_type) ? String.valueOf(PrefUtils.DEFAULT_VALUE_UPLOAD_SUB_DIR) : upload_subdirectory_type;
        String _uploadDescriptionType = TextUtils.isEmpty(upload_description_type) ? String.valueOf(PrefUtils.DEFAULT_VALUE_UPLOAD_DESCRIPTION_TYPE) : upload_description_type;
        String _uploadNotificationType = TextUtils.isEmpty(upload_notification_type) ? String.valueOf(PrefUtils.DEFAULT_VALUE_UPLOAD_NOTIFICATION_TYPE) : upload_notification_type;
        String _downloadSubdirectoryType = TextUtils.isEmpty(download_subdirectory_type) ? String.valueOf(PrefUtils.DEFAULT_VALUE_DOWNLOAD_SUB_DIR) : download_subdirectory_type;
        String _downloadDescriptionType = TextUtils.isEmpty(download_description_type) ? String.valueOf(PrefUtils.DEFAULT_VALUE_DOWNLOAD_DESCRIPTION_TYPE) : download_description_type;
        String _downloadNotificationType = TextUtils.isEmpty(download_notification_type) ? String.valueOf(PrefUtils.DEFAULT_VALUE_DOWNLOAD_NOTIFICATION_TYPE) : download_notification_type;

        PrefUtils.setUploadPath(accountName, upload_directory);
        PrefUtils.setUploadSubdirType(accountName, _uploadSubdirectoryType);
        PrefUtils.setUploadSubdirValue(accountName, upload_subdirectory_value);
        PrefUtils.setUploadDescriptionType(accountName, _uploadDescriptionType);
        PrefUtils.setUploadDescriptionValue(accountName, upload_description_value);
        PrefUtils.setUploadNotificationType(accountName, _uploadNotificationType);
        PrefUtils.setDownloadPath(accountName, download_directory);
        PrefUtils.setDownloadSubdirValue(accountName, download_subdirectory_value);
        PrefUtils.setDownloadDescriptionValue(accountName, download_description_value);
        PrefUtils.setDownloadSubdirType(accountName, _downloadSubdirectoryType);
        PrefUtils.setDownloadDescriptionType(accountName, _downloadDescriptionType);
        PrefUtils.setDownloadNotificationType(accountName, _downloadNotificationType);

        PrefUtils.setUploadPath(null, upload_directory);
        PrefUtils.setUploadSubdirType(null, _uploadSubdirectoryType);
        PrefUtils.setUploadSubdirValue(null, upload_subdirectory_value);
        PrefUtils.setUploadDescriptionType(null, _uploadDescriptionType);
        PrefUtils.setUploadDescriptionValue(null, upload_description_value);
        PrefUtils.setUploadNotificationType(null, _uploadNotificationType);
        PrefUtils.setDownloadPath(null, download_directory);
        PrefUtils.setDownloadSubdirValue(null, download_subdirectory_value);
        PrefUtils.setDownloadDescriptionValue(null, download_description_value);
        PrefUtils.setDownloadSubdirType(null, _downloadSubdirectoryType);
        PrefUtils.setDownloadDescriptionType(null, _downloadDescriptionType);
        PrefUtils.setDownloadNotificationType(null, _downloadNotificationType);
    }

    public interface ResultCallback {
        void result(Bundle result);
    };

    public interface AuthTokenCallback {
        void onError(String errorMessage);
        void onSuccess(String authToken);
    };

    public static void login(final Activity activity, final Account account, boolean forceRelogin, final ResultCallback loginCallback) {
//		Log.d(TAG, "login(): account=" + account + ", forceRelogin=" + forceRelogin);

        final AccountManager accountManager = AccountManager.get(activity);
        final String nickname = accountManager.getUserData(account, Constants.PARAM_NICKNAME);

        AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {
            public void run(final AccountManagerFuture<Bundle> future) {
                Bundle res = new Bundle();
                res.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                res.putString(Constants.PARAM_NICKNAME, nickname);

                boolean loggedIn = false;
                String authToken = null;
                String message = null;
                try {
                    Bundle loginResult = future.getResult();
                    authToken = loginResult.getString(AccountManager.KEY_AUTHTOKEN);
                    if ( authToken != null ) {
//                        Log.d(TAG, "login().run(): authToken=" + authToken);
                        if ( PrefUtils.getGCMTokenChanged() && !TextUtils.isEmpty(PrefUtils.getPushServiceToken()) ) {
                            PrefUtils.setGCMTokenChanged(false);
                        }
                        loggedIn = true;
                        res.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                    } else {
                        message = loginResult.getString(AccountManager.KEY_ERROR_MESSAGE);
                        int errorCode = loginResult.getInt(AccountManager.KEY_ERROR_CODE);
//                        Log.d(TAG, "login().run(): errorCode=" + errorCode + ", message=" + message);
                        res.putString(AccountManager.KEY_ERROR_MESSAGE, message);
                        res.putInt(AccountManager.KEY_ERROR_CODE, errorCode);
                    }
                } catch ( Exception e ) {
                    message = activity.getResources().getString(R.string.message_sign_in_failed);
                    String errorMessage =  e.getMessage();
                    if ( !TextUtils.isEmpty(errorMessage) ) {
                        message += "\n" + errorMessage;
                    }
//                    Log.d(TAG, "login().run(): errorMessage=" + errorMessage);
                    res.putString(AccountManager.KEY_ERROR_MESSAGE, message);
                }

//                Log.d(TAG, "login(), loggedIn=" + loggedIn);

                accountManager.setUserData(account, Constants.EXT_PARAM_LOGGED_IN, Boolean.toString(loggedIn));

                if ( loginCallback != null ) {
                    loginCallback.result(res);
                }
            }
        };

        Bundle options = null;
        if ( forceRelogin ) {
            String oldSessionId = accountManager.getUserData(account, Constants.PARAM_SESSION_ID);
            options = new Bundle();
            options.putBoolean(Constants.EXT_PARAM_FORCE_RELOGIN, true);
            accountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE_FILELUG, oldSessionId);
        }
        accountManager.getAuthToken(account, Constants.AUTH_TOKEN_TYPE_GENERAL_SERVICE, options, activity, callback, null);
    }

    public static void connectToComputer(Activity activity, final Account account, final String authToken, final int computerId, final ResultCallback connectCallback) {

        AccountUtils.setSocketConnected(account, false);

        boolean showHidden = true;
        String deviceToken = PrefUtils.getPushServiceToken();
        String notificationType = PrefUtils.getPushServiceTypeString();
        String deviceVersion = String.valueOf(Build.VERSION.SDK_INT);
        String filelugVersion = MiscUtils.getFilelugVersion(activity);
        String locale = activity.getResources().getConfiguration().locale.toString();

        RepositoryClient.getInstance().connectToComputer(
            authToken,
            computerId,
            showHidden,
            deviceToken,
            notificationType,
            deviceVersion,
            filelugVersion,
            filelugVersion,
            locale,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    Log.d(TAG, "connectToComputer --> onResponse(): response=" + response.toString());

                    boolean socketConnected = response.optBoolean(Constants.PARAM_SOCKET_CONNECTED, false);
                    String lugServerId = response.optString(Constants.PARAM_LUG_SERVER_ID, null);
                    String fileSeparator = response.optString(Constants.PARAM_FILE_SEPARATOR, null);
                    String lineSeparator = response.optString(Constants.PARAM_LINE_SEPARATOR, null);
                    String computerName = response.optString(Constants.PARAM_COMPUTER_NAME, null);
                    String computerGroup = response.optString(Constants.PARAM_COMPUTER_GROUP, null);
                    String computerAdminId = response.optString(Constants.PARAM_COMPUTER_ADMIN_ID, null);
                    String userComputerId = response.optString(Constants.PARAM_USER_COMPUTER_ID, null);
                    String uploadDirectory = response.optString(Constants.PARAM_UPLOAD_DIRECTORY, null);
                    String uploadSubdirType = response.optString(Constants.PARAM_UPLOAD_SUB_DIRECTORY_TYPE, null);
                    String uploadSubdirValue = response.optString(Constants.PARAM_UPLOAD_SUB_DIRECTORY_VALUE, null);
                    String uploadDescriptionType = response.optString(Constants.PARAM_UPLOAD_DESCRIPTION_TYPE, null);
                    String uploadDescriptionValue = response.optString(Constants.PARAM_UPLOAD_DESCRIPTION_VALUE, null);
                    String uploadNotificationType = response.optString(Constants.PARAM_UPLOAD_NOTIFICATION_TYPE, null);
                    String downloadDirectory = response.optString(Constants.PARAM_DOWNLOAD_DIRECTORY, null);
                    String downloadSubdirType = response.optString(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_TYPE, null);
                    String downloadSubdirValue = response.optString(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_VALUE, null);
                    String downloadDescriptionType = response.optString(Constants.PARAM_DOWNLOAD_DESCRIPTION_TYPE, null);
                    String downloadDescriptionValue = response.optString(Constants.PARAM_DOWNLOAD_DESCRIPTION_VALUE, null);
                    String downloadNotificationType = response.optString(Constants.PARAM_DOWNLOAD_NOTIFICATION_TYPE, null);

                    long accessTime = System.currentTimeMillis();

                    if ( PrefUtils.getGCMTokenChanged() && !TextUtils.isEmpty(PrefUtils.getPushServiceToken()) ) {
                        PrefUtils.setGCMTokenChanged(false);
                    }

                    Bundle userData = new Bundle();
                    userData.putString(Constants.PARAM_SOCKET_CONNECTED, Boolean.toString(socketConnected));
                    userData.putString(Constants.EXT_PARAM_ACCESS_TIME, Long.toString(accessTime));
                    userData.putString(Constants.PARAM_LUG_SERVER_ID, lugServerId);
                    userData.putString(Constants.PARAM_UPLOAD_DIRECTORY, uploadDirectory);
                    userData.putString(Constants.PARAM_UPLOAD_SUB_DIRECTORY_TYPE, uploadSubdirType);
                    userData.putString(Constants.PARAM_UPLOAD_SUB_DIRECTORY_VALUE, uploadSubdirValue);
                    userData.putString(Constants.PARAM_UPLOAD_DESCRIPTION_TYPE, uploadDescriptionType);
                    userData.putString(Constants.PARAM_UPLOAD_DESCRIPTION_VALUE, uploadDescriptionValue);
                    userData.putString(Constants.PARAM_UPLOAD_NOTIFICATION_TYPE, uploadNotificationType);
                    userData.putString(Constants.PARAM_DOWNLOAD_DIRECTORY, downloadDirectory);
                    userData.putString(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_TYPE, downloadSubdirType);
                    userData.putString(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_VALUE, downloadSubdirValue);
                    userData.putString(Constants.PARAM_DOWNLOAD_DESCRIPTION_TYPE, downloadDescriptionType);
                    userData.putString(Constants.PARAM_DOWNLOAD_DESCRIPTION_VALUE, downloadDescriptionValue);
                    userData.putString(Constants.PARAM_DOWNLOAD_NOTIFICATION_TYPE, downloadNotificationType);
                    userData.putString(Constants.PARAM_FILE_SEPARATOR, fileSeparator);
                    userData.putString(Constants.PARAM_LINE_SEPARATOR, lineSeparator);
                    userData.putString(Constants.PARAM_COMPUTER_ID, String.valueOf(computerId));
                    userData.putString(Constants.PARAM_COMPUTER_NAME, computerName);
                    if ( TextUtils.isEmpty(computerGroup) ) {
                        userData.putString(Constants.PARAM_COMPUTER_GROUP, null);
                    } else {
                        userData.putString(Constants.PARAM_COMPUTER_GROUP, computerGroup);
                    }
                    if ( TextUtils.isEmpty(computerAdminId) ) {
                        userData.putString(Constants.PARAM_COMPUTER_ADMIN_ID, null);
                    } else {
                        userData.putString(Constants.PARAM_COMPUTER_ADMIN_ID, computerAdminId);
                    }
                    if ( TextUtils.isEmpty(userComputerId) ) {
                        userData.putString(Constants.PARAM_USER_COMPUTER_ID, null);
                    } else {
                        userData.putString(Constants.PARAM_USER_COMPUTER_ID, userComputerId);
                    }

                    resetUserData2(account, userData);

                    if ( connectCallback != null ) {
                        Bundle res = new Bundle();
                        res.putBoolean(Constants.PARAM_SOCKET_CONNECTED, socketConnected);
                        res.putString(Constants.PARAM_LUG_SERVER_ID, lugServerId);
                        res.putInt(Constants.PARAM_COMPUTER_ID, computerId);
                        res.putString(Constants.PARAM_COMPUTER_NAME, computerName);
                        connectCallback.result(res);
                    }

                }
            },
            new BaseResponseError(true, activity) {
                @Override
                protected void afterShowErrorMessage(VolleyError volleyError) {
                    super.afterShowErrorMessage(volleyError);
                    int statusCode = MiscUtils.getStatusCode(volleyError);
                    String message = getMessage(volleyError);
                    if ( connectCallback != null ) {
                        Bundle res = new Bundle();
                        res.putInt(AccountManager.KEY_ERROR_CODE, statusCode);
                        res.putString(AccountManager.KEY_ERROR_MESSAGE, message);
                        connectCallback.result(res);
                    }
                }
            }
        );

    }

    public static Bundle connectToComputer2(Context context, final Account account, final int computerId) {

        Bundle result = new Bundle();

        if ( !NetworkUtils.isNetworkAvailable(context, null) ) {
            String errorMsg = context.getResources().getString(R.string.message_network_error);
            result.putInt(AccountManager.KEY_ERROR_CODE, -1);
            result.putString(AccountManager.KEY_ERROR_MESSAGE, errorMsg);
            return result;
        }

        AccountManager accountManager = AccountManager.get(mContext);
        String authToken = null;
        String errorMessage = null;
        try {
            authToken = AccountUtils.getAuthToken3(context, account);
//            if ( Constants.DEBUG ) Log.d(TAG, "connectToComputer2(), authToken=" + authToken);
        } catch (AuthFailureError afe) {
            errorMessage = afe.getMessage();
//            if ( Constants.DEBUG ) Log.d(TAG, "connectToComputer2(), errorMessage=" + errorMessage);
        }

        if ( !TextUtils.isEmpty(errorMessage) ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "connectToComputer2(), Get authToken error!\n" + errorMessage);
            AccountUtils.setSocketConnected(account, false);
            result.putInt(AccountManager.KEY_ERROR_CODE, -1);
            result.putString(AccountManager.KEY_ERROR_MESSAGE, errorMessage);
            return result;
        }

        String _computerIdStr = accountManager.getUserData(account, Constants.PARAM_COMPUTER_ID);
        String _computerName = accountManager.getUserData(account, Constants.PARAM_COMPUTER_NAME);
        int _accountComputerId = TextUtils.isEmpty(_computerIdStr) ? -1 : Integer.valueOf(_computerIdStr);
        String _lugServerId = accountManager.getUserData(account, Constants.PARAM_LUG_SERVER_ID);
        boolean _socketConnected = AccountUtils.isSocketConnected(account);

        if ( computerId == _accountComputerId && _socketConnected && !TextUtils.isEmpty(_lugServerId) ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "connectToComputer2(), Logged in, authToken=" + authToken + ", _socketConnected=" + _socketConnected + ", _lugServerId=" + _lugServerId + ", _accountComputerId=" + _accountComputerId + ", _computerName=" + _computerName);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            result.putBoolean(Constants.PARAM_SOCKET_CONNECTED, _socketConnected);
            result.putString(Constants.PARAM_LUG_SERVER_ID, _lugServerId);
            result.putInt(Constants.PARAM_COMPUTER_ID, _accountComputerId);
            result.putString(Constants.PARAM_COMPUTER_NAME, _computerName);
            return result;
        }

        boolean showHidden = PrefUtils.isShowHiddenFiles();
        String deviceToken = PrefUtils.getPushServiceToken();
        String notificationType = PrefUtils.getPushServiceTypeString();
        String deviceVersion = String.valueOf(Build.VERSION.SDK_INT);
        String filelugVersion = MiscUtils.getFilelugVersion(context);
        String locale = context.getResources().getConfiguration().locale.toString();

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        int timeOut = context.getResources().getInteger(R.integer.sync_timeout_a);
        RepositoryClient.getInstance().connectToComputer(authToken, computerId, showHidden, deviceToken, notificationType, deviceVersion, filelugVersion, filelugVersion, locale, future, future);
        JSONObject response = null;
        RepositoryErrorObject errorObject = null;

        try {
            response = future.get(timeOut, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            errorObject = MiscUtils.getErrorObject(context, e, account);
        }

        if ( errorObject != null ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "connectToComputer2(), Connect to computer error!\n" + errorObject.getMessage());
            result.putInt(AccountManager.KEY_ERROR_CODE, errorObject.getCode());
            result.putString(AccountManager.KEY_ERROR_MESSAGE, errorObject.getMessage());
            return result;
        }

        boolean socketConnected = response.optBoolean(Constants.PARAM_SOCKET_CONNECTED, false);
        String lugServerId = response.optString(Constants.PARAM_LUG_SERVER_ID, null);
        String fileSeparator = response.optString(Constants.PARAM_FILE_SEPARATOR, null);
        String lineSeparator = response.optString(Constants.PARAM_LINE_SEPARATOR, null);
        String computerName = response.optString(Constants.PARAM_COMPUTER_NAME, null);
        String computerGroup = response.optString(Constants.PARAM_COMPUTER_GROUP, null);
        String computerAdminId = response.optString(Constants.PARAM_COMPUTER_ADMIN_ID, null);
        String userComputerId = response.optString(Constants.PARAM_USER_COMPUTER_ID, null);
        String uploadDirectory = response.optString(Constants.PARAM_UPLOAD_DIRECTORY, null);
        String uploadSubdirType = response.optString(Constants.PARAM_UPLOAD_SUB_DIRECTORY_TYPE, null);
        String uploadSubdirValue = response.optString(Constants.PARAM_UPLOAD_SUB_DIRECTORY_VALUE, null);
        String uploadDescriptionType = response.optString(Constants.PARAM_UPLOAD_DESCRIPTION_TYPE, null);
        String uploadDescriptionValue = response.optString(Constants.PARAM_UPLOAD_DESCRIPTION_VALUE, null);
        String uploadNotificationType = response.optString(Constants.PARAM_UPLOAD_NOTIFICATION_TYPE, null);
        String downloadDirectory = response.optString(Constants.PARAM_DOWNLOAD_DIRECTORY, null);
        String downloadSubdirType = response.optString(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_TYPE, null);
        String downloadSubdirValue = response.optString(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_VALUE, null);
        String downloadDescriptionType = response.optString(Constants.PARAM_DOWNLOAD_DESCRIPTION_TYPE, null);
        String downloadDescriptionValue = response.optString(Constants.PARAM_DOWNLOAD_DESCRIPTION_VALUE, null);
        String downloadNotificationType = response.optString(Constants.PARAM_DOWNLOAD_NOTIFICATION_TYPE, null);

        long accessTime = System.currentTimeMillis();

        if ( PrefUtils.getGCMTokenChanged() && !TextUtils.isEmpty(PrefUtils.getPushServiceToken()) ) {
            PrefUtils.setGCMTokenChanged(false);
        }

        Bundle userData = new Bundle();
        userData.putString(Constants.PARAM_SOCKET_CONNECTED, Boolean.toString(socketConnected));
        userData.putString(Constants.EXT_PARAM_ACCESS_TIME, Long.toString(accessTime));
        userData.putString(Constants.PARAM_LUG_SERVER_ID, lugServerId);
        userData.putString(Constants.PARAM_UPLOAD_DIRECTORY, uploadDirectory);
        userData.putString(Constants.PARAM_UPLOAD_SUB_DIRECTORY_TYPE, uploadSubdirType);
        userData.putString(Constants.PARAM_UPLOAD_SUB_DIRECTORY_VALUE, uploadSubdirValue);
        userData.putString(Constants.PARAM_UPLOAD_DESCRIPTION_TYPE, uploadDescriptionType);
        userData.putString(Constants.PARAM_UPLOAD_DESCRIPTION_VALUE, uploadDescriptionValue);
        userData.putString(Constants.PARAM_UPLOAD_NOTIFICATION_TYPE, uploadNotificationType);
        userData.putString(Constants.PARAM_DOWNLOAD_DIRECTORY, downloadDirectory);
        userData.putString(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_TYPE, downloadSubdirType);
        userData.putString(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_VALUE, downloadSubdirValue);
        userData.putString(Constants.PARAM_DOWNLOAD_DESCRIPTION_TYPE, downloadDescriptionType);
        userData.putString(Constants.PARAM_DOWNLOAD_DESCRIPTION_VALUE, downloadDescriptionValue);
        userData.putString(Constants.PARAM_DOWNLOAD_NOTIFICATION_TYPE, downloadNotificationType);
        userData.putString(Constants.PARAM_FILE_SEPARATOR, fileSeparator);
        userData.putString(Constants.PARAM_LINE_SEPARATOR, lineSeparator);
        userData.putString(Constants.PARAM_COMPUTER_ID, String.valueOf(computerId));
        userData.putString(Constants.PARAM_COMPUTER_NAME, computerName);
        if ( TextUtils.isEmpty(computerGroup) ) {
            userData.putString(Constants.PARAM_COMPUTER_GROUP, null);
        } else {
            userData.putString(Constants.PARAM_COMPUTER_GROUP, computerGroup);
        }
        if ( TextUtils.isEmpty(computerAdminId) ) {
            userData.putString(Constants.PARAM_COMPUTER_ADMIN_ID, null);
        } else {
            userData.putString(Constants.PARAM_COMPUTER_ADMIN_ID, computerAdminId);
        }
        if ( TextUtils.isEmpty(userComputerId) ) {
            userData.putString(Constants.PARAM_USER_COMPUTER_ID, null);
        } else {
            userData.putString(Constants.PARAM_USER_COMPUTER_ID, userComputerId);
        }

        resetUserData2(account, userData);

//        if ( Constants.DEBUG ) Log.d(TAG, "connectToComputer2(), Connected! authToken=" + authToken + ", socketConnected=" + socketConnected + ", lugServerId=" + lugServerId + ", computerId=" + computerId + ", computerName=" + computerName);

        result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        result.putBoolean(Constants.PARAM_SOCKET_CONNECTED, socketConnected);
        result.putString(Constants.PARAM_LUG_SERVER_ID, lugServerId);
        result.putInt(Constants.PARAM_COMPUTER_ID, computerId);
        result.putString(Constants.PARAM_COMPUTER_NAME, computerName);
        return result;
    }

    public static void resetToken(Account account, String authTokenType, String token) {
        AccountManager accountManager = AccountManager.get(mContext);
        accountManager.setUserData(account, Constants.PARAM_SESSION_ID, token);
        accountManager.setAuthToken(account, authTokenType, token);
    }

    public static void removeAuthToken(Account account) {
        AccountManager accountManager = AccountManager.get(mContext);
        String authToken = accountManager.getUserData(account, Constants.PARAM_SESSION_ID);
        accountManager.invalidateAuthToken(account.type, authToken);
    }

    public static void resetUserData(Account account, Bundle userData) {
        AccountManager accountManager = AccountManager.get(mContext);
        for ( String key : userData.keySet() ) {
            String value = userData.getString(key);
            accountManager.setUserData(account, key, value);
        }
    }

    public static void resetUserData2(Account account, Bundle userData) {
        resetUserData(account, userData);
        resetDefaultPreference(account.name, userData, false);
    }

   	public static boolean accountExist(Context context, String accountName) {
		return getAccount(accountName) != null;
	}

    public static Account getAccount(String accountName) {
        AccountManager accountManager = AccountManager.get(mContext);
        Account retAccount = null;
        Account[] accounts = getFilelugAccounts();
        if ( accounts != null && accounts.length > 0 ) {
            for ( Account account : accounts ) {
                if ( accountName.equals(account.name) ) {
                    retAccount = account;
                    break;
                }
            }
        }
        return retAccount;
    }

    public static Account getActiveAccount() {
        AccountManager accountManager = AccountManager.get(mContext);
        String activeAccountName = PrefUtils.getActiveAccount();
        if ( TextUtils.isEmpty(activeAccountName) ) {
            return null;
        }
        Account retAccount = null;
        Account[] accounts = getFilelugAccounts();
        if ( accounts != null && accounts.length > 0 ) {
            for ( Account account : accounts ) {
                if ( activeAccountName.equals(account.name) ) {
                    retAccount = account;
                    break;
                }
            }
        }
        return retAccount;
    }

    public static void setActiveAccountAccessTime() {
		Account activeAccount = getActiveAccount();
		if ( activeAccount == null ) return;
		setAccountAccessTime(activeAccount, System.currentTimeMillis());
	}

    public static void setAccountAccessTime(Account account, long accessTime) {
        if ( account == null ) return;
        AccountManager accountManager = AccountManager.get(mContext);
        accountManager.setUserData(account, Constants.EXT_PARAM_ACCESS_TIME, Long.toString(accessTime));
    }

    public static boolean authTokenExpired(Account account) {
        AccountManager accountManager = AccountManager.get(mContext);
        String last_access_time = accountManager.getUserData(account, Constants.EXT_PARAM_ACCESS_TIME);
        return authTokenExpired(last_access_time, System.currentTimeMillis());
    }

    public static boolean authTokenExpired(String lastAccessTime, long currentTime) {
        if ( TextUtils.isEmpty(lastAccessTime) || ( Long.parseLong(lastAccessTime) + 1000 * 60 * 57 ) < currentTime ) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isLoggedIn(Account account) {
        AccountManager accountManager = AccountManager.get(mContext);
        String tmp = accountManager.getUserData(account, Constants.EXT_PARAM_LOGGED_IN);
        boolean loggedIn = tmp == null ? false : Boolean.valueOf(tmp);
        return loggedIn;
    }

    public static void setLoggedIn(Account account, boolean loggedIn) {
        AccountManager accountManager = AccountManager.get(mContext);
        accountManager.setUserData(account, Constants.EXT_PARAM_LOGGED_IN, Boolean.toString(loggedIn));
    }

    public static boolean isSocketConnected(Account account) {
        AccountManager accountManager = AccountManager.get(mContext);
        String tmp = accountManager.getUserData(account, Constants.PARAM_SOCKET_CONNECTED);
        boolean socketConnected = tmp == null ? false : Boolean.valueOf(tmp);
        return socketConnected;
    }

    public static void setSocketConnected(Account account, boolean socketConnected) {
        AccountManager accountManager = AccountManager.get(mContext);
        accountManager.setUserData(account, Constants.PARAM_SOCKET_CONNECTED, Boolean.toString(socketConnected));
    }

    public static boolean isEmailVerified(Account account) {
        AccountManager accountManager = AccountManager.get(mContext);
        String tmp = accountManager.getUserData(account, Constants.PARAM_EMAIL_IS_VERIFIED);
        boolean emailVerified = tmp == null ? false : Boolean.valueOf(tmp);
        return emailVerified;
    }

    public static void setEmailVerified(Account account, boolean emailVerified) {
        AccountManager accountManager = AccountManager.get(mContext);
        accountManager.setUserData(account, Constants.PARAM_EMAIL_IS_VERIFIED, Boolean.toString(emailVerified));
    }

    public static void noticeDesktopConnectionChanged(int status) {
//        if (Constants.DEBUG) Log.d(TAG, "noticeDesktopConnectionChanged()");
        Intent desktopConnStatus = new Intent(Constants.LOCAL_BROADCAST_DESKTOP_CONNECTION_STATUS);
        desktopConnStatus.putExtra(Constants.PARAM_STATUS, status);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(desktopConnStatus);
    }

    public static boolean getAuthToken(final Activity activity, final AuthTokenCallback callback) {
        Account activeAccount = getActiveAccount();
//        if ( activeAccount == null || isLoggedIn(activeAccount) ) {
        if ( activeAccount == null ) {
            return false;
        }
        return getAuthToken(activity, activeAccount, callback);
    }

    public static boolean getAuthToken2(final Context context, final AuthTokenCallback callback) {
        if ( context == null || callback == null ) {
            return false;
        }
        Account activeAccount = AccountUtils.getActiveAccount();
        return getAuthToken(context, activeAccount, callback);
    }

    public static boolean getAuthToken(final Context context, final Account account, final AuthTokenCallback callback) {
        if ( context == null || callback == null ) {
            return false;
        }

        if ( account == null ) {
            return false;
        }

        final AccountManager accountManager = AccountManager.get(context);

        AccountManagerCallback<Bundle> myCallback = new AccountManagerCallback<Bundle>() {
            public void run( final AccountManagerFuture<Bundle> future ) {
                String authToken = null;
                String message = null;
                try {
                    Bundle result = future.getResult();
                    authToken = result.getString(AccountManager.KEY_AUTHTOKEN);
                    if ( authToken == null ) {
                        //int errorCode = result.getInt(AccountManager.KEY_ERROR_CODE);
                        message = result.getString(AccountManager.KEY_ERROR_MESSAGE);
                    }
                } catch ( Exception e ) {
                    message = context.getResources().getString(R.string.message_sign_in_failed);
                    String errorMessage =  e.getMessage();
                    if ( !TextUtils.isEmpty(errorMessage) ) {
                        message += "\n" + errorMessage;
                    }
                }
                if ( message != null ) {
                    callback.onError(message);
                } else {
                    callback.onSuccess(authToken);
                }
            }
        };
        if ( context instanceof Activity ) {
            accountManager.getAuthToken(account, Constants.AUTH_TOKEN_TYPE_GENERAL_SERVICE, null, (Activity)context, myCallback, null);
        } else {
            accountManager.getAuthToken(account, Constants.AUTH_TOKEN_TYPE_GENERAL_SERVICE, null, true, myCallback, null);
        }

        return true;
    }

    public static String getAuthToken3(Context context, Account account) throws AuthFailureError {
        AccountManager accountManager = AccountManager.get(context);
        boolean loggedIn = AccountUtils.isLoggedIn(account);
        boolean tokenExpired = AccountUtils.authTokenExpired(account);

//        if (Constants.DEBUG) Log.d(TAG, "getAuthToken3(): account=" + account.toString() + ", loggedIn=" + loggedIn + ", tokenExpired=" + tokenExpired);

        Bundle options = null;
        if ( !loggedIn || tokenExpired ) {
            options = new Bundle();
            options.putBoolean(Constants.EXT_PARAM_FORCE_RELOGIN, true);
        }

        AccountManagerFuture<Bundle> future = accountManager.getAuthToken(account, Constants.AUTH_TOKEN_TYPE_GENERAL_SERVICE, options, true, null, null);
        Bundle result;
        try {
            result = future.getResult();
        } catch (Exception e) {
            throw new AuthFailureError("Error while retrieving auth token", e);
        }

        String newAuthToken = null;
        if ( future.isDone() && !future.isCancelled() ) {
            if ( result.containsKey(AccountManager.KEY_INTENT) ) {
                Intent intent = result.getParcelable(AccountManager.KEY_INTENT);
                throw new AuthFailureError(intent);
            } else if ( result.containsKey(AccountManager.KEY_ERROR_MESSAGE) ) {
                String errorMessage = result.getString(AccountManager.KEY_ERROR_MESSAGE);
                throw new AuthFailureError(errorMessage);
            }
            newAuthToken = result.getString(AccountManager.KEY_AUTHTOKEN);
        }
        if ( newAuthToken == null ) {
            throw new AuthFailureError("Got null auth token for type: " + Constants.AUTH_TOKEN_TYPE_GENERAL_SERVICE);
        }

        return newAuthToken;
    }

    public static void removeAccount(Activity activity, Account account, AccountManagerCallback callback) {
        if (account == null) {
            return;
        }
        FileCache.cleanActiveAccountCache(account.name);
        AccountManager accountManager = AccountManager.get(activity);
        if ( Build.VERSION.SDK_INT >= 22 ) {
            accountManager.removeAccount(account, activity, callback, null);
        } else {
            accountManager.removeAccount(account, callback, null);
        }
    }

    public static String[] getUserSessions(Context context) {
        String[] sessionArray = null;
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = getFilelugAccounts();
        if ( accounts != null && accounts.length > 0 ) {
            sessionArray = new String[accounts.length];
            for ( int i=0; i<accounts.length; i++ ) {
                Account account = accounts[i];
                String userSession = accountManager.getUserData(account, Constants.EXT_PARAM_FILELUG_ACCOUNT);
                sessionArray[i] = userSession;
            }
        }
        return sessionArray;
    }

    public static String getAccountsString() {
        String accountsStr = "";

        AccountManager accountManager = AccountManager.get(mContext);
        Account[] accounts = getFilelugAccounts();

        // Set accounts
        if ( accounts != null && accounts.length > 0 ) {
            for ( int i=0; i<accounts.length; i++ ) {
                Account account = accounts[i];
                accountsStr += ( i==0 ? "" : SEPARATOR_ACCOUNTS) + account.name;
            }
//			if ( Constants.DEBUG ) Log.d(TAG, "getAccountsString(), Accounts string: " + accountsStr);
            PrefUtils.setAccounts(accountsStr);
        }

        return accountsStr;
    }

    public static Account getAccountByUserId(String userId) {
//        if ( Constants.DEBUG ) Log.d(TAG, "getAccountByUserId(), userId=" + userId);
        Account result = null;
        if ( TextUtils.isEmpty(userId) ) {
            return result;
        }
        AccountManager accountManager = AccountManager.get(mContext);
        Account[] accounts = getFilelugAccounts();
        if ( accounts != null && accounts.length > 0 ) {
            for ( Account account : accounts ) {
                String filelugAccount = accountManager.getUserData(account, Constants.EXT_PARAM_FILELUG_ACCOUNT);
//                if ( Constants.DEBUG ) Log.d(TAG, "getAccountByUserId(), filelugAccount=" + filelugAccount);
                if ( userId.equals(filelugAccount) ) {
                    result = account;
                    break;
                }
            }
        }
        return result;
    }

    public static HashMap<String, String> getAccountSettingsInfo(Account account) {
        AccountManager accountManager = AccountManager.get(mContext);

        boolean loggedIn = false;
        boolean socketConnected = false;
        boolean emailIsVerified = false;
        if ( account != null ) {
            String loggedInStr = accountManager.getUserData(account, Constants.EXT_PARAM_LOGGED_IN);
            loggedIn = loggedInStr == null ? false : Boolean.valueOf(loggedInStr);
            String socketConnectedStr = accountManager.getUserData(account, Constants.PARAM_SOCKET_CONNECTED);
            socketConnected = socketConnectedStr == null ? false : Boolean.valueOf(socketConnectedStr);
            String emailIsVerifiedStr = accountManager.getUserData(account, Constants.PARAM_EMAIL_IS_VERIFIED);
            emailIsVerified = emailIsVerifiedStr == null ? false : Boolean.valueOf(emailIsVerifiedStr);
        }

        String accountName = null;
        String accountNameStr;
        String computerName;
        String computerNameStr;
        String nicknameStr;
        String email;
        String emailStr;
        String msgNotSet = "("+mContext.getResources().getString(R.string.message_not_set)+")";
        String msgEmailNotVerified = " ("+mContext.getResources().getString(R.string.message_email_not_verified)+")";

        if ( account != null ) {
            accountName = account.name;
            computerName = accountManager.getUserData(account, Constants.PARAM_COMPUTER_NAME);
            nicknameStr = accountManager.getUserData(account, Constants.PARAM_NICKNAME);
            email = accountManager.getUserData(account, Constants.PARAM_EMAIL);
            if ( !loggedIn ) {
                String msgNotLoggedIn = " ("+mContext.getResources().getString(R.string.message_computer_not_logged_in)+")";
                accountNameStr = accountName + msgNotLoggedIn;
                computerNameStr = computerName;
            } else {
                if ( socketConnected ) {
                    accountNameStr = accountName;
                    computerNameStr = computerName;
                } else {
                    String msgNotConnected = " ("+mContext.getResources().getString(R.string.message_computer_not_connected)+")";
                    accountNameStr = accountName;
                    if ( computerName == null ) {
                        computerNameStr = " ("+mContext.getResources().getString(R.string.message_not_set)+")";
                    } else {
                        computerNameStr = computerName + msgNotConnected;
                    }
                }
            }
            if ( TextUtils.isEmpty(email) ) {
                emailStr = msgNotSet;
            } else {
                if ( emailIsVerified ) {
                    emailStr = email;
                } else {
                    emailStr = email + msgEmailNotVerified;
                }
            }
        } else {
            accountNameStr = msgNotSet;
            computerNameStr = msgNotSet;
            nicknameStr = msgNotSet;
            emailStr = msgNotSet;
        }

        HashMap<String, String> result = new HashMap<String, String>();
        result.put(mContext.getResources().getString(R.string.pref_account_name), accountNameStr);
        result.put(mContext.getResources().getString(R.string.pref_computer_name), computerNameStr);
        result.put(mContext.getResources().getString(R.string.pref_nickname), nicknameStr);
        result.put(mContext.getResources().getString(R.string.pref_email), emailStr);

        return result;
    }

}
