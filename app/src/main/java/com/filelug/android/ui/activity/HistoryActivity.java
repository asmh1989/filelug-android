package com.filelug.android.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.ui.fragment.HistoryFragment;

/**
 * Created by Vincent Chang on 2017/3/2.
 * Copyright (c) 2017 Filelug. All rights reserved.
 */
public class HistoryActivity extends BaseConfigureActivity {

    private static final String TAG = HistoryActivity.class.getSimpleName();

    protected Handler mHandler;
//    private Toolbar mActionBarToolbar;
    private Toolbar mToolbar;
    private HistoryFragment mFragment = null;

    private int mHistoryType = HistoryFragment.HISTORY_TYPE_DOWNLOAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Constants.DEBUG) Log.d(TAG, "onCreate()");

        setContentView(R.layout.layout_history);

        initIntentAndExtras(getIntent());
        initUI();
    }

    private void initIntentAndExtras(Intent intent) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mHistoryType = extras.getInt(Constants.EXT_PARAM_HISTORY_TYPE);
        }
    }

    private void initUI() {
        String itemTitle = null;
        if ( mHistoryType == HistoryFragment.HISTORY_TYPE_DOWNLOAD ) {
            itemTitle = getResources().getString(R.string.drawer_title_downloaded_files_history);
        } else if ( mHistoryType == HistoryFragment.HISTORY_TYPE_UPLOAD ) {
            itemTitle = getResources().getString(R.string.drawer_title_uploaded_files_history);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mToolbar.setTitle(itemTitle);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                if (Constants.DEBUG) Log.d(TAG, "mHandler.handleMessage(): msg=" + msg);
            }
        };
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        showFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if ( mFragment != null && mFragment.backToParent() ) {
            return;
        }
        super.onBackPressed();
    }

    public HistoryFragment getFragment() {
        return mFragment;
    }

    private class CommitFragmentRunnable implements Runnable {

        private Fragment fragment;

        public CommitFragmentRunnable(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void run() {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        }
    }

    public void commitFragment(Fragment fragment) {
        //Using Handler class to avoid lagging while
        //committing fragment in same time as closing
        //navigation drawer
        mHandler.post(new CommitFragmentRunnable(fragment));
    }

    private void showFragment() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.EXT_PARAM_HISTORY_TYPE, mHistoryType);
        fragment.setArguments(args);
        commitFragment(fragment);
        mFragment = fragment;
    }

}
