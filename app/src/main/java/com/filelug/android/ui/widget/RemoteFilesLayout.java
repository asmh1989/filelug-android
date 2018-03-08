package com.filelug.android.ui.widget;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.service.ContentType;
import com.filelug.android.ui.activity.ConfirmDownloadActivity;
import com.filelug.android.ui.activity.MainActivity;
import com.filelug.android.ui.activity.OpenFromFilelugActivity;
import com.filelug.android.ui.adapter.RemoteFilesAdapter;
import com.filelug.android.ui.adapter.RemoteParentFoldersAdapter;
import com.filelug.android.ui.model.RemoteFile;
import com.filelug.android.ui.model.RemoteFileObject;
import com.filelug.android.ui.viewHolder.SelectableViewHolder;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.FilelugUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.PrefUtils;
import com.filelug.android.util.RemoteFileUtils;
import com.filelug.android.util.SortUtils;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Vincent Chang on 2015/8/2.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class RemoteFilesLayout extends FrameLayout implements FileLayout {

	private static final String TAG = RemoteFilesLayout.class.getSimpleName();

	private int choiceMode = CHOICE_MODE_NONE;
	private String acceptedType = ContentType.ALL;
	private RemoteParentFoldersAdapter remoteParentFoldersAdapter = null;
	private Spinner sParentFolders = null;
	private RecyclerView filesListView = null;
	private RemoteFilesAdapter fileListAdapter = null;
	private TextView tvNoFiles = null;
	private FloatingActionButton mFloatingActionButton = null;
	private ActionMode mActionMode = null;
	private int fileSortBy = 0;
	private int fileSortType = 0;
	private List<RemoteFile> selectedFileList = new ArrayList<RemoteFile>();
	private AdapterView.OnItemSelectedListener itemSelectedListener = null;

	public RemoteFilesLayout(Context context) {
		this(context, null);
	}

	public RemoteFilesLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RemoteFilesLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
		initListView();
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RemoteFilesLayout, 0, 0);
		try {
			this.choiceMode = a.getInteger(R.styleable.RemoteFilesLayout_choiceMode, 0);
		} finally {
			a.recycle();
		}
		LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_files, this, true);
	}

	private void initListView() {

		Context context = getContext();

		this.remoteParentFoldersAdapter = new RemoteParentFoldersAdapter(context, new ArrayList<RemoteFile>());
		this.sParentFolders = (Spinner) this.findViewById(R.id.parentDirSpinner);
		this.sParentFolders.setAdapter(this.remoteParentFoldersAdapter);

		int remoteDirSortBy = PrefUtils.getRemoteDirSortBy();
		int remoteDirSortType = PrefUtils.getRemoteDirSortType();
		this.fileSortBy = remoteDirSortBy == -1 ? 0 : remoteDirSortBy;
		this.fileSortType = remoteDirSortType == -1 ? 0 : remoteDirSortType;

		SelectableViewHolder.ClickListener clickListener = new SelectableViewHolder.ClickListener() {
			@Override
			public void onItemClicked(int position) {
				list_onItemClick(position);
			}
			@Override
			public boolean onItemLongClicked(int position) {
				return list_onItemLongClick(position);
			}
		};
		this.fileListAdapter = new RemoteFilesAdapter(context, new ArrayList<RemoteFile>(), clickListener);

		this.filesListView = (RecyclerView) this.findViewById(R.id.file_recycler_view);
		this.filesListView.setAdapter(this.fileListAdapter);
		this.filesListView.setLayoutManager(new LinearLayoutManager(context));
//		this.filesListView.setLayoutManager(new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false));
//		this.filesListView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));
		this.filesListView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
		this.filesListView.setItemAnimator(new DefaultItemAnimator());

		this.tvNoFiles = (TextView) this.findViewById(R.id.noFiles);

		this.mFloatingActionButton = (FloatingActionButton) this.findViewById(R.id.fab);
		this.mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Context context = getContext();
				if ( selectedFileList == null || selectedFileList.size() <= 0 ) {
					MsgUtils.showToast(context, R.string.message_no_file_selected);
					return;
				}

				if ( context instanceof MainActivity) {
					Intent intent = new Intent(context, ConfirmDownloadActivity.class);
					intent.putExtra(Constants.EXT_PARAM_SELECTED_DOWNLOAD_FILES, selectedFileList.toArray(new RemoteFileObject[0]));
					((MainActivity)context).startActivityForResult(intent, Constants.REQUEST_DOWNLOAD_FILES);
				} else if ( context instanceof OpenFromFilelugActivity ) {
					((OpenFromFilelugActivity)context).downloadFilesToCache(selectedFileList);
				}

				if ( mActionMode != null ) {
					mActionMode.finish();
				}
			}
		});

		RemoteFileObject remoteRoot = RemoteFileUtils.getRemoteRoot(context);
		this.addSpinnerItem(remoteRoot);

		itemSelectedListener = new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				spinner_onItemSelected(parent, view, position, id);
			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};
		this.sParentFolders.setOnItemSelectedListener(itemSelectedListener);

	}

	private boolean canBeSelect(RemoteFile remoteFile) {

		boolean isSelect = false;

		if ( !remoteFile.isSelectable() ) {
			return isSelect;
		}

		RemoteFile.FileType type = remoteFile.getType();

		if ( this.choiceMode == CHOICE_MODE_ONE_FOLDER) {
			if ( type == RemoteFile.FileType.REMOTE_DIR ||
				 type == RemoteFile.FileType.REMOTE_WINDOWS_SHORTCUT_DIR ||
				 type == RemoteFile.FileType.REMOTE_UNIX_SYMBOLIC_LINK_DIR ||
				 type == RemoteFile.FileType.REMOTE_MAC_ALIAS_DIR ) {
				isSelect = true;
				SparseBooleanArray selectedIds = this.fileListAdapter.getSelectedIdArray();
				if ( selectedIds.size() > 0 ) {
					int selectedPosition = selectedIds.keyAt(0);
					adapterToggleSelection(selectedPosition);
				}
			}
		} else if ( this.choiceMode == CHOICE_MODE_MULTIPLE_FILES ) {
			isSelect = ( type == RemoteFile.FileType.REMOTE_FILE ||
						 type == RemoteFile.FileType.REMOTE_WINDOWS_SHORTCUT_FILE ||
						 type == RemoteFile.FileType.REMOTE_UNIX_SYMBOLIC_LINK_FILE ||
						 type == RemoteFile.FileType.REMOTE_MAC_ALIAS_FILE ||
						 type == RemoteFile.FileType.UNKNOWN
			);
		} else if ( this.choiceMode == CHOICE_MODE_SINGLE_FILE ) {
			if ( type == RemoteFile.FileType.REMOTE_FILE ||
				 type == RemoteFile.FileType.REMOTE_WINDOWS_SHORTCUT_FILE ||
				 type == RemoteFile.FileType.REMOTE_UNIX_SYMBOLIC_LINK_FILE ||
				 type == RemoteFile.FileType.REMOTE_MAC_ALIAS_FILE ||
				 type == RemoteFile.FileType.UNKNOWN ) {
				isSelect = true;
				SparseBooleanArray selectedIds = this.fileListAdapter.getSelectedIdArray();
				if ( selectedIds.size() > 0 ) {
					int selectedPosition = selectedIds.keyAt(0);
					RemoteFile selectedRemoteFile = this.fileListAdapter.getItem(selectedPosition);
					if ( !remoteFile.equals(selectedRemoteFile) ) {
						adapterToggleSelection(selectedPosition);
					}
				}
			}
		}

		return isSelect;

	}

	private boolean list_onItemLongClick(int position) {
		if ( position < 0 ) {
			return false;
		}

		RemoteFile remoteFile = this.fileListAdapter.getItem(position);
		if ( canBeSelect(remoteFile) ) {
			list_onItemSelected(position);
		}
		return true;
	}

	private void list_onItemClick(int position) {

		if ( position < 0 ) {
			return;
		}

		RemoteFile remoteFile = this.fileListAdapter.getItem(position);

		if ( mActionMode != null ) {
			if ( canBeSelect(remoteFile) ) {
				list_onItemSelected(position);
			}
			return;
		}

		if ( !remoteFile.isReadable() ) {
			String message = String.format(getResources().getString(R.string.message_folder_or_file_can_not_be_read), remoteFile.getDisplayName());
			MsgUtils.showInfoMessage(getContext(), message);
			return;
		}

		if ( remoteFile.getType().isDirectory() ) {
			doShowFolderFiles(remoteFile);
		} else {
//			doOpenFile(remoteFile);
			Context context = getContext();
			if ( context instanceof OpenFromFilelugActivity ) {
				if ( canBeSelect(remoteFile) ) {
					list_onItemSelected(position);
				}
			}
		}

	}

	private void list_onItemSelected(int position) {
		adapterToggleSelection(position);
		int selectedCount = this.fileListAdapter.getSelectedCount();
		boolean hasSelectedItems = selectedCount > 0;
		if (hasSelectedItems && mActionMode == null) {
			mActionMode = startActionMode(new ActionModeCallback());
		} else if (!hasSelectedItems && mActionMode != null) {
			mActionMode.finish();
		}
		showCABText();
	}

	private void adapterToggleSelection(int position) {
		RemoteFile remoteFile = this.fileListAdapter.getItem(position);
		boolean isSelect = this.fileListAdapter.toggleSelection(position);
		if ( isSelect ) {
			if ( !this.selectedFileList.contains(remoteFile) ) {
				this.selectedFileList.add(remoteFile);
			}
		} else {
			this.selectedFileList.remove(remoteFile);
		}
	}

	private void showCABText() {
		if (mActionMode != null) {
			Resources res = getContext().getResources();
			int selectedCount = this.fileListAdapter.getSelectedCount();
			mActionMode.setTitle(String.valueOf(selectedCount));
			adjustMenuItemStatus(mActionMode.getMenu());
		}
	}

	private class ActionModeCallback implements ActionMode.Callback {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			return list_onCreateActionMode(mode, menu);
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return list_onPrepareActionMode(mode, menu);
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return list_onActionItemClicked(mode, item);
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			list_onDestroyActionMode(mode);
		}

	}

	private boolean list_onCreateActionMode(ActionMode actionMode, Menu menu) {
		Activity parentActivity = (Activity)getContext();
		if ( parentActivity instanceof MainActivity) {
			MenuInflater menuInflater = parentActivity.getMenuInflater();
			menuInflater.inflate(R.menu.menu_browse_remote_file_cab, menu);
		} else if ( parentActivity instanceof OpenFromFilelugActivity ) {
			MenuInflater menuInflater = parentActivity.getMenuInflater();
			menuInflater.inflate(R.menu.menu_open_from_filelug_cab, menu);
			if ( this.choiceMode == CHOICE_MODE_SINGLE_FILE ) {
				menu.removeItem(R.id.action_select_all);
				menu.removeItem(R.id.action_invert_selection);
			}
		}
		this.mFloatingActionButton.setVisibility(View.VISIBLE);
		AnimatorSet animatorSet = (AnimatorSet)AnimatorInflater.loadAnimator(parentActivity, R.animator.fab_download_show);
		animatorSet.setTarget(this.mFloatingActionButton);
		animatorSet.start();
		_enableDisableSwipeRefresh(false);
		return true;
	}

	private boolean list_onPrepareActionMode(ActionMode actionMode, Menu menu) {
		adjustMenuItemStatus(menu);
		return true;
	}

	private void adjustMenuItemStatus(Menu menu) {
		int selectedCount = this.fileListAdapter.getSelectedCount();
		for ( int i = 0; i < menu.size(); i++ ) {
			boolean enable = true;
			MenuItem menuItem = menu.getItem(i);
			switch ( menuItem.getItemId() ) {
				case R.id.action_invert_selection:
					if ( selectedCount == 0 ) {
						enable = false;
					}
					break;
				case R.id.action_details:
				case R.id.action_open_file:
					if ( selectedCount != 1 ) {
						enable = false;
					}
					break;
				default:
					break;
			}
			menuItem.setVisible(enable);
		}
	}

	private boolean list_onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
		switch ( menuItem.getItemId() ) {
			case R.id.action_select_all:
				doSelectAll();
				break;
			case R.id.action_invert_selection:
				doInvertSelection();
				break;
			case R.id.action_details:
				if ( this.fileListAdapter.getSelectedCount() == 1 ) {
					SparseBooleanArray selectedIds = this.fileListAdapter.getSelectedIdArray();
					int position = selectedIds.keyAt(0);
					RemoteFile remoteFile = this.fileListAdapter.getItem(position);
					if ( remoteFile != null ) {
						doShowFileDetails(remoteFile);
					}
				}
				break;
			default:
				break;
		}
		return false;
	}

	private void list_onDestroyActionMode(ActionMode actionMode) {
		Activity parentActivity = (Activity)getContext();
		AnimatorSet animatorSet = (AnimatorSet)AnimatorInflater.loadAnimator(parentActivity, R.animator.fab_download_hide);
		animatorSet.setTarget(this.mFloatingActionButton);
		animatorSet.start();
		this.selectedFileList.clear();
		this.fileListAdapter.deselectAll();
		_enableDisableSwipeRefresh(true);
		mActionMode = null;
	}

	private void fillObjects(List<RemoteFile> remoteFileList) {
//		this.fileListAdapter.clear();
		if ( remoteFileList != null && remoteFileList.size() > 0 ) {
			Comparator<RemoteFile> comparator = getComparator();
			Collections.sort(remoteFileList, comparator);
			this.filesListView.setVisibility(View.VISIBLE);
			this.tvNoFiles.setVisibility(View.GONE);
		} else {
			this.filesListView.setVisibility(View.GONE);
			this.tvNoFiles.setVisibility(View.VISIBLE);
		}
		this.fileListAdapter.addAll(remoteFileList);
	}

	private Comparator<RemoteFile> getComparator() {

		Context context = getContext();
		Resources resources = context.getResources();
		Comparator<RemoteFile> comparator = null;

		if ( this.fileSortType == context.getResources().getInteger(R.integer.const_sort_type_ascending) ) {
			if ( this.fileSortBy == context.getResources().getInteger(R.integer.const_sort_by_name) ) {
				comparator = new SortUtils.RemoteFileNameAscComparator();
			} else if ( this.fileSortBy == context.getResources().getInteger(R.integer.const_sort_by_date) ) {
				comparator = new SortUtils.RemoteFileDateAscComparator();
			} else if ( this.fileSortBy == context.getResources().getInteger(R.integer.const_sort_by_size) ) {
				comparator = new SortUtils.RemoteFileSizeAscComparator();
			} else if ( this.fileSortBy == context.getResources().getInteger(R.integer.const_sort_by_type) ) {
				comparator = new SortUtils.RemoteFileTypeAscComparator();
			}
		} else {
			if ( this.fileSortBy == context.getResources().getInteger(R.integer.const_sort_by_name) ) {
				comparator = new SortUtils.RemoteFileNameDescComparator();
			} else if ( this.fileSortBy == context.getResources().getInteger(R.integer.const_sort_by_date) ) {
				comparator = new SortUtils.RemoteFileDateDescComparator();
			} else if ( this.fileSortBy == context.getResources().getInteger(R.integer.const_sort_by_size) ) {
				comparator = new SortUtils.RemoteFileSizeDescComparator();
			} else if ( this.fileSortBy == context.getResources().getInteger(R.integer.const_sort_by_type) ) {
				comparator = new SortUtils.RemoteFileTypeDescComparator();
			}
		}

		return comparator;

	}

	private void addSpinnerItem(RemoteFile remoteFile) {
//		if (Constants.DEBUG) Log.d(TAG, "addSpinnerItem(): remoteFile=" + remoteFile.getName());
		remoteParentFoldersAdapter.add(remoteFile);
		sParentFolders.setSelection(remoteParentFoldersAdapter.getCount() > 0 ? remoteParentFoldersAdapter.getCount() - 1 : 0, true);
	}

	private void spinner_onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//		if (Constants.DEBUG) Log.d(TAG, "spinner_onItemSelected(): position=" + position + ", id=" + id);
		if ( mActionMode != null ) {
			mActionMode.finish();
		}
		int itemCount = this.remoteParentFoldersAdapter.getCount();
		if ( itemCount > 0 ) {
			for ( int i=itemCount-1; i>position; i-- ) {
				RemoteFile remoteItem = this.remoteParentFoldersAdapter.getItem(i);
				this.remoteParentFoldersAdapter.remove(remoteItem);
			}
		}
		doRefreshAction();
	}

	private void _onRefreshingStateChanged(boolean refreshing) {
//		if (Constants.DEBUG) Log.d(TAG, "_onRefreshingStateChanged(): refreshing=" + refreshing);
		Context context = getContext();
		if ( context instanceof MainActivity) {
			((MainActivity)context).getFragment().onRefreshingStateChanged(refreshing);
		} else if ( context instanceof OpenFromFilelugActivity ) {
			((OpenFromFilelugActivity)context).onRefreshingStateChanged(refreshing);
		}
	}

	private void _enableDisableSwipeRefresh(boolean enable) {
		Context context = getContext();
		if ( context instanceof MainActivity) {
			((MainActivity)context).getFragment().enableDisableSwipeRefresh(enable);
		} else if ( context instanceof OpenFromFilelugActivity ) {
			((OpenFromFilelugActivity)context).enableDisableSwipeRefresh(enable);
		}
	}

	// doRefreshAction ==>
	private void pingDesktop(final RemoteFile remoteFile) {
//		if (Constants.DEBUG) Log.d(TAG, "pingDesktop(): remoteFile=" + remoteFile.getFullName());
		FilelugUtils.Callback callback = new FilelugUtils.Callback() {
			@Override
			public void onError(int errorCode, String errorMessage) {
//				if (Constants.DEBUG) Log.d(TAG, "pingDesktop() --> onError(): errorCode=" + errorCode + ", errorMessage=" + errorMessage);
				_onRefreshingStateChanged(false);
			}
			@Override
			public void onSuccess(Bundle result) {
//				if (Constants.DEBUG) Log.d(TAG, "pingDesktop() --> onSuccess()");
				showFiles(remoteFile);
			}
		};
		this.fileListAdapter.removeAll();
		Activity parentActivity = (Activity)getContext();
		if ( !FilelugUtils.pingDesktopB(parentActivity, callback) ) {
			_onRefreshingStateChanged(false);
		}
	}

	private void showFiles(final RemoteFile remoteFile) {
//		if (Constants.DEBUG) Log.d(TAG, "showFiles(): remoteFile=" + remoteFile.getFullName());
		_onRefreshingStateChanged(true);
//		this.fileListAdapter.removeAll();
		RemoteFileUtils.FileObjectListCallback callback = new RemoteFileUtils.FileObjectListCallback() {
			@Override
			public void loaded(boolean success, List<RemoteFile> fileObjectList) {
//				if (Constants.DEBUG) Log.d(TAG, "showFiles() --> loaded(): fileObjectList=" + fileObjectList.size());
				_onRefreshingStateChanged(false);
				if ( success ) {
					fillObjects(fileObjectList);
				}
			}
		};
		RemoteFileUtils.findRemoteFileObjectList(RemoteFilesLayout.this.getContext(), remoteFile, false, this.acceptedType, callback);
	}

	public void doSortAction() {
		DialogUtils.SortSettingCallback callback = new DialogUtils.SortSettingCallback() {
			public void selected(int sortBy, int sortType) {
				RemoteFilesLayout.this.fileSortBy = sortBy;
				RemoteFilesLayout.this.fileSortType = sortType;
				RemoteFilesLayout.this.fileListAdapter.sort(getComparator());
				PrefUtils.setRemoteDirSortBy(sortBy);
				PrefUtils.setRemoteDirSortType(sortType);
			}
		};
		MaterialDialog dialog = DialogUtils.createSortSettingDialog(getContext(), this.fileSortBy, this.fileSortType, callback);
		dialog.show();
	}

	public void doRefreshAction() {
		boolean reloadRemoteRootDir = PrefUtils.isReloadRemoteRootDir();
//		if (Constants.DEBUG) Log.d(TAG, "doRefreshAction(): reloadRemoteRootDir=" + reloadRemoteRootDir);
		if ( reloadRemoteRootDir ) {
			PrefUtils.setReloadRemoteRootDir(false);
			int itemCount = this.remoteParentFoldersAdapter.getCount();
//			if (Constants.DEBUG) Log.d(TAG, "doRefreshAction(): itemCount=" + itemCount);
			if ( itemCount > 0 ) {
				sParentFolders.setSelection(0);
				if ( itemCount > 1 ) {
					return;
				}
			}
		}
		RemoteFileObject remoteFile = (RemoteFileObject) this.sParentFolders.getSelectedItem();
//		if (Constants.DEBUG) Log.d(TAG, "doRefreshAction(): remoteFile=" + remoteFile.getName());
		pingDesktop(remoteFile);
	}

	public void doResetUI() {
//		if (Constants.DEBUG) Log.d(TAG, "doResetUI()");
		this.sParentFolders.setOnItemSelectedListener(null);
		int itemCount = this.remoteParentFoldersAdapter.getCount();
		if ( itemCount > 1 ) {
			for ( int i=itemCount-1; i>0; i-- ) {
				RemoteFile remoteItem = this.remoteParentFoldersAdapter.getItem(i);
				this.remoteParentFoldersAdapter.remove(remoteItem);
			}
		}
		this.sParentFolders.setSelection(0);
		this.sParentFolders.setOnItemSelectedListener(this.itemSelectedListener);
	}

	public boolean backToParent() {
		boolean isBackTo = false;
		int itemCount = this.remoteParentFoldersAdapter.getCount();
		if ( itemCount > 1 ) {
			sParentFolders.setSelection(itemCount-2);
			isBackTo = true;
		} else if ( mActionMode != null ) {
			mActionMode.finish();
		}
		return isBackTo;
	}

	private void doSelectAll() {
		SparseBooleanArray selectedIds = this.fileListAdapter.getSelectedIdArray();
		for( int i=0; i<this.fileListAdapter.getItemCount(); i++ ) {
			RemoteFile remoteFile = this.fileListAdapter.getItem(i);
			if ( canBeSelect(remoteFile) ) {
				boolean checked = !selectedIds.get(i);
				if ( checked ) {
					adapterToggleSelection(i);
				}
			}
		}
		showCABText();
	}

	private void doInvertSelection() {
		for( int i=0; i<this.fileListAdapter.getItemCount(); i++ ) {
			RemoteFile remoteFile = this.fileListAdapter.getItem(i);
			if ( canBeSelect(remoteFile) ) {
				adapterToggleSelection(i);
			}
		}
		if ( this.fileListAdapter.getSelectedCount() == 0 ) {
			mActionMode.finish();
		}
		showCABText();
	}

	private void doShowFolderFiles(RemoteFile remoteFile) {
		addSpinnerItem(remoteFile);
	}

	private void doShowFileDetails(RemoteFile remoteFile) {
		MaterialDialog dialog = DialogUtils.createRemoteFileObjectDetailDialog(getContext(), remoteFile);
		dialog.show();
	}

	@Override
	public int getChoiceMode() {
		return this.choiceMode;
	}

	@Override
	public void setChoiceMode(int choiceMode) {
		this.choiceMode = choiceMode;
	}

	public String getAcceptedType() {
		return this.acceptedType;
	}

	public void setAcceptedType(String acceptedType) {
		this.acceptedType = acceptedType;
	}
}
