package com.filelug.android.docsprovider;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.DocumentsProvider;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.android.volley.toolbox.RequestFuture;
import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.RepositoryClient;
import com.filelug.android.crepo.RepositoryErrorObject;
import com.filelug.android.docsprovider.cursor.FLComputerDocumentCursor;
import com.filelug.android.docsprovider.cursor.FLFileDocumentCursor;
import com.filelug.android.docsprovider.cursor.FLRootDirectoryDocumentCursor;
import com.filelug.android.docsprovider.cursor.ListAvailableComputersCursor;
import com.filelug.android.docsprovider.cursor.ListDirectoryFilesCursor;
import com.filelug.android.docsprovider.cursor.ListRootDirectoriesCursor;
import com.filelug.android.docsprovider.cursor.RecentDocumentsCursor;
import com.filelug.android.docsprovider.cursor.RootCursor;
import com.filelug.android.docsprovider.cursor.RootDocumentCursor;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelColumns;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelCursor;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelSelection;
import com.filelug.android.ui.model.RemoteFile;
import com.filelug.android.ui.model.RemoteFileObject;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.FileCache;
import com.filelug.android.util.FilelugUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.NetworkUtils;
import com.filelug.android.util.RemoteFileUtils;
import com.filelug.android.util.TransferDBHelper;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vincent Chang on 2016/05/11.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class RemoteFilesProvider extends DocumentsProvider {

    private static final String TAG = RemoteFilesProvider.class.getSimpleName();

    public static final String AUTHORITY = "com.filelug.android.docsprovider";

    private Context mContext;
    private String mAuthority;

    @Override
    public boolean onCreate() {
//        if (Constants.DEBUG) Log.d(TAG, "onCreate()");
        return true;
    }

    @Override
    public void shutdown() {
//        if (Constants.DEBUG) Log.d(TAG, "shutdown()");
        super.shutdown();
    }

    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        mContext = context;
        mAuthority = info.authority;
//        if (Constants.DEBUG) Log.d(TAG, "attachInfo() ------> authority=" + mAuthority);
        super.attachInfo(context, info);
        context.getContentResolver().notifyChange(DocumentsContract.buildRootsUri(mAuthority), null);
    }

    @Override
    public Cursor queryRoots(String[] projection) throws FileNotFoundException {
//        if (Constants.DEBUG) Log.d(TAG, "queryRoots() ------> Arguments: projection=" + (projection == null ? null : TextUtils.join(",", projection)));
        Account[] accounts = getFilelugAccounts();
        return new RootCursor(getContext(), projection, accounts);
    }

    private Account[] getFilelugAccounts() {
        Account[] accounts = null;
        if ( ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED ) {
            accounts = AccountUtils.getFilelugAccounts();
        }
        return accounts;
    }

    @Override
    public Cursor queryDocument(final String documentId, String[] projection) throws FileNotFoundException {
//        if ( Constants.DEBUG ) Log.d(TAG, "queryDocument() ------> Arguments: documentId=" + documentId + ", projection=" + (projection == null ? null : TextUtils.join(",", projection)));
        if ( DocumentIdUtils.isNotSet(documentId) ||
             DocumentIdUtils.isAccountRoot(documentId) ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "queryDocument(), It's a root document!");
            return new RootDocumentCursor(getContext(), documentId, projection);
        } else if ( DocumentIdUtils.isComputer(documentId) ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "queryDocument(), It's a computer document!");
            return new FLComputerDocumentCursor(getContext(), mAuthority, documentId, projection);
        } else if ( DocumentIdUtils.isRootDirectory(documentId) ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "queryDocument(), It's a root directory document!");
            return new FLRootDirectoryDocumentCursor(getContext(), mAuthority, documentId, projection);
        } else if ( DocumentIdUtils.isFile(documentId) ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "queryDocument(), It's a file document!");
            return new FLFileDocumentCursor(getContext(), mAuthority, documentId, projection);
        } else {
//            if ( Constants.DEBUG ) Log.d(TAG, "queryDocument(), Null!");
            return null;
        }
    }

    @Override
    public Cursor queryChildDocuments(String parentDocumentId, String[] projection, String sortOrder) throws FileNotFoundException {
//        if ( Constants.DEBUG ) Log.d(TAG, "queryChildDocuments() ------> Arguments: parentDocumentId=" + parentDocumentId + ", projection=" + (projection == null ? null : TextUtils.join(",", projection)) + ", sortOrder=" + sortOrder);

        Context context = getContext();
        if ( !NetworkUtils.isNetworkAvailable(context) ) {
            throw new FileNotFoundException("No network connection!");
        }
        if ( TextUtils.isEmpty(parentDocumentId) ) {
            throw new FileNotFoundException("No parent document id!");
        }

        if ( DocumentIdUtils.isNotSet(parentDocumentId) ||
             DocumentIdUtils.isAccountRoot(parentDocumentId) ) {
            return new ListAvailableComputersCursor(context, parentDocumentId, projection, sortOrder);
        } else if ( DocumentIdUtils.isComputer(parentDocumentId) ) {
            return new ListRootDirectoriesCursor(context, parentDocumentId, projection, sortOrder);
        } else if ( DocumentIdUtils.isRootDirectory(parentDocumentId) ||
                    DocumentIdUtils.isFile(parentDocumentId) ) {
            return new ListDirectoryFilesCursor(context, parentDocumentId, projection, sortOrder);
        }

        return null;
    }

    @Override
    public Cursor queryRecentDocuments(String rootId, String[] projection) throws FileNotFoundException {
//        if ( Constants.DEBUG ) Log.d(TAG, "queryRecentDocuments() ------> Arguments: rootId=" + rootId + ", projection=" + (projection == null ? null : TextUtils.join(",", projection)));

        Context context = getContext();
        if ( !NetworkUtils.isNetworkAvailable(context) ) {
            throw new FileNotFoundException("No network connection!");
        }
        if ( TextUtils.isEmpty(rootId) ) {
            throw new FileNotFoundException("No root id!");
        }

        return new RecentDocumentsCursor(getContext(), rootId, projection);
    }

    @Override
    public ParcelFileDescriptor openDocument(final String documentId, String mode, CancellationSignal signal) throws FileNotFoundException {
//        if ( Constants.DEBUG ) Log.d(TAG, "openDocument() ------> Arguments: documentId=" + documentId + ", mode=" + mode + ", signal=" + signal);

        Context context = getContext();

        String[] idParts = DocumentIdUtils.getFileRowIdAndParents(documentId);
        String accountName = idParts[0];
        int computerId = Integer.valueOf(idParts[1]).intValue();
        long fileRowId = Long.valueOf(idParts[2]).longValue();
//        if ( Constants.DEBUG ) Log.d(TAG, "openDocument(), accountName=" + accountName + ", computerId=" + computerId + ", fileRowId=" + fileRowId);

        RemoteHierarchicalModelSelection remoteHierarchicalModelSelection = new RemoteHierarchicalModelSelection()
            .id(fileRowId).and()
            .computerId(computerId);
        RemoteHierarchicalModelCursor c = remoteHierarchicalModelSelection.query(
            context.getContentResolver(),
            RemoteHierarchicalModelColumns.ALL_COLUMNS,
            null
        );

        String userId = null;
        File cacheFile = null;
        RemoteFileObject remoteFileObject = null;
        if ( c.moveToFirst() ) {
            userId = c.getUserId();
            cacheFile = RemoteFileUtils.getFileInCache(context, userId, computerId, c);
//            if ( Constants.DEBUG ) Log.d(TAG, "openDocument(), userId=" + userId + ", computerId=" + computerId + ", cacheFile=" + (cacheFile != null ? cacheFile.getAbsolutePath() : ""));
            remoteFileObject = new RemoteFileObject(c, "");
        }
        c.close();

        ParcelFileDescriptor fd = null;
        if ( cacheFile == null ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "openDocument(), File not in cache, need to download!");
            fd = downloadCacheFile(documentId, mode, signal, userId, computerId, remoteFileObject);
        } else {
//            if ( Constants.DEBUG ) Log.d(TAG, "openDocument(), Open file!");
            fd = openFile(documentId, cacheFile, mode, signal);
        }

        return fd;
    }

    private synchronized ParcelFileDescriptor downloadCacheFile(final String documentId, String mode, final CancellationSignal signal, String userId, int computerId, RemoteFileObject selectedFile) throws FileNotFoundException {
//        if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), documentId=" + documentId + ", mode=" + mode + ", signal=" + signal + ", userId=" + userId + ", computerId=" + computerId + ", selectedFile=" + selectedFile);
        Context context = getContext();
        Account account = AccountUtils.getAccountByUserId(userId);
        if ( account == null ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), Account is null!");
            String errorMsg = context.getResources().getString(R.string.message_account_not_created);
            throw new FileNotFoundException(errorMsg);
        }

        Bundle connResult = AccountUtils.connectToComputer2(context, account, computerId);
        int errorCode = connResult.getInt(AccountManager.KEY_ERROR_CODE);
        String errorMessage = connResult.getString(AccountManager.KEY_ERROR_MESSAGE);

