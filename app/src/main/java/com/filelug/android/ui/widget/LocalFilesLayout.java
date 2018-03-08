package com.filelug.android.ui.widget;

import android.accounts.Account;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
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
import com.filelug.android.ui.activity.ConfirmUploadActivity;
import com.filelug.android.ui.activity.MainActivity;
import com.filelug.android.ui.adapter.LocalFilesAdapter;
import com.filelug.android.ui.adapter.LocalParentFoldersAdapter;
import com.filelug.android.ui.fragment.BaseFragment;
import com.filelug.android.ui.model.LocalFile;
import com.filelug.android.ui.model.LocalFileObject;
import com.filelug.android.ui.viewHolder.SelectableViewHolder;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.LocalFileUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.PrefUtils;
import com.filelug.android.util.SortUtils;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//import com.filelug.android.ui.activity.MainActivity;

public class LocalFilesLayout extends FrameLayout implements FileLayout {

	private static final String TAG = LocalFilesLayout.class.getSimpleName();

	private int choiceMode = CHOICE_MODE_NONE;
	private LocalParentFoldersAdapter localParentFoldersAdapter = null;
	private Spinner sParentFolders = null;
	private RecyclerView filesListView = null;
	private LocalFilesAdapter fileListAdapter = null;
	private TextView tvNoFiles = null;
	private FloatingActionButton mFloatingActionButton = null;
	private ActionMode mActionMode = null;
	private int fileSortBy = 0;
	private int fileSortType = 0;
	private List<LocalFile> selectedFileList = new ArrayList<LocalFile>();

	public LocalFilesLayout(Context context) {
		this(context, null);
	}

