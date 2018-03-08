package com.filelug.android.ui.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.filelug.android.R;

/**
 * Created by Vincent Chang on 2015/12/22.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class SelectedFileViewHolder extends SelectableViewHolder {

	public ImageView ivIcon;
	public TextView tvDisplayName;
	public View vTopDivider;
	public ImageView ivSelectedIcon;
//	public ImageView ivShortcutIcon;

	public SelectedFileViewHolder(View view, SelectableViewHolder.ClickListener listener) {
		super(view, listener);
		ivIcon = (ImageView) view.findViewById(R.id.object_icon);
		tvDisplayName = (TextView) view.findViewById(R.id.display_name);
		vTopDivider = (View) view.findViewById(R.id.divider_top);
		ivSelectedIcon = (ImageView) view.findViewById(R.id.object_selected_icon);
//        ivShortcutIcon = (ImageView) view.findViewById(R.id.object_shortcut_icon);
	}

}
