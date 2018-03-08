package com.filelug.android.ui.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.filelug.android.R;

public class HistoryTitleViewHolder extends RecyclerView.ViewHolder {

    public ImageView ivTitleImage;
    public TextView tvSearchType;
    public TextView tvSearchReport;

    public HistoryTitleViewHolder(View view) {
        super(view);
        ivTitleImage = (ImageView) view.findViewById(R.id.titleImage);
        tvSearchType = (TextView) view.findViewById(R.id.searchType);
        tvSearchReport = (TextView) view.findViewById(R.id.searchReport);
    }

}
