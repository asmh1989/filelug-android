package com.filelug.android.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dd.processbutton.iml.ActionProcessButton;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.BaseResponseError;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.crepo.RepositoryUtility;
import com.filelug.android.ui.widget.RobotoTextView;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.NetworkUtils;
import com.filelug.android.util.PrefUtils;
import com.filelug.android.util.Validation;
import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;

import org.json.JSONObject;

public class LoginActivity extends AccountAuthenticatorActionBarActivity {

	private static final String TAG = LoginActivity.class.getSimpleName();

	private Toolbar mToolbar;
	private RobotoTextView tvDescription = null;
	private FloatingLabelEditText etPhoneNumber = null;
	private FloatingLabelEditText etNickname = null;
	private FloatingLabelEditText etEmail = null;
	private ActionProcessButton btnSend = null;

	private String mOriginCountryId = null;
	private String mOriginCountryCode = null;
	private String mOriginPhone = null;
	private String mOriginPhoneWithCountry = null;
	private String mOriginAndroidAccountName = null;
	private String mFilelugAccount = null;
	private String mCountryId = null;
	private String mCountryCode = null;
	private String mPhone = null;
	private String mPhoneWithCountry = null;
	private String mAndroidAccountName = null;
	private String mAuthorizationCode = null;
	private String mSessionId = null;
	private boolean mNeedCreateOrUpdateUserProfile = false;
	private String mNickname = null;
	private String mEmail = null;
	private boolean mEmailIsVerified = false;
	private boolean mPhoneIsVerified = false;

