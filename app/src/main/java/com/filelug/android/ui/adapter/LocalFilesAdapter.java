package com.filelug.android.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filelug.android.R;
import com.filelug.android.ui.model.LocalFile;
import com.filelug.android.ui.viewHolder.FileViewHolder;
import com.filelug.android.ui.viewHolder.LocalFolderViewHolder;
import com.filelug.android.ui.viewHolder.SelectableViewHolder;
import com.filelug.android.ui.viewHolder.SystemFolderViewHolder;
import com.filelug.android.util.ImageUtil;
import com.filelug.android.util.LocalFileUtils;
import com.filelug.android.util.MiscUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LocalFilesAdapter extends RecyclerView.Adapter {

	private static final int ROW_TYPE_SYSTEM_FOLDER_OBJECT = 0;
	private static final int ROW_TYPE_FOLDER_OBJECT = 1;
	private static final int ROW_TYPE_FILE_OBJECT = 2;

	private Context mContext = null;
	private List<LocalFile> mLocalFiles = null;
	private SparseBooleanArray mSelectedItemsIds;
	private SelectableViewHolder.ClickListener mClickListener = null;

	public LocalFilesAdapter(Context context, List<LocalFile> localFiles, SelectableViewHolder.ClickListener clickListener) {
		mContext = context;
		mLocalFiles = localFiles;
		mClickListener = clickListener;
		this.mSelectedItemsIds = new SparseBooleanArray();
	}

	@Override
	public int getItemViewType(int position) {
		int viewType = 0;
		LocalFile localFile = getItem(position);
		LocalFile.FileType fileType = localFile.getType();
		if ( fileType == LocalFile.FileType.LOCAL_DIR ||
			 fileType == LocalFile.FileType.LOCAL_SYMBOLIC_LINK_DIR ||
			 fileType == LocalFile.FileType.MEDIA_DIR ) {
			viewType = ROW_TYPE_FOLDER_OBJECT;
		} else if ( fileType == LocalFile.FileType.LOCAL_FILE ||
					fileType == LocalFile.FileType.LOCAL_SYMBOLIC_LINK_FILE ||
					fileType == LocalFile.FileType.MEDIA_FILE ||
					fileType == LocalFile.FileType.UNKNOWN ) {
			viewType = ROW_TYPE_FILE_OBJECT;
		} else {
			viewType = ROW_TYPE_SYSTEM_FOLDER_OBJECT;
		}
		return viewType;
	}

	public LocalFile getItem(int position) {
		return mLocalFiles.get(position);
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
					.inflate(R.layout.rowitem_filelist_local_folder, parent, false);
			vh = new LocalFolderViewHolder(itemView, mClickListener);
		} else if (viewType == ROW_TYPE_FILE_OBJECT) {
			itemView = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.rowitem_filelist_file, parent, false);
			vh = new FileViewHolder(itemView, mClickListener);
		}
		return vh;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		int type = getItemViewType(position);
		if ( type == ROW_TYPE_SYSTEM_FOLDER_OBJECT ) {
			onBindSystemFolderViewHolder((SystemFolderViewHolder)holder, position);
		} else if ( type == ROW_TYPE_FOLDER_OBJECT ) {
			onBindFolderViewHolder((LocalFolderViewHolder)holder, position);
		} else if ( type == ROW_TYPE_FILE_OBJECT ) {
			onBindFileViewHolder((FileViewHolder)holder, position);
		}
	}

	private void onBindSystemFolderViewHolder(SystemFolderViewHolder holder, int position) {
		LocalFile localFile = getItem(position);

		holder.tvDisplayName.setText(localFile.getDisplayName());
		String displayCountOrSize = localFile.getDisplayCountOrSize();
		if ( TextUtils.isEmpty(displayCountOrSize) ) {
			holder.tvFileCount.setVisibility(View.GONE);
		} else {
			holder.tvFileCount.setVisibility(View.VISIBLE);
			holder.tvFileCount.setText(localFile.getDisplayCountOrSize());
		}

		// File Icon
		int iconResourceId = MiscUtils.getIconResourceIdByLocalFileType(localFile.getType());
		if ( iconResourceId > -1 ) {
			holder.ivIcon.setImageResource(iconResourceId);
		}
	}

	private void onBindFolderViewHolder(LocalFolderViewHolder holder, int position) {
		LocalFile localFile = getItem(position);
		Resources res = mContext.getResources();

		holder.tvDisplayName.setText(localFile.getDisplayName());
		holder.tvFileCount.setText(localFile.getDisplayCountOrSize());
		holder.tvModifiedDate.setText(localFile.getDisplayLastModifiedDate());

		// Selected Icon & Background
		if (mSelectedItemsIds.get(position) ) {
			holder.ivSelectedIcon.setVisibility(View.VISIBLE);
//			holder.ivShortcutIcon.setVisibility(View.GONE);
			holder.itemView.setBackgroundColor(res.getColor(R.color.list_item_background_selected));
		} else {
			holder.ivSelectedIcon.setVisibility(View.GONE);
//			if ( localFile.isSymlink() ) {
//				holder.ivShortcutIcon.setVisibility(View.VISIBLE);
//			} else {
//				holder.ivShortcutIcon.setVisibility(View.GONE);
//			}
			holder.itemView.setBackgroundColor(res.getColor(android.R.color.transparent));
		}
	}

	private void onBindFileViewHolder(FileViewHolder holder, int position) {
		LocalFile localFile = getItem(position);
		Resources res = mContext.getResources();

		holder.tvDisplayName.setText(localFile.getDisplayName());
		holder.tvFileSize.setText(localFile.getDisplayCountOrSize());
		holder.tvModifiedDate.setText(localFile.getDisplayLastModifiedDate());

		// Selected Icon & Background
		if ( mSelectedItemsIds.get(position) ) {
			holder.ivSelectedIcon.setVisibility(View.VISIBLE);
//			holder.ivShortcutIcon.setVisibility(View.GONE);
			holder.itemView.setBackgroundColor(res.getColor(R.color.list_item_background_selected));
		} else {
			holder.ivSelectedIcon.setVisibility(View.GONE);
//			if ( localFile.isSymlink() ) {
//				holder.ivShortcutIcon.setVisibility(View.VISIBLE);
//			} else {
//				holder.ivShortcutIcon.setVisibility(View.GONE);
//			}
			holder.itemView.setBackgroundColor(res.getColor(android.R.color.transparent));
		}

		// File Icon
		String thumbnailPath = null;
		String path = null;
		LocalFile.MediaType mediaType = localFile.getMediaType();
		if ( mediaType == LocalFile.MediaType.IMAGE || mediaType == LocalFile.MediaType.VIDEO ) {
			if (mediaType == LocalFile.MediaType.IMAGE) {
				thumbnailPath = LocalFileUtils.getImageThumbnail(mContext, (int) localFile.getMediaId());
			} else if (mediaType == LocalFile.MediaType.VIDEO) {
				thumbnailPath = LocalFileUtils.getVideoThumbnail(mContext, (int) localFile.getMediaId());
			}
			if (TextUtils.isEmpty(thumbnailPath) && localFile.getSize() <= 20971520L) { // <= 20MB
				path = localFile.getFullName();
			}
		} else if ( mediaType == LocalFile.MediaType.DOWNLOAD || mediaType == LocalFile.MediaType.NONE ) {
			String mimeType = localFile.getContentType();
			if ( !TextUtils.isEmpty(mimeType) && ( mimeType.startsWith("image") || mimeType.startsWith("video") ) ) {
				if ( mimeType.startsWith("image") ) {
					thumbnailPath = LocalFileUtils.getImageThumbnail(mContext, (int) localFile.getMediaId());
				} else if ( mimeType.startsWith("video") ) {
					thumbnailPath = LocalFileUtils.getVideoThumbnail(mContext, (int) localFile.getMediaId());
				}
				if (TextUtils.isEmpty(thumbnailPath) && localFile.getSize() <= 20971520L) { // <= 20MB
					path = localFile.getFullName();
				}
			}
		}

		if (!TextUtils.isEmpty(thumbnailPath)) {
			Bitmap thumbnailBitmap = MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(), localFile.getMediaId(), MediaStore.Video.Thumbnails.MICRO_KIND, null);;
			holder.ivIcon.setImageBitmap(thumbnailBitmap);

//            holder.ivIcon.setImageURI(Uri.parse("file://" + thumbnailPath));
		} else if (!TextUtils.isEmpty(path)) {
			ImageUtil.displayImage(holder.ivIcon, "file://" + path, null);
		} else {
			int iconResourceId = MiscUtils.getIconResourceIdByLocalFileObject(localFile);
			if (iconResourceId > -1) {
				holder.ivIcon.setImageResource(iconResourceId);
				int colorFilter = localFile.getType() == LocalFile.FileType.UNKNOWN ? R.color.main_color_grey_400 : android.R.color.transparent;
				holder.ivIcon.setColorFilter(res.getColor(colorFilter));
			}
		}
	}

	@Override
	public int getItemCount() {
		return mLocalFiles.size();
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
		if ( mLocalFiles != null ) {
			for ( int i=0; i<mLocalFiles.size(); i++ ) {
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
		mLocalFiles.clear();
		deselectAll();
	}

	public boolean addAll(List<LocalFile> localFiles) {
		int lastIndex = getItemCount();
		if (mLocalFiles.addAll(localFiles)) {
			notifyItemRangeInserted(lastIndex, localFiles.size());
			return true;
		} else {
			return false;
		}
	}

	public void sort(Comparator<LocalFile> comparator) {
		Collections.sort(mLocalFiles, comparator);
		notifyItemRangeChanged(0, getItemCount());
	}

}
