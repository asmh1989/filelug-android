package com.filelug.android.ui.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.filelug.android.R;

/**
 * Created by Vincent Chang on 2016/4/28.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public class DownloadFileViewHolder extends BaseViewHolder {

	public View vTopDivider;
	public ImageView ivSelectedIcon;
	public TextView tvStatus;
	public ProgressBar pbProgress;

	public DownloadFileViewHolder(View view) {
		super(view);
		vTopDivider = (View) view.findViewById(R.id.divider_top);
		ivSelectedIcon = (ImageView) view.findViewById(R.id.object_selected_icon);
		tvStatus = (TextView) view.findViewById(R.id.status);
		pbProgress = (ProgressBar) view.findViewById(R.id.progress);
	}

}
