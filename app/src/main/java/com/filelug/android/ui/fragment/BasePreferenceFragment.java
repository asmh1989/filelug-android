package com.filelug.android.ui.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.text.TextUtils;

import com.android.volley.Response;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.BaseResponseError;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.PrefUtils;

import java.util.HashMap;

/**
 * Created by Vincent Chang on 2017/3/1.
 * Copyright (c) 2017 Filelug. All rights reserved.
 */
public class BasePreferenceFragment extends PreferenceFragment {

    private static final String TAG = BasePreferenceFragment.class.getSimpleName();

    protected void checkNeedUpdateAgainConfig() {
        Account activeAccount = AccountUtils.getActiveAccount();
        if ( activeAccount == null ) {
            return;
        }

        AccountManager accountManager = AccountManager.get(getActivity());
        HashMap<String, Object> needUpdateAgainConfig = new HashMap<String, Object>();

        String value_upload_path = PrefUtils.getUploadPath(null);
        if ( !TextUtils.isEmpty(value_upload_path) ) {
            String user_data_upload_path = accountManager.getUserData(activeAccount, Constants.PARAM_UPLOAD_DIRECTORY);
            if ( !TextUtils.equals(value_upload_path, user_data_upload_path) ) {
                String key_upload_path = getResources().getString(R.string.pref_upload_path);
                needUpdateAgainConfig.put(key_upload_path, value_upload_path);
            }
        }

        String value_upload_subdir = PrefUtils.getUploadSubdirType(null);
        if ( !TextUtils.isEmpty(value_upload_subdir) ) {
            String user_data_upload_subdir = accountManager.getUserData(activeAccount, Constants.PARAM_UPLOAD_SUB_DIRECTORY_TYPE);
            if ( !TextUtils.equals(value_upload_subdir, user_data_upload_subdir) ) {
                String key_upload_subdir = getResources().getString(R.string.pref_upload_subdir);
                needUpdateAgainConfig.put(key_upload_subdir, Integer.valueOf(value_upload_subdir));
            }
        }

        String value_upload_subdir_value = PrefUtils.getUploadSubdirValue(null);
        if ( !TextUtils.isEmpty(value_upload_subdir_value) ) {
            String user_data_upload_subdir_value = accountManager.getUserData(activeAccount, Constants.PARAM_UPLOAD_SUB_DIRECTORY_VALUE);
            if ( !TextUtils.equals(value_upload_subdir_value, user_data_upload_subdir_value) ) {
                String key_upload_subdir_value = getResources().getString(R.string.pref_upload_subdir_value);
                needUpdateAgainConfig.put(key_upload_subdir_value, value_upload_subdir_value);
            }
        }

        String value_upload_description_type = PrefUtils.getUploadDescriptionType(null);
        if ( !TextUtils.isEmpty(value_upload_description_type) ) {
            String user_data_upload_description_type = accountManager.getUserData(activeAccount, Constants.PARAM_UPLOAD_DESCRIPTION_TYPE);
            if ( !TextUtils.equals(value_upload_description_type, user_data_upload_description_type) ) {
                String key_upload_description_type = getResources().getString(R.string.pref_upload_description_type);
                needUpdateAgainConfig.put(key_upload_description_type, Integer.valueOf(value_upload_description_type));
            }
        }

        String value_upload_description_value = PrefUtils.getUploadDescriptionValue(null);
        if ( !TextUtils.isEmpty(value_upload_description_value) ) {
            String user_data_upload_description_value = accountManager.getUserData(activeAccount, Constants.PARAM_UPLOAD_DESCRIPTION_VALUE);
            if ( !TextUtils.equals(value_upload_description_value, user_data_upload_description_value) ) {
                String key_upload_description_value = getResources().getString(R.string.pref_upload_description_value);
                needUpdateAgainConfig.put(key_upload_description_value, value_upload_description_value);
            }
        }

        String value_upload_notification_type = PrefUtils.getUploadNotificationType(null);
        if ( !TextUtils.isEmpty(value_upload_notification_type) ) {
            String user_data_upload_notification_type = accountManager.getUserData(activeAccount, Constants.PARAM_UPLOAD_NOTIFICATION_TYPE);
            if ( !TextUtils.equals(value_upload_notification_type, user_data_upload_notification_type) ) {
                String key_upload_notification_type = getResources().getString(R.string.pref_upload_notification_type);
                needUpdateAgainConfig.put(key_upload_notification_type, Integer.valueOf(value_upload_notification_type));
            }
        }

        String value_download_path = PrefUtils.getDownloadPath(null);
        if ( !TextUtils.isEmpty(value_download_path) ) {
            String user_data_download_path = accountManager.getUserData(activeAccount, Constants.PARAM_DOWNLOAD_DIRECTORY);
            if ( !TextUtils.equals(value_download_path, user_data_download_path) ) {
                String key_download_path = getResources().getString(R.string.pref_download_path);
                needUpdateAgainConfig.put(key_download_path, value_download_path);
            }
        }

        String value_download_subdir = PrefUtils.getDownloadSubdirType(null);
        if ( !TextUtils.isEmpty(value_download_subdir) ) {
            String user_data_download_subdir = accountManager.getUserData(activeAccount, Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_TYPE);
            if ( !TextUtils.equals(value_download_subdir, user_data_download_subdir) ) {
                String key_download_subdir = getResources().getString(R.string.pref_download_subdir);
                needUpdateAgainConfig.put(key_download_subdir, Integer.valueOf(value_download_subdir));
            }
        }

        String value_download_subdir_value = PrefUtils.getDownloadSubdirValue(null);
        if ( !TextUtils.isEmpty(value_download_subdir_value) ) {
            String user_data_download_subdir_value = accountManager.getUserData(activeAccount, Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_VALUE);
            if ( !TextUtils.equals(value_download_subdir_value, user_data_download_subdir_value) ) {
                String key_download_subdir_value = getResources().getString(R.string.pref_download_subdir_value);
                needUpdateAgainConfig.put(key_download_subdir_value, value_download_subdir_value);
            }
        }

        String value_download_description_type = PrefUtils.getDownloadDescriptionType(null);
        if ( !TextUtils.isEmpty(value_download_description_type) ) {
            String user_data_download_description_type = accountManager.getUserData(activeAccount, Constants.PARAM_DOWNLOAD_DESCRIPTION_TYPE);
            if ( !TextUtils.equals(value_download_description_type, user_data_download_description_type) ) {
                String key_download_description_type = getResources().getString(R.string.pref_download_description_type);
                needUpdateAgainConfig.put(key_download_description_type, Integer.valueOf(value_download_description_type));
            }
        }

        String value_download_description_value = PrefUtils.getDownloadDescriptionValue(null);
        if ( !TextUtils.isEmpty(value_download_description_value) ) {
            String user_data_download_description_value = accountManager.getUserData(activeAccount, Constants.PARAM_DOWNLOAD_DESCRIPTION_VALUE);
            if ( !TextUtils.equals(value_download_description_value, user_data_download_description_value) ) {
                String key_download_description_value = getResources().getString(R.string.pref_download_description_value);
                needUpdateAgainConfig.put(key_download_description_value, value_download_description_value);
            }
        }

        String value_download_notification_type = PrefUtils.getDownloadNotificationType(null);
        if ( !TextUtils.isEmpty(value_download_notification_type) ) {
            String user_data_download_notification_type = accountManager.getUserData(activeAccount, Constants.PARAM_DOWNLOAD_NOTIFICATION_TYPE);
            if ( !TextUtils.equals(value_download_notification_type, user_data_download_notification_type) ) {
                String key_download_notification_type = getResources().getString(R.string.pref_download_notification_type);
                needUpdateAgainConfig.put(key_download_notification_type, Integer.valueOf(value_download_notification_type));
            }
        }

        if ( needUpdateAgainConfig.size() > 0 ) {
            updateRepoUserProfiles_getAuthToken(activeAccount, needUpdateAgainConfig);
        }
    }

