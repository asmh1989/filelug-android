package com.filelug.android.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.filelug.android.R;
import com.filelug.android.ui.model.LocalFile;
import com.filelug.android.util.MiscUtils;

import java.util.List;

public class LocalParentFoldersAdapter extends ArrayAdapter<LocalFile> {

	private static final int LOCAL_LAYOUT_RESOURCE = R.layout.rowitem_spinner_parentfolder;

	public LocalParentFoldersAdapter(Context context, List<LocalFile> localFiles) {
		super(context, LOCAL_LAYOUT_RESOURCE, localFiles);
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
		LocalFile localFile = getItem(position);
		if (convertView == null) {
			convertView = inflater.inflate(LOCAL_LAYOUT_RESOURCE, parent, false);
		}

		ImageView ivFileIcon = (ImageView) convertView.findViewById(R.id.spinner_list_item_icon);
		int iconResourceId = MiscUtils.getIconResourceIdByLocalFileType(localFile.getType());
		if ( iconResourceId < 0 ) {
			iconResourceId = R.drawable.ic_folder;
		}
		if ( iconResourceId > 0 ) {
			ivFileIcon.setImageResource(iconResourceId);
		}

		TextView tvDisplayName = (TextView) convertView.findViewById(R.id.spinner_list_item_display_name);
		tvDisplayName.setText(localFile.getDisplayName());

		return convertView;

	}

}
