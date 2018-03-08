package com.filelug.android.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import com.filelug.android.Constants;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.MsgUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Vincent Chang on 2016/12/16.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public class MediaStatusReceiver extends BroadcastReceiver {

    private static final String TAG = MediaStatusReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
//        String logStr = "onReceive()";
        if ( intent == null ) {
//            if ( Constants.DEBUG ) Log.d(TAG, logStr);
//            MsgUtils.showToast(context, logStr);
            return;
        }

        final String action = intent.getAction();
        final Uri uri = intent.getData();

//        logStr += ", action=" + action + ", uri=" + uri;
        if ( !( Intent.ACTION_MEDIA_MOUNTED.equals(action) ||
                Intent.ACTION_MEDIA_UNMOUNTED.equals(action) ||
                Intent.ACTION_MEDIA_BAD_REMOVAL.equals(action) ) ) {
//            if ( Constants.DEBUG ) Log.d(TAG, logStr);
//            MsgUtils.showToast(context, logStr);
            return;
        }

        Bundle extras = intent.getExtras();
        Parcelable storageVolume = extras.getParcelable("storage_volume");

//        String extrasStr = MiscUtils.convertBundleToString(extras);
//        logStr += ", extras=" + extrasStr + ", **storageVolume**=" + storageVolume;
//        if ( Constants.DEBUG ) Log.d(TAG, logStr);
//        MsgUtils.showToast(context, logStr);
    }

}
