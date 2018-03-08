package com.filelug.android.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.filelug.android.R;

/**
 * Created by Vincent Chang on 2015/12/27.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Removing ActionBar
        setContentView(R.layout.layout_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Class activityClass = PrefUtils.isShowInitialPage() ? InitialPageActivity.class : MainActivity.class;
//                Intent i = new Intent(SplashActivity.this, activityClass);
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 1000);
    }

}
