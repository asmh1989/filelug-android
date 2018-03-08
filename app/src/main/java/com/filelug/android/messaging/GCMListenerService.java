package com.filelug.android.messaging;

import android.content.Context;
import android.os.Bundle;

import com.filelug.android.Constants;
import com.filelug.android.provider.assetfile.UploadStatusType;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.NotificationUtils;
import com.filelug.android.util.TransferDBHelper;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.Map;

/**
 * Created by Vincent Chang on 2015/10/15.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class GCMListenerService extends GcmListenerService {

    private static final String TAG = GCMListenerService.class.getSimpleName();

    public static final String FLTYPE_UPLOAD_FILE = "upload-file";
    public static final String FLTYPE_UPLOAD_FILES = "all-files-uploaded-successfully";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Context context = getApplicationContext();

        String title = data.getString(Constants.PARAM_TITLE);
        String body = data.getString(Constants.PARAM_BODY);
        String flType = data.getString(Constants.PARAM_FL_TYPE);
        String transferKey = data.getString(Constants.PARAM_TRANSFER_KEY_2);
        String transferStatus = data.getString(Constants.PARAM_TRANSFER_STATUS);
        String uploadGroupId = data.getString(Constants.PARAM_UPLOAD_GROUP_ID);

//        if (Constants.DEBUG) {
//            String msgReceived = "onMessageReceived()";
//            for ( String key : data.keySet() ) {
//                Object value = data.get(key);
//                msgReceived += ", " + key + "=" + value;
//            }
//            Log.d(TAG, msgReceived);
//        }

        if ( FLTYPE_UPLOAD_FILE.equals(flType) ) {
            long rowId = TransferDBHelper.getUploadRowId(transferKey);
            TransferDBHelper.writeUploadStatus(rowId, transferStatus);
            NotificationUtils.noticeGCMMessageUploadFile(context, rowId, transferStatus, body);
        } else if ( FLTYPE_UPLOAD_FILES.equals(flType) ) {
            transferStatus = UploadStatusType.success.name();
            Map<Long, String> rows = TransferDBHelper.getUploadRowIdAndFileNames(uploadGroupId);
            if ( rows != null && rows.size() > 0 ) {
                for ( Long rowId : rows.keySet() ) {
                    NotificationUtils.removeUploadNotification(context, rowId);
                    TransferDBHelper.writeUploadStatus(rowId, transferStatus);
                }
            }
            NotificationUtils.noticeGCMMessageUploadFiles(context, uploadGroupId, body, rows);
        } else {
            MsgUtils.showToast(context, flType + "\n" + body);
        }
    }

}
