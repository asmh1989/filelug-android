package com.filelug.android.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.provider.downloadhistory.DownloadHistoryColumns;
import com.filelug.android.ui.fragment.HistoryFragment;
import com.filelug.android.ui.viewHolder.HistoryTitleViewHolder;
import com.filelug.android.ui.viewHolder.HistoryViewHolder;
import com.filelug.android.util.FormatUtils;
import com.filelug.android.util.MiscUtils;

import java.util.Date;

public class HistoryCursorAdapter extends RecyclerViewCursorAdapter<RecyclerView.ViewHolder> implements View.OnClickListener {

	public interface OnItemClickListener {
		void onItemClicked(Cursor cursor);
	}

	private int mHistoryType;
	private int mDescriptionRes = -1;
	private OnItemClickListener mItemClickListener;

	private String mSearchTypeText;
	private String mSearchReportText;

	public HistoryCursorAdapter(Context context, Cursor cursor, int historyType) {
		super(context, cursor, true);
		this.mHistoryType = historyType;
		if ( mHistoryType == Constants.TRANSFER_TYPE_UPLOAD ) {
			mDescriptionRes = R.string.message_history_upload_description;
		} else {
			mDescriptionRes = R.string.message_history_download_description;
		}
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mItemClickListener = onItemClickListener;
	}

	@Override
	public RecyclerView.ViewHolder onCreateTitleViewHolder(ViewGroup parent) {
		View headerView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.layout_title_histories, parent, false);
		HistoryTitleViewHolder vh = new HistoryTitleViewHolder(headerView);
		return vh;
	}

	@Override
	public void onBindTitleViewHolder(RecyclerView.ViewHolder viewHolder) {
		HistoryTitleViewHolder historyHeaderVH = (HistoryTitleViewHolder)viewHolder;
		if ( this.mHistoryType == HistoryFragment.HISTORY_TYPE_DOWNLOAD ) {
			historyHeaderVH.ivTitleImage.setImageResource(R.drawable.header_ic_download_history);
		} else if ( this.mHistoryType == HistoryFragment.HISTORY_TYPE_UPLOAD ) {
			historyHeaderVH.ivTitleImage.setImageResource(R.drawable.header_ic_upload_history);
		}
		historyHeaderVH.tvSearchType.setText(mSearchTypeText);
		historyHeaderVH.tvSearchReport.setText(mSearchReportText);
	}

	@Override
	public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.rowitem_history, parent, false);
		HistoryViewHolder vh = new HistoryViewHolder(itemView);
		itemView.setOnClickListener(this);
		return vh;
	}

	@Override
	public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
		Context context = viewHolder.itemView.getContext();
		HistoryViewHolder historyVH = (HistoryViewHolder)viewHolder;

		String fileName = cursor.getString(cursor.getColumnIndex(DownloadHistoryColumns.FILE_NAME));
//		String computerGroup = cursor.getString(cursor.getColumnIndex(DownloadHistoryColumns.COMPUTER_GROUP));
		String computerName = cursor.getString(cursor.getColumnIndex(DownloadHistoryColumns.COMPUTER_NAME));
		long fileSize = cursor.getLong(cursor.getColumnIndex(DownloadHistoryColumns.FILE_SIZE));
		long endTimestamp = cursor.getLong(cursor.getColumnIndex(DownloadHistoryColumns.END_TIMESTAMP));

		historyVH.tvDisplayName.setText(fileName);

		String fileSizeStr = FormatUtils.formatFileSize(context, fileSize);
		String endDate = FormatUtils.formatDate2(context, new Date(endTimestamp));
		String descriptionStr = String.format(context.getResources().getString(mDescriptionRes), fileSizeStr, endDate, computerName);

		historyVH.tvDescription.setText(descriptionStr);

		String extension = MiscUtils.getExtension(fileName);
		int iconResourceId = MiscUtils.getIconResourceIdByExtension(extension);
		if ( iconResourceId > -1 ) {
			historyVH.ivIcon.setImageResource(iconResourceId);
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

	public void setSearchTypeText(String mSearchTypeText) {
		this.mSearchTypeText = mSearchTypeText;
	}

	public void setSearchReportText(String mSearchReportText) {
		this.mSearchReportText = mSearchReportText;
	}

}
