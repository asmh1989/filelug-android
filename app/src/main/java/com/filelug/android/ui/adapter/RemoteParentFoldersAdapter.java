package com.filelug.android.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.filelug.android.R;
import com.filelug.android.ui.model.RemoteFile;
import com.filelug.android.util.MiscUtils;

import java.util.List;

/**
 * Created by Vincent Chang on 2015/8/2.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class RemoteParentFoldersAdapter extends ArrayAdapter<RemoteFile> {

	private static final int REMOTE_LAYOUT_RESOURCE = R.layout.rowitem_spinner_remote_parentfolder;

	public RemoteParentFoldersAdapter(Context context, List<RemoteFile> remoteFiles) {
		super(context, REMOTE_LAYOUT_RESOURCE, remoteFiles);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = LayoutInflater.from(getContext());
		RemoteFile remoteFile = getItem(position);
		if (convertView == null) {
			convertView = inflater.inflate(REMOTE_LAYOUT_RESOURCE, parent, false);
		}

		ImageView ivFileIcon = (ImageView) convertView.findViewById(R.id.spinner_list_item_icon);
		ImageView ivShortcutIcon  = (ImageView) convertView.findViewById(R.id.object_shortcut_icon);

		if ( remoteFile.isSymlink() ) {
			ivShortcutIcon.setVisibility(View.VISIBLE);
		} else {
			ivShortcutIcon.setVisibility(View.GONE);
		}

		int iconResourceId = MiscUtils.getIconResourceIdByRemoteFileType(remoteFile.getType());
		if ( iconResourceId < 0 ) {
			iconResourceId = R.drawable.ic_folder;
		}
		if ( iconResourceId > 0 ) {
			ivFileIcon.setImageResource(iconResourceId);
		}

		TextView tvDisplayName = (TextView) convertView.findViewById(R.id.spinner_list_item_display_name);
		tvDisplayName.setText(remoteFile.getDisplayName());

		return convertView;

	}

}
