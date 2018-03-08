package com.filelug.android.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.provider.assetfile.UploadStatusType;
import com.filelug.android.ui.activity.MainActivity;

import java.util.Map;

/**
 * Created by Vincent Chang on 2015/8/13.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class NotificationUtils {

	private static final String TAG = NotificationUtils.class.getSimpleName();

	public static final String NOTIFICATION_TAG = "Filelug";

	private static int summary_notification_id = -1;

	private static int getSummaryNotificationId() {
		summary_notification_id++;
		summary_notification_id = (int)(summary_notification_id % 999999);
//		if ( Constants.DEBUG ) Log.d(TAG, "getSummaryNotificationId(), summary_notification_id=" + summary_notification_id);
		return summary_notification_id;
	}

	private static int getNotificationId(boolean isUpload, long rowId) {
		long num = rowId * 2 + (isUpload ? 1000001 : 1000000);
		int notificationId = (int)(num % 2147483647);
//		if ( Constants.DEBUG ) Log.d(TAG, "getNotificationId(), isUpload=" + isUpload + ", rowId=" + rowId + ", notificationId=" + notificationId);
		return notificationId;
	}

	private static NotificationCompat.Builder getBuilder(Context context) {
		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		// 建立 Builder 物件
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

		//通知效果設定 1

		// 準備設定通知效果用的變數
		int defaults = 0;
		// 加入震動效果
		//defaults |= Notification.DEFAULT_VIBRATE;
		// 加入音效效果
		//defaults |= Notification.DEFAULT_SOUND;
		// 加入閃燈效果
		defaults |= Notification.DEFAULT_LIGHTS;
		// 設定通知效果
		builder.setDefaults(defaults);

		//通知效果設定 2

		// 建立震動效果，陣列中元素依序為停止、震動的時間，單位是毫秒
		//long[] vibrate_effect = {1000, 500, 1000, 400, 1000, 300, 1000, 200, 1000, 100};
		//builder.setVibrate(vibrate_effect);
		// 建立音效效果，放在res/raw下的音效檔
		//Uri sound_effect = Uri.parse( "android.resource://" + context.getPackageName() + "/raw/zeta");
		//builder.setSound(sound_effect);
		// 設定閃燈效果，參數依序為顏色、打開與關閉時間，單位是毫秒
		//builder.setLights(Color.GREEN, 1000, 1000);

		builder.setWhen(System.currentTimeMillis());

		if ( android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
			builder.setColor(context.getResources().getColor(R.color.material_filelug_blue_500));
		}

		return builder;
	}

	public static void noticePrepareToUploadFile(Context context, long rowId, String fileName) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticePrepareToUploadFile(), fileName=" + fileName);
		String contentTitle = context.getResources().getString(R.string.message_file_prepare_to_upload_1);
		String contentText = String.format(context.getResources().getString(R.string.message_file_prepare_to_upload_2), fileName);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.notification_ic_uploading0)
			.setOngoing(true)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setProgress(100, 0, true);
//			.setContentIntent(notificationConfig.getPendingIntent(this))

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		// 發出通知
		int notificationId = getNotificationId(true, rowId);
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void noticeFileUploadPingError(Context context, long rowId, String fileName, String errorMessage) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeFileUploadPingError(), fileName=" + fileName);
		String contentTitle = String.format(context.getResources().getString(R.string.message_file_upload_ping_error_1), fileName);
		String contentText = context.getResources().getString(R.string.message_file_upload_ping_error_2);

		String btContentTitle = context.getResources().getString(R.string.message_file_upload_error);
		String btContentText = contentText + errorMessage;
		String btSummaryText = fileName;
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.bigText(btContentText);
		bigTextStyle.setBigContentTitle(btContentTitle);
		bigTextStyle.setSummaryText(btSummaryText);
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_ic_upload_failed_big);

		// 產生通知ID
		int notificationId = getNotificationId(true, rowId);

		Intent openFilelugIntent = new Intent(context, MainActivity.class);
		openFilelugIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_UPLOAD_PING_ERROR);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		Intent resumeUploadIntent = new Intent(context, MainActivity.class);
		resumeUploadIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		resumeUploadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_GCM_RESUME_UPLOAD_FILE);
		resumeUploadIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		resumeUploadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		PendingIntent pIntent1 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), openFilelugIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pIntent2 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), resumeUploadIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.notification_ic_upload_failed)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setStyle(bigTextStyle)
			.setLargeIcon(largeIcon)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.addAction(R.drawable.notification_ic_transfered, context.getResources().getString(R.string.btn_label_open_filelug), pIntent1)
			.addAction(R.drawable.ic_action_resume_upload, context.getResources().getString(R.string.btn_label_resume_upload), pIntent2);

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void noticeUploadFileProgress(Context context, long rowId, String fileName, int progress) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeUploadFileProgress(), fileName=" + fileName + ", progress=" + progress);

		String contentTitle = context.getResources().getString(R.string.message_file_uploading_1);
		String contentText = String.format(context.getResources().getString(R.string.message_file_uploading_2), fileName);
		String contentInfo = Integer.toString(progress) + "%";

		// 產生通知ID
		int notificationId = getNotificationId(true, rowId);

		Intent stopUploadIntent = new Intent(context, MainActivity.class);
		stopUploadIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		stopUploadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_STOP_UPLOAD_FILE);
		stopUploadIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		stopUploadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		PendingIntent pIntent1 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), stopUploadIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.animation_upload)
			.setOngoing(true)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setContentInfo(contentInfo)
			.setProgress(100, progress, false)
			.addAction(R.drawable.ic_action_stop_upload, context.getResources().getString(R.string.btn_label_stop), pIntent1);

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void noticeUploadFileCompleted(Context context, long rowId, String fileName) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeUploadFileCompleted(), fileName=" + fileName);
		String contentTitle = String.format(context.getResources().getString(R.string.message_file_upload_completed_1), fileName);
		String contentText = context.getResources().getString(R.string.message_file_upload_completed_2);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.notification_ic_uploading0)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setAutoCancel(true);

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		int notificationId = getNotificationId(true, rowId);
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void noticeUploadFileTransferError(Context context, long rowId, String fileName, String message) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeUploadFileTransferError(), fileName=" + fileName);
		String contentTitle = String.format(context.getResources().getString(R.string.message_file_upload_transfer_error_1), fileName);
		String contentText = context.getResources().getString(R.string.message_file_upload_transfer_error_2);

		String btContentTitle = context.getResources().getString(R.string.message_file_upload_error);
		String btContentText = contentText + message;
		String btSummaryText = fileName;
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.bigText(btContentText);
		bigTextStyle.setBigContentTitle(btContentTitle);
		bigTextStyle.setSummaryText(btSummaryText);
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_ic_upload_failed_big);

		// 產生通知ID
		int notificationId = getNotificationId(true, rowId);

		Intent openFilelugIntent = new Intent(context, MainActivity.class);
		openFilelugIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_UPLOAD_TRANSFER_ERROR);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		Intent resumeUploadIntent = new Intent(context, MainActivity.class);
		resumeUploadIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		resumeUploadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_GCM_RESUME_UPLOAD_FILE);
		resumeUploadIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		resumeUploadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		PendingIntent pIntent1 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), openFilelugIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pIntent2 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), resumeUploadIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.notification_ic_upload_failed)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setStyle(bigTextStyle)
			.setLargeIcon(largeIcon)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.addAction(R.drawable.notification_ic_transfered, context.getResources().getString(R.string.btn_label_open_filelug), pIntent1)
			.addAction(R.drawable.ic_action_resume_upload, context.getResources().getString(R.string.btn_label_resume_upload), pIntent2);

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void noticeUploadFileResponseError(Context context, long rowId, String fileName, String message) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeUploadFileResponseError(), fileName=" + fileName);
		String contentTitle = String.format(context.getResources().getString(R.string.message_file_upload_response_error_1), fileName);
		String contentText = String.format(context.getResources().getString(R.string.message_file_upload_response_error_2), message);

		String btContentTitle = context.getResources().getString(R.string.message_file_upload_error);
		String btContentText = contentText + message;
		String btSummaryText = fileName;
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.bigText(btContentText);
		bigTextStyle.setBigContentTitle(btContentTitle);
		bigTextStyle.setSummaryText(btSummaryText);
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_ic_upload_failed_big);

		// 產生通知ID
		int notificationId = getNotificationId(true, rowId);

		Intent openFilelugIntent = new Intent(context, MainActivity.class);
		openFilelugIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_UPLOAD_RESPONSE_ERROR);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		Intent resumeUploadIntent = new Intent(context, MainActivity.class);
		resumeUploadIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		resumeUploadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_GCM_RESUME_UPLOAD_FILE);
		resumeUploadIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		resumeUploadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		PendingIntent pIntent1 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), openFilelugIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pIntent2 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), resumeUploadIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.notification_ic_upload_failed)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setStyle(bigTextStyle)
			.setLargeIcon(largeIcon)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.addAction(R.drawable.notification_ic_transfered, context.getResources().getString(R.string.btn_label_open_filelug), pIntent1)
			.addAction(R.drawable.ic_action_resume_upload, context.getResources().getString(R.string.btn_label_resume_upload), pIntent2);

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void noticeFileUploadCancel(Context context, long rowId, String fileName, long transferredBytes) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeFileUploadCancel(), fileName=" + fileName + ", transferredBytes=" + transferredBytes);

		if ( transferredBytes == 0l ) return;

		String contentTitle = context.getResources().getString(R.string.message_file_upload_canceled_1);
		String contentText = String.format(context.getResources().getString(R.string.message_file_upload_canceled_2), fileName);

		String btContentTitle = contentTitle;
		String btContentText = contentText;
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.bigText(btContentText);
		bigTextStyle.setBigContentTitle(btContentTitle);
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_ic_upload_failed_big);

		// 產生通知ID
		int notificationId = getNotificationId(true, rowId);

		Intent openFilelugIntent = new Intent(context, MainActivity.class);
		openFilelugIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_UPLOAD_CANCELED);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		Intent resumeUploadIntent = new Intent(context, MainActivity.class);
		resumeUploadIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		resumeUploadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_GCM_RESUME_UPLOAD_FILE);
		resumeUploadIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		resumeUploadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		PendingIntent pIntent1 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), openFilelugIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pIntent2 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), resumeUploadIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.notification_ic_upload_failed)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setStyle(bigTextStyle)
			.setLargeIcon(largeIcon)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.addAction(R.drawable.notification_ic_transfered, context.getResources().getString(R.string.btn_label_open_filelug), pIntent1)
			.addAction(R.drawable.ic_action_resume_upload, context.getResources().getString(R.string.btn_label_resume_upload), pIntent2);

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void removeUploadNotification(Context context, long rowId) {
//		if ( Constants.DEBUG ) Log.d(TAG, "removeUploadNotification(), rowId=" + rowId);
		int notificationId = getNotificationId(true, rowId);
		removeNotification(context, notificationId);
	}

	public static void noticeGCMMessageUploadFiles(Context context, String groupId, String message, Map<Long, String> rows) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeGCMMessageUploadFiles(), groupId=" + groupId + ", message=" + message + ", rows=" + rows);

		String contentTitle = NOTIFICATION_TAG;
		String contentText = message;

		String btContentTitle = contentTitle;
		String btSummaryText = String.format(context.getResources().getString(R.string.message_notify_all_files_uploaded_successfully_2), rows.size());
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		inboxStyle.setBigContentTitle(btContentTitle);
		inboxStyle.setSummaryText(btSummaryText);
		int lineCount = 0;
		for ( Long rowId : rows.keySet() ) {
			String fileName = rows.get(rowId);
			if ( lineCount < 5 ) {
				inboxStyle.addLine(fileName);
			}
			lineCount++;
		}
		if ( lineCount > 5 ) {
			inboxStyle.addLine("...");
		}
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_ic_transfered_big);

		// 產生通知ID
		int notificationId = getSummaryNotificationId();

		Intent openFilelugIntent = new Intent(context, MainActivity.class);
		openFilelugIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_GCM_UPLOAD_FILES);
		openFilelugIntent.putExtra(Constants.PARAM_UPLOAD_GROUP_ID, groupId);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		PendingIntent pIntent1 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), openFilelugIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.notification_ic_transfered)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setStyle(inboxStyle)
			.setLargeIcon(largeIcon)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.addAction(R.drawable.notification_ic_transfered, context.getResources().getString(R.string.btn_label_open_filelug), pIntent1);

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void noticeGCMMessageUploadFile(Context context, long rowId, String status, String message) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeGCMMessageUploadFile(),  rowId=" + rowId + ", status=" + status + ", message=" + message);

		String contentTitle = NOTIFICATION_TAG;
		String contentText = message;

		String btContentTitle = contentTitle;
		String btContentText = contentText;
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.bigText(btContentText);
		bigTextStyle.setBigContentTitle(btContentTitle);
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), UploadStatusType.failure.name().equals(status) ? R.drawable.notification_ic_upload_failed_big : R.drawable.notification_ic_transfered_big);

		// 產生通知ID
		int notificationId = getSummaryNotificationId();

		Intent openFilelugIntent = new Intent(context, MainActivity.class);
		openFilelugIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_GCM_UPLOAD_FILE);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		PendingIntent pIntent1 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), openFilelugIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		int iconRes =  UploadStatusType.failure.name().equals(status) ? R.drawable.notification_ic_upload_failed : R.drawable.notification_ic_transfered;

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(iconRes)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setStyle(bigTextStyle)
			.setLargeIcon(largeIcon)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.addAction(R.drawable.notification_ic_transfered, context.getResources().getString(R.string.btn_label_open_filelug), pIntent1);

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void noticePrepareToDownloadFile(Context context, long rowId, String fileName) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticePrepareToDownloadFile(), fileName=" + fileName);
		String contentTitle = context.getResources().getString(R.string.message_file_prepare_to_download_1);
		String contentText = String.format(context.getResources().getString(R.string.message_file_prepare_to_download_2), fileName);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.notification_ic_downloading0)
			.setOngoing(true)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setProgress(100, 0, true);
//			.setContentIntent(notificationConfig.getPendingIntent(this))

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		int notificationId = getNotificationId(false, rowId);
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void noticeFileDownloadPingError(Context context, long rowId, String fileName, String errorMessage) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeFileDownloadPingError(), fileName=" + fileName);
		String contentTitle = String.format(context.getResources().getString(R.string.message_file_download_ping_error_1), fileName);
		String contentText = context.getResources().getString(R.string.message_file_download_ping_error_2);

		String btContentTitle = context.getResources().getString(R.string.message_file_download_error);
		String btContentText = contentText + errorMessage;
		String btSummaryText = fileName;
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.bigText(btContentText);
		bigTextStyle.setBigContentTitle(btContentTitle);
		bigTextStyle.setSummaryText(btSummaryText);
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_ic_download_failed_big);

		// 產生通知ID
		int notificationId = getNotificationId(false, rowId);

		Intent openFilelugIntent = new Intent(context, MainActivity.class);
		openFilelugIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_DOWNLOAD_PING_ERROR);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		Intent resumeDownloadIntent = new Intent(context, MainActivity.class);
		resumeDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		resumeDownloadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_GCM_RESUME_DOWNLOAD_FILE);
		resumeDownloadIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		resumeDownloadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		PendingIntent pIntent1 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), openFilelugIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pIntent2 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), resumeDownloadIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.notification_ic_download_failed)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setStyle(bigTextStyle)
			.setLargeIcon(largeIcon)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.addAction(R.drawable.notification_ic_transfered, context.getResources().getString(R.string.btn_label_open_filelug), pIntent1)
			.addAction(R.drawable.ic_action_resume_download, context.getResources().getString(R.string.btn_label_resume_download), pIntent2);

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void noticeDownloadFileProgress(Context context, long rowId, String fileName, int progress) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeDownloadFileProgress(), fileName=" + fileName + ", progress=" + progress);

		String contentTitle = context.getResources().getString(R.string.message_file_downloading_1);
		String contentText = String.format(context.getResources().getString(R.string.message_file_downloading_2), fileName);
		String contentInfo = Integer.toString(progress) + "%";

		// 產生通知ID
		int notificationId = getNotificationId(false, rowId);

		Intent stopDownloadIntent = new Intent(context, MainActivity.class);
		stopDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		stopDownloadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_STOP_DOWNLOAD_FILE);
		stopDownloadIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		stopDownloadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		PendingIntent pIntent1 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), stopDownloadIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.animation_download)
			.setOngoing(true)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setContentInfo(contentInfo)
			.setProgress(100, progress, false)
			.addAction(R.drawable.ic_action_stop_download, context.getResources().getString(R.string.btn_label_stop), pIntent1);

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void noticeDownloadFileCompleted(Context context, long rowId, String fileName) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeDownloadFileCompleted(), fileName=" + fileName);
		String contentTitle = String.format(context.getResources().getString(R.string.message_file_download_completed_1), fileName);
		String contentText = context.getResources().getString(R.string.message_file_download_completed_2);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.notification_ic_downloading0)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setAutoCancel(true);
		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		int notificationId = getNotificationId(false, rowId);
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void noticeDownloadFileSuccess(Context context, long rowId, String fileName) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeDownloadFileSuccess(), rowId=" + rowId + ", fileName=" + fileName);

		String contentTitle = NOTIFICATION_TAG;
		String contentText = String.format(context.getResources().getString(R.string.message_notify_file_download_success), fileName);

		String btContentTitle = contentTitle;
		String btContentText = contentText;
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.bigText(btContentText);
		bigTextStyle.setBigContentTitle(btContentTitle);
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_ic_transfered_big);

		// 產生通知ID
		int notificationId = getNotificationId(false, rowId);

		Intent openFilelugIntent = new Intent(context, MainActivity.class);
		openFilelugIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_DOWNLOAD_SUCCESS);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		Intent openFileIntent = new Intent(context, MainActivity.class);
		openFileIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		openFileIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_OPEN_DOWNLOADED_FILE);
		openFileIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		openFileIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		PendingIntent pIntent1 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), openFilelugIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pIntent2 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), openFileIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.notification_ic_transfered)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setStyle(bigTextStyle)
			.setLargeIcon(largeIcon)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.addAction(R.drawable.notification_ic_transfered, context.getResources().getString(R.string.btn_label_open_filelug), pIntent1)
			.addAction(R.drawable.ic_action_open_file, context.getResources().getString(R.string.btn_label_open_file), pIntent2);

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void noticeDownloadFileTransferError(Context context, long rowId, String fileName, String message) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeDownloadFileTransferError(), fileName=" + fileName);
		String contentTitle = String.format(context.getResources().getString(R.string.message_file_download_transfer_error_1), fileName);
		String contentText = context.getResources().getString(R.string.message_file_download_transfer_error_2);

		String btContentTitle = context.getResources().getString(R.string.message_file_download_error);
		String btContentText = contentText + message;
		String btSummaryText = fileName;
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.bigText(btContentText);
		bigTextStyle.setBigContentTitle(btContentTitle);
		bigTextStyle.setSummaryText(btSummaryText);
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_ic_download_failed_big);

		// 產生通知ID
		int notificationId = getNotificationId(false, rowId);

		Intent openFilelugIntent = new Intent(context, MainActivity.class);
		openFilelugIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_DOWNLOAD_TRANSFER_ERROR);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		Intent resumeDownloadIntent = new Intent(context, MainActivity.class);
		resumeDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		resumeDownloadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_GCM_RESUME_DOWNLOAD_FILE);
		resumeDownloadIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		resumeDownloadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		PendingIntent pIntent1 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), openFilelugIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pIntent2 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), resumeDownloadIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.notification_ic_download_failed)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setStyle(bigTextStyle)
			.setLargeIcon(largeIcon)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.addAction(R.drawable.notification_ic_transfered, context.getResources().getString(R.string.btn_label_open_filelug), pIntent1)
			.addAction(R.drawable.ic_action_resume_download, context.getResources().getString(R.string.btn_label_resume_download), pIntent2);

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void noticeDownloadFileResponseError(Context context, long rowId, String fileName, String message) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeDownloadFileResponseError(), fileName=" + fileName);
		String contentTitle = String.format(context.getResources().getString(R.string.message_file_download_response_error_1), fileName);
		String contentText = String.format(context.getResources().getString(R.string.message_file_download_response_error_2), message);

		String btContentTitle = context.getResources().getString(R.string.message_file_download_error);
		String btContentText = contentText + message;
		String btSummaryText = fileName;
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.bigText(btContentText);
		bigTextStyle.setBigContentTitle(btContentTitle);
		bigTextStyle.setSummaryText(btSummaryText);
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_ic_download_failed_big);

		// 產生通知ID
		int notificationId = getNotificationId(false, rowId);

		Intent openFilelugIntent = new Intent(context, MainActivity.class);
		openFilelugIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_DOWNLOAD_RESPONSE_ERROR);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		Intent resumeDownloadIntent = new Intent(context, MainActivity.class);
		resumeDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		resumeDownloadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_GCM_RESUME_DOWNLOAD_FILE);
		resumeDownloadIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		resumeDownloadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		PendingIntent pIntent1 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), openFilelugIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pIntent2 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), resumeDownloadIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.notification_ic_download_failed)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setStyle(bigTextStyle)
			.setLargeIcon(largeIcon)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.addAction(R.drawable.notification_ic_transfered, context.getResources().getString(R.string.btn_label_open_filelug), pIntent1)
			.addAction(R.drawable.ic_action_resume_download, context.getResources().getString(R.string.btn_label_resume_download), pIntent2);

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void noticeFileDownloadCancel(Context context, long rowId, String fileName, long transferredBytes) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeFileDownloadCancel(), fileName=" + fileName + ", transferredBytes=" + transferredBytes);

		if ( transferredBytes == 0l ) return;

		String contentTitle = context.getResources().getString(R.string.message_file_download_canceled_1);
		String contentText = String.format(context.getResources().getString(R.string.message_file_download_canceled_2), fileName);

		String btContentTitle = contentTitle;
		String btContentText = contentText;
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.bigText(btContentText);
		bigTextStyle.setBigContentTitle(btContentTitle);
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_ic_download_failed_big);

		// 產生通知ID
		int notificationId = getNotificationId(false, rowId);

		Intent openFilelugIntent = new Intent(context, MainActivity.class);
		openFilelugIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_DOWNLOAD_CANCELED);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		Intent resumeDownloadIntent = new Intent(context, MainActivity.class);
		resumeDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		resumeDownloadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_GCM_RESUME_DOWNLOAD_FILE);
		resumeDownloadIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		resumeDownloadIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		PendingIntent pIntent1 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), openFilelugIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pIntent2 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), resumeDownloadIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.notification_ic_download_failed)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setStyle(bigTextStyle)
			.setLargeIcon(largeIcon)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.addAction(R.drawable.notification_ic_transfered, context.getResources().getString(R.string.btn_label_open_filelug), pIntent1)
			.addAction(R.drawable.ic_action_resume_download, context.getResources().getString(R.string.btn_label_resume_download), pIntent2);

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void removeDownloadNotification(Context context, long rowId) {
//		if ( Constants.DEBUG ) Log.d(TAG, "removeDownloadNotification(), rowId=" + rowId);
		int notificationId = getNotificationId(false, rowId);
		removeNotification(context, notificationId);
	}

	public static void removeNotification(Context context, int notificationId) {
//		if ( Constants.DEBUG ) Log.d(TAG, "removeNotification(), rowId=" + rowId);
		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(NOTIFICATION_TAG, notificationId);
	}

	public static void noticeSimulateGCMMessageDownloadFiles(Context context, String groupId, String message, Map<Long, String> rows) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeSimulateGCMMessageDownloadFiles(), groupId=" + groupId + ", message=" + message + ", rows=" + (rows == null ? "null" : rows.size()));

		String contentTitle = NOTIFICATION_TAG;
		String contentText = message;

		String btContentTitle = contentTitle;
		String btSummaryText = String.format(context.getResources().getString(R.string.message_notify_all_files_downloaded_successfully_2), rows.size());
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		inboxStyle.setBigContentTitle(btContentTitle);
		inboxStyle.setSummaryText(btSummaryText);
		int lineCount = 0;
		for ( Long rowId : rows.keySet() ) {
			String fileName = rows.get(rowId);
			if ( lineCount < 5 ) {
				inboxStyle.addLine(fileName);
			}
			lineCount++;
		}
		if ( lineCount > 5 ) {
			inboxStyle.addLine("...");
		}
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_ic_transfered_big);

		// 產生通知ID
		int notificationId = getSummaryNotificationId();

		Intent openFilelugIntent = new Intent(context, MainActivity.class);
		openFilelugIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_GCM_DOWNLOAD_FILES);
		openFilelugIntent.putExtra(Constants.PARAM_DOWNLOAD_GROUP_ID, groupId);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		PendingIntent pIntent1 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), openFilelugIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(R.drawable.notification_ic_transfered)
			.setTicker(message)
			.setContentTitle(NOTIFICATION_TAG)
			.setContentText(message)
			.setStyle(inboxStyle)
			.setLargeIcon(largeIcon)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.addAction(R.drawable.notification_ic_transfered, context.getResources().getString(R.string.btn_label_open_filelug), pIntent1);
		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void noticeSimulateGCMMessageDownloadFile(Context context, long rowId, String fileName) {
//		if ( Constants.DEBUG ) Log.d(TAG, "noticeSimulateGCMMessageDownloadFile(), rowId=" + rowId + ", fileName=" + fileName);

		String contentTitle = NOTIFICATION_TAG;
		String contentText = String.format(context.getResources().getString(R.string.message_notify_file_download_success), fileName);

		String btContentTitle = contentTitle;
		String btContentText = contentText;
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.bigText(btContentText);
		bigTextStyle.setBigContentTitle(btContentTitle);
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_ic_transfered_big);

		// 產生通知ID
		int notificationId = getSummaryNotificationId();

		Intent openFilelugIntent = new Intent(context, MainActivity.class);
		openFilelugIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_GCM_DOWNLOAD_FILE);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		openFilelugIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		Intent openFileIntent = new Intent(context, MainActivity.class);
		openFileIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		openFileIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, Constants.NOTIFICATION_CALLBACK_TYPE_OPEN_DOWNLOADED_FILE);
		openFileIntent.putExtra(Constants.EXT_PARAM_ROW_ID, rowId);
		openFileIntent.putExtra(Constants.EXT_PARAM_NOTIFICATION_ID, notificationId);

		PendingIntent pIntent1 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), openFilelugIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pIntent2 = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), openFileIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		int iconRes =  R.drawable.notification_ic_transfered;

		// 建立 Builder 物件
		NotificationCompat.Builder builder = getBuilder(context);
		builder.setSmallIcon(iconRes)
			.setTicker(contentText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setStyle(bigTextStyle)
			.setLargeIcon(largeIcon)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.addAction(R.drawable.notification_ic_transfered, context.getResources().getString(R.string.btn_label_open_filelug), pIntent1)
			.addAction(R.drawable.ic_action_open_file, context.getResources().getString(R.string.btn_label_open_file), pIntent2);

		// 建立通知物件
		Notification notification = builder.build();

		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		// 發出通知
		manager.notify(NOTIFICATION_TAG, notificationId, notification);
	}

	public static void cleanAllNotifications(Context context) {
//		if ( Constants.DEBUG ) Log.d(TAG, "cleanAllNotifications()");
		// 取得 NotificationManager 物件
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancelAll();
	}

}
