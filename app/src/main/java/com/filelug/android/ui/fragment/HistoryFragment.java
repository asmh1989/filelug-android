package com.filelug.android.ui.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.BaseResponseError;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.provider.downloadhistory.DownloadHistoryColumns;
import com.filelug.android.provider.downloadhistory.DownloadHistoryContentValues;
import com.filelug.android.provider.downloadhistory.DownloadHistorySelection;
import com.filelug.android.provider.uploadhistory.UploadHistoryColumns;
import com.filelug.android.provider.uploadhistory.UploadHistoryContentValues;
import com.filelug.android.provider.uploadhistory.UploadHistorySelection;
import com.filelug.android.ui.adapter.HistoryCursorAdapter;
import com.filelug.android.ui.widget.DividerItemDecoration;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.FormatUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.NetworkUtils;
import com.filelug.android.util.PrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistoryFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = HistoryFragment.class.getSimpleName();

	public static final int HISTORY_TYPE_DOWNLOAD = 0;
	public static final int HISTORY_TYPE_UPLOAD = 1;

	protected static final int HISTORY_SEARCH_RANGE_LATEST_20 = 0;
	protected static final int HISTORY_SEARCH_RANGE_LATEST_WEEK = 1;
	protected static final int HISTORY_SEARCH_RANGE_LATEST_MONTH = 2;
	protected static final int HISTORY_SEARCH_RANGE_ALL = 3;

	private String[] searchRanges = null;

	private int historyType = HISTORY_TYPE_DOWNLOAD;
	private int historySearchType = -1;
	private RecyclerView recordList = null;
	private HistoryCursorAdapter recordListAdapter = null;
	private ImageView ivHeaderImage = null;
	private TextView tvSearchType = null;
	private TextView tvSearchReport = null;
	private TextView tvNoRecords = null;

	public HistoryFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.historyType = getArguments().getInt(Constants.EXT_PARAM_HISTORY_TYPE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		if ( Constants.DEBUG ) Log.d(TAG, "onCreateView()");
		View view = inflater.inflate(R.layout.fragment_history, container, false);
		this.recordList = (RecyclerView) view.findViewById(R.id.file_recycler_view);
		this.tvNoRecords = (TextView) view.findViewById(R.id.noRecords);
		this.setHasOptionsMenu(true);
		initListView();
		return view;
	}

	private void initListView() {
		Context ctx = getActivity();

		if ( this.historyType == HISTORY_TYPE_DOWNLOAD ) {
			this.historySearchType = PrefUtils.getDownloadSearchType();
		} else if ( this.historyType == HISTORY_TYPE_UPLOAD ) {
			this.historySearchType = PrefUtils.getUploadSearchType();
		}
		if ( this.historySearchType == -1 ) {
			this.historySearchType = HISTORY_SEARCH_RANGE_LATEST_20;
		}
		this.searchRanges = ctx.getResources().getStringArray(R.array.history_search_range_array);
		String searchTypeStr = String.format(ctx.getResources().getString(R.string.message_history_search_type), this.searchRanges[this.historySearchType]);

		this.recordListAdapter = new HistoryCursorAdapter(ctx, null, this.historyType);
		this.recordListAdapter.setSearchTypeText(searchTypeStr);
		this.recordListAdapter.setSearchReportText("");
		this.recordList.setAdapter(this.recordListAdapter);
		this.recordList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
//		this.recordList.setLayoutManager(new GridLayoutManager(this.getActivity(), 2, GridLayoutManager.VERTICAL, false));
//		this.recordList.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));
		this.recordList.addItemDecoration(new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL_LIST));
		this.recordList.setItemAnimator(new DefaultItemAnimator());

		getLoaderManager().initLoader(0, null, this);
	}

	private void showHistory(String authToken, String lugServerId, final String userId, final String computerId) {
		final Context ctx = getActivity();
		if ( !NetworkUtils.isNetworkAvailable(ctx) ) {
			onRefreshingStateChanged(false);
			return;
		}

		String locale = ctx.getResources().getConfiguration().locale.toString();
		Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				processResponseArray(response, userId, computerId);
			}
		};
		BaseResponseError responseError = new BaseResponseError(true, ctx, BaseResponseError.MESSAGE_TYPE_ERROR_MESSAGE) {
			@Override
			protected void beforeShowErrorMessage(VolleyError volleyError) {
				onRefreshingStateChanged(false);
			}
		};

		if ( this.historyType == HISTORY_TYPE_DOWNLOAD ) {
			RepositoryClient.getInstance().findAllFileDownloaded( authToken, lugServerId, this.historySearchType, locale, responseListener, responseError );
		} else if ( this.historyType == HISTORY_TYPE_UPLOAD ) {
			RepositoryClient.getInstance().findAllFileUploaded(authToken, lugServerId, this.historySearchType, locale, responseListener, responseError);
		}
	}

	private void processResponseArray(JSONArray response, String userId, String computerId) {

		Context ctx = getActivity();

		if ( this.historyType == HISTORY_TYPE_DOWNLOAD ) {

			PrefUtils.setDownloadSearchType(this.historySearchType);
			DownloadHistorySelection downloadHistorySelection = new DownloadHistorySelection();
//			downloadHistorySelection.userId(userId).and().computerId(Integer.valueOf(computerId));
			downloadHistorySelection.userId(userId);
			downloadHistorySelection.delete(ctx.getContentResolver());

		} else if ( this.historyType == HISTORY_TYPE_UPLOAD ) {

			PrefUtils.setUploadSearchType(this.historySearchType);
			UploadHistorySelection uploadHistorySelection = new UploadHistorySelection();
//			uploadHistorySelection.userId(userId).and().computerId(Integer.valueOf(computerId));
			uploadHistorySelection.userId(userId);
			uploadHistorySelection.delete(ctx.getContentResolver());

		}

		int fileCount = response.length();
		long totalSize = 0L;

		try {
			for (int i = 0; i < fileCount; i++) {
				JSONObject jso = response.getJSONObject(i);
				String computerGroup = jso.getString(Constants.PARAM_COMPUTER_GROUP);
				String computerName = jso.getString(Constants.PARAM_COMPUTER_NAME);
				long fileSize = jso.getLong(Constants.PARAM_FILE_SIZE);
				long endTimestamp = jso.getLong(Constants.PARAM_END_TIMESTAMP);
				String filename = jso.getString(Constants.PARAM_FILE_NAME);
				totalSize += fileSize;

				if ( this.historyType == HISTORY_TYPE_DOWNLOAD ) {

					DownloadHistoryContentValues values = new DownloadHistoryContentValues();
					values.putUserId(userId);
					values.putComputerId(Integer.valueOf(computerId));
					values.putComputerGroup(computerGroup);
					values.putComputerName(computerName);
					values.putFileSize(fileSize);
					values.putEndTimestamp(endTimestamp);
					values.putFileName(filename);

					Uri uri = values.insert(ctx.getContentResolver());
					long _id = ContentUris.parseId(uri);

				} else if ( this.historyType == HISTORY_TYPE_UPLOAD ) {

					UploadHistoryContentValues values = new UploadHistoryContentValues();
					values.putUserId(userId);
					values.putComputerId(Integer.valueOf(computerId));
					values.putComputerGroup(computerGroup);
					values.putComputerName(computerName);
					values.putFileSize(fileSize);
					values.putEndTimestamp(endTimestamp);
					values.putFileName(filename);

					Uri uri = values.insert(ctx.getContentResolver());
					long _id = ContentUris.parseId(uri);

				}

			}
		} catch (JSONException e) {
			Log.e(TAG, (this.historyType == HISTORY_TYPE_DOWNLOAD ? "Download" : "Upload") + " history json object parsing error!");
		}

		String searchTypeStr = String.format(ctx.getResources().getString(R.string.message_history_search_type), this.searchRanges[this.historySearchType]);
		String totalSizeStr = FormatUtils.formatFileSize(ctx, totalSize);
		int strRes = fileCount > 1 ?
				( this.historyType == HISTORY_TYPE_DOWNLOAD ? R.string.message_history_files_downloaded : R.string.message_history_files_uploaded ) :
				( this.historyType == HISTORY_TYPE_DOWNLOAD ? R.string.message_history_file_downloaded : R.string.message_history_file_uploaded );
		String searchReportStr = String.format(ctx.getResources().getString(strRes), fileCount, totalSizeStr);

		this.recordListAdapter.setSearchTypeText(searchTypeStr);
		this.recordListAdapter.setSearchReportText(searchReportStr);
		this.recordListAdapter.notifyDataSetChanged();
		onRefreshingStateChanged(false);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_history, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.action_search_range:
				doSearchRangeAction();
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
		return super.backToParent();
	}

	@Override
	public boolean canSwipeRefreshChildScrollUp() {
		boolean canSwipe = getFirstVisiblePosition() != 0;
//		if ( Constants.DEBUG ) Log.d(TAG, "canSwipeRefreshChildScrollUp(): canSwipe=" + canSwipe);
		return canSwipe;
	}

	private int getFirstVisiblePosition() {
		String logStr = "getFirstVisiblePosition(): ";
		int position;
		RecyclerView.LayoutManager manager = this.recordList.getLayoutManager();
		if (manager instanceof LinearLayoutManager) {
			position = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
			logStr += "Is LinearLayoutManager, position=" + position;
		} else if (manager instanceof GridLayoutManager) {
			position = ((GridLayoutManager) manager).findFirstVisibleItemPosition();
			logStr += "Is GridLayoutManager, position=" + position;
		} else if (manager instanceof StaggeredGridLayoutManager) {
			StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
			int[] lastPositions = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()]);
			position = getMinPositions(lastPositions);
			logStr += "Is StaggeredGridLayoutManager, position=" + position;
		} else {
			position = 0;
			logStr += "Others, position=" + position;
		}
