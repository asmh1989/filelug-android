package com.filelug.android.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.provider.assetfile.AssetFileColumns;
import com.filelug.android.provider.filetransfer.FileTransferColumns;
import com.filelug.android.ui.viewHolder.TransferItemViewHolder;
import com.filelug.android.ui.viewHolder.TransferSectionViewHolder;
import com.filelug.android.util.FormatUtils;
import com.filelug.android.util.MiscUtils;

import java.util.Date;

public class TransferCursorAdapter extends SectionRecyclerViewCursorAdapter<RecyclerView.ViewHolder> implements View.OnClickListener {

	public interface OnItemClickListener {
		void onItemClicked(Cursor cursor);
	}

	private int mTransferType;
	private OnItemClickListener mItemClickListener;

	public TransferCursorAdapter(Context context, Cursor cursor, int transferType) {
		super(context, cursor, true);
		this.mTransferType = transferType;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mItemClickListener = onItemClickListener;
	}

	@Override
	public RecyclerView.ViewHolder onCreateSectionViewHolder(ViewGroup parent) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.rowitem_transfer_section, parent, false);
		TransferSectionViewHolder vh = new TransferSectionViewHolder(itemView);
		itemView.setOnClickListener(this);
		return vh;
	}

	@Override
	public void onBindSectionViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
		TransferSectionViewHolder sectionVH = (TransferSectionViewHolder)viewHolder;

		Bundle sectionsInfo = getSectionsInfo().get(position);
		int groupFileCount = (int)sectionsInfo.get(KEY_GROUP_FILE_COUNT);
		long groupStartTimestamp = (long)sectionsInfo.get(KEY_GROUP_START_TIMESTAMP);

		Context context = viewHolder.itemView.getContext();
		String dateStr = FormatUtils.formatDate2(context, new Date(groupStartTimestamp));
		String fileCountStr = null;
		if ( groupFileCount > 1 ) {
			String pattern = context.getResources().getString(R.string.label_transfer_group_section_many_files);
			fileCountStr = String.format(pattern, groupFileCount);
		} else {
			fileCountStr = context.getResources().getString(R.string.label_transfer_group_section_1_file);
		}
		String descText = dateStr + " " + fileCountStr;

		sectionVH.tvDescription.setText(descText);

