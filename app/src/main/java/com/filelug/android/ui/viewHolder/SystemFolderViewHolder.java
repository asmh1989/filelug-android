package com.filelug.android.ui.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.filelug.android.R;

public class SystemFolderViewHolder extends SelectableViewHolder {

	public ImageView ivShortcutIcon;
	public TextView tvFileCount;

	public SystemFolderViewHolder(View view, SelectableViewHolder.ClickListener listener) {
		super(view, listener);
		ivShortcutIcon = (ImageView) view.findViewById(R.id.object_shortcut_icon);
		tvFileCount = (TextView) view.findViewById(R.id.system_object_count_or_size);
	}

}
