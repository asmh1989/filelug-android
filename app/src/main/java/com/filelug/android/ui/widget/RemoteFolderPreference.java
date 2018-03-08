package com.filelug.android.ui.widget;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.filelug.android.R;
import com.filelug.android.service.ContentType;
import com.filelug.android.ui.adapter.CustomMaterialSimpleListAdapter;
import com.filelug.android.ui.model.CustomMaterialSimpleListItem;
import com.filelug.android.ui.model.RemoteFile;
import com.filelug.android.ui.model.RemoteFileObject;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.FilelugUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.NetworkUtils;
import com.filelug.android.util.RemoteFileUtils;
import com.filelug.android.util.SortUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Vincent Chang on 2015/11/27.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class RemoteFolderPreference extends DialogPreference implements CustomMaterialSimpleListAdapter.Callback {

    private String mFolder;

    private Context mContext;
    private MaterialDialog mDialog;
    private CustomMaterialSimpleListAdapter mAdapter = null;
    private List<RemoteFile> mParentFolderList;
    private RemoteFile mPath;
    private RemoteFile[] mSubFolders;
    private boolean mCanGoUp = true;

    public RemoteFolderPreference(Context context) {
        super(context);
        mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RemoteFolderPreference(Context context, AttributeSet attrs) {
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

        FilelugUtils.Callback callback = new FilelugUtils.Callback() {
            @Override
            public void onError(int errorCode, String errorMessage) {
            }
            @Override
            public void onSuccess(Bundle result) {
                initDialog();
            }
        };
        FilelugUtils.pingDesktopB(mContext, callback);
    }

    private void initDialog() {
        String currentFolderStr = getPersistedString(null);
        if ( currentFolderStr != null ) {
//            File tmp = new File(currentFolderStr);
//            if ( tmp.isDirectory() ) {
//                mPath = tmp;
//            }
        }

        mPath = RemoteFileUtils.getRemoteRoot(mContext);
        mParentFolderList = new ArrayList<RemoteFile>();
        mParentFolderList.add(mPath);
        mCanGoUp = false;

        mAdapter = new CustomMaterialSimpleListAdapter(this);

        mDialog = DialogUtils.createFolderDialog(
            mContext,
            mPath.getFullName(),
            mAdapter,
            new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.dismiss();
                    String newFolderStr = mPath.getFullRealName();
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
        mAdapter.setDialog(mDialog);
        reloadFolderData(mDialog);

        mDialog.show();
    }

    private void reloadFolderData(final MaterialDialog dialog) {
        setButtonState(dialog, 0);

        mAdapter.clear();

        RemoteFileUtils.FileObjectListCallback callback = new RemoteFileUtils.FileObjectListCallback() {
            @Override
            public void loaded(boolean success, List<RemoteFile> fileObjectList) {
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
                    // Sort by file name
                    if ( fileObjectList != null && fileObjectList.size() > 0 ) {
                        Comparator<RemoteFile> comparator = new SortUtils.RemoteFileNameAscComparator();
                        Collections.sort(fileObjectList, comparator);
                    }
                    // Folder items
                    mSubFolders = fileObjectList.toArray(new RemoteFileObject[0]);
                    for ( int i=0; i<mSubFolders.length; i++ ) {
                        RemoteFile subFolder = mSubFolders[i];
                        int iconRes = MiscUtils.getIconResourceIdByRemoteFileType(subFolder.getType());
                        mAdapter.add(
                            new CustomMaterialSimpleListItem.
                                Builder(mContext).
                                content(subFolder.getName()).
                                icon(iconRes != -1 ? iconRes : R.drawable.ic_folder).
                                build()
                        );
                    }
                    if ( mParentFolderList.size() > 1 ) {
                        setButtonState(dialog, 1);
                    } else {
                        setButtonState(dialog, -1);
                    }
                } else {
                    setButtonState(dialog, -1);
                }

                mAdapter.notifyDataSetChanged();
            }
        };
        RemoteFileUtils.findRemoteFileObjectList(mContext, mPath, true, ContentType.ALL, callback);

    }

    private void setButtonState(MaterialDialog dialog, int state) {
        if ( state == 0 ) { // Loading
            dialog.setActionButton(DialogAction.NEUTRAL, "");
            dialog.getActionButton(DialogAction.NEUTRAL).setEnabled(false);
            dialog.setActionButton(DialogAction.POSITIVE, R.string.btn_label_loading);
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
//			dialog.getListView().setVisibility(View.GONE);
        } else if ( state == 1 ){ // Loaded & have country data
            dialog.setActionButton(DialogAction.NEUTRAL, R.string.btn_label_reload);
            dialog.getActionButton(DialogAction.NEUTRAL).setEnabled(true);
            dialog.setActionButton(DialogAction.POSITIVE, R.string.btn_label_choose);
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
//			dialog.getListView().setVisibility(View.VISIBLE);
        } else if ( state == -1 ){ // No country data or failed
            dialog.setActionButton(DialogAction.NEUTRAL, R.string.btn_label_reload);
            dialog.getActionButton(DialogAction.NEUTRAL).setEnabled(true);
            dialog.setActionButton(DialogAction.POSITIVE, R.string.btn_label_choose);
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
//			dialog.getListView().setVisibility(View.GONE);
        }
    }

    @Override
    public void onMaterialListItemSelected(MaterialDialog dialog, int index, CustomMaterialSimpleListItem item) {
        if (mCanGoUp && index == 0) {
            mParentFolderList.remove(mParentFolderList.size() - 1);
            mPath = mParentFolderList.get(mParentFolderList.size()-1);
            mCanGoUp = mParentFolderList.size() > 1;
        } else {
            mPath = mSubFolders[mCanGoUp ? index - 1 : index];
            mParentFolderList.add(mPath);
            mCanGoUp = true;
        }
        dialog.setTitle(mPath.getFullName());
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
