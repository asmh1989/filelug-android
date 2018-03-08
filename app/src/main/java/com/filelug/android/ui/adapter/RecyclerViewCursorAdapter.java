package com.filelug.android.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by Vincent Chang on 2016/9/26.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public abstract class RecyclerViewCursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    public static final int TYPE_TITLE_VIEW = 0;
    public static final int TYPE_ITEM_VIEW = 1;

    private Context mContext;
    private Cursor mCursor;
    private boolean mHasTitleView;
    private boolean mDataValid;
    private int mRowIdColumn;
    private DataSetObserver mDataSetObserver;

    public RecyclerViewCursorAdapter(Context context, Cursor cursor) {
        this(context, cursor, false);
    }

    public RecyclerViewCursorAdapter(Context context, Cursor cursor, boolean hasTitleView) {
        mContext = context;
        mCursor = cursor;
        mHasTitleView = hasTitleView;
        mDataValid = cursor != null;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex("_id") : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mHasTitleView && position == 0) {
            return TYPE_TITLE_VIEW;
        }
        return TYPE_ITEM_VIEW;
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        if (mHasTitleView) {
            itemCount++;
        }
        if (mDataValid && mCursor != null) {
            itemCount += mCursor.getCount();
        }
        return itemCount;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public Cursor getItem(final int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_TITLE_VIEW) {
            return null;
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.moveToPosition(mHasTitleView ? position-1 : position);
        }
        return mCursor;
    }

    @Override
    public long getItemId(int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_TITLE_VIEW) {
            return RecyclerView.NO_ID;
        }
        if (mDataValid && mCursor != null && mCursor.moveToPosition(mHasTitleView ? position-1 : position)) {
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TITLE_VIEW) {
            return onCreateTitleViewHolder(parent);
        } else if (viewType == TYPE_ITEM_VIEW) {
            return onCreateItemViewHolder(parent);
        }
        return null;
    }

    public abstract VH onCreateTitleViewHolder(ViewGroup parent);

    public abstract VH onCreateItemViewHolder(ViewGroup parent);

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_TITLE_VIEW) {
            onBindTitleViewHolder(viewHolder);
        } else if (viewType == TYPE_ITEM_VIEW) {
            if (!mDataValid) {
                throw new IllegalStateException("this should only be called when the cursor is valid");
            }
            int p = mHasTitleView ? position-1 : position;
            if (!mCursor.moveToPosition(p)) {
                throw new IllegalStateException("couldn't move cursor to position " + p);
            }
            onBindItemViewHolder(viewHolder, mCursor);
        }
    }

    public abstract void onBindTitleViewHolder(VH viewHolder);

    public abstract void onBindItemViewHolder(VH viewHolder, Cursor cursor);

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }

    }

}
