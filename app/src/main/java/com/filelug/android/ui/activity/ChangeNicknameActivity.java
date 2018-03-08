package com.filelug.android.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class ChangeNicknameActivity extends BaseConfigureActivity {

	private static final String TAG = ChangeNicknameActivity.class.getSimpleName();

	private Toolbar mToolbar;
	private FloatingLabelEditText etCurrentNickname = null;
	private FloatingLabelEditText etNewNickname = null;
	private ActionProcessButton btnChange = null;

	private String currentNickname = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_change_nickname);

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(Constants.PARAM_NICKNAME)) {
			currentNickname = extras.getString(Constants.PARAM_NICKNAME);
		}

		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		mToolbar.setTitle(R.string.page_change_nickname_header);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		etCurrentNickname = (FloatingLabelEditText) findViewById(R.id.etCurrentNickname);
		etNewNickname = (FloatingLabelEditText) findViewById(R.id.etNewNickname);

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
					getAuthToken();
				}
			}
		} );

		etCurrentNickname.setInputWidgetText(currentNickname);
		etCurrentNickname.getInputWidget().setEnabled(false);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		etNewNickname.requestFocus();
	}

	private boolean checkValidation() {
		View view = getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}

		boolean ret = true;

		if ( !Validation.hasText(etNewNickname.getInputWidget()) ) {
			etNewNickname.requestFocus();
			ret = false;
		}

		if ( !NetworkUtils.isNetworkAvailable(ChangeNicknameActivity.this) ) {
			return false;
		}

		return ret;
	}

	private void setItemsEnable(boolean enable) {
		btnChange.setEnabled(enable);
		etNewNickname.getInputWidget().setEnabled(enable);
	}

	private void getAuthToken() {
		AccountUtils.AuthTokenCallback callback = new AccountUtils.AuthTokenCallback() {
			@Override
			public void onError(String errorMessage) {
				btnChange.setProgress(-1);
				MsgUtils.showWarningMessage(ChangeNicknameActivity.this, errorMessage);
				btnChange.setProgress(0);
				setItemsEnable(true);
			}
			@Override
			public void onSuccess(String authToken) {
				doChangeNickname(authToken);
			}
		};
		AccountUtils.getAuthToken(ChangeNicknameActivity.this, callback);
	}

	private void doChangeNickname(String authToken) {
		final String newNickname = etNewNickname.getInputWidgetText().toString();
		String locale = getResources().getConfiguration().locale.toString();

		RepositoryClient.getInstance().changeNickname(
			authToken,
			newNickname,
			locale,
			new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					btnChange.setProgress(100);
					Account activeAccount = AccountUtils.getActiveAccount();
					AccountManager accountManager = AccountManager.get(ChangeNicknameActivity.this);
					accountManager.setUserData(activeAccount, Constants.PARAM_NICKNAME, newNickname);
					Intent intent = new Intent();
					intent.putExtra(Constants.PARAM_NEW_NICKNAME, newNickname);
					setResult(RESULT_OK, intent);
					finish();
				}
			},
			new BaseResponseError(true, ChangeNicknameActivity.this, BaseResponseError.MESSAGE_TYPE_WARNING_MESSAGE) {
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
