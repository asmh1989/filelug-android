package com.filelug.android.ui.activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.PermissionsHelper;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.io.IOException;

/**
 * Created by Vincent Chang on 2015/12/28.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class InitialPageActivity extends AppCompatActivity {

    private static final String TAG = InitialPageActivity.class.getSimpleName();

    private View btnSignIn = null;
    private View btnGettingStarted = null;
    private View btnSkip = null;
    private MaterialDialog mPermissionsDialog = null;

    private String mRequestFromActivity = null;
    private Bundle mOriginExtra = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Constants.DEBUG) Log.d(TAG, "onCreate()");
        setContentView(R.layout.layout_initial_page);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mRequestFromActivity = extras.getString(Constants.EXT_PARAM_REQUEST_FROM_ACTIVITY);
            mOriginExtra = extras.getBundle(Constants.EXT_PARAM_ORIGIN_EXTRA);
        }

        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAddAccount();
            }
        });

        btnGettingStarted = findViewById(R.id.btnGettingStarted);
        btnGettingStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialPageActivity.this, GettingStartedActivity.class);
                startActivityForResult(intent, Constants.REQUEST_GETTING_STARTED);
            }
        });

        btnSkip = findViewById(R.id.btnSkip);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResponse(null);
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        if (Constants.DEBUG) Log.d(TAG, "onPostCreate()");

        if (!TextUtils.isEmpty(mRequestFromActivity)) {
            btnSkip.setVisibility(View.GONE);
        }

        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
        } catch (GooglePlayServicesNotAvailableException e) {
        }

        boolean showPermissionDialog = PermissionsHelper.areExplicitPermissionsRequired() && !PermissionsHelper.isFilelugRequiredPermissionGranted(this);
        if (showPermissionDialog) {
            MaterialDialog.SingleButtonCallback positiveButtonCallback = new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    doGoToSettings();
                }
            };
            MaterialDialog.SingleButtonCallback neutralButtonCallback = new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    doDeny();
                }
            };
            mPermissionsDialog = DialogUtils.createPermissionsDialog(this, R.string.title_permissions_dialog, -1, R.string.btn_label_go_to_settings, R.string.btn_label_deny, positiveButtonCallback, neutralButtonCallback);
            PermissionsHelper.refreshPermissionsState(this, mPermissionsDialog, false);
            mPermissionsDialog.show();
        }
    }

    private void doGoToSettings() {
        MaterialDialog.SingleButtonCallback buttonCallback = new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//      		startActivityForResult(myAppSettings, Constants.REQUEST_PERMISSIONS);
                startActivity(myAppSettings);
                dialog.dismiss();
            }
        };
        DialogUtils.createPermissionsSetupTodoDialog(
            this,
            R.string.title_permissions_step_description,
            -1,
            android.R.string.ok,
            buttonCallback
        ).show();
    }

    private void doDeny() {
        MaterialDialog.SingleButtonCallback neutralButtonCallback = new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                dialog.dismiss();
//                mPermissionsDialog = null;
                finish();
            }
        };
        DialogUtils.createButtonsDialog31(
            this,
            -1,
            -1,
            R.string.message_deny_permission_setup,
            R.string.btn_label_yes_deny,
            R.string.btn_label_no_try_again,
            neutralButtonCallback
        ).show();
    }

    private void doAddAccount() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.GET_ACCOUNTS}, Constants.REQUEST_PERMISSION_GET_ACCOUNTS_WHEN_ADD_ACCOUNT);
            return;
        }

        final Activity activity = InitialPageActivity.this;
        AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle result = future.getResult();
                    String accountName = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                    Account account = AccountUtils.getAccount(accountName);
                    sendResponse(account);
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

    private void sendResponse(Account account) {
        Intent intent = new Intent();
        if ( account != null) {
            intent.putExtra(Constants.PARAM_ACCOUNT, account.name);
        }
        if ( mOriginExtra != null ) {
            intent.putExtra(Constants.EXT_PARAM_ORIGIN_EXTRA, mOriginExtra);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        PermissionsHelper.refreshPermissionsState(this, mPermissionsDialog, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if ( requestCode == Constants.REQUEST_PERMISSION_GET_ACCOUNTS_WHEN_ADD_ACCOUNT ) {
            if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                doAddAccount();
            } else {
                MsgUtils.showWarningMessage(this, R.string.message_can_not_get_accounts);
            }
        }
    }

}
