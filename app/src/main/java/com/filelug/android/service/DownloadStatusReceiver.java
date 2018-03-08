package com.filelug.android.service;

import android.content.Context;
import android.util.Log;

import com.filelug.android.Constants;
import com.filelug.android.provider.filetransfer.DownloadStatusType;
import com.filelug.android.util.NotificationUtils;
import com.filelug.android.util.TransferDBHelper;

/**
 * Created by Vincent Chang on 2016/2/4.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public class DownloadStatusReceiver extends AbstractServiceReceiver {

    private static final String TAG = DownloadStatusReceiver.class.getSimpleName();

    @Override
    public void onPrepare(Context context, boolean fromAnotherApp, long rowId, String fileName, boolean isResume) {
//        if ( Constants.DEBUG ) Log.d(TAG, "onPrepare(), fromAnotherApp=" + fromAnotherApp + ", rowId=" + rowId + ", fileName=" + fileName + ", isResume=" + isResume);
        TransferDBHelper.writeDownloadPrepare(rowId, fileName, isResume);
        if ( !fromAnotherApp ) {
            NotificationUtils.noticePrepareToDownloadFile(context, rowId, fileName);
        }
    }

    @Override
    public void onStart(Context context, boolean fromAnotherApp, long rowId, String fileName, long totalBytes, long transferredSize) {
//        if ( Constants.DEBUG ) Log.d(TAG, "onStart(), fromAnotherApp=" + fromAnotherApp + ", rowId=" + rowId + ", fileName=" + fileName + ", totalBytes=" + totalBytes + ", transferredSize=" + transferredSize);
        TransferDBHelper.writeDownloadStart(rowId, fileName, totalBytes, transferredSize);

    }

    @Override
    public void onPingError(Context context, boolean fromAnotherApp, long rowId, String fileName, String errorMessage) {
//        if ( Constants.DEBUG ) Log.d(TAG, "onPingError(), fromAnotherApp=" + fromAnotherApp + ", rowId=" + rowId + ", fileName=" + fileName + ", errorMessage=" + errorMessage);
        TransferDBHelper.writeDownloadPingError(rowId, fileName, errorMessage);
        if ( !fromAnotherApp ) {
            NotificationUtils.noticeFileDownloadPingError(context, rowId, fileName, errorMessage);
        }
    }

    @Override
    public void onProgressing(Context context, boolean fromAnotherApp, long rowId, String fileName, long transferredBytes, int progress) {
//        if ( Constants.DEBUG ) Log.d(TAG, "onProgressing(), fromAnotherApp=" + fromAnotherApp + ", rowId=" + rowId + ", fileName=" + fileName + ", transferredBytes=" + transferredBytes + ", progress=" + progress);
        TransferDBHelper.writeDownloadProgressing(rowId, fileName, transferredBytes);
        if ( !fromAnotherApp ) {
            NotificationUtils.noticeDownloadFileProgress(context, rowId, fileName, progress);
        }
    }

    @Override
    public void onTransferCompleted(Context context, boolean fromAnotherApp, long rowId, String fileName, String savedFileName, long transferredBytes, int serverResponseCode, String serverResponseMessage) {
//        if ( Constants.DEBUG ) Log.d(TAG, "onTransferCompleted(), fromAnotherApp=" + fromAnotherApp + ", rowId=" + rowId + ", fileName=" + fileName + ", savedFileName=" + savedFileName + ", transferredBytes=" + transferredBytes + ", serverResponseCode=" + serverResponseCode + ", serverResponseMessage=" + serverResponseMessage);
        TransferDBHelper.writeDownloadTransferCompleted(rowId, fileName, savedFileName, transferredBytes, serverResponseCode, serverResponseMessage);
        if ( !fromAnotherApp ) {
            NotificationUtils.noticeDownloadFileCompleted(context, rowId, fileName);
        }
    }

    @Override
    public void onTransferError(Context context, boolean fromAnotherApp, long rowId, String fileName, long transferredBytes, Exception exception) {
//        if ( Constants.DEBUG ) Log.d(TAG, "onTransferError(), fromAnotherApp=" + fromAnotherApp + ", rowId=" + rowId + ", fileName=" + fileName + ", transferredBytes=" + transferredBytes + ", exception=" + exception == null ? "" : exception.getMessage());
        TransferDBHelper.writeDownloadTransferError(rowId, fileName, transferredBytes, exception);
        if ( !fromAnotherApp ) {
            NotificationUtils.noticeDownloadFileTransferError(context, rowId, fileName, exception == null ? "" : exception.getMessage());
        }
    }

    @Override
    public void onResponseError(Context context, boolean fromAnotherApp, long rowId, String fileName, long transferredBytes, int serverResponseCode, String errorMessage) {
//        if ( Constants.DEBUG ) Log.d(TAG, "onResponseError(), fromAnotherApp=" + fromAnotherApp + ", rowId=" + rowId + ", fileName=" + fileName + ", transferredBytes=" + transferredBytes + ", serverResponseCode=" + serverResponseCode + ", errorMessage=" + errorMessage);
        TransferDBHelper.writeDownloadResponseError(rowId, fileName, transferredBytes, serverResponseCode, errorMessage);
        if ( !fromAnotherApp ) {
            NotificationUtils.noticeDownloadFileResponseError(context, rowId, fileName, errorMessage);
        }
    }

    @Override
    public void onCanceled(Context context, boolean fromAnotherApp, long rowId, String fileName, long transferredBytes) {
//        if ( Constants.DEBUG ) Log.d(TAG, "onCanceled(), fromAnotherApp=" + fromAnotherApp + ", rowId=" + rowId + ", fileName=" + fileName + ", transferredBytes=" + transferredBytes);
        TransferDBHelper.writeDownloadCanceled(rowId, fileName, transferredBytes);
        if ( !fromAnotherApp ) {
            NotificationUtils.noticeFileDownloadCancel(context, rowId, fileName, transferredBytes);
        }
    }

    @Override
    public void onConfirmed(Context context, boolean fromAnotherApp, int notificationType, long rowId, String fileName, String confirmStatus) {
//        if ( Constants.DEBUG ) Log.d(TAG, "onConfirmed(), fromAnotherApp=" + fromAnotherApp + ", rowId=" + rowId + ", fileName=" + fileName + ", confirmStatus=" + confirmStatus);
        TransferDBHelper.writeDownloadStatus(rowId, confirmStatus);
        if ( !fromAnotherApp ) {
            if ( DownloadStatusType.success.name().equals(confirmStatus) ) {
                NotificationUtils.removeDownloadNotification(context, rowId);
                if ( notificationType == 1 ) {// Notify on each file
                    NotificationUtils.noticeDownloadFileSuccess(context, rowId, fileName);
                }
            }
        }
    }

}
