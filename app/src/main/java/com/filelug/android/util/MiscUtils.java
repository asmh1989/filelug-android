package com.filelug.android.util;

import android.accounts.Account;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.filelug.android.Constants;
import com.filelug.android.MainApplication;
import com.filelug.android.R;
import com.filelug.android.crepo.RepositoryErrorObject;
import com.filelug.android.fileprovider.LocalFilesProvider;
import com.filelug.android.provider.downloadgroup.DownloadGroupColumns;
import com.filelug.android.provider.filetransfer.FileTransferColumns;
import com.filelug.android.service.ContentType;
import com.filelug.android.service.DownloadNotificationService;
import com.filelug.android.service.DownloadService;
import com.filelug.android.service.UploadService;
import com.filelug.android.ui.adapter.CustomMaterialSimpleListAdapter;
import com.filelug.android.ui.model.CustomMaterialSimpleListItem;
import com.filelug.android.ui.model.LocalFile;
import com.filelug.android.ui.model.RemoteFile;

import org.json.JSONException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class MiscUtils {

	private static final String TAG = MiscUtils.class.getSimpleName();

	private static Map<String, Integer> fileIconMap = null;

	static {
		fileIconMap = createFileIconMap();
	}

	public static int getStatusCode(VolleyError volleyError) {
		int statusCode = -1;
		if ( volleyError != null && volleyError.networkResponse != null ) {
			statusCode = volleyError.networkResponse.statusCode;
		}
		return statusCode;
	}

	public static String getVolleyErrorMessage(VolleyError volleyError) {
		Context context = MainApplication.getInstance().getBaseContext();
		String message = null;

		if ( volleyError.networkResponse != null ) {
			if ( volleyError.networkResponse.statusCode == Constants.HTTP_STATUS_CODE_SERVICE_UNAVAILABLE ) {
				message = context.getResources().getString(R.string.message_request_connection_failed_1);
			} else if ( volleyError.networkResponse.statusCode == Constants.HTTP_STATUS_CODE_FORBIDDEN ) {
				message = context.getResources().getString(R.string.message_session_not_exists);
			} else {
				message = new String(volleyError.networkResponse.data) + " (" + volleyError.networkResponse.statusCode + ")";
			}
		} else if (volleyError instanceof AuthFailureError) {
			message = context.getResources().getString(R.string.message_auth_failure_error);
		} else if (volleyError instanceof ParseError) {
			message = context.getResources().getString(R.string.message_parse_error);
		} else if (volleyError instanceof ServerError) {
			message = context.getResources().getString(R.string.message_server_error);
		} else if (volleyError instanceof TimeoutError) {
			message = context.getResources().getString(R.string.message_timeout_error);
		} else if (volleyError instanceof NoConnectionError) {
			if ( volleyError.getMessage().contains("authentication challenge") ) {
				message = context.getResources().getString(R.string.message_account_or_password_error);
			} else {
				message = context.getResources().getString(R.string.message_no_connection);
			}
		} else if (volleyError instanceof NetworkError) {
			message = context.getResources().getString(R.string.message_network_error);
		} else {
			message = volleyError.getLocalizedMessage();
			if ( volleyError.networkResponse != null ) {
				message += " (" + volleyError.networkResponse.statusCode + ")";
			}
		}

		return message;
	}

	public static String getExtension(String fileName, boolean toLowerCase) {
		String extension = null;
		int lastIndexOf = fileName.lastIndexOf('.');
		if ( lastIndexOf >= 0 ) {
			extension = fileName.substring(lastIndexOf+1, fileName.length());
		}
		return extension != null ? ( toLowerCase ? extension.toLowerCase() : extension ) : null;
	}

	public static String getExtension(String fileName) {
		return getExtension(fileName, true);
	}

	public static Map createFileIconMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("3ds", R.drawable.ic_file_3ds);
		map.put("3g2", R.drawable.ic_file_3g2);
		map.put("3gp", R.drawable.ic_file_3gp);
		map.put("7z", R.drawable.ic_file_7z);
		map.put("ai", R.drawable.ic_file_ai);
		map.put("aif", R.drawable.ic_file_aif);
		map.put("apk", R.drawable.ic_file_apk);
		map.put("app", R.drawable.ic_file_app);
		map.put("asf", R.drawable.ic_file_asf);
		map.put("asp", R.drawable.ic_file_asp);
		map.put("aspx", R.drawable.ic_file_aspx);
		map.put("asx", R.drawable.ic_file_asx);
		map.put("avi", R.drawable.ic_file_avi);
		map.put("bat", R.drawable.ic_file_bat);
		map.put("bin", R.drawable.ic_file_bin);
		map.put("bmp", R.drawable.ic_file_bmp);
		map.put("c", R.drawable.ic_file_c);
		map.put("cab", R.drawable.ic_file_cab);
		map.put("cer", R.drawable.ic_file_cer);
		map.put("cfg", R.drawable.ic_file_cfg);
		map.put("cgi", R.drawable.ic_file_cgi);
		map.put("class", R.drawable.ic_file_class);
		map.put("com", R.drawable.ic_file_com);
		map.put("cpp", R.drawable.ic_file_cpp);
		map.put("css", R.drawable.ic_file_css);
		map.put("csv", R.drawable.ic_file_csv);
		map.put("cur", R.drawable.ic_file_cur);
		map.put("dat", R.drawable.ic_file_dat);
		map.put("db", R.drawable.ic_file_db);
		map.put("dbf", R.drawable.ic_file_dbf);
		map.put("deb", R.drawable.ic_file_deb);
		map.put("dll", R.drawable.ic_file_dll);
		map.put("dmg", R.drawable.ic_file_dmg);
		map.put("doc", R.drawable.ic_file_doc);
		map.put("docx", R.drawable.ic_file_docx);
		map.put("dtd", R.drawable.ic_file_dtd);
		map.put("dwg", R.drawable.ic_file_dwg);
		map.put("dxf", R.drawable.ic_file_dxf);
		map.put("eps", R.drawable.ic_file_eps);
		map.put("exe", R.drawable.ic_file_exe);
		map.put("flv", R.drawable.ic_file_flv);
		map.put("gif", R.drawable.ic_file_gif);
		map.put("gz", R.drawable.ic_file_gz);
		map.put("h", R.drawable.ic_file_h);
		map.put("heic", R.drawable.ic_file_heic);
		map.put("hqx", R.drawable.ic_file_hqx);
		map.put("htm", R.drawable.ic_file_htm);
		map.put("html", R.drawable.ic_file_html);
		map.put("icns", R.drawable.ic_file_icns);
		map.put("ico", R.drawable.ic_file_ico);
		map.put("ics", R.drawable.ic_file_ics);
		map.put("ini", R.drawable.ic_file_ini);
		map.put("iso", R.drawable.ic_file_iso);
		map.put("jar", R.drawable.ic_file_jar);
		map.put("java", R.drawable.ic_file_java);
		map.put("jpeg", R.drawable.ic_file_jpeg);
		map.put("jpg", R.drawable.ic_file_jpg);
		map.put("js", R.drawable.ic_file_js);
		map.put("jsp", R.drawable.ic_file_jsp);
		map.put("key", R.drawable.ic_file_key);
		map.put("kml", R.drawable.ic_file_kml);
		map.put("kmz", R.drawable.ic_file_kmz);
		map.put("log", R.drawable.ic_file_log);
		map.put("m3u", R.drawable.ic_file_m3u);
		map.put("m4a", R.drawable.ic_file_m4a);
		map.put("m4v", R.drawable.ic_file_m4v);
		map.put("max", R.drawable.ic_file_max);
		map.put("mdb", R.drawable.ic_file_mdb);
		map.put("mid", R.drawable.ic_file_mid);
		map.put("mov", R.drawable.ic_file_mov);
		map.put("mp3", R.drawable.ic_file_mp3);
		map.put("mp4", R.drawable.ic_file_mp4);
		map.put("mpeg", R.drawable.ic_file_mpeg);
		map.put("mpg", R.drawable.ic_file_mpg);
		map.put("msg", R.drawable.ic_file_msg);
		map.put("msi", R.drawable.ic_file_msi);
		map.put("odt", R.drawable.ic_file_odt);
		map.put("ogg", R.drawable.ic_file_ogg);
		map.put("otf", R.drawable.ic_file_otf);
		map.put("pdf", R.drawable.ic_file_pdf);
		map.put("php", R.drawable.ic_file_php);
		map.put("pkg", R.drawable.ic_file_pkg);
		map.put("png", R.drawable.ic_file_png);
		map.put("pps", R.drawable.ic_file_pps);
		map.put("ppt", R.drawable.ic_file_ppt);
		map.put("pptx", R.drawable.ic_file_pptx);
		map.put("prf", R.drawable.ic_file_prf);
		map.put("ps", R.drawable.ic_file_ps);
		map.put("psd", R.drawable.ic_file_psd);
		map.put("ra", R.drawable.ic_file_ra);
		map.put("rar", R.drawable.ic_file_rar);
		map.put("rm", R.drawable.ic_file_rm);
		map.put("rpm", R.drawable.ic_file_rpm);
		map.put("rss", R.drawable.ic_file_rss);
		map.put("rtf", R.drawable.ic_file_rtf);
		map.put("sh", R.drawable.ic_file_sh);
		map.put("sql", R.drawable.ic_file_sql);
		map.put("svg", R.drawable.ic_file_svg);
		map.put("swf", R.drawable.ic_file_swf);
		map.put("swift", R.drawable.ic_file_swift);
		map.put("sys", R.drawable.ic_file_sys);
		map.put("tar", R.drawable.ic_file_tar);
		map.put("tif", R.drawable.ic_file_tif);
		map.put("tiff", R.drawable.ic_file_tiff);
		map.put("ttc", R.drawable.ic_file_ttc);
		map.put("ttf", R.drawable.ic_file_ttf);
		map.put("txt", R.drawable.ic_file_txt);
		map.put("vb", R.drawable.ic_file_vb);
		map.put("vcd", R.drawable.ic_file_vcd);
		map.put("vcf", R.drawable.ic_file_vcf);
		map.put("vob", R.drawable.ic_file_vob);
		map.put("wav", R.drawable.ic_file_wav);
		map.put("wma", R.drawable.ic_file_wma);
		map.put("wmv", R.drawable.ic_file_wmv);
		map.put("xhtml", R.drawable.ic_file_xhtml);
		map.put("xls", R.drawable.ic_file_xls);
		map.put("xlsx", R.drawable.ic_file_xlsx);
		map.put("xml", R.drawable.ic_file_xml);
		map.put("zip", R.drawable.ic_file_zip);
		return map;
	}

	public static int getIconResourceIdByExtension(String extension) {
		int resourceId = -1;
		Integer resID = fileIconMap.get(extension);
		if ( resID != null ) {
			resourceId = resID.intValue();
		} else {
			resourceId = R.drawable.ic_file;
		}
		return resourceId;
	}

	public static int getIconResourceIdByLocalFileObject(LocalFile localFile) {

		int resourceId = -1;

		if ( localFile.getType().isDirectory() ) {
			resourceId = R.drawable.ic_folder_l;
//		} else if ( localFile.getType() == LocalFile.FileType.UNKNOWN ) {
//			resourceId = R.drawable.ic_file_unknown;
		} else if ( localFile.getType().isFile() ) {
			String extension = localFile.getExtension();
			resourceId = MiscUtils.getIconResourceIdByExtension(extension);
		}

		return resourceId;

	}

	public static int getIconResourceIdByLocalFileType(LocalFile.FileType fileType) {

		int resourceId = -1;

		switch (fileType) {
			// 本機裝置資料夾
			case ROOT:
				resourceId = R.drawable.ic_folder_mobile_device;
				break;
			case SYS_DIR_PICTURES:
				resourceId = R.drawable.ic_folder_pictures;
				break;
			case SYS_DIR_MUSIC:
				resourceId = R.drawable.ic_folder_music;
				break;
			case SYS_DIR_MOVIES:
				resourceId = R.drawable.ic_folder_movies;
				break;
			case SYS_DIR_DOCUMENTS:
				resourceId = R.drawable.ic_folder_documents;
				break;
			case SYS_DIR_DOWNLOADS:
				resourceId = R.drawable.ic_folder_downloads;
				break;
			case SYS_DIR_INTERNAL_STORAGE:
				resourceId = R.drawable.ic_folder_internal_storage;
				break;
			case SYS_DIR_SD_CARD_STORAGE:
				resourceId = R.drawable.ic_folder_sdcard;
				break;
			case SYS_DIR_USB_STORAGE:
				resourceId = R.drawable.ic_folder_usb_storage;
				break;
			case SYS_DIR_DEVICE_ROOT:
				resourceId = R.drawable.ic_folder_local_root;
				break;
			default:
				break;
		}

		return resourceId;

	}

	public static int getIconResourceIdByRemoteFileObject(RemoteFile remoteFile) {

		int resourceId = -1;

		if ( remoteFile.getType().isDirectory() ) {
			resourceId = R.drawable.ic_folder_l;
//		} else if ( remoteFile.getType() == RemoteFile.FileType.UNKNOWN ) {
//			resourceId = R.drawable.ic_file_unknown;
		} else if ( remoteFile.getType().isFile() ) {
			String extension = remoteFile.getExtension();
			resourceId = MiscUtils.getIconResourceIdByExtension(extension);
		}

		return resourceId;

	}

	public static int getIconResourceIdByRemoteFileType(RemoteFile.FileType fileType) {

		int resourceId = -1;

		switch (fileType) {
			// 遠端電腦資料夾
			case ROOT:
				resourceId = R.drawable.ic_laptop;
				break;
			case REMOTE_ROOT_USER_HOME:
				resourceId = R.drawable.ic_root_user_home;
				break;
			case REMOTE_ROOT_LOCAL_DISK:
				resourceId = R.drawable.ic_root_hard_disk;
				break;
			case REMOTE_ROOT_DVD_PLAYER:
				resourceId = R.drawable.ic_root_dvd;
				break;
			case REMOTE_ROOT_NETWORK_DISK:
				resourceId = R.drawable.ic_root_network_driver;
				break;
			case REMOTE_ROOT_EXTERNAL_DISK:
				resourceId = R.drawable.ic_root_usb_disk;
				break;
			case REMOTE_ROOT_TIME_MACHINE:
				resourceId = R.drawable.ic_root_time_machine;
				break;
			case REMOTE_ROOT_DIRECTORY:
			case REMOTE_ROOT_WINDOWS_SHORTCUT_DIRECTORY:
			case REMOTE_ROOT_UNIX_SYMBOLIC_LINK_DIRECTORY:
			case REMOTE_ROOT_MAC_ALIAS_DIRECTORY:
				resourceId = R.drawable.ic_folder;
				break;
			default:
				break;
		}

		return resourceId;

	}

	public static boolean isServiceRunning(Class<?> serviceClass) {
//		if ( Constants.DEBUG ) Log.d(TAG, "isServiceRunning(), serviceClass=" + serviceClass.getName());
		Context context = MainApplication.getInstance().getBaseContext();
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//			if ( Constants.DEBUG ) Log.d(TAG, service.service.getClassName());
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isUploadOrDownloadOrNotificationServiceRunning() {
//		if ( Constants.DEBUG ) Log.d(TAG, "isUploadOrDownloadOrNotificationServiceRunning()");
		String uploadServiceName = UploadService.class.getName();
		String downloadServiceName = DownloadService.class.getName();
		String notificationServiceName = DownloadNotificationService.class.getName();
		Context context = MainApplication.getInstance().getBaseContext();
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			String serviceName = service.service.getClassName();
//			if ( Constants.DEBUG ) Log.d(TAG, "serviceName=" + serviceName);
			if ( uploadServiceName.equals(serviceName) || downloadServiceName.equals(serviceName) || notificationServiceName.equals(serviceName) ) {
				return true;
			}
		}
		return false;
	}

	public static void openFile(Context context, long rowId) {
		Bundle downloadFileInfo = TransferDBHelper.getDownloadFileInfoByRowId(rowId);
		if ( downloadFileInfo == null ) {
			MsgUtils.showToast(context, "Download record not found!");
			return;
		}
		String localDir = downloadFileInfo.getString(DownloadGroupColumns.LOCAL_PATH, null);
		String fileName = downloadFileInfo.getString(FileTransferColumns.LOCAL_FILE_NAME, null);
		String savedFileName = downloadFileInfo.getString(FileTransferColumns.SAVED_FILE_NAME, null);
		String contentType = downloadFileInfo.getString(FileTransferColumns.CONTENT_TYPE, null);

		String fullName = localDir + "/" + ( TextUtils.isEmpty(savedFileName) ? fileName : savedFileName );
		openFile(context, contentType, fullName);
	}

	public static void openFile(Context context, String mimeType, String fileFullName) {
		if ( TextUtils.isEmpty(mimeType) ) {
			showFileTypeChooser(context, fileFullName);
		} else {
			viewFile(context, mimeType, fileFullName);
		}
	}

	public static void viewFile(Context context, String mimeType, String fullName) {
		File file = new File(fullName);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri contentUri = null;

		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ) {
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			try {
				contentUri = LocalFilesProvider.getUriForFile(context, LocalFilesProvider.AUTHORITIES_NAME, file);
			} catch (IllegalArgumentException iae) {
				MsgUtils.showToast(context, R.string.message_unable_to_open_this_file);
				return;
			}
		} else {
			contentUri = Uri.fromFile(file);
		}

		if ( TextUtils.isEmpty(mimeType) ) {
			intent.setData(contentUri);
		} else {
			intent.setDataAndType(contentUri, mimeType);
		}

		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException anfe) {
			if ( Constants.DEBUG ) Log.e(TAG, "viewFile(), Activity not found! file:" + fullName);
			MsgUtils.showToast(context, R.string.message_cannot_find_any_app);
		} catch (Exception e) {
			if ( Constants.DEBUG ) Log.e(TAG, "viewFile(), file:" + fullName + ", Open error! " + e.getMessage());
			MsgUtils.showToast(context, e.getMessage());
		}
	}

	private static void showFileTypeChooser(final Context context, final String fullName) {
		CustomMaterialSimpleListAdapter.Callback callback = new CustomMaterialSimpleListAdapter.Callback() {
			@Override
			public void onMaterialListItemSelected(MaterialDialog dialog, int index, CustomMaterialSimpleListItem item) {
				String mimeType = null;
				switch(index) {
					case 0:
						mimeType = ContentType.IMAGE_ALL;
						break;
					case 1:
						mimeType = ContentType.AUDIO_ALL;
						break;
					case 2:
						mimeType = ContentType.VIDEO_ALL;
						break;
					case 3:
						mimeType = ContentType.TEXT_ALL;
						break;
					case 4:
						mimeType = ContentType.APPLICATION_ALL;
						break;
					default:
						break;
				}
				if ( mimeType != null ) {
					viewFile(context, mimeType, fullName);
				}
				dialog.dismiss();
			}
		};

		CustomMaterialSimpleListAdapter adapter = new CustomMaterialSimpleListAdapter(callback);

		adapter.add(new CustomMaterialSimpleListItem.Builder(context)
			.content(R.string.fileOpenType_image)
			.icon(R.drawable.ic_folder_pictures)
			.build());
		adapter.add(new CustomMaterialSimpleListItem.Builder(context)
			.content(R.string.fileOpenType_music)
			.icon(R.drawable.ic_folder_music)
			.build());
		adapter.add(new CustomMaterialSimpleListItem.Builder(context)
			.content(R.string.fileOpenType_movie)
			.icon(R.drawable.ic_folder_movies)
			.build());
		adapter.add(new CustomMaterialSimpleListItem.Builder(context)
			.content(R.string.fileOpenType_document)
			.icon(R.drawable.ic_file)
			.build());
		adapter.add(new CustomMaterialSimpleListItem.Builder(context)
			.content(R.string.fileOpenType_other)
			.icon(R.drawable.ic_note)
			.build());

		DialogUtils.createSingleChoiceDialog(
			context,
			R.string.openAsDialog_title,
			R.drawable.ic_action_open_file,
			adapter
		).show();
	}

	public static void createRateUsIntent(Context context) {
		int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
		if ( Build.VERSION.SDK_INT >= 21 ) {
			flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
		} else {
			//noinspection deprecation
			flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
		}

		String packageName = context.getPackageName();
		Uri uri = Uri.parse("market://details?id=" + packageName);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.addFlags(flags);
		try {
			context.startActivity(intent);
		} catch (android.content.ActivityNotFoundException e) {
			Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
			intent2.addFlags(flags);
			context.startActivity(intent2);
		}
	}

	public static void createSendFeedbackIntent(Context context) {
		int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
		if ( Build.VERSION.SDK_INT >= 21 ) {
			flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
		} else {
			//noinspection deprecation
			flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
		}

		Uri uri = Uri.parse("mailto:feedback@filelug.com");
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.message_feedback_subject));
		intent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.message_feedback_text));
