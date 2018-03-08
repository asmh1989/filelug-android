package com.filelug.android.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.filelug.android.ui.adapter.ConfirmDownloadFileListAdapter;
import com.filelug.android.ui.model.RemoteFile;
import com.filelug.android.ui.model.RemoteFileObject;
import com.filelug.android.ui.viewHolder.SelectableViewHolder;
import com.filelug.android.ui.widget.LocalFolderChooser;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.FilelugUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.PrefUtils;
import com.filelug.android.util.RemoteFileUtils;
import com.filelug.android.util.Validation;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.util.HashMap;

public class ConfirmDownloadActivity extends BaseConfigureActivity implements LocalFolderChooser.FolderSelectCallback {

	private static final String TAG = ConfirmDownloadActivity.class.getSimpleName();

	private Toolbar mToolbar;
	private View vRowFileCount = null;
	private View vRowDownloadToFolder = null;
	private View vRowSubFolderType = null;
	private View vRowDescriptionType = null;
	private View vRowNotificationType = null;
	private TextView tvFileCount = null;
	private TextView tvDownloadPath = null;
	private TextView tvSubdirType = null;
	private TextView tvDescriptionType = null;
	private TextView tvNotificationType = null;
	private FloatingActionButton mFloatingActionButton = null;

	private ConfirmDownloadFileListAdapter mConfirmDownloadFileListAdapter = null;
	private RemoteFile[] mSelectedFiles = null;
	private Integer[] mSelectedItemsIds = null;

	private String mDownloadPath = null;
	private int mSubdirType = 0;
	private String mCustomizedSubDirName = null;
	private int mDescriptionType = 0;
	private String mCustomizedDescription = null;
	private int mNotificationType = 0;

