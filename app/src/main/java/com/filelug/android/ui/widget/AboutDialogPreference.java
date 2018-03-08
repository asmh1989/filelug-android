package com.filelug.android.ui.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.filelug.android.R;
import com.filelug.android.util.MiscUtils;

/**
 * Created by Vincent Chang on 2015/12/7.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class AboutDialogPreference extends DialogPreference {

    private TextView mVersionName;

    public AboutDialogPreference(Context context) {
        this(context, null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AboutDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public CharSequence getDialogTitle() {
        return null;
    }

    @Override
    protected View onCreateDialogView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        return inflater.inflate(R.layout.layout_about, null);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onBindDialogView(View view) {
        mVersionName = (TextView) view.findViewById(R.id.flVersionName);
        String versionName = MiscUtils.getFilelugVersion(getContext());
        if ( versionName != null ) {
            String versionNameStr = String.format(getContext().getResources().getString(R.string.message_app_version), versionName);
            mVersionName.setText(versionNameStr);
        }
    }

}
