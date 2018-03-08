package com.filelug.android.ui.activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.BaseResponseError;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.crepo.RepositoryUtility;
import com.filelug.android.ui.fragment.BasePreferenceFragment;
import com.filelug.android.ui.fragment.HistoryFragment;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.NetworkUtils;
import com.filelug.android.util.PrefUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Vincent Chang on 2017/2/27.
 * Copyright (c) 2017 Filelug. All rights reserved.
 */
public class ManageCurrentAccountActivity extends BaseConfigureActivity {

    private static final String TAG = ManageCurrentAccountActivity.class.getSimpleName();

    private Toolbar mToolbar;

    private static Account mActiveAccount;
    private static HashMap<String, String> mAccountInfoMap;
    private static HashMap<String, Object> mChangedMap;

    public static class CurrentAccountPreferenceFragment extends BasePreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mChangedMap = new HashMap<String, Object>();
            addPreferencesFromResource(R.xml.manage_current_account);
            // show the current value in the settings screen
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                initSummary(getPreferenceScreen().getPreference(i));
            }
            checkNeedUpdateAgainConfig();
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            boolean res = super.onPreferenceTreeClick(preferenceScreen, preference);
            String key = preference.getKey();
            if ( !TextUtils.isEmpty(key) ) {
                if ( key.equals(getString(R.string.pref_email)) ) {
                    doChangeEmail();
                } else if ( key.equals(getString(R.string.pref_nickname)) ) {
                    doChangeNickname();
                } else if ( key.equals(getString(R.string.pref_downloaded_files_history)) ) {
                    doShowDownloadHistory();
                } else if ( key.equals(getString(R.string.pref_uploaded_files_history)) ) {
                    doShowUploadHistory();
                } else if ( key.equals(getString(R.string.pref_delete_account)) ) {
                    beforeShowDeleteAccountDialog();
                }
            }
            return res;
        }

        private void doChangeNickname() {
            if ( mActiveAccount != null ) {
                AccountManager mAccountManager = AccountManager.get(getActivity());
                String nickname = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_NICKNAME);

                Intent intent = new Intent(getActivity(), ChangeNicknameActivity.class);
                intent.putExtra(Constants.PARAM_NICKNAME, nickname);
                startActivityForResult(intent, Constants.REQUEST_CHANGE_NICKNAME);
            }
        }

        private void doChangeEmail() {
            if ( mActiveAccount != null ) {
                AccountManager mAccountManager = AccountManager.get(getActivity());
                String email = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_EMAIL);
                String tmp = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_EMAIL_IS_VERIFIED);
                boolean emailIsVerified = tmp == null ? false : Boolean.valueOf(tmp);

                Intent intent = new Intent(getActivity(), ChangeEmailActivity.class);
                intent.putExtra(Constants.PARAM_EMAIL, email);
                intent.putExtra(Constants.PARAM_EMAIL_IS_VERIFIED, emailIsVerified);
                startActivityForResult(intent, Constants.REQUEST_CHANGE_EMAIL);
            }
        }

        private void doShowDownloadHistory() {
            Intent intent = new Intent(getActivity(), HistoryActivity.class);
            intent.putExtra(Constants.EXT_PARAM_HISTORY_TYPE, HistoryFragment.HISTORY_TYPE_DOWNLOAD);
            startActivity(intent);
        }

        private void doShowUploadHistory() {
            Intent intent = new Intent(getActivity(), HistoryActivity.class);
            intent.putExtra(Constants.EXT_PARAM_HISTORY_TYPE, HistoryFragment.HISTORY_TYPE_UPLOAD);
            startActivity(intent);
        }

        private void beforeShowDeleteAccountDialog() {
            int permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS);
            if ( permission != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.GET_ACCOUNTS }, Constants.REQUEST_PERMISSION_GET_ACCOUNTS_WHEN_DELETE_ACCOUNT);
                return;
            }
            showDeleteAccountDialog();
        }

        private void showDeleteAccountDialog() {
            final Account mActiveAccount = AccountUtils.getActiveAccount();
            if ( mActiveAccount != null ) {
                String title = String.format(getResources().getString(R.string.title_delete_account), mActiveAccount.name);
                String btn1Text = getResources().getString(R.string.btn_label_delete_account_from_device);
                String btn2Text = getResources().getString(R.string.btn_label_delete_account_from_remote_and_device);
                String dialogContent = String.format(getResources().getString(R.string.message_delete_account), btn1Text, btn2Text);
                MaterialDialog.SingleButtonCallback positiveButtonCallback = new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        doDeleteLocalAccount(mActiveAccount);
                    }
                };
                MaterialDialog.SingleButtonCallback negativeButtonCallback = new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        verifyPhoneNumber(mActiveAccount);
                    }
                };
                MaterialDialog.SingleButtonCallback neutralButtonCallback = new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    }
                };
                DialogUtils.createStackedButtonsDialog(
                    getActivity(),
                    title,
                    R.drawable.menu_ic_delete_account,
                    dialogContent,
                    R.string.btn_label_delete_account_from_device,
                    R.string.btn_label_delete_account_from_remote_and_device,
                    R.string.btn_label_cancel,
                    positiveButtonCallback,
                    negativeButtonCallback,
                    neutralButtonCallback
                ).show();
            }
        }

        private void doDeleteLocalAccount(final Account account) {
            AccountManagerCallback callback = new AccountManagerCallback<Boolean>() {
                public void run(AccountManagerFuture<Boolean> future) {
                    try {
                        boolean result = false;
                        Object o = future.getResult();
                        if ( o instanceof Bundle ) {
                            result = ((Bundle)o).getBoolean("booleanResult");
                        } else if ( o instanceof Boolean ) {
                            result = ((Boolean)o).booleanValue();
                        }
                        if ( result ) {
                            PrefUtils.cleanActiveInfo(account.name);
                            MsgUtils.showToast(getActivity(), R.string.message_account_deleted);
                            String key = getString(R.string.pref_delete_account);
                            mChangedMap.put(key, account.name);
                            getActivity().finish();
                        }
                    } catch (OperationCanceledException e) {
                    } catch (IOException e) {
                    } catch (AuthenticatorException e) {
                    } catch (Exception e) {
                    }
                }
            };
            AccountUtils.removeAccount(getActivity(), account, callback);
        }

        private void verifyPhoneNumber(final Account account) {
            MaterialDialog.SingleButtonCallback buttonCallback = new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    AccountManager mAccountManager = AccountManager.get(getActivity());
                    String countryId = mAccountManager.getUserData(account, Constants.PARAM_COUNTRY_ID);
                    String countryCode = mAccountManager.getUserData(account, Constants.PARAM_COUNTRY_CODE);
                    String phone = mAccountManager.getUserData(account, Constants.PARAM_PHONE);
                    String phoneWithCountry = mAccountManager.getUserData(account, Constants.PARAM_PHONE_WITH_COUNTRY);
                    Bundle options = new Bundle();
                    options.putString(Constants.PARAM_COUNTRY_ID, countryId);
                    options.putString(Constants.PARAM_COUNTRY_CODE, countryCode);
                    options.putString(Constants.PARAM_PHONE, phone);
                    options.putString(Constants.PARAM_PHONE_WITH_COUNTRY, phoneWithCountry);
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);
//				    intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
                    intent.putExtra(Constants.AUTH_TOKEN_TYPE, Constants.AUTH_TOKEN_TYPE_GENERAL_SERVICE);
                    intent.putExtra(Constants.AUTH_ACTION_TYPE, Constants.AUTH_ACTION_VERIFY_PHONE_NUMBER);
                    intent.putExtra(Constants.AUTH_OPTIONS, options);

                    startActivityForResult(intent, Constants.REQUEST_ACCOUNT_KIT_VERIFY_PHONE_NUMBER);
                }
            };
            MsgUtils.showInfoMessage(getActivity(), R.string.message_verify_current_account, buttonCallback);
        }

        private void beforeDoCheckUserDeletable(String accountName) {
            final Account account = AccountUtils.getAccount(accountName);
            getAuthToken(new AccountUtils.AuthTokenCallback() {
                @Override
                public void onError(String errorMessage) {
                    String msg = String.format(getResources().getString(R.string.message_failed_to_delete_account), errorMessage);
                    MsgUtils.showWarningMessage(getActivity(), msg);
                }
                @Override
                public void onSuccess(String authToken) {
                    doCheckUserDeletable(authToken, account);
                }
            });
        }

        protected void getAuthToken(AccountUtils.AuthTokenCallback callback) {
            if ( !NetworkUtils.isNetworkAvailable(getActivity()) ) {
                return;
            }
            AccountUtils.getAuthToken(getActivity(), callback);
        }

        // beforeDoCheckUserDeletable ==>
        private void doCheckUserDeletable(final String authToken, final Account account) {
            AccountManager mAccountManager = AccountManager.get(getActivity());
            final String filelugAccount = mAccountManager.getUserData(account, Constants.EXT_PARAM_FILELUG_ACCOUNT);
            String nickname = mAccountManager.getUserData(account, Constants.PARAM_NICKNAME);
            final String locale = getResources().getConfiguration().locale.toString();
            final String verification = RepositoryUtility.generateDeleteUserVerification(filelugAccount, nickname, authToken);
            RepositoryClient.getInstance().checkUserDeletable(
                authToken,
                filelugAccount,
                locale,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response == null || response.trim().length() <= 0) {
                            doDeleteUser(authToken, account, filelugAccount, verification, locale);
                        } else {
                            if ( response.endsWith("\n") ) {
                                response = response.substring(0, response.length()-1);
                            }
                            response = response.replaceAll("\n", "<br/>");
                            String title = String.format(getResources().getString(R.string.title_delete_account), account.name);
                            String dialogContent = String.format(getResources().getString(R.string.message_confirm_delete_this_account_2), response);
                            MaterialDialog.SingleButtonCallback positiveButtonCallback = new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    doDeleteUser(authToken, account, filelugAccount, verification, locale);
                                }
                            };
                            DialogUtils.createButtonsDialog31(
                                getActivity(),
                                title,
                                R.drawable.menu_ic_delete_account,
                                dialogContent,
                                R.string.btn_label_delete,
                                R.string.btn_label_cancel,
                                positiveButtonCallback
                            ).show();
                        }
                    }
                },
                new BaseResponseError(true, getActivity(), BaseResponseError.MESSAGE_TYPE_WARNING_MESSAGE) {
                    @Override
                    protected String getMessage(VolleyError volleyError) {
                        String message = MiscUtils.getVolleyErrorMessage(volleyError);
                        return String.format(getResources().getString(R.string.message_failed_to_delete_account), message);
                    }
                }
            );
        }

        private void doDeleteUser(String authToken, final Account account, String filelugAccount, String verification, String locale) {
            RepositoryClient.getInstance().deleteUser2(
                authToken,
                filelugAccount,
                verification,
                locale,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        doDeleteLocalAccount(account);
                    }
                },
                new BaseResponseError(true, getActivity(), BaseResponseError.MESSAGE_TYPE_WARNING_MESSAGE) {
                    @Override
                    protected String getMessage(VolleyError volleyError) {
                        String message = MiscUtils.getVolleyErrorMessage(volleyError);
                        return String.format(getResources().getString(R.string.message_failed_to_delete_account), message);
                    }
                }
            );
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if ( requestCode == Constants.REQUEST_PERMISSION_GET_ACCOUNTS_WHEN_DELETE_ACCOUNT ) {
                if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    showDeleteAccountDialog();
                } else {
                    MsgUtils.showWarningMessage(getActivity(), R.string.message_can_not_get_accounts);
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference preference = findPreference(key);
            if ( preference != null ) {
                updateSummaryText(preference);
            }
            doAction(sharedPreferences, key);
        }

        private void initSummary(Preference p) {
            boolean hasActiveAccount = mActiveAccount != null;
            if (p instanceof PreferenceCategory) {
                PreferenceCategory cat = (PreferenceCategory) p;
                String key = cat.getKey();
                if ( key != null && !hasActiveAccount ) {
                    if ( key.equals(getResources().getString(R.string.pref_category_account_profile)) ||
                         key.equals(getResources().getString(R.string.pref_category_history)) ||
                         key.equals(getResources().getString(R.string.pref_category_delete)) ) {
                        cat.setEnabled(false);
                    }
                }
                for (int i = 0; i < cat.getPreferenceCount(); i++) {
                    initSummary(cat.getPreference(i));
                }
            } else {
                updateSummaryText(p);
            }
        }

        private void updateSummaryText(Preference p) {
            if ( p==null ) {
                return;
            }

//            if ( Constants.DEBUG ) Log.d(TAG, "updateSummaryText(), key=" + p.getKey() + ", title=" + p.getTitle() + ", summary=" + p.getSummary());

            String key = p.getKey();
            if ( getResources().getString(R.string.pref_account_name).equals(key) ||
                 getResources().getString(R.string.pref_email).equals(key) ||
                 getResources().getString(R.string.pref_nickname).equals(key) ) {
                String summaryText = mAccountInfoMap.get(key);
                p.setSummary(summaryText);
            } else {
                return;
            }
        }

        private void doAction(SharedPreferences sharedPreferences, String key) {
/*
            if ( getResources().getString(R.string.pref_display_language).equals(key) ) {
                String locale = sharedPreferences.getString(key, null);
                Intent intent = new Intent();
                intent.putExtra(Constants.PARAM_LOCALE, locale);
                getActivity().setResult(RESULT_OK, intent);
                getActivity().finish();
            }
*/
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if ( resultCode == RESULT_OK ) {
                Bundle extras = data.getExtras();
                String message = null;
                if ( requestCode == Constants.REQUEST_CHANGE_NICKNAME ) {
                    if ( extras != null ) {
                        String newNickname = extras.getString(Constants.PARAM_NEW_NICKNAME);
                        message = String.format(getResources().getString(R.string.message_successfully_change_nickname), newNickname);
                        MsgUtils.showToast(getActivity(), message);
                        String key = getString(R.string.pref_nickname);
                        Preference preference = findPreference(getString(R.string.pref_nickname));
                        preference.setSummary(newNickname);
                        mChangedMap.put(key, newNickname);
                        mAccountInfoMap.remove(key);
                        mAccountInfoMap.put(key, newNickname);
                    }
                } else if ( requestCode == Constants.REQUEST_CHANGE_EMAIL  ) {
                    if ( extras != null ) {
                        String newEmail = extras.getString(Constants.PARAM_NEW_EMAIL);
                        String originEmail = extras.getString(Constants.EXT_PARAM_ORIGIN_EMAIL, null);
                        boolean originEmailVerifyStatus = extras.getBoolean(Constants.EXT_PARAM_ORIGIN_EMAIL_VERIFY_STATUS, false);
                        if ( originEmailVerifyStatus ) {
                            message = String.format(getResources().getString(R.string.message_successfully_change_email), newEmail);
                        } else {
                            message = getResources().getString(R.string.message_email_verified);
                        }
                        MsgUtils.showToast(getActivity(), message);
                        String key = getString(R.string.pref_email);
                        Preference preference = findPreference(getString(R.string.pref_email));
                        preference.setSummary(newEmail);
                        mChangedMap.put(key, newEmail);
                        mAccountInfoMap.remove(key);
                        mAccountInfoMap.put(key, newEmail);
                    }
                } else if ( requestCode == Constants.REQUEST_ACCOUNT_KIT_VERIFY_PHONE_NUMBER  ) {
                    if ( extras != null ) {
                        String accountName = extras.getString(AccountManager.KEY_ACCOUNT_NAME);
                        beforeDoCheckUserDeletable(accountName);
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mToolbar.setTitle(R.string.drawer_account_title_manage_current_account);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActiveAccount = AccountUtils.getActiveAccount();
        mAccountInfoMap = AccountUtils.getAccountSettingsInfo(mActiveAccount);

        if (getFragmentManager().findFragmentById(R.id.content_frame) == null) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new CurrentAccountPreferenceFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void finish() {
        if ( mChangedMap != null && mChangedMap.size() > 0 ) {
            Intent intent = new Intent();
            intent.putExtra(Constants.EXT_PARAM_CHANGED_PREFERENCES, mChangedMap);
            setResult(RESULT_OK, intent);
        }
        super.finish();
    }

}
