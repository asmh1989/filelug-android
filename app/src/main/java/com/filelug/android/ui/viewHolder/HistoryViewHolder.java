package com.filelug.android.ui.viewHolder;

import android.view.View;
import android.widget.TextView;

import com.filelug.android.R;

public class HistoryViewHolder extends BaseViewHolder {

	public TextView tvDescription;

	public HistoryViewHolder(View view) {
		super(view);
		tvDescription = (TextView) view.findViewById(R.id.description);
	}

}
