package com.filelug.android.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.BaseResponseError;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.crepo.RepositoryUtility;
import com.filelug.android.provider.uploadgroup.UploadGroupContentValues;
import com.filelug.android.ui.adapter.ConfirmUploadFileListAdapter;
import com.filelug.android.ui.model.LocalFile;
import com.filelug.android.ui.model.LocalFileObject;
import com.filelug.android.ui.model.RemoteFile;
import com.filelug.android.ui.viewHolder.SelectableViewHolder;
import com.filelug.android.ui.widget.RemoteFolderChooser;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.FileCache;
import com.filelug.android.util.FilelugUtils;
import com.filelug.android.util.LocalFileUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.PrefUtils;
import com.filelug.android.util.Validation;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmUploadActivity extends BaseActivity implements RemoteFolderChooser.FolderSelectCallback {

	private static final String TAG = ConfirmUploadActivity.class.getSimpleName();

	private BroadcastReceiver mCacheStatusBroadcastReceiver;
	private Toolbar mToolbar;
	private View vRowFileCount = null;
	private View vRowUploadToFolder = null;
	private View vRowSubFolderType = null;
	private View vRowDescriptionType = null;
	private View vRowNotificationType = null;
	private TextView tvFileCount = null;
	private TextView tvUploadPath = null;
	private TextView tvSubdirType = null;
	private TextView tvDescriptionType = null;
	private TextView tvNotificationType = null;
	private FloatingActionButton mFloatingActionButton = null;

	private ConfirmUploadFileListAdapter mConfirmUploadFileListAdapter = null;
	private LocalFileObject[] mSelectedFiles = null;
	private Integer[] mSelectedItemsIds = null;
	private Map<LocalFileObject, Boolean> cacheFiles = null;

	private boolean mRequestFromShare = false;
	private Boolean mNeedCopyCache = null;
	private String mExternalSubject = null;
	private String mExternalText = null;
	private String mUploadPath = null;
	private int mSubdirType = 0;
	private String mCustomizedSubDirName = null;
	private int mDescriptionType = 0;
	private String mCustomizedDescription = null;
	private int mNotificationType = 0;

	private boolean mSaveAsDefault_UploadPath = false;
	private boolean mSaveAsDefault_SubdirType = false;
	private boolean mSaveAsDefault_DescriptionType = false;
	private boolean mSaveAsDefault_NotificationType = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

//		if ( Constants.DEBUG ) Log.d(TAG, "onCreate()");
		setContentView(R.layout.layout_confirm_upload);

		mCacheStatusBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
//				Log.d(TAG, "onReceive()");
				if ( cacheFilesDownloaded() ) {
//					Log.d(TAG, "onReceive(), mFloatingActionButton.setEnabled(true)");
					mFloatingActionButton.setEnabled(true);
				}
			}
		};

		initExtras(getIntent().getExtras());
		initUI();
	}

	private void initExtras(Bundle extras) {
		if ( extras != null ) {
//			Log.d(TAG, "==================================================================================================================================");
//			for ( String key : extras.keySet() ) {
//				Object value = extras.get(key);
//				Log.d(TAG, "key=" + key + ", value=" + value + "\n");
//			}
			if ( extras.containsKey(Constants.EXT_PARAM_SELECTED_UPLOAD_FILES) ) {
				Parcelable[] array = extras.getParcelableArray(Constants.EXT_PARAM_SELECTED_UPLOAD_FILES);
				mSelectedFiles = new LocalFileObject[array.length];
				for ( int i=0; i<array.length; i++ ) {
					mSelectedFiles[i] = (LocalFileObject)array[i];
				}
				//Arrays.sort(mSelectedFiles, new SortUtils.LocalFileFullNameAscComparator());
				mSelectedItemsIds = new Integer[mSelectedFiles.length];
				for ( int i=0; i<mSelectedFiles.length; i++ ) {
					mSelectedItemsIds[i] = i;
				}
			} else if ( extras.containsKey(Intent.EXTRA_STREAM) ) {
				mRequestFromShare = true;
//				if ( Constants.DEBUG ) Log.d(TAG, "onCreate(), invoke from the other apps");
				String subject = extras.getString(Intent.EXTRA_SUBJECT, null);
				if ( !TextUtils.isEmpty(subject) ) {
					String regex = '[' + Constants.INVALID_FILE_NAME_CHARACTERS + ']';
					mExternalSubject = subject.replaceAll(regex, " ");
				}
				mExternalText = extras.getString(Intent.EXTRA_TEXT, null);
				Object stream = extras.get(Intent.EXTRA_STREAM);
				mSelectedFiles = convertStreamToFileObjectArray(ConfirmUploadActivity.this, stream);
				//Arrays.sort(mSelectedFiles, new SortUtils.LocalFileFullNameAscComparator());
				mSelectedItemsIds = new Integer[mSelectedFiles.length];
//				String msg = "";
				for ( int i=0; i<mSelectedFiles.length; i++ ) {
					mSelectedItemsIds[i] = i;
//					msg += ( i==0 ? "" : "," ) + mSelectedFiles[i].getFullName();
				}
//				if ( Constants.DEBUG ) Log.d(TAG, "onCreate(), mSelectedFiles="+msg);
			} else {
				mRequestFromShare = true;
				mSelectedFiles = new LocalFileObject[0];
			}
		}
	}

	private void initUI() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		mToolbar.setTitle(R.string.drawer_section_upload);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		tvFileCount = (TextView) findViewById(R.id.file_count);
		vRowFileCount = (View) findViewById(R.id.row_file_count);
		vRowFileCount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeSelectedUploadFiles();
			}
		});
		tvUploadPath = (TextView) findViewById(R.id.upload_to_folder);
		vRowUploadToFolder = (View) findViewById(R.id.row_upload_to_folder);
		vRowUploadToFolder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeUploadToFolder();
			}
		});
		tvSubdirType = (TextView) findViewById(R.id.sub_folder_type);
		vRowSubFolderType = (View) findViewById(R.id.row_sub_folder_type);
		vRowSubFolderType.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeSubFolderType();
			}
		});
		tvDescriptionType = (TextView) findViewById(R.id.upload_description);
		vRowDescriptionType = (View) findViewById(R.id.row_upload_description);
		vRowDescriptionType.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeDescriptionType();
			}
		});
		tvNotificationType = (TextView) findViewById(R.id.notification_type);
		vRowNotificationType = (View) findViewById(R.id.row_notification_type);
		vRowNotificationType.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeNotificationType();
			}
		});

		mFloatingActionButton = (FloatingActionButton) this.findViewById(R.id.fab);
		mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Activity activity = ConfirmUploadActivity.this;
				if (mSelectedItemsIds == null || mSelectedItemsIds.length == 0) {
					MsgUtils.showInfoMessage(activity, activity.getResources().getString(R.string.message_no_file_selected));
					return;
				}
				if (TextUtils.isEmpty(mUploadPath)) {
					MsgUtils.showInfoMessage(activity, String.format(getResources().getString(R.string.message_field_can_not_be_empty_2), getResources().getString(R.string.label_upload_to_folder)));
					return;
				}
				if ( mSubdirType >= 2 && TextUtils.isEmpty(mCustomizedSubDirName) ) {
					MsgUtils.showInfoMessage(activity, String.format(getResources().getString(R.string.message_field_can_not_be_empty_2), MiscUtils.getSubdirTypeStr(ConfirmUploadActivity.this, 2)));
					return;
				}
				if ( mDescriptionType >= 2 && TextUtils.isEmpty(mCustomizedDescription) ) {
					MsgUtils.showInfoMessage(activity, String.format(getResources().getString(R.string.message_field_can_not_be_empty_2), MiscUtils.getDescriptionTypeStr(ConfirmUploadActivity.this, 2)));
					return;
				}
				if ( AccountUtils.getActiveAccount() == null) {
					MsgUtils.showToast(activity, R.string.message_registered_computer_not_found);
					return;
				}
				pingDesktop();
			}
		});
	}

	private void initUIText() {
		Account activeAccount = AccountUtils.getActiveAccount();
		String accountName = null;
		if ( activeAccount != null ) {
			accountName = activeAccount.name;
		}

		mUploadPath = PrefUtils.getUploadPath(accountName);
		if ( TextUtils.isEmpty(mUploadPath) ) {
			mSaveAsDefault_UploadPath = true;
			mSaveAsDefault_SubdirType = true;
			mSaveAsDefault_DescriptionType = true;
			mSaveAsDefault_NotificationType = true;
		}
		String _subdirType = PrefUtils.getUploadSubdirType(accountName);
		mSubdirType = TextUtils.isEmpty(_subdirType) ? PrefUtils.DEFAULT_VALUE_UPLOAD_SUB_DIR : Integer.valueOf(_subdirType);
		String _subdirValue = PrefUtils.getUploadSubdirValue(accountName);
		mCustomizedSubDirName = TextUtils.isEmpty(_subdirValue) ? null : _subdirValue;
		String _descriptionType = PrefUtils.getUploadDescriptionType(accountName);
		mDescriptionType = TextUtils.isEmpty(_descriptionType) ? PrefUtils.DEFAULT_VALUE_UPLOAD_DESCRIPTION_TYPE : Integer.valueOf(_descriptionType);
		String _descriptionValue = PrefUtils.getUploadDescriptionValue(accountName);
		mCustomizedDescription = TextUtils.isEmpty(_descriptionValue) ? null : _descriptionValue;
		String _notificationType = PrefUtils.getUploadNotificationType(accountName);
		mNotificationType = TextUtils.isEmpty(_descriptionType) ? PrefUtils.DEFAULT_VALUE_UPLOAD_NOTIFICATION_TYPE : Integer.valueOf(_notificationType);

		if ( !TextUtils.isEmpty(mExternalSubject) ) {
			mSubdirType = 4;
			mCustomizedSubDirName = mExternalSubject.trim();
		}
		if ( !TextUtils.isEmpty(mExternalText) ) {
			mDescriptionType = 3;
			mCustomizedDescription = mExternalText;
		}

		SelectableViewHolder.ClickListener clickListener = new SelectableViewHolder.ClickListener() {
			@Override
			public void onItemClicked(int position) {
				toggleSelection(position);
			}
			@Override
			public boolean onItemLongClicked(int position) {
				toggleSelection(position);
				return true;
			}
		};
		mConfirmUploadFileListAdapter = new ConfirmUploadFileListAdapter(ConfirmUploadActivity.this, mSelectedFiles, clickListener);

		String fileCountStr = String.format(getResources().getString(R.string.format_selected_file_count), (mSelectedItemsIds != null ? mSelectedItemsIds.length : 0) );
		String uploadPathStr = null;
		if ( TextUtils.isEmpty(mUploadPath) ) {
			uploadPathStr = getResources().getString(R.string.message_not_set);
		} else {
			uploadPathStr = mUploadPath;
		}
		String subdirTypeStr = MiscUtils.getSubDirTypeSettingText(ConfirmUploadActivity.this, mSubdirType, mCustomizedSubDirName);
		String descriptionTypeStr = MiscUtils.getDescriptionTypeSettingText(ConfirmUploadActivity.this, mDescriptionType, mCustomizedDescription);
		String notificationTypeStr = MiscUtils.getNotificationTypeStr(ConfirmUploadActivity.this, mNotificationType);

		tvFileCount.setText(fileCountStr);
		tvUploadPath.setText(uploadPathStr);
		tvSubdirType.setText(subdirTypeStr);
		tvDescriptionType.setText(descriptionTypeStr);
		tvNotificationType.setText(notificationTypeStr);
	}

	private boolean cacheFilesDownloaded() {
//		Log.d(TAG, "cacheFilesDownloaded()");
		if ( cacheFiles == null || cacheFiles.size() == 0 ) {
			return true;
		}
		boolean isDownloading = false;
		for ( Boolean cached : cacheFiles.values() ) {
			if ( !cached ) {
				isDownloading = true;
				break;
			}
		}
		return !isDownloading;
	}

	private class SaveToCacheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
