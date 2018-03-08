package com.filelug.android.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.ui.fragment.BasePreferenceFragment;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.FileCache;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.MsgUtils;

import java.util.HashMap;

/**
 * Created by Vincent Chang on 2015/11/24.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class SettingsActivity extends BaseConfigureActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private Toolbar mToolbar;

    private static HashMap<String, Object> mChangedMap;

    public static class SettingsFragment extends BasePreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mChangedMap = new HashMap<String, Object>();
            addPreferencesFromResource(R.xml.settings);
            // show the current value in the settings screen
            boolean isServiceRunning = MiscUtils.isUploadOrDownloadOrNotificationServiceRunning();
//            if ( Constants.DEBUG ) Log.d(TAG, "onCreate(), isServiceRunning="+isServiceRunning);
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                initSummary(getPreferenceScreen().getPreference(i), isServiceRunning);
            }
            checkNeedUpdateAgainConfig();
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            boolean res = super.onPreferenceTreeClick(preferenceScreen, preference);
            String key = preference.getKey();
            if ( !TextUtils.isEmpty(key) ) {
                if ( key.equals(getString(R.string.pref_delete_local_cached_data)) ) {
                    doDeleteLocalCachedData();
                } else if ( key.equals(getString(R.string.pref_getting_started)) ) {
                    Intent intent = new Intent(getActivity(), GettingStartedActivity.class);
                    startActivity(intent);
                } else if ( key.equals(getString(R.string.pref_rate_us_on_google_play)) ) {
                    MiscUtils.createRateUsIntent(getActivity());
                } else if ( key.equals(getString(R.string.pref_share_app_to_friends)) ) {
                    MiscUtils.createShareAppToFriendsIntent(getActivity());
                } else if ( key.equals(getString(R.string.pref_frequently_asked_questions)) ) {
                } else if ( key.equals(getString(R.string.pref_send_feedback)) ) {
                    MiscUtils.createSendFeedbackIntent(getActivity());
                } else if ( key.equals(getString(R.string.pref_about)) ) {
                }
            }
            return res;
        }

        private void doDeleteLocalCachedData() {
            MaterialDialog.SingleButtonCallback buttonCallback = new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    FileCache.cleanAllCachedData();
                    dialog.dismiss();
                    MsgUtils.showInfoMessage(getActivity(), R.string.message_local_cached_data_delete);
                }
            };
            DialogUtils.createButtonsDialog31(
                getActivity(),
                -1,
                -1,
                R.string.message_confirm_delete_local_data,
                R.string.btn_label_yes_delete,
                R.string.btn_label_cancel,
                buttonCallback
            ).show();
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

        private void initSummary(Preference p, boolean isServiceRunning) {
            if (p instanceof PreferenceCategory) {
                PreferenceCategory cat = (PreferenceCategory) p;
                String key = cat.getKey();
                if ( key != null && isServiceRunning ) {
                    if ( key.equals(getResources().getString(R.string.pref_category_display_settings)) ) {
                        cat.setEnabled(false);
                    }
                }
                for (int i = 0; i < cat.getPreferenceCount(); i++) {
                    initSummary(cat.getPreference(i), isServiceRunning);
                }
            } else {
                String key = p.getKey();
//                if ( Constants.DEBUG ) Log.d(TAG, "initSummary(), key="+key);
                if ( key != null && isServiceRunning ) {
                    if ( key.equals(getResources().getString(R.string.pref_delete_local_cached_data)) ) {
                        p.setEnabled(false);
                    }
                }
                updateSummaryText(p);
            }
        }

        private void updateSummaryText(Preference p) {
            if ( p==null ) {
                return;
            }

//            if ( Constants.DEBUG ) Log.d(TAG, "updateSummaryText(), key=" + p.getKey() + ", title=" + p.getTitle() + ", summary=" + p.getSummary());

            String key = p.getKey();
            if ( getResources().getString(R.string.pref_about).equals(key) ) {
                String versionName = MiscUtils.getFilelugVersion(getActivity());
                p.setSummary(versionName);
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
            if ( getResources().getString(R.string.pref_show_hidden_files).equals(key) ||
                 getResources().getString(R.string.pref_show_local_system_folder).equals(key) ) {
                boolean value = sharedPreferences.getBoolean(key, false);
                mChangedMap.put(key, value);
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
        mToolbar.setTitle(R.string.drawer_title_settings);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getFragmentManager().findFragmentById(R.id.content_frame) == null) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
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
