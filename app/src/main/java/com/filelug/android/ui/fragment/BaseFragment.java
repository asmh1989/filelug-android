package com.filelug.android.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filelug.android.R;
import com.filelug.android.ui.widget.MultiSwipeRefreshLayout;

public class BaseFragment extends Fragment implements MultiSwipeRefreshLayout.CanChildScrollUpCallback {

	private static final String TAG = BaseFragment.class.getSimpleName();

	private SwipeRefreshLayout mSwipeRefreshLayout;

	public BaseFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		if ( Constants.DEBUG ) Log.d(TAG, "onActivityCreated()");
		trySetupSwipeRefresh();
		updateSwipeRefreshProgressBarTop();
	}

	private void trySetupSwipeRefresh() {
//		if ( Constants.DEBUG ) Log.d(TAG, "trySetupSwipeRefresh()");
		mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh_layout);
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setColorSchemeResources(
				R.color.main_color_A100,
				R.color.main_color_500,
				R.color.material_red_500);
			mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
//					if ( Constants.DEBUG ) Log.d(TAG, "trySetupSwipeRefresh() --> OnRefreshListener.onRefresh()");
					doRefreshAction();
				}
			});
//			if ( Constants.DEBUG ) Log.d(TAG, "trySetupSwipeRefresh(): mSwipeRefreshLayout=" + mSwipeRefreshLayout.getClass().getName());
			if (mSwipeRefreshLayout instanceof MultiSwipeRefreshLayout) {
				MultiSwipeRefreshLayout mswrl = (MultiSwipeRefreshLayout) mSwipeRefreshLayout;
				mswrl.setCanChildScrollUpCallback(this);
			}
		}
	}

	private void updateSwipeRefreshProgressBarTop() {
//		if ( Constants.DEBUG ) Log.d(TAG, "updateSwipeRefreshProgressBarTop()");
		if (mSwipeRefreshLayout == null) {
			return;
		}
		int progressBarStartMargin = getResources().getDimensionPixelSize(R.dimen.swipe_refresh_progress_bar_start_margin);
		int progressBarEndMargin = getResources().getDimensionPixelSize(R.dimen.swipe_refresh_progress_bar_end_margin);
		int top = 0;
//		if ( Constants.DEBUG ) Log.d(TAG, "updateSwipeRefreshProgressBarTop(): start=" + (top + progressBarStartMargin) + ", end=" + (top + progressBarEndMargin));
		mSwipeRefreshLayout.setProgressViewOffset(false, top + progressBarStartMargin, top + progressBarEndMargin);
	}

	public void onRefreshingStateChanged(boolean refreshing) {
//		if ( Constants.DEBUG ) Log.d(TAG, "onRefreshingStateChanged(): refreshing=" + refreshing + ", mSwipeRefreshLayout=" + mSwipeRefreshLayout);
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(refreshing);
		}
	}

	public void enableDisableSwipeRefresh(boolean enable) {
//		if ( Constants.DEBUG ) Log.d(TAG, "onRefreshingStateChanged(): enable=" + enable + ", mSwipeRefreshLayout=" + mSwipeRefreshLayout);
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setEnabled(enable);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	public boolean backToParent() {
		return false;
	}

	@Override
	public boolean canSwipeRefreshChildScrollUp() {
		return false;
	}

	protected void doRefreshAction() {
	}

}
