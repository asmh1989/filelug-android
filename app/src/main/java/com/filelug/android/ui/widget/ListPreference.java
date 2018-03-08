package com.filelug.android.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.prefs.MaterialListPreference;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.PrefUtils;
import com.filelug.android.util.Validation;

import java.lang.reflect.Method;

// Copy from com.afollestad.materialdialogs.prefs.MaterialListPreference 0.8.5.9
/**
 * Created by Vincent Chang on 2015/11/25.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class ListPreference extends MaterialListPreference {

    private Context mContext;
    private MaterialDialog mDialog;

    public ListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ListPreference(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void setLayoutResource(int layoutResId) {
//        super.setLayoutResource(layoutResId);
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    @Override
    public Dialog getDialog() {
        return mDialog;
    }

    @Override
    protected void showDialog(Bundle state) {
        String key = getKey();
        MaterialDialog.Builder builder = null;

        if ( mContext.getResources().getString(R.string.pref_upload_subdir).equals(key) ) {
            builder = createUploadSubdirTypeDialogBuilder();
        } else if ( mContext.getResources().getString(R.string.pref_upload_description_type).equals(key) ) {
            builder = createDescriptionTypeDialogBuilder(true);
        } else if ( mContext.getResources().getString(R.string.pref_upload_notification_type).equals(key) ) {
            builder = createNotificationTypeDialogBuilder(true);
        } else if ( mContext.getResources().getString(R.string.pref_download_subdir).equals(key) ) {
            builder = createDownloadSubdirTypeDialogBuilder();
        } else if ( mContext.getResources().getString(R.string.pref_download_description_type).equals(key) ) {
            builder = createDescriptionTypeDialogBuilder(false);
        } else if ( mContext.getResources().getString(R.string.pref_download_notification_type).equals(key) ) {
            builder = createNotificationTypeDialogBuilder(false);
        }

        final View contentView = onCreateDialogView();
        if (contentView != null) {
            onBindDialogView(contentView);
            builder.customView(contentView, false);
        } else {
            builder.content(getDialogMessage());
        }

        registerOnActivityDestroyListener(this, this);

        mDialog = builder.build();
        if (state != null)
            mDialog.onRestoreInstanceState(state);
        onClick(mDialog, DialogInterface.BUTTON_NEGATIVE);
        mDialog.show();
    }

    private MaterialDialog.Builder createUploadSubdirTypeDialogBuilder() {
        int subdirType = Integer.valueOf(getValue());

        MaterialDialog.ListCallbackSingleChoice callback = new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                setValue(String.valueOf(which));
                dialog.dismiss();
                if ( which >= 2 ) {
                    showUploadSubdirValueDialog();
                }
                return true;
            }
        };

        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
            .title(R.string.label_subfolder_type)
//			.iconRes(R.drawable.ic_new_folder)
//			.limitIconToDefaultSize()
            .items(getEntries())
            .itemsCallbackSingleChoice(subdirType, callback)
            .positiveText(DialogUtils.DIALOG_BUTTON_CHOOSE_RES);

        return builder;
    }

    private void showUploadSubdirValueDialog() {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source != null && Constants.INVALID_FILE_NAME_CHARACTERS.contains(("" + source))) {
                    return "";
                }
                return null;
            }
        };
        String customizedSubDirName = PrefUtils.getUploadSubdirValue(null);
        MaterialDialog.SingleButtonCallback singleButtonCallback = new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                EditText editText = dialog.getInputEditText();
                if ( !Validation.hasText(editText) ) {
                    editText.requestFocus();
                    return;
                }

                String newCustomizedSubDirName = editText.getText().toString().trim();
                PrefUtils.setUploadSubdirValue(null, newCustomizedSubDirName);

                InputMethodManager imm = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                dialog.dismiss();
            }
        };
        DialogUtils.createTextInputDialog(
            getContext(),
            R.string.label_subfolder_type,
            R.string.hint_enter_customized_name,
            customizedSubDirName,
            InputType.TYPE_CLASS_TEXT,
            false,
            new InputFilter[] {filter},
            singleButtonCallback
        ).show();
    }

    private MaterialDialog.Builder createDescriptionTypeDialogBuilder(final boolean isUpload) {
        int descriptionType = Integer.valueOf(getValue());

        MaterialDialog.ListCallbackSingleChoice callback = new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                setValue(String.valueOf(which));
                dialog.dismiss();
                if ( which >= 2 ) {
                    showDescriptionValueDialog(isUpload);
                }
                return true;
            }
        };

        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
            .title(isUpload ? R.string.label_upload_description_type : R.string.label_download_description_type)
//			.iconRes(R.drawable.ic_note)
//			.limitIconToDefaultSize()
            .items(getEntries())
            .itemsCallbackSingleChoice(descriptionType, callback)
            .positiveText(DialogUtils.DIALOG_BUTTON_CHOOSE_RES);

        return builder;
    }

    private void showDescriptionValueDialog(boolean isUpload) {
        String customizedDescription = PrefUtils.getUploadDescriptionValue(null);
        MaterialDialog.SingleButtonCallback singleButtonCallback = new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                EditText editText = dialog.getInputEditText();
                if ( !Validation.hasText(editText) ) {
                    editText.requestFocus();
                    return;
                }

                String newCustomizedDescription = editText.getText().toString().trim();
                PrefUtils.setUploadDescriptionValue(null, newCustomizedDescription);

                InputMethodManager imm = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                dialog.dismiss();
            }
        };
        DialogUtils.createTextInputDialog(
            getContext(),
            isUpload ? R.string.label_upload_description_type : R.string.label_download_description_type,
            R.string.hint_enter_customized_description,
            customizedDescription,
            InputType.TYPE_CLASS_TEXT,
            false,
            null,
            singleButtonCallback
        ).show();
    }

    private MaterialDialog.Builder createNotificationTypeDialogBuilder(boolean isUpload) {
        int notificationType = Integer.valueOf(getValue());

        MaterialDialog.ListCallbackSingleChoice callback = new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                setValue(String.valueOf(which));
                dialog.dismiss();
                return true;
            }
        };

        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
            .title(isUpload ? R.string.label_upload_description_type : R.string.label_download_description_type)
//			.iconRes(R.drawable.ic_notifications)
//			.limitIconToDefaultSize()
            .items(getEntries())
            .itemsCallbackSingleChoice(notificationType, callback)
            .positiveText(DialogUtils.DIALOG_BUTTON_CHOOSE_RES);

        return builder;
    }

    private MaterialDialog.Builder createDownloadSubdirTypeDialogBuilder() {
        int subdirType = Integer.valueOf(getValue());

        MaterialDialog.ListCallbackSingleChoice callback = new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                setValue(String.valueOf(which));
                dialog.dismiss();
                if ( which >= 2 ) {
                    showDownloadSubdirValueDialog();
                }
                return true;
            }
        };

        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
                .title(R.string.label_subfolder_type)
//			.iconRes(R.drawable.ic_new_folder)
//			.limitIconToDefaultSize()
                .items(getEntries())
                .itemsCallbackSingleChoice(subdirType, callback)
                .positiveText(DialogUtils.DIALOG_BUTTON_CHOOSE_RES);

        return builder;
    }

    private void showDownloadSubdirValueDialog() {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source != null && Constants.INVALID_FILE_NAME_CHARACTERS.contains(("" + source))) {
                    return "";
                }
                return null;
            }
        };
        String customizedSubDirName = PrefUtils.getDownloadSubdirValue(null);
        MaterialDialog.SingleButtonCallback singleButtonCallback = new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                EditText editText = dialog.getInputEditText();
                if ( !Validation.hasText(editText) ) {
                    editText.requestFocus();
                    return;
                }

                String newCustomizedSubDirName = editText.getText().toString().trim();
                PrefUtils.setDownloadSubdirValue(null, newCustomizedSubDirName);

                InputMethodManager imm = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                dialog.dismiss();
            }
        };
        DialogUtils.createTextInputDialog(
                getContext(),
                R.string.label_subfolder_type,
                R.string.hint_enter_customized_name,
                customizedSubDirName,
                InputType.TYPE_CLASS_TEXT,
                false,
                new InputFilter[] {filter},
                singleButtonCallback
        ).show();
    }

    public static void registerOnActivityDestroyListener(@NonNull Preference preference, @NonNull PreferenceManager.OnActivityDestroyListener listener) {
        try {
            PreferenceManager pm = preference.getPreferenceManager();
            Method method = pm.getClass().getDeclaredMethod(
                "registerOnActivityDestroyListener",
                PreferenceManager.OnActivityDestroyListener.class);
            method.setAccessible(true);
            method.invoke(pm, listener);
        } catch (Exception ignored) {
        }
    }

}
