package com.filelug.android.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filelug.android.R;
import com.filelug.android.ui.model.RemoteFile;
import com.filelug.android.ui.viewHolder.SelectableViewHolder;
import com.filelug.android.ui.viewHolder.SelectedFileViewHolder;
import com.filelug.android.util.MiscUtils;

/**
 * Created by Vincent Chang on 2015/12/24.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class ConfirmDownloadFileListAdapter extends RecyclerView.Adapter<SelectedFileViewHolder> {

    private static final String TAG = ConfirmDownloadFileListAdapter.class.getSimpleName();

    private Context mContext = null;
    private RemoteFile[] mRemoteFiles = null;
    private SparseBooleanArray mSelectedItemsIds = null;
    private SelectableViewHolder.ClickListener mClickListener = null;

    public ConfirmDownloadFileListAdapter(Context context, RemoteFile[] remoteFiles, SelectableViewHolder.ClickListener clickListener) {
        mContext = context;
        mRemoteFiles = remoteFiles;
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
        RemoteFile remoteFile = mRemoteFiles[position];
        Resources res = mContext.getResources();

        if ( position == 0 ) {
            holder.vTopDivider.setVisibility(View.VISIBLE);
        } else {
            holder.vTopDivider.setVisibility(View.GONE);
        }
        holder.tvDisplayName.setText(remoteFile.getDisplayName());

        // Selected Icon & Background
        if ( mSelectedItemsIds.get(position) ) {
            holder.ivSelectedIcon.setVisibility(View.VISIBLE);
//            holder.ivShortcutIcon.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(res.getColor(R.color.list_item_background_selected));
        } else {
            holder.ivSelectedIcon.setVisibility(View.GONE);
//            if ( remoteFile.isSymlink() ) {
//                holder.ivShortcutIcon.setVisibility(View.VISIBLE);
//            } else {
//                holder.ivShortcutIcon.setVisibility(View.GONE);
//            }
            holder.itemView.setBackgroundColor(res.getColor(android.R.color.transparent));
        }

        // File Icon
        int iconResourceId = MiscUtils.getIconResourceIdByRemoteFileObject(remoteFile);
        if ( iconResourceId > -1 ) {
            holder.ivIcon.setImageResource(iconResourceId);
            int colorFilter = remoteFile.getType() == RemoteFile.FileType.UNKNOWN ? R.color.main_color_grey_400 : android.R.color.transparent;
            holder.ivIcon.setColorFilter(res.getColor(colorFilter));
        }
    }

    @Override
    public int getItemCount() {
        return mRemoteFiles.length;
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
            for ( int i=0; i<mRemoteFiles.length; i++ ) {
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
