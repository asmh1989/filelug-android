package com.filelug.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class AbstractServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if ( intent == null ||
             !( FilelugService.getActionUploadBroadcast().equals(intent.getAction()) ||
             FilelugService.getActionDownloadBroadcast().equals(intent.getAction()) ) ) {
            return;
        }

        int status = intent.getIntExtra(FilelugService.PARAM_STATUS, 0);
        boolean fromAnotherApp = intent.getBooleanExtra(FilelugService.PARAM_FROM_ANOTHER_APP, false);
//        String userId = intent.getStringExtra(FilelugService.PARAM_USER_ID);
//        int computerId = intent.getIntExtra(FilelugService.PARAM_COMPUTER_ID, 0);
//        String groupId = intent.getStringExtra(FilelugService.PARAM_GROUP_ID);
//        String transferKey = intent.getStringExtra(FilelugService.PARAM_TRANSFER_KEY);
        long rowId = intent.getLongExtra(FilelugService.PARAM_ROW_ID, -1);
        String fileName = intent.getStringExtra(FilelugService.PARAM_FILE_NAME);

        switch (status) {
            case FilelugService.STATUS_PREPARE:
                boolean isResume = intent.getBooleanExtra(FilelugService.PARAM_IS_RESUME, false);
                onPrepare(context, fromAnotherApp, rowId, fileName, isResume);
                break;

            case FilelugService.STATUS_START:
                long totalBytes = intent.getLongExtra(FilelugService.PARAM_TOTAL_BYTES, 0l);
                long transferredSize = intent.getLongExtra(FilelugService.PARAM_TRANSFERRED_SIZE, 0l);
                onStart(context, fromAnotherApp, rowId, fileName, totalBytes, transferredSize);
                break;

            case FilelugService.STATUS_PING_ERROR:
                String pingErrorMessage = (String) intent.getSerializableExtra(FilelugService.ERROR_MESSAGE);
                onPingError(context, fromAnotherApp, rowId, fileName, pingErrorMessage);
                break;

            case FilelugService.STATUS_PROGRESSING:
                long transferredBytes1 = intent.getLongExtra(FilelugService.PARAM_TRANSFERRED_BYTES, 0l);
                int progress = intent.getIntExtra(FilelugService.PARAM_PROGRESS, 0);
                onProgressing(context, fromAnotherApp, rowId, fileName, transferredBytes1, progress);
                break;

            case FilelugService.STATUS_TRANSFER_COMPLETED:
                long transferredBytes2 = intent.getLongExtra(FilelugService.PARAM_TRANSFERRED_BYTES, 0l);
                int responseCode = intent.getIntExtra(FilelugService.SERVER_RESPONSE_CODE, 0);
                String responseMsg = intent.getStringExtra(FilelugService.SERVER_RESPONSE_MESSAGE);
                String savedFileName1 = intent.getStringExtra(FilelugService.PARAM_SAVED_FILE_NAME);
                onTransferCompleted(context, fromAnotherApp, rowId, fileName, savedFileName1, transferredBytes2, responseCode, responseMsg);
                break;

            case FilelugService.STATUS_TRANSFER_ERROR:
                long transferredBytes3 = intent.getLongExtra(FilelugService.PARAM_TRANSFERRED_BYTES, 0l);
                Exception exception = (Exception) intent.getSerializableExtra(FilelugService.ERROR_EXCEPTION);
                onTransferError(context, fromAnotherApp, rowId, fileName, transferredBytes3, exception);
                break;

            case FilelugService.STATUS_RESPONSE_ERROR:
                long transferredBytes4 = intent.getLongExtra(FilelugService.PARAM_TRANSFERRED_BYTES, 0l);
                int errorResponseCode = intent.getIntExtra(FilelugService.SERVER_RESPONSE_CODE, 0);
                String errorMessage = (String) intent.getSerializableExtra(FilelugService.ERROR_MESSAGE);
                onResponseError(context, fromAnotherApp, rowId, fileName, transferredBytes4, errorResponseCode, errorMessage);
                break;

            case FilelugService.STATUS_CANCEL:
                long transferredBytes5 = intent.getLongExtra(FilelugService.PARAM_TRANSFERRED_BYTES, 0l);
                onCanceled(context, fromAnotherApp, rowId, fileName, transferredBytes5);
                break;

            case FilelugService.STATUS_CONFIRMED:
                String confirmStatus = intent.getStringExtra(FilelugService.PARAM_CONFIRM_STATUS);
                int notificationType = intent.getIntExtra(FilelugService.PARAM_NOTIFICATION_TYPE, 0);
                onConfirmed(context, fromAnotherApp, notificationType, rowId, fileName, confirmStatus);
                break;

            default:
                break;
        }

    }

    /**
     * Register this upload receiver. It's recommended to register the receiver in Activity's onResume method.
     *
     * @param context context in which to register this receiver
     */
    public void register(final Context context, final String actionBroadcast) {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(actionBroadcast);
        context.registerReceiver(this, intentFilter);
    }

    /**
     * Unregister this upload receiver. It's recommended to unregister the receiver in Activity's onPause method.
     * 
     * @param context context in which to unregister this receiver
     */
    public void unregister(final Context context) {
        context.unregisterReceiver(this);
    }

    public abstract void onPrepare(Context context, boolean fromAnotherApp, long rowId, String fileName, boolean isResume);

    public abstract void onStart(Context context, boolean fromAnotherApp, long rowId, String fileName, long totalBytes, long transferredSize);

    public abstract void onPingError(Context context, boolean fromAnotherApp, long rowId, String fileName, String errorMessage);

    public abstract void onProgressing(Context context, boolean fromAnotherApp, long rowId, String fileName, long transferredBytes, int progress);

    public abstract void onTransferCompleted(Context context, boolean fromAnotherApp, long rowId, String fileName, String savedFileName, long transferredBytes, int serverResponseCode, String serverResponseMessage);

    public abstract void onTransferError(Context context, boolean fromAnotherApp, long rowId, String fileName, long transferredBytes, Exception exception);

    public abstract void onResponseError(Context context, boolean fromAnotherApp, long rowId, String fileName, long transferredBytes, int serverResponseCode, String errorMessage);

    public abstract void onCanceled(Context context, boolean fromAnotherApp, long rowId, String fileName, long transferredBytes);

    public abstract void onConfirmed(Context context, boolean fromAnotherApp, int notificationType, long rowId, String fileName, String confirmStatus);

}
