package com.filelug.android.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.filelug.android.R;
import com.filelug.android.ui.widget.RemoteFilesLayout;

public class BrowseRemoteDirectoryFragment extends BaseFragment {

	private static final String TAG = BrowseRemoteDirectoryFragment.class.getSimpleName();

	private RemoteFilesLayout remoteFilesLayout = null;
	private RecyclerView recordList = null;

	public BrowseRemoteDirectoryFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		if (Constants.DEBUG) Log.d(TAG, "onCreateView()");
		View view = inflater.inflate(R.layout.fragment_remote_files, container, false);
		this.remoteFilesLayout = (RemoteFilesLayout) view.findViewById(R.id.remote_files_layout);
		this.recordList = (RecyclerView) this.remoteFilesLayout.findViewById(R.id.file_recycler_view);
		this.setHasOptionsMenu(true);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
//		if (Constants.DEBUG) Log.d(TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
		doRefreshAction();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_browse_file, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.action_sort:
				this.remoteFilesLayout.doSortAction();
				return true;
//			case R.id.action_refresh:
//				doRefreshAction();
//				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean backToParent() {
		return remoteFilesLayout.backToParent();
	}

	@Override
	public void doRefreshAction() {
//		if (Constants.DEBUG) Log.d(TAG, "doRefreshAction()");
		this.remoteFilesLayout.doRefreshAction();
	}

	@Override
	public boolean canSwipeRefreshChildScrollUp() {
		return getFirstVisiblePosition() > 0;
	}

	private int getFirstVisiblePosition() {
		int position;
		RecyclerView.LayoutManager manager = this.recordList.getLayoutManager();
		if (manager instanceof LinearLayoutManager) {
			position = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
		} else if (manager instanceof GridLayoutManager) {
			position = ((GridLayoutManager) manager).findFirstVisibleItemPosition();
		} else if (manager instanceof StaggeredGridLayoutManager) {
			StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
			int[] lastPositions = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()]);
			position = getMinPositions(lastPositions);
		} else {
			position = 0;
		}
		return position;
	}

	private int getMinPositions(int[] positions) {
		int size = positions.length;
		int minPosition = Integer.MAX_VALUE;
		for (int i = 0; i < size; i++) {
			minPosition = Math.min(minPosition, positions[i]);
		}
		return minPosition;
	}

	private int getLastVisiblePosition() {
		int position;
		RecyclerView.LayoutManager manager = this.recordList.getLayoutManager();
		if (manager instanceof LinearLayoutManager) {
			position = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
		} else if (manager instanceof GridLayoutManager) {
			position = ((GridLayoutManager) manager).findLastVisibleItemPosition();
		} else if (manager instanceof StaggeredGridLayoutManager) {
			StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
			int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
			position = getMaxPosition(lastPositions);
		} else {
			position = manager.getItemCount() - 1;
		}
		return position;
	}

	private int getMaxPosition(int[] positions) {
		int size = positions.length;
		int maxPosition = Integer.MIN_VALUE;
		for (int i = 0; i < size; i++) {
			maxPosition = Math.max(maxPosition, positions[i]);
		}
		return maxPosition;
	}

}