//        if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), errorCode=" + errorCode + ", errorMessage=" + errorMessage);

        if ( !TextUtils.isEmpty(errorMessage) ) {
            if ( errorCode == Constants.HTTP_STATUS_CODE_SERVICE_UNAVAILABLE ) {
                FilelugUtils.actionWhen503(context, account, false);
                throw new FileNotFoundException(context.getResources().getString(R.string.message_server_error_503));
            } else {
                throw new FileNotFoundException(errorMessage);
            }
        }

        String authToken = connResult.getString(AccountManager.KEY_AUTHTOKEN);
        String lugServerId = connResult.getString(Constants.PARAM_LUG_SERVER_ID);

//        if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), authToken=" + authToken + ", lugServerId=" + lugServerId);

        if ( TextUtils.isEmpty(lugServerId) ) {
            FilelugUtils.actionWhen503(context, account, false);
            throw new FileNotFoundException(context.getResources().getString(R.string.message_server_error_503));
        }

        AccountManager accountManager = AccountManager.get(context);
        String fileSeparator = accountManager.getUserData(account, Constants.PARAM_FILE_SEPARATOR);
        selectedFile.setFileSeparator(fileSeparator);

//        if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), authToken=" + authToken + ", file=" + selectedFile.getFullName());

        String errorMsg = null;

        String locale = context.getResources().getConfiguration().locale.toString();
        int timeOut = context.getResources().getInteger(R.integer.sync_timeout_a);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        RepositoryClient.getInstance().pingDesktop(
            authToken,
            userId,
            locale,
            future,
            future
        );

        try {
            JSONObject response = future.get(timeOut, TimeUnit.MILLISECONDS);
            long uploadSizeLimit = response.optLong(Constants.PARAM_UPLOAD_SIZE_LIMIT);
            long downloadSizeLimit = response.optLong(Constants.PARAM_DOWNLOAD_SIZE_LIMIT);
            long fileSize = selectedFile.getSize();
//            if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), pingDesktop(): uploadSizeLimit=" + uploadSizeLimit + ", downloadSizeLimit=" + downloadSizeLimit + ", fileSize=" + fileSize);
            if ( downloadSizeLimit < fileSize ) {
                errorMsg = String.format(context.getResources().getString(R.string.message_exceed_download_size_limit), selectedFile.getName());
            }
        } catch (Exception e) {
            RepositoryErrorObject errorObject = MiscUtils.getErrorObject(context, e, account);
            errorMsg = errorObject.getMessage();
        }

        if ( errorMsg != null ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), Ping desktop error!\n" + errorMsg);
            throw new FileNotFoundException(errorMsg);
        }

        File accountCacheDir = FileCache.getActiveAccountInCacheDir();
        String cacheDirName = accountCacheDir != null ? accountCacheDir.getAbsolutePath() : FileCache.IN_CACHE_DIR_NAME;
