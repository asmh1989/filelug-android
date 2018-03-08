package com.filelug.android.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.prefs.MaterialDialogPreference;

// Copy from com.afollestad.materialdialogs.prefs.MaterialDialogPreference 0.8.5.9
/**
 * Created by Vincent Chang on 2015/11/25.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class DialogPreference extends MaterialDialogPreference {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogPreference(Context context) {
        super(context);
    }

    @Override
    public void setLayoutResource(int layoutResId) {
//        super.setLayoutResource(layoutResId);
    }

}
