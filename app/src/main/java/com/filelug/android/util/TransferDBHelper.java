package com.filelug.android.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.filelug.android.Constants;
import com.filelug.android.MainApplication;
import com.filelug.android.R;
import com.filelug.android.fileprovider.LocalFilesProvider;
import com.filelug.android.provider.FilelugSQLiteOpenHelper;
import com.filelug.android.provider.assetfile.AssetFileColumns;
import com.filelug.android.provider.assetfile.AssetFileContentValues;
import com.filelug.android.provider.assetfile.AssetFileCursor;
import com.filelug.android.provider.assetfile.AssetFileSelection;
import com.filelug.android.provider.assetfile.UploadStatusType;
import com.filelug.android.provider.downloadgroup.DownloadGroupColumns;
import com.filelug.android.provider.filetransfer.DownloadStatusType;
import com.filelug.android.provider.filetransfer.FileTransferColumns;
import com.filelug.android.provider.filetransfer.FileTransferContentValues;
import com.filelug.android.provider.filetransfer.FileTransferCursor;
import com.filelug.android.provider.filetransfer.FileTransferSelection;
import com.filelug.android.provider.remoteroot.RemoteRootContentValues;
import com.filelug.android.provider.remoteroot.RemoteRootCursor;
import com.filelug.android.provider.remoteroot.RemoteRootSelection;
import com.filelug.android.provider.remoteroot.RemoteRootType;
import com.filelug.android.provider.uploadgroup.UploadGroupColumns;
import com.filelug.android.provider.usercomputer.UserComputerColumns;
import com.filelug.android.provider.usercomputer.UserComputerContentValues;
import com.filelug.android.provider.usercomputer.UserComputerSelection;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Vincent Chang on 2016/2/7.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public class TransferDBHelper {

    private static final String TAG = TransferDBHelper.class.getSimpleName();
    private static Context ctx = MainApplication.getInstance().getApplicationContext();

    public static long getUploadRowId(String userId, int computerId, String groupId, String transferKey) {
//        if ( Constants.DEBUG ) Log.d(TAG, "getUploadRowId(): userId=" + userId + ", computerId=" + computerId + ", groupId=" + groupId + ", transferKey=" + transferKey);
        long rowId = -1L;

        String[] projection = new String[] { AssetFileColumns._ID };
        AssetFileSelection selection = new AssetFileSelection()
            .userId(userId).and()
            .computerId(computerId).and()
            .groupId(groupId).and()
            .transferKey(transferKey);
        AssetFileCursor c = selection.query(ctx.getContentResolver(), projection);
        if ( c.moveToFirst() ) {
            rowId = c.getId();
        }
        c.close();

        return rowId;
    }

    public static long getUploadRowId(String transferKey) {
//        if ( Constants.DEBUG ) Log.d(TAG, "getUploadRowId(): transferKey=" + transferKey);
        long rowId = -1;

        String[] projection = new String[] { AssetFileColumns._ID };
        AssetFileSelection selection = new AssetFileSelection()
            .transferKey(transferKey);
        AssetFileCursor c = selection.query(ctx.getContentResolver(), projection);
        if ( c.moveToFirst() ) {
            rowId = c.getId();
        }
        c.close();

        return rowId;
    }

    public static Map<Long, String> getUploadRowIdAndFileNames(String groupId) {
//        if ( Constants.DEBUG ) Log.d(TAG, "getUploadRowIdAndFileNames(): groupId=" + groupId);
        Map<Long, String> rows = new LinkedHashMap<Long, String>();

        String[] projection = new String[] { AssetFileColumns._ID, AssetFileColumns.SERVER_FILE_NAME };
        AssetFileSelection selection = new AssetFileSelection()
            .groupId(groupId);
        AssetFileCursor c = selection.query(ctx.getContentResolver(), projection);
        while (c.moveToNext()) {
            long rowId = c.getId();
            String fileName = c.getServerFileName();
            rows.put(rowId, fileName);
        }
        c.close();

        return rows;
    }

    public static void writeUploadPrepare(long rowId, String fileName, boolean isResume) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeUploadPrepare(): rowId=" + rowId + ", fileName=" + fileName + ", isResume=" + isResume);
        AssetFileSelection selection = new AssetFileSelection()
            .id(rowId);
        AssetFileContentValues values = new AssetFileContentValues()
            .putStatus(UploadStatusType.wait)
            .putWaitToConfirm(false);
        if ( !isResume ) {
            values.putStartTimestamp(new Date().getTime());
        }
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeUploadStart(long rowId, String fileName, long totalBytes, long transferredSize) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeUploadStart(): rowId=" + rowId + ", fileName=" + fileName + ", totalBytes=" + totalBytes + ", transferredSize=" + transferredSize);
        AssetFileSelection selection = new AssetFileSelection()
            .id(rowId);
        AssetFileContentValues values = new AssetFileContentValues()
            .putTransferredSize(transferredSize)
            .putStatus(UploadStatusType.processing);
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeUploadPingError(long rowId, String fileName, String errorMessage) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeUploadTransferError(): rowId=" + rowId + ", fileName=" + fileName + ", errorMessage=" + errorMessage);
        writeUploadStatus(rowId, UploadStatusType.failure.name());
    }

    public static void writeUploadProgressing(long rowId, String fileName, long transferredBytes) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeUploadProgressing(): rowId=" + rowId + ", fileName=" + fileName + ", transferredBytes=" + transferredBytes);
        AssetFileSelection selection = new AssetFileSelection()
            .id(rowId);
        AssetFileContentValues values = new AssetFileContentValues()
            .putTransferredSize(transferredBytes);
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeUploadTransferCompleted(long rowId, String fileName, long transferredBytes, int serverResponseCode, String serverResponseMessage) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeUploadTransferCompleted(): rowId=" + rowId + ", fileName=" + fileName + ", transferredBytes=" + transferredBytes + ", serverResponseCode=" + serverResponseCode + ", serverResponseMessage=" + serverResponseMessage);
        AssetFileSelection selection = new AssetFileSelection()
            .id(rowId);
        AssetFileContentValues values = new AssetFileContentValues()
            .putTransferredSize(transferredBytes)
            .putWaitToConfirm(true);
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeUploadTransferError(long rowId, String fileName, long transferredBytes, Exception exception) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeUploadTransferError(): rowId=" + rowId + ", fileName=" + fileName + ", exception=" + exception.getMessage());
        AssetFileSelection selection = new AssetFileSelection()
            .id(rowId);
        AssetFileContentValues values = new AssetFileContentValues()
            .putTransferredSize(transferredBytes)
            .putWaitToConfirm(false)
            .putStatus(UploadStatusType.failure)
            .putEndTimestamp(new Date().getTime());
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeUploadResponseError(long rowId, String fileName, long transferredBytes, int serverResponseCode, String serverResponseMessage) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeUploadResponseError(): rowId=" + rowId + ", fileName=" + fileName + ", serverResponseCode=" + serverResponseCode + ", serverResponseMessage=" + serverResponseMessage);
        AssetFileSelection selection = new AssetFileSelection()
            .id(rowId);
        AssetFileContentValues values = new AssetFileContentValues()
            .putTransferredSize(transferredBytes)
            .putWaitToConfirm(false)
            .putStatus(UploadStatusType.failure)
            .putEndTimestamp(new Date().getTime());
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeUploadCanceled(long rowId, String fileName, long transferredBytes) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeUploadCanceled(): rowId=" + rowId + ", fileName=" + fileName + ", transferredBytes=" + transferredBytes);
        UploadStatusType status = transferredBytes > 0l ? UploadStatusType.failure : UploadStatusType.canceling;
        AssetFileSelection selection = new AssetFileSelection()
            .id(rowId);
        AssetFileContentValues values = new AssetFileContentValues()
            .putWaitToConfirm(false)
            .putStatus(status)
            .putEndTimestamp(new Date().getTime());
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeUploadStatus(long rowId, String status) {
        AssetFileSelection selection = new AssetFileSelection()
            .id(rowId);
        AssetFileContentValues values = new AssetFileContentValues()
            .putWaitToConfirm(false)
            .putStatus(UploadStatusType.valueOf(status))
            .putEndTimestamp(new Date().getTime());
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeUploadFailedStatus(String userId, int computerId) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeUploadFailedStatus(): userId=" + userId + ", computerId=" + computerId);
        AssetFileSelection selection = new AssetFileSelection()
            .userId(userId).and()
            .computerId(computerId).and()
            .waitToConfirm(false).and()
            .status(UploadStatusType.processing);
        AssetFileContentValues values = new AssetFileContentValues()
            .putStatus(UploadStatusType.failure)
            .putWaitToConfirm(true);
        values.update(ctx.getContentResolver(), selection);
    }

    public static Map<String, Map<String, Object>> getUploadDetailsByRowId(long rowId) {
//        if ( Constants.DEBUG ) Log.d(TAG, "getUploadDetailsByRowId(): rowId=" + rowId);

        String tables = AssetFileColumns.TABLE_NAME + " a" +
                " LEFT OUTER JOIN " + UploadGroupColumns.TABLE_NAME + " b" +
                " ON b." + UploadGroupColumns.USER_ID + " = " + "a." + AssetFileColumns.USER_ID +
                " AND b." + UploadGroupColumns.COMPUTER_ID + " = " + "a." + AssetFileColumns.COMPUTER_ID +
                " AND b." + UploadGroupColumns.GROUP_ID + " = " + "a." + AssetFileColumns.GROUP_ID +
                " LEFT OUTER JOIN " + UserComputerColumns.TABLE_NAME + " c" +
                " ON c." + UserComputerColumns.USER_ID + " = " + "a." + AssetFileColumns.USER_ID +
                " AND c." + UserComputerColumns.COMPUTER_ID + " = " + "a." + AssetFileColumns.COMPUTER_ID;
        String[] columns = new String[] {
                "a." + AssetFileColumns.COMPUTER_ID,
                "c." + UserComputerColumns.COMPUTER_NAME,
                "a." + AssetFileColumns.ASSET_URL,
                "a." + AssetFileColumns.SERVER_FILE_NAME,
                "a." + AssetFileColumns.CONTENT_TYPE,
                "a." + AssetFileColumns.LAST_MODIFIED_TIMESTAMP,
                "a." + AssetFileColumns.STATUS,
                "a." + AssetFileColumns.START_TIMESTAMP,
                "a." + AssetFileColumns.END_TIMESTAMP,
                "a." + AssetFileColumns.TOTAL_SIZE,
                "a." + AssetFileColumns.TRANSFERRED_SIZE,
                "b." + UploadGroupColumns.UPLOAD_DIRECTORY,
                "b." + UploadGroupColumns.SUBDIRECTORY_TYPE,
                "b." + UploadGroupColumns.DESCRIPTION_TYPE,
                "b." + UploadGroupColumns.NOTIFICATION_TYPE
        };
        String selection = "a." + AssetFileColumns._ID + " = ? ";
        String[] selectionArgs = new String[] { String.valueOf(rowId) };

        SQLiteDatabase database = FilelugSQLiteOpenHelper.getInstance(ctx).getReadableDatabase();
        Cursor c = database.query(tables, columns, selection, selectionArgs, null, null, null, null);

        Map<String, Map<String, Object>> result = null;

        if ( c.moveToFirst() ) {
            int computerId = c.getInt(0);
            String computerName = c.getString(1);
            String assetPath = c.getString(2);
            String serverFileName = c.getString(3);
            String contentType = c.getString(4);
            long lastModifiedTimestamp = c.getLong(5);
            int status = c.getInt(6);
            long startTimestamp = c.getLong(7);
            long endTimestamp = c.getLong(8);
            long totalSize = c.getLong(9);
            long transferredSize = c.getLong(10);
            String uploadDirectory = c.getString(11);
            int subDirType = c.getInt(12);
            int descriptionType = c.getInt(13);
            int notificationType = c.getInt(14);
            String subDirTypeStr = ctx.getResources().getStringArray(R.array.subfolder_type_array)[subDirType];
            String descriptionTypeStr = ctx.getResources().getStringArray(R.array.description_type_array)[descriptionType];
            String notificationTypeStr = ctx.getResources().getStringArray(R.array.notification_type_array)[notificationType];
            String lastModifiedTimestampStr = FormatUtils.formatDate1(ctx, new Date(lastModifiedTimestamp));
            String statusStr = ctx.getResources().getStringArray(R.array.upload_status_array)[status];
            String startTimestampStr = FormatUtils.formatDate1(ctx, new Date(startTimestamp));
            String endTimestampStr = FormatUtils.formatDate1(ctx, new Date(endTimestamp));
            String totalSizeStr = FormatUtils.formatFileSize(ctx, totalSize);
            String transferredSizeStr = FormatUtils.formatFileSize(ctx, transferredSize);

            // 上傳資訊
            Map<String, Object> uploadInfo = new LinkedHashMap<String, Object>();

            uploadInfo.put(ctx.getResources().getString(R.string.label_upload_to_folder), uploadDirectory);
            uploadInfo.put(ctx.getResources().getString(R.string.label_file_name), serverFileName);
            uploadInfo.put(ctx.getResources().getString(R.string.label_upload_status), statusStr);
            uploadInfo.put(ctx.getResources().getString(R.string.label_start_upload_timestamp), startTimestampStr);
            uploadInfo.put(ctx.getResources().getString(R.string.label_end_upload_timestamp), endTimestampStr);
            uploadInfo.put(ctx.getResources().getString(R.string.fileItem_size), totalSizeStr);
            uploadInfo.put(ctx.getResources().getString(R.string.label_file_uploaded_size), transferredSizeStr);

            // 來源檔案資訊
            Map<String, Object> sourceFileInfo = new LinkedHashMap<String, Object>();

            sourceFileInfo.put(ctx.getResources().getString(R.string.label_upload_to_computer), computerName != null ? computerName : computerId);
            sourceFileInfo.put(ctx.getResources().getString(R.string.label_source_file_path), assetPath);
            sourceFileInfo.put(ctx.getResources().getString(R.string.label_content_type), contentType);
            sourceFileInfo.put(ctx.getResources().getString(R.string.fileItem_modified_date), lastModifiedTimestampStr);

            // 上傳摘要資訊
            Map<String, Object> uploadSummaryInfo = new LinkedHashMap<String, Object>();

            uploadSummaryInfo.put(ctx.getResources().getString(R.string.label_subfolder_type), subDirTypeStr);
            uploadSummaryInfo.put(ctx.getResources().getString(R.string.label_upload_description_type), descriptionTypeStr);
            uploadSummaryInfo.put(ctx.getResources().getString(R.string.label_upload_notification_type), notificationTypeStr);

            result = new LinkedHashMap<String, Map<String, Object>>();
            result.put(ctx.getResources().getString(R.string.label_upload_info), uploadInfo);
            result.put(ctx.getResources().getString(R.string.label_source_file_info), sourceFileInfo);
            result.put(ctx.getResources().getString(R.string.label_upload_summary_info), uploadSummaryInfo);

        }
        c.close();

        return result;
    }

    public static void writeUploadCancelStatus(long rowId) {
        AssetFileSelection selection = new AssetFileSelection()
            .id(rowId).and()
            .status(UploadStatusType.wait);
        AssetFileContentValues values = new AssetFileContentValues()
            .putStatus(UploadStatusType.canceling);
        values.update(ctx.getContentResolver(), selection);
    }

    public static long getDownloadRowId(String userId, int computerId, String groupId, String transferKey) {
//        if ( Constants.DEBUG ) Log.d(TAG, "getDownloadRowId(): userId=" + userId + ", computerId=" + computerId + ", groupId=" + groupId + ", transferKey=" + transferKey);
        long rowId = -1L;

        String[] projection = new String[] { FileTransferColumns._ID };
        FileTransferSelection selection = new FileTransferSelection()
            .userId(userId).and()
            .computerId(computerId).and()
            .groupId(groupId).and()
            .transferKey(transferKey);
        FileTransferCursor c = selection.query(ctx.getContentResolver(), projection);
        if ( c.moveToFirst() ) {
            rowId = c.getId();
        }
        c.close();

        return rowId;
    }

    public static Bundle getDownloadRowIdAndFileNames(String groupId) {
//        if ( Constants.DEBUG ) Log.d(TAG, "getAllSuccessDownloadedFilesInfo(): groupId=" + groupId);
        List<String> rows = new LinkedList<String>();
        String message = null;

        String[] projection = new String[] { FileTransferColumns._ID, FileTransferColumns.STATUS, FileTransferColumns.LOCAL_FILE_NAME };
        FileTransferSelection selection = new FileTransferSelection()
            .groupId(groupId);
        FileTransferCursor c = selection.query(ctx.getContentResolver(), projection);

        boolean allSuccess = true;
        String firstFileName = null;
        while (c.moveToNext()) {
            long rowId = c.getId();
            DownloadStatusType status = c.getStatus();
            if ( !DownloadStatusType.success.equals(status) ) {
                allSuccess = false;
                break;
            }
            String fileName = c.getLocalFileName();
            rows.add(rowId + "," + fileName);
            if ( firstFileName == null ) {
                firstFileName = fileName;
            }
        }
        c.close();

        if ( !allSuccess || rows.size() <= 0 ) {
            return null;
        }

        String[] rowArray = rows.toArray(new String[0]);
        if ( rows.size() == 1 ) {
            message = String.format(ctx.getString(R.string.message_notify_file_download_success), firstFileName);
        } else {
            message = String.format(ctx.getString(R.string.message_notify_all_files_downloaded_successfully), rows.size(), firstFileName);
        }

        Bundle result = new Bundle();
        result.putStringArray("idAndFileNames", rowArray);
        result.putString("message", message);

        return result;
    }

    public static void writeDownloadPrepare(long rowId, String fileName, boolean isResume) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeDownloadPrepare(): rowId=" + rowId + ", fileName=" + fileName);
        FileTransferSelection selection = new FileTransferSelection()
            .id(rowId);
        FileTransferContentValues values = new FileTransferContentValues()
            .putSavedFileNameNull()
            .putStatus(DownloadStatusType.wait)
            .putWaitToConfirm(false);
        if ( !isResume ) {
            values.putStartTimestamp(new Date().getTime());
        }
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeDownloadStart(long rowId, String fileName, long totalBytes, long transferredSize) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeDownloadStart(): rowId=" + rowId + ", fileName=" + fileName + ", totalBytes=" + totalBytes + ", transferredSize=" + transferredSize);
        FileTransferSelection selection = new FileTransferSelection()
            .id(rowId);
        FileTransferContentValues values = new FileTransferContentValues()
            .putTransferredSize(transferredSize)
            .putStatus(DownloadStatusType.processing);
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeDownloadPingError(long rowId, String fileName, String errorMessage) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeDownloadTransferError(): rowId=" + rowId + ", fileName=" + fileName + ", errorMessage=" + errorMessage);
        writeDownloadStatus(rowId, DownloadStatusType.failure.name());
    }

    public static void writeDownloadProgressing(long rowId, String fileName, long transferredBytes) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeDownloadProgressing(): rowId=" + rowId + ", fileName=" + fileName + ", transferredBytes=" + transferredBytes);
        FileTransferSelection selection = new FileTransferSelection()
            .id(rowId);
        FileTransferContentValues values = new FileTransferContentValues()
            .putTransferredSize(transferredBytes);
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeDownloadTransferCompleted(long rowId, String fileName, String savedFileName, long transferredBytes, int serverResponseCode, String serverResponseMessage) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeDownloadTransferCompleted(): rowId=" + rowId + ", fileName=" + fileName + ", transferredBytes=" + transferredBytes + ", serverResponseCode=" + serverResponseCode + ", serverResponseMessage=" + serverResponseMessage);
        FileTransferSelection selection = new FileTransferSelection()
            .id(rowId);
        FileTransferContentValues values = new FileTransferContentValues()
            .putTransferredSize(transferredBytes)
            .putWaitToConfirm(true)
            .putStatus(DownloadStatusType.success);
        if ( !TextUtils.isEmpty(savedFileName) && !savedFileName.equals(fileName) ) {
            values.putSavedFileName(savedFileName);
        }
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeDownloadTransferError(long rowId, String fileName, long transferredBytes, Exception exception) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeDownloadTransferError(): rowId=" + rowId + ", fileName=" + fileName + ", exception=" + exception.getMessage());
        FileTransferSelection selection = new FileTransferSelection()
            .id(rowId);
        FileTransferContentValues values = new FileTransferContentValues()
            .putTransferredSize(transferredBytes)
            .putWaitToConfirm(true)
            .putStatus(DownloadStatusType.failure);
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeDownloadResponseError(long rowId, String fileName, long transferredBytes, int serverResponseCode, String serverResponseMessage) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeDownloadResponseError(): rowId=" + rowId + ", fileName=" + fileName + ", serverResponseCode=" + serverResponseCode + ", serverResponseMessage=" + serverResponseMessage);
        FileTransferSelection selection = new FileTransferSelection()
            .id(rowId);
        FileTransferContentValues values = new FileTransferContentValues()
            .putTransferredSize(transferredBytes)
            .putWaitToConfirm(true)
            .putStatus(DownloadStatusType.failure);
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeDownloadCanceled(long rowId, String fileName, long transferredBytes) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeDownloadCanceled(): rowId=" + rowId + ", fileName=" + fileName + ", transferredBytes=" + transferredBytes);
        FileTransferSelection selection = new FileTransferSelection()
            .id(rowId);
        FileTransferContentValues values = null;
        if ( transferredBytes > 0l ) {
            values = new FileTransferContentValues()
                .putTransferredSize(transferredBytes)
                .putWaitToConfirm(true)
                .putStatus(DownloadStatusType.failure);
        } else {
            values = new FileTransferContentValues()
                .putWaitToConfirm(false)
                .putStatus(DownloadStatusType.canceling)
                .putEndTimestamp(new Date().getTime());
        }
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeDownloadConfirmed(long rowId) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeDownloadConfirmed(): rowId=" + rowId);
        FileTransferSelection selection = new FileTransferSelection()
            .id(rowId);
        FileTransferContentValues values = new FileTransferContentValues()
            .putWaitToConfirm(false)
            .putEndTimestamp(new Date().getTime());
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeDownloadStatus(long rowId, String status) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeDownloadStatus(): rowId=" + rowId + ", status=" + status);
        FileTransferSelection selection = new FileTransferSelection()
            .id(rowId);
        FileTransferContentValues values = new FileTransferContentValues()
            .putWaitToConfirm(false)
            .putStatus(DownloadStatusType.valueOf(status))
            .putEndTimestamp(new Date().getTime());
        values.update(ctx.getContentResolver(), selection);
    }

    public static void writeDownloadFailedStatus(String userId, int computerId) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeDownloadFailedStatus(): userId=" + userId + ", computerId=" + computerId);
        FileTransferSelection selection = new FileTransferSelection()
            .userId(userId).and()
            .computerId(computerId).and()
            .waitToConfirm(false).and()
            .status(DownloadStatusType.processing);
        FileTransferContentValues values = new FileTransferContentValues()
            .putStatus(DownloadStatusType.failure)
            .putWaitToConfirm(true);
        values.update(ctx.getContentResolver(), selection);
    }

    public static Bundle getDownloadFileInfoByRowId(long rowId) {
//        if ( Constants.DEBUG ) Log.d(TAG, "getDownloadFileInfoByRowId(): rowId=" + rowId);
        String localDir = null;
        String fileName = null;
        String savedFileName = null;
        String contentType = null;

        String tables = DownloadGroupColumns.TABLE_NAME + " a, " + FileTransferColumns.TABLE_NAME + " b";
        String[] columns = new String[] {
            "a." + DownloadGroupColumns.LOCAL_PATH,
            "b." + FileTransferColumns.LOCAL_FILE_NAME,
            "b." + FileTransferColumns.SAVED_FILE_NAME,
            "b." + FileTransferColumns.CONTENT_TYPE
        };
        String selection = "a." + DownloadGroupColumns.USER_ID + " = " + "b." + FileTransferColumns.USER_ID + " AND " +
            "a." + DownloadGroupColumns.COMPUTER_ID + " = " + "b." + FileTransferColumns.COMPUTER_ID + " AND " +
            "a." + DownloadGroupColumns.GROUP_ID + " = " + "b." + FileTransferColumns.GROUP_ID + " AND " +
            "b." + DownloadGroupColumns._ID + " = ? ";
        String[] selectionArgs = new String[] { String.valueOf(rowId) };

        SQLiteDatabase database = FilelugSQLiteOpenHelper.getInstance(ctx).getReadableDatabase();
        Cursor c = database.query(tables, columns, selection, selectionArgs, null, null, null, null);

        if ( c.moveToFirst() ) {
            localDir = c.getString(0);
            fileName = c.getString(1);
            savedFileName = c.getString(2);
            contentType = c.getString(3);
        }
        c.close();

        Bundle result = new Bundle();
        if ( localDir != null ) result.putString(DownloadGroupColumns.LOCAL_PATH, localDir);
        if ( fileName != null ) result.putString(FileTransferColumns.LOCAL_FILE_NAME, fileName);
        if ( savedFileName != null ) result.putString(FileTransferColumns.SAVED_FILE_NAME, savedFileName);
        if ( contentType != null ) result.putString(FileTransferColumns.CONTENT_TYPE, contentType);

        return result.size() > 0 ? result : null;
    }

    public static Bundle getDownloadFileUris(String userId, int computerId, String groupId) {
//        if ( Constants.DEBUG ) Log.d(TAG, "getDownloadFilesByGroup(): userId=" + userId + ", computerId=" + computerId + ", groupId=" + groupId);

        String[] projection = new String[] {
            FileTransferColumns.DG_LOCAL_PATH_WITH_ALIAS,
            FileTransferColumns.REAL_SERVER_PATH,
            FileTransferColumns.REAL_LOCAL_FILE_NAME,
            FileTransferColumns.LOCAL_FILE_NAME,
            FileTransferColumns.SAVED_FILE_NAME,
            FileTransferColumns.CONTENT_TYPE,
            FileTransferColumns.STATUS
        };
        FileTransferSelection selection = new FileTransferSelection()
            .userId(userId).and()
            .computerId(computerId).and()
            .groupId(groupId);
        FileTransferCursor c = selection.query(ctx.getContentResolver(), projection);

        List<Uri> uris = new LinkedList<Uri>();
        List<String> mimeTypes = new LinkedList<String>();
        List<String> errors = new LinkedList<String>();
        while( c.moveToNext() ) {
            String localDir = c.getDGLocalPath();
            String realFullName = c.getRealServerPath();
            String realFileName = c.getRealLocalFileName();
            String fileName = c.getLocalFileName();
            String savedFileName = c.getSavedFileName();
            String contentType = c.getContentType();
            DownloadStatusType status = c.getStatus();

            if ( status == DownloadStatusType.success ) {
                int index = realFullName.lastIndexOf(realFileName);
                String realFilePath = realFullName.substring(0, index-1);

                String cacheDir = FileCache.createDirInActiveAccountCache(realFilePath);
                String filePath = cacheDir + File.separator + ( savedFileName != null ? savedFileName : ( realFileName != null ? realFileName : fileName ) );
                File downloadFile = new File(filePath);
                Uri fileUri = LocalFilesProvider.getUriForFile(ctx, LocalFilesProvider.AUTHORITIES_NAME, downloadFile);
                uris.add(fileUri);
                if ( !mimeTypes.contains(contentType) ) {
                    mimeTypes.add(contentType);
                }
            } else {
                errors.add(fileName);
            }
        }
        c.close();

        Bundle result = new Bundle();
        result.putParcelableArray(Constants.EXT_PARAM_OFF_RESULT_URIS, uris.toArray(new Uri[0]));
        result.putStringArray(Constants.EXT_PARAM_OFF_RESULT_MIME_TYPES, mimeTypes.toArray(new String[0]));
        result.putStringArray(Constants.EXT_PARAM_OFF_RESULT_ERRORS, errors.toArray(new String[0]));

        return result;
    }

    public static boolean isGroupFileDownloading(String userId, int computerId, String groupId) {
//        if ( Constants.DEBUG ) Log.d(TAG, "isGroupFileDownloading(), userId=" + userId + ", computerId=" + computerId + ", groupId=" + groupId);

        boolean result = false;

        String[] projection = new String[] { FileTransferColumns._ID, FileTransferColumns.STATUS, FileTransferColumns.WAIT_TO_CONFIRM };
        FileTransferSelection selection = new FileTransferSelection()
            .userId(userId).and()
            .computerId(computerId).and()
            .groupId(groupId).and()
            .openParen()
                .status(DownloadStatusType.wait).or()
                .status(DownloadStatusType.processing).or()
                .waitToConfirm(true)
            .closeParen();
        FileTransferCursor c = selection.query(ctx.getContentResolver(), projection);
        if ( c.moveToFirst() ) {
            String msg = ", status=" + c.getStatus().toString() + ", waitToConfirm=" + c.getWaitToConfirm();
//            if ( Constants.DEBUG ) Log.d(TAG, "isGroupFileDownloading(): groupId=" + groupId + msg);
            result = true;
        }
        c.close();

//        if ( Constants.DEBUG ) Log.d(TAG, "isGroupFileDownloading(): result=" + result);

        return result;
    }

    public static Map<String, Map<String, Object>> getDownloadDetailsByRowId(long rowId) {
//        if ( Constants.DEBUG ) Log.d(TAG, "getDownloadDetailsByRowId(): rowId=" + rowId);

        String tables = FileTransferColumns.TABLE_NAME + " a" +
                        " LEFT OUTER JOIN " + DownloadGroupColumns.TABLE_NAME + " b" +
                        " ON b." + DownloadGroupColumns.USER_ID + " = " + "a." + FileTransferColumns.USER_ID +
                        " AND b." + DownloadGroupColumns.COMPUTER_ID + " = " + "a." + FileTransferColumns.COMPUTER_ID +
                        " AND b." + DownloadGroupColumns.GROUP_ID + " = " + "a." + FileTransferColumns.GROUP_ID +
                        " LEFT OUTER JOIN " + UserComputerColumns.TABLE_NAME + " c" +
                        " ON c." + UserComputerColumns.USER_ID + " = " + "a." + FileTransferColumns.USER_ID +
                        " AND c." + UserComputerColumns.COMPUTER_ID + " = " + "a." + FileTransferColumns.COMPUTER_ID;
        String[] columns = new String[] {
            "a." + FileTransferColumns.COMPUTER_ID,
            "c." + UserComputerColumns.COMPUTER_NAME,
            "a." + FileTransferColumns.SERVER_PATH,
            "a." + FileTransferColumns.REAL_SERVER_PATH,
            "a." + FileTransferColumns.LOCAL_FILE_NAME,
            "a." + FileTransferColumns.REAL_LOCAL_FILE_NAME,
            "a." + FileTransferColumns.SAVED_FILE_NAME,
            "a." + FileTransferColumns.CONTENT_TYPE,
            "a." + FileTransferColumns.LAST_MODIFIED,
            "a." + FileTransferColumns.STATUS,
            "a." + FileTransferColumns.START_TIMESTAMP,
            "a." + FileTransferColumns.END_TIMESTAMP,
            "a." + FileTransferColumns.TOTAL_SIZE,
            "a." + FileTransferColumns.TRANSFERRED_SIZE,
            "b." + DownloadGroupColumns.LOCAL_PATH,
            "b." + DownloadGroupColumns.SUBDIRECTORY_TYPE,
            "b." + DownloadGroupColumns.DESCRIPTION_TYPE,
            "b." + DownloadGroupColumns.NOTIFICATION_TYPE
        };
        String selection = "a." + FileTransferColumns._ID + " = ? ";
        String[] selectionArgs = new String[] { String.valueOf(rowId) };

        SQLiteDatabase database = FilelugSQLiteOpenHelper.getInstance(ctx).getReadableDatabase();
        Cursor c = database.query(tables, columns, selection, selectionArgs, null, null, null, null);

        Map<String, Map<String, Object>> result = null;

        if ( c.moveToFirst() ) {
            int computerId = c.getInt(0);
            String computerName = c.getString(1);
            String serverPath = c.getString(2);
            String realServerPath = c.getString(3);
            String localFileName = c.getString(4);
            String realLocalFileName = c.getString(5);
            String savedFileName = c.getString(6);
            String contentType = c.getString(7);
            String lastModifiedDate = c.getString(8);
            int status = c.getInt(9);
            long startTimestamp = c.getLong(10);
            long endTimestamp = c.getLong(11);
            long totalSize = c.getLong(12);
            long transferredSize = c.getLong(13);
            String localPath = c.getString(14);
            int subDirType = c.getInt(15);
            int descriptionType = c.getInt(16);
            int notificationType = c.getInt(17);
            String subDirTypeStr = ctx.getResources().getStringArray(R.array.subfolder_type_array)[subDirType];
            String descriptionTypeStr = ctx.getResources().getStringArray(R.array.description_type_array)[descriptionType];
            String notificationTypeStr = ctx.getResources().getStringArray(R.array.notification_type_array)[notificationType];
            String statusStr = ctx.getResources().getStringArray(R.array.download_status_array)[status];
            String startTimestampStr = FormatUtils.formatDate1(ctx, new Date(startTimestamp));
            String endTimestampStr = FormatUtils.formatDate1(ctx, new Date(endTimestamp));
            String totalSizeStr = FormatUtils.formatFileSize(ctx, totalSize);
            String transferredSizeStr = FormatUtils.formatFileSize(ctx, transferredSize);

            // 下載資訊
            Map<String, Object> downloadInfo = new LinkedHashMap<String, Object>();

            downloadInfo.put(ctx.getResources().getString(R.string.label_download_to_folder), localPath);
            String saveAsFileName = null;
            if ( !TextUtils.isEmpty(savedFileName) ) {
                saveAsFileName = savedFileName;
            } else {
                if ( !TextUtils.isEmpty(realLocalFileName) && !TextUtils.equals(realLocalFileName,localFileName) ) {
                    saveAsFileName = realLocalFileName;
                } else {
                    saveAsFileName = localFileName;
                }
            }
            downloadInfo.put(ctx.getResources().getString(R.string.label_file_name), saveAsFileName);
            downloadInfo.put(ctx.getResources().getString(R.string.label_download_status), statusStr);
            downloadInfo.put(ctx.getResources().getString(R.string.label_start_download_timestamp), startTimestampStr);
            downloadInfo.put(ctx.getResources().getString(R.string.label_end_download_timestamp), endTimestampStr);
            downloadInfo.put(ctx.getResources().getString(R.string.fileItem_size), totalSizeStr);
            downloadInfo.put(ctx.getResources().getString(R.string.label_file_downloaded_size), transferredSizeStr);

            // 來源檔案資訊
            Map<String, Object> sourceFileInfo = new LinkedHashMap<String, Object>();

            sourceFileInfo.put(ctx.getResources().getString(R.string.label_download_from_computer), computerName != null ? computerName : computerId);
            sourceFileInfo.put(ctx.getResources().getString(R.string.label_source_file_path), serverPath);
            if ( !TextUtils.equals(serverPath, realServerPath) && !TextUtils.isEmpty(realServerPath) ) {
                sourceFileInfo.put(ctx.getResources().getString(R.string.label_real_source_file_path), realServerPath);
            }
            sourceFileInfo.put(ctx.getResources().getString(R.string.label_content_type), contentType);
            sourceFileInfo.put(ctx.getResources().getString(R.string.fileItem_modified_date), lastModifiedDate);

            // 下載摘要資訊
            Map<String, Object> downloadSummaryInfo = new LinkedHashMap<String, Object>();

            downloadSummaryInfo.put(ctx.getResources().getString(R.string.label_subfolder_type), subDirTypeStr);
            downloadSummaryInfo.put(ctx.getResources().getString(R.string.label_download_description_type), descriptionTypeStr);
            downloadSummaryInfo.put(ctx.getResources().getString(R.string.label_download_notification_type), notificationTypeStr);

            result = new LinkedHashMap<String, Map<String, Object>>();
            result.put(ctx.getResources().getString(R.string.label_download_info), downloadInfo);
            result.put(ctx.getResources().getString(R.string.label_source_file_info), sourceFileInfo);
            result.put(ctx.getResources().getString(R.string.label_download_summary_info), downloadSummaryInfo);

        }
        c.close();

        return result;
    }

    public static void writeDownloadCancelStatus(long rowId) {
//        if ( Constants.DEBUG ) Log.d(TAG, "writeDownloadCancelStatus(): rowId=" + rowId);
        FileTransferSelection selection = new FileTransferSelection()
            .id(rowId).and()
            .status(DownloadStatusType.wait);
        FileTransferContentValues values = new FileTransferContentValues()
            .putStatus(DownloadStatusType.canceling);
        values.update(ctx.getContentResolver(), selection);
    }

    public static void createOrUpdateUserComputer(String userId, int computerId, String userComputerId, String computerGroup, String computerName, String computerAdminId, String lugServerId) {
        UserComputerSelection selection = new UserComputerSelection()
            .userId(userId).and()
            .computerId(computerId);
        UserComputerContentValues values = new UserComputerContentValues()
            .putUserComputerId(userComputerId)
            .putComputerGroup(computerGroup)
            .putComputerName(computerName)
            .putComputerAdminId(computerAdminId)
            .putLugServerId(lugServerId);
        int updatedCount = values.update(ctx.getContentResolver(), selection);
        if ( updatedCount == 0 ) {
            values.putUserId(userId).putComputerId(computerId);
            values.insert(ctx.getContentResolver());
//            if (Constants.DEBUG) Log.d(TAG, "createOrUpdateUserComputer(), Insert new UserComputer!");
        } else {
//            if (Constants.DEBUG) Log.d(TAG, "createOrUpdateUserComputer(), Update UserComputer!");
        }
    }

    public static void createOrUpdateRemoteRoot(String userId, int computerId, String path, String realPath, String label, String type) {
        RemoteRootSelection remoteRootSelection = new RemoteRootSelection()
            .userId(userId).and()
            .computerId(computerId).and()
            .path(path).and()
            .realPath(realPath);
        RemoteRootContentValues values = new RemoteRootContentValues()
            .putLabel(label)
            .putType(RemoteRootType.valueOf(type));
        int updatedCount = values.update(ctx.getContentResolver(), remoteRootSelection);
        if ( updatedCount == 0 ) {
            values.putUserId(userId)
                .putComputerId(computerId)
                .putPath(path)
                .putRealPath(realPath);
            values.insert(ctx.getContentResolver());
//            if (Constants.DEBUG) Log.d(TAG, "createOrUpdateRemoteRoot(), Insert new RemoteRoot!");
        } else {
//            if (Constants.DEBUG) Log.d(TAG, "createOrUpdateRemoteRoot(), Update RemoteRoot!");
        }
    }

    public static void checkRemovedRemoteRoot(String userId, int computerId, List<String> rootDirList, String separator) {
//        if ( Constants.DEBUG ) Log.d(TAG, "checkRemovedRemoteRoot(), Delete RemoteRoot! userId=" + userId + ", computerId=" + computerId);
        RemoteRootSelection remoteRootSelection2 = new RemoteRootSelection()
            .userId(userId).and()
            .computerId(computerId);
        RemoteRootCursor c2 = remoteRootSelection2.query(ctx.getContentResolver());
        while (c2.moveToNext()) {
            long id = c2.getId();
            String _path = c2.getPath();
            String _realPath = c2.getRealPath();
            String logStr = "checkRemovedRemoteRoot, path=" + _path + ", realPath=" + _realPath;
            if ( !rootDirList.contains(_path + separator + _realPath) ) {
                RemoteRootSelection deleteSelection = new RemoteRootSelection()
                    .id(id);
                deleteSelection.delete(ctx.getContentResolver());
                logStr += ", Removed!";
            }
//            if ( Constants.DEBUG ) Log.d(TAG, logStr);
        }
        c2.close();
    }

}
