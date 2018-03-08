package com.filelug.android.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.ui.adapter.CustomMaterialSimpleListAdapter;
import com.filelug.android.ui.model.CustomMaterialSimpleListItem;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.LocalFileUtils;
import com.filelug.android.util.NetworkUtils;

import java.io.File;

public class LocalFolderChooser extends DialogFragment implements CustomMaterialSimpleListAdapter.Callback {

	private CustomMaterialSimpleListAdapter mAdapter = null;
    private File mPath;
	private boolean mSaveAsDefault = false;
    private File[] mSubFolders;
    private boolean mCanGoUp = true;
    private FolderSelectCallback mCallback;

	public interface FolderSelectCallback {
		void onFolderSelection(File folder, boolean saveAsDefault);
	}

	public LocalFolderChooser() {
		super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		Context context = getActivity();

		Bundle args = this.getArguments();
		if ( args != null ) {
			String currentFolderStr = args.getString(Constants.EXT_PARAM_CURRENT_FOLDER);
			if ( currentFolderStr != null ) {
				File tmp = new File(currentFolderStr);
				if ( tmp.isDirectory() ) {
					mPath = tmp;
				}
			}
			mSaveAsDefault = args.getBoolean(Constants.EXT_PARAM_IS_SAVE_AS_DEFAULT, false);
		}
		if ( mPath == null ) {
			mPath = Environment.getExternalStorageDirectory();
		}
		mCanGoUp = mPath.getParent() != null;

        mAdapter = new CustomMaterialSimpleListAdapter(this);

		MaterialDialog dialog = DialogUtils.createFolderDialog(
			context,
			mPath.getAbsolutePath(),
			true,
			R.string.label_save_as_default,
			mSaveAsDefault,
			mAdapter,
			new MaterialDialog.SingleButtonCallback() {
				@Override
				public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
					boolean saveAsDefault = dialog.isPromptCheckBoxChecked();
					dialog.dismiss();
					mCallback.onFolderSelection(mPath, saveAsDefault);
				}
			},
			new MaterialDialog.SingleButtonCallback() {
				@Override
				public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
					if (NetworkUtils.isNetworkAvailable(getActivity(), null)) {
						reloadFolderData(dialog);
					}
				}
			}
		);
		mAdapter.setDialog(dialog);
		reloadFolderData(dialog);

		return dialog;
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

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (FolderSelectCallback) activity;
    }

	public void show(AppCompatActivity context) {
        show(context.getSupportFragmentManager(), "FOLDER_SELECTOR");
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
								Builder(getActivity()).
								content(getResources().getString(R.string.message_back_to_parent_folder)).
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
									Builder(getActivity()).
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

}