//        if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), cacheDirName=" + cacheDirName);
        String mDownloadGroupId = null;
        try {
            mDownloadGroupId = RemoteFileUtils.createFileDownloadSummary(context, userId, computerId, lugServerId, authToken, new RemoteFile[] {selectedFile}, cacheDirName, 0, null, 0, null, 0, null, true);
//            if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), mDownloadGroupId=" + mDownloadGroupId);
        } catch (Exception e) {
//            if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), Create download summary error!\n" + e.getMessage());
            throw new FileNotFoundException(e.getMessage());
        }

        while (true) {
            if ( signal != null && signal.isCanceled() ) {
                break;
            }
            if ( !MiscUtils.isUploadOrDownloadOrNotificationServiceRunning() && !TransferDBHelper.isGroupFileDownloading(userId, computerId, mDownloadGroupId) ) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        Bundle result = TransferDBHelper.getDownloadFileUris(userId, computerId, mDownloadGroupId);
        Uri[] uris = (Uri[])result.getParcelableArray(Constants.EXT_PARAM_OFF_RESULT_URIS);
        String[] errors = result.getStringArray(Constants.EXT_PARAM_OFF_RESULT_ERRORS);

        if ( uris != null && uris.length > 0 ) {
            String path = uris[0].getPath();
//            if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), uris[0]=" + path);
            File cacheFile = new File(path);
            return openFile(documentId, cacheFile, mode, signal);
        } else if ( errors != null && errors.length > 0 ) {
//            if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), errors[0]=" + errors[0]);
            throw new FileNotFoundException(errors[0]);
        }

//        if ( Constants.DEBUG ) Log.d(TAG, "downloadCacheFile(), Return null");
        return null;
    }

