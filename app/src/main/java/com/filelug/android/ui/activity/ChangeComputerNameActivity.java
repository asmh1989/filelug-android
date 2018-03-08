package com.filelug.android.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dd.processbutton.iml.ActionProcessButton;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.BaseResponseError;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.NetworkUtils;
import com.filelug.android.util.Validation;
import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;

import org.json.JSONObject;

public class ChangeComputerNameActivity extends BaseConfigureActivity {

	private static final String TAG = ChangeComputerNameActivity.class.getSimpleName();

	private Toolbar mToolbar;
	private FloatingLabelEditText etCurrentComputerName = null;
	private FloatingLabelEditText etNewComputerName = null;
	private ActionProcessButton btnChange = null;

	private int currentComputerId = -1;
	private String currentComputerName = null;
	private String currentComputerGroup = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_change_computer_name);

		Bundle extras = getIntent().getExtras();
		currentComputerId = extras.getInt(Constants.PARAM_COMPUTER_ID);
		currentComputerName = extras.getString(Constants.PARAM_COMPUTER_NAME);
		currentComputerGroup = extras.getString(Constants.PARAM_COMPUTER_GROUP);

		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		mToolbar.setTitle(R.string.page_change_computer_name_header);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		etCurrentComputerName = (FloatingLabelEditText) findViewById(R.id.etCurrentComputerName);
		etNewComputerName = (FloatingLabelEditText) findViewById(R.id.etNewComputerName);

		Resources res = getResources();

		btnChange = (ActionProcessButton) findViewById(R.id.btnChange);
		btnChange.setColorScheme(res.getColor(R.color.main_color_A100), res.getColor(R.color.main_color_500), res.getColor(R.color.white), res.getColor(R.color.material_red_500));
		btnChange.setMode(ActionProcessButton.Mode.ENDLESS);
		btnChange.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ( checkValidation() ) {
					setItemsEnable(false);
					btnChange.setProgress(1);
					beforeChangeComputerName();
				}
			}
		} );

		etCurrentComputerName.setInputWidgetText(currentComputerName);
		etCurrentComputerName.getInputWidget().setEnabled(false);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		etNewComputerName.requestFocus();
	}

	private boolean checkValidation() {
		View view = getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}

		boolean ret = true;

		if ( !Validation.hasText(etNewComputerName.getInputWidget()) ) {
			etNewComputerName.requestFocus();
			ret = false;
		}

		if ( !NetworkUtils.isNetworkAvailable(ChangeComputerNameActivity.this) ) {
			return false;
		}

		return ret;
	}

	private void setItemsEnable(boolean enable) {
		btnChange.setEnabled(enable);
		etNewComputerName.getInputWidget().setEnabled(enable);
	}

	private void beforeChangeComputerName() {
		AccountUtils.AuthTokenCallback callback = new AccountUtils.AuthTokenCallback() {
			@Override
			public void onError(String errorMessage) {
				btnChange.setProgress(-1);
				MsgUtils.showWarningMessage(ChangeComputerNameActivity.this, errorMessage);
				btnChange.setProgress(0);
				setItemsEnable(true);
			}
			@Override
			public void onSuccess(String authToken) {
				doChangeComputerName(authToken);
			}
		};
		AccountUtils.getAuthToken(ChangeComputerNameActivity.this, callback);
	}

	private void doChangeComputerName(String authToken) {
		final Account activeAccount = AccountUtils.getActiveAccount();
		final AccountManager accountManager = AccountManager.get(ChangeComputerNameActivity.this);
		String lugServerId = accountManager.getUserData(activeAccount, Constants.PARAM_LUG_SERVER_ID);

		final String newComputerName = etNewComputerName.getInputWidgetText().toString();
		String locale = getResources().getConfiguration().locale.toString();

		RepositoryClient.getInstance().changeComputerName(
			authToken,
			lugServerId,
			currentComputerId,
			newComputerName,
			currentComputerGroup,
			locale,
			new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					btnChange.setProgress(100);
					int computerId = response.optInt(Constants.PARAM_COMPUTER_ID, -1);
					String computerName = response.optString(Constants.PARAM_COMPUTER_NAME, null);
					String computerGroup = response.optString(Constants.PARAM_COMPUTER_GROUP, null);

					Bundle userData = new Bundle();
					userData.putString(Constants.PARAM_COMPUTER_NAME, computerName);
					if ( TextUtils.isEmpty(computerGroup) ) {
						userData.putString(Constants.PARAM_COMPUTER_GROUP, null);
					} else {
						userData.putString(Constants.PARAM_COMPUTER_GROUP, computerGroup);
					}
					AccountUtils.resetUserData2(activeAccount, userData);

					Intent intent = new Intent();
					intent.putExtra(Constants.PARAM_COMPUTER_ID, computerId);
					intent.putExtra(Constants.PARAM_NEW_COMPUTER_NAME, computerName);
					intent.putExtra(Constants.PARAM_COMPUTER_GROUP, computerGroup);
					setResult(RESULT_OK, intent);
					finish();
				}
			},
			new BaseResponseError(true, ChangeComputerNameActivity.this, BaseResponseError.MESSAGE_TYPE_WARNING_MESSAGE) {
				@Override
				protected void beforeShowErrorMessage(VolleyError volleyError) {
					btnChange.setProgress(-1);
				}
				@Override
				protected void afterShowErrorMessage(VolleyError volleyError) {
					btnChange.setProgress(0);
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
