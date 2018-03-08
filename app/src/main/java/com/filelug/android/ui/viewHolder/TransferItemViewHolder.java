package com.filelug.android.ui.viewHolder;

import android.view.View;
import android.widget.TextView;

import com.filelug.android.R;

public class TransferItemViewHolder extends BaseViewHolder {

//	public TextView tvGroupId;
//	public TextView tvTransferKey;
	public TextView tvStatus;
	public TextView tvLastModified;

	public TransferItemViewHolder(View view) {
		super(view);
//		tvGroupId = (TextView) view.findViewById(R.id.group_id);
//		tvTransferKey = (TextView) view.findViewById(R.id.transfer_key);
		tvStatus = (TextView) view.findViewById(R.id.status);
		tvLastModified = (TextView) view.findViewById(R.id.last_modified);
	}

}
