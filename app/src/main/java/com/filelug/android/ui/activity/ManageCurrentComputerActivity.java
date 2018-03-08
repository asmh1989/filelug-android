package com.filelug.android.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;

import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.ui.fragment.BasePreferenceFragment;
import com.filelug.android.ui.widget.EditTextPreference;
import com.filelug.android.ui.widget.ListPreference;
import com.filelug.android.ui.widget.LocalFolderPreference;
import com.filelug.android.ui.widget.RemoteFolderPreference;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.PrefUtils;

import java.util.HashMap;

/**
 * Created by Vincent Chang on 2017/2/27.
 * Copyright (c) 2017 Filelug. All rights reserved.
 */
public class ManageCurrentComputerActivity extends BaseConfigureActivity {

    private static final String TAG = ManageCurrentComputerActivity.class.getSimpleName();

    private Toolbar mToolbar;

    private static Account mActiveAccount;
    private static HashMap<String, String> mAccountInfoMap;
    private static HashMap<String, Object> mChangedMap;

    public static class CurrentComputerPreferenceFragment extends BasePreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mChangedMap = new HashMap<String, Object>();
            addPreferencesFromResource(R.xml.manage_current_computer);
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
                if ( key.equals(getString(R.string.pref_computer_name)) ) {
                    doChangeComputerName();
                } else if ( key.equals(getString(R.string.pref_delete_computer)) ) {
                    doDeleteComputer();
                }
            }
            return res;
        }

        private void doChangeComputerName() {
            if ( mActiveAccount != null ) {
                AccountManager mAccountManager = AccountManager.get(getActivity());
                String tmpComputerId = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_COMPUTER_ID);
                int computerId = TextUtils.isEmpty(tmpComputerId) ? -1 : Integer.valueOf(tmpComputerId);
                String computerName = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_COMPUTER_NAME);
                String computerGroup = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_COMPUTER_GROUP);

                if ( computerId == -1 ) {
                    MsgUtils.showWarningMessage(getActivity(), R.string.message_computer_not_set);
                    return;
                }

                Intent intent = new Intent(getActivity(), ChangeComputerNameActivity.class);
                intent.putExtra(Constants.PARAM_COMPUTER_ID, computerId);
                intent.putExtra(Constants.PARAM_COMPUTER_NAME, computerName);
                intent.putExtra(Constants.PARAM_COMPUTER_GROUP, computerGroup);
                startActivityForResult(intent, Constants.REQUEST_CHANGE_COMPUTER_NAME);
            }
        }

        private void doDeleteComputer() {
            Intent intent = new Intent(getActivity(), DeleteComputerActivity.class);

            Account mActiveAccount = AccountUtils.getActiveAccount();
            if ( mActiveAccount != null ) {
                AccountManager mAccountManager = AccountManager.get(getActivity());
                String tmpComputerId = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_COMPUTER_ID);
                int computerId = TextUtils.isEmpty(tmpComputerId) ? -1 : Integer.valueOf(tmpComputerId);
                String computerName = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_COMPUTER_NAME);

                intent.putExtra(Constants.PARAM_COMPUTER_ID, computerId);
                intent.putExtra(Constants.PARAM_COMPUTER_NAME, computerName);
            }

            startActivityForResult(intent, Constants.REQUEST_DELETE_COMPUTER);
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
            if ( preference == null ) {
                Preference listPreference = null;
                if ( getResources().getString(R.string.pref_upload_subdir_value).equals(key) ) {
                    listPreference = findPreference(getResources().getString(R.string.pref_upload_subdir));
                } else if ( getResources().getString(R.string.pref_upload_description_value).equals(key) ) {
                    listPreference = findPreference(getResources().getString(R.string.pref_upload_description_type));
                } else if ( getResources().getString(R.string.pref_download_subdir_value).equals(key) ) {
                    listPreference = findPreference(getResources().getString(R.string.pref_download_subdir));
                } else if ( getResources().getString(R.string.pref_download_description_value).equals(key) ) {
                    listPreference = findPreference(getResources().getString(R.string.pref_download_description_type));
                }
                if ( listPreference != null ) {
                    updateSummaryText(listPreference);
                }
            } else {
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
                    if ( key.equals(getResources().getString(R.string.pref_category_computer_profile)) ||
                         key.equals(getResources().getString(R.string.pref_category_upload_settings)) ||
                         key.equals(getResources().getString(R.string.pref_category_download_settings)) ||
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

            String summaryText = null;
            if (p instanceof EditTextPreference) {
                EditTextPreference editTextPref = (EditTextPreference) p;
                summaryText = editTextPref.getText();
            } else if (p instanceof ListPreference) {
                ListPreference listPref = (ListPreference) p;
                summaryText = getListPreferenceSummaryText(listPref);
            } else if (p instanceof LocalFolderPreference) {
                LocalFolderPreference localFolderPref = (LocalFolderPreference) p;
                summaryText = localFolderPref.getFolder();
            } else if (p instanceof RemoteFolderPreference) {
                RemoteFolderPreference remoteFolderPref = (RemoteFolderPreference) p;
                summaryText = remoteFolderPref.getFolder();
            } else {
                String key = p.getKey();
                if ( getResources().getString(R.string.pref_computer_name).equals(key) ) {
                    summaryText = mAccountInfoMap.get(key);
                } else {
                    return;
                }
            }

            if ( TextUtils.isEmpty(summaryText) ) {
                summaryText = getResources().getString(R.string.message_not_set);
            }
            p.setSummary(summaryText);
        }

        private String getListPreferenceSummaryText(ListPreference listPref) {
            if ( mActiveAccount == null ) {
                return null;
            }
            String summaryText = null;
            String key = listPref.getKey();
            int value = Integer.valueOf(listPref.getValue());
            if ( getResources().getString(R.string.pref_upload_subdir).equals(key) ) {
                String subdirValue = PrefUtils.getUploadSubdirValue(null);
                summaryText = MiscUtils.getSubDirTypeSettingText(CurrentComputerPreferenceFragment.this.getActivity(), value, subdirValue);
            } else if ( getResources().getString(R.string.pref_upload_description_type).equals(key) ) {
                String descriptionValue = PrefUtils.getUploadDescriptionValue(null);
                summaryText = MiscUtils.getDescriptionTypeSettingText(CurrentComputerPreferenceFragment.this.getActivity(), value, descriptionValue);
            } else if ( getResources().getString(R.string.pref_upload_notification_type).equals(key) ) {
                summaryText = MiscUtils.getNotificationTypeStr(CurrentComputerPreferenceFragment.this.getActivity(), value);
            } else if ( getResources().getString(R.string.pref_download_subdir).equals(key) ) {
                String subdirValue = PrefUtils.getDownloadSubdirValue(null);
                summaryText = MiscUtils.getSubDirTypeSettingText(CurrentComputerPreferenceFragment.this.getActivity(), value, subdirValue);
            } else if ( getResources().getString(R.string.pref_download_description_type).equals(key) ) {
                String descriptionValue = PrefUtils.getDownloadDescriptionValue(null);
                summaryText = MiscUtils.getDescriptionTypeSettingText(CurrentComputerPreferenceFragment.this.getActivity(), value, descriptionValue);
            } else if ( getResources().getString(R.string.pref_download_notification_type).equals(key) ) {
                summaryText = MiscUtils.getNotificationTypeStr(CurrentComputerPreferenceFragment.this.getActivity(), value);
            }
            return summaryText;
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
            if ( mActiveAccount != null ) {
                String value = sharedPreferences.getString(key, null);
                updateUploadDownloadProfiles(key, value);
            }
        }

        private void updateUploadDownloadProfiles(String key, String value) {
            HashMap<String, Object> updateConfig = new HashMap<String, Object>();
            if ( getResources().getString(R.string.pref_upload_path).equals(key) ) {
                PrefUtils.setUploadPath(mActiveAccount.name, value);
                PrefUtils.setUploadPath(null, value);
                updateConfig.put(Constants.PARAM_UPLOAD_DIRECTORY, value);
            } else if ( getResources().getString(R.string.pref_upload_subdir).equals(key) ) {
                PrefUtils.setUploadSubdirType(mActiveAccount.name, value);
                PrefUtils.setUploadSubdirType(null, value);
                updateConfig.put(Constants.PARAM_UPLOAD_SUB_DIRECTORY_TYPE, Integer.valueOf(value));
            } else if ( getResources().getString(R.string.pref_upload_subdir_value).equals(key) ) {
                PrefUtils.setUploadSubdirValue(mActiveAccount.name, value);
                PrefUtils.setUploadSubdirValue(null, value);
                updateConfig.put(Constants.PARAM_UPLOAD_SUB_DIRECTORY_VALUE, value);
            } else if ( getResources().getString(R.string.pref_upload_description_type).equals(key) ) {
                PrefUtils.setUploadDescriptionType(mActiveAccount.name, value);
                PrefUtils.setUploadDescriptionType(null, value);
                updateConfig.put(Constants.PARAM_UPLOAD_DESCRIPTION_TYPE, Integer.valueOf(value));
            } else if ( getResources().getString(R.string.pref_upload_description_value).equals(key) ) {
                PrefUtils.setUploadDescriptionValue(mActiveAccount.name, value);
                PrefUtils.setUploadDescriptionValue(null, value);
                updateConfig.put(Constants.PARAM_UPLOAD_DESCRIPTION_VALUE, value);
            } else if ( getResources().getString(R.string.pref_upload_notification_type).equals(key) ) {
                PrefUtils.setUploadNotificationType(mActiveAccount.name, value);
                PrefUtils.setUploadNotificationType(null, value);
                updateConfig.put(Constants.PARAM_UPLOAD_NOTIFICATION_TYPE, Integer.valueOf(value));
            } else if ( getResources().getString(R.string.pref_download_path).equals(key) ) {
                PrefUtils.setDownloadPath(mActiveAccount.name, value);
                PrefUtils.setDownloadPath(null, value);
                updateConfig.put(Constants.PARAM_DOWNLOAD_DIRECTORY, value);
            } else if ( getResources().getString(R.string.pref_download_subdir).equals(key) ) {
                PrefUtils.setDownloadSubdirType(mActiveAccount.name, value);
                PrefUtils.setDownloadSubdirType(null, value);
                updateConfig.put(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_TYPE, Integer.valueOf(value));
            } else if ( getResources().getString(R.string.pref_download_subdir_value).equals(key) ) {
                PrefUtils.setDownloadSubdirValue(mActiveAccount.name, value);
                PrefUtils.setDownloadSubdirValue(null, value);
                updateConfig.put(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_VALUE, value);
            } else if ( getResources().getString(R.string.pref_download_description_type).equals(key) ) {
                PrefUtils.setDownloadDescriptionType(mActiveAccount.name, value);
                PrefUtils.setDownloadDescriptionType(null, value);
                updateConfig.put(Constants.PARAM_DOWNLOAD_DESCRIPTION_TYPE, Integer.valueOf(value));
            } else if ( getResources().getString(R.string.pref_download_description_value).equals(key) ) {
                PrefUtils.setDownloadDescriptionValue(mActiveAccount.name, value);
                PrefUtils.setDownloadDescriptionValue(null, value);
                updateConfig.put(Constants.PARAM_DOWNLOAD_DESCRIPTION_VALUE, value);
            } else if ( getResources().getString(R.string.pref_download_notification_type).equals(key) ) {
                PrefUtils.setDownloadNotificationType(mActiveAccount.name, value);
                PrefUtils.setDownloadNotificationType(null, value);
                updateConfig.put(Constants.PARAM_DOWNLOAD_NOTIFICATION_TYPE, Integer.valueOf(value));
            }
            if ( updateConfig.size() > 0 ) {
                updateRepoUserProfiles_getAuthToken(mActiveAccount, updateConfig);
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if ( resultCode == RESULT_OK ) {
                Bundle extras = data.getExtras();
                String message = null;
                if (requestCode == Constants.REQUEST_CHANGE_COMPUTER_NAME) {
                    if (extras != null) {
                        String newComputerName = extras.getString(Constants.PARAM_NEW_COMPUTER_NAME);
                        message = String.format(getResources().getString(R.string.message_successfully_change_computer_name), newComputerName);
                        MsgUtils.showToast(getActivity(), message);
                        String key = getString(R.string.pref_computer_name);
                        Preference preference = findPreference(getString(R.string.pref_computer_name));
                        preference.setSummary(newComputerName);
                        mChangedMap.put(key, newComputerName);
                        mAccountInfoMap.remove(key);
                        mAccountInfoMap.put(key, newComputerName);
                    }
                } else if ( requestCode == Constants.REQUEST_DELETE_COMPUTER ) {
                    if ( extras != null ) {
                        int computerId = extras.getInt(Constants.PARAM_COMPUTER_ID);
                        String computerName = extras.getString(Constants.PARAM_COMPUTER_NAME);
                        message = String.format(getResources().getString(R.string.message_computer_has_been_deleted), computerName);
                        MsgUtils.showToast(getActivity(), message);
                        String key = getString(R.string.pref_delete_computer);
                        mChangedMap.put(key, computerName);
                        getActivity().finish();
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
        mToolbar.setTitle(R.string.drawer_account_title_manage_current_computer);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActiveAccount = AccountUtils.getActiveAccount();
        mAccountInfoMap = AccountUtils.getAccountSettingsInfo(mActiveAccount);

        if (getFragmentManager().findFragmentById(R.id.content_frame) == null) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new CurrentComputerPreferenceFragment()).commit();
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
