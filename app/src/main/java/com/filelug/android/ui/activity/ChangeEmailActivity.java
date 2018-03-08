package com.filelug.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dd.processbutton.iml.ActionProcessButton;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.BaseResponseError;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.ui.widget.RobotoTextView;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.NetworkUtils;
import com.filelug.android.util.Validation;
import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;

public class ChangeEmailActivity extends BaseConfigureActivity {

	private static final String TAG = ChangeEmailActivity.class.getSimpleName();

	private Toolbar mToolbar;
	private ImageView ivPageImage;
	private RobotoTextView tvPageDescription = null;
	private FloatingLabelEditText etCurrentEmail = null;
	private FloatingLabelEditText etNewEmail = null;
	private ActionProcessButton btnSendMeSecurityCode = null;

	private String mCurrentEmail = null;
	private boolean mCurrentEmailIsVerified = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_change_email);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mCurrentEmail = extras.getString(Constants.PARAM_EMAIL, null);
			mCurrentEmailIsVerified = extras.getBoolean(Constants.PARAM_EMAIL_IS_VERIFIED, false);
		}

		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		mToolbar.setTitle(mCurrentEmailIsVerified ? R.string.page_change_email_header : R.string.page_verify_email);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		ivPageImage = (ImageView) findViewById(R.id.ivPageImage);
		ivPageImage.setImageResource(mCurrentEmailIsVerified ? R.drawable.header_ic_current_email : R.drawable.header_ic_verify_email_security_code);
		tvPageDescription = (RobotoTextView) findViewById(R.id.tvPageDescription);
		tvPageDescription.setText(mCurrentEmailIsVerified ? R.string.page_change_email_message_1 : R.string.page_verify_email_message_1);
		etCurrentEmail = (FloatingLabelEditText) findViewById(R.id.etCurrentEmail);
		etNewEmail = (FloatingLabelEditText) findViewById(R.id.etNewEmail);

		Resources res = getResources();

		btnSendMeSecurityCode = (ActionProcessButton) findViewById(R.id.btnSendMeSecurityCode);
		btnSendMeSecurityCode.setColorScheme(res.getColor(R.color.main_color_A100), res.getColor(R.color.main_color_500), res.getColor(R.color.white), res.getColor(R.color.material_red_500));
		btnSendMeSecurityCode.setMode(ActionProcessButton.Mode.ENDLESS);
		btnSendMeSecurityCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ( checkValidation() ) {
					setItemsEnable(false);
					btnSendMeSecurityCode.setProgress(1);
					sendMeSecurityCodeAction();
				}
			}
		});

		etCurrentEmail.setInputWidgetText(!TextUtils.isEmpty(mCurrentEmail) ? mCurrentEmail : res.getString(R.string.message_not_set));
		etCurrentEmail.getInputWidget().setEnabled(false);
		if ( mCurrentEmailIsVerified ) {
			// 已驗證
			etCurrentEmail.setInputWidgetText(mCurrentEmail);
			etCurrentEmail.getInputWidget().setEnabled(false);
		} else {
			// 未驗證
			etCurrentEmail.setVisibility(View.GONE);
			if ( !TextUtils.isEmpty(mCurrentEmail) ) {
				etNewEmail.setLabelText(R.string.label_current_email_address);
				etNewEmail.setInputWidgetText(mCurrentEmail);
			}
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		etNewEmail.requestFocus();
	}

	private boolean checkValidation() {
		View view = getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}

		boolean ret = true;

		if ( !Validation.hasText(etNewEmail.getInputWidget()) || !Validation.isEmailAddress(etNewEmail.getInputWidget(), false) ) {
			etNewEmail.requestFocus();
			ret = false;
		}

		if ( !NetworkUtils.isNetworkAvailable(ChangeEmailActivity.this) ) {
			return false;
		}

		return ret;
	}

	private void setItemsEnable(boolean enable) {
		etNewEmail.getInputWidget().setEnabled(enable);
		btnSendMeSecurityCode.setEnabled(enable);
	}

	private void sendMeSecurityCodeAction() {
		AccountUtils.AuthTokenCallback callback = new AccountUtils.AuthTokenCallback() {
			@Override
			public void onError(String errorMessage) {
				btnSendMeSecurityCode.setProgress(-1);
				MaterialDialog.SingleButtonCallback buttonCallback = new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						btnSendMeSecurityCode.setProgress(0);
						setItemsEnable(true);
					}
				};
				MsgUtils.showWarningMessage(ChangeEmailActivity.this, errorMessage, buttonCallback);
			}
			@Override
			public void onSuccess(String authToken) {
				doSendChangeUserEmailSecurityCode(authToken);
			}
		};
		AccountUtils.getAuthToken(ChangeEmailActivity.this, callback);
	}

	private void doSendChangeUserEmailSecurityCode(String authToken) {
		final String newEmail = etNewEmail.getInputWidgetText().toString();
		String locale = getResources().getConfiguration().locale.toString();

		RepositoryClient.getInstance().sendChangeUserEmailSecurityCode(
			authToken,
			newEmail,
			locale,
			new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					btnSendMeSecurityCode.setProgress(100);
					String message = String.format(getResources().getString(R.string.message_successfully_sent_security_code_to), newEmail);
					MsgUtils.showToast(ChangeEmailActivity.this, message);

					Intent intent = new Intent(ChangeEmailActivity.this, VerifyEmailActivity.class);
					intent.putExtra(Constants.PARAM_NEW_EMAIL, newEmail);
					startActivityForResult(intent, Constants.REQUEST_VERIFY_EMAIL_SECURITY_CODE);
				}
			},
			new BaseResponseError(true, ChangeEmailActivity.this, BaseResponseError.MESSAGE_TYPE_WARNING_MESSAGE) {
				@Override
				protected void beforeShowErrorMessage(VolleyError volleyError) {
					btnSendMeSecurityCode.setProgress(-1);
				}
				@Override
				protected void afterShowErrorMessage(VolleyError volleyError) {
					btnSendMeSecurityCode.setProgress(0);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ( requestCode == Constants.REQUEST_VERIFY_EMAIL_SECURITY_CODE  ) {
			if ( resultCode == RESULT_OK ) {
				Bundle extras = data.getExtras();
				if ( extras != null ) {
					String newEmail = extras.getString(Constants.PARAM_NEW_EMAIL);
					Intent intent = new Intent();
					intent.putExtra(Constants.PARAM_NEW_EMAIL, newEmail);
					intent.putExtra(Constants.EXT_PARAM_ORIGIN_EMAIL, mCurrentEmail);
					intent.putExtra(Constants.EXT_PARAM_ORIGIN_EMAIL_VERIFY_STATUS, mCurrentEmailIsVerified);
					setResult(RESULT_OK, intent);
					finish();
				}
			} else {
				setResult(RESULT_CANCELED, null);
				finish();
			}
		}
	}

}
