package com.filelug.android.ui.viewHolder;

import android.view.View;

/**
 * Created by Vincent Chang on 2016/10/12.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public class SelectableViewHolder extends BaseViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public interface ClickListener {
        void onItemClicked(int position);
        boolean onItemLongClicked(int position);
    }

    private SelectableViewHolder.ClickListener clickListener;

    public SelectableViewHolder(View view, SelectableViewHolder.ClickListener listener) {
        super(view);
        this.clickListener = listener;
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.onItemClicked(getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        return clickListener != null && clickListener.onItemLongClicked(getAdapterPosition());
    }

}