//		intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("<h1>Sou um H1</h1><p>Eu sou um paragrafo</p><p style=\"color:red;background-color:black;\">Eu sou um paragrafo colorido!</p>"));
//		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///storage/emulated/0/Pictures/Screenshots/Screenshot_2015-11-30-20-42-43.png"));
		intent.addFlags(flags);
		try {
			context.startActivity(intent);
		} catch (android.content.ActivityNotFoundException ex) {
			MsgUtils.showToast(context, "There are no email clients installed.");
		}
	}

	public static void createShareAppToFriendsIntent(Context context) {
		String urlYoutube = context.getResources().getString(R.string.const_url_youtube_video);
		String urlFacebookFanPage = context.getResources().getString(R.string.const_url_facebook_fan_page);
		String urlFilelugWebsite = context.getResources().getString(R.string.const_url_filelug_website);
		String urlAppStore = context.getResources().getString(R.string.const_url_app_store);
		String urlPlayStore = context.getResources().getString(R.string.const_url_play_store);
		String contentText = context.getResources().getString(R.string.page_initial_page_message) + "\n\n" + context.getResources().getString(R.string.message_share_app_to_friends_text_1) + "\n" + urlYoutube + "\n\n" +
				context.getResources().getString(R.string.message_share_app_to_friends_text_2) + "\n" + urlFacebookFanPage + "\n\n" +
				context.getResources().getString(R.string.message_share_app_to_friends_text_3) + "\n" + urlFilelugWebsite + "\n\n" +
				context.getResources().getString(R.string.message_share_app_to_friends_text_4) + "\niOS: " + urlAppStore + "\nAndroid: " +urlPlayStore;
		String subject = context.getResources().getString(R.string.message_share_app_to_friends_subject);

		List<Intent> targetedShareIntents = new ArrayList<Intent>();
		final PackageManager packageManager = context.getPackageManager();
		Intent prototype = createShareAppToFriendsIntent(context, subject, contentText);
		List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(prototype, 0);
		if (!resInfo.isEmpty()) {
			Collections.sort(resInfo, new Comparator<ResolveInfo>() {
				@Override
				public int compare(ResolveInfo first, ResolveInfo second) {
					String firstName = first.activityInfo.loadLabel(packageManager).toString();
					String secondName = second.activityInfo.loadLabel(packageManager).toString();
					return firstName.compareTo(secondName);
				}
			});

			for (ResolveInfo info : resInfo) {
				Intent targetedShare = (Intent)prototype.clone();
				String currentPackageName = context.getPackageName();
				String activityPackageName = info.activityInfo.packageName;
				String activityName = info.activityInfo.name;
				if (!activityPackageName.equalsIgnoreCase(currentPackageName)) {
					targetedShare.setPackage(activityPackageName);
					targetedShare.setClassName(activityPackageName, activityName);
					targetedShareIntents.add(targetedShare);
				}
			}

			String chooseTitle = context.getResources().getString(R.string.title_which_application);
			Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), chooseTitle);
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));

			try {
				context.startActivity(chooserIntent);
			} catch (android.content.ActivityNotFoundException ex) {
				MsgUtils.showToast(context, "There are no app to open.");
			}
		} else {
			MsgUtils.showToast(context, "There are no app to open.");
		}
	}

	private static Intent createShareAppToFriendsIntent(Context context, String subject, String contentText) {
		int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
		if ( Build.VERSION.SDK_INT >= 21 ) {
			flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
		} else {
			//noinspection deprecation
			flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
		}

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.message_share_app_to_friends_subject));
		intent.putExtra(Intent.EXTRA_TEXT, contentText);
		intent.addFlags(flags);
		return intent;
	}

	public static String getSubdirTypeStr(Context context, int index) {
		return context.getResources().getStringArray(R.array.subfolder_type_array)[index];
	}

	public static String getDescriptionTypeStr(Context context, int index) {
		return context.getResources().getStringArray(R.array.description_type_array)[index];
	}

	public static String getNotificationTypeStr(Context context, int index) {
		return context.getResources().getStringArray(R.array.notification_type_array)[index];
	}

	public static String getSubDirTypeSettingText(Context context, int subdirType, String subdirValue) {
		String result = null;
		String subdirType_None = context.getResources().getStringArray(R.array.subfolder_type_array)[0];
		String subdirType_CurrentTimestamp = context.getResources().getStringArray(R.array.subfolder_type_array)[1];
		if ( subdirType == 0 ) {
			result = subdirType_None;
		} else if ( subdirType == 1 ) {
			result = subdirType_CurrentTimestamp;
		} else if ( subdirType == 2 ) {
			result = "{" + ( TextUtils.isEmpty(subdirValue) ? context.getResources().getString(R.string.message_not_set) : subdirValue ) + "}";
		} else if ( subdirType == 3 ) {
			result = subdirType_CurrentTimestamp + "+{" + ( TextUtils.isEmpty(subdirValue) ? context.getResources().getString(R.string.message_not_set) : subdirValue ) + "}";
		} else if ( subdirType == 4 ) {
			result = "{" + ( TextUtils.isEmpty(subdirValue) ? context.getResources().getString(R.string.message_not_set) : subdirValue ) + "}+" + subdirType_CurrentTimestamp;
		}
		return result;
	}

	public static String getCustomizedSubDirName(Context context, int subdirType, String subdirValue) {
		SimpleDateFormat formatter = new SimpleDateFormat(context.getResources().getString(R.string.const_format_dateTime_subdir));
		String dateTimeStr = formatter.format(new Date());
		String result = null;
		if ( subdirType == 1 ) {
			result = dateTimeStr;
		} else if ( subdirType == 2 ) {
			result = subdirValue;
		} else if ( subdirType == 3 ) {
			result = dateTimeStr + subdirValue;
		} else if ( subdirType == 4 ) {
			result = subdirValue + dateTimeStr;
		}
		return result;
	}

	public static String getDescriptionTypeSettingText(Context context, int descriptionType, String descriptionValue) {
		String result = null;
		String descriptionType_None = context.getResources().getStringArray(R.array.description_type_array)[0];
		String descriptionType_FilenameList = context.getResources().getStringArray(R.array.description_type_array)[1];
		if ( descriptionType == 0 ) {
			result = descriptionType_None;
		} else if ( descriptionType == 1 ) {
			result = descriptionType_FilenameList;
		} else if ( descriptionType == 2 ) {
			result = "{" + ( TextUtils.isEmpty(descriptionValue) ? context.getResources().getString(R.string.message_not_set) : descriptionValue ) + "}";
		} else if ( descriptionType == 3 ) {
			result = "{" + ( TextUtils.isEmpty(descriptionValue) ? context.getResources().getString(R.string.message_not_set) : descriptionValue ) + "}" + "+" + descriptionType_FilenameList;
		}
		return result;
	}

	public static String getCustomizedDescription(Context context, int descriptionType, String descriptionValue, String fileListStr, String lineSeparator) {
		String result = null;
		if ( descriptionType == 1 ) {
			result = fileListStr;
		} else if ( descriptionType == 2 ) {
			result = descriptionValue;
		} else if ( descriptionType == 3 ) {
			result = descriptionValue + lineSeparator + lineSeparator + fileListStr;
		}
		return result;
	}

	public static Integer[] intArrayToIntegerArray(int[] intArray) {
		Integer[] result = new Integer[intArray.length];
		for (int i = 0; i < intArray.length; i++) {
			result[i] = Integer.valueOf(intArray[i]);
		}
		return result;
	}

	public static int[] integerArrayToIntegerArray(Integer[] intArray) {
		int[] result = new int[intArray.length];
		for (int i = 0; i < intArray.length; i++) {
			result[i] = intArray[i].intValue();
		}
		return result;
	}

	public static boolean isAcceptedContentType(String acceptedType, String contentType) {
		boolean isAccept = false;

		if ( TextUtils.isEmpty(acceptedType) ) {
			isAccept = true;
		} else if ( ContentType.ALL.equals(acceptedType) ) {
			isAccept = true;
		} else if ( ContentType.FILE_ALL.equals(acceptedType) ) {
			isAccept = true;
		} else if ( ContentType.IMAGE_ALL.equals(acceptedType) ) {
			if ( !TextUtils.isEmpty(contentType) && contentType.startsWith(ContentType.IMAGE) ) {
				isAccept = true;
			}

		} else if ( ContentType.VIDEO_ALL.equals(acceptedType) ) {
			if ( !TextUtils.isEmpty(contentType) && contentType.startsWith(ContentType.VIDEO) ) {
				isAccept = true;
			}
		} else if ( ContentType.AUDIO_ALL.equals(acceptedType) ) {
			if ( !TextUtils.isEmpty(contentType) && contentType.startsWith(ContentType.AUDIO) ) {
				isAccept = true;
			}
		} else if ( ContentType.TEXT_ALL.equals(acceptedType) ) {
			if ( !TextUtils.isEmpty(contentType) && contentType.startsWith(ContentType.TEXT) ) {
				isAccept = true;
			}
		} else if ( ContentType.APPLICATION_ALL.equals(acceptedType) ) {
			if ( !TextUtils.isEmpty(contentType) && contentType.startsWith(ContentType.APPLICATION) ) {
				isAccept = true;
			}
		} else if ( acceptedType.equals(contentType) ) {
			isAccept = true;
		}

		return isAccept;
	}

	public static String convertBundleToString(Bundle bundle) {
		String result = null;
		if ( bundle == null ) {
			return result;
		}
		Set<String> keys = bundle.keySet();
		result = "";
		if ( bundle.keySet().size() == 0 ) {
			result += "{}";
			return result;
		}
		for ( String key : keys ) {
			Object value = bundle.get(key);
			result += ( result.length() == 0 ? "{ " : ", " ) + key + "(" + value.getClass().getName() + ")=" + bundle.get(key);
			if ( value instanceof Bundle ) {
				result += convertBundleToString((Bundle)value);
			} else {
				result += "[S]" +value.toString();
			}
		}
		result += " }";
		return result;
	}

	public static RepositoryErrorObject getErrorObject(Context context, Exception e, @Nullable Account account) {
		int errorCode = -1;
		String errorMsg = null;

		if ( e instanceof JSONException ) {
			JSONException jse = (JSONException)e;
			errorMsg = jse.getMessage();
		} else if ( e instanceof InterruptedException ) {
			InterruptedException ie = (InterruptedException)e;
			errorMsg = ie.getMessage();
		} else if ( e instanceof ExecutionException ) {
			ExecutionException ee = (ExecutionException)e;
			if ( ee.getCause() instanceof VolleyError) {
				VolleyError volleyError = (VolleyError)ee.getCause();
				errorCode = MiscUtils.getStatusCode(volleyError);
				errorMsg = MiscUtils.getVolleyErrorMessage(volleyError);
			} else {
				errorMsg = ee.getMessage();
			}
		} else if ( e instanceof TimeoutException ) {
			TimeoutException te = (TimeoutException)e;
			errorMsg = context.getString(R.string.message_timeout_error);
		} else {
			errorMsg = e.getMessage();
		}

		boolean isActivity = false;
		if ( context instanceof Activity ) {
			isActivity = true;
		}

		if ( errorCode > -1 ) {
			if ( account == null ) {
				if ( errorCode == Constants.HTTP_STATUS_CODE_FORBIDDEN ) {
					FilelugUtils.actionWhen403();
				} else if ( errorCode == Constants.HTTP_STATUS_CODE_NOT_IMPLEMENTED ) {
					FilelugUtils.actionWhen501();
				} else if ( errorCode == Constants.HTTP_STATUS_CODE_SERVICE_UNAVAILABLE ) {
					FilelugUtils.actionWhen503(context);
				}
			} else {
				if ( errorCode == Constants.HTTP_STATUS_CODE_FORBIDDEN ) {
					FilelugUtils.actionWhen403(account);
				} else if ( errorCode == Constants.HTTP_STATUS_CODE_NOT_IMPLEMENTED ) {
					FilelugUtils.actionWhen501(account);
				} else if ( errorCode == Constants.HTTP_STATUS_CODE_SERVICE_UNAVAILABLE ) {
					FilelugUtils.actionWhen503(context, account, isActivity);
				}
			}
		}

		return new RepositoryErrorObject(errorCode, errorMsg);
	}

	public static String getFilelugVersion(Context context) {
		String versionName = null;
		try {
			PackageManager pkgManager = context.getPackageManager();
			String pkgName = context.getPackageName();
			PackageInfo pkgInfo = pkgManager.getPackageInfo(pkgName, 0);
			versionName = pkgInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
		}
		return versionName;
	}

	public static void overridePendingTransition(Activity activity, int enterAnim, int exitAnim) {
		activity.overridePendingTransition(enterAnim, exitAnim);
	}

}
