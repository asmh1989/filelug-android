package com.filelug.android.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.service.ContentType;
import com.filelug.android.ui.adapter.CustomMaterialSimpleListAdapter;
import com.filelug.android.ui.model.CustomMaterialSimpleListItem;
import com.filelug.android.ui.model.RemoteFile;
import com.filelug.android.ui.model.RemoteFileObject;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.NetworkUtils;
import com.filelug.android.util.RemoteFileUtils;
import com.filelug.android.util.SortUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RemoteFolderChooser extends DialogFragment implements CustomMaterialSimpleListAdapter.Callback {

	private final String TAG = RemoteFolderChooser.class.getSimpleName();

	private MaterialDialog mDialog = null;
	private CustomMaterialSimpleListAdapter mAdapter = null;
	private List<RemoteFile> mParentFolderList;
	private RemoteFile mPath;
	private boolean mSaveAsDefault = false;
	private RemoteFile[] mSubFolders;
	private boolean mCanGoUp = true;
	private FolderSelectCallback mCallback;

	public interface FolderSelectCallback {
		void onFolderSelection(RemoteFile folder, boolean saveAsDefault);
	}

	public RemoteFolderChooser() {
		super();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Activity activity = getActivity();

		Bundle args = this.getArguments();
		if ( args != null ) {
			String currentFolderStr = args.getString(Constants.EXT_PARAM_CURRENT_FOLDER);
			if ( currentFolderStr != null ) {
//				File tmp = new File(currentFolderStr);
//				if ( tmp.isDirectory() ) {
//					mPath = tmp;
//				}
			}
			mSaveAsDefault = args.getBoolean(Constants.EXT_PARAM_IS_SAVE_AS_DEFAULT, false);
		}

		mPath = RemoteFileUtils.getRemoteRoot(activity);
		mParentFolderList = new ArrayList<RemoteFile>();
		mParentFolderList.add(mPath);
		mCanGoUp = false;

		mAdapter = new CustomMaterialSimpleListAdapter(this);

		mDialog = DialogUtils.createFolderDialog(
			activity,
			mPath.getFullName(),
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
					reloadFolderData();
				}
			}
		);
		mAdapter.setDialog(mDialog);

		reloadFolderData();

		return mDialog;
	}

	@Override
	public void onMaterialListItemSelected(MaterialDialog dialog, int index, CustomMaterialSimpleListItem item) {
		if (mCanGoUp && index == 0) {
			mParentFolderList.remove(mParentFolderList.size()-1);
			mPath = mParentFolderList.get(mParentFolderList.size()-1);
			mCanGoUp = mParentFolderList.size() > 1;
		} else {
			mPath = mSubFolders[mCanGoUp ? index - 1 : index];
			mParentFolderList.add(mPath);
			mCanGoUp = true;
		}
		dialog.setTitle(mPath.getFullName());
		reloadFolderData();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback = (FolderSelectCallback) activity;
	}

	public void show(AppCompatActivity context) {
		show(context.getSupportFragmentManager(), "FOLDER_SELECTOR");
	}

	private void reloadFolderData() {
		final Activity activity = getActivity();
		if ( !NetworkUtils.isNetworkAvailable(activity) ) {
			return;
		} else if ( AccountUtils.getActiveAccount() == null) {
			MsgUtils.showToast(activity, R.string.message_registered_computer_not_found);
			return;
		}

		setButtonState(mDialog, 0);

		mAdapter.clear();

		RemoteFileUtils.FileObjectListCallback callback = new RemoteFileUtils.FileObjectListCallback() {
			@Override
			public void loaded(boolean success, List<RemoteFile> fileObjectList) {
				if ( success ) {
					// Back item
					if ( mCanGoUp ) {
						mAdapter.add(
							new CustomMaterialSimpleListItem.
								Builder(activity).
								content(getResources().getString(R.string.message_back_to_parent_folder)).
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
					mSubFolders = fileObjectList.toArray(new RemoteFileObject [0]);
					for ( int i=0; i<mSubFolders.length; i++ ) {
						RemoteFile subFolder = mSubFolders[i];
						int iconRes = MiscUtils.getIconResourceIdByRemoteFileType(subFolder.getType());
						mAdapter.add(
							new CustomMaterialSimpleListItem.
								Builder(activity).
								content(subFolder.getName()).
								icon(iconRes != -1 ? iconRes : R.drawable.ic_folder).
								build()
						);
					}
					if ( mParentFolderList.size() > 1 ) {
						setButtonState(mDialog, 1);
					} else {
						setButtonState(mDialog, -1);
					}
				} else {
					setButtonState(mDialog, -1);
				}

				mAdapter.notifyDataSetChanged();
			}
		};
		RemoteFileUtils.findRemoteFileObjectList(activity, mPath, true, ContentType.ALL, callback);

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

}
