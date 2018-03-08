package com.filelug.android.ui.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.BaseResponseError;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.crepo.RepositoryErrorObject;
import com.filelug.android.crepo.RepositoryUtility;
import com.filelug.android.provider.assetfile.AssetFileColumns;
import com.filelug.android.provider.assetfile.AssetFileContentValues;
import com.filelug.android.provider.assetfile.AssetFileCursor;
import com.filelug.android.provider.assetfile.AssetFileSelection;
import com.filelug.android.provider.assetfile.UploadStatusType;
import com.filelug.android.provider.downloadgroup.DownloadGroupContentValues;
import com.filelug.android.provider.downloadgroup.DownloadGroupCursor;
import com.filelug.android.provider.downloadgroup.DownloadGroupSelection;
import com.filelug.android.provider.filetransfer.DownloadStatusType;
import com.filelug.android.provider.filetransfer.FileTransferColumns;
import com.filelug.android.provider.filetransfer.FileTransferContentValues;
import com.filelug.android.provider.filetransfer.FileTransferCursor;
import com.filelug.android.provider.filetransfer.FileTransferSelection;
import com.filelug.android.provider.filetransfer.RemoteObjectType;
import com.filelug.android.provider.uploadgroup.UploadGroupContentValues;
import com.filelug.android.provider.uploadgroup.UploadGroupCursor;
import com.filelug.android.provider.uploadgroup.UploadGroupSelection;
import com.filelug.android.service.DownloadNotificationService;
import com.filelug.android.service.DownloadService;
import com.filelug.android.service.UploadService;
import com.filelug.android.ui.activity.MainActivity;
import com.filelug.android.ui.adapter.TransferCursorAdapter;
import com.filelug.android.ui.adapter.TransferLoader;
import com.filelug.android.ui.widget.DividerItemDecoration;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.FilelugUtils;
import com.filelug.android.util.LocalFileUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.NotificationUtils;
import com.filelug.android.util.RemoteFileUtils;
import com.filelug.android.util.TransferDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vincent Chang on 2015/9/1.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class TransferFragment extends BaseFragment {

	private static final String TAG = TransferFragment.class.getSimpleName();

	private int transferType = Constants.TRANSFER_TYPE_DOWNLOAD;
	private int nCallbackType = -1;
	private long nCallbackRowId = -1L;

	private RecyclerView recordList = null;
	private TransferCursorAdapter recordListAdapter = null;
	private TextView tvNoRecords = null;

	public TransferFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if ( Constants.DEBUG ) Log.d(TAG, "onCreate(), savedInstanceState=" + MiscUtils.convertBundleToString(savedInstanceState));
		Bundle args = getArguments();
		this.transferType = args.getInt(Constants.EXT_PARAM_TRANSFER_TYPE);
		this.nCallbackType = args.getInt(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, -1);
		this.nCallbackRowId = args.getLong(Constants.EXT_PARAM_ROW_ID, -1l);
//		if ( Constants.DEBUG ) Log.d(TAG, "onCreate(), transferType=" + transferType + ", nCallbackType=" + nCallbackType + ", nCallbackRowId=" + nCallbackRowId);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		if ( Constants.DEBUG ) Log.d(TAG, "onCreateView(), savedInstanceState=" + MiscUtils.convertBundleToString(savedInstanceState));
		View view = inflater.inflate(R.layout.fragment_transfer, container, false);
		this.recordList = (RecyclerView) view.findViewById(R.id.file_recycler_view);
		this.tvNoRecords = (TextView) view.findViewById(R.id.noRecords);
		this.setHasOptionsMenu(true);
		initListView();
		return view;
	}

	private void initListView() {
		final Context ctx = getActivity();

		this.recordListAdapter = new TransferCursorAdapter(ctx, null, this.transferType);
		this.recordList.setAdapter(this.recordListAdapter);
		this.recordListAdapter.setOnItemClickListener(new TransferCursorAdapter.OnItemClickListener() {
			@Override
			public void onItemClicked(Cursor cursor) {
				recordList_onItemClicked(cursor);
			}
		});
		this.recordList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
//		this.recordList.setLayoutManager(new GridLayoutManager(this.getActivity(), 2, GridLayoutManager.VERTICAL, false));
//		this.recordList.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));
		this.recordList.addItemDecoration(new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL_LIST));
		this.recordList.setItemAnimator(new DefaultItemAnimator());

		getLoaderManager().initLoader(0, null, new TransferLoader(ctx, this.recordListAdapter, this.transferType));
	}

	private void recordList_onItemClicked(Cursor cursor) {

		final Activity activity = getActivity();
		int status = -1;

		if (transferType == Constants.TRANSFER_TYPE_DOWNLOAD) {
			status = cursor.getInt(cursor.getColumnIndex(FileTransferColumns.STATUS));
			if ( status == DownloadStatusType.success.ordinal() ) {
				showActionsDialogForDownloadSuccess(activity, status, cursor);
			} else if ( status == DownloadStatusType.failure.ordinal() ||
						status == DownloadStatusType.canceling.ordinal() ||
						status == DownloadStatusType.desktop_uploaded_but_unconfirmed.ordinal() ) {
				showActionsDialogForDownloadFailed(activity, status, cursor);
			} else if ( status == DownloadStatusType.wait.ordinal() ) {
				showActionsDialogForCancelDownload(activity, status, cursor);
			} else if ( status == DownloadStatusType.processing.ordinal() ) {
				showActionsDialogForStopDownload(activity, status, cursor);
			}
		} else if (transferType == Constants.TRANSFER_TYPE_UPLOAD) {
			status = cursor.getInt(cursor.getColumnIndex(AssetFileColumns.STATUS));
			if ( status == UploadStatusType.success.ordinal() ) {
				showActionsDialogForUploadSuccess(activity, status, cursor);
			} else if ( status == UploadStatusType.failure.ordinal() ||
						status == UploadStatusType.canceling.ordinal() ||
						status == UploadStatusType.device_uploaded_but_unconfirmed.ordinal() ) {
				showActionsDialogForUploadFailed(activity, status, cursor);
			} else if ( status == UploadStatusType.wait.ordinal() ) {
				showActionsDialogForCancelUpload(activity, status, cursor);
			} else if ( status == UploadStatusType.processing.ordinal() ) {
				showActionsDialogForStopUpload(activity, status, cursor);
			}
		}

	}

	private void downloadFileAndRenewGroupID(final Context context, final String userId, final int computerId, final String lugServerId, final String authToken, String groupId, String transferKey, final String fileName) {
		DownloadGroupSelection downloadGroupSelection = new DownloadGroupSelection()
			.userId(userId).and()
			.computerId(computerId).and()
			.groupId(groupId);
		DownloadGroupCursor c1 = downloadGroupSelection.query(context.getContentResolver(), null, null);
		if ( !c1.moveToFirst() ) {
			c1.close();
			Log.e(TAG, "downloadFileAndRenewGroupID(), Group ID: " + groupId + " not found!");
			return;
		}

		final String newDownloadGroupId = RepositoryUtility.generateDownloadGroupId(fileName);
		final boolean fromAnotherApp = c1.getFromAnotherApp();
		String downloadPath = c1.getLocalPath();
		final int subDirType = c1.getSubdirectoryType();
		String subDirValue = c1.getSubdirectoryValue();
		final int descriptionType = c1.getDescriptionType();
		String descriptionValue = c1.getDescriptionValue();
		int notificationType = c1.getNotificationType();
		c1.close();

		String newSubDirValue = null;
		if ( subDirType == 0 || subDirType == 2 ) {
			newSubDirValue = subDirValue;
		} else {
			String cSubDirValue = null;
			SimpleDateFormat formatter = new SimpleDateFormat(context.getResources().getString(R.string.const_format_dateTime_subdir));
			String dateTimeStr = formatter.format(new Date());
			if ( subDirType == 3 ) {
				cSubDirValue = subDirValue.substring(dateTimeStr.length(), subDirValue.length());
			} else if ( subDirType == 4 ) {
				cSubDirValue = subDirValue.substring(0, subDirValue.length()-dateTimeStr.length());
			}
			newSubDirValue = MiscUtils.getCustomizedSubDirName(context, subDirType, cSubDirValue);
		}

		String newDownloadPath = null;
		if ( subDirType == 0 ) {
			newDownloadPath = downloadPath;
		} else {
			newDownloadPath = downloadPath.substring(0, downloadPath.indexOf(subDirValue)) + newSubDirValue;
		}
		final File newDownloadPathFile = new File(newDownloadPath);
		if ( !newDownloadPathFile.exists() ) {
			newDownloadPathFile.mkdir();
		}

		String lineSeparator = "\n";
		String newDescriptionValue = null;
		if ( descriptionType == 0 || descriptionType == 2 ) {
			newDescriptionValue = descriptionValue;
		} else {
			String cDescriptionValue = null;
			String fileListPrefix = context.getResources().getString(R.string.content_header_file_download_list) + lineSeparator;
			String fileListStr = fileListPrefix + fileName + lineSeparator;
			if ( descriptionType == 3 ) {
				int fileListHeaderIndex = descriptionValue.indexOf(lineSeparator + lineSeparator + fileListPrefix);
				cDescriptionValue = descriptionValue.substring(0, fileListHeaderIndex);
			}
			newDescriptionValue = MiscUtils.getCustomizedDescription(context, descriptionType, cDescriptionValue, fileListStr, lineSeparator);
		}

		final File newDescriptionFile;
		try {
			newDescriptionFile = RemoteFileUtils.writeDescription(context, newDownloadPathFile, descriptionType, newDescriptionValue);
		} catch (Exception e) {
			MsgUtils.showErrorMessage(context, e.getMessage());
			return;
		}

		DownloadGroupContentValues downloadGroupValues = new DownloadGroupContentValues()
			.putUserId(userId)
			.putComputerId(computerId)
			.putGroupId(newDownloadGroupId)
			.putFromAnotherApp(fromAnotherApp)
			.putStartTimestamp(new Date().getTime())
			.putLocalPath(newDownloadPath)
			.putSubdirectoryType(subDirType)
			.putSubdirectoryValue(newSubDirValue)
			.putDescriptionType(descriptionType)
			.putDescriptionValue(newDescriptionValue)
			.putNotificationType(notificationType);
		downloadGroupValues.insert(context.getContentResolver());

		FileTransferSelection fileTransferSelection = new FileTransferSelection()
			.userId(userId).and()
			.computerId(computerId).and()
			.groupId(groupId).and()
			.transferKey(transferKey);
		FileTransferCursor c2 = fileTransferSelection.query(context.getContentResolver(), null, null);
		if ( !c2.moveToFirst() ) {
			c2.close();
			Log.e(TAG, "downloadFileAndRenewGroupID(), Transfer key: " + transferKey + " not found!");
			return;
		}

		final String newTransferKey = RepositoryUtility.generateDownloadTransferKey(userId, fileName);
		final RemoteObjectType objectType = c2.getType();
		final String fullName = c2.getServerPath();
		final String fullRealName = c2.getRealServerPath();
		final String fileRealName = c2.getRealLocalFileName();
		final long fileSize = c2.getTotalSize();
		final String contentType = c2.getContentType();
		final String lastModified = c2.getLastModified();
		c2.close();

		Map<String, String> keyPaths = new LinkedHashMap<String, String>();
		keyPaths.put(newTransferKey, fullRealName);

//		if ( Constants.DEBUG ) Log.d(TAG, "downloadFileAndRenewGroupID(), newDownloadGroupId=" + newDownloadGroupId + ", newTransferKey=" + newTransferKey);

		String locale = context.getResources().getConfiguration().locale.toString();
		RepositoryClient.getInstance().createFileDownloadSummary(
			authToken,
			lugServerId,
			newDownloadGroupId,
			keyPaths,
			newDownloadPath,
			subDirType,
			newSubDirValue,
			descriptionType,
			newDescriptionValue,
			notificationType,
			locale,
			new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					FileTransferContentValues fileTransferValues = new FileTransferContentValues()
						.putUserId(userId)
						.putComputerId(computerId)
						.putGroupId(newDownloadGroupId)
						.putTransferKey(newTransferKey)
						.putType(objectType)
						.putServerPath(fullName)
						.putRealServerPath(fullRealName)
						.putLocalFileName(fileName)
						.putRealLocalFileName(fileRealName)
						.putTotalSize(fileSize)
						.putTransferredSize(0l)
						.putContentType(contentType)
						.putLastModified(lastModified)
						.putStatus(DownloadStatusType.wait)
						.putWaitToConfirm(false);
					fileTransferValues.insert(context.getContentResolver());

					RemoteFileUtils.downloadFile(context, userId, computerId, lugServerId, authToken, newDownloadGroupId, newTransferKey, fromAnotherApp);
				}
			},
			new BaseResponseError(true, getActivity()) {
				@Override
				protected void afterShowErrorMessage(VolleyError volleyError) {
					super.afterShowErrorMessage(volleyError);
					if ( descriptionType != 0 ) {
						newDescriptionFile.delete();
					}
					if ( subDirType == 1 || subDirType == 3 || subDirType == 4 ) {
						newDownloadPathFile.delete();
					}
				}
			}
		);
	}

	private void checkDirectOrResumeUpload(final Context context, final String userId, final int computerId, final String lugServerId, final String authToken, final String groupId, final String transferKey, String fileName) {
		String locale = context.getResources().getConfiguration().locale.toString();
		Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				String tKey = response.optString(Constants.PARAM_TRANSFER_KEY);
				long transferredSize = response.optLong(Constants.PARAM_TRANSFERRED_SIZE);
				long fileSize = response.optLong(Constants.PARAM_FILE_SIZE);
				long fileLastModifiedDate = response.optLong(Constants.PARAM_FILE_LAST_MODIFIED_DATE);
//				if ( Constants.DEBUG ) Log.d(TAG, "checkDirectOrResumeUpload().onResponse, transferKey=" + tKey + ", transferredSize=" + transferredSize + ", fileSize=" + fileSize + ", fileLastModifiedDate=" + fileLastModifiedDate);
				LocalFileUtils.uploadFile_resume(context, userId, computerId, lugServerId, authToken, groupId, transferKey, transferredSize, fileSize, fileLastModifiedDate);
			}
		};
		BaseResponseError error = new BaseResponseError(true, context) {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				int statusCode = MiscUtils.getStatusCode(volleyError);
				if ( statusCode == 400 ) {
					LocalFileUtils.uploadFile(context, userId, computerId, lugServerId, authToken, groupId, transferKey, false);
				} else {
					super.onErrorResponse(volleyError);
				}
			}
		};
		RepositoryClient.getInstance().findFileUploadedByTransferKey(
			authToken,
			lugServerId,
			transferKey,
			locale,
			response,
			error
		);
	}

	private void uploadFileAndRenewGroupID(final Context context, final String userId, final int computerId, final String lugServerId, final String authToken, String groupId, String transferKey, final String fileName, String lineSeparator) {
		UploadGroupSelection uploadGroupSelection = new UploadGroupSelection()
			.userId(userId).and()
			.computerId(computerId).and()
			.groupId(groupId);
		UploadGroupCursor c1 = uploadGroupSelection.query(context.getContentResolver(), null, null);
		if ( !c1.moveToFirst() ) {
			c1.close();
			Log.e(TAG, "uploadFileAndRenewGroupID(), Group ID: " + groupId + " not found!");
			return;
		}

		final String newUploadGroupId = RepositoryUtility.generateUploadGroupId(fileName);
		final boolean fromAnotherApp = c1.getFromAnotherApp();
		String uploadPath = c1.getUploadDirectory();
		int subDirType = c1.getSubdirectoryType();
		String subDirValue = c1.getSubdirectoryValue();
		int descriptionType = c1.getDescriptionType();
		String descriptionValue = c1.getDescriptionValue();
		int notificationType = c1.getNotificationType();
		c1.close();

		String newSubDirValue = null;
		if ( subDirType == 0 || subDirType == 2 ) {
			newSubDirValue = subDirValue;
		} else {
			String cSubDirValue = null;
			SimpleDateFormat formatter = new SimpleDateFormat(context.getResources().getString(R.string.const_format_dateTime_subdir));
			String dateTimeStr = formatter.format(new Date());
			if ( subDirType == 3 ) {
				cSubDirValue = subDirValue.substring(dateTimeStr.length(), subDirValue.length());
			} else if ( subDirType == 4 ) {
				cSubDirValue = subDirValue.substring(0, subDirValue.length()-dateTimeStr.length());
			}
			newSubDirValue = MiscUtils.getCustomizedSubDirName(context, subDirType, cSubDirValue);
		}
		String newDescriptionValue = null;
		if ( descriptionType == 0 || descriptionType == 2 ) {
			newDescriptionValue = descriptionValue;
		} else {
			String cDescriptionValue = null;
			String fileListPrefix = context.getResources().getString(R.string.content_header_file_upload_list) + lineSeparator;
			String fileListStr = fileListPrefix + fileName + lineSeparator;
			if ( descriptionType == 3 ) {
				int fileListHeaderIndex = descriptionValue.indexOf(lineSeparator + lineSeparator + fileListPrefix);
				cDescriptionValue = descriptionValue.substring(0, fileListHeaderIndex);
			}
			newDescriptionValue = MiscUtils.getCustomizedDescription(context, descriptionType, cDescriptionValue, fileListStr, lineSeparator);
		}

		UploadGroupContentValues uploadGroupValues = new UploadGroupContentValues()
			.putUserId(userId)
			.putComputerId(computerId)
			.putGroupId(newUploadGroupId)
			.putFromAnotherApp(fromAnotherApp)
			.putStartTimestamp(new Date().getTime())
			.putUploadDirectory(uploadPath)
			.putSubdirectoryType(subDirType)
			.putSubdirectoryValue(newSubDirValue)
			.putDescriptionType(descriptionType)
			.putDescriptionValue(newDescriptionValue)
			.putNotificationType(notificationType);
		uploadGroupValues.insert(context.getContentResolver());

		AssetFileSelection assetFileSelection = new AssetFileSelection()
			.userId(userId).and()
			.computerId(computerId).and()
			.groupId(groupId).and()
			.transferKey(transferKey);
		AssetFileCursor c2 = assetFileSelection.query(context.getContentResolver(), null, null);
		if ( !c2.moveToFirst() ) {
			c2.close();
			Log.e(TAG, "uploadFileAndRenewGroupID(), Transfer key: " + transferKey + " not found!");
			return;
		}

		final String newTransferKey = RepositoryUtility.generateUploadTransferKey(userId, fileName);
		final String fullName = c2.getAssetUrl();
		final long fileSize = c2.getTotalSize();
		final String contentType = c2.getContentType();
		final long lastModifiedDate = c2.getLastModifiedTimestamp();
		String[] transferKeyArray = new String[] { newTransferKey };
		c2.close();

//		if ( Constants.DEBUG ) Log.d(TAG, "uploadFileAndRenewGroupID(), newUploadGroupId=" + newUploadGroupId + ", newTransferKey=" + newTransferKey);

		RepositoryClient.getInstance().createFileUploadSummary(
			authToken,
			lugServerId,
			newUploadGroupId,
			transferKeyArray,
			uploadPath,
			subDirType,
			newSubDirValue,
			descriptionType,
			newDescriptionValue,
			notificationType,
			context.getResources().getConfiguration().locale.toString(),
			new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					AssetFileContentValues assetFileValues = new AssetFileContentValues()
						.putUserId(userId)
						.putComputerId(computerId)
						.putGroupId(newUploadGroupId)
						.putTransferKey(newTransferKey)
						.putAssetUrl(fullName)
						.putServerFileName(fileName)
						.putTotalSize(fileSize)
						.putTransferredSize(0l)
						.putContentType(contentType)
						.putLastModifiedTimestamp(lastModifiedDate)
						.putStatus(UploadStatusType.wait)
						.putWaitToConfirm(false);
					assetFileValues.insert(context.getContentResolver());

					LocalFileUtils.uploadFile(context, userId, computerId, lugServerId, authToken, newUploadGroupId, newTransferKey, fromAnotherApp);
				}
			},
			new BaseResponseError(true, context)
		);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		if ( Constants.DEBUG ) Log.d(TAG, "onActivityCreated(), savedInstanceState=" + MiscUtils.convertBundleToString(savedInstanceState));
		if ( this.nCallbackType == Constants.NOTIFICATION_CALLBACK_TYPE_GCM_RESUME_UPLOAD_FILE ) {
			// Resume upload
			resumeUploadFromNotification(this.nCallbackRowId);
		} else if ( this.nCallbackType == Constants.NOTIFICATION_CALLBACK_TYPE_GCM_RESUME_DOWNLOAD_FILE ) {
			// Resume download
			resumeDownloadFromNotification(this.nCallbackRowId);
		} else {
			checkTransferStatus();
		}
	}

	private void resumeUploadFromNotification(long rowId) {
		Context context = getActivity();
		String msgRecordNotFound = context.getResources().getString(R.string.message_cannot_find_upload_record);
		if ( rowId < 0 ) {
			MsgUtils.showToast(context, msgRecordNotFound);
			return;
		}

		String groupId = null;
		String transferKey = null;
		String fileName = null;
		boolean fromAnotherApp = false;
		String[] projection = new String[] { AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.GROUP_ID, AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.TRANSFER_KEY, AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.SERVER_FILE_NAME, AssetFileColumns.UG_FROM_ANOTHER_APP_WITH_ALIAS };
		AssetFileSelection selection = new AssetFileSelection()
			.id(rowId);
		AssetFileCursor c = selection.query(context.getContentResolver(), projection);
		if ( c.moveToFirst() ) {
			groupId = c.getGroupId();
			transferKey = c.getTransferKey();
			fileName = c.getServerFileName();
			fromAnotherApp = c.getUGFromAnotherApp();
		}
		c.close();

		if ( groupId == null ) {
			MsgUtils.showToast(context, msgRecordNotFound);
		} else {
			resumeUploadFile(context, fileName, groupId, transferKey, fromAnotherApp);
		}
	}

	private void resumeDownloadFromNotification(long rowId) {
		Context context = getActivity();
		String msgRecordNotFound = context.getResources().getString(R.string.message_cannot_find_download_record);
		if ( rowId < 0 ) {
			MsgUtils.showToast(context, msgRecordNotFound);
			return;
		}

		String groupId = null;
		String transferKey = null;
		String fileName = null;
		boolean fromAnotherApp = false;
		String[] projection = new String[] { FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.GROUP_ID, FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.TRANSFER_KEY, FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.LOCAL_FILE_NAME, FileTransferColumns.DG_FROM_ANOTHER_APP_WITH_ALIAS };
		FileTransferSelection selection = new FileTransferSelection()
			.id(rowId);
		FileTransferCursor c = selection.query(context.getContentResolver(), projection);
		if ( c.moveToFirst() ) {
			groupId = c.getGroupId();
			transferKey = c.getTransferKey();
			fileName = c.getLocalFileName();
			fromAnotherApp = c.getDGFromAnotherApp();
		}
		c.close();

		if ( groupId == null ) {
			MsgUtils.showToast(context, msgRecordNotFound);
		} else {
			resumeDownloadFile(context, fileName, groupId, transferKey, fromAnotherApp);
		}
	}

	private void checkTransferStatus() {
		final int tType = this.transferType;
//		if (Constants.DEBUG) Log.d(TAG, "checkTransferStatus(), transferType=" + transferType);
		final Activity activity = getActivity();
		if ( tType == Constants.TRANSFER_TYPE_DOWNLOAD && MiscUtils.isServiceRunning(DownloadService.class) ||
			 tType == Constants.TRANSFER_TYPE_UPLOAD && MiscUtils.isServiceRunning(UploadService.class) ) {
			return;
		}

		FilelugUtils.Callback callback = new FilelugUtils.Callback() {
			@Override
			public void onError(int errorCode, String errorMessage) {
			}
			@Override
			public void onSuccess(Bundle result) {
				long uploadSizeLimit = result.getLong(Constants.PARAM_UPLOAD_SIZE_LIMIT);
				long downloadSizeLimit = result.getLong(Constants.PARAM_DOWNLOAD_SIZE_LIMIT);
				String authToken = result.getString(AccountManager.KEY_AUTHTOKEN);

				Account activeAccount = AccountUtils.getActiveAccount();
				AccountManager accountManager = AccountManager.get(activity);
				String userId = accountManager.getUserData(activeAccount, Constants.EXT_PARAM_FILELUG_ACCOUNT);
				String computerId = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_ID);
				String lugServerId = accountManager.getUserData(activeAccount, Constants.PARAM_LUG_SERVER_ID);

				if (tType == Constants.TRANSFER_TYPE_DOWNLOAD) {
					checkDownloadTransferStatus(activity, userId, Integer.valueOf(computerId).intValue(), lugServerId, authToken);
				} else if (tType == Constants.TRANSFER_TYPE_UPLOAD) {
					checkUploadTransferStatus(activity, userId, Integer.valueOf(computerId).intValue(), lugServerId, authToken);
				}
			}
		};
		FilelugUtils.pingDesktopB(activity, callback);
	}

	private void checkDownloadTransferStatus(final Context context, final String userId, int computerId, String lugServerId, String authToken) {
//		if ( Constants.DEBUG ) Log.d(TAG, "checkDownloadTransferStatus()");
		if ( !MiscUtils.isServiceRunning(DownloadService.class) ) {
			TransferDBHelper.writeDownloadFailedStatus(userId, computerId);
		}

		String locale = context.getResources().getConfiguration().locale.toString();
		String[] projection = new String[] {
			FileTransferColumns._ID,
			FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.GROUP_ID,
			FileTransferColumns.TRANSFER_KEY,
			FileTransferColumns.LOCAL_FILE_NAME,
			FileTransferColumns.STATUS,
			FileTransferColumns.TRANSFERRED_SIZE,
			FileTransferColumns.WAIT_TO_CONFIRM,
			FileTransferColumns.DG_FROM_ANOTHER_APP_WITH_ALIAS,
			FileTransferColumns.DG_NOTIFICATION_TYPE_WITH_ALIAS
		};
		String sortOrder =
			FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.GROUP_ID + " ASC, " +
			FileTransferColumns.TABLE_NAME + "." + FileTransferColumns.TRANSFER_KEY + " ASC ";
		FileTransferSelection fileTransferSelection = new FileTransferSelection();
		fileTransferSelection
			.userId(userId).and()
			.computerId(computerId).and()
			.dgFromAnotherApp(false).and()
			.openParen()
			.waitToConfirm(true).or()
			.status(DownloadStatusType.wait).or()
			.status(DownloadStatusType.desktop_uploaded_but_unconfirmed)
			.closeParen();
		FileTransferCursor c = fileTransferSelection.query(context.getContentResolver(), projection, sortOrder);
		List<Bundle> reconfirmRows = new ArrayList<Bundle>();
		while (c.moveToNext()) {
			long rowId = c.getId();
			String groupId = c.getGroupId();
			String transferKey = c.getTransferKey();
			boolean fromAnotherApp = c.getDGFromAnotherApp();
			int notificationType = c.getDGNotificationType();
			String fileName = c.getLocalFileName();
			String status = c.getStatus().toString();
			long transferredSize = c.getTransferredSize();
			boolean waitToConfirm = c.getWaitToConfirm();
			if ( waitToConfirm ) {
				// Do reconfirm
//				if ( Constants.DEBUG ) Log.d(TAG, "checkDownloadTransferStatus(), Reconfirem! groupId=" + groupId + ", transferKey=" + transferKey + ", status=" + status + ", transferredSize=" + transferredSize + ", waitToConfirm=" + waitToConfirm);
				Bundle row = new Bundle();
				row.putString(Constants.AUTH_TOKEN_TYPE, authToken);
				row.putString(Constants.PARAM_LUG_SERVER_ID, lugServerId);
				row.putString(Constants.PARAM_TRANSFER_KEY, transferKey);
				row.putString(Constants.PARAM_STATUS, status);
				row.putLong(Constants.PARAM_TRANSFERRED_SIZE, transferredSize);
				row.putLong(Constants.EXT_PARAM_ROW_ID, rowId);
				row.putString(Constants.PARAM_USER_ID, userId);
				row.putInt(Constants.PARAM_COMPUTER_ID, computerId);
				row.putString(Constants.PARAM_DOWNLOAD_GROUP_ID, groupId);
				row.putInt(Constants.PARAM_DOWNLOAD_NOTIFICATION_TYPE, notificationType);
				row.putString(Constants.PARAM_FILE_NAME, fileName);
				reconfirmRows.add(row);
			} else {
				// Do download
//				if ( Constants.DEBUG ) Log.d(TAG, "checkDownloadTransferStatus(), Do download!");
				RemoteFileUtils.downloadFile(context, userId, computerId, lugServerId, authToken, groupId, transferKey, fromAnotherApp);
			}
		}
		c.close();

		if ( reconfirmRows.size() > 0 ) {
			new DownloadReconfirmTask().execute(reconfirmRows.toArray(new Bundle[0]));
		}
	}

	private class DownloadReconfirmTask extends AsyncTask<Bundle, Void, Void> {
		@Override
		protected Void doInBackground(Bundle... bundles) {
//			if ( Constants.DEBUG ) Log.d(TAG, "DownloadReconfirmTask.doInBackground(), Reconfirem!");
			Context context = getActivity();
			String locale = context.getResources().getConfiguration().locale.toString();

			boolean needToNotify = false;
			for ( Bundle params : bundles ) {
				String status = params.getString(Constants.PARAM_STATUS);
				String userId = params.getString(Constants.PARAM_USER_ID);
				int computerId = params.getInt(Constants.PARAM_COMPUTER_ID);
				String groupId = params.getString(Constants.PARAM_DOWNLOAD_GROUP_ID);
				int notificationType = params.getInt(Constants.PARAM_DOWNLOAD_NOTIFICATION_TYPE);
				if ( DownloadStatusType.success.name().equals(status) ) {
					if ( notificationType == 2 ) {
//						if ( Constants.DEBUG ) Log.d(TAG, "DownloadReconfirmTask.doInBackground().reconfirm, groupId=" + groupId);
						DownloadNotificationService.addDownloadedGroupID(userId, computerId, groupId);
						needToNotify = true;
					}
				}
			}
			if ( needToNotify ) {
				DownloadNotificationService.beginStartingService(context);
			}

			for ( Bundle params : bundles ) {
				String authToken = params.getString(Constants.AUTH_TOKEN_TYPE);
				String lugServerId = params.getString(Constants.PARAM_LUG_SERVER_ID);
				String transferKey = params.getString(Constants.PARAM_TRANSFER_KEY);
				String status = params.getString(Constants.PARAM_STATUS);
				long transferredSize = params.getLong(Constants.PARAM_TRANSFERRED_SIZE);
				long rowId = params.getLong(Constants.EXT_PARAM_ROW_ID);
				int notificationType = params.getInt(Constants.PARAM_DOWNLOAD_NOTIFICATION_TYPE);
				String fileName = params.getString(Constants.PARAM_FILE_NAME);

				RequestFuture<String> future = RequestFuture.newFuture();
				int timeOut = context.getResources().getInteger(R.integer.sync_timeout_a);
				RepositoryClient.getInstance().confirmDownloadFileFromDevice(authToken, lugServerId, transferKey, status, transferredSize, locale, future, future);
				String response = null;
				RepositoryErrorObject errorObject = null;

				try {
					response = future.get(timeOut, TimeUnit.MILLISECONDS);
				} catch (Exception e) {
					errorObject = MiscUtils.getErrorObject(context, e, null);
				}

				if ( errorObject != null ) {
					Log.e(TAG, "DownloadReconfirmTask.doInBackground(), Failed to reconfirm! " + errorObject.getMessage());
					continue;
				}

//				if ( Constants.DEBUG ) Log.d(TAG, "DownloadReconfirmTask.doInBackground().reconfirm, response=" + response + ", status=" + status + ", notificationType=" + notificationType);

				updateDownloadStatus(rowId);
				NotificationUtils.removeDownloadNotification(context, rowId);
				if ( DownloadStatusType.success.name().equals(status) ) {
					if ( notificationType == 1 ) {
//						if ( Constants.DEBUG ) Log.d(TAG, "DownloadReconfirmTask.doInBackground().reconfirm, rowId=" + rowId + ", fileName=" + fileName);
						NotificationUtils.noticeDownloadFileSuccess(context, rowId, fileName);
					}
				}
			}

			return null;
		}
	}

	private void checkUploadTransferStatus(final Context context, final String userId, int computerId, String lugServerId, String authToken) {
//		if ( Constants.DEBUG ) Log.d(TAG, "checkUploadTransferStatus()");
		if ( !MiscUtils.isServiceRunning(UploadService.class) ) {
			TransferDBHelper.writeUploadFailedStatus(userId, computerId);
		}

		String locale = context.getResources().getConfiguration().locale.toString();
		String[] projection = new String[] {
			AssetFileColumns._ID,
			AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.GROUP_ID,
			AssetFileColumns.TRANSFER_KEY,
			AssetFileColumns.STATUS,
			AssetFileColumns.TRANSFERRED_SIZE,
			AssetFileColumns.WAIT_TO_CONFIRM,
			AssetFileColumns.UG_FROM_ANOTHER_APP_WITH_ALIAS
		};
		String sortOrder =
			AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.GROUP_ID + " ASC, " +
			AssetFileColumns.TABLE_NAME + "." + AssetFileColumns.TRANSFER_KEY + " ASC ";
		AssetFileSelection assetFileSelection = new AssetFileSelection();
		assetFileSelection
			.userId(userId).and()
			.computerId(computerId).and()
//			.ugFromAnotherApp(false).and()
			.openParen()
			.waitToConfirm(true).or()
			.status(UploadStatusType.wait).or()
			.status(UploadStatusType.device_uploaded_but_unconfirmed)
			.closeParen();
		AssetFileCursor c = assetFileSelection.query(context.getContentResolver(), projection, sortOrder);
		List<Bundle> reconfirmRows = new ArrayList<Bundle>();
		while (c.moveToNext()) {
			final long rowId = c.getId();
			final String groupId = c.getGroupId();
			String transferKey = c.getTransferKey();
			boolean fromAnotherApp = c.getUGFromAnotherApp();
			String status = c.getStatus().toString();
			boolean waitToConfirm = c.getWaitToConfirm();
			if ( waitToConfirm || UploadStatusType.device_uploaded_but_unconfirmed.name().equals(status) ) {
				// Do reconfirm
//				if ( Constants.DEBUG ) Log.d(TAG, "checkUploadTransferStatus(), Reconfirem! groupId=" + groupId + ", transferKey=" + transferKey + ", status=" + status + ", waitToConfirm=" + waitToConfirm);
				Bundle row = new Bundle();
				row.putString(Constants.AUTH_TOKEN_TYPE, authToken);
				row.putString(Constants.PARAM_LUG_SERVER_ID, lugServerId);
				row.putString(Constants.PARAM_TRANSFER_KEY, transferKey);
				row.putString(Constants.PARAM_STATUS, status);
				row.putLong(Constants.EXT_PARAM_ROW_ID, rowId);
				reconfirmRows.add(row);
			} else {
				// Do upload
//				if ( Constants.DEBUG ) Log.d(TAG, "checkUploadTransferStatus(), Do upload!");
				LocalFileUtils.uploadFile(context, userId, computerId, lugServerId, authToken, groupId, transferKey, fromAnotherApp);
			}
		}
		c.close();

		if ( reconfirmRows.size() > 0 ) {
			new UploadReconfirmTask().execute(reconfirmRows.toArray(new Bundle[0]));
		}
	}

	private class UploadReconfirmTask extends AsyncTask<Bundle, Void, Void> {
		@Override
		protected Void doInBackground(Bundle... bundles) {
//			if ( Constants.DEBUG ) Log.d(TAG, "UploadReconfirmTask.doInBackground(), Reconfirem!");
			Context context = getActivity();
			String locale = context.getResources().getConfiguration().locale.toString();

			for ( Bundle params : bundles ) {
				String authToken = params.getString(Constants.AUTH_TOKEN_TYPE);
				String lugServerId = params.getString(Constants.PARAM_LUG_SERVER_ID);
				String transferKey = params.getString(Constants.PARAM_TRANSFER_KEY);
				String status = params.getString(Constants.PARAM_STATUS);
				long rowId = params.getLong(Constants.EXT_PARAM_ROW_ID);

				RequestFuture<JSONArray> future = RequestFuture.newFuture();
				int timeOut = context.getResources().getInteger(R.integer.sync_timeout_a);
				RepositoryClient.getInstance().confirmUploadFileFromDevice2(authToken, lugServerId, transferKey, status, locale, future, future);
				JSONArray response = null;
				RepositoryErrorObject errorObject = null;

				try {
					response = future.get(timeOut, TimeUnit.MILLISECONDS);
				} catch (Exception e) {
					errorObject = MiscUtils.getErrorObject(context, e, null);
				}

				if ( errorObject != null ) {
					Log.e(TAG, "UploadReconfirmTask.doInBackground(), Failed to reconfirem! " + errorObject.getMessage());
					continue;
				}

//				if ( Constants.DEBUG ) Log.d(TAG, "UploadReconfirmTask.doInBackground().reconfirm, response=" + response + ", status=" + status);

				try {
					for (int i = 0; i < response.length(); i++) {
						JSONObject jso = response.getJSONObject(i);
						String tmpTransferKey = jso.getString(Constants.PARAM_TRANSFER_KEY);
						String tmpStatus = jso.getString(Constants.PARAM_STATUS);
						if ( !UploadStatusType.processing.name().equals(tmpStatus) ) {
							if ( UploadStatusType.not_found.name().equals(tmpStatus) ) {
								tmpStatus = UploadStatusType.failure.name();
							}
							updateUploadStatus(rowId, tmpStatus);
							NotificationUtils.removeUploadNotification(context, rowId);
						}
					}
				} catch (JSONException e) {
					Log.e(TAG, "UploadReconfirmTask.doInBackground().reconfirm, Upload confirm json object parsing error!");
				}
			}

			return null;
		}
	}

	private void updateUploadStatus(long rowId, String status) {
//		if ( Constants.DEBUG ) Log.d(TAG, "writeUploadStatus(): rowId=" + rowId + ", status=" + status);
		TransferDBHelper.writeUploadStatus(rowId, status);
	}

	private void updateDownloadStatus(long rowId) {
//		if ( Constants.DEBUG ) Log.d(TAG, "updateDownloadStatus(): rowId=" + rowId);
		TransferDBHelper.writeDownloadConfirmed(rowId);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		int menuRes = -1;
		if ( this.transferType == Constants.TRANSFER_TYPE_DOWNLOAD ) {
			menuRes = R.menu.menu_transfer_download;
		} else if ( this.transferType == Constants.TRANSFER_TYPE_UPLOAD ) {
			menuRes = R.menu.menu_transfer_upload;
		}
		inflater.inflate(menuRes, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.action_choose_file:
				doChooseFile();
				return true;
			case R.id.action_stop_all:
				doStopAll();
				return true;
//			case R.id.action_refresh:
//				doRefreshAction();
//				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void doChooseFile() {
		if ( this.transferType == Constants.TRANSFER_TYPE_DOWNLOAD ) {
			((MainActivity)getActivity()).selectBrowseRemoteDirItem();
		} else if ( this.transferType == Constants.TRANSFER_TYPE_UPLOAD ) {
			((MainActivity)getActivity()).selectBrowseLocalDirItem();
		}
	}

	private void doStopAll() {
		final int tType = this.transferType;
		int titleRes = -1;
		int contentRes = -1;
		if ( tType == Constants.TRANSFER_TYPE_DOWNLOAD ) {
			if ( !MiscUtils.isServiceRunning(DownloadService.class) ) {
				return;
			}
			titleRes = R.string.title_stop_all_downloads;
			contentRes = R.string.message_stop_all_download_tasks;
		} else if ( tType == Constants.TRANSFER_TYPE_UPLOAD ) {
			if ( !MiscUtils.isServiceRunning(UploadService.class) ) {
				return;
			}
			titleRes = R.string.title_stop_all_uploads;
			contentRes = R.string.message_stop_all_upload_tasks;
		}
		MaterialDialog.SingleButtonCallback positiveButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				if ( tType == Constants.TRANSFER_TYPE_DOWNLOAD ) {
					if ( MiscUtils.isServiceRunning(DownloadService.class) ) {
						DownloadService.stopAllTasks();
					}
				} else if ( tType == Constants.TRANSFER_TYPE_UPLOAD ) {
					if ( MiscUtils.isServiceRunning(UploadService.class) ) {
						UploadService.stopAllTasks();
					}
				}
			}
		};
		DialogUtils.createButtonsDialog31(
			getActivity(),
			titleRes,
			-1,
			contentRes,
			R.string.btn_label_stop,
			R.string.btn_label_cancel,
			positiveButtonCallback
		).show();
	}

	private void showActionsDialogForDownloadSuccess(final Context context, int status, Cursor cursor) {

		final long rowId = cursor.getLong(cursor.getColumnIndex(FileTransferColumns._ID));
		final String fileName = cursor.getString(cursor.getColumnIndex(FileTransferColumns.LOCAL_FILE_NAME));
		final String localDir = cursor.getString(cursor.getColumnIndex(FileTransferColumns.ALIAS_LOCAL_PATH));
		final String savedFileName = cursor.getString(cursor.getColumnIndex(FileTransferColumns.SAVED_FILE_NAME));
		final String contentType = cursor.getString(cursor.getColumnIndex(FileTransferColumns.CONTENT_TYPE));
		final String fullName = localDir + "/" + ( TextUtils.isEmpty(savedFileName) ? fileName : savedFileName );
		final String groupId = cursor.getString(cursor.getColumnIndex(FileTransferColumns.GROUP_ID));
		final String transferKey = cursor.getString(cursor.getColumnIndex(FileTransferColumns.TRANSFER_KEY));
		final boolean fromAnotherApp = cursor.getInt(cursor.getColumnIndex(FileTransferColumns.ALIAS_FROM_ANOTHER_APP)) != 0;

		String btn1Text = context.getResources().getString(R.string.btn_label_open_file);
		String btn2Text = context.getResources().getString(R.string.btn_label_download_again);
		String btn3Text = context.getResources().getString(R.string.action_details);
		String dialogContent = String.format(context.getResources().getString(R.string.message_actions_for_download_success), btn1Text, btn2Text, btn3Text);

		MaterialDialog.SingleButtonCallback positiveButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				MiscUtils.openFile(context, contentType, fullName);
			}
		};
		MaterialDialog.SingleButtonCallback negativeButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				downloadAgain(context, fileName, groupId, transferKey, fromAnotherApp);
			}
		};
		MaterialDialog.SingleButtonCallback neutralButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				showDownloadDetails(context, rowId);
			}
		};
		DialogUtils.createStackedButtonsDialog2(
			context,
			fileName,
			-1,
			dialogContent,
			R.string.btn_label_open_file,
			positiveButtonCallback,
			R.string.btn_label_download_again,
			negativeButtonCallback,
			R.string.action_details,
			neutralButtonCallback
		).show();

	}

	private void downloadAgain(final Context context, final String fileName, final String groupId, final String transferKey, final boolean fromAnotherApp) {
		FilelugUtils.Callback callback = new FilelugUtils.Callback() {
			@Override
			public void onError(int errorCode, String errorMessage) {
			}
			@Override
			public void onSuccess(Bundle result) {
				long uploadSizeLimit = result.getLong(Constants.PARAM_UPLOAD_SIZE_LIMIT);
				long downloadSizeLimit = result.getLong(Constants.PARAM_DOWNLOAD_SIZE_LIMIT);
				String authToken = result.getString(AccountManager.KEY_AUTHTOKEN);

				Account activeAccount = AccountUtils.getActiveAccount();
				AccountManager accountManager = AccountManager.get(context);
				String userId = accountManager.getUserData(activeAccount, Constants.EXT_PARAM_FILELUG_ACCOUNT);
				String computerId = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_ID);
				String lugServerId = accountManager.getUserData(activeAccount, Constants.PARAM_LUG_SERVER_ID);

				// 狀態 - 成功, 換 GroupID 及 TransferKey 重新下載
				downloadFileAndRenewGroupID(context, userId, Integer.valueOf(computerId).intValue(), lugServerId, authToken, groupId, transferKey, fileName);
			}
		};
		FilelugUtils.pingDesktopB(context, callback);
	}

	private void showDownloadDetails(Context context, long rowId) {
		Map<String, Map<String, Object>> details = TransferDBHelper.getDownloadDetailsByRowId(rowId);
		MaterialDialog detailDialog = DialogUtils.createDetailDialog(context, R.string.action_details, details);
		detailDialog.show();
	}

	private void showActionsDialogForDownloadFailed(final Context context, final int status, Cursor cursor) {

		final long endTimestamp = cursor.getLong(cursor.getColumnIndex(FileTransferColumns.END_TIMESTAMP));
		if ( endTimestamp <= 0 ) {
			return;
		}

		final long rowId = cursor.getLong(cursor.getColumnIndex(FileTransferColumns._ID));
		final String fileName = cursor.getString(cursor.getColumnIndex(FileTransferColumns.LOCAL_FILE_NAME));
		final String groupId = cursor.getString(cursor.getColumnIndex(FileTransferColumns.GROUP_ID));
		final String transferKey = cursor.getString(cursor.getColumnIndex(FileTransferColumns.TRANSFER_KEY));
		final boolean fromAnotherApp = cursor.getInt(cursor.getColumnIndex(FileTransferColumns.ALIAS_FROM_ANOTHER_APP)) != 0;

		String btn1Text = context.getResources().getString(R.string.btn_label_resume_download);
		String btn2Text = context.getResources().getString(R.string.action_details);
		String dialogContent = String.format(context.getResources().getString(R.string.message_actions_for_download_failed), btn1Text, btn2Text);

		MaterialDialog.SingleButtonCallback positiveButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				resumeDownloadFile(context, fileName, groupId, transferKey, fromAnotherApp);
			}
		};
		MaterialDialog.SingleButtonCallback neutralButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				showDownloadDetails(context, rowId);
			}
		};
		DialogUtils.createStackedButtonsDialog2(
				context,
				fileName,
				-1,
				dialogContent,
				R.string.btn_label_resume_download,
				positiveButtonCallback,
				-1,
				null,
				R.string.action_details,
				neutralButtonCallback
		).show();

	}

	private void resumeDownloadFile(final Context context, final String fileName, final String groupId, final String transferKey, final boolean fromAnotherApp) {
		FilelugUtils.Callback callback = new FilelugUtils.Callback() {
			@Override
			public void onError(int errorCode, String errorMessage) {
			}
			@Override
			public void onSuccess(Bundle result) {
				long uploadSizeLimit = result.getLong(Constants.PARAM_UPLOAD_SIZE_LIMIT);
				long downloadSizeLimit = result.getLong(Constants.PARAM_DOWNLOAD_SIZE_LIMIT);
				String authToken = result.getString(AccountManager.KEY_AUTHTOKEN);

				Account activeAccount = AccountUtils.getActiveAccount();
				AccountManager accountManager = AccountManager.get(context);
				String userId = accountManager.getUserData(activeAccount, Constants.EXT_PARAM_FILELUG_ACCOUNT);
				String computerId = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_ID);
				String lugServerId = accountManager.getUserData(activeAccount, Constants.PARAM_LUG_SERVER_ID);

				RemoteFileUtils.downloadFile_resume(context, userId, Integer.valueOf(computerId), lugServerId, authToken, groupId, transferKey, fromAnotherApp);
			}
		};
		FilelugUtils.pingDesktopB(context, callback);
	}

	private void showActionsDialogForCancelDownload(final Context context, final int status, Cursor cursor) {

		final long rowId = cursor.getLong(cursor.getColumnIndex(FileTransferColumns._ID));
		final String fileName = cursor.getString(cursor.getColumnIndex(FileTransferColumns.LOCAL_FILE_NAME));
		final String userId = cursor.getString(cursor.getColumnIndex(FileTransferColumns.USER_ID));
		final int computerId = cursor.getInt(cursor.getColumnIndex(FileTransferColumns.COMPUTER_ID));
		final String groupId = cursor.getString(cursor.getColumnIndex(FileTransferColumns.GROUP_ID));
		final String transferKey = cursor.getString(cursor.getColumnIndex(FileTransferColumns.TRANSFER_KEY));
		final boolean fromAnotherApp = cursor.getInt(cursor.getColumnIndex(FileTransferColumns.ALIAS_FROM_ANOTHER_APP)) != 0;

		String btn1Text = context.getResources().getString(R.string.btn_label_cancel_download);
		String dialogContent = String.format(context.getResources().getString(R.string.message_actions_for_wait_for_download), btn1Text);

		MaterialDialog.SingleButtonCallback positiveButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				DownloadService.cancelDownload(userId, computerId, groupId, transferKey, rowId);
			}
		};
		DialogUtils.createStackedButtonsDialog2(
			context,
			fileName,
			-1,
			dialogContent,
			R.string.btn_label_cancel_download,
			positiveButtonCallback,
			-1,
			null,
			-1,
			null
		).show();

	}

	private void showActionsDialogForStopDownload(final Context context, final int status, Cursor cursor) {

		final long rowId = cursor.getLong(cursor.getColumnIndex(FileTransferColumns._ID));
		final String fileName = cursor.getString(cursor.getColumnIndex(FileTransferColumns.LOCAL_FILE_NAME));
		final String userId = cursor.getString(cursor.getColumnIndex(FileTransferColumns.USER_ID));
		final int computerId = cursor.getInt(cursor.getColumnIndex(FileTransferColumns.COMPUTER_ID));
		final String groupId = cursor.getString(cursor.getColumnIndex(FileTransferColumns.GROUP_ID));
		final String transferKey = cursor.getString(cursor.getColumnIndex(FileTransferColumns.TRANSFER_KEY));
		final boolean fromAnotherApp = cursor.getInt(cursor.getColumnIndex(FileTransferColumns.ALIAS_FROM_ANOTHER_APP)) != 0;

		String btn1Text = context.getResources().getString(R.string.btn_label_stop_download);
		String dialogContent = String.format(context.getResources().getString(R.string.message_actions_for_downloading), btn1Text);

		MaterialDialog.SingleButtonCallback positiveButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				DownloadService.stopDownload(userId, computerId, groupId, transferKey);
			}
		};
		DialogUtils.createStackedButtonsDialog2(
            context,
            fileName,
            -1,
            dialogContent,
            R.string.btn_label_stop_download,
            positiveButtonCallback,
            -1,
            null,
            -1,
            null
		).show();

	}

	private void showActionsDialogForUploadSuccess(final Context context, int status, Cursor cursor) {

		final long rowId = cursor.getLong(cursor.getColumnIndex(AssetFileColumns._ID));
		final String fileName = cursor.getString(cursor.getColumnIndex(AssetFileColumns.SERVER_FILE_NAME));
		final String groupId = cursor.getString(cursor.getColumnIndex(AssetFileColumns.GROUP_ID));
		final String transferKey = cursor.getString(cursor.getColumnIndex(AssetFileColumns.TRANSFER_KEY));
		final boolean fromAnotherApp = cursor.getInt(cursor.getColumnIndex(AssetFileColumns.ALIAS_FROM_ANOTHER_APP)) != 0;

		String btn1Text = context.getResources().getString(R.string.btn_label_upload_again);
		String btn2Text = context.getResources().getString(R.string.action_details);
		String dialogContent = String.format(context.getResources().getString(R.string.message_actions_for_upload_success), btn1Text, btn2Text);

		MaterialDialog.SingleButtonCallback positiveButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				uploadAgain(context, fileName, groupId, transferKey, fromAnotherApp);
			}
		};
		MaterialDialog.SingleButtonCallback neutralButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				showUploadDetails(context, rowId);
			}
		};
		DialogUtils.createStackedButtonsDialog2(
			context,
			fileName,
			-1,
			dialogContent,
			R.string.btn_label_upload_again,
			positiveButtonCallback,
			-1,
			null,
			R.string.action_details,
			neutralButtonCallback
		).show();

	}

	private void uploadAgain(final Context context, final String fileName, final String groupId, final String transferKey, final boolean fromAnotherApp) {
		FilelugUtils.Callback callback = new FilelugUtils.Callback() {
			@Override
			public void onError(int errorCode, String errorMessage) {
			}
			@Override
			public void onSuccess(Bundle result) {
				long uploadSizeLimit = result.getLong(Constants.PARAM_UPLOAD_SIZE_LIMIT);
				long downloadSizeLimit = result.getLong(Constants.PARAM_DOWNLOAD_SIZE_LIMIT);
				String authToken = result.getString(AccountManager.KEY_AUTHTOKEN);

				Account activeAccount = AccountUtils.getActiveAccount();
				AccountManager accountManager = AccountManager.get(context);
				String userId = accountManager.getUserData(activeAccount, Constants.EXT_PARAM_FILELUG_ACCOUNT);
				String computerId = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_ID);
				String lugServerId = accountManager.getUserData(activeAccount, Constants.PARAM_LUG_SERVER_ID);
				String lineSeparator = accountManager.getUserData(activeAccount, Constants.PARAM_LINE_SEPARATOR);

				// 狀態 - 成功, 換 GroupID 及 TransferKey 重新上傳
				uploadFileAndRenewGroupID(context, userId, Integer.valueOf(computerId).intValue(), lugServerId, authToken, groupId, transferKey, fileName, lineSeparator);
			}
		};
		FilelugUtils.pingDesktopB(context, callback);
	}

	private void showUploadDetails(Context context, long rowId) {
		Map<String, Map<String, Object>> details = TransferDBHelper.getUploadDetailsByRowId(rowId);
		MaterialDialog detailDialog = DialogUtils.createDetailDialog(context, R.string.action_details, details);
		detailDialog.show();
	}

	private void showActionsDialogForUploadFailed(final Context context, int status, Cursor cursor) {

		final long endTimestamp = cursor.getLong(cursor.getColumnIndex(AssetFileColumns.END_TIMESTAMP));
		if ( endTimestamp <= 0 ) {
			return;
		}

		final long rowId = cursor.getLong(cursor.getColumnIndex(AssetFileColumns._ID));
		final String fileName = cursor.getString(cursor.getColumnIndex(AssetFileColumns.SERVER_FILE_NAME));
		final String groupId = cursor.getString(cursor.getColumnIndex(AssetFileColumns.GROUP_ID));
		final String transferKey = cursor.getString(cursor.getColumnIndex(AssetFileColumns.TRANSFER_KEY));
		final boolean fromAnotherApp = cursor.getInt(cursor.getColumnIndex(AssetFileColumns.ALIAS_FROM_ANOTHER_APP)) != 0;

		String btn1Text = context.getResources().getString(R.string.btn_label_resume_upload);
		String btn2Text = context.getResources().getString(R.string.action_details);
		String dialogContent = String.format(context.getResources().getString(R.string.message_actions_for_upload_failed), btn1Text, btn2Text);

		MaterialDialog.SingleButtonCallback positiveButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				resumeUploadFile(context, fileName, groupId, transferKey, fromAnotherApp);
			}
		};
		MaterialDialog.SingleButtonCallback neutralButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				showUploadDetails(context, rowId);
			}
		};
		DialogUtils.createStackedButtonsDialog2(
				context,
				fileName,
				-1,
				dialogContent,
				R.string.btn_label_resume_upload,
				positiveButtonCallback,
				-1,
				null,
				R.string.action_details,
				neutralButtonCallback
		).show();

	}

	private void resumeUploadFile(final Context context, final String fileName, final String groupId, final String transferKey, final boolean fromAnotherApp) {
		FilelugUtils.Callback callback = new FilelugUtils.Callback() {
			@Override
			public void onError(int errorCode, String errorMessage) {
			}
			@Override
			public void onSuccess(Bundle result) {
				long uploadSizeLimit = result.getLong(Constants.PARAM_UPLOAD_SIZE_LIMIT);
				long downloadSizeLimit = result.getLong(Constants.PARAM_DOWNLOAD_SIZE_LIMIT);
				String authToken = result.getString(AccountManager.KEY_AUTHTOKEN);

				Account activeAccount = AccountUtils.getActiveAccount();
				AccountManager accountManager = AccountManager.get(context);
				String userId = accountManager.getUserData(activeAccount, Constants.EXT_PARAM_FILELUG_ACCOUNT);
				String computerId = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_ID);
				String lugServerId = accountManager.getUserData(activeAccount, Constants.PARAM_LUG_SERVER_ID);

				checkDirectOrResumeUpload(context, userId, Integer.valueOf(computerId).intValue(), lugServerId, authToken, groupId, transferKey, fileName);
			}
		};
		FilelugUtils.pingDesktopB(context, callback);
	}

	private void showActionsDialogForCancelUpload(final Context context, int status, Cursor cursor) {

		final long rowId = cursor.getLong(cursor.getColumnIndex(AssetFileColumns._ID));
		final String fileName = cursor.getString(cursor.getColumnIndex(AssetFileColumns.SERVER_FILE_NAME));
		final String userId = cursor.getString(cursor.getColumnIndex(AssetFileColumns.USER_ID));
		final int computerId = cursor.getInt(cursor.getColumnIndex(AssetFileColumns.COMPUTER_ID));
		final String groupId = cursor.getString(cursor.getColumnIndex(AssetFileColumns.GROUP_ID));
		final String transferKey = cursor.getString(cursor.getColumnIndex(AssetFileColumns.TRANSFER_KEY));
		final boolean fromAnotherApp = cursor.getInt(cursor.getColumnIndex(AssetFileColumns.ALIAS_FROM_ANOTHER_APP)) != 0;

		String btn1Text = context.getResources().getString(R.string.btn_label_cancel_upload);
		String dialogContent = String.format(context.getResources().getString(R.string.message_actions_for_wait_for_upload), btn1Text);

		MaterialDialog.SingleButtonCallback positiveButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				UploadService.cancelUpload(userId, computerId, groupId, transferKey, rowId);
			}
		};
		DialogUtils.createStackedButtonsDialog2(
			context,
			fileName,
			-1,
			dialogContent,
			R.string.btn_label_cancel_upload,
			positiveButtonCallback,
			-1,
			null,
			-1,
			null
		).show();

	}

	private void showActionsDialogForStopUpload(final Context context, int status, Cursor cursor) {

		final long rowId = cursor.getLong(cursor.getColumnIndex(AssetFileColumns._ID));
		final String fileName = cursor.getString(cursor.getColumnIndex(AssetFileColumns.SERVER_FILE_NAME));
		final String userId = cursor.getString(cursor.getColumnIndex(AssetFileColumns.USER_ID));
		final int computerId = cursor.getInt(cursor.getColumnIndex(AssetFileColumns.COMPUTER_ID));
		final String groupId = cursor.getString(cursor.getColumnIndex(AssetFileColumns.GROUP_ID));
		final String transferKey = cursor.getString(cursor.getColumnIndex(AssetFileColumns.TRANSFER_KEY));
		final boolean fromAnotherApp = cursor.getInt(cursor.getColumnIndex(AssetFileColumns.ALIAS_FROM_ANOTHER_APP)) != 0;

		String btn1Text = context.getResources().getString(R.string.btn_label_stop_upload);
		String dialogContent = String.format(context.getResources().getString(R.string.message_actions_for_uploading), btn1Text);

		MaterialDialog.SingleButtonCallback positiveButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				UploadService.stopUpload(userId, computerId, groupId, transferKey);
			}
		};
		DialogUtils.createStackedButtonsDialog2(
			context,
			fileName,
			-1,
			dialogContent,
			R.string.btn_label_stop_upload,
			positiveButtonCallback,
			-1,
			null,
			-1,
			null
		).show();

	}

	@Override
	public boolean backToParent() {
		return super.backToParent();
	}

	@Override
	public boolean canSwipeRefreshChildScrollUp() {
		boolean canSwipe = getFirstVisiblePosition() > 0;
//		if ( Constants.DEBUG ) Log.d(TAG, "canSwipeRefreshChildScrollUp(): canSwipe=" + canSwipe);
		return canSwipe;
	}

	private int getFirstVisiblePosition() {
		String logStr = "getFirstVisiblePosition(): ";
		int position;
		RecyclerView.LayoutManager manager = this.recordList.getLayoutManager();
		if (manager instanceof LinearLayoutManager) {
			position = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
			logStr += "Is LinearLayoutManager, position=" + position + ", childCount=" + ((LinearLayoutManager) manager).getChildCount();
		} else if (manager instanceof GridLayoutManager) {
			position = ((GridLayoutManager) manager).findFirstVisibleItemPosition();
			logStr += "Is GridLayoutManager, position=" + position + ", childCount=" + ((GridLayoutManager) manager).getChildCount();
		} else if (manager instanceof StaggeredGridLayoutManager) {
			StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
			int[] lastPositions = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()]);
			position = getMinPositions(lastPositions);
			logStr += "Is StaggeredGridLayoutManager, position=" + position + ", childCount=" + ((StaggeredGridLayoutManager) manager).getChildCount();
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

	@Override
	public void doRefreshAction() {
		onRefreshingStateChanged(true);
		this.recordListAdapter.notifyDataSetChanged();
		checkTransferStatus();
		onRefreshingStateChanged(false);
	}

}
