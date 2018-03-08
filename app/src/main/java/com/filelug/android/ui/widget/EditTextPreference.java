package com.filelug.android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.prefs.MaterialEditTextPreference;

// Copy from com.afollestad.materialdialogs.prefs.MaterialEditTextPreference 0.8.5.9
/**
 * Created by Vincent Chang on 2015/11/25.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class EditTextPreference extends MaterialEditTextPreference {

    public EditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextPreference(Context context) {
        super(context);
    }

    @Override
    public void setLayoutResource(int layoutResId) {
//        super.setLayoutResource(layoutResId);
    }

}
