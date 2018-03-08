package com.filelug.android.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filelug.android.R;
import com.filelug.android.ui.model.LocalFile;
import com.filelug.android.ui.viewHolder.SelectableViewHolder;
import com.filelug.android.ui.viewHolder.SelectedFileViewHolder;
import com.filelug.android.util.ImageUtil;
import com.filelug.android.util.LocalFileUtils;
import com.filelug.android.util.MiscUtils;

/**
 * Created by Vincent Chang on 2015/12/22.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class ConfirmUploadFileListAdapter extends RecyclerView.Adapter<SelectedFileViewHolder>  {

    private static final String TAG = ConfirmUploadFileListAdapter.class.getSimpleName();

    private Context mContext = null;
    private LocalFile[] mLocalFiles = null;
    private SparseBooleanArray mSelectedItemsIds = null;
    private SelectableViewHolder.ClickListener mClickListener = null;

    public ConfirmUploadFileListAdapter(Context context, LocalFile[] localFiles, SelectableViewHolder.ClickListener clickListener) {
        mContext = context;
        mLocalFiles = localFiles;
        mClickListener = clickListener;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public SelectedFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.rowitem_selected_file, parent, false);
        SelectedFileViewHolder vh = new SelectedFileViewHolder(itemView, mClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(SelectedFileViewHolder holder, int position) {
        LocalFile localFile = mLocalFiles[position];
        Resources res = mContext.getResources();

        if ( position == 0 ) {
            holder.vTopDivider.setVisibility(View.VISIBLE);
        } else {
            holder.vTopDivider.setVisibility(View.GONE);
        }
        holder.tvDisplayName.setText(localFile.getDisplayName());

        // Selected Icon & Background
        if ( mSelectedItemsIds.get(position) ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "3a, initLocalSelectedFileViewHolder(), Item selected!");
            holder.ivSelectedIcon.setVisibility(View.VISIBLE);
//            holder.ivShortcutIcon.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(res.getColor(R.color.list_item_background_selected));
        } else {
//            if ( Constants.DEBUG ) Log.d(TAG, "3b, initLocalSelectedFileViewHolder(), Item not select!");
            holder.ivSelectedIcon.setVisibility(View.GONE);
//            if ( localFile.isSymlink() ) {
//                holder.ivShortcutIcon.setVisibility(View.VISIBLE);
//            } else {
//                holder.ivShortcutIcon.setVisibility(View.GONE);
//            }
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
//            if ( Constants.DEBUG ) Log.d(TAG, "4a, initLocalSelectedFileViewHolder(), thumbnailPath="+thumbnailPath);
            holder.ivIcon.setImageURI(Uri.parse("file://" + thumbnailPath));
        } else if (!TextUtils.isEmpty(path)) {
//            if ( Constants.DEBUG ) Log.d(TAG, "4b, initLocalSelectedFileViewHolder(), path="+path);
            ImageUtil.displayImage(holder.ivIcon, "file://" + path, null);
        } else {
            int iconResourceId = MiscUtils.getIconResourceIdByLocalFileObject(localFile);
//            if ( Constants.DEBUG ) Log.d(TAG, "4c, initLocalSelectedFileViewHolder(), iconResourceId="+iconResourceId);
            if (iconResourceId > -1) {
                holder.ivIcon.setImageResource(iconResourceId);
                int colorFilter = localFile.getType() == LocalFile.FileType.UNKNOWN ? R.color.main_color_grey_400 : android.R.color.transparent;
                holder.ivIcon.setColorFilter(res.getColor(colorFilter));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mLocalFiles.length;
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
            for ( int i=0; i<mLocalFiles.length; i++ ) {
                mSelectedItemsIds.put(i, true);
            }
        }
        notifyDataSetChanged();
    }

    public void removeAll() {
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

}
