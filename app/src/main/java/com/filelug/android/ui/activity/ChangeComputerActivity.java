package com.filelug.android.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dd.processbutton.iml.ActionProcessButton;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.BaseResponseError;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.ui.model.ComputerObject;
import com.filelug.android.ui.widget.RobotoTextView;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.DialogUtils;
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

public class ChangeComputerActivity extends BaseConfigureActivity {

	private static final String TAG = ChangeComputerActivity.class.getSimpleName();

	private Toolbar mToolbar;
	private ImageView ivPageImage;
	private RobotoTextView tvPageDescription = null;
	private FloatingLabelEditText etConnectedComputer = null;
	private FloatingLabelItemPicker<ComputerObject> pkNewComputer = null;
	private ActionProcessButton btnChange = null;
	private TextView btnAddNewComputer = null;

	private int mCurrentComputerId = -1;
	private String mCurrentComputerName = null;
	private String mCurrentComputerNameStr = null;
	private boolean mCurrentSocketConnected = false;

	private ArrayList<ComputerObject> mComputerList = null;
	private ComputerObject[] mComputers = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_change_computer);

		Resources res = getResources();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mCurrentComputerId = extras.getInt(Constants.PARAM_COMPUTER_ID);
			mCurrentComputerName = extras.getString(Constants.PARAM_COMPUTER_NAME);
			mCurrentSocketConnected = extras.getBoolean(Constants.PARAM_SOCKET_CONNECTED);
			if ( mCurrentSocketConnected && mCurrentComputerId != -1 ) {
				mCurrentComputerNameStr = mCurrentComputerName;
			} else {
				mCurrentComputerNameStr = res.getString(R.string.message_computer_not_connected);
			}
		}

		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		mToolbar.setTitle(mCurrentSocketConnected ? R.string.page_change_computer_header : R.string.page_connect_to_computer_header);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		ivPageImage = (ImageView) findViewById(R.id.ivPageImage);
		ivPageImage.setImageResource(mCurrentSocketConnected ? R.drawable.header_ic_current_computer : R.drawable.header_ic_connect_to_computer);
		tvPageDescription = (RobotoTextView) findViewById(R.id.tvPageDescription);
		tvPageDescription.setText(mCurrentSocketConnected ? R.string.page_change_computer_message_1 : R.string.page_connect_to_computer_message_1);
		etConnectedComputer = (FloatingLabelEditText) findViewById(R.id.etConnectedComputer);
		pkNewComputer = (FloatingLabelItemPicker<ComputerObject>) findViewById(R.id.ipNewComputer);

		btnChange = (ActionProcessButton) findViewById(R.id.btnChange);
		btnChange.setColorScheme(res.getColor(R.color.main_color_A100), res.getColor(R.color.main_color_500), res.getColor(R.color.white), res.getColor(R.color.material_red_500));
		btnChange.setMode(ActionProcessButton.Mode.ENDLESS);
		btnChange.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkValidation()) {
					ComputerObject[] selectedObjects = pkNewComputer.getSelectedItems().toArray(new ComputerObject[0]);
					int computerId = selectedObjects[0].getComputerId();
					String computerName = selectedObjects[0].getComputerName();
					sendResult(computerId, computerName);
				}
			}
		});
		btnChange.setText(mCurrentSocketConnected ? R.string.btn_label_change : R.string.btn_label_connect);

		btnAddNewComputer = (TextView) findViewById(R.id.btnAddNewComputer);
		btnAddNewComputer.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v instanceof TextView) {
					Intent intent = new Intent(ChangeComputerActivity.this, AddNewComputerActivity.class);
					startActivityForResult(intent, Constants.REQUEST_ADD_NEW_COMPUTER);
				}
			}
		} );

		etConnectedComputer.setInputWidgetText(mCurrentComputerNameStr);
		etConnectedComputer.getInputWidget().setEnabled(false);
		beforeDoFindAvailableComputers();
	}

	private boolean checkValidation() {
		boolean ret = true;

		if ( !NetworkUtils.isNetworkAvailable(ChangeComputerActivity.this) ) {
			return false;
		}

		if ( !Validation.hasSelect(pkNewComputer) ) {
			String message = String.format(getResources().getString(R.string.message_field_can_not_be_empty_2), getResources().getString(R.string.label_connect_to_computer));
			MsgUtils.showWarningMessage(ChangeComputerActivity.this, message);
			pkNewComputer.requestFocus();
			ret = false;
		}

		return ret;
	}

	private void setItemsEnable(boolean enable) {
		btnChange.setEnabled(enable);
		btnAddNewComputer.setEnabled(enable);
		pkNewComputer.setEnabled(enable);
	}

	private void beforeDoFindAvailableComputers() {
		if ( !NetworkUtils.isNetworkAvailable(ChangeComputerActivity.this) ) {
			return;
		}

		AccountUtils.AuthTokenCallback callback = new AccountUtils.AuthTokenCallback() {
			@Override
			public void onError(String errorMessage) {
				MsgUtils.showToast(ChangeComputerActivity.this, errorMessage);
			}
			@Override
			public void onSuccess(String authToken) {
				doFindAvailableComputers(authToken);
			}
		};
		AccountUtils.getAuthToken(ChangeComputerActivity.this, callback);
	}

	private void doFindAvailableComputers(String authToken) {
		if ( !NetworkUtils.isNetworkAvailable(ChangeComputerActivity.this) ) {
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
			new BaseResponseError(true, ChangeComputerActivity.this, BaseResponseError.MESSAGE_TYPE_ERROR_MESSAGE) {
				@Override
				protected void afterShowErrorMessage(VolleyError volleyError) {
					pkNewComputer.setEnabled(false);
					btnChange.setEnabled(false);
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
					if ( computerId == mCurrentComputerId && !mCurrentSocketConnected ) {
						tmpIndex = i;
					}
					TransferDBHelper.createOrUpdateUserComputer(userId, computerId, userComputerId, computerGroup, computerName, computerAdminId, lugServerId);
				}
			} catch (JSONException e) {
			}
		}

		final int selectedIndex = tmpIndex;
		boolean isEnable = mComputerList.size() > 0;
		pkNewComputer.setEnabled(isEnable);
		btnChange.setEnabled(isEnable);

		if ( mComputerList.size() == 0 ) {
			MsgUtils.showWarningMessage(ChangeComputerActivity.this, R.string.message_can_not_find_available_computers);
			return;
		}

		mComputers = mComputerList.toArray(new ComputerObject[0]);
		pkNewComputer.setAvailableItems(mComputerList);
		if ( selectedIndex > -1 ) {
			pkNewComputer.setSelectedIndices(new int[]{selectedIndex});
		}
		pkNewComputer.setWidgetListener(new FloatingLabelItemPicker.OnWidgetEventListener<ComputerObject>() {
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
					ChangeComputerActivity.this,
					R.string.label_choose_computer_name,
					R.drawable.menu_ic_computer,
					computers,
					selectedIndex,
					callback
				).show();
			}
		});
	}

	private void sendResult(final int computerId, final String computerName) {
		setItemsEnable(false);
		btnChange.setProgress(1);

		Activity activity = ChangeComputerActivity.this;
		String message = String.format(activity.getResources().getString(R.string.message_try_to_connect_to_computer), computerName);
		final MaterialDialog dialog = DialogUtils.createProgressDialog2(activity, message);
		dialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				dialog.dismiss();
				Intent intent = new Intent();
				if ( mCurrentComputerId != -1 ) {
					intent.putExtra(Constants.EXT_PARAM_OLD_COMPUTER_ID, mCurrentComputerId);
					intent.putExtra(Constants.EXT_PARAM_OLD_COMPUTER_NAME, mCurrentComputerName);
				}
				intent.putExtra(Constants.EXT_PARAM_NEW_COMPUTER_ID, computerId);
				intent.putExtra(Constants.EXT_PARAM_NEW_COMPUTER_NAME, computerName);
				setResult(RESULT_OK, intent);
				finish();
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if( requestCode == Constants.REQUEST_ADD_NEW_COMPUTER ) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				if ( extras != null ) {
					int computerId = extras.getInt(Constants.PARAM_COMPUTER_ID);
					String computerName = extras.getString(Constants.PARAM_COMPUTER_NAME);
					sendResult(computerId, computerName);
				}
			}
		}
	}

}