//			Log.d(TAG, "doInBackground()");
			cacheFiles = new HashMap<LocalFileObject, Boolean>();
			for ( LocalFileObject fileObj : mSelectedFiles ) {
				String cacheFileName = fileObj.getCacheFileName();
				if ( !TextUtils.isEmpty(cacheFileName) ) {
					cacheFiles.put(fileObj, Boolean.FALSE);
				}
			}

			for ( LocalFileObject fileObj : cacheFiles.keySet() ) {
				copyCacheFile(fileObj);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void param) {
//			Log.d(TAG, "onPostExecute()");
			Context context = ConfirmUploadActivity.this;
			Intent cacheSharedStatus = new Intent(Constants.LOCAL_BROADCAST_CACHE_SHARED_STATUS);
			LocalBroadcastManager.getInstance(context).sendBroadcast(cacheSharedStatus);
		}

		private void copyCacheFile(LocalFileObject fileObj) {
//			Log.d(TAG, "copyCacheFile(), fileObj: " + fileObj.getDisplayName());

			Context context = ConfirmUploadActivity.this;

			ContentResolver contentResolver = context.getContentResolver();
			Uri uri = fileObj.getUri();
			AssetFileDescriptor fd = null;
			FileInputStream is = null;
			String errorMessage = null;

			try {
				fd = contentResolver.openAssetFileDescriptor(uri, "r");
				// This creates an auto-closing input-stream, so
				// the file descriptor will be closed whenever the InputStream
				// is closed.
				is = fd.createInputStream();
			} catch (FileNotFoundException fnfe) {
				errorMessage = String.format(context.getResources().getString(R.string.message_shared_file_not_found), fileObj.getDisplayName());
				Log.e(TAG, "copyCacheFile(), File not found: " + uri.toString());
			} catch (IOException e) {
				errorMessage = String.format(context.getResources().getString(R.string.message_failed_to_read_shared_file), fileObj.getDisplayName());
				Log.e(TAG, "copyCacheFile(), " + uri.toString() + ", Read stream exception: ", e);
				try {
					fd.close();
				} catch (IOException e2) {
				}
			}
			if ( is == null ) {
				try {
					is = (FileInputStream) contentResolver.openInputStream(uri);
					errorMessage = null;
				} catch (FileNotFoundException e) {
					errorMessage = String.format(context.getResources().getString(R.string.message_shared_file_not_found), fileObj.getDisplayName());
					Log.e(TAG, "copyCacheFile(), File not found: " + uri.toString());
				}
			}
			if ( is != null ) {
//				Log.d(TAG, "copyCacheFile(), " + "Read input stream...");
				byte[] buffer = new byte[context.getResources().getInteger(R.integer.bufferSize)];
				int bytesRead;
				FileOutputStream os = null;
				File cacheFile = new File(fileObj.getCacheFileName());
				try {
					os = new FileOutputStream(cacheFile);
					while ( (bytesRead = is.read(buffer, 0, buffer.length)) > 0 ) {
						os.write(buffer, 0, bytesRead);
					}
				} catch (FileNotFoundException fnfe) {
					errorMessage = String.format(context.getResources().getString(R.string.message_failed_to_create_upload_temporary), fileObj.getDisplayName());
					Log.e(TAG, "copyCacheFile(), " + cacheFile.getAbsolutePath() + ", Create temp file exception: ", fnfe);
				} catch (IOException ioe) {
					errorMessage = String.format(context.getResources().getString(R.string.message_failed_to_write_upload_temporary), fileObj.getDisplayName());
					Log.e(TAG, "copyCacheFile(), " + cacheFile.getAbsolutePath() + ", Write content exception: ", ioe);
				} finally {
					try {
						is.close();
						os.flush();
						os.close();
					} catch (Exception exc) {
					}
				}
			} else {
				errorMessage = String.format(context.getResources().getString(R.string.message_shared_file_not_found), fileObj.getDisplayName());
				Log.e(TAG, "copyCacheFile(), Input stream is null: " + uri.toString());
			}
			if ( errorMessage != null ) {
				MsgUtils.showToast(context, errorMessage);
			} else {
				cacheFiles.put(fileObj, Boolean.TRUE);
			}

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ( resultCode == RESULT_OK ) {
			Bundle extras = data.getExtras();
			if ( requestCode == Constants.REQUEST_CHANGE_ACCOUNT ) {
				if ( extras != null ) {
					String accountName = extras.getString(Constants.EXT_PARAM_NEW_ACCOUNT);
					PrefUtils.setActiveAccount(accountName);
					if ( !TextUtils.isEmpty(accountName) ) {
						Account account = AccountUtils.getAccount(accountName);
						accountLogin(account, true);
					}
				}
			} else if ( requestCode == Constants.REQUEST_CHANGE_COMPUTER ) {
				if ( extras != null ) {
					int newComputerId = extras.getInt(Constants.EXT_PARAM_NEW_COMPUTER_ID);
					String newComputerName = extras.getString(Constants.EXT_PARAM_NEW_COMPUTER_NAME);

					Account activeAccount = AccountUtils.getActiveAccount();
					AccountManager accountManager = AccountManager.get(ConfirmUploadActivity.this);
					accountManager.setUserData(activeAccount, Constants.PARAM_COMPUTER_ID, Integer.toString(newComputerId));
					accountManager.setUserData(activeAccount, Constants.PARAM_COMPUTER_NAME, newComputerName);
					accountManager.setUserData(activeAccount, Constants.PARAM_SOCKET_CONNECTED, Boolean.FALSE.toString());

					beforeDoComputerChanged(activeAccount, newComputerId, newComputerName);
				}
			}
		} else if ( resultCode == RESULT_CANCELED ) {
			if ( requestCode == Constants.REQUEST_INITIAL ) {
				MaterialDialog.SingleButtonCallback buttonCallback = new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						setResult(RESULT_CANCELED, null);
						finish();
					}
				};
				MsgUtils.showWarningMessage(ConfirmUploadActivity.this, getResources().getString(R.string.message_login_then_save_to_filelug), buttonCallback);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
//		Log.d(TAG, "onResume()");
		LocalBroadcastManager.getInstance(this).registerReceiver(mCacheStatusBroadcastReceiver,
			new IntentFilter(Constants.LOCAL_BROADCAST_CACHE_SHARED_STATUS));
	}

	@Override
	protected void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mCacheStatusBroadcastReceiver);
		super.onPause();
