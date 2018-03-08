package com.filelug.android.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dd.processbutton.iml.ActionProcessButton;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.BaseResponseError;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.crepo.RepositoryUtility;
import com.filelug.android.ui.model.ComputerObject;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.FileCache;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.NetworkUtils;
import com.filelug.android.util.TransferDBHelper;
import com.filelug.android.util.Validation;
import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;
import com.marvinlabs.widget.floatinglabel.itempicker.FloatingLabelItemPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DeleteComputerActivity extends BaseConfigureActivity {

	private static final String TAG = DeleteComputerActivity.class.getSimpleName();

	private Toolbar mToolbar;
	private FloatingLabelEditText etConnectedComputer = null;
	private FloatingLabelItemPicker<ComputerObject> pkComputerToDelete = null;
	private ActionProcessButton btnDelete = null;

	private int mCurrentComputerId = -1;
	private String mCurrentComputerName = null;
	private String mCurrentComputerNameStr = null;

	private ArrayList<ComputerObject> mComputerList = null;
	private ComputerObject[] mComputers = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_delete_computer);

		Resources res = getResources();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mCurrentComputerId = extras.getInt(Constants.PARAM_COMPUTER_ID);
			mCurrentComputerName = extras.getString(Constants.PARAM_COMPUTER_NAME);
			if ( mCurrentComputerId != -1 ) {
				mCurrentComputerNameStr = mCurrentComputerName;
			} else {
				mCurrentComputerNameStr = res.getString(R.string.message_computer_not_connected);
			}
		}

		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		mToolbar.setTitle(R.string.page_delete_computer_header);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		etConnectedComputer = (FloatingLabelEditText) findViewById(R.id.etConnectedComputer);
		pkComputerToDelete = (FloatingLabelItemPicker<ComputerObject>) findViewById(R.id.ipComputerToDelete);

		btnDelete = (ActionProcessButton) findViewById(R.id.btnDelete);
		btnDelete.setColorScheme(res.getColor(R.color.main_color_A100), res.getColor(R.color.main_color_500), res.getColor(R.color.white), res.getColor(R.color.material_red_500));
		btnDelete.setMode(ActionProcessButton.Mode.ENDLESS);
		btnDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkValidation()) {
					showConfirmDialog();
				}
			}
		});

		etConnectedComputer.setInputWidgetText(mCurrentComputerNameStr);
		etConnectedComputer.getInputWidget().setEnabled(false);
		beforeDoFindAvailableComputers();
	}

	private boolean checkValidation() {
		boolean ret = true;

		if ( !NetworkUtils.isNetworkAvailable(DeleteComputerActivity.this) ) {
			return false;
		}

		if ( !Validation.hasSelect(pkComputerToDelete) ) {
			String message = String.format(getResources().getString(R.string.message_field_can_not_be_empty_2), getResources().getString(R.string.label_connect_to_computer));
			MsgUtils.showWarningMessage(DeleteComputerActivity.this, message);
			pkComputerToDelete.requestFocus();
			ret = false;
		}

		return ret;
	}

	private void setItemsEnable(boolean enable) {
		btnDelete.setEnabled(enable);
		pkComputerToDelete.setEnabled(enable);
	}

	private void beforeDoFindAvailableComputers() {
		if ( !NetworkUtils.isNetworkAvailable(DeleteComputerActivity.this) ) {
			return;
		}

		AccountUtils.AuthTokenCallback callback = new AccountUtils.AuthTokenCallback() {
			@Override
			public void onError(String errorMessage) {
				MsgUtils.showToast(DeleteComputerActivity.this, errorMessage);
			}
			@Override
			public void onSuccess(String authToken) {
				doFindAvailableComputers(authToken);
			}
		};
		AccountUtils.getAuthToken(DeleteComputerActivity.this, callback);
	}

	private void doFindAvailableComputers(String authToken) {
		if ( !NetworkUtils.isNetworkAvailable(DeleteComputerActivity.this) ) {
			return;
		}

		String locale = getResources().getConfiguration().locale.toString();

		RepositoryClient.getInstance().findAvailableComputers3(
			authToken,
			locale,
			new Response.Listener<JSONArray>() {
				@Override
				public void onResponse(JSONArray response) {
					initComputerPicker(response);
				}
			},
			new BaseResponseError(true, DeleteComputerActivity.this, BaseResponseError.MESSAGE_TYPE_ERROR_MESSAGE) {
				@Override
				protected void afterShowErrorMessage(VolleyError volleyError) {
					pkComputerToDelete.setEnabled(false);
					btnDelete.setEnabled(false);
				}
			}
		);
	}

	private void initComputerPicker(JSONArray jsonArray) {
		int tmpIndex = -1;
		mComputerList = new ArrayList<ComputerObject>();
		if (jsonArray != null && jsonArray.length() > 0) {
			try {
				for ( int i=0; i<jsonArray.length(); i++ ) {
					JSONObject jso = jsonArray.getJSONObject(i);
					String userComputerId = jso.getString(Constants.PARAM_USER_COMPUTER_ID);
					if ( TextUtils.isEmpty(userComputerId) ) {
						continue;
					}
					String userId = jso.getString(Constants.PARAM_USER_ID);
					int computerId = jso.getInt(Constants.PARAM_COMPUTER_ID);
					String computerGroup = jso.getString(Constants.PARAM_COMPUTER_GROUP);
					String computerName = jso.getString(Constants.PARAM_COMPUTER_NAME);
					String computerAdminId = jso.getString(Constants.PARAM_COMPUTER_ADMIN_ID);
					String lugServerId = jso.optString(Constants.PARAM_LUG_SERVER_ID, null);
					ComputerObject computerObject = new ComputerObject(userComputerId, userId, computerId, computerGroup, computerName, computerAdminId);
					mComputerList.add(computerObject);
					if ( computerId == mCurrentComputerId ) {
						tmpIndex = i;
					}
					TransferDBHelper.createOrUpdateUserComputer(userId, computerId, userComputerId, computerGroup, computerName, computerAdminId, lugServerId);
				}
			} catch (JSONException e) {
			}
		}

		final int selectedIndex = tmpIndex;
		boolean isEnable = mComputerList.size() > 0;
		pkComputerToDelete.setEnabled(isEnable);
		btnDelete.setEnabled(isEnable);

		if ( mComputerList.size() == 0 ) {
			MsgUtils.showWarningMessage(DeleteComputerActivity.this, R.string.message_can_not_find_available_computers);
			return;
		}

		mComputers = mComputerList.toArray(new ComputerObject[0]);
		pkComputerToDelete.setAvailableItems(mComputerList);
		if ( selectedIndex > -1 ) {
			pkComputerToDelete.setSelectedIndices(new int[]{selectedIndex});
		}
		pkComputerToDelete.setWidgetListener(new FloatingLabelItemPicker.OnWidgetEventListener<ComputerObject>() {
			@Override
			public void onShowItemPickerDialog(final FloatingLabelItemPicker source) {
				String[] computers = new String[mComputers.length];
				for ( int i=0; i<mComputers.length; i++ ) {
					ComputerObject computerObject = mComputers[i];
					computers[i] = computerObject.toString();
				}
				MaterialDialog.ListCallbackSingleChoice callback = new MaterialDialog.ListCallbackSingleChoice() {
					@Override
					public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
						if (which < 0) {
							return false;
						}
						source.setSelectedIndices(new int[]{which});
						dialog.dismiss();
						return true; // allow selection
					}
				};
				DialogUtils.createSingleChoiceDialog(
					DeleteComputerActivity.this,
					R.string.label_choose_computer_to_delete,
					R.drawable.menu_ic_computer,
					computers,
					selectedIndex,
					callback
				).show();
			}
		});
	}

	private void showConfirmDialog() {
		ComputerObject[] selectedObjects = pkComputerToDelete.getSelectedItems().toArray(new ComputerObject[0]);
		final int computerId = selectedObjects[0].getComputerId();
		final String computerName = selectedObjects[0].getComputerName();

		String message = String.format(getResources().getString(R.string.message_confirm_delete_remote_computer), computerName);
		MaterialDialog.SingleButtonCallback buttonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				beforeDeleteComputerFromDevice(computerId, computerName);
                dialog.dismiss();
			}
		};

		DialogUtils.createButtonsDialog31(
			this,
			-1,
			-1,
			message,
			R.string.btn_label_yes_delete,
			R.string.btn_label_cancel,
			buttonCallback
		).show();
	}

	private void beforeDeleteComputerFromDevice(final int computerId, final String computerName) {
		AccountUtils.AuthTokenCallback callback = new AccountUtils.AuthTokenCallback() {
			@Override
			public void onError(String errorMessage) {
				btnDelete.setProgress(-1);
				MsgUtils.showWarningMessage(DeleteComputerActivity.this, errorMessage);
				btnDelete.setProgress(0);
				setItemsEnable(true);
			}
			@Override
			public void onSuccess(String authToken) {
				doDeleteComputerFromDevice(authToken, computerId, computerName);
			}
		};
		AccountUtils.getAuthToken(DeleteComputerActivity.this, callback);
	}

	private void doDeleteComputerFromDevice(String authToken, final int computerId, final String computerName) {
		final Account activeAccount = AccountUtils.getActiveAccount();
		final AccountManager accountManager = AccountManager.get(DeleteComputerActivity.this);
		String tmpComputerId = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_ID);
		final int currentComputerId = TextUtils.isEmpty(tmpComputerId) ? -1 : Integer.valueOf(tmpComputerId);
		String userId = accountManager.getUserData(activeAccount, Constants.EXT_PARAM_FILELUG_ACCOUNT);
		String lugServerId = accountManager.getUserData(activeAccount, Constants.PARAM_LUG_SERVER_ID);

		String verification = RepositoryUtility.generateVerificationToDeleteComputer(userId, computerId);
		String locale = getResources().getConfiguration().locale.toString();

		RepositoryClient.getInstance().deleteComputerFromDevice(
			authToken,
			lugServerId,
			computerId,
			verification,
			locale,
			new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					btnDelete.setProgress(100);

					String dirName = FileCache.getAccountComputerCacheDirName(activeAccount, computerId);
					if ( dirName != null ) {
						FileCache.deleteFilesInDir(dirName, true);
					}

					if ( currentComputerId == computerId ) {
						Bundle userData = new Bundle();
						userData.putString(Constants.PARAM_COMPUTER_ID, null);
						userData.putString(Constants.PARAM_COMPUTER_NAME, null);
						userData.putString(Constants.PARAM_COMPUTER_GROUP, null);
						userData.putString(Constants.PARAM_COMPUTER_ADMIN_ID, null);
						userData.putString(Constants.PARAM_USER_COMPUTER_ID, null);
						userData.putString(Constants.PARAM_LUG_SERVER_ID, null);
						userData.putString(Constants.PARAM_SOCKET_CONNECTED, Boolean.FALSE.toString());
						AccountUtils.resetUserData2(activeAccount, userData);
					}

					Intent intent = new Intent();
					intent.putExtra(Constants.PARAM_COMPUTER_ID, computerId);
					intent.putExtra(Constants.PARAM_COMPUTER_NAME, computerName);
					setResult(RESULT_OK, intent);
					finish();
				}
			},
			new BaseResponseError(true, DeleteComputerActivity.this, BaseResponseError.MESSAGE_TYPE_WARNING_MESSAGE) {
				@Override
				protected void beforeShowErrorMessage(VolleyError volleyError) {
					btnDelete.setProgress(-1);
				}
				@Override
				protected void afterShowErrorMessage(VolleyError volleyError) {
					btnDelete.setProgress(0);
					setItemsEnable(true);
				}
			}
		);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

}
