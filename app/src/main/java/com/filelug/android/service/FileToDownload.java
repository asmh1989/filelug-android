package com.filelug.android.service;

import android.os.Parcel;
import android.os.Parcelable;

import com.filelug.android.util.FormatUtils;

/**
 * Created by Vincent Chang on 2015/10/7.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class FileToDownload implements Parcelable {

    private String filePath = null;
    private String fileName = null;
    private String fullName = null;
    private String fileRealPath = null;
    private String fileRealName = null;
    private String realFullName = null;
    private long fileSize = 0L;
    private long lastModified = 0L;
    private String localDir = null;
    private boolean isResume = false;
    private long cacheFileSize = 0L;

    /**
     * Create a new {@link FileToDownload} object.
     *
     * @param filePath Parent path to the file that you want to download
     * @param fileName File name seen by the server side script
     * @param fullName Absolute path to the file that you want to download
     * @param fileRealPath Parent real path to the file that you want to download
     * @param fileRealName File real name seen by the server side script
     * @param realFullName Absolute real path to the file that you want to download
     * @param fileSize Content length of the file
     * @param lastModifiedStr Last modified date of the file
     * @param localDir Save to directory
     * @param isResume is resume to upload file
     * @param cacheFileSize File size of the cache file
     */
    public FileToDownload(final String filePath, final String fileName, final String fullName, final String fileRealPath, final String fileRealName, final String realFullName, final long fileSize, final String lastModifiedStr, final String localDir, boolean isResume, long cacheFileSize) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.fullName = fullName;
        this.fileRealPath = fileRealPath;
        this.fileRealName = fileRealName;
        this.realFullName = realFullName;
        this.fileSize = fileSize;
        this.lastModified = FormatUtils.convertRemoteFileLastModifiedToTimestamp(null, lastModifiedStr);
        this.localDir = localDir;
        this.isResume = isResume;
        this.cacheFileSize = cacheFileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFileRealPath() {
        return fileRealPath;
    }

    public String getFileRealName() {
        return fileRealName;
    }

    public String getRealFullName() {
        return realFullName;
    }

    public long length() {
        return fileSize;
    }

    public long getLastModified() {
        return lastModified;
    }

    public String getLocalDir() {
        return localDir;
    }

    public boolean isResume() {
        return isResume;
    }

    public long getCacheFileSize() {
        return cacheFileSize;
    }

    // This is used to regenerate the object.
    // All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<FileToDownload> CREATOR = new Creator<FileToDownload>() {
        @Override
        public FileToDownload createFromParcel(final Parcel in) {
            return new FileToDownload(in);
        }

        @Override
        public FileToDownload[] newArray(final int size) {
            return new FileToDownload[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int arg1) {
        parcel.writeString(filePath);
        parcel.writeString(fileName);
        parcel.writeString(fullName);
        parcel.writeString(fileRealPath);
        parcel.writeString(fileRealName);
        parcel.writeString(realFullName);
        parcel.writeLong(fileSize);
        parcel.writeLong(lastModified);
        parcel.writeString(localDir);
        parcel.writeInt(isResume ? 1 : 0);
        parcel.writeLong(cacheFileSize);
    }

    private FileToDownload(Parcel in) {
        filePath = in.readString();
        fileName = in.readString();
        fullName = in.readString();
        fileRealPath = in.readString();
        fileRealName = in.readString();
        realFullName = in.readString();
        fileSize = in.readLong();
        lastModified = in.readLong();
        localDir = in.readString();
        isResume = in.readInt() == 1 ? true : false;
        cacheFileSize = in.readLong();
    }

}
