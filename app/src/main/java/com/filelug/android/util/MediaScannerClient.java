package com.filelug.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vincent Chang on 2016/3/6.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public class MediaScannerClient implements MediaScannerConnectionClient {

    private static final String TAG = MediaScannerClient.class.getSimpleName();

    private Context mContext;
    private List<String> mFileNames;
    private Map<String, String> mFileContentTypes;
    private MediaScannerConnection mConn;

    public MediaScannerClient(Context ctx, List<String> fileNameList) {
        this.mContext = ctx;
        this.mFileNames = fileNameList;
        initScannedFiles(fileNameList);
        this.mConn = new MediaScannerConnection(mContext, this);
        this.mConn.connect();
    }

    private void initScannedFiles(List<String> fileNameList) {
        this.mFileContentTypes = new HashMap<String, String>();

        for ( int i=0; i<fileNameList.size(); i++ ) {
            String fileName = fileNameList.get(i);
            String fileExtension = MiscUtils.getExtension(fileName);
            String contentType = "";

            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            if ( mimeTypeMap.hasExtension(fileExtension) ) {
                contentType = mimeTypeMap.getMimeTypeFromExtension(fileExtension);
            }

            this.mFileContentTypes.put(fileName, contentType);
        }
    }

    @Override
    public void onMediaScannerConnected() {
        String[] files = this.mFileNames.toArray(new String[0]);
//        if ( Constants.DEBUG ) Log.d(TAG, "onMediaScannerConnected(): files=[" + TextUtils.join(",", files) + "]");
        mConn.scanFile(this.mContext, files, null, this);
    }

    @Override
    public void onScanCompleted(String path, final Uri uri) {
//        if ( Constants.DEBUG ) Log.d(TAG, "scanCompleted(): path=" + path + ", uri=" + uri.toString());
        mConn.disconnect();
        final String contentType = this.mFileContentTypes.get(path);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getThumbnail(uri, contentType);
            }
        }).start();
    }

    private Bitmap getThumbnail(Uri uri, String mimeType) {
//        if ( Constants.DEBUG ) Log.d(TAG, "getThumbnail(): uri=" + uri.toString() + ", mimeType=" + mimeType);
        if ( uri == null || mimeType == null ) {
            return null;
        }

        Bitmap bmp = null;
        long imageId = LocalFileUtils.getMediaIdFromUri(mContext, uri);
//        if ( Constants.DEBUG ) Log.d(TAG, "getThumbnail(): imageId=" + imageId);

        if ( mimeType.startsWith("image") ) {
            bmp = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), imageId, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        } else if ( mimeType.startsWith("video") ) {
            bmp = MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(), imageId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
        }

//        if ( Constants.DEBUG ) Log.d(TAG, "getThumbnail(): " + (bmp == null ? "bmp=null" : "bmp.size="+bmp.getByteCount()));
        return bmp;
    }

}
