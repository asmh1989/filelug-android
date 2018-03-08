package com.filelug.android.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.filelug.android.Constants;
import com.filelug.android.MainApplication;
import com.filelug.android.R;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.crepo.RepositoryUtility;
import com.filelug.android.ui.activity.LoginActivity;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Authenticator extends AbstractAccountAuthenticator {

	private static final String TAG = Authenticator.class.getSimpleName();
	private final Context mContext;
	private AccountManager mAccountManager;

	public Authenticator(Context context) {
		super(context);
		mContext = context;
		mAccountManager = AccountManager.get(mContext);
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
//		if ( Constants.DEBUG ) Log.d(TAG, "addAccount()");

		Bundle bundle = new Bundle();
//		int accounts = mAccountManager.getAccountsByType(accountType).length;
//
//		if (accounts > 0) {
//			final String error = mContext.getString(R.string.message_only_one_filelug_account);
//			bundle.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_UNSUPPORTED_OPERATION);
//			bundle.putString(AccountManager.KEY_ERROR_MESSAGE, error);
//			mHandler.post(new Runnable() {
//				@Override
//				public void run() {
//					MsgUtils.showToast(mContext, error);
//				}
//			});
//		} else {
			Intent intent = new Intent(mContext, LoginActivity.class);
			intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
			intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
			intent.putExtra(Constants.AUTH_TOKEN_TYPE, authTokenType);
			intent.putExtra(Constants.AUTH_REQUIRED_FEATURES, requiredFeatures);
			intent.putExtra(Constants.AUTH_OPTIONS, options);
			intent.putExtra(Constants.AUTH_ACTION_TYPE, Constants.AUTH_ACTION_ADD_ACCOUNT);
			bundle.putParcelable(AccountManager.KEY_INTENT, intent);
//		}

		return bundle;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
//		if (Constants.DEBUG) Log.d(TAG, "getAuthToken(): account=" + account.toString() + ", authTokenType=" + authTokenType + ", options=" + MiscUtils.convertBundleToString(options));

		Bundle result = new Bundle();

		// If the caller requested an authToken type we don't support, then
		// return an error
		if ( !authTokenType.equals(Constants.AUTH_TOKEN_TYPE_GENERAL_SERVICE) ) {
			result.putString(AccountManager.KEY_ERROR_MESSAGE, "Invalid authTokenType!");
			return result;
		}

		boolean forceRelogin = false;
		if ( options != null ) {
			forceRelogin = options.getBoolean(Constants.EXT_PARAM_FORCE_RELOGIN);
		}

//		if (Constants.DEBUG) Log.d(TAG, "getAuthToken(), forceRelogin=" + forceRelogin);

		String authToken = this.mAccountManager.peekAuthToken(account, authTokenType);
		String sessionId = this.mAccountManager.getUserData(account, Constants.PARAM_SESSION_ID);
		String filelugAccount = this.mAccountManager.getUserData(account, Constants.EXT_PARAM_FILELUG_ACCOUNT);
		String countryId = this.mAccountManager.getUserData(account, Constants.PARAM_COUNTRY_ID);
		String countryCode = this.mAccountManager.getUserData(account, Constants.PARAM_COUNTRY_CODE);
		String phone = this.mAccountManager.getUserData(account, Constants.PARAM_PHONE);
		String phoneWithCountry = this.mAccountManager.getUserData(account, Constants.PARAM_PHONE_WITH_COUNTRY);
		String verification = RepositoryUtility.generateLoginVerification(filelugAccount, countryId, phone);
		String needCreateOrUpdateUserProfileStr = this.mAccountManager.getUserData(account, Constants.PARAM_NEED_CREATE_OR_UPDATE_USER_PROFILE);
		boolean needCreateOrUpdateUserProfile = TextUtils.isEmpty(needCreateOrUpdateUserProfileStr) ? Boolean.TRUE : Boolean.valueOf(needCreateOrUpdateUserProfileStr);
		String nickname = this.mAccountManager.getUserData(account, Constants.PARAM_NICKNAME);
		String email = this.mAccountManager.getUserData(account, Constants.PARAM_EMAIL);
		String emailIsVerifiedStr = this.mAccountManager.getUserData(account, Constants.PARAM_EMAIL_IS_VERIFIED);
		boolean emailIsVerified = TextUtils.isEmpty(emailIsVerifiedStr) ? Boolean.FALSE : Boolean.valueOf(emailIsVerifiedStr);

		boolean needToVerify = false;
		int errorCode = -1;
		String errorMsg = null;

//		if (Constants.DEBUG) Log.d(TAG, "getAuthToken(), Account UserData: authToken=" + authToken + ", sessionId=" + sessionId + ", filelugAccount=" + filelugAccount + ", countryId=" + countryId + ", countryCode=" + countryCode + ", phone=" + phone + ", phoneWithCountry=" + phoneWithCountry + ", needCreateOrUpdateUserProfile=" + needCreateOrUpdateUserProfile + ", nickname=" + nickname + ", email=" + email + ", emailIsVerified=" + emailIsVerified);

		if ( needCreateOrUpdateUserProfile ) {
			needToVerify = true;
//			if (Constants.DEBUG) Log.d(TAG, "getAuthToken(), needToVerify 1, needCreateOrUpdateUserProfile=" + needCreateOrUpdateUserProfile);
		} else if ( TextUtils.isEmpty(authToken) && !forceRelogin ) {
			needToVerify = true;
//			if (Constants.DEBUG) Log.d(TAG, "getAuthToken(), needToVerify 2, authToken=" + authToken + ", forceRelogin=" + forceRelogin);
		} else {

			String lastAccessTime = this.mAccountManager.getUserData(account, Constants.EXT_PARAM_ACCESS_TIME);
			long currentTime = System.currentTimeMillis();

//			if (Constants.DEBUG) Log.d(TAG, "getAuthToken(), lastAccessTime=" + lastAccessTime + ", currentTime=" + currentTime);

			if ( !forceRelogin && !AccountUtils.authTokenExpired(lastAccessTime, currentTime) ) {
//				if (Constants.DEBUG) Log.d(TAG, "getAuthToken(): Token not expired!");
				// Session沒過期
				result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
				result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
				result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
				return result;
			}

			// Session過期了, 更換authToken
//			if (Constants.DEBUG) Log.d(TAG, "getAuthToken(), re-authenticating with the existing user data");

			String notificationType = PrefUtils.getPushServiceTypeString();
			String deviceToken = PrefUtils.getPushServiceToken();
			String deviceVersion = String.valueOf(Build.VERSION.SDK_INT);
			String filelugVersion = MiscUtils.getFilelugVersion(mContext);
			String newSessionId = null;
			Bundle newAttributes = new Bundle();

//			if (Constants.DEBUG) Log.d(TAG, "getAuthToken(), loginWithSessionId: notificationType=" + notificationType + ", deviceToken=" + deviceToken + ", deviceVersion=" + deviceVersion + ", filelugVersion=" + filelugVersion);

			String locale = MainApplication.getInstance().getApplicationContext().getResources().getConfiguration().locale.toString();
			RequestFuture<JSONObject> future = RequestFuture.newFuture();
			RepositoryClient.getInstance().loginWithSessionId(
				TextUtils.isEmpty(authToken) ? sessionId : authToken,
				verification,
				notificationType,
				deviceToken,
				deviceVersion,
				filelugVersion,
				filelugVersion,
				locale,
				future,
				future
			);
			try {
				JSONObject res = future.get(mContext.getResources().getInteger(R.integer.sync_timeout_b), TimeUnit.MILLISECONDS);
				String oldSessionId = res.getString(Constants.PARAM_OLD_SESSION_ID);
				newSessionId = res.getString(Constants.PARAM_NEW_SESSION_ID);
				boolean _needCreateOrUpdateUserProfile = res.getBoolean(Constants.PARAM_NEED_CREATE_OR_UPDATE_USER_PROFILE);
				String _nickname = res.getString(Constants.PARAM_NICKNAME);
				String _email = res.getString(Constants.PARAM_EMAIL);
				boolean _emailIsVerified = res.getBoolean(Constants.PARAM_EMAIL_IS_VERIFIED);

//				if (Constants.DEBUG) Log.d(TAG, "getAuthToken(): oldSessionId=" + oldSessionId + ", newSessionId=" + newSessionId + ", _needCreateOrUpdateUserProfile=" + _needCreateOrUpdateUserProfile + ", _nickname=" + _nickname + ", _email=" + _email + ", _emailIsVerified=" + _emailIsVerified);

				if ( needCreateOrUpdateUserProfile != _needCreateOrUpdateUserProfile) {
					newAttributes.putString(Constants.PARAM_NEED_CREATE_OR_UPDATE_USER_PROFILE, Boolean.toString(_needCreateOrUpdateUserProfile));
					needCreateOrUpdateUserProfile = _needCreateOrUpdateUserProfile;
					if ( needCreateOrUpdateUserProfile ) {
						needToVerify = true;
					}
//					if (Constants.DEBUG) Log.d(TAG, "getAuthToken(): needCreateOrUpdateUserProfile=" + needCreateOrUpdateUserProfile);
				}
				if ( !TextUtils.equals(nickname, _nickname) ) {
					newAttributes.putString(Constants.PARAM_NICKNAME, _nickname);
				}
				if ( !TextUtils.equals(email, _email) ) {
					newAttributes.putString(Constants.PARAM_EMAIL, _email);
				}
				if ( emailIsVerified != _emailIsVerified) {
					newAttributes.putString(Constants.PARAM_EMAIL_IS_VERIFIED, Boolean.toString(_emailIsVerified));
					emailIsVerified = _emailIsVerified;
				}

				if ( PrefUtils.getGCMTokenChanged() && !TextUtils.isEmpty(PrefUtils.getPushServiceToken()) ) {
					PrefUtils.setGCMTokenChanged(false);
				}
			} catch (JSONException je) {
				errorMsg = mContext.getString(R.string.message_parse_error);
			} catch (InterruptedException ie) {
				errorMsg = ie.getMessage();
			} catch (ExecutionException ee) {
				if ( ee.getCause() instanceof VolleyError ) {
					VolleyError volleyError = (VolleyError)ee.getCause();
					errorMsg = MiscUtils.getVolleyErrorMessage(volleyError);
					if ( volleyError.networkResponse != null ) {
						errorCode = volleyError.networkResponse.statusCode;
						if ( errorCode == Constants.HTTP_STATUS_CODE_FORBIDDEN ) {
							needToVerify = true;
						}
					}
				} else {
					errorMsg = ee.getMessage();
				}
			} catch (TimeoutException te) {
				errorMsg = mContext.getString(R.string.message_timeout_error);
			} catch (Exception e) {
				errorMsg = e.getMessage();
			}

			if ( !TextUtils.isEmpty(newSessionId) ) {
//				if (Constants.DEBUG) Log.d(TAG, "getAuthToken(): newSessionId=" + newSessionId);
				AccountUtils.resetToken(account, authTokenType, newSessionId);
				AccountUtils.setLoggedIn(account, true);
				result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
				result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
				result.putString(AccountManager.KEY_AUTHTOKEN, newSessionId);
				if ( newAttributes.size() > 0 ) {
					AccountUtils.resetUserData(account, newAttributes);
				}
				return result;
			}

		}

		AccountUtils.setLoggedIn(account, false);

		if ( !needToVerify ) {
			if ( errorCode >= 0 ) {
				result.putInt(AccountManager.KEY_ERROR_CODE, errorCode);
			}
			result.putString(AccountManager.KEY_ERROR_MESSAGE, errorMsg);
//			if (Constants.DEBUG) Log.d(TAG, "getAuthToken(): errorCode=" + errorCode + ", errorMsg=" + errorMsg);
			return result;
		}

		if ( !TextUtils.isEmpty(errorMsg) ) {
//			if (Constants.DEBUG) Log.d(TAG, "getAuthToken(): errorMsg=" + errorMsg);
			MsgUtils.showToast(mContext, errorMsg);
		}

//		if (Constants.DEBUG) Log.d(TAG, "getAuthToken(): Relogin...");

		Intent intent = new Intent(mContext, LoginActivity.class);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);
//		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
		intent.putExtra(Constants.AUTH_TOKEN_TYPE, authTokenType);
		intent.putExtra(Constants.AUTH_ACTION_TYPE, Constants.AUTH_ACTION_LOGIN_WHEN_403);
		if ( options == null ) {
			options = new Bundle();
		}
		options.putString(Constants.PARAM_COUNTRY_ID, countryId);
		options.putString(Constants.PARAM_COUNTRY_CODE, countryCode);
		options.putString(Constants.PARAM_PHONE, phone);
		options.putString(Constants.PARAM_PHONE_WITH_COUNTRY, phoneWithCountry);
		intent.putExtra(Constants.AUTH_OPTIONS, options);
		result.putParcelable(AccountManager.KEY_INTENT, intent);

		return result;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		if (Constants.AUTH_TOKEN_TYPE_GENERAL_SERVICE.equals(authTokenType))
			return Constants.AUTH_TOKEN_TYPE_GENERAL_SERVICE_LABEL;
		else
			return authTokenType + " (Label)";
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
		final Bundle result = new Bundle();
		result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
		return result;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
		return null;
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
		return null;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
		return null;
	}

}
