package com.filelug.android.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.filelug.android.Constants;
import com.filelug.android.MainApplication;
import com.filelug.android.provider.assetfile.AssetFileSelection;
import com.filelug.android.provider.downloadgroup.DownloadGroupSelection;
import com.filelug.android.provider.downloadhistory.DownloadHistorySelection;
import com.filelug.android.provider.filetransfer.FileTransferSelection;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelSelection;
import com.filelug.android.provider.remoteroot.RemoteRootSelection;
import com.filelug.android.provider.uploadgroup.UploadGroupSelection;
import com.filelug.android.provider.uploadhistory.UploadHistorySelection;
import com.filelug.android.provider.usercomputer.UserComputerSelection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent Chang on 2016/4/19.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public class FileCache {

    public static File IN_CACHE_DIR;
    public static String IN_CACHE_DIR_NAME;
    public static File OUT_CACHE_DIR;
    public static String OUT_CACHE_DIR_NAME;

    private static final String IN_TEMP = "off_temp";
    private static final String OUT_TEMP = "out_temp";

    static {
        Context context = MainApplication.getInstance().getApplicationContext();
        File inCacheDir = null;

        if ( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ) {
            inCacheDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + context.getPackageName() + "/cache/" + IN_TEMP);
        } else {
            inCacheDir = new File(context.getCacheDir(), IN_TEMP);
        }
        if ( !inCacheDir.exists() ) {
            inCacheDir.mkdirs();
        }

        IN_CACHE_DIR = inCacheDir;
        IN_CACHE_DIR_NAME = inCacheDir.getAbsolutePath();

        File outCacheDir = null;

        if ( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ) {
            outCacheDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + context.getPackageName() + "/cache/" + OUT_TEMP);
        } else {
            outCacheDir = new File(context.getCacheDir(), OUT_TEMP);
        }
        if ( !outCacheDir.exists() ) {
            outCacheDir.mkdirs();
        }

        OUT_CACHE_DIR = outCacheDir;
        OUT_CACHE_DIR_NAME = outCacheDir.getAbsolutePath();
    }

    public static String getAccountInCacheDirName(Account account) {
        if ( account == null ) {
            return null;
        }

        Context context = MainApplication.getInstance().getApplicationContext();
        AccountManager accountManager = AccountManager.get(context);
        String userId = accountManager.getUserData(account, Constants.EXT_PARAM_FILELUG_ACCOUNT);
        String computerId = accountManager.getUserData(account, Constants.PARAM_COMPUTER_ID);

        String accountInCacheDirName = IN_CACHE_DIR_NAME + File.separator + userId;
        if ( !TextUtils.isEmpty(computerId) ) {
            accountInCacheDirName += File.separator + computerId.trim();
        }

        File accountInCacheDir = new File(accountInCacheDirName);
        if ( !accountInCacheDir.exists() ) {
            accountInCacheDir.mkdirs();
        }

        return accountInCacheDirName;
    }

    public static String getActiveAccountInCacheDirName() {
        Context context = MainApplication.getInstance().getApplicationContext();
        Account activeAccount = AccountUtils.getActiveAccount();
        return getAccountInCacheDirName(activeAccount);
    }

    public static File getAccountInCacheDir(Account account) {
        String accountInCacheDirName = getAccountInCacheDirName(account);
        if ( accountInCacheDirName == null ) {
            return null;
        }
        return new File(accountInCacheDirName);
    }












    public static String getAccountComputerCacheDirName(Account account, int computerId) {
        if ( account == null ) {
            return null;
        }

        Context context = MainApplication.getInstance().getApplicationContext();
        AccountManager accountManager = AccountManager.get(context);
        String userId = accountManager.getUserData(account, Constants.EXT_PARAM_FILELUG_ACCOUNT);

        String accountComputerCacheDirName = IN_CACHE_DIR_NAME + File.separator + userId + File.separator + computerId;
        File accountInCacheDir = new File(accountComputerCacheDirName);
        if ( !accountInCacheDir.exists() ) {
            return null;
        }

        return accountComputerCacheDirName;
    }

















    public static File getActiveAccountInCacheDir() {
        Context context = MainApplication.getInstance().getApplicationContext();
        Account activeAccount = AccountUtils.getActiveAccount();
        return getAccountInCacheDir(activeAccount);
    }

    public static String createDirInActiveAccountCache(String remoteFolder) {
        if ( TextUtils.isEmpty(remoteFolder) ) {
            return null;
        }

        Context context = MainApplication.getInstance().getApplicationContext();
        AccountManager accountManager = AccountManager.get(context);
        Account activeAccount = AccountUtils.getActiveAccount();
        String remoteFileSeparator = accountManager.getUserData(activeAccount, Constants.PARAM_FILE_SEPARATOR);
        if ( TextUtils.isEmpty(remoteFileSeparator) ) {
            return null;
        }

        String folder = remoteFolder.trim().replace(":", "");
        if ( !File.separator.equals(remoteFileSeparator) ) {
            folder = folder.replace(remoteFileSeparator, File.separator);
        }

        String activeAccountInCacheDirName = getActiveAccountInCacheDirName();
        if ( TextUtils.isEmpty(activeAccountInCacheDirName) ) {
            return null;
        }

        File inCacheDir = new File(activeAccountInCacheDirName + File.separator + folder);
        if ( !inCacheDir.exists() ) {
            inCacheDir.mkdirs();
        }

        return inCacheDir.getAbsolutePath();
    }

    public static void cleanAllCachedData() {
        Account[] accounts = AccountUtils.getFilelugAccounts();
        if ( accounts == null || accounts.length == 0 ) {
            return;
        }
        for ( Account account : accounts ) {
            cleanActiveAccountCache(account.name);
        }
    }

    public static void cleanActiveAccountCache(String accountStr) {
        if ( accountStr == null ) {
            return;
        }

        Context context = MainApplication.getInstance().getApplicationContext();
        AccountManager accountManager = AccountManager.get(context);
        Account account = AccountUtils.getAccount(accountStr);

        if ( account == null ) {
            File[] files = IN_CACHE_DIR.listFiles();
            Account[] accounts = AccountUtils.getFilelugAccounts();
            boolean hasFiles = files != null && files.length > 0;
            boolean hasAccounts = accounts != null && accounts.length > 0;
            List<File> removeDirList = new ArrayList<File>();
            List<String> removeUserIdList = new ArrayList<String>();

            if ( hasFiles && !hasAccounts ) {
                for ( File file : files ) {
                    removeDirList.add(file);
                    removeUserIdList.add(file.getName());
                }
            } else if ( hasFiles && hasAccounts ) {
                List<String> dirList = new ArrayList<String>();
                for ( Account tmpAccount : accounts ) {
                    String userId = accountManager.getUserData(tmpAccount, Constants.EXT_PARAM_FILELUG_ACCOUNT);
                    String accountInCacheDirName = IN_CACHE_DIR_NAME + File.separator + userId;
                    dirList.add(accountInCacheDirName);
                }
                for ( File file : files ) {
                    String absPath = file.getAbsolutePath();
                    if ( !dirList.contains(absPath) ) {
                        removeDirList.add(file);
                        removeUserIdList.add(file.getName());
                    }
                }
            }
            for ( File file : removeDirList ) {
                deleteFilesInDir(file, true);
            }
            deleteFilesInDir(OUT_CACHE_DIR, false);
            for ( String userId : removeUserIdList ) {
                removeDBData(context, userId);
            }
        } else {
            String userId = accountManager.getUserData(account, Constants.EXT_PARAM_FILELUG_ACCOUNT);
            String accountInCacheDirName = IN_CACHE_DIR_NAME + File.separator + userId;
            deleteFilesInDir(accountInCacheDirName, true);
            deleteFilesInDir(OUT_CACHE_DIR, false);
            removeDBData(context, userId);
        }
    }

    public static void deleteFile(String fileName) {
        if ( TextUtils.isEmpty(fileName) ) {
            return;
        }
        File cacheFile = new File(fileName);
        if ( !cacheFile.exists() ) {
            return;
        }
        cacheFile.delete();
    }

    public static void deleteFilesInDir(String dirName, boolean includeCurrentDir) {
        File cacheDir = new File(dirName);
        if ( !cacheDir.exists() ) {
            return;
        }
        deleteFilesInDir(cacheDir, includeCurrentDir);
    }

    public static void deleteFilesInDir(File dir, boolean includeCurrentDir) {
        File[] files = dir.listFiles();
        if ( files != null && files.length > 0 ) {
            for ( File file : files ) {
                if ( file.isDirectory() ) {
                    deleteFilesInDir(file, true);
                } else {
                    file.delete();
                }
            }
        }
        if ( includeCurrentDir ) {
            dir.delete();
        }
    }

    private static void removeDBData(Context context, String userId) {
        // Remove SQLite data
        new UserComputerSelection().userId(userId).delete(context.getContentResolver());
        new RemoteRootSelection().userId(userId).delete(context.getContentResolver());
        new RemoteHierarchicalModelSelection().userId(userId).delete(context.getContentResolver());
        new UploadHistorySelection().userId(userId).delete(context.getContentResolver());
        new UploadGroupSelection().userId(userId).delete(context.getContentResolver());
        new AssetFileSelection().userId(userId).delete(context.getContentResolver());
        new DownloadHistorySelection().userId(userId).delete(context.getContentResolver());
        new DownloadGroupSelection().userId(userId).delete(context.getContentResolver());
        new FileTransferSelection().userId(userId).delete(context.getContentResolver());
    }

}
