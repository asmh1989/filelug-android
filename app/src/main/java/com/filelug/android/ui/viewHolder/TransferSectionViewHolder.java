package com.filelug.android.ui.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.filelug.android.R;

public class TransferSectionViewHolder extends RecyclerView.ViewHolder {

	public TextView tvDescription;
	public TextView tvGroupId;

	public TransferSectionViewHolder(View view) {
		super(view);
		tvDescription = (TextView) view.findViewById(R.id.description);
		tvGroupId = (TextView) view.findViewById(R.id.group_id);
	}

}
