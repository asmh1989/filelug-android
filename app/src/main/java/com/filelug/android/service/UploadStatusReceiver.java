package com.filelug.android.service;

import android.content.Context;

import com.filelug.android.provider.assetfile.UploadStatusType;
import com.filelug.android.util.NotificationUtils;
import com.filelug.android.util.TransferDBHelper;

/**
 * Created by Vincent Chang on 2016/2/4.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public class UploadStatusReceiver extends AbstractServiceReceiver {

    private static final String TAG = UploadStatusReceiver.class.getSimpleName();

    @Override
    public void onPrepare(Context context, boolean fromAnotherApp, long rowId, String fileName, boolean isResume) {
        TransferDBHelper.writeUploadPrepare(rowId, fileName, isResume);
        NotificationUtils.noticePrepareToUploadFile(context, rowId, fileName);
    }

    @Override
    public void onStart(Context context, boolean fromAnotherApp, long rowId, String fileName, long totalBytes, long transferredSize) {
        TransferDBHelper.writeUploadStart(rowId, fileName, totalBytes, transferredSize);

    }

    @Override
    public void onPingError(Context context, boolean fromAnotherApp, long rowId, String fileName, String errorMessage) {
        TransferDBHelper.writeUploadPingError(rowId, fileName, errorMessage);
        NotificationUtils.noticeFileUploadPingError(context, rowId, fileName, errorMessage);
    }

    @Override
    public void onProgressing(Context context, boolean fromAnotherApp, long rowId, String fileName, long transferredBytes, int progress) {
        TransferDBHelper.writeUploadProgressing(rowId, fileName, transferredBytes);
        NotificationUtils.noticeUploadFileProgress(context, rowId, fileName, progress);
    }

    @Override
    public void onTransferCompleted(Context context, boolean fromAnotherApp, long rowId, String fileName, String savedFileName, long transferredBytes, int serverResponseCode, String serverResponseMessage) {
        TransferDBHelper.writeUploadTransferCompleted(rowId, fileName, transferredBytes, serverResponseCode, serverResponseMessage);
        NotificationUtils.noticeUploadFileCompleted(context, rowId, fileName);
    }

    @Override
    public void onTransferError(Context context, boolean fromAnotherApp, long rowId, String fileName, long transferredBytes, Exception exception) {
        TransferDBHelper.writeUploadTransferError(rowId, fileName, transferredBytes, exception);
        NotificationUtils.noticeUploadFileTransferError(context, rowId, fileName, exception == null ? "" : exception.getMessage());
    }

    @Override
    public void onResponseError(Context context, boolean fromAnotherApp, long rowId, String fileName, long transferredBytes, int serverResponseCode, String errorMessage) {
        TransferDBHelper.writeUploadResponseError(rowId, fileName, transferredBytes, serverResponseCode, errorMessage);
        NotificationUtils.noticeUploadFileResponseError(context, rowId, fileName, errorMessage);
    }

    @Override
    public void onCanceled(Context context, boolean fromAnotherApp, long rowId, String fileName, long transferredBytes) {
        TransferDBHelper.writeUploadCanceled(rowId, fileName, transferredBytes);
        NotificationUtils.noticeFileUploadCancel(context, rowId, fileName, transferredBytes);
    }

    @Override
    public void onConfirmed(Context context, boolean fromAnotherApp, int notificationType, long rowId, String fileName, String confirmStatus) {
//        Log.d(TAG, "onConfirmed()");
        NotificationUtils.removeUploadNotification(context, rowId);
        if ( UploadStatusType.success.name().equals(confirmStatus) ||
             UploadStatusType.failure.name().equals(confirmStatus) ||
             UploadStatusType.device_uploaded_but_unconfirmed.name().equals(confirmStatus) ) {
            TransferDBHelper.writeUploadStatus(rowId, confirmStatus);
        } else if ( UploadStatusType.not_found.name().equals(confirmStatus) ) {
            TransferDBHelper.writeUploadStatus(rowId, UploadStatusType.failure.name());
        }
    }

}
