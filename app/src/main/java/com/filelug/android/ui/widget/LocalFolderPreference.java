package com.filelug.android.ui.widget;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.filelug.android.R;
import com.filelug.android.ui.adapter.CustomMaterialSimpleListAdapter;
import com.filelug.android.ui.model.CustomMaterialSimpleListItem;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.LocalFileUtils;
import com.filelug.android.util.NetworkUtils;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by Vincent Chang on 2015/11/26.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class LocalFolderPreference extends DialogPreference implements CustomMaterialSimpleListAdapter.Callback {

    private String mFolder;

    private Context mContext;
    private MaterialDialog mDialog;
    private CustomMaterialSimpleListAdapter mAdapter;
    private File mPath;
    private File[] mSubFolders;
    private boolean mCanGoUp = true;

    public LocalFolderPreference(Context context) {
        super(context);
        mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LocalFolderPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public Dialog getDialog() {
        return mDialog;
    }

    @Override
    protected void showDialog(Bundle state) {
        try {
            PreferenceManager pm = getPreferenceManager();
            Method method = pm.getClass().getDeclaredMethod(
                "registerOnActivityDestroyListener",
                PreferenceManager.OnActivityDestroyListener.class);
            method.setAccessible(true);
            method.invoke(pm, this);
        } catch (Exception ignored) {
        }

        String currentFolderStr = getPersistedString(null);
        if ( currentFolderStr != null ) {
				File tmp = new File(currentFolderStr);
				if ( tmp.isDirectory() ) {
					mPath = tmp;
				}
        }
        if ( mPath == null ) {
            mPath = Environment.getExternalStorageDirectory();
        }
        mCanGoUp = mPath.getParent() != null;

        mAdapter = new CustomMaterialSimpleListAdapter(this);

        mDialog = DialogUtils.createFolderDialog(
                mContext,
                mPath.getAbsolutePath(),
                mAdapter,
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        String newFolderStr = mPath.getAbsolutePath();
                        setFolder(newFolderStr);
                    }
                },
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (NetworkUtils.isNetworkAvailable(mContext, null)) {
                            reloadFolderData(dialog);
                        }
                    }
            }
        );

        if (state != null) {
            mDialog.onRestoreInstanceState(state);
        }
        mAdapter.setDialog(mDialog);
        reloadFolderData(mDialog);

        mDialog.show();
    }

    private void reloadFolderData(final MaterialDialog dialog) {
        setButtonState(dialog, 0);

        mAdapter.clear();

        LocalFileUtils.FileArrayCallback callback = new LocalFileUtils.FileArrayCallback() {
            @Override
            public void loaded(boolean success, File[] files) {
                if ( success ) {
                    // Back item
                    if ( mCanGoUp ) {
                        mAdapter.add(
                            new CustomMaterialSimpleListItem.
                                Builder(mContext).
                                content(mContext.getResources().getString(R.string.message_back_to_parent_folder)).
                                icon(R.drawable.ic_back_to_parent).
                                build()
                        );
                    }
                    // Folder items
                    mSubFolders = files;
                    if ( mSubFolders != null ) {
                        for ( int i=0; i<mSubFolders.length; i++ ) {
                            File subFolder = mSubFolders[i];
                            mAdapter.add(
                                new CustomMaterialSimpleListItem.
                                    Builder(mContext).
                                    content(subFolder.getName()).
                                    icon(R.drawable.ic_folder).
                                    build()
                            );
                        }
                    }
                    setButtonState(dialog, 1);
                } else {
                    setButtonState(dialog, -1);
                }

                mAdapter.notifyDataSetChanged();
            }
        };

        LocalFileUtils.findLocalFolderList(mPath, callback);
    }

    private void setButtonState(MaterialDialog dialog, int state) {
        if ( state == 0 ) { // Loading
            dialog.setActionButton(DialogAction.NEUTRAL, "");
            dialog.getActionButton(DialogAction.NEUTRAL).setEnabled(false);
            dialog.setActionButton(DialogAction.POSITIVE, R.string.btn_label_loading);
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        } else if ( state == 1 ){ // Loaded & have country data
            dialog.setActionButton(DialogAction.NEUTRAL, R.string.btn_label_reload);
            dialog.getActionButton(DialogAction.NEUTRAL).setEnabled(true);
            dialog.setActionButton(DialogAction.POSITIVE, R.string.btn_label_choose);
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
        } else if ( state == -1 ){ // No country data or failed
            dialog.setActionButton(DialogAction.NEUTRAL, R.string.btn_label_reload);
            dialog.getActionButton(DialogAction.NEUTRAL).setEnabled(true);
            dialog.setActionButton(DialogAction.POSITIVE, R.string.btn_label_choose);
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        }
    }

    @Override
    public void onMaterialListItemSelected(MaterialDialog dialog, int index, CustomMaterialSimpleListItem item) {
        if (mCanGoUp && index == 0) {
            mPath = mPath.getParentFile();
            mCanGoUp = mPath.getParent() != null;
        } else {
            mPath = mSubFolders[mCanGoUp ? index - 1 : index];
            mCanGoUp = true;
        }
        dialog.setTitle(mPath.getAbsolutePath());
        reloadFolderData(dialog);
    }

    public void setFolder(String folder) {
        final boolean wasBlocking = shouldDisableDependents();

        mFolder = folder;

        persistString(folder);

        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking);
        }
    }

    public String getFolder() {
        return mFolder;
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setFolder(restoreValue ? getPersistedString(mFolder) : (String) defaultValue);
    }

    @Override
    public boolean shouldDisableDependents() {
        return TextUtils.isEmpty(mFolder) || super.shouldDisableDependents();
    }

}