//		String groupId = (String)sectionsInfo.get(KEY_GROUP_ID);
//		sectionVH.tvGroupId.setText(groupId);
	}

	@Override
	public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.rowitem_transfer_item, parent, false);
		TransferItemViewHolder vh = new TransferItemViewHolder(itemView);
		itemView.setOnClickListener(this);
		return vh;
	}

	@Override
	public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
		Context context = viewHolder.itemView.getContext();
		if ( mTransferType == Constants.TRANSFER_TYPE_UPLOAD ) {
			bindUploadView(viewHolder, context, cursor);
		} else {
			bindDownloadView(viewHolder, context, cursor);
		}
	}

	private void bindUploadView(RecyclerView.ViewHolder viewHolder, Context context, Cursor cursor) {
		TransferItemViewHolder itemVH = (TransferItemViewHolder)viewHolder;

//		long _id = cursor.getLong(cursor.getColumnIndex(AssetFileColumns._ID));
//		String groupId = cursor.getString(cursor.getColumnIndex(AssetFileColumns.GROUP_ID));
//		String transferKey = cursor.getString(cursor.getColumnIndex(AssetFileColumns.TRANSFER_KEY));
		String serverFileName = cursor.getString(cursor.getColumnIndex(AssetFileColumns.SERVER_FILE_NAME));
		long lastModifiedTimestamp = cursor.getLong(cursor.getColumnIndex(AssetFileColumns.LAST_MODIFIED_TIMESTAMP));
		boolean waitToConfirm = cursor.getInt(cursor.getColumnIndex(AssetFileColumns.WAIT_TO_CONFIRM)) != 0;
		int status = cursor.getInt(cursor.getColumnIndex(AssetFileColumns.STATUS));

		int statusRes = R.array.upload_status_array;
		String lastModifiedTimestampStr = lastModifiedTimestamp == 0 ? "" : FormatUtils.formatDate1(context, new Date(lastModifiedTimestamp));
		String statusStr = null;
		int textColor = context.getResources().getColor(R.color.main_color_500);

		if ( waitToConfirm ) {
			statusStr = context.getResources().getString(R.string.transfer_status_confirming);
		} else {
			long totalSize = cursor.getLong(cursor.getColumnIndex(AssetFileColumns.TOTAL_SIZE));
			long transferredSize = cursor.getLong(cursor.getColumnIndex(AssetFileColumns.TRANSFERRED_SIZE));
			statusStr = context.getResources().getStringArray(statusRes)[status];
			if ( status == 0 ) { // Wait
				lastModifiedTimestampStr = FormatUtils.formatFileSize(context, totalSize);
			} else if ( status == 1 ) { // Processing
				int percentage = transferredSize == 0 ? 0 : (int)((transferredSize * 100 ) / totalSize);
				statusStr += String.format(context.getResources().getString(R.string.transfer_progress), percentage);
				lastModifiedTimestampStr = FormatUtils.formatFileSize(context, totalSize);
			} else if ( status == 2 ) { // Success
				textColor = context.getResources().getColor(R.color.main_color_grey_900);
				statusStr += ", " + FormatUtils.formatFileSize(context, totalSize);
			} else if ( status >= 3 && status <= 5 ) { // Failure, Cancel, Not found
				textColor = context.getResources().getColor(R.color.material_red_500);
			} else { // status == 6, device_uploaded_but_unconfirmed
			}
		}

//		itemVH.tvGroupId.setText(groupId);
//		itemVH.tvTransferKey.setText(_id + ", " + transferKey);
		itemVH.tvDisplayName.setText(serverFileName);
		itemVH.tvStatus.setText(statusStr);
		itemVH.tvStatus.setTextColor(textColor);
		itemVH.tvLastModified.setText(lastModifiedTimestampStr);

		String extension = MiscUtils.getExtension(serverFileName);
		int iconResourceId = MiscUtils.getIconResourceIdByExtension(extension);
		if ( iconResourceId > -1 ) {
			itemVH.ivIcon.setImageResource(iconResourceId);
		}
	}

	private void bindDownloadView(RecyclerView.ViewHolder viewHolder, Context context, Cursor cursor) {
		TransferItemViewHolder itemVH = (TransferItemViewHolder)viewHolder;

//		long _id = cursor.getLong(cursor.getColumnIndex(FileTransferColumns._ID));
//		String groupId = cursor.getString(cursor.getColumnIndex(FileTransferColumns.GROUP_ID));
//		String transferKey = cursor.getString(cursor.getColumnIndex(FileTransferColumns.TRANSFER_KEY));
		String localFileName = cursor.getString(cursor.getColumnIndex(FileTransferColumns.LOCAL_FILE_NAME));
		String savedFileName = cursor.getString(cursor.getColumnIndex(FileTransferColumns.SAVED_FILE_NAME));
		boolean waitToConfirm = cursor.getInt(cursor.getColumnIndex(FileTransferColumns.WAIT_TO_CONFIRM)) != 0;
		int status = cursor.getInt(cursor.getColumnIndex(FileTransferColumns.STATUS));

		String lastModified = null;
		if ( status > 1 ) {
			String lastModifiedStr = cursor.getString(cursor.getColumnIndex(FileTransferColumns.LAST_MODIFIED));
			long lastModifiedTimestamp = FormatUtils.convertRemoteFileLastModifiedToTimestamp(context, lastModifiedStr);
			lastModified = FormatUtils.formatDate1(context, new Date(lastModifiedTimestamp));
		}

		int statusRes = R.array.download_status_array;
		String displayName = null;
		String statusStr = null;
		int textColor = context.getResources().getColor(R.color.main_color_500);

		if ( waitToConfirm ) {
			statusStr = context.getResources().getString(R.string.transfer_status_confirming);
		} else {
			long totalSize = cursor.getLong(cursor.getColumnIndex(FileTransferColumns.TOTAL_SIZE));
			long transferredSize = cursor.getLong(cursor.getColumnIndex(FileTransferColumns.TRANSFERRED_SIZE));
			statusStr = context.getResources().getStringArray(statusRes)[status];
			if ( status == 0 ) { // Wait
				lastModified = FormatUtils.formatFileSize(context, totalSize);
			} else if ( status == 1 ) { // Processing
				int percentage = transferredSize == 0 ? 0 : (int)((transferredSize * 100 ) / totalSize);
				statusStr += String.format(context.getResources().getString(R.string.transfer_progress), percentage);
				lastModified = FormatUtils.formatFileSize(context, totalSize);
			} else if ( status == 2 ) { // Success
				textColor = context.getResources().getColor(R.color.main_color_grey_900);
				if ( !TextUtils.isEmpty(savedFileName) ) {
					displayName = String.format(context.getResources().getString(R.string.message_download_file_save_as), localFileName, savedFileName);
				}
				statusStr += ", " + FormatUtils.formatFileSize(context, totalSize);
			} else if ( status >= 3 && status <= 5 ) { // Failure, Cancel, Not found
				textColor = context.getResources().getColor(R.color.material_red_500);
			} else { // status == 6, desktop_uploaded_but_unconfirmed
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
		itemVH.tvLastModified.setText(lastModified);

		String extension = MiscUtils.getExtension(localFileName);
		int iconResourceId = MiscUtils.getIconResourceIdByExtension(extension);
		if ( iconResourceId > -1 ) {
			itemVH.ivIcon.setImageResource(iconResourceId);
		}
	}

	@Override
	public void onClick(final View view) {
		if (this.mItemClickListener != null) {
			RecyclerView recyclerView = (RecyclerView) view.getParent();
			int position = recyclerView.getChildLayoutPosition(view);
			if (position != RecyclerView.NO_POSITION) {
				Cursor cursor = this.getItem(position);
				if (cursor != null) {
					this.mItemClickListener.onItemClicked(cursor);
				}
			}
		}
	}

}
