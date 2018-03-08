package com.filelug.android.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filelug.android.R;
import com.filelug.android.provider.filetransfer.FileTransferColumns;
import com.filelug.android.ui.viewHolder.DownloadFileViewHolder;
import com.filelug.android.util.MiscUtils;

/**
 * Created by Vincent Chang on 2016/4/28.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public class DownloadToCacheCursorAdapter extends SectionRecyclerViewCursorAdapter<RecyclerView.ViewHolder> {

    public DownloadToCacheCursorAdapter(Context context, Cursor cursor){
        super(context, cursor);
    }

    @Override
    public RecyclerView.ViewHolder onCreateSectionViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindSectionViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rowitem_download_file, parent, false);
        DownloadFileViewHolder vh = new DownloadFileViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        Context context = viewHolder.itemView.getContext();
        DownloadFileViewHolder itemVH = (DownloadFileViewHolder)viewHolder;

//		long _id = cursor.getLong(cursor.getColumnIndex(FileTransferColumns._ID));
//		String groupId = cursor.getString(cursor.getColumnIndex(FileTransferColumns.GROUP_ID));
//		String transferKey = cursor.getString(cursor.getColumnIndex(FileTransferColumns.TRANSFER_KEY));
        String localFileName = cursor.getString(cursor.getColumnIndex(FileTransferColumns.LOCAL_FILE_NAME));
        String savedFileName = cursor.getString(cursor.getColumnIndex(FileTransferColumns.SAVED_FILE_NAME));
        boolean waitToConfirm = cursor.getInt(cursor.getColumnIndex(FileTransferColumns.WAIT_TO_CONFIRM)) != 0;
        int status = cursor.getInt(cursor.getColumnIndex(FileTransferColumns.STATUS));

        int statusRes = R.array.download_status_array;
        String displayName = null;
        String statusStr = null;
        int textColor = context.getResources().getColor(R.color.main_color_500);

        int backgroundRes = android.R.color.transparent;
        boolean showProgressBar = false;
        boolean showSelectedIcon = false;
        int percentage = -1;

        if ( waitToConfirm ) {
            statusStr = context.getResources().getString(R.string.transfer_status_confirming);
        } else {
            statusStr = context.getResources().getStringArray(statusRes)[status];
            if ( status == 1 ) { // Processing
                long totalSize = cursor.getLong(cursor.getColumnIndex(FileTransferColumns.TOTAL_SIZE));
                long transferredSize = cursor.getLong(cursor.getColumnIndex(FileTransferColumns.TRANSFERRED_SIZE));
                percentage = transferredSize == 0 ? 0 : (int)((transferredSize * 100 ) / totalSize);
                statusStr += String.format(context.getResources().getString(R.string.transfer_progress), percentage);
                showProgressBar = true;
            } else if ( status == 2 ) { // Success
                textColor = context.getResources().getColor(R.color.main_color_grey_900);
                if ( !TextUtils.isEmpty(savedFileName) ) {
                    displayName = String.format(context.getResources().getString(R.string.message_download_file_save_as), localFileName, savedFileName);
                }
                showSelectedIcon = true;
                backgroundRes = R.color.list_item_background_selected;
            } else if ( status >= 3 ) { // Failure, Cancel, Not found
                textColor = context.getResources().getColor(R.color.material_red_500);
            }
        }

        if ( TextUtils.isEmpty(displayName) ) {
            displayName = localFileName;
        }

//		itemVH.tvGroupId.setText(groupId);
//		itemVH.tvTransferKey.setText(_id + ", " + transferKey);
        itemVH.tvDisplayName.setText(displayName);
        itemVH.tvStatus.setText(statusStr);
        itemVH.tvStatus.setTextColor(textColor);
        if ( showProgressBar ) {
            itemVH.pbProgress.setVisibility(View.VISIBLE);
            itemVH.pbProgress.setProgress(percentage);
        } else {
            itemVH.pbProgress.setVisibility(View.GONE);
        }
        itemVH.ivSelectedIcon.setVisibility(showSelectedIcon ? View.VISIBLE : View.GONE);

        String extension = MiscUtils.getExtension(localFileName);
        int iconResourceId = MiscUtils.getIconResourceIdByExtension(extension);
        if ( iconResourceId > -1 ) {
            itemVH.ivIcon.setImageResource(iconResourceId);
        }

        itemVH.itemView.setBackgroundResource(backgroundRes);
    }

}
