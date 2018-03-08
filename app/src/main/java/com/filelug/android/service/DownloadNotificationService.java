package com.filelug.android.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.filelug.android.Constants;
import com.filelug.android.MainApplication;
import com.filelug.android.provider.filetransfer.FileTransferColumns;
import com.filelug.android.util.NotificationUtils;
import com.filelug.android.util.TransferDBHelper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Vincent Chang on 2016/12/29.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public class DownloadNotificationService extends Service  {

    private static final String TAG = DownloadNotificationService.class.getSimpleName();

    private static Uri FILE_TRANSFER_URI = FileTransferColumns.CONTENT_URI;

    private static volatile Map<String, Bundle> downloadedGroupIDs;

    private Context mContext;
    private ContentResolver mContentResolver;
    private StatusObserver mStatusObserver;

    @Override
    public void onCreate() {
        super.onCreate();
//        if (Constants.DEBUG) Log.d(TAG, "onCreate()");
        mContext = this.getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
//        if (Constants.DEBUG) Log.d(TAG, "onStartCommand()");
        registerObserver();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
//        if (Constants.DEBUG) Log.d(TAG, "onDestroy()");
        unregisterObserver();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
//        if (Constants.DEBUG) Log.d(TAG, "onBind()");
        return null;
    }

    private void registerObserver() {
        if ( mStatusObserver == null ) {
            mStatusObserver = new StatusObserver(mHandler);
            mContentResolver = getContentResolver();
            mContentResolver.registerContentObserver(FILE_TRANSFER_URI, true, mStatusObserver);
//            if (Constants.DEBUG) Log.d(TAG, "registerObserver(), StatusObserver registered.");
        }
    }

    private void unregisterObserver() {
        if (mContentResolver != null) {
            mContentResolver.unregisterContentObserver(mStatusObserver);
        }
        if (mStatusObserver != null) {
            mStatusObserver = null;
        }
//        if (Constants.DEBUG) Log.d(TAG, "unregisterObserver(), Unregistered StatusObserver.");
    }

    private class StatusObserver extends ContentObserver {

        public StatusObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
//            if (Constants.DEBUG) Log.d(TAG, "DownloadNotificationService.StatusObserver.onChange(), selfChange=" + selfChange);
            checkDownloadGroups();
        }

        private void checkDownloadGroups() {
//            if (Constants.DEBUG) Log.d(TAG, "DownloadNotificationService.StatusObserver.checkDownloadGroups(), downloadedGroupIDs=" + (downloadedGroupIDs == null ? "null" : downloadedGroupIDs.size()));
            if ( downloadedGroupIDs == null ) {
                return;
            }

            if ( downloadedGroupIDs.size() > 0 ) {
                String[] groupArray = downloadedGroupIDs.keySet().toArray(new String[0]);
                for ( String key : groupArray ) {
                    Bundle downloadedGroup = downloadedGroupIDs.get(key);
                    String userId = downloadedGroup.getString(Constants.PARAM_USER_ID);
                    int computerId = downloadedGroup.getInt(Constants.PARAM_COMPUTER_ID);
                    String groupId = downloadedGroup.getString(Constants.PARAM_DOWNLOAD_GROUP_ID);

                    boolean idDownloading = TransferDBHelper.isGroupFileDownloading(userId, computerId, groupId);
//                    if (Constants.DEBUG) Log.d(TAG, "DownloadNotificationService.StatusObserver.checkDownloadGroups(), idDownloading=" + idDownloading + ", key=" + key + ", downloadedGroup=" + downloadedGroup + ", key=" + key + ", userId=" + userId + ", computerId=" + computerId + ", groupId=" + groupId);
                    if ( !idDownloading ) {
                        sendNotification(userId, computerId, groupId);
                        downloadedGroupIDs.remove(key);
//                        if (Constants.DEBUG) Log.d(TAG, "DownloadNotificationService.StatusObserver.checkDownloadGroups() 2, downloadedGroupIDs=" + (downloadedGroupIDs == null ? "null" : downloadedGroupIDs.size()));
                        if ( downloadedGroupIDs.size() == 0 ) {
                            stopSelf();
                            break;
                        }
                    }
                }
            } else {
                stopSelf();
            }
        }

        private void sendNotification(String userId, int computerId, String groupId) {
            Bundle result = TransferDBHelper.getDownloadRowIdAndFileNames(groupId);
            if ( result != null ) {
                String[] rows = result.getStringArray("idAndFileNames");
                String message = result.getString("message");
                Map<Long, String> rowsMap = new LinkedHashMap<Long, String>();
                long rowId = -1;
                String fileName = null;
                for ( String row : rows ) {
                    int separatorIndex = row.indexOf(",");
                    rowId = Long.parseLong(row.substring(0, separatorIndex));
                    fileName = row.substring(separatorIndex+1, row.length());
//                    if (Constants.DEBUG) Log.d(TAG, "DownloadNotificationService.StatusObserver.checkDownloadGroup(), rowId=" + rowId + ", fileName=" + fileName);
                    rowsMap.put(rowId, fileName);
                    NotificationUtils.removeDownloadNotification(mContext, rowId);
                }
                if ( rowsMap.size() == 1 ) {
                    NotificationUtils.noticeSimulateGCMMessageDownloadFile(mContext, rowId, fileName);
                } else {
                    NotificationUtils.noticeSimulateGCMMessageDownloadFiles(mContext, groupId, message, rowsMap);
                }
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            Log.v(TAG, "++++handleMessage " + msg);
//            switch (msg.what) {
//                case LOCALCONTACTS_SYNC:
//                    //do what you want to do
//                    break;
//                default:
//                    break;
//            }
        }

    };

    public static void beginStartingService(Context context) {
//        if (Constants.DEBUG) Log.d(TAG, "DownloadNotificationService: beginStartingService()");
        if ( downloadedGroupIDs != null && downloadedGroupIDs.size() > 0 ) {
            context.startService(new Intent(context, DownloadNotificationService.class));
        }
    }

//    public static void beginStartingService(Context context, long rowId) {
//        if (Constants.DEBUG) Log.d(TAG, "DownloadNotificationService: beginStartingService(), rowId=" + rowId);
//        Intent intent = new Intent(context, DownloadNotificationService.class);
//        intent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
//        context.startService(intent);
//    }

    public static void finishStartedService(Context context) {
//        if (Constants.DEBUG) Log.d(TAG, "DownloadNotificationService: finishStartingService()");
        Intent intent = new Intent(context, DownloadNotificationService.class);
        context.stopService(intent);
    }

//    public static void finishStartingService(Service service) {
////        if (Constants.DEBUG) Log.d(TAG, "DownloadNotificationService: finishStartingService()");
//        service.stopSelf();
//    }

    public static void addDownloadedGroupID(String userId, int computerId, String groupId) {
//        if ( Constants.DEBUG ) Log.d(TAG, "addDownloadedGroupID(), userId=" + userId + ", computerId=" + computerId + ", groupId=" + groupId + ", addDownloadedGroupID(), downloadedGroupIDs=" + (downloadedGroupIDs == null ? "null" : downloadedGroupIDs.size()));
        if ( downloadedGroupIDs == null ) {
            downloadedGroupIDs = new HashMap<String, Bundle>();
        }
        String key = userId + "||" + computerId + "||" + groupId;
        if ( downloadedGroupIDs.containsKey(key) ) {
            return;
        }
        Bundle downloadedGroup = new Bundle();
        downloadedGroup.putString(Constants.PARAM_USER_ID, userId);
        downloadedGroup.putInt(Constants.PARAM_COMPUTER_ID, computerId);
        downloadedGroup.putString(Constants.PARAM_DOWNLOAD_GROUP_ID, groupId);
        downloadedGroupIDs.put(key, downloadedGroup);
    }

    public static void removeDownloadedGroupID(String userId, int computerId, String groupId) {
//        if ( Constants.DEBUG ) Log.d(TAG, "removeDownloadedGroupID(), userId=" + userId + ", computerId=" + computerId + ", groupId=" + groupId + ", addDownloadedGroupID(), downloadedGroupIDs=" + (downloadedGroupIDs == null ? "null" : downloadedGroupIDs.size()));
        if ( downloadedGroupIDs == null || downloadedGroupIDs.size() == 0 ) {
            Context context = MainApplication.getInstance().getApplicationContext();
            finishStartedService(context);
            return;
        }
        String key = userId + "||" + computerId + "||" + groupId;
        if ( downloadedGroupIDs.containsKey(key) ) {
            downloadedGroupIDs.remove(key);
        }
    }

}
