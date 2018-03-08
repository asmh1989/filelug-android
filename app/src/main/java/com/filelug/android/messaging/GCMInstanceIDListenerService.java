package com.filelug.android.messaging;

import android.content.Intent;

import com.filelug.android.Constants;
import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Vincent Chang on 2015/10/15.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class GCMInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = GCMInstanceIDListenerService.class.getSimpleName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
//        if (Constants.DEBUG) Log.d(TAG, "onTokenRefresh()");
        Intent intent = new Intent(this, GCMRegistrationIntentService.class);
        intent.putExtra(Constants.EXT_PARAM_SYSTEM_CHANGE_DEVICE_TOKEN, true);
        startService(intent);
    }
    // [END refresh_token]

}