	public LocalFilesLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LocalFilesLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
		initListView();
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LocalFilesLayout, 0, 0);
		try {
			choiceMode = a.getInteger(R.styleable.LocalFilesLayout_choiceMode, 0);
		} finally {
			a.recycle();
		}
		LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_files, this, true);
	}

	private void initListView() {

		Context context = getContext();

		this.localParentFoldersAdapter = new LocalParentFoldersAdapter(context, new ArrayList<LocalFile>());
		this.sParentFolders = (Spinner) this.findViewById(R.id.parentDirSpinner);
		this.sParentFolders.setAdapter(this.localParentFoldersAdapter);
		this.sParentFolders.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				spinner_onItemSelected(parent, view, position, id);
			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		int localDirSortBy = PrefUtils.getLocalDirSortBy();
		int localDirSortType = PrefUtils.getLocalDirSortType();
		this.fileSortBy = localDirSortBy == -1 ? 0 : localDirSortBy;
		this.fileSortType = localDirSortType == -1 ? 0 : localDirSortType;

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
		this.fileListAdapter = new LocalFilesAdapter(context, new ArrayList<LocalFile>(), clickListener);

		this.filesListView = (RecyclerView) this.findViewById(R.id.file_recycler_view);
		this.filesListView.setAdapter(this.fileListAdapter);
		this.filesListView.setLayoutManager(new LinearLayoutManager(context));
//		this.filesListView.setLayoutManager(new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false));
//		this.filesListView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));
		this.filesListView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
		this.filesListView.setItemAnimator(new DefaultItemAnimator());

		this.tvNoFiles = (TextView) this.findViewById(R.id.noFiles);

		this.mFloatingActionButton = (FloatingActionButton) this.findViewById(R.id.fab);
		this.mFloatingActionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Context context = getContext();
				if ( selectedFileList == null || selectedFileList.size() <= 0 ) {
					MsgUtils.showToast(context, R.string.message_no_file_selected);
					return;
				}
				Account activeAccount = AccountUtils.getActiveAccount();
				if ( activeAccount == null ) {
					MsgUtils.showToast(context, R.string.message_login_then_upload);
					return;
				}
				Intent intent = new Intent(context, ConfirmUploadActivity.class);
				intent.putExtra(Constants.EXT_PARAM_SELECTED_UPLOAD_FILES, selectedFileList.toArray(new LocalFileObject[0]));
				((MainActivity) context).startActivityForResult(intent, Constants.REQUEST_UPLOAD_FILES);
				if ( mActionMode != null ) {
					mActionMode.finish();
				}
			}
		});

		LocalFileObject deviceRoot = LocalFileUtils.getDeviceRoot(context);
		this.addSpinnerItem(deviceRoot);

	}

	private boolean canBeSelect(LocalFile localFile) {

		boolean isSelect = false;

		LocalFile.FileType type = localFile.getType();

		if ( this.choiceMode == CHOICE_MODE_ONE_FOLDER) {
			if ( type == LocalFile.FileType.LOCAL_DIR ||
				 type == LocalFile.FileType.MEDIA_DIR ) {
				isSelect = true;
				SparseBooleanArray selectedIds = this.fileListAdapter.getSelectedIdArray();
				if ( selectedIds.size() > 0 ) {
					int selectedPosition = selectedIds.keyAt(0);
					adapterToggleSelection(selectedPosition);
				}
			}
		} else if ( this.choiceMode == CHOICE_MODE_MULTIPLE_FILES ) {
			isSelect = ( type == LocalFile.FileType.LOCAL_FILE ||
						 type == LocalFile.FileType.LOCAL_SYMBOLIC_LINK_FILE ||
						 type == LocalFile.FileType.MEDIA_FILE ||
						 type == LocalFile.FileType.UNKNOWN
			);
		} else if ( this.choiceMode == CHOICE_MODE_SINGLE_FILE ) {
			if ( type == LocalFile.FileType.LOCAL_FILE ||
				 type == LocalFile.FileType.LOCAL_SYMBOLIC_LINK_FILE ||
				 type == LocalFile.FileType.MEDIA_FILE ||
				 type == LocalFile.FileType.UNKNOWN ) {
				isSelect = true;
				SparseBooleanArray selectedIds = this.fileListAdapter.getSelectedIdArray();
				if ( selectedIds.size() > 0 ) {
					int selectedPosition = selectedIds.keyAt(0);
					adapterToggleSelection(selectedPosition);
				}
			}
		}

		return isSelect;

	}

	private boolean list_onItemLongClick(int position) {
		if ( position < 0 ) {
			return false;
		}

		LocalFile localFile = this.fileListAdapter.getItem(position);
		if ( canBeSelect(localFile) ) {
			list_onItemSelected(position);
		}
		return true;
	}

	private void list_onItemClick(int position) {

		if ( position < 0 ) {
			return;
		}

		LocalFile localFile = this.fileListAdapter.getItem(position);

		if ( mActionMode != null ) {
			if ( canBeSelect(localFile) ) {
				list_onItemSelected(position);
			}
			return;
		}

		if ( !localFile.isReadable() ) {
			String message = String.format(getResources().getString(R.string.message_folder_or_file_can_not_be_read), localFile.getDisplayName());
			MsgUtils.showInfoMessage(getContext(), message);
			return;
		}

		if ( localFile.getType().isDirectory() ) {
			doShowFolderFiles(localFile);
		} else {
			doOpenFile(localFile);
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
		LocalFile localFile = this.fileListAdapter.getItem(position);
		boolean isSelect = this.fileListAdapter.toggleSelection(position);
		if ( isSelect ) {
			if ( !this.selectedFileList.contains(localFile) ) {
				this.selectedFileList.add(localFile);
			}
		} else {
			this.selectedFileList.remove(localFile);
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
		MenuInflater menuInflater = parentActivity.getMenuInflater();
		menuInflater.inflate(R.menu.menu_browse_local_file_cab, menu);
		this.mFloatingActionButton.setVisibility(View.VISIBLE);
		AnimatorSet animatorSet = (AnimatorSet)AnimatorInflater.loadAnimator(parentActivity, R.animator.fab_upload_show);
		animatorSet.setTarget(this.mFloatingActionButton);
		animatorSet.start();
		getParentFragment().enableDisableSwipeRefresh(false);
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
			case R.id.action_open_file:
				if ( this.fileListAdapter.getSelectedCount() == 1 ) {
					SparseBooleanArray selectedIds = this.fileListAdapter.getSelectedIdArray();
					int position = selectedIds.keyAt(0);
					LocalFile localFile = this.fileListAdapter.getItem(position);
					if ( localFile != null ) {
						doOpenFile(localFile);
					}
				}
				break;
			case R.id.action_details:
				if ( this.fileListAdapter.getSelectedCount() == 1 ) {
					SparseBooleanArray selectedIds = this.fileListAdapter.getSelectedIdArray();
					int position = selectedIds.keyAt(0);
					LocalFile localFile = this.fileListAdapter.getItem(position);
					if ( localFile != null ) {
						doShowFileDetails(localFile);
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
		AnimatorSet animatorSet = (AnimatorSet)AnimatorInflater.loadAnimator(parentActivity, R.animator.fab_upload_hide);
		animatorSet.setTarget(this.mFloatingActionButton);
		animatorSet.start();
		this.selectedFileList.clear();
		this.fileListAdapter.deselectAll();
		getParentFragment().enableDisableSwipeRefresh(true);
		mActionMode = null;
	}

	// Origin
/*
	private void fillObjects(List<LocalFile> localFileList) {
		this.fileListAdapter.clear();
		if ( localFileList != null && localFileList.size() > 0 ) {
			Comparator<LocalFile> comparator = getComparator();
			Collections.sort(localFileList, comparator);
			this.filesListView.setVisibility(View.VISIBLE);
			this.tvNoFiles.setVisibility(View.GONE);
		} else {
			this.filesListView.setVisibility(View.GONE);
			this.tvNoFiles.setVisibility(View.VISIBLE);
		}
		this.fileListAdapter.addAll(localFileList);
		this.fileListAdapter.notifyDataSetChanged();
	}
*/
	// Rewrite like LocalFilesLayout
	private void fillObjects(List<LocalFile> localFileList) {
//		this.fileListAdapter.clear();
		if ( localFileList != null && localFileList.size() > 0 ) {
			Comparator<LocalFile> comparator = getComparator();
			Collections.sort(localFileList, comparator);
			this.filesListView.setVisibility(View.VISIBLE);
			this.tvNoFiles.setVisibility(View.GONE);
		} else {
			this.filesListView.setVisibility(View.GONE);
			this.tvNoFiles.setVisibility(View.VISIBLE);
		}
		this.fileListAdapter.addAll(localFileList);
	}

	private Comparator<LocalFile> getComparator() {

		Context context = getContext();
		Resources resources = context.getResources();
		Comparator<LocalFile> comparator = null;

		if ( this.fileSortType == context.getResources().getInteger(R.integer.const_sort_type_ascending) ) {
			if ( this.fileSortBy == context.getResources().getInteger(R.integer.const_sort_by_name) ) {
				comparator = new SortUtils.LocalFileNameAscComparator();
			} else if ( this.fileSortBy == context.getResources().getInteger(R.integer.const_sort_by_date) ) {
				comparator = new SortUtils.LocalFileDateAscComparator();
			} else if ( this.fileSortBy == context.getResources().getInteger(R.integer.const_sort_by_size) ) {
				comparator = new SortUtils.LocalFileSizeAscComparator();
			} else if ( this.fileSortBy == context.getResources().getInteger(R.integer.const_sort_by_type) ) {
				comparator = new SortUtils.LocalFileTypeAscComparator();
			}
		} else {
			if ( this.fileSortBy == context.getResources().getInteger(R.integer.const_sort_by_name) ) {
				comparator = new SortUtils.LocalFileNameDescComparator();
			} else if ( this.fileSortBy == context.getResources().getInteger(R.integer.const_sort_by_date) ) {
				comparator = new SortUtils.LocalFileDateDescComparator();
			} else if ( this.fileSortBy == context.getResources().getInteger(R.integer.const_sort_by_size) ) {
				comparator = new SortUtils.LocalFileSizeDescComparator();
			} else if ( this.fileSortBy == context.getResources().getInteger(R.integer.const_sort_by_type) ) {
				comparator = new SortUtils.LocalFileTypeDescComparator();
			}
		}

		return comparator;

	}

	private void addSpinnerItem(LocalFile localFile) {
		localParentFoldersAdapter.add(localFile);
		sParentFolders.setSelection(localParentFoldersAdapter.getCount() > 0 ? localParentFoldersAdapter.getCount() - 1 : 0, true);
	}

	private void spinner_onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if ( mActionMode != null ) {
			mActionMode.finish();
		}
		int itemCount = this.localParentFoldersAdapter.getCount();
		if ( itemCount > 0 ) {
			for ( int i=itemCount-1; i>position; i-- ) {
				LocalFile localItem = this.localParentFoldersAdapter.getItem(i);
				this.localParentFoldersAdapter.remove(localItem);
			}
		}
		LocalFile localFile = this.localParentFoldersAdapter.getItem(position);
		showFiles(localFile);
	}

	private BaseFragment getParentFragment() {
		return ((MainActivity)getContext()).getFragment();
	}

	// Origin
/*
	private void showFiles(final LocalFile localFile) {
		parentFragment.onRefreshingStateChanged(true);
		LocalFileUtils.FileObjectListCallback callback = new LocalFileUtils.FileObjectListCallback() {
			@Override
			public void loaded(boolean success, List<LocalFile> fileObjectList) {
				fillObjects(fileObjectList);
				parentFragment.onRefreshingStateChanged(false);
			}
		};
		LocalFileUtils.findLocalFileObjectList(LocalFilesLayout.this.getContext(), localFile, callback);
	}
*/
	// Rewrite like LocalFilesLayout
	private void showFiles(final LocalFile localFile) {
		final BaseFragment parentFragment = getParentFragment();
		parentFragment.onRefreshingStateChanged(true);
		this.fileListAdapter.removeAll();
		LocalFileUtils.FileObjectListCallback callback = new LocalFileUtils.FileObjectListCallback() {
			@Override
			public void loaded(boolean success, List<LocalFile> fileObjectList) {
				parentFragment.onRefreshingStateChanged(false);
				if ( success ) {
					fillObjects(fileObjectList);
				}
			}
		};
		LocalFileUtils.findLocalFileObjectList(LocalFilesLayout.this.getContext(), localFile, callback);
	}

	public void doSortAction() {
		DialogUtils.SortSettingCallback callback = new DialogUtils.SortSettingCallback() {
			public void selected(int sortBy, int sortType) {
				LocalFilesLayout.this.fileSortBy = sortBy;
				LocalFilesLayout.this.fileSortType = sortType;
				LocalFilesLayout.this.fileListAdapter.sort(getComparator());
				PrefUtils.setLocalDirSortBy(sortBy);
				PrefUtils.setLocalDirSortType(sortType);
			}
		};
		MaterialDialog dialog = DialogUtils.createSortSettingDialog(getContext(), this.fileSortBy, this.fileSortType, callback);
		dialog.show();
	}

	public void doRefreshAction() {
		boolean showLocalSystemFolder = PrefUtils.isShowLocalSystemFolder();
		if ( !showLocalSystemFolder ) {
			int itemCount = this.localParentFoldersAdapter.getCount();
			if ( itemCount >= 2 ) {
				LocalFile localItem = this.localParentFoldersAdapter.getItem(1);
				if ( localItem.getType() == LocalFile.FileType.SYS_DIR_DEVICE_ROOT ) {
					sParentFolders.setSelection(0);
					return;
				}
			}
		}
		LocalFileObject localFile = (LocalFileObject) this.sParentFolders.getSelectedItem();
		showFiles(localFile);
	}

	public boolean backToParent() {
		boolean isBackTo = false;
		int itemCount = this.localParentFoldersAdapter.getCount();
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
			LocalFile localFile = this.fileListAdapter.getItem(i);
			if ( canBeSelect(localFile) ) {
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
			LocalFile localFile = this.fileListAdapter.getItem(i);
			if ( canBeSelect(localFile) ) {
				adapterToggleSelection(i);
			}
		}
		if ( this.fileListAdapter.getSelectedCount() == 0 ) {
			mActionMode.finish();
		}
		showCABText();
	}

	private void doShowFolderFiles(LocalFile localFile) {
		addSpinnerItem(localFile);
	}

	private void doOpenFile(LocalFile localFile) {
		Activity activity = (Activity)getContext();
		String mimeType = localFile.getContentType();
		String fullName = localFile.getFullName();
		MiscUtils.openFile(activity, mimeType, fullName);
	}

	private void doShowFileDetails(LocalFile localFile) {
		MaterialDialog dialog = DialogUtils.createLocalFileObjectDetailDialog(getContext(), localFile);
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

}
