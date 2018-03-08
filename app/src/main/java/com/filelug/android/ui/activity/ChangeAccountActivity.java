package com.filelug.android.ui.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.processbutton.iml.ActionProcessButton;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.ui.widget.RobotoTextView;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.NetworkUtils;
import com.filelug.android.util.Validation;
import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;
import com.marvinlabs.widget.floatinglabel.itempicker.FloatingLabelItemPicker;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Vincent Chang on 2016/4/22.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public class ChangeAccountActivity extends BaseConfigureActivity {

    private static final String TAG = ChangeAccountActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private ImageView ivPageImage;
    private RobotoTextView tvPageDescription = null;
    private FloatingLabelEditText etCurrentAccount = null;
    private FloatingLabelItemPicker<String> pkNewAccount = null;
    private ActionProcessButton btnChange = null;
    private TextView btnAddNewAccount = null;

    private String mCurrentAccount = null;
    private String mCurrentAccountStr = null;
    private boolean mCurrentLoggedIn = false;

    private ArrayList<String> mAccountList = null;
    private String[] mAccounts = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_change_account);

        Resources res = getResources();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCurrentAccount = extras.getString(AccountManager.KEY_ACCOUNT_NAME);
            mCurrentLoggedIn = extras.getBoolean(Constants.EXT_PARAM_LOGGED_IN);
            if ( mCurrentLoggedIn && !TextUtils.isEmpty(mCurrentAccount) ) {
                mCurrentAccountStr = mCurrentAccount;
            } else {
                mCurrentAccountStr = res.getString(R.string.message_computer_not_logged_in);
            }
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mToolbar.setTitle(mCurrentLoggedIn ? R.string.page_change_account_header : R.string.page_account_login_header);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivPageImage = (ImageView) findViewById(R.id.ivPageImage);
        ivPageImage.setImageResource(mCurrentLoggedIn ? R.drawable.header_ic_current_account : R.drawable.header_ic_account_login);
        tvPageDescription = (RobotoTextView) findViewById(R.id.tvPageDescription);
        tvPageDescription.setText(mCurrentLoggedIn ? R.string.page_change_account_message_1 : R.string.page_account_login_message_1);
        etCurrentAccount = (FloatingLabelEditText) findViewById(R.id.etCurrentAccount);
        pkNewAccount = (FloatingLabelItemPicker<String>) findViewById(R.id.ipNewAccount);

        btnChange = (ActionProcessButton) findViewById(R.id.btnChange);
        btnChange.setColorScheme(res.getColor(R.color.main_color_A100), res.getColor(R.color.main_color_500), res.getColor(R.color.white), res.getColor(R.color.material_red_500));
        btnChange.setMode(ActionProcessButton.Mode.ENDLESS);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    String[] selectedObjects = pkNewAccount.getSelectedItems().toArray(new String[0]);
                    String newAccount = selectedObjects[0];
                    sendResult(newAccount);
                }
            }
        });
        btnChange.setText(mCurrentLoggedIn ? R.string.btn_label_change : R.string.btn_label_login);

        btnAddNewAccount = (TextView) findViewById(R.id.btnAddNewAccount);
        btnAddNewAccount.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof TextView) {
                    beforeDoAddAccount();
                }
            }
        } );

        etCurrentAccount.setInputWidgetText(mCurrentAccountStr);
        etCurrentAccount.getInputWidget().setEnabled(false);
        doFindAccounts();
    }

    private boolean checkValidation() {
        boolean ret = true;
        if ( !NetworkUtils.isNetworkAvailable(this) ) {
            return false;
        }
        if ( !Validation.hasSelect(pkNewAccount) ) {
            String message = String.format(getResources().getString(R.string.message_field_can_not_be_empty_2), getResources().getString(R.string.label_login_to_new_account));
            MsgUtils.showWarningMessage(this, message);
            pkNewAccount.requestFocus();
            ret = false;
        }
        return ret;
    }

    private void setItemsEnable(boolean enable) {
        btnChange.setEnabled(enable);
        btnAddNewAccount.setEnabled(enable);
        pkNewAccount.setEnabled(enable);
    }

    private void doFindAccounts() {
        String accountsStr = AccountUtils.getAccountsString();
        if ( !TextUtils.isEmpty(accountsStr) ) {
            mAccounts = accountsStr.split(AccountUtils.SEPARATOR_ACCOUNTS);
        }
        if ( mAccounts != null && mAccounts.length > 0 ) {
            initAccountPicker();
        } else {
            pkNewAccount.setEnabled(false);
            btnChange.setEnabled(false);
            MsgUtils.showWarningMessage(this, getResources().getString(R.string.message_can_not_find_created_accounts));
        }
    }

    private void initAccountPicker() {
        int tmpIndex = -1;
        mAccountList = new ArrayList<String>();
        for ( int i=0; i<mAccounts.length; i++ ) {
            String account = mAccounts[i];
            mAccountList.add(account);
            if ( account.equals(mCurrentAccount) ) {
                tmpIndex = i;
            }
        }

        final int selectedIndex = tmpIndex;
        boolean isEnable = mAccountList.size() > 0;
        pkNewAccount.setEnabled(isEnable);
        btnChange.setEnabled(isEnable);

        if ( mAccountList.size() == 0 ) {
            return;
        }

        pkNewAccount.setAvailableItems(mAccountList);
        if ( selectedIndex > -1 ) {
            pkNewAccount.setSelectedIndices(new int[]{selectedIndex});
        }
        pkNewAccount.setWidgetListener(new FloatingLabelItemPicker.OnWidgetEventListener<String>() {
            @Override
            public void onShowItemPickerDialog(final FloatingLabelItemPicker source) {
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
                    ChangeAccountActivity.this,
                    R.string.label_login_to_new_account,
                    R.drawable.ic_human,
                    mAccounts,
                    selectedIndex,
                    callback
                ).show();
            }
        });
    }

    private void sendResult(final String accountName) {
        setItemsEnable(false);
        btnChange.setProgress(1);

        Intent intent = new Intent();
        if ( !TextUtils.isEmpty(mCurrentAccount) ) {
            intent.putExtra(Constants.EXT_PARAM_OLD_ACCOUNT, mCurrentAccount);
        }
        intent.putExtra(Constants.EXT_PARAM_NEW_ACCOUNT, accountName);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void beforeDoAddAccount() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if ( permission != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.GET_ACCOUNTS }, Constants.REQUEST_PERMISSION_GET_ACCOUNTS_WHEN_ADD_ACCOUNT);
            return;
        }
        doAddAccount();
    }

    private void doAddAccount() {
        final Activity activity = ChangeAccountActivity.this;
        AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle result = future.getResult();
                    String accountName = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                    sendResult(accountName);
                } catch ( OperationCanceledException e) {
                    Log.e(TAG, "doConnectToComputer(), addAccount was canceled");
                } catch ( IOException e) {
                    Log.e(TAG,"doConnectToComputer(), addAccount failed: " + e);
                } catch ( AuthenticatorException e) {
                    Log.e(TAG,"doConnectToComputer(), addAccount failed: " + e);
                }
            }
        };
        AccountManager.get(activity).addAccount(Constants.ACCOUNT_TYPE_FILELUG, Constants.AUTH_TOKEN_TYPE_GENERAL_SERVICE, null, null, activity, callback, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( requestCode == Constants.REQUEST_PERMISSION_GET_ACCOUNTS_WHEN_ADD_ACCOUNT ) {
            if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                doAddAccount();
            } else {
                MsgUtils.showWarningMessage(this, R.string.message_can_not_get_accounts);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