//		if ( Constants.DEBUG ) Log.d(TAG, logStr);
		return position;
	}

	private int getMinPositions(int[] positions) {
//		if ( Constants.DEBUG ) Log.d(TAG, "getMinPositions(): positions=" + positions);
		int size = positions.length;
		int minPosition = Integer.MAX_VALUE;
		for (int i = 0; i < size; i++) {
			minPosition = Math.min(minPosition, positions[i]);
//			if ( Constants.DEBUG ) Log.d(TAG, "getMinPositions(): positions[" + i + "]=" + positions[i] + ", minPosition=" + minPosition);
		}
		return minPosition;
	}

	private int getLastVisiblePosition() {
		String logStr = "getLastVisiblePosition(): ";
		int position;
		RecyclerView.LayoutManager manager = this.recordList.getLayoutManager();
		if (manager instanceof LinearLayoutManager) {
			position = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
			logStr += "Is LinearLayoutManager, position=" + position;
		} else if (manager instanceof GridLayoutManager) {
			position = ((GridLayoutManager) manager).findLastVisibleItemPosition();
			logStr += "Is GridLayoutManager, position=" + position;
		} else if (manager instanceof StaggeredGridLayoutManager) {
			StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
			int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
			position = getMaxPosition(lastPositions);
			logStr += "Is StaggeredGridLayoutManager, position=" + position;
		} else {
			position = manager.getItemCount() - 1;
			logStr += "Others, position=" + position;
		}
//		if ( Constants.DEBUG ) Log.d(TAG, logStr);
		return position;
	}

	private int getMaxPosition(int[] positions) {
//		if ( Constants.DEBUG ) Log.d(TAG, "getMaxPosition(): positions=" + positions);
		int size = positions.length;
		int maxPosition = Integer.MIN_VALUE;
		for (int i = 0; i < size; i++) {
			maxPosition = Math.max(maxPosition, positions[i]);
//			if ( Constants.DEBUG ) Log.d(TAG, "getMaxPosition(): positions[" + i + "]=" + positions[i] + ", maxPosition=" + maxPosition);
		}
		return maxPosition;
	}

	public void doSearchRangeAction() {
		MaterialDialog.ListCallbackSingleChoice callback = new MaterialDialog.ListCallbackSingleChoice() {
			@Override
			public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
				HistoryFragment.this.historySearchType = which;
				doRefreshAction();
				dialog.dismiss();
				return true; // allow selection
			}
		};
		DialogUtils.createSingleChoiceDialog(
				getActivity(),
				R.string.action_history_search_range,
				R.drawable.ic_action_search,
				this.searchRanges,
				this.historySearchType,
				callback
		).show();
	}

	@Override
	public void doRefreshAction() {
		onRefreshingStateChanged(true);
		getAuthToken();
	}

	private void getAuthToken() {
		AccountUtils.AuthTokenCallback callback = new AccountUtils.AuthTokenCallback() {
			@Override
			public void onError(String errorMessage) {
				MsgUtils.showWarningMessage(getActivity(), errorMessage);
				onRefreshingStateChanged(false);
			}
			@Override
			public void onSuccess(String authToken) {
				Account activeAccount = AccountUtils.getActiveAccount();
				AccountManager accountManager = AccountManager.get(getActivity());
				String lugServerId = accountManager.getUserData(activeAccount, Constants.PARAM_LUG_SERVER_ID);
				String userId = accountManager.getUserData(activeAccount, Constants.EXT_PARAM_FILELUG_ACCOUNT);
				String computerId = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_ID);
				showHistory(authToken, lugServerId, userId, computerId);
			}
		};
		AccountUtils.getAuthToken(getActivity(), callback);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Context ctx = getActivity();
		Account activeAccount = AccountUtils.getActiveAccount();
		AccountManager accountManager = AccountManager.get(ctx);
		String userId = accountManager.getUserData(activeAccount, Constants.EXT_PARAM_FILELUG_ACCOUNT);
		String computerId = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_ID);

		Uri baseUri = null;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;

		if ( this.historyType == HISTORY_TYPE_DOWNLOAD ) {

			DownloadHistorySelection downloadHistorySelection = new DownloadHistorySelection();
			baseUri = downloadHistorySelection.uri();
			projection = new String[] {
					DownloadHistoryColumns._ID,
					DownloadHistoryColumns.COMPUTER_GROUP,
					DownloadHistoryColumns.COMPUTER_NAME,
					DownloadHistoryColumns.FILE_SIZE,
					DownloadHistoryColumns.END_TIMESTAMP,
					DownloadHistoryColumns.FILE_NAME
			};
			selection = DownloadHistoryColumns.USER_ID + " = ?";
			selectionArgs = new String[] { userId };
			sortOrder = DownloadHistoryColumns.END_TIMESTAMP + " DESC";

		} else if ( this.historyType == HISTORY_TYPE_UPLOAD ) {

			UploadHistorySelection uploadHistorySelection = new UploadHistorySelection();
			baseUri = uploadHistorySelection.uri();
			projection = new String[] {
					UploadHistoryColumns._ID,
					UploadHistoryColumns.COMPUTER_GROUP,
					UploadHistoryColumns.COMPUTER_NAME,
					UploadHistoryColumns.FILE_SIZE,
					UploadHistoryColumns.END_TIMESTAMP,
					UploadHistoryColumns.FILE_NAME
			};
			selection = DownloadHistoryColumns.USER_ID + " = ?";
			selectionArgs = new String[] { userId };
			sortOrder = UploadHistoryColumns.END_TIMESTAMP + " DESC";

		}

		return new CursorLoader(getActivity(), baseUri, projection, selection, selectionArgs, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		this.recordListAdapter.swapCursor(data);
		// The list should now be shown.
//		if (isResumed()) {
//			setListShown(true);
//		} else {
//			setListShownNoAnimation(true);
//		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		this.recordListAdapter.swapCursor(null);
	}

}