    protected void updateRepoUserProfiles_getAuthToken(final Account activeAccount, final HashMap<String, Object> profiles) {
        AccountUtils.AuthTokenCallback callback = new AccountUtils.AuthTokenCallback() {
            @Override
            public void onError(String errorMessage) {
                MsgUtils.showWarningMessage(getActivity(), errorMessage);
            }
            @Override
            public void onSuccess(String authToken) {
                updateRepoUserProfiles(activeAccount, authToken, profiles);
            }
        };
        AccountUtils.getAuthToken(getActivity(), callback);
    }

    private void updateRepoUserProfiles(final Account activeAccount, String authToken, final HashMap<String, Object> profiles) {
        String locale = getResources().getConfiguration().locale.toString();
        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Bundle userData = new Bundle();
                for ( String key : profiles.keySet() ) {
                    Object value = profiles.get(key);
                    if ( getResources().getString(R.string.pref_upload_path).equals(key) ) {
                        userData.putString(Constants.PARAM_UPLOAD_DIRECTORY, (value!=null ? (String)value : ""));
                    } else if ( getResources().getString(R.string.pref_upload_subdir).equals(key) ) {
                        userData.putString(Constants.PARAM_UPLOAD_SUB_DIRECTORY_TYPE, ((Integer)value).toString());
                    } else if ( getResources().getString(R.string.pref_upload_subdir_value).equals(key) ) {
                        userData.putString(Constants.PARAM_UPLOAD_SUB_DIRECTORY_VALUE, (value!=null ? (String)value : ""));
                    } else if ( getResources().getString(R.string.pref_upload_description_type).equals(key) ) {
                        userData.putString(Constants.PARAM_UPLOAD_DESCRIPTION_TYPE, ((Integer)value).toString());
                    } else if ( getResources().getString(R.string.pref_upload_description_value).equals(key) ) {
                        userData.putString(Constants.PARAM_UPLOAD_DESCRIPTION_VALUE, (value!=null ? (String)value : ""));
                    } else if ( getResources().getString(R.string.pref_upload_notification_type).equals(key) ) {
                        userData.putString(Constants.PARAM_UPLOAD_NOTIFICATION_TYPE, ((Integer)value).toString());
                    } else if ( getResources().getString(R.string.pref_download_path).equals(key) ) {
                        userData.putString(Constants.PARAM_DOWNLOAD_DIRECTORY, (value!=null ? (String)value : ""));
                    } else if ( getResources().getString(R.string.pref_download_subdir).equals(key) ) {
                        userData.putString(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_TYPE, ((Integer)value).toString());
                    } else if ( getResources().getString(R.string.pref_download_subdir_value).equals(key) ) {
                        userData.putString(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_VALUE, (value!=null ? (String)value : ""));
                    } else if ( getResources().getString(R.string.pref_download_description_type).equals(key) ) {
                        userData.putString(Constants.PARAM_DOWNLOAD_DESCRIPTION_TYPE, ((Integer)value).toString());
                    } else if ( getResources().getString(R.string.pref_download_description_value).equals(key) ) {
                        userData.putString(Constants.PARAM_DOWNLOAD_DESCRIPTION_VALUE, (value!=null ? (String)value : ""));
                    } else if ( getResources().getString(R.string.pref_download_notification_type).equals(key) ) {
                        userData.putString(Constants.PARAM_DOWNLOAD_NOTIFICATION_TYPE, ((Integer)value).toString());
                    }
                }
                AccountUtils.resetUserData(activeAccount, userData);
            }
        };
        BaseResponseError error = new BaseResponseError(true, getActivity());
        RepositoryClient.getInstance().changeUserComputerProfiles(
            authToken,
            profiles,
            locale,
            response,
            error
        );
    }

}
