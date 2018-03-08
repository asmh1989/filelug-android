package com.filelug.android.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.service.DownloadService;
import com.filelug.android.ui.adapter.DownloadToCacheCursorAdapter;
import com.filelug.android.ui.adapter.TransferLoader;
import com.filelug.android.ui.model.RemoteFile;
import com.filelug.android.ui.widget.MultiSwipeRefreshLayout;
import com.filelug.android.ui.widget.RemoteFilesLayout;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.DialogUtils;
import com.filelug.android.util.FileCache;
import com.filelug.android.util.FilelugUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.PrefUtils;
import com.filelug.android.util.RemoteFileUtils;
import com.filelug.android.util.SortUtils;
import com.filelug.android.util.TransferDBHelper;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Vincent Chang on 2015/11/10.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class OpenFromFilelugActivity extends BaseActivity implements MultiSwipeRefreshLayout.CanChildScrollUpCallback {

    private static final String TAG = OpenFromFilelugActivity.class.getSimpleName();

    private String mUserId = null;
    private int mComputerId = -1;
    private String mDownloadGroupId = null;

    private String mIntentAction = null;
    private String mAcceptType = null;
    private boolean mOpenable = false;
    private boolean mAllowMultiple = false;
    private boolean mLocalOnly = false;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Toolbar mToolbar;
    private RemoteFilesLayout mRemoteFilesLayout = null;
    private RecyclerView mRecordList = null;
    private MaterialDialog downloadProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

//        if ( Constants.DEBUG ) Log.d(TAG, "onCreate()");
        setContentView(R.layout.layout_open_from_filelug);

        initIntentAndExtras(getIntent());
        initUI();
    }

    private void initIntentAndExtras(Intent intent) {
        mIntentAction = intent.getAction();
        mAcceptType = intent.getType();
        Set<String> categories = intent.getCategories();
        if (categories != null) {
            mOpenable = categories.contains(Intent.CATEGORY_OPENABLE);
        }
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(Intent.EXTRA_ALLOW_MULTIPLE)) {
                mAllowMultiple = extras.getBoolean(Intent.EXTRA_ALLOW_MULTIPLE);
            }
            if (extras.containsKey(Intent.EXTRA_LOCAL_ONLY)) {
                mLocalOnly = extras.getBoolean(Intent.EXTRA_LOCAL_ONLY);
            }
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            mAllowMultiple = false;
        }

