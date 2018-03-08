package com.filelug.android.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filelug.android.R;
import com.filelug.android.ui.model.RemoteFile;
import com.filelug.android.ui.viewHolder.RemoteFileViewHolder;
import com.filelug.android.ui.viewHolder.RemoteFolderViewHolder;
import com.filelug.android.ui.viewHolder.SelectableViewHolder;
import com.filelug.android.ui.viewHolder.SystemFolderViewHolder;
import com.filelug.android.util.MiscUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Vincent Chang on 2015/8/2.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class RemoteFilesAdapter extends RecyclerView.Adapter {

	private static final int ROW_TYPE_SYSTEM_FOLDER_OBJECT = 0;
	private static final int ROW_TYPE_FOLDER_OBJECT = 1;
	private static final int ROW_TYPE_FILE_OBJECT = 2;

	private Context mContext = null;
	private List<RemoteFile> mRemoteFiles = null;
	private SparseBooleanArray mSelectedItemsIds;
	private SelectableViewHolder.ClickListener mClickListener = null;

	public RemoteFilesAdapter(Context context, List<RemoteFile> remoteFiles, SelectableViewHolder.ClickListener clickListener) {
		mContext = context;
		mRemoteFiles = remoteFiles;
		mClickListener = clickListener;
		this.mSelectedItemsIds = new SparseBooleanArray();
	}

	@Override
	public int getItemViewType(int position) {
		int viewType = 0;
		RemoteFile remoteFile = getItem(position);
		RemoteFile.FileType fileType = remoteFile.getType();
		if ( fileType == RemoteFile.FileType.REMOTE_DIR ||
			 fileType == RemoteFile.FileType.REMOTE_WINDOWS_SHORTCUT_DIR ||
			 fileType == RemoteFile.FileType.REMOTE_UNIX_SYMBOLIC_LINK_DIR ||
			 fileType == RemoteFile.FileType.REMOTE_MAC_ALIAS_DIR ) {
			viewType = ROW_TYPE_FOLDER_OBJECT;
		} else if ( fileType == RemoteFile.FileType.REMOTE_FILE ||
					fileType == RemoteFile.FileType.REMOTE_WINDOWS_SHORTCUT_FILE ||
					fileType == RemoteFile.FileType.REMOTE_UNIX_SYMBOLIC_LINK_FILE ||
					fileType == RemoteFile.FileType.REMOTE_MAC_ALIAS_FILE ||
					fileType == RemoteFile.FileType.UNKNOWN ) {
			viewType = ROW_TYPE_FILE_OBJECT;
		} else {
			viewType = ROW_TYPE_SYSTEM_FOLDER_OBJECT;
		}
		return viewType;
	}

	public RemoteFile getItem(int position) {
		return mRemoteFiles.get(position);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = null;
		RecyclerView.ViewHolder vh = null;
		if (viewType == ROW_TYPE_SYSTEM_FOLDER_OBJECT) {
			itemView = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.rowitem_filelist_systemfolder, parent, false);
			vh = new SystemFolderViewHolder(itemView, mClickListener);
		} else if (viewType == ROW_TYPE_FOLDER_OBJECT) {
			itemView = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.rowitem_filelist_remote_folder, parent, false);
			vh = new RemoteFolderViewHolder(itemView, mClickListener);
		} else if (viewType == ROW_TYPE_FILE_OBJECT) {
			itemView = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.rowitem_filelist_remote_file, parent, false);
			vh = new RemoteFileViewHolder(itemView, mClickListener);
		}
		return vh;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		int type = getItemViewType(position);
		if ( type == ROW_TYPE_SYSTEM_FOLDER_OBJECT ) {
			onBindSystemFolderViewHolder((SystemFolderViewHolder)holder, position);
		} else if ( type == ROW_TYPE_FOLDER_OBJECT ) {
			onBindFolderViewHolder((RemoteFolderViewHolder)holder, position);
		} else if ( type == ROW_TYPE_FILE_OBJECT ) {
			onBindFileViewHolder((RemoteFileViewHolder)holder, position);
		}
	}

	private void onBindSystemFolderViewHolder(SystemFolderViewHolder holder, int position) {
		RemoteFile remoteFile = getItem(position);

		holder.tvDisplayName.setText(remoteFile.getDisplayName());
		String displayCountOrSize = remoteFile.getDisplayCountOrSize();
		if ( TextUtils.isEmpty(displayCountOrSize) ) {
			holder.tvFileCount.setVisibility(View.GONE);
		} else {
			holder.tvFileCount.setVisibility(View.VISIBLE);
			holder.tvFileCount.setText(remoteFile.getDisplayCountOrSize());
		}

		RemoteFile.FileType type = remoteFile.getType();
		if ( type == RemoteFile.FileType.REMOTE_ROOT_WINDOWS_SHORTCUT_DIRECTORY ||
			 type == RemoteFile.FileType.REMOTE_ROOT_UNIX_SYMBOLIC_LINK_DIRECTORY ||
			 type == RemoteFile.FileType.REMOTE_ROOT_MAC_ALIAS_DIRECTORY ) {
			holder.ivShortcutIcon.setVisibility(View.VISIBLE);
		} else {
			holder.ivShortcutIcon.setVisibility(View.GONE);
		}

		// File Icon
		int iconResourceId = MiscUtils.getIconResourceIdByRemoteFileType(remoteFile.getType());
		if ( iconResourceId > -1 ) {
			holder.ivIcon.setImageResource(iconResourceId);
		}
	}

	private void onBindFolderViewHolder(RemoteFolderViewHolder holder, int position) {
		RemoteFile remoteFile = getItem(position);
		Resources res = mContext.getResources();

		holder.tvDisplayName.setText(remoteFile.getDisplayName());
		holder.tvModifiedDate.setText(convertDateStr(remoteFile.getDisplayLastModifiedDate()));

		// Selected Icon & Background
		if (mSelectedItemsIds.get(position) ) {
			holder.ivSelectedIcon.setVisibility(View.VISIBLE);
			holder.ivShortcutIcon.setVisibility(View.GONE);
			holder.itemView.setBackgroundColor(res.getColor(R.color.list_item_background_selected));
		} else {
			holder.ivSelectedIcon.setVisibility(View.GONE);
			if ( remoteFile.isSymlink() ) {
				holder.ivShortcutIcon.setVisibility(View.VISIBLE);
			} else {
				holder.ivShortcutIcon.setVisibility(View.GONE);
			}
			holder.itemView.setBackgroundColor(res.getColor(android.R.color.transparent));
		}

		// Folder Icon
		int iconResourceId = MiscUtils.getIconResourceIdByRemoteFileObject(remoteFile);
		if ( iconResourceId > -1 ) {
			holder.ivIcon.setImageResource(iconResourceId);
		}
	}

	private void onBindFileViewHolder(RemoteFileViewHolder holder, int position) {
		RemoteFile remoteFile = getItem(position);
		Resources res = mContext.getResources();

		holder.tvDisplayName.setText(remoteFile.getDisplayName());
		holder.tvFileSize.setText(remoteFile.getDisplayCountOrSize());
		holder.tvModifiedDate.setText(convertDateStr(remoteFile.getDisplayLastModifiedDate()));

		// Selected Icon
		if ( mSelectedItemsIds.get(position) ) {
			holder.ivSelectedIcon.setVisibility(View.VISIBLE);
			holder.ivShortcutIcon.setVisibility(View.GONE);
			holder.itemView.setBackgroundColor(res.getColor(R.color.list_item_background_selected));
		} else {
			holder.ivSelectedIcon.setVisibility(View.GONE);
			if ( remoteFile.isSymlink() ) {
				holder.ivShortcutIcon.setVisibility(View.VISIBLE);
			} else {
				holder.ivShortcutIcon.setVisibility(View.GONE);
			}
			holder.itemView.setBackgroundColor(res.getColor(android.R.color.transparent));
		}

		// File Icon
		int iconResourceId = MiscUtils.getIconResourceIdByRemoteFileObject(remoteFile);
		if ( iconResourceId > -1 ) {
			holder.ivIcon.setImageResource(iconResourceId);
			int colorFilter = remoteFile.getType() == RemoteFile.FileType.UNKNOWN ? R.color.main_color_grey_400 : android.R.color.transparent;
			holder.ivIcon.setColorFilter(res.getColor(colorFilter));
		}

		float alpha = 1.0f;
		boolean focusable = false;
		if ( !remoteFile.isSelectable() ) {
			alpha = 0.3f;
			focusable = true;
		}
		holder.itemView.setAlpha(alpha);
		holder.itemView.setFocusableInTouchMode(focusable);
		holder.itemView.setFocusable(focusable);
	}

	private String convertDateStr(String dateStr) {
		String result = "";
		if ( dateStr != null && dateStr.indexOf(" GMT") >= 0 ) {
			result = dateStr.substring(0, dateStr.indexOf(" GMT"));
		}
		return result;
	}

	@Override
	public int getItemCount() {
		return mRemoteFiles.size();
	}

	public boolean toggleSelection(int position) {
		boolean isSelect = !mSelectedItemsIds.get(position);
		selectView(position, isSelect);
		return isSelect;
	}

	private void selectView(int position, boolean value) {
		if (value)
			mSelectedItemsIds.put(position, value);
		else
			mSelectedItemsIds.delete(position);
		notifyDataSetChanged();
	}

	public void selectAll() {
		mSelectedItemsIds = new SparseBooleanArray();
		if ( mRemoteFiles != null ) {
			for ( int i=0; i<mRemoteFiles.size(); i++ ) {
				mSelectedItemsIds.put(i, true);
			}
		}
		notifyDataSetChanged();
	}

	public void deselectAll() {
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}

	public int getSelectedCount() {
		return mSelectedItemsIds.size();
	}

	public void setSelectedIds(Integer[] ids) {
		mSelectedItemsIds = new SparseBooleanArray();
		if ( ids != null ) {
			for ( int i=0; i<ids.length; i++ ) {
				mSelectedItemsIds.put(ids[i], true);
			}
		}
		notifyDataSetChanged();
	}

	public Integer[] getSelectedIds() {
		Integer[] ids = new Integer[mSelectedItemsIds.size()];
		for ( int i=0; i<mSelectedItemsIds.size(); i++ ) {
			ids[i] = mSelectedItemsIds.keyAt(i);
		}
		return ids;
	}

	public SparseBooleanArray getSelectedIdArray() {
		return mSelectedItemsIds;
	}

	public void removeAll() {
		mRemoteFiles.clear();
		deselectAll();
	}

	public boolean addAll(List<RemoteFile> localFiles) {
		int lastIndex = getItemCount();
		if (mRemoteFiles.addAll(localFiles)) {
			notifyItemRangeInserted(lastIndex, localFiles.size());
			return true;
		} else {
			return false;
		}
	}

	public void sort(Comparator<RemoteFile> comparator) {
		Collections.sort(mRemoteFiles, comparator);
		notifyItemRangeChanged(0, getItemCount());
	}

}
