package com.filelug.android.messaging;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.filelug.android.Constants;
import com.filelug.android.util.PrefUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

/**
 * Created by Vincent Chang on 2015/10/15.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class GCMRegistrationIntentService extends IntentService {

    private static final String TAG = GCMRegistrationIntentService.class.getSimpleName();
    private static final String SENDER_ID = "11001166842";
    private static final String[] TOPICS = {"global"};

    public GCMRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean systemChangeDeviceToken = false;

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            systemChangeDeviceToken = intent.getBooleanExtra(Constants.EXT_PARAM_SYSTEM_CHANGE_DEVICE_TOKEN, false);
//            if (Constants.DEBUG) Log.d(TAG, "onHandleIntent(), systemChangeDeviceToken="+systemChangeDeviceToken);

            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String newDeviceToken = instanceID.getToken(SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            String oldDeviceToken = PrefUtils.getPushServiceToken();
            // [END get_token]

            boolean gcmTokenChanged = !TextUtils.equals(oldDeviceToken, newDeviceToken);
//            if (Constants.DEBUG) Log.d(TAG, "onHandleIntent(), newDeviceToken=" + newDeviceToken +
//                    ", oldDeviceToken=" + oldDeviceToken +
//                    ", gcmTokenChanged=" + gcmTokenChanged);
            if ( gcmTokenChanged ) {
                PrefUtils.setPushServiceToken(newDeviceToken);
            }
            PrefUtils.setGCMTokenChanged(gcmTokenChanged);
            // [END register_for_gcm]
        } catch (Exception e) {
//            if (Constants.DEBUG) Log.d(TAG, "onHandleIntent(), Failed to complete token refresh: ", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Constants.LOCAL_BROADCAST_REGISTRATION_COMPLETE);
        if ( systemChangeDeviceToken )
            registrationComplete.putExtra(Constants.EXT_PARAM_SYSTEM_CHANGE_DEVICE_TOKEN, systemChangeDeviceToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

}
