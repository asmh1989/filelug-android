package com.filelug.android.ui.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.filelug.android.R;

public class BaseViewHolder extends RecyclerView.ViewHolder {

	public ImageView ivIcon;
	public TextView tvDisplayName;

	public BaseViewHolder(View view) {
		super(view);
		ivIcon = (ImageView) view.findViewById(R.id.object_icon);
		tvDisplayName = (TextView) view.findViewById(R.id.display_name);
	}

}
