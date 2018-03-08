package com.filelug.android.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.filelug.android.Constants;
import com.filelug.android.provider.assetfile.AssetFileColumns;
import com.filelug.android.provider.filetransfer.FileTransferColumns;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Vincent Chang on 2016/11/18.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public abstract class SectionRecyclerViewCursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private static final String TAG = SectionRecyclerViewCursorAdapter.class.getSimpleName();

    public static final int TYPE_SECTION_VIEW = 0;
    public static final int TYPE_ITEM_VIEW = 1;

    protected static final String KEY_GROUP_ID = "groupId";
    protected static final String KEY_GROUP_FILE_COUNT = "groupFileCount";
    protected static final String KEY_GROUP_START_TIMESTAMP = "groupStartTimestamp";

    private Context mContext;
    private Cursor mCursor;
    private boolean mShowGroupSection;
    private boolean mDataValid;
    private int mRowIdColumn;
    private int mGroupIdColumn;
    private int mTransferKeyColumn;
    private int mGroupStartTimestampColumn;
    private DataSetObserver mDataSetObserver;
    private Map<Integer, Bundle> sectionsInfo;
    private Map<Integer, Integer> sectionLastItemsInfo;

    public SectionRecyclerViewCursorAdapter(Context context, Cursor cursor) {
        this(context, cursor, false);
    }

    public SectionRecyclerViewCursorAdapter(Context context, Cursor cursor, boolean showGroupSection) {
        mContext = context;
        mCursor = cursor;
        mShowGroupSection = showGroupSection;
        mDataValid = cursor != null;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex("_id") : -1;
        mGroupIdColumn = mDataValid ? mCursor.getColumnIndex(FileTransferColumns.GROUP_ID) : -1;
        mTransferKeyColumn = mDataValid ? mCursor.getColumnIndex(FileTransferColumns.TRANSFER_KEY) : -1;
        int downloadGroupColumnIndex = mDataValid ? mCursor.getColumnIndex(FileTransferColumns.ALIAS_START_TIMESTAMP) : -1;
        int uploadGroupColumnIndex = mDataValid ? mCursor.getColumnIndex(AssetFileColumns.ALIAS_START_TIMESTAMP) : -1;
        mGroupStartTimestampColumn = mDataValid ? ( downloadGroupColumnIndex >= 0 ? downloadGroupColumnIndex : uploadGroupColumnIndex ) : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
        checkGroupsInfo();
    }

    private void checkGroupsInfo() {
        sectionsInfo = new LinkedHashMap<Integer, Bundle>();
        sectionLastItemsInfo = new LinkedHashMap<Integer, Integer>();

        if ( !mShowGroupSection ) {
            return;
        }
        if ( !mDataValid || mCursor == null ) {
            return;
        }

        int viewItemPosition = 0;
        String groupId = null;
        long groupStartTimestamp = -1;
        int groupFileCount = 0;
        int groupSectionPosition = 0;
        int groupLastItemViewPosition = 0;
        int groupLastItemCursorPosition = 0;

        for ( int i=0; i<mCursor.getCount(); i++ ) {
            mCursor.moveToPosition(i);
            long tmpRowId = mCursor.getLong(mRowIdColumn);
            String tmpGroupId = mCursor.getString(mGroupIdColumn);
            String tmpTransferKey = mCursor.getString(mTransferKeyColumn);
            long tmpGroupStartTimestamp = mCursor.getLong(mGroupStartTimestampColumn);
//            if ( Constants.DEBUG ) Log.d(TAG, "checkGroupsInfo(), rowId=" + tmpRowId + ", groupId=" + tmpGroupId + ", transferKey=" + tmpTransferKey + ", i=" + i + ", viewPos=" + viewItemPosition);
            if ( !tmpGroupId.equals(groupId) ) { // First row in group
                if ( groupId != null ) { // Save last row info in group
                    Bundle sectionInfo = new Bundle();
                    sectionInfo.putString(KEY_GROUP_ID, groupId);
                    sectionInfo.putInt(KEY_GROUP_FILE_COUNT, groupFileCount);
                    sectionInfo.putLong(KEY_GROUP_START_TIMESTAMP, groupStartTimestamp);
                    sectionsInfo.put(groupSectionPosition, sectionInfo);
                    sectionLastItemsInfo.put(groupLastItemViewPosition, groupLastItemCursorPosition);
                }
                groupId = tmpGroupId;
                groupSectionPosition = viewItemPosition;
                groupStartTimestamp = tmpGroupStartTimestamp;
                groupFileCount = 0;
                viewItemPosition++;
            }
            groupLastItemViewPosition = viewItemPosition;
            groupLastItemCursorPosition = i;
            groupFileCount++;
            viewItemPosition++;
        }
        if ( groupId != null ) {
            Bundle sectionInfo = new Bundle();
            sectionInfo.putString(KEY_GROUP_ID, groupId);
            sectionInfo.putInt(KEY_GROUP_FILE_COUNT, groupFileCount);
            sectionInfo.putLong(KEY_GROUP_START_TIMESTAMP, groupStartTimestamp);
            sectionsInfo.put(groupSectionPosition, sectionInfo);
            sectionLastItemsInfo.put(groupLastItemViewPosition, groupLastItemCursorPosition);
        }
    }

    protected Map<Integer, Bundle> getSectionsInfo() {
        return sectionsInfo;
    }

    @Override
    public int getItemViewType(int position) {
        if ( sectionsInfo != null && sectionsInfo.containsKey(position) ) {
            return TYPE_SECTION_VIEW;
        }
        return TYPE_ITEM_VIEW;
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        if (mDataValid && mCursor != null) {
            itemCount += mCursor.getCount();
        }
        if ( sectionsInfo != null ) {
            itemCount += sectionsInfo.size();
        }
        return itemCount;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public Cursor getItem(final int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_SECTION_VIEW) {
            return null;
        }
        if (mCursor == null || mCursor.isClosed()) {
            return null;
        }
        int offset = 0;
        for ( Integer pos : sectionLastItemsInfo.keySet() ) {
            if ( pos.intValue() >= position ) {
                Integer curPos = sectionLastItemsInfo.get(pos);
                offset = pos.intValue() - curPos.intValue();
                break;
            }
        }
        int p = position-offset;
        if (!mCursor.moveToPosition(p)) {
            throw new IllegalStateException("couldn't move cursor to position " + p);
        }
        return mCursor;
    }

    @Override
    public long getItemId(int position) {
        Cursor c = getItem(position);
        if ( c == null ) {
            return RecyclerView.NO_ID;
        }
        return mCursor.getLong(mRowIdColumn);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SECTION_VIEW) {
            return onCreateSectionViewHolder(parent);
        } else if (viewType == TYPE_ITEM_VIEW) {
            return onCreateItemViewHolder(parent);
        }
        return null;
    }

    public abstract VH onCreateSectionViewHolder(ViewGroup parent);

    public abstract VH onCreateItemViewHolder(ViewGroup parent);

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_SECTION_VIEW) {
            onBindSectionViewHolder(viewHolder, position);
        } else if (viewType == TYPE_ITEM_VIEW) {
            if (!mDataValid) {
                throw new IllegalStateException("this should only be called when the cursor is valid");
            }
            getItem(position);
            onBindItemViewHolder(viewHolder, mCursor);
        }
    }

    public abstract void onBindSectionViewHolder(VH viewHolder, int position);

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
            mGroupIdColumn = newCursor.getColumnIndexOrThrow(FileTransferColumns.GROUP_ID);
            mTransferKeyColumn = newCursor.getColumnIndexOrThrow(FileTransferColumns.TRANSFER_KEY);
            int downloadGroupColumnIndex = mCursor.getColumnIndex(FileTransferColumns.ALIAS_START_TIMESTAMP);
            int uploadGroupColumnIndex = mCursor.getColumnIndex(AssetFileColumns.ALIAS_START_TIMESTAMP);
            mGroupStartTimestampColumn = downloadGroupColumnIndex >= 0 ? downloadGroupColumnIndex : uploadGroupColumnIndex;
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mGroupIdColumn = -1;
            mTransferKeyColumn = -1;
            mGroupStartTimestampColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        checkGroupsInfo();
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
