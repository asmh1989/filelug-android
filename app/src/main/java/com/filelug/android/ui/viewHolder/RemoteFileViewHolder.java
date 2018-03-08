package com.filelug.android.ui.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.filelug.android.R;

public class RemoteFileViewHolder extends SelectableViewHolder {

	public ImageView ivSelectedIcon;
	public ImageView ivShortcutIcon;
	public TextView tvFileSize;
	public TextView tvModifiedDate;

	public RemoteFileViewHolder(View view, SelectableViewHolder.ClickListener listener) {
		super(view, listener);
		ivSelectedIcon = (ImageView) view.findViewById(R.id.object_selected_icon);
		ivShortcutIcon = (ImageView) view.findViewById(R.id.object_shortcut_icon);
		tvFileSize = (TextView) view.findViewById(R.id.file_size);
		tvModifiedDate = (TextView) view.findViewById(R.id.modified_date);
	}

}
