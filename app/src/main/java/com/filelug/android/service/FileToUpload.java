package com.filelug.android.service;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.filelug.android.MainApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

class FileToUpload implements Parcelable {

    private String path = null;
    private String fileName = null;
    private String cacheFileName = null;
    private String contentType = null;
    private long fileSize = 0L;
    private long cacheFileSize = 0L;
    private boolean isResume = false;
    private Uri uri = null;
    private File file = null;
    private File cacheFile = null;

    /**
     * Create a new {@link FileToUpload} object.
     * 
     * @param path absolute path to the file
     * @param fileName file name
     * @param cacheFileName cache file name
     * @param contentType content type of the file to send
     * @param size file content size
     * @param isResume is resume to upload file
     * @param cacheFileSize cache file size
     */
    public FileToUpload(String path, String fileName, String cacheFileName, String contentType, long size, boolean isResume, long cacheFileSize) {
        init(path, fileName, cacheFileName, contentType, size, isResume, cacheFileSize);
    }

    private void init(final String path, final String fileName, final String cacheFileName, final String contentType, final long size, final boolean isResume, final long cacheFileSize) {
        this.path = path;
        if ( path.startsWith("content") ) {
            this.uri = Uri.parse(path);
            if ( TextUtils.isEmpty(fileName) ) {
                this.fileName = path;
            } else {
                this.fileName = fileName;
            }
            this.fileSize = size;
            this.cacheFileName = cacheFileName;
            if ( !TextUtils.isEmpty(cacheFileName) ) {
                this.cacheFile = new File(cacheFileName);
            }
        } else {
            this.file = new File(path);
            if ( TextUtils.isEmpty(fileName) ) {
                this.fileName = this.file.getName();
            } else {
                this.fileName = fileName;
            }
            if ( size == 0l ) {
                this.fileSize = this.file.length();
            } else {
                this.fileSize = size;
            }
        }
        this.contentType = contentType;
        this.isResume = isResume;
        this.cacheFileSize = cacheFileSize;
    }

    public final InputStream getStream() throws FileNotFoundException {
        InputStream is = null;
        if (this.file != null) {
            is = new FileInputStream(this.file);
        } else if (this.uri != null) {
            if (this.cacheFile == null) {
                Context context = MainApplication.getInstance().getApplicationContext();
                is = context.getContentResolver().openInputStream(uri);
            } else {
                is = new FileInputStream(this.cacheFile);
            }
        }
        return is;
    }

    public long getFileSize() {
        return fileSize;
    }

    public boolean isResume() {
        return isResume;
    }

    public long getCacheFileSize() {
        return cacheFileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public String getCacheFileName() {
        return cacheFileName;
    }

    public String getContentType() {
        return contentType;
    }

    // This is used to regenerate the object.
    // All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<FileToUpload> CREATOR = new Creator<FileToUpload>() {
        @Override
        public FileToUpload createFromParcel(final Parcel in) {
            return new FileToUpload(in);
        }

        @Override
        public FileToUpload[] newArray(final int size) {
            return new FileToUpload[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int arg1) {
        parcel.writeString(this.path);
        parcel.writeString(this.fileName);
        parcel.writeString(this.cacheFileName);
        parcel.writeString(this.contentType);
        parcel.writeLong(this.fileSize);
        parcel.writeInt(this.isResume ? 1 : 0);
        parcel.writeLong(this.cacheFileSize);
    }

    private FileToUpload(Parcel in) {
        String p1 = in.readString();
        String p2 = in.readString();
        String p3 = in.readString();
        String p4 = in.readString();
        long p5 = in.readLong();
        boolean p6 = in.readInt() == 1 ? true : false;
        long p7 = in.readLong();
        init(p1, p2, p3, p4, p5, p6, p7);
    }

}