	private AccountManager mAccountManager = null;
	private String mAccountType = null;
	private String mAuthTokenType = null;
	private String[] requiredFeatures = null;
	private Bundle options = null;
	private String mAuthActionType = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mAccountType = extras.getString(AccountManager.KEY_ACCOUNT_TYPE);
			mAuthTokenType = extras.getString(Constants.AUTH_TOKEN_TYPE);
			if (mAuthTokenType == null)
				mAuthTokenType = Constants.AUTH_TOKEN_TYPE_GENERAL_SERVICE;
			requiredFeatures = extras.getStringArray(Constants.AUTH_REQUIRED_FEATURES);
			options = extras.getBundle(Constants.AUTH_OPTIONS);
			mAuthActionType = extras.getString(Constants.AUTH_ACTION_TYPE);
			if ( Constants.AUTH_ACTION_LOGIN_WHEN_403.equals(mAuthActionType) ||
				 Constants.AUTH_ACTION_VERIFY_PHONE_NUMBER.equals(mAuthActionType) ) {
				mOriginCountryId = options.getString(Constants.PARAM_COUNTRY_ID);
				mOriginCountryCode = options.getString(Constants.PARAM_COUNTRY_CODE);
				mOriginPhone = options.getString(Constants.PARAM_PHONE);
				mOriginPhoneWithCountry = options.getString(Constants.PARAM_PHONE_WITH_COUNTRY);
				mOriginAndroidAccountName = String.format(Constants.USER_ACCOUNT_PATTERN, mOriginCountryCode, mOriginPhone);
			}
		}

		mAccountManager = AccountManager.get(this);

		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		mToolbar.setTitle(R.string.page_login_filelug_header);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		tvDescription = (RobotoTextView) findViewById(R.id.tvDescription);

		etPhoneNumber = (FloatingLabelEditText) findViewById(R.id.etPhoneNumber);
		etPhoneNumber.getInputWidget().setEnabled(false);
		etNickname = (FloatingLabelEditText) findViewById(R.id.etNickname);
		etNickname.getInputWidget().setEnabled(false);
		etEmail = (FloatingLabelEditText) findViewById(R.id.etEmail);
		etEmail.getInputWidget().setEnabled(false);

		Resources res = getResources();

		btnSend = (ActionProcessButton) findViewById(R.id.btnSend);
		btnSend.setColorScheme(res.getColor(R.color.main_color_A100), res.getColor(R.color.main_color_500), res.getColor(R.color.white), res.getColor(R.color.material_red_500));
		btnSend.setMode(ActionProcessButton.Mode.ENDLESS);
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendAction();
			}
		});

		if ( Constants.AUTH_ACTION_LOGIN_WHEN_403.equals(mAuthActionType) ||
			 Constants.AUTH_ACTION_VERIFY_PHONE_NUMBER.equals(mAuthActionType) ) {
			mToolbar.setTitle(R.string.page_verify_phone_number_header);
			tvDescription.setText(R.string.page_login_filelug_message);
			btnSend.setText(R.string.btn_label_verify);
			etPhoneNumber.setInputWidgetText(mOriginAndroidAccountName);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if ( !NetworkUtils.isNetworkAvailable(LoginActivity.this) ) {
			setResult(RESULT_CANCELED, null);
			finish();
			return;
		}
		validPhoneNumber();
	}

	private void validPhoneNumber() {
		AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
			new AccountKitConfiguration.AccountKitConfigurationBuilder(
				LoginType.PHONE,
				AccountKitActivity.ResponseType.CODE
			);
		if ( Constants.AUTH_ACTION_VERIFY_PHONE_NUMBER.equals(mAuthActionType) ) {
			configurationBuilder.setTitleType(AccountKitActivity.TitleType.APP_NAME);
		}
		if ( !TextUtils.isEmpty(mOriginCountryId) && !TextUtils.isEmpty(mOriginPhone) ) {
			configurationBuilder.setInitialPhoneNumber(new PhoneNumber(mOriginCountryId, mOriginPhone, mOriginCountryId));
		}
		Intent intent = new Intent(LoginActivity.this, AccountKitActivity.class);
		intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
		startActivityForResult(intent, Constants.REQUEST_ACCOUNT_KIT_LOGIN);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ( requestCode == Constants.REQUEST_ACCOUNT_KIT_LOGIN ) {
			AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
			AccountKitError accountKitError = loginResult.getError();
			boolean cancelled = loginResult.wasCancelled();
			mAuthorizationCode = loginResult.getAuthorizationCode();
			String message = null;
			if ( accountKitError != null ) {
				message = accountKitError.getErrorType().getMessage();
				MsgUtils.showToast(LoginActivity.this, message);
			} else if ( cancelled ) {
				// Do nothing, go back.
			} else {
				doExchangeAccessTokenWithAuthorizationCode();
				return;
			}

			setResult(RESULT_CANCELED, null);
			finish();
		}
	}

	private void doExchangeAccessTokenWithAuthorizationCode() {
		if ( !NetworkUtils.isNetworkAvailable(LoginActivity.this) ) {
			return;
		}

		setItemsEnable(false, false);

		String locale = getResources().getConfiguration().locale.toString();
		String verification = RepositoryUtility.generateVerificationForExchangeAccessToken(mAuthorizationCode, locale);

		RepositoryClient.getInstance().exchangeAccessTokenWithAuthorizationCode(
			mAuthorizationCode,
			verification,
			locale,
			new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					String countryId = response.optString(Constants.PARAM_COUNTRY_ID);
					String countryCode = response.optString(Constants.PARAM_COUNTRY_CODE);
					String phone = response.optString(Constants.PARAM_PHONE);
					String phoneWithCountry = response.optString(Constants.PARAM_PHONE_WITH_COUNTRY);
					String returnVerification = response.optString(Constants.PARAM_VERIFICATION);

					String securityCode = RepositoryUtility.generateVerificationForAccessTokenSecurityCode(countryId, phone);
					if ( !securityCode.equals(returnVerification) ) {
						showErrorMessageAndFinish(getResources().getString(R.string.message_verification_error));
						return;
					}

					String androidAccName = String.format(Constants.USER_ACCOUNT_PATTERN, countryCode, phone);
					String errMsg = null;

					if ( Constants.AUTH_ACTION_VERIFY_PHONE_NUMBER.equals(mAuthActionType) ) {
						if ( androidAccName.equals(mOriginAndroidAccountName) ) {
							Bundle result = new Bundle();
							result.putString(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE_FILELUG);
							result.putString(AccountManager.KEY_ACCOUNT_NAME, androidAccName);
							setAccountAuthenticatorResult(result);
							Intent intent = new Intent();
							intent.putExtra(Constants.AUTH_ACTION_TYPE, mAuthActionType);
							intent.putExtras(result);
							setResult(RESULT_OK, intent);
							finish();
						} else {
							String msg = String.format(getResources().getString(R.string.message_describe_wrong_login_account), mOriginAndroidAccountName, androidAccName);
							showErrorMessageAndFinish(msg);
						}
						return;
					}

					mPhoneIsVerified = true;
					mCountryId = countryId;
					mCountryCode = countryCode;
					mPhone = phone;
					mPhoneWithCountry = phoneWithCountry;
					mAndroidAccountName = androidAccName;

					etPhoneNumber.setInputWidgetText(mAndroidAccountName);
					doLoginWithAuthorizationCode();
				}
			},
			new BaseResponseError(true, LoginActivity.this, BaseResponseError.MESSAGE_TYPE_WARNING_MESSAGE) {
				@Override
				protected void beforeShowErrorMessage(VolleyError volleyError) {
					btnSend.setProgress(-1);
				}
				@Override
				protected void afterShowErrorMessage(VolleyError volleyError) {
					if ( Constants.AUTH_ACTION_VERIFY_PHONE_NUMBER.equals(mAuthActionType) ) {
						setResult(RESULT_CANCELED, null);
						finish();
					} else {
						setItemsEnable(true, false);
						btnSend.setProgress(0);
					}
				}
			}
		);
	}

	private void showErrorMessageAndFinish(String errorMessage) {
		MsgUtils.showWarningMessage(
			LoginActivity.this,
			errorMessage,
			new MaterialDialog.SingleButtonCallback() {
				@Override
				public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
					setResult(RESULT_CANCELED, null);
					finish();
				}
			}
		);
	}

	private void doLoginWithAuthorizationCode() {
		if ( !NetworkUtils.isNetworkAvailable(LoginActivity.this) ) {
			return;
		}

		setItemsEnable(false, false);

		String encodedAuthorizationCode = RepositoryUtility.encrypt2Sha256(mAuthorizationCode);
		String pushServiceTypeStr = PrefUtils.getPushServiceTypeString();
		String deviceToken = PrefUtils.getPushServiceToken();
		String deviceVersion = String.valueOf(Build.VERSION.SDK_INT);
		String filelugVersion = MiscUtils.getFilelugVersion(LoginActivity.this);
		String locale = getResources().getConfiguration().locale.toString();

		RepositoryClient.getInstance().loginWithAuthorizationCode(
			encodedAuthorizationCode,
			pushServiceTypeStr,
			deviceToken,
			deviceVersion,
			filelugVersion,
			filelugVersion,
			locale,
			new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					if ( PrefUtils.getGCMTokenChanged() && !TextUtils.isEmpty(PrefUtils.getPushServiceToken()) ) {
						PrefUtils.setGCMTokenChanged(false);
					}

					mFilelugAccount = response.optString(Constants.PARAM_ACCOUNT);
					mSessionId = response.optString(Constants.PARAM_SESSION_ID);
					mNeedCreateOrUpdateUserProfile = response.optBoolean(Constants.PARAM_NEED_CREATE_OR_UPDATE_USER_PROFILE);
					mNickname = response.optString(Constants.PARAM_NICKNAME, null);
					mEmail = response.optString(Constants.PARAM_EMAIL, null);
					mEmailIsVerified = response.optBoolean(Constants.PARAM_EMAIL_IS_VERIFIED);

					if ( !mNeedCreateOrUpdateUserProfile ) {
						etNickname.setInputWidgetText(mNickname);
						etEmail.setInputWidgetText(mEmail);
						addOrUpdateAccount();
						return;
					}

					setItemsEnable(true, true);
					etNickname.requestFocus();
				}
			},
			new BaseResponseError(true, LoginActivity.this, BaseResponseError.MESSAGE_TYPE_WARNING_MESSAGE) {
				@Override
				protected void beforeShowErrorMessage(VolleyError volleyError) {
					btnSend.setProgress(-1);
				}
				@Override
				protected void afterShowErrorMessage(VolleyError volleyError) {
					setItemsEnable(true, false);
					btnSend.setProgress(0);
				}
			}
		);
	}

	private void addOrUpdateAccount() {
		long accessTime = System.currentTimeMillis();
		Bundle userData = new Bundle();
		userData.putString(Constants.EXT_PARAM_FILELUG_ACCOUNT, mFilelugAccount);
		userData.putString(Constants.PARAM_COUNTRY_ID, mCountryId);
		userData.putString(Constants.PARAM_COUNTRY_CODE, mCountryCode);
		userData.putString(Constants.PARAM_PHONE, mPhone);
		userData.putString(Constants.PARAM_PHONE_WITH_COUNTRY, mPhoneWithCountry);
		userData.putString(Constants.PARAM_SESSION_ID, mSessionId);
		userData.putString(Constants.PARAM_NEED_CREATE_OR_UPDATE_USER_PROFILE, Boolean.toString(mNeedCreateOrUpdateUserProfile));
		userData.putString(Constants.PARAM_NICKNAME, mNickname);
		userData.putString(Constants.PARAM_EMAIL, mEmail);
		userData.putString(Constants.PARAM_EMAIL_IS_VERIFIED, Boolean.toString(mEmailIsVerified));
		userData.putString(Constants.EXT_PARAM_LOGGED_IN, Boolean.TRUE.toString());
		userData.putString(Constants.EXT_PARAM_ACCESS_TIME, Long.toString(accessTime));

		Account account = AccountUtils.getAccount(mAndroidAccountName);

		if ( account == null ) {
			account = new Account(mAndroidAccountName, Constants.ACCOUNT_TYPE_FILELUG);
			AccountUtils.addAccount(account, mAuthTokenType, mSessionId, userData);
		} else {
			AccountUtils.resetToken(account, mAuthTokenType, mSessionId);
			AccountUtils.resetUserData2(account, userData);
		}

		Bundle result = new Bundle();
		result.putString(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE_FILELUG);
		result.putString(AccountManager.KEY_ACCOUNT_NAME, mAndroidAccountName);
		result.putString(AccountManager.KEY_AUTHTOKEN, mSessionId);
		result.putBundle(AccountManager.KEY_USERDATA, userData);
		setAccountAuthenticatorResult(result);
		Intent intent = new Intent();
		intent.putExtra(Constants.AUTH_ACTION_TYPE, mAuthActionType);
		intent.putExtras(result);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void sendAction() {
		if ( TextUtils.isEmpty(mSessionId) ) {
			doLoginWithAuthorizationCode();
			return;
		}

		if ( !checkValidation() ) {
			return;
		}

		mNickname = etNickname.getInputWidgetText().toString();
		mEmail = etEmail.getInputWidgetText().toString();
		setItemsEnable(false, false);
		btnSend.setProgress(1);
		doCreateOrUpdateUserProfile();
	}

	private boolean checkValidation() {
		View view = getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}

		boolean ret = true;

		if ( !NetworkUtils.isNetworkAvailable(LoginActivity.this) ) {
			return false;
		}

		if ( !Validation.hasText(etNickname.getInputWidget()) ) {
			etNickname.requestFocus();
			ret = false;
		}
		if ( !Validation.isEmailAddress(etEmail.getInputWidget(), true) ) {
			etEmail.requestFocus();
			ret = false;
		}
		return ret;
	}

	private void doCreateOrUpdateUserProfile() {
		if ( !NetworkUtils.isNetworkAvailable(LoginActivity.this) ) {
			return;
		}

		String locale = getResources().getConfiguration().locale.toString();
		RepositoryClient.getInstance().createOrUpdateUserProfile(
			mSessionId,
			mEmail,
			mNickname,
			locale,
			new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					mNeedCreateOrUpdateUserProfile = false;
					addOrUpdateAccount();
				}
			},
			new BaseResponseError(true, LoginActivity.this) {
				@Override
				protected void beforeShowErrorMessage(VolleyError volleyError) {
					btnSend.setProgress(-1);
				}
				@Override
				protected void afterShowErrorMessage(VolleyError volleyError) {
					btnSend.setProgress(0);
					setItemsEnable(true, true);
				}
			}
		);
	}

	private void setItemsEnable(boolean buttonEnabled, boolean inputEnabled) {
		btnSend.setEnabled(buttonEnabled);
		etNickname.getInputWidget().setEnabled(inputEnabled);
		etEmail.getInputWidget().setEnabled(inputEnabled);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
