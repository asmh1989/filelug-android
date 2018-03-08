package com.filelug.android.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.NetworkUtils;
import com.filelug.android.util.Validation;
import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;

public class VerifyEmailActivity extends BaseConfigureActivity {

	private static final String TAG = VerifyEmailActivity.class.getSimpleName();

	private static VerifyEmailActivity instance;

	private Toolbar mToolbar;
	private FloatingLabelEditText etVerifyCode = null;
	private ActionProcessButton btnVerify = null;

	private String mOriginNewEmail = null;

	public static VerifyEmailActivity getInstance() {
		return instance;
	}

	@Override
	public void onStart() {
		super.onStart();
		instance = this;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_verify_email);

		Bundle extras = getIntent().getExtras();
        mOriginNewEmail = extras.getString(Constants.PARAM_NEW_EMAIL);

		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		mToolbar.setTitle(R.string.page_enter_security_code);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		etVerifyCode = (FloatingLabelEditText) findViewById(R.id.etVerifyCode);

		Resources res = getResources();

		btnVerify = (ActionProcessButton) findViewById(R.id.btnVerify);
		btnVerify.setColorScheme(res.getColor(R.color.main_color_A100), res.getColor(R.color.main_color_500), res.getColor(R.color.white), res.getColor(R.color.material_red_500));
		btnVerify.setMode(ActionProcessButton.Mode.ENDLESS);
		btnVerify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				verifyAction();
			}
		});

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		etVerifyCode.requestFocus();
	}

	private void verifyAction() {
		if ( checkValidation() ) {
			setItemsEnable(false);
			btnVerify.setProgress(1);
			doVerify();
		}
	}

	private boolean checkValidation() {
		View view = getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}

		boolean ret = true;

		if ( !NetworkUtils.isNetworkAvailable(VerifyEmailActivity.this) ) {
			return false;
		}

		if ( !Validation.hasText(etVerifyCode.getInputWidget()) ) {
			etVerifyCode.requestFocus();
			ret = false;
		}

		return ret;
	}

	private void setItemsEnable(boolean enable) {
		btnVerify.setEnabled(enable);
		etVerifyCode.getInputWidget().setEnabled(enable);
	}

    private void doVerify() {
        AccountUtils.AuthTokenCallback callback = new AccountUtils.AuthTokenCallback() {
            @Override
            public void onError(String errorMessage) {
                btnVerify.setProgress(-1);
                MaterialDialog.SingleButtonCallback buttonCallback = new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        btnVerify.setProgress(0);
                        setItemsEnable(true);
                    }
                };
                MsgUtils.showWarningMessage(VerifyEmailActivity.this, errorMessage, buttonCallback);
            }

            @Override
            public void onSuccess(String authToken) {
                doChangeUserEmail(authToken);
            }
        };
        AccountUtils.getAuthToken(VerifyEmailActivity.this, callback);
    }

    private void doChangeUserEmail(String authToken) {
        final String verifyCode = RepositoryUtility.encrypt2Sha256(etVerifyCode.getInputWidgetText().toString());
        String locale = getResources().getConfiguration().locale.toString();

        RepositoryClient.getInstance().changeUserEmail(
            authToken,
            verifyCode,
            mOriginNewEmail,
            locale,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    btnVerify.setProgress(100);
                    Account activeAccount = AccountUtils.getActiveAccount();
                    AccountManager accountManager = AccountManager.get(VerifyEmailActivity.this);
					accountManager.setUserData(activeAccount, Constants.PARAM_EMAIL, mOriginNewEmail);
					accountManager.setUserData(activeAccount, Constants.PARAM_EMAIL_IS_VERIFIED, Boolean.TRUE.toString());
                    Intent intent = new Intent();
                    intent.putExtra(Constants.PARAM_NEW_EMAIL, mOriginNewEmail);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            },
            new BaseResponseError(true, VerifyEmailActivity.this, BaseResponseError.MESSAGE_TYPE_ERROR_MESSAGE) {
                @Override
                protected void beforeShowErrorMessage(VolleyError volleyError) {
                    btnVerify.setProgress(-1);
                }
                @Override
                protected void afterShowErrorMessage(VolleyError volleyError) {
                    btnVerify.setProgress(0);
                    setItemsEnable(true);
                }
            }
        );
    }

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

}