	private boolean mSaveAsDefault_DownloadPath = false;
	private boolean mSaveAsDefault_SubdirType = false;
	private boolean mSaveAsDefault_DescriptionType = false;
	private boolean mSaveAsDefault_NotificationType = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_confirm_download);

		Bundle extras = getIntent().getExtras();

		initExtras(extras);
		initUI();
		initUIText();
	}

	private void initExtras(Bundle extras) {
		if ( extras != null ) {
			if ( extras.containsKey(Constants.EXT_PARAM_SELECTED_DOWNLOAD_FILES) ) {
				Parcelable[] array = extras.getParcelableArray(Constants.EXT_PARAM_SELECTED_DOWNLOAD_FILES);
				mSelectedFiles = new RemoteFileObject[array.length];
				for ( int i=0; i<array.length; i++ ) {
					mSelectedFiles[i] = (RemoteFileObject)array[i];
				}
//				Arrays.sort(mSelectedFiles, new SortUtils.RemoteFileNameAscComparator());
				mSelectedItemsIds = new Integer[mSelectedFiles.length];
				for ( int i=0; i<mSelectedFiles.length; i++ ) {
					mSelectedItemsIds[i] = i;
				}
			}
		}
	}

	private void initUI() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		mToolbar.setTitle(R.string.drawer_section_download);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		tvFileCount = (TextView) findViewById(R.id.file_count);
		vRowFileCount = findViewById(R.id.row_file_count);
		vRowFileCount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeSelectedDownloadFiles();
			}
		});
		tvDownloadPath = (TextView) findViewById(R.id.download_to_folder);
		vRowDownloadToFolder = findViewById(R.id.row_download_to_folder);
		vRowDownloadToFolder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeDownloadToFolder();
			}
		});
		tvSubdirType = (TextView) findViewById(R.id.sub_folder_type);
		vRowSubFolderType = findViewById(R.id.row_sub_folder_type);
		vRowSubFolderType.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeSubFolderType();
			}
		});
		tvDescriptionType = (TextView) findViewById(R.id.download_description);
		vRowDescriptionType = findViewById(R.id.row_download_description);
		vRowDescriptionType.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeDescriptionType();
			}
		});
		tvNotificationType = (TextView) findViewById(R.id.notification_type);
		vRowNotificationType = findViewById(R.id.row_notification_type);
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
				final Context context = ConfirmDownloadActivity.this;
				if (mSelectedItemsIds == null || mSelectedItemsIds.length == 0) {
					MsgUtils.showInfoMessage(context, context.getResources().getString(R.string.message_no_file_selected));
					return;
				}
				if (TextUtils.isEmpty(mDownloadPath)) {
					MsgUtils.showInfoMessage(context, String.format(getResources().getString(R.string.message_field_can_not_be_empty_2), getResources().getString(R.string.label_download_to_folder)));
					return;
				}
				if ( mSubdirType >= 2 && TextUtils.isEmpty(mCustomizedSubDirName) ) {
					MsgUtils.showInfoMessage(context, String.format(getResources().getString(R.string.message_field_can_not_be_empty_2), MiscUtils.getSubdirTypeStr(context, 2)));
					return;
				}
				if ( mDescriptionType >= 2 && TextUtils.isEmpty(mCustomizedDescription) ) {
					MsgUtils.showInfoMessage(context, String.format(getResources().getString(R.string.message_field_can_not_be_empty_2), MiscUtils.getDescriptionTypeStr(context, 2)));
					return;
				}
				if ( AccountUtils.getActiveAccount() == null) {
					MsgUtils.showToast(context, R.string.message_registered_computer_not_found);
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

		mDownloadPath = PrefUtils.getDownloadPath(accountName);
		if ( TextUtils.isEmpty(mDownloadPath) ) {
			mSaveAsDefault_DownloadPath = true;
			mSaveAsDefault_SubdirType = true;
			mSaveAsDefault_DescriptionType = true;
			mSaveAsDefault_NotificationType = true;
		}
		String _subdirType = PrefUtils.getDownloadSubdirType(accountName);
		mSubdirType = TextUtils.isEmpty(_subdirType) ? PrefUtils.DEFAULT_VALUE_DOWNLOAD_SUB_DIR : Integer.valueOf(_subdirType);
		String _subdirValue = PrefUtils.getDownloadSubdirValue(accountName);
		mCustomizedSubDirName = TextUtils.isEmpty(_subdirValue) ? null : _subdirValue;
		String _descriptionType = PrefUtils.getDownloadDescriptionType(accountName);
		mDescriptionType = TextUtils.isEmpty(_descriptionType) ? PrefUtils.DEFAULT_VALUE_DOWNLOAD_DESCRIPTION_TYPE : Integer.valueOf(_descriptionType);
		String _descriptionValue = PrefUtils.getDownloadDescriptionValue(accountName);
		mCustomizedDescription = TextUtils.isEmpty(_descriptionValue) ? null : _descriptionValue;
		String _notificationType = PrefUtils.getDownloadNotificationType(accountName);
		mNotificationType = TextUtils.isEmpty(_descriptionType) ? PrefUtils.DEFAULT_VALUE_DOWNLOAD_NOTIFICATION_TYPE : Integer.valueOf(_notificationType);

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
		mConfirmDownloadFileListAdapter = new ConfirmDownloadFileListAdapter(ConfirmDownloadActivity.this, mSelectedFiles, clickListener);

		String fileCountStr = String.format(getResources().getString(R.string.format_selected_file_count), mSelectedItemsIds.length);
		String downloadPathStr = null;
		if ( TextUtils.isEmpty(mDownloadPath) ) {
			downloadPathStr = getResources().getString(R.string.message_not_set);
		} else {
			downloadPathStr = mDownloadPath;
		}
		String subdirTypeStr = MiscUtils.getSubDirTypeSettingText(ConfirmDownloadActivity.this, mSubdirType, mCustomizedSubDirName);
		String descriptionTypeStr = MiscUtils.getDescriptionTypeSettingText(ConfirmDownloadActivity.this, mDescriptionType, mCustomizedDescription);
		String notificationTypeStr = MiscUtils.getNotificationTypeStr(ConfirmDownloadActivity.this, mNotificationType);

		tvFileCount.setText(fileCountStr);
		tvDownloadPath.setText(downloadPathStr);
		tvSubdirType.setText(subdirTypeStr);
		tvDescriptionType.setText(descriptionTypeStr);
		tvNotificationType.setText(notificationTypeStr);
	}

	private void pingDesktop() {
		FilelugUtils.Callback callback = new FilelugUtils.Callback() {
			@Override
			public void onError(int errorCode, String errorMessage) {
			}
			@Override
			public void onSuccess(Bundle result) {
				//long uploadSizeLimit = result.getLong(Constants.PARAM_UPLOAD_SIZE_LIMIT);
				long downloadSizeLimit = result.getLong(Constants.PARAM_DOWNLOAD_SIZE_LIMIT);
				String authToken = result.getString(AccountManager.KEY_AUTHTOKEN);

				String msg = checkDownloadFileLimit(downloadSizeLimit);
				if (msg != null) {
					MsgUtils.showInfoMessage(ConfirmDownloadActivity.this, msg);
				} else {
					updateRepoUserProfiles(authToken);
				}
			}
		};
		FilelugUtils.pingDesktopB(ConfirmDownloadActivity.this, callback);
	}

	private String checkDownloadFileLimit(long downloadSizeLimit) {
		String msg = null;
		String str = null;
		for ( int i=0; i<mSelectedItemsIds.length; i++ ) {
			RemoteFile downloadFile = mSelectedFiles[mSelectedItemsIds[i]];
			long fileSize = downloadFile.getSize();
			if ( downloadSizeLimit < fileSize ) {
				str = (str == null ? "\n" : str+",\n") + downloadFile.getName();
			}
		}
		if ( str != null ) {
			msg = String.format(getResources().getString(R.string.message_exceed_download_size_limit), str);
		}
		return msg;
	}

	private void updateRepoUserProfiles(final String authToken) {
		final Account activeAccount = AccountUtils.getActiveAccount();
		HashMap<String, Object> updateConfig = new HashMap<String, Object>();

		if ( mSaveAsDefault_DownloadPath ) {
			PrefUtils.setDownloadPath(activeAccount.name, mDownloadPath);
			PrefUtils.setDownloadPath(null, mDownloadPath);
			updateConfig.put(Constants.PARAM_DOWNLOAD_DIRECTORY, mDownloadPath);
		}
		if ( mSaveAsDefault_SubdirType ) {
			PrefUtils.setDownloadSubdirType(activeAccount.name, String.valueOf(mSubdirType));
			PrefUtils.setDownloadSubdirType(null, String.valueOf(mSubdirType));
			updateConfig.put(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_TYPE, Integer.valueOf(mSubdirType));
			if ( mSubdirType >= 2 ) {
				PrefUtils.setDownloadSubdirValue(activeAccount.name, mCustomizedSubDirName);
				PrefUtils.setDownloadSubdirValue(null, mCustomizedSubDirName);
				updateConfig.put(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_VALUE, mCustomizedSubDirName);
			}
		}
		if ( mSaveAsDefault_DescriptionType ) {
			PrefUtils.setDownloadDescriptionType(activeAccount.name, String.valueOf(mDescriptionType));
			PrefUtils.setDownloadDescriptionType(null, String.valueOf(mDescriptionType));
			updateConfig.put(Constants.PARAM_DOWNLOAD_DESCRIPTION_TYPE, Integer.valueOf(mDescriptionType));
			if ( mDescriptionType >= 2 ) {
				PrefUtils.setDownloadDescriptionValue(activeAccount.name, mCustomizedDescription);
				PrefUtils.setDownloadDescriptionValue(null, mCustomizedDescription);
				updateConfig.put(Constants.PARAM_DOWNLOAD_DESCRIPTION_VALUE, mCustomizedDescription);
			}
		}
		if ( mSaveAsDefault_NotificationType ) {
			PrefUtils.setDownloadNotificationType(activeAccount.name, String.valueOf(mNotificationType));
			PrefUtils.setDownloadNotificationType(null, String.valueOf(mNotificationType));
			updateConfig.put(Constants.PARAM_DOWNLOAD_NOTIFICATION_TYPE, Integer.valueOf(mNotificationType));
		}

		if ( updateConfig.size() == 0 ) {
			doDownload(authToken);
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
					if ( getResources().getString(R.string.pref_download_path).equals(key) ) {
						userData.putString(Constants.PARAM_DOWNLOAD_DIRECTORY, (value!=null ? (String)value : ""));
					} else if ( getResources().getString(R.string.pref_download_subdir).equals(key) ) {
						userData.putString(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_TYPE, ((Integer)value).toString());
					} else if ( getResources().getString(R.string.pref_download_subdir_value).equals(key) ) {
						userData.putString(Constants.PARAM_DOWNLOAD_SUB_DIRECTORY_VALUE, (value!=null ? (String)value : ""));
					} else if ( getResources().getString(R.string.pref_download_description_type).equals(key) ) {
						userData.putString(Constants.PARAM_DOWNLOAD_DESCRIPTION_TYPE, ((Integer)value).toString());
					} else if ( getResources().getString(R.string.pref_download_description_value).equals(key) ) {
						userData.putString(Constants.PARAM_DOWNLOAD_DESCRIPTION_VALUE, (value!=null ? (String)value : ""));
					} else if ( getResources().getString(R.string.pref_download_notification_type).equals(key) ) {
						userData.putString(Constants.PARAM_DOWNLOAD_NOTIFICATION_TYPE, ((Integer)value).toString());
					}
				}
				AccountUtils.resetUserData(activeAccount, userData);
				doDownload(authToken);
			}
		};
		BaseResponseError error = new BaseResponseError(true, ConfirmDownloadActivity.this) {
			@Override
			protected void afterShowErrorMessage(VolleyError volleyError) {
				super.afterShowErrorMessage(volleyError);
				doDownload(authToken);
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

	private void doDownload(String authToken) {
		RemoteFile[] selectedFiles = new RemoteFile[mSelectedItemsIds.length];
		for ( int i=0; i<mSelectedItemsIds.length; i++ ) {
			RemoteFile selectedFile = mSelectedFiles[mSelectedItemsIds[i]];
			selectedFiles[i] = selectedFile;
		}

		AccountManager accountManager = AccountManager.get(this);
		Account activeAccount = AccountUtils.getActiveAccount();
		final String userId = accountManager.getUserData(activeAccount, Constants.EXT_PARAM_FILELUG_ACCOUNT);
		String tmpComputerId = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_ID);
		final int computerId = TextUtils.isEmpty(tmpComputerId) ? -1 : Integer.valueOf(tmpComputerId);
		String lugServerId = accountManager.getUserData(activeAccount, Constants.PARAM_LUG_SERVER_ID);

		RemoteFileUtils.FileDownloadSummaryCallback callback = new RemoteFileUtils.FileDownloadSummaryCallback() {
			@Override
			public void created(String downloadGroupId) {
				MsgUtils.showToast(ConfirmDownloadActivity.this, R.string.message_start_receiving_files);
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}
			@Override
			public void failed(String message) {
				MsgUtils.showToast(ConfirmDownloadActivity.this, message);
			}
		};
		try {
			RemoteFileUtils.createFileDownloadSummary(this, userId, computerId, lugServerId, authToken, selectedFiles, (String)tvDownloadPath.getText(), mSubdirType, mCustomizedSubDirName, mDescriptionType, mCustomizedDescription, mNotificationType, callback, false);
		} catch (Exception e) {
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	private void changeSelectedDownloadFiles() {
		mConfirmDownloadFileListAdapter.setSelectedIds(mSelectedItemsIds);

		MaterialDialog.ListCallback listCallback = new MaterialDialog.ListCallback() {
			@Override
			public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
				mConfirmDownloadFileListAdapter.toggleSelection(which);
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

			public void positiveButtonClick(MaterialDialog dialog) {
				mSelectedItemsIds = mConfirmDownloadFileListAdapter.getSelectedIds();
				String fileCountStr = String.format(getResources().getString(R.string.format_selected_file_count), mSelectedItemsIds.length);
				tvFileCount.setText(fileCountStr);
				dialog.dismiss();
			}

			public void negativeButtonClick(MaterialDialog dialog) {
				mConfirmDownloadFileListAdapter.selectAll();
			}

			public void neutralButtonClick(MaterialDialog dialog) {
				mConfirmDownloadFileListAdapter.removeAll();
			}

		};
		DialogUtils.createMultiChoiceDialog(
			ConfirmDownloadActivity.this,
			R.string.label_files_to_download,
			R.drawable.menu_ic_download_file,
			android.R.string.ok,
			R.string.action_select_all,
			R.string.action_cancel_selection,
			mConfirmDownloadFileListAdapter,
			listCallback,
			buttonCallback
		).show();
	}

	private void changeDownloadToFolder() {
		LocalFolderChooser folderChooser = new LocalFolderChooser();
		Bundle args = new Bundle();
		if ( !TextUtils.isEmpty(mDownloadPath) ) {
			args.putString(Constants.EXT_PARAM_CURRENT_FOLDER, mDownloadPath);
		}
		args.putBoolean(Constants.EXT_PARAM_IS_SAVE_AS_DEFAULT, mSaveAsDefault_DownloadPath);
		folderChooser.setArguments(args);
		folderChooser.show(ConfirmDownloadActivity.this);
	}

	private void changeSubFolderType() {
		MaterialDialog.ListCallbackSingleChoice callback = new MaterialDialog.ListCallbackSingleChoice() {
			@Override
			public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
				mSubdirType = which;
				tvSubdirType.setText(MiscUtils.getSubDirTypeSettingText(ConfirmDownloadActivity.this, mSubdirType, mCustomizedSubDirName));
				mSaveAsDefault_SubdirType = dialog.isPromptCheckBoxChecked();
				dialog.dismiss();
				if ( which >= 2 ) {
					editCustomizedSubDirName();
				}
				return true; // allow selection
			}
		};
		DialogUtils.createSingleChoiceDialog(
			ConfirmDownloadActivity.this,
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
				tvSubdirType.setText(MiscUtils.getSubDirTypeSettingText(ConfirmDownloadActivity.this, mSubdirType, mCustomizedSubDirName));

				InputMethodManager imm = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				dialog.dismiss();
			}
		};
		DialogUtils.createTextInputDialog(
			ConfirmDownloadActivity.this,
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
				tvDescriptionType.setText(MiscUtils.getDescriptionTypeSettingText(ConfirmDownloadActivity.this, mDescriptionType, mCustomizedDescription));
				mSaveAsDefault_DescriptionType = dialog.isPromptCheckBoxChecked();
				dialog.dismiss();
				if ( which >= 2 ) {
					editCustomizedDescription();
				}
				return true; // allow selection
			}
		};
		DialogUtils.createSingleChoiceDialog(
			ConfirmDownloadActivity.this,
			R.string.label_download_description_type,
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
				tvDescriptionType.setText(MiscUtils.getDescriptionTypeSettingText(ConfirmDownloadActivity.this, mDescriptionType, mCustomizedDescription));

				InputMethodManager imm = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				dialog.dismiss();
			}
		};
		DialogUtils.createTextInputDialog(
			ConfirmDownloadActivity.this,
			R.string.label_download_description_type,
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
				tvNotificationType.setText(MiscUtils.getNotificationTypeStr(ConfirmDownloadActivity.this, which));
				mSaveAsDefault_NotificationType = dialog.isPromptCheckBoxChecked();
				dialog.dismiss();
				return true; // allow selection
			}
		};
		DialogUtils.createSingleChoiceDialog(
			ConfirmDownloadActivity.this,
			R.string.label_download_notification_type,
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
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onFolderSelection(File folder, boolean saveAsDefault) {
		mDownloadPath = folder.getAbsolutePath();
		mSaveAsDefault_DownloadPath = saveAsDefault;
		tvDownloadPath.setText(mDownloadPath);
	}

	private void toggleSelection(int position) {
		mConfirmDownloadFileListAdapter.toggleSelection (position);
	}

}