//        if ( Constants.DEBUG ) Log.d(TAG, "initIntentAndExtras(), mAcceptType=" + mAcceptType + ", mOpenable=" + mOpenable + ", mAllowMultiple=" + mAllowMultiple + ", mLocalOnly=" + mLocalOnly);
    }

    private void initUI() {
//        if ( Constants.DEBUG ) Log.d(TAG, "initUI(), mAllowMultiple="+mAllowMultiple+", mAcceptType="+mAcceptType);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.mRemoteFilesLayout = (RemoteFilesLayout) findViewById(R.id.remote_files_layout);
        this.mRemoteFilesLayout.setChoiceMode(mAllowMultiple ? RemoteFilesLayout.CHOICE_MODE_MULTIPLE_FILES : RemoteFilesLayout.CHOICE_MODE_SINGLE_FILE);
        this.mRemoteFilesLayout.setAcceptedType(mAcceptType);
        this.mRecordList = (RecyclerView) findViewById(R.id.file_recycler_view);
    }

    @Override
    public void onBackPressed() {
//		Log.d(TAG, "onBackPressed()");
        if ( this.mRemoteFilesLayout.backToParent() ) {
            return;
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }
    }

    public void doRefreshAction() {
//        if ( Constants.DEBUG ) Log.d(TAG, "doRefreshAction()");
        this.mRemoteFilesLayout.doRefreshAction();
    }

    public boolean canSwipeRefreshChildScrollUp() {
        boolean canSwipe = getFirstVisiblePosition() > 0;
//		if ( Constants.DEBUG ) Log.d(TAG, "canSwipeRefreshChildScrollUp(): canSwipe=" + canSwipe);
        return canSwipe;
    }

    private int getFirstVisiblePosition() {
        int position;
        RecyclerView.LayoutManager manager = this.mRecordList.getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
        } else if (manager instanceof GridLayoutManager) {
            position = ((GridLayoutManager) manager).findFirstVisibleItemPosition();
        } else if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
            int[] lastPositions = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMinPositions(lastPositions);
        } else {
            position = 0;
        }
        return position;
    }

    private int getMinPositions(int[] positions) {
        int size = positions.length;
        int minPosition = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            minPosition = Math.min(minPosition, positions[i]);
        }
        return minPosition;
    }

    private int getLastVisiblePosition() {
        int position;
        RecyclerView.LayoutManager manager = this.mRecordList.getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
        } else if (manager instanceof GridLayoutManager) {
            position = ((GridLayoutManager) manager).findLastVisibleItemPosition();
        } else if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = manager.getItemCount() - 1;
        }
        return position;
    }

    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if ( Constants.DEBUG ) Log.d(TAG, "onResume()");
        if ( downloadProgressDialog != null ) {
            checkAllDownloaded();
        }
    }

    private void checkAllDownloaded() {
//        if ( Constants.DEBUG ) Log.d(TAG, "checkAllDownloaded(), mDownloadGroupId=" + mDownloadGroupId);
        if ( TextUtils.isEmpty(mDownloadGroupId) ) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    boolean isGroupFileDownloading = TransferDBHelper.isGroupFileDownloading(mUserId, mComputerId, mDownloadGroupId);
                    if ( !isGroupFileDownloading ) {
                        downloadProgressDialog.dismiss();
                        break;
                    }
                    try {
//                        if ( Constants.DEBUG ) Log.d(TAG, "checkAllDownloaded().Runnable().run(), isGroupFileDownloading=" + isGroupFileDownloading);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_open_from_filelug, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        if ( Constants.DEBUG ) Log.d(TAG, "onPostCreate()");
        trySetupSwipeRefresh();
        updateSwipeRefreshProgressBarTop();
    }

    private void trySetupSwipeRefresh() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(
                R.color.main_color_A100,
                R.color.main_color_500,
                R.color.material_red_500);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    doRefreshAction();
                }
            });
            if (mSwipeRefreshLayout instanceof MultiSwipeRefreshLayout) {
                MultiSwipeRefreshLayout mswrl = (MultiSwipeRefreshLayout) mSwipeRefreshLayout;
                mswrl.setCanChildScrollUpCallback(this);
            }
        }
    }

    private void updateSwipeRefreshProgressBarTop() {
        if (mSwipeRefreshLayout == null) {
            return;
        }
        int progressBarStartMargin = getResources().getDimensionPixelSize(R.dimen.swipe_refresh_progress_bar_start_margin);
        int progressBarEndMargin = getResources().getDimensionPixelSize(R.dimen.swipe_refresh_progress_bar_end_margin);
        int top = 0;
        mSwipeRefreshLayout.setProgressViewOffset(false, top + progressBarStartMargin, top + progressBarEndMargin);
    }

    public void onRefreshingStateChanged(boolean refreshing) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    public void enableDisableSwipeRefresh(boolean enable) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(enable);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort:
                this.mRemoteFilesLayout.doSortAction();
                return true;
            case R.id.action_change_account:
                doChangeAccount();
                return true;
            case R.id.action_change_computer:
                doChangeComputer();
                return true;
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doChangeAccount() {
        if ( MiscUtils.isUploadOrDownloadOrNotificationServiceRunning() ) {
            MsgUtils.showWarningMessage(this, R.string.message_cannot_change_account);
            return;
        }

        Intent intent = new Intent(this, ChangeAccountActivity.class);

        Account mActiveAccount = AccountUtils.getActiveAccount();
        if ( mActiveAccount != null ) {
            String accountName = mActiveAccount.name;
            boolean loggedIn = AccountUtils.isLoggedIn(mActiveAccount);
            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
            intent.putExtra(Constants.EXT_PARAM_LOGGED_IN, loggedIn);
        }

        startActivityForResult(intent, Constants.REQUEST_CHANGE_ACCOUNT);
    }

    private void doChangeComputer() {
        if ( MiscUtils.isUploadOrDownloadOrNotificationServiceRunning() ) {
            MsgUtils.showWarningMessage(this, R.string.message_cannot_change_computer);
            return;
        }

        Account mActiveAccount = AccountUtils.getActiveAccount();
        if ( mActiveAccount != null ) {
            AccountManager mAccountManager = AccountManager.get(OpenFromFilelugActivity.this);
            String tmpComputerId = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_COMPUTER_ID);
            int computerId = TextUtils.isEmpty(tmpComputerId) ? -1 : Integer.valueOf(tmpComputerId);
            String computerName = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_COMPUTER_NAME);
            String tmpSocketConnected = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_SOCKET_CONNECTED);
            boolean socketConnected = tmpSocketConnected==null ? false : Boolean.valueOf(tmpSocketConnected);

            Intent intent = new Intent(this, ChangeComputerActivity.class);
            intent.putExtra(Constants.PARAM_COMPUTER_ID, computerId);
            intent.putExtra(Constants.PARAM_COMPUTER_NAME, computerName);
            intent.putExtra(Constants.PARAM_SOCKET_CONNECTED, socketConnected);
            startActivityForResult(intent, Constants.REQUEST_CHANGE_COMPUTER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if ( Constants.DEBUG ) Log.d(TAG, "onActivityResult(): requestCode=" + requestCode + ", resultCode=" + resultCode);
        if ( resultCode == RESULT_OK ) {
            Bundle extras = data.getExtras();
            if ( requestCode == Constants.REQUEST_CHANGE_ACCOUNT ) {
                if ( extras != null ) {
                    String accountName = extras.getString(Constants.EXT_PARAM_NEW_ACCOUNT);
//                    if ( Constants.DEBUG ) Log.d(TAG, "onActivityResult() --> REQUEST_CHANGE_ACCOUNT: accountName=" + accountName);
                    PrefUtils.setActiveAccount(accountName);
                    if ( !TextUtils.isEmpty(accountName) ) {
                        Account account = AccountUtils.getAccount(accountName);
                        accountLogin(account, true);
                    }
                }
            } else if ( requestCode == Constants.REQUEST_CHANGE_COMPUTER ) {
                if ( extras != null ) {
                    int newComputerId = extras.getInt(Constants.EXT_PARAM_NEW_COMPUTER_ID);
                    String newComputerName = extras.getString(Constants.EXT_PARAM_NEW_COMPUTER_NAME);
//                    if ( Constants.DEBUG ) Log.d(TAG, "onActivityResult() --> REQUEST_CHANGE_ACCOUNT: newComputerId=" + newComputerId + ", newComputerName=" + newComputerName);

                    Account activeAccount = AccountUtils.getActiveAccount();
                    AccountManager accountManager = AccountManager.get(OpenFromFilelugActivity.this);
                    accountManager.setUserData(activeAccount, Constants.PARAM_COMPUTER_ID, Integer.toString(newComputerId));
                    accountManager.setUserData(activeAccount, Constants.PARAM_COMPUTER_NAME, newComputerName);
                    accountManager.setUserData(activeAccount, Constants.PARAM_SOCKET_CONNECTED, Boolean.FALSE.toString());

                    beforeDoComputerChanged(activeAccount, newComputerId, newComputerName);
                }
            }
        } else if ( resultCode == RESULT_CANCELED ) {
            if ( requestCode == Constants.REQUEST_INITIAL ) {
                MaterialDialog.SingleButtonCallback buttonCallback = new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        setResult(RESULT_CANCELED, null);
                        finish();
                    }
                };
                MsgUtils.showWarningMessage(OpenFromFilelugActivity.this, getResources().getString(R.string.message_login_then_upload), buttonCallback);
            }
        }
    }

    public void downloadFilesToCache(List<RemoteFile> selectedFileList) {
        Collections.sort(selectedFileList, new SortUtils.RemoteFileNameAscComparator());
        final RemoteFile[] selectedFiles = selectedFileList.toArray(new RemoteFile[0]);
        FilelugUtils.Callback callback = new FilelugUtils.Callback() {
            @Override
            public void onError(int errorCode, String errorMessage) {
            }
            @Override
            public void onSuccess(Bundle result) {
                //long uploadSizeLimit = result.getLong(Constants.PARAM_UPLOAD_SIZE_LIMIT);
                long downloadSizeLimit = result.getLong(Constants.PARAM_DOWNLOAD_SIZE_LIMIT);
                String authToken = result.getString(AccountManager.KEY_AUTHTOKEN);

                String msg = checkDownloadFileLimit(downloadSizeLimit, selectedFiles);
                if (msg != null) {
                    MsgUtils.showInfoMessage(OpenFromFilelugActivity.this, msg);
                } else {
                    doDownload(authToken, selectedFiles);
                }
            }
        };
        FilelugUtils.pingDesktopB(OpenFromFilelugActivity.this, callback);
    }

    private String checkDownloadFileLimit(long downloadSizeLimit, RemoteFile[] selectedFiles) {
        String msg = null;
        String str = null;
        for ( RemoteFile selectedFile : selectedFiles ) {
            long fileSize = selectedFile.getSize();
            if ( downloadSizeLimit < fileSize ) {
                str = (str == null ? "\n" : str+",\n") + selectedFile.getName();
            }
        }
        if ( str != null ) {
            msg = String.format(getResources().getString(R.string.message_exceed_download_size_limit), str);
        }
        return msg;
    }

    private void doDownload(String authToken, RemoteFile[] selectedFiles) {
//        if ( Constants.DEBUG ) Log.d(TAG, "doDownload(), selectedFiles.length=" + selectedFiles.length);
        File accountCacheDir = FileCache.getActiveAccountInCacheDir();
        String cacheDirName = accountCacheDir != null ? accountCacheDir.getAbsolutePath() : FileCache.IN_CACHE_DIR_NAME;

        AccountManager accountManager = AccountManager.get(this);
        Account activeAccount = AccountUtils.getActiveAccount();
        mUserId = accountManager.getUserData(activeAccount, Constants.EXT_PARAM_FILELUG_ACCOUNT);
        String tmpComputerId = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_ID);
        mComputerId = TextUtils.isEmpty(tmpComputerId) ? -1 : Integer.valueOf(tmpComputerId);
        String lugServerId = accountManager.getUserData(activeAccount, Constants.PARAM_LUG_SERVER_ID);

        RemoteFileUtils.FileDownloadSummaryCallback callback = new RemoteFileUtils.FileDownloadSummaryCallback() {
            @Override
            public void created(String downloadGroupId) {
                mDownloadGroupId = downloadGroupId;
                showDownloadProgressDialog();
            }
            @Override
            public void failed(String message) {
                MsgUtils.showToast(OpenFromFilelugActivity.this, message);
            }
        };
        try {
            RemoteFileUtils.createFileDownloadSummary(this, mUserId, mComputerId, lugServerId, authToken, selectedFiles, cacheDirName, 0, null, 0, null, 0, callback, true);
        } catch (Exception e) {
        }
    }

    private void showDownloadProgressDialog() {
//        if ( Constants.DEBUG ) Log.d(TAG, "showDownloadProgressDialog()");
        DownloadToCacheCursorAdapter downloadListAdapter = new DownloadToCacheCursorAdapter(this, null);
        getLoaderManager().initLoader(0, null, new TransferLoader(this, downloadListAdapter, mDownloadGroupId));

        MaterialDialog.SingleButtonCallback buttonCallback = new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if ( MiscUtils.isUploadOrDownloadOrNotificationServiceRunning() ) {
                    DownloadService.stopAllTasks();
                }
                dialog.dismiss();
            }
        };
        DialogInterface.OnShowListener showListener = new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                checkAllDownloaded();
            }
        };
        DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finishDownload();
            }
        };
        downloadProgressDialog = DialogUtils.createListDialog(
            OpenFromFilelugActivity.this,
            R.string.message_start_receiving_files,
            R.drawable.menu_ic_download_file,
            R.string.btn_label_cancel,
            downloadListAdapter,
            buttonCallback,
            showListener,
            dismissListener
        );
        downloadProgressDialog.show();
    }

    private void finishDownload() {
//        if ( Constants.DEBUG ) Log.d(TAG, "finishDownload()");
        Bundle result = TransferDBHelper.getDownloadFileUris(mUserId, mComputerId, mDownloadGroupId);
        Uri[] uris = (Uri[])result.getParcelableArray(Constants.EXT_PARAM_OFF_RESULT_URIS);
        String[] mimeTypes = result.getStringArray(Constants.EXT_PARAM_OFF_RESULT_MIME_TYPES);
        String[] errors = result.getStringArray(Constants.EXT_PARAM_OFF_RESULT_ERRORS);

        final Intent intent = new Intent();

        if ( uris == null || uris.length == 0 ) {

        } else if ( uris.length == 1 ) {
            intent.setData(uris[0]);
        } else if ( uris.length > 1 ) {
            if ( Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 ) {
                ClipData clipData = new ClipData(null, mimeTypes, new ClipData.Item(uris[0]));
                for (int i = 1; i < uris.length; i++) {
                    clipData.addItem(new ClipData.Item(uris[i]));
                }
                intent.setClipData(clipData);
            } else {
                intent.setData(uris[0]);
            }
        }

        if ( errors == null || errors.length == 0 ) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            String message = String.format(getResources().getString(R.string.message_download_failed), TextUtils.join("\n", errors));
            MsgUtils.showWarningMessage(this, message, new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }

    @Override
    public void loginOrConnectStatusChanged(int status) {
//        if ( Constants.DEBUG ) Log.d(TAG, "loginOrConnectStatusChanged(): status=" + status);

        switch (status) {
            case Constants.MESSAGE_LOGGED_IN_AND_SOCKET_CONNECTED:
            case Constants.MESSAGE_COMPUTER_CHANGED_AND_SOCKET_CONNECTED:
                PrefUtils.setReloadRemoteRootDir(true);
                doRefreshAction();
                break;
            case Constants.MESSAGE_LOGGED_IN_BUT_COMPUTER_NOT_CONNECTED:
            case Constants.MESSAGE_LOGGED_IN_BUT_CONNECTION_FAILED:
            case Constants.MESSAGE_COMPUTER_CHANGED_BUT_NOT_CONNECTED:
            case Constants.MESSAGE_COMPUTER_CHANGE_ERROR:
                PrefUtils.setReloadRemoteRootDir(true);
                doRefreshAction();
                break;
            case Constants.MESSAGE_LOGIN_FAILED:
            case Constants.MESSAGE_LOGGED_IN_BUT_COMPUTER_NOT_SET:
            case Constants.MESSAGE_FIND_AVAILABLE_COMPUTERS_GET_AUTH_TOKEN_ERROR:
            case Constants.MESSAGE_LOGGED_IN_BUT_COMPUTER_LIST_NOT_FOUND:
            case Constants.MESSAGE_CHANGE_COMPUTER_GET_AUTH_TOKEN_ERROR:
                this.mRemoteFilesLayout.doResetUI();
                break;
            default:
                break;
        }
    }

}
