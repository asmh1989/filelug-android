package com.filelug.android.ui.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import com.filelug.android.R;

/**
 * Created by Vincent Chang on 2015/12/3.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class WebViewDialogPreference extends DialogPreference {

    private WebView mWebView;
    private String mContentUrl;

    public WebViewDialogPreference(Context context) {
        this(context, null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WebViewDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WebViewDialogPreference, 0, 0);
        try {
            mContentUrl = a.getString(R.styleable.WebViewDialogPreference_contentUrl);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mContentUrl);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public CharSequence getDialogTitle() {
        return null;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected View onCreateDialogView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        return inflater.inflate(R.layout.layout_webview_dialog, null);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onBindDialogView(View view) {
        mWebView = (WebView) view.findViewById(R.id.webview);
    }

}
