package com.filelug.android.ui.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.filelug.android.R;

/**
 * Created by Vincent Chang on 2015/8/5.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class RemoteFolderViewHolder extends SelectableViewHolder {

	public ImageView ivSelectedIcon;
	public ImageView ivShortcutIcon;
	public TextView tvModifiedDate;

	public RemoteFolderViewHolder(View view, SelectableViewHolder.ClickListener listener) {
		super(view, listener);
		ivSelectedIcon = (ImageView) view.findViewById(R.id.object_selected_icon);
		ivShortcutIcon = (ImageView) view.findViewById(R.id.object_shortcut_icon);
		tvModifiedDate = (TextView) view.findViewById(R.id.modified_date);
	}

}