//		Log.d(TAG, "onPause()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		Log.d(TAG, "onDestroy()");
		if ( !MiscUtils.isUploadOrDownloadOrNotificationServiceRunning() ) {
//			Log.d(TAG, "onDestroy(), Delete files in out_cache_dir!");
			FileCache.deleteFilesInDir(FileCache.OUT_CACHE_DIR, false);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
//		Log.d(TAG, "onBackPressed()");
		overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
//		Log.d(TAG, "onPostCreate()");
		if ( mRequestFromShare ) {
			mToolbar.setTitle(R.string.intent_save_to_filelug);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.action_change_account:
				doChangeAccount();
				return true;
			case R.id.action_change_computer:
				doChangeComputer();
				return true;
			case android.R.id.home:
				finish();
				overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void doChangeAccount() {
		if ( MiscUtils.isUploadOrDownloadOrNotificationServiceRunning() ) {
			MsgUtils.showWarningMessage(this, R.string.message_cannot_change_account);
			return;
		}

		Intent intent = new Intent(this, ChangeAccountActivity.class);

		Account mActiveAccount = AccountUtils.getActiveAccount();
		if ( mActiveAccount != null ) {
			String accountName = mActiveAccount.name;
			boolean loggedIn = AccountUtils.isLoggedIn(mActiveAccount);
			intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
			intent.putExtra(Constants.EXT_PARAM_LOGGED_IN, loggedIn);
		}

		startActivityForResult(intent, Constants.REQUEST_CHANGE_ACCOUNT);
	}

	private void doChangeComputer() {
		if ( MiscUtils.isUploadOrDownloadOrNotificationServiceRunning() ) {
			MsgUtils.showWarningMessage(this, R.string.message_cannot_change_computer);
			return;
		}

		Account mActiveAccount = AccountUtils.getActiveAccount();
		if ( mActiveAccount != null ) {
			AccountManager mAccountManager = AccountManager.get(ConfirmUploadActivity.this);
			String tmpComputerId = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_COMPUTER_ID);
			int computerId = TextUtils.isEmpty(tmpComputerId) ? -1 : Integer.valueOf(tmpComputerId);
			String computerName = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_COMPUTER_NAME);
			String tmpSocketConnected = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_SOCKET_CONNECTED);
			boolean socketConnected = tmpSocketConnected==null ? false : Boolean.valueOf(tmpSocketConnected);

			Intent intent = new Intent(this, ChangeComputerActivity.class);
			intent.putExtra(Constants.PARAM_COMPUTER_ID, computerId);
			intent.putExtra(Constants.PARAM_COMPUTER_NAME, computerName);
			intent.putExtra(Constants.PARAM_SOCKET_CONNECTED, socketConnected);
			startActivityForResult(intent, Constants.REQUEST_CHANGE_COMPUTER);
		}
	}

	private LocalFileObject[] convertStreamToFileObjectArray(Context context, Object stream) {
		List<LocalFileObject> fileList = new ArrayList<LocalFileObject>();
		List<String> errorFileList = new ArrayList<String>();

		if ( stream instanceof List ) {
			List list = (List)stream;
			for ( Object obj : list ) {
				if ( obj instanceof Uri ) {
					Uri uri = (Uri)obj;
					LocalFileObject fileObject = LocalFileUtils.getLocalFileObjectFromUri(context, uri, true, true);
					if ( fileObject != null ) {
						fileList.add(fileObject);
					} else {
						errorFileList.add(obj.toString());
					}
				} else {
					errorFileList.add(obj.toString());
				}
			}
		} else if ( stream instanceof Uri ) {
			Uri uri = (Uri)stream;
			LocalFileObject fileObject = LocalFileUtils.getLocalFileObjectFromUri(context, uri, true, true);
			if ( fileObject != null ) {
				fileList.add(fileObject);
			} else {
				errorFileList.add(stream.toString());
			}
		} else {
			errorFileList.add(stream.toString());
		}

		int errSize = errorFileList.size();
		if ( errSize > 0 ) {
			String errorFileListStr = "";
			for ( String errorFile : errorFileList ) {
				errorFileListStr += ( TextUtils.isEmpty(errorFileListStr) ? "" : "," ) + stream.toString();
			}
			String message = null;
			if ( errSize == 1 ) {
				message = String.format(getResources().getString(R.string.message_failed_to_read_file), errorFileListStr);
			} else {
				message = String.format(getResources().getString(R.string.message_failed_to_read_files), errSize, errorFileListStr);
			}
			MsgUtils.showWarningMessage(this, message);
		}

		return fileList.toArray(new LocalFileObject[0]);
	}

	private void pingDesktop() {
		FilelugUtils.Callback callback = new FilelugUtils.Callback() {
			@Override
			public void onError(int errorCode, String errorMessage) {
			}
			@Override
			public void onSuccess(Bundle result) {
				long uploadSizeLimit = result.getLong(Constants.PARAM_UPLOAD_SIZE_LIMIT);
				//long downloadSizeLimit = result.getLong(Constants.PARAM_DOWNLOAD_SIZE_LIMIT);
				String authToken = result.getString(AccountManager.KEY_AUTHTOKEN);

				String msg = checkUploadFileLimit(uploadSizeLimit);
				if (msg != null) {
					MsgUtils.showInfoMessage(ConfirmUploadActivity.this, msg);
				} else {
					updateRepoUserProfiles(authToken);
				}
			}
		};
		FilelugUtils.pingDesktopB(ConfirmUploadActivity.this, callback);
	}

	private String checkUploadFileLimit(long uploadSizeLimit) {
		String msg = null;
		String str = null;
		for ( int i=0; i<mSelectedItemsIds.length; i++ ) {
			LocalFile uploadFile = mSelectedFiles[mSelectedItemsIds[i]];
			long fileSize = uploadFile.getSize();
			if ( uploadSizeLimit < fileSize ) {
				str = (str == null ? "\n" : str+",\n") + uploadFile.getName();
			}
		}
		if ( str != null ) {
			msg = String.format(getResources().getString(R.string.message_exceed_upload_size_limit), str);
		}
		return msg;
	}

	private void updateRepoUserProfiles(final String authToken) {
		final Account activeAccount = AccountUtils.getActiveAccount();
		HashMap<String, Object> updateConfig = new HashMap<String, Object>();

		if ( mSaveAsDefault_UploadPath ) {
			PrefUtils.setUploadPath(activeAccount.name, mUploadPath);
			PrefUtils.setUploadPath(null, mUploadPath);
			updateConfig.put(Constants.PARAM_UPLOAD_DIRECTORY, mUploadPath);
		}
		if ( mSaveAsDefault_SubdirType ) {
			PrefUtils.setUploadSubdirType(activeAccount.name, String.valueOf(mSubdirType));
			PrefUtils.setUploadSubdirType(null, String.valueOf(mSubdirType));
			updateConfig.put(Constants.PARAM_UPLOAD_SUB_DIRECTORY_TYPE, Integer.valueOf(mSubdirType));
			if ( mSubdirType >= 2 ) {
				PrefUtils.setUploadSubdirValue(activeAccount.name, mCustomizedSubDirName);
				PrefUtils.setUploadSubdirValue(null, mCustomizedSubDirName);
				updateConfig.put(Constants.PARAM_UPLOAD_SUB_DIRECTORY_VALUE, mCustomizedSubDirName);
			}
		}
		if ( mSaveAsDefault_DescriptionType ) {
			PrefUtils.setUploadDescriptionType(activeAccount.name, String.valueOf(mDescriptionType));
			PrefUtils.setUploadDescriptionType(null, String.valueOf(mDescriptionType));
			updateConfig.put(Constants.PARAM_UPLOAD_DESCRIPTION_TYPE, Integer.valueOf(mDescriptionType));
			if ( mDescriptionType >= 2 ) {
				PrefUtils.setUploadDescriptionValue(activeAccount.name, mCustomizedDescription);
				PrefUtils.setUploadDescriptionValue(null, mCustomizedDescription);
				updateConfig.put(Constants.PARAM_UPLOAD_DESCRIPTION_VALUE, mCustomizedDescription);
			}
		}
		if ( mSaveAsDefault_NotificationType ) {
			PrefUtils.setUploadNotificationType(activeAccount.name, String.valueOf(mNotificationType));
			PrefUtils.setUploadNotificationType(null, String.valueOf(mNotificationType));
			updateConfig.put(Constants.PARAM_UPLOAD_NOTIFICATION_TYPE, Integer.valueOf(mNotificationType));
		}

		if ( updateConfig.size() == 0 ) {
			doUpload(authToken);
			return;
		}

		final HashMap<String, Object> profiles = updateConfig;

		String locale = getResources().getConfiguration().locale.toString();
		Response.Listener<String> response = new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Bundle userData = new Bundle();
				for ( String key : profiles.keySet() ) {
					Object value = profiles.get(key);
					if ( getResources().getString(R.string.pref_upload_path).equals(key) ) {
						userData.putString(Constants.PARAM_UPLOAD_DIRECTORY, (value!=null ? (String)value : ""));
					} else if ( getResources().getString(R.string.pref_upload_subdir).equals(key) ) {
						userData.putString(Constants.PARAM_UPLOAD_SUB_DIRECTORY_TYPE, ((Integer)value).toString());
					} else if ( getResources().getString(R.string.pref_upload_subdir_value).equals(key) ) {
						userData.putString(Constants.PARAM_UPLOAD_SUB_DIRECTORY_VALUE, (value!=null ? (String)value : ""));
					} else if ( getResources().getString(R.string.pref_upload_description_type).equals(key) ) {
						userData.putString(Constants.PARAM_UPLOAD_DESCRIPTION_TYPE, ((Integer)value).toString());
					} else if ( getResources().getString(R.string.pref_upload_description_value).equals(key) ) {
						userData.putString(Constants.PARAM_UPLOAD_DESCRIPTION_VALUE, (value!=null ? (String)value : ""));
					} else if ( getResources().getString(R.string.pref_upload_notification_type).equals(key) ) {
						userData.putString(Constants.PARAM_UPLOAD_NOTIFICATION_TYPE, ((Integer)value).toString());
					}
				}
				AccountUtils.resetUserData(activeAccount, userData);
				doUpload(authToken);
			}
		};
		BaseResponseError error = new BaseResponseError(true, ConfirmUploadActivity.this) {
			@Override
			protected void afterShowErrorMessage(VolleyError volleyError) {
				super.afterShowErrorMessage(volleyError);
				doUpload(authToken);
			}
		};
		RepositoryClient.getInstance().changeUserComputerProfiles(
			authToken,
			profiles,
			locale,
			response,
			error
		);
	}

	private void doUpload(final String authToken) {
		final Context context = ConfirmUploadActivity.this;
		Account activeAccount = AccountUtils.getActiveAccount();
		AccountManager accountManager = AccountManager.get(context);
		final String userId = accountManager.getUserData(activeAccount, Constants.EXT_PARAM_FILELUG_ACCOUNT);
		final String computerId = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_ID);
		final String lugServerId = accountManager.getUserData(activeAccount, Constants.PARAM_LUG_SERVER_ID);
		String fileSeparator = accountManager.getUserData(activeAccount, Constants.PARAM_FILE_SEPARATOR);
		String lineSeparator = accountManager.getUserData(activeAccount, Constants.PARAM_LINE_SEPARATOR);

		String[] transferKeyArray = new String[mSelectedItemsIds.length];
		Map<String, LocalFile> tempFiles = new HashMap<String, LocalFile>();
		String fileListStr = getResources().getString(R.string.content_header_file_upload_list) + lineSeparator;
		String fileNameCombineStr = "";
		for ( int i=0; i<mSelectedItemsIds.length; i++ ) {
			LocalFile uploadFile = mSelectedFiles[mSelectedItemsIds[i]];
			String fileName = uploadFile.getName();
			String transferKey = RepositoryUtility.generateUploadTransferKey(userId, fileName);
			transferKeyArray[i] = transferKey;
			tempFiles.put(transferKey, uploadFile);
			fileListStr += fileName + lineSeparator;
			fileNameCombineStr += fileName;
		}

		final String subDirValue = MiscUtils.getCustomizedSubDirName(ConfirmUploadActivity.this, mSubdirType, mCustomizedSubDirName);
		final String descriptionValue = MiscUtils.getCustomizedDescription(ConfirmUploadActivity.this, mDescriptionType, mCustomizedDescription, fileListStr, lineSeparator);
		final String uploadGroupId = RepositoryUtility.generateUploadGroupId(fileNameCombineStr);
		final String fullUploadPath = mUploadPath + ( subDirValue == null ? "" : fileSeparator + subDirValue );
		final Map<String, LocalFile> uploadFiles = tempFiles;

		UploadGroupContentValues values = new UploadGroupContentValues()
			.putUserId(userId)
			.putComputerId(Integer.valueOf(computerId))
			.putGroupId(uploadGroupId)
			.putFromAnotherApp(mRequestFromShare)
			.putStartTimestamp(new Date().getTime())
			.putUploadDirectory(fullUploadPath)
			.putSubdirectoryType(mSubdirType)
			.putSubdirectoryValue(subDirValue)
			.putDescriptionType(mDescriptionType)
			.putDescriptionValue(descriptionValue)
			.putNotificationType(mNotificationType);
		values.insert(context.getContentResolver());

		String locale = getResources().getConfiguration().locale.toString();
		RepositoryClient.getInstance().createFileUploadSummary(
			authToken,
			lugServerId,
			uploadGroupId,
			transferKeyArray,
			fullUploadPath,
			mSubdirType,
			subDirValue,
			mDescriptionType,
			descriptionValue,
			mNotificationType,
			locale,
			new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					MsgUtils.showToast(ConfirmUploadActivity.this, R.string.message_start_transfer_files);
					LocalFileUtils.uploadFiles(context, userId, computerId, lugServerId, authToken, uploadGroupId, mRequestFromShare, fullUploadPath, uploadFiles);
					Intent intent = new Intent();
					setResult(RESULT_OK, intent);
					finish();
				}
			},
			new BaseResponseError(true, ConfirmUploadActivity.this)
		);
	}

	private void changeSelectedUploadFiles() {
		mConfirmUploadFileListAdapter.setSelectedIds(mSelectedItemsIds);

		MaterialDialog.ListCallback listCallback = new MaterialDialog.ListCallback() {
			@Override
			public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
				mConfirmUploadFileListAdapter.toggleSelection(which);
			}
		};

		MaterialDialog.SingleButtonCallback buttonCallback = new MaterialDialog.SingleButtonCallback() {

			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				switch (which) {
					default:
						positiveButtonClick(dialog);
						break;
					case NEUTRAL:
						neutralButtonClick(dialog);
						break;
					case NEGATIVE:
						negativeButtonClick(dialog);
						break;
				}
			}

			private void positiveButtonClick(MaterialDialog dialog) {
				mSelectedItemsIds = mConfirmUploadFileListAdapter.getSelectedIds();
				String fileCountStr = String.format(getResources().getString(R.string.format_selected_file_count), mSelectedItemsIds.length);
				tvFileCount.setText(fileCountStr);
				dialog.dismiss();
			}

			private void negativeButtonClick(MaterialDialog dialog) {
				mConfirmUploadFileListAdapter.selectAll();
			}

			private void neutralButtonClick(MaterialDialog dialog) {
				mConfirmUploadFileListAdapter.removeAll();
			}

		};
		DialogUtils.createMultiChoiceDialog(
			ConfirmUploadActivity.this,
			R.string.label_files_to_upload,
			R.drawable.menu_ic_upload_file,
			android.R.string.ok,
			R.string.action_select_all,
			R.string.action_cancel_selection,
			mConfirmUploadFileListAdapter,
			listCallback,
			buttonCallback
		).show();
	}

	private void changeUploadToFolder() {
		FilelugUtils.Callback callback = new FilelugUtils.Callback() {
			@Override
			public void onError(int errorCode, String errorMessage) {
			}
			@Override
			public void onSuccess(Bundle result) {
				RemoteFolderChooser folderChooser = new RemoteFolderChooser();
				Bundle args = new Bundle();
				if ( !TextUtils.isEmpty(mUploadPath) ) {
					args.putString(Constants.EXT_PARAM_CURRENT_FOLDER, mUploadPath);
				}
				args.putBoolean(Constants.EXT_PARAM_IS_SAVE_AS_DEFAULT, mSaveAsDefault_UploadPath);
				folderChooser.setArguments(args);
				folderChooser.show(ConfirmUploadActivity.this);
			}
		};
		FilelugUtils.pingDesktopB(ConfirmUploadActivity.this, callback);
	}

	private void changeSubFolderType() {
		MaterialDialog.ListCallbackSingleChoice callback = new MaterialDialog.ListCallbackSingleChoice() {
			@Override
			public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
				mSubdirType = which;
				tvSubdirType.setText(MiscUtils.getSubDirTypeSettingText(ConfirmUploadActivity.this, mSubdirType, mCustomizedSubDirName));
				mSaveAsDefault_SubdirType = dialog.isPromptCheckBoxChecked();
				dialog.dismiss();
				if ( which >= 2 ) {
					editCustomizedSubDirName();
				}
				return true; // allow selection
			}
		};
		DialogUtils.createSingleChoiceDialog(
			ConfirmUploadActivity.this,
			R.string.label_subfolder_type,
			R.drawable.ic_new_folder,
			getResources().getStringArray(R.array.subfolder_type_array),
			mSubdirType,
			true,
			R.string.label_save_as_default,
			mSaveAsDefault_SubdirType,
			callback
		).show();
	}

	private void editCustomizedSubDirName() {
		InputFilter filter = new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				if (source != null && Constants.INVALID_FILE_NAME_CHARACTERS.contains(("" + source))) {
					return "";
				}
				return null;
			}
		};
		MaterialDialog.SingleButtonCallback singleButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				EditText editText = dialog.getInputEditText();
				if ( !Validation.hasText(editText) ) {
					editText.requestFocus();
					return;
				}

				mCustomizedSubDirName = editText.getText().toString().trim();
				tvSubdirType.setText(MiscUtils.getSubDirTypeSettingText(ConfirmUploadActivity.this, mSubdirType, mCustomizedSubDirName));

				InputMethodManager imm = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				dialog.dismiss();
			}
		};
		DialogUtils.createTextInputDialog(
			ConfirmUploadActivity.this,
			R.string.label_subfolder_type,
			R.string.hint_enter_customized_name,
			mCustomizedSubDirName,
			InputType.TYPE_CLASS_TEXT,
			false,
			new InputFilter[] {filter},
			singleButtonCallback
		).show();
	}

	private void changeDescriptionType() {
		MaterialDialog.ListCallbackSingleChoice callback = new MaterialDialog.ListCallbackSingleChoice() {
			@Override
			public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
				mDescriptionType = which;
				tvDescriptionType.setText(MiscUtils.getDescriptionTypeSettingText(ConfirmUploadActivity.this, mDescriptionType, mCustomizedDescription));
				mSaveAsDefault_DescriptionType = dialog.isPromptCheckBoxChecked();
				dialog.dismiss();
				if ( which >= 2 ) {
					editCustomizedDescription();
				}
				return true; // allow selection
			}
		};
		DialogUtils.createSingleChoiceDialog(
			ConfirmUploadActivity.this,
			R.string.label_upload_description_type,
			R.drawable.ic_note,
			getResources().getStringArray(R.array.description_type_array),
			mDescriptionType,
			true,
			R.string.label_save_as_default,
			mSaveAsDefault_DescriptionType,
			callback
		).show();
	}

	private void editCustomizedDescription() {
		MaterialDialog.SingleButtonCallback singleButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				EditText editText = dialog.getInputEditText();
				if ( !Validation.hasText(editText) ) {
					editText.requestFocus();
					return;
				}

				mCustomizedDescription = editText.getText().toString().trim();
				tvDescriptionType.setText(MiscUtils.getDescriptionTypeSettingText(ConfirmUploadActivity.this, mDescriptionType, mCustomizedDescription));

				InputMethodManager imm = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				dialog.dismiss();
			}
		};
		DialogUtils.createTextInputDialog(
			ConfirmUploadActivity.this,
			R.string.label_upload_description_type,
			R.string.hint_enter_customized_description,
			mCustomizedDescription,
			InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE,
			false,
			null,
			singleButtonCallback
		).show();
	}

	private void changeNotificationType() {
		MaterialDialog.ListCallbackSingleChoice callback = new MaterialDialog.ListCallbackSingleChoice() {
			@Override
			public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
				mNotificationType = which;
				tvNotificationType.setText(MiscUtils.getNotificationTypeStr(ConfirmUploadActivity.this, which));
				mSaveAsDefault_NotificationType = dialog.isPromptCheckBoxChecked();
				dialog.dismiss();
				return true; // allow selection
			}
		};
		DialogUtils.createSingleChoiceDialog(
			ConfirmUploadActivity.this,
			R.string.label_upload_notification_type,
			R.drawable.ic_notifications,
			getResources().getStringArray(R.array.notification_type_array),
			mNotificationType,
			true,
			R.string.label_save_as_default,
			mSaveAsDefault_NotificationType,
			callback
		).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int menuRes = R.menu.menu_main;
		if ( mRequestFromShare ) {
			menuRes = R.menu.menu_upload_confirm;
		}
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(menuRes, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onFolderSelection(RemoteFile folder, boolean saveAsDefault) {
		mUploadPath = folder.getFullRealName();
		mSaveAsDefault_UploadPath = saveAsDefault;
		tvUploadPath.setText(mUploadPath);
	}

	private void toggleSelection(int position) {
		mConfirmUploadFileListAdapter.toggleSelection (position);
	}

	@Override
	public void loginOrConnectStatusChanged(int status) {
		initUIText();
		// Check files is need to copy to cache, and only run once!
		if ( mNeedCopyCache == null ) {
			mNeedCopyCache = Boolean.FALSE;
			for ( LocalFileObject fileObj : mSelectedFiles ) {
				String cacheFileName = fileObj.getCacheFileName();
				if ( !TextUtils.isEmpty(cacheFileName) ) {
					mNeedCopyCache = Boolean.TRUE;
					break;
				}
			}
			if ( mNeedCopyCache ) {
				mFloatingActionButton.setEnabled(false);
				MsgUtils.showToast(this, R.string.message_read_shared_files);
				new SaveToCacheTask().execute();
			}
		}
	}

}