/*
    private ParcelFileDescriptor openFile(final String documentId, File cacheFile, String mode, CancellationSignal signal) throws FileNotFoundException {
//        if ( Constants.DEBUG ) Log.d(TAG, "openFile(), documentId=" + documentId + ", cacheFile=" + cacheFile.getAbsolutePath() + ", mode=" + mode);
        Context context = getContext();
        String[] idParts = documentId.split(DocumentIdUtils.NODE_SEPARATOR);
        String accountId = DocumentIdUtils.getAccountId(idParts[0]);
        long fileRowId = DocumentIdUtils.getFileRowId(idParts[1]);

        long currentTime = new Date().getTime();
        RemoteFileUtils.updateRemoteHierarchicalModelLastAccess(context, fileRowId, currentTime);

        try {
            return ParcelFileDescriptorUtil.pipeFrom(new FileInputStream(cacheFile));
        } catch (IOException e) {
            throw new FileNotFoundException("Failed to open document with id " + documentId + " and mode " + mode);
        }
    }
*/
    private ParcelFileDescriptor openFile(final String documentId, File cacheFile, String mode, CancellationSignal signal) throws FileNotFoundException {
//        if ( Constants.DEBUG ) Log.d(TAG, "openFile(), documentId=" + documentId + ", cacheFile=" + cacheFile.getAbsolutePath() + ", mode=" + mode);
        Context context = getContext();
        String[] idParts = DocumentIdUtils.getFileRowIdAndParents(documentId);
        int computerId = Long.valueOf(idParts[1]).intValue();
        long fileRowId = Long.valueOf(idParts[2]).longValue();
        final int accessMode = ParcelFileDescriptor.parseMode(mode);
        boolean isWrite = (mode.indexOf('w') != -1);

//        if ( Constants.DEBUG ) Log.d(TAG, "openFile(), cacheFile=" + cacheFile.getAbsolutePath() + ", accessMode=" + accessMode);

        long currentTime = new Date().getTime();
        RemoteFileUtils.updateRemoteHierarchicalModelLastAccess(context, fileRowId, currentTime);

        ParcelFileDescriptor fd = null;
        if (isWrite) {
//            if ( Constants.DEBUG ) Log.d(TAG, "openFile(), Open file to write!");
            // Attach a close listener if the document is opened in write mode.
            try {
                Handler handler = new Handler(context.getMainLooper());
                fd = ParcelFileDescriptor.open(cacheFile, accessMode, handler,
                        new ParcelFileDescriptor.OnCloseListener() {
                            @Override
                            public void onClose(IOException e) {
                                // Update the file with the cloud server.  The client is done writing.
//                                if ( Constants.DEBUG ) Log.d(TAG, "openFile(), A file with id " + documentId + " has been closed!  Time to update the server.");
                            }
                        }
                );
            } catch (IOException e) {
                throw new FileNotFoundException("Failed to open document with id " + documentId + " and mode " + mode);
            }
        } else {
//            if ( Constants.DEBUG ) Log.d(TAG, "openFile(), Open file to read!");
            fd = ParcelFileDescriptor.open(cacheFile, accessMode);
        }

        return fd;
    }
/*
    private ParcelFileDescriptor openFile(final String documentId, File cacheFile, String mode, CancellationSignal signal) throws FileNotFoundException {
//        if ( Constants.DEBUG ) Log.d(TAG, "openFile(), documentId=" + documentId + ", cacheFile=" + cacheFile.getAbsolutePath() + ", mode=" + mode);
        Context context = getContext();
        String[] idParts = documentId.split(DocumentIdUtils.NODE_SEPARATOR);
        String accountId = DocumentIdUtils.getAccountId(idParts[0]);
        long fileRowId = DocumentIdUtils.getFileRowId(idParts[1]);

        long currentTime = new Date().getTime();
        RemoteFileUtils.updateRemoteHierarchicalModelLastAccess(context, fileRowId, currentTime);

        int accessMode = (mode.indexOf('w') != -1) ? ParcelFileDescriptor.MODE_READ_WRITE : ParcelFileDescriptor.MODE_READ_ONLY;
//        if ( Constants.DEBUG ) Log.d(TAG, "openFile(), accessMode=" + accessMode);
        return ParcelFileDescriptor.open(cacheFile, accessMode);
    }
*/
}
