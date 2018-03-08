package com.filelug.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.filelug.android.MainApplication;
import com.filelug.android.R;
import com.filelug.android.messaging.PushService;

import java.util.Locale;

public class PrefUtils {

	private static final String TAG = PrefUtils.class.getSimpleName();
	private static Context ctx = MainApplication.getInstance().getApplicationContext();
	private static Resources resources = ctx.getResources();

	public static final String PREF_ACTIVE_ACCOUNT = resources.getString(R.string.pref_active_account);
	public static final String PREF_RELOAD_REMOTE_ROOT_DIR = resources.getString(R.string.pref_reload_remote_root_dir);
	public static final String PREF_ACCOUNT_LIST_GENERATED = resources.getString(R.string.pref_account_list_generated);
	public static final String PREF_ACCOUNTS = resources.getString(R.string.pref_accounts);
	public static final String PREF_ULH_SEARCH_TYPE = resources.getString(R.string.pref_ulh_search_type);
	public static final String PREF_DLH_SEARCH_TYPE = resources.getString(R.string.pref_dlh_search_type);
	public static final String PREF_PUSH_SERVICE_TYPE = resources.getString(R.string.pref_push_service_type);
	public static final String PREF_PUSH_SERVICE_TOKEN = resources.getString(R.string.pref_push_service_token);
	public static final String PREF_GCM_TOKEN_CHANGED = resources.getString(R.string.pref_gcm_token_changed);
	public static final String PREF_SHOW_HIDDEN_FILES = resources.getString(R.string.pref_show_hidden_files);
	public static final String PREF_SHOW_LOCAL_SYSTEM_FOLDER = resources.getString(R.string.pref_show_local_system_folder);
	public static final String PREF_SHOW_THUMBNAIL = resources.getString(R.string.pref_show_thumbnail);
	public static final String PREF_SHOW_MODIFIED_DATE_AND_PERMISSION = resources.getString(R.string.pref_show_modified_date_and_permission);
	public static final String PREF_REMEMBER_RECENT_FOLDER = resources.getString(R.string.pref_remember_recent_folder);
	public static final String PREF_DISPLAY_LANGUAGE = resources.getString(R.string.pref_display_language);
	public static final String PREF_APP_OPEN_PASSWORD = resources.getString(R.string.pref_app_open_password);
	public static final String PREF_TRANSFER_PASSWORD = resources.getString(R.string.pref_transfer_password);
	public static final String PREF_FILE_OPEN_PASSWORD = resources.getString(R.string.pref_file_open_password);
	public static final String PREF_UPLOAD_PATH = resources.getString(R.string.pref_upload_path);
	public static final String PREF_UPLOAD_SUB_DIR_TYPE = resources.getString(R.string.pref_upload_subdir);
	public static final String PREF_UPLOAD_SUB_DIR_VALUE = resources.getString(R.string.pref_upload_subdir_value);
	public static final String PREF_UPLOAD_DESCRIPTION_TYPE = resources.getString(R.string.pref_upload_description_type);
	public static final String PREF_UPLOAD_DESCRIPTION_VALUE = resources.getString(R.string.pref_upload_description_value);
	public static final String PREF_UPLOAD_NOTIFICATION_TYPE = resources.getString(R.string.pref_upload_notification_type);
	public static final String PREF_DOWNLOAD_PATH = resources.getString(R.string.pref_download_path);
	public static final String PREF_DOWNLOAD_SUB_DIR_TYPE = resources.getString(R.string.pref_download_subdir);
	public static final String PREF_DOWNLOAD_SUB_DIR_VALUE = resources.getString(R.string.pref_download_subdir_value);
	public static final String PREF_DOWNLOAD_DESCRIPTION_TYPE = resources.getString(R.string.pref_download_description_type);
	public static final String PREF_DOWNLOAD_DESCRIPTION_VALUE = resources.getString(R.string.pref_download_description_value);
	public static final String PREF_DOWNLOAD_NOTIFICATION_TYPE = resources.getString(R.string.pref_download_notification_type);
	public static final String PREF_LOCAL_DIR_SORT_BY = resources.getString(R.string.pref_local_dir_sort_by);
	public static final String PREF_LOCAL_DIR_SORT_TYPE = resources.getString(R.string.pref_local_dir_sort_type);
	public static final String PREF_REMOTE_DIR_SORT_BY = resources.getString(R.string.pref_remote_dir_sort_by);
	public static final String PREF_REMOTE_DIR_SORT_TYPE = resources.getString(R.string.pref_remote_dir_sort_type);

	private static boolean DEFAULT_VALUE_SHOW_HIDDEN_FILES = false;
	private static boolean DEFAULT_VALUE_SHOW_LOCAL_SYSTEM_FOLDER = false;
	private static boolean DEFAULT_VALUE_SHOW_THUMBNAIL = false;
	private static boolean DEFAULT_VALUE_SHOW_MODIFIED_DATE_AND_PERMISSION = false;
	private static boolean DEFAULT_VALUE_REMEMBER_RECENT_FOLDER = false;
	private static boolean DEFAULT_VALUE_APP_OPEN_PASSWORD = false;
	private static boolean DEFAULT_VALUE_TRANSFER_PASSWORD = false;
	private static boolean DEFAULT_VALUE_FILE_OPEN_PASSWORD = false;
	public static int DEFAULT_VALUE_UPLOAD_SUB_DIR = 0;
	public static int DEFAULT_VALUE_UPLOAD_DESCRIPTION_TYPE = 0;
	public static int DEFAULT_VALUE_UPLOAD_NOTIFICATION_TYPE = 2;
	public static int DEFAULT_VALUE_DOWNLOAD_SUB_DIR = 0;
	public static int DEFAULT_VALUE_DOWNLOAD_DESCRIPTION_TYPE = 0;
	public static int DEFAULT_VALUE_DOWNLOAD_NOTIFICATION_TYPE = 2;

	public static boolean isReloadRemoteRootDir() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getBoolean(PREF_RELOAD_REMOTE_ROOT_DIR, false);
	}

	public static void setReloadRemoteRootDir(boolean reloadRemoteRootDir) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putBoolean(PREF_RELOAD_REMOTE_ROOT_DIR, reloadRemoteRootDir).apply();
	}

	public static boolean isShowHiddenFiles() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getBoolean(PREF_SHOW_HIDDEN_FILES, DEFAULT_VALUE_SHOW_HIDDEN_FILES);
	}

	public static void setShowHiddenFiles(boolean showHiddenFiles) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putBoolean(PREF_SHOW_HIDDEN_FILES, showHiddenFiles).apply();
	}

	public static boolean isShowLocalSystemFolder() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getBoolean(PREF_SHOW_LOCAL_SYSTEM_FOLDER, DEFAULT_VALUE_SHOW_LOCAL_SYSTEM_FOLDER);
	}

	public static void setShowLocalSystemFolder(boolean showLocalSystemFolder) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putBoolean(PREF_SHOW_LOCAL_SYSTEM_FOLDER, showLocalSystemFolder).apply();
	}

	public static boolean isShowThumbnail() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getBoolean(PREF_SHOW_THUMBNAIL, DEFAULT_VALUE_SHOW_THUMBNAIL);
	}

	public static void setShowThumbnail(boolean showThumbnail) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putBoolean(PREF_SHOW_THUMBNAIL, showThumbnail).apply();
	}

	public static boolean isShowModifiedDateAndPermission() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getBoolean(PREF_SHOW_MODIFIED_DATE_AND_PERMISSION, DEFAULT_VALUE_SHOW_MODIFIED_DATE_AND_PERMISSION);
	}

	public static void setShowModifiedDateAndPermission(boolean showModifiedDateAndPermission) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putBoolean(PREF_SHOW_MODIFIED_DATE_AND_PERMISSION, showModifiedDateAndPermission).apply();
	}

	public static boolean isRememberRecentFolder() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getBoolean(PREF_REMEMBER_RECENT_FOLDER, DEFAULT_VALUE_REMEMBER_RECENT_FOLDER);
	}

	public static void setRememberRecentFolder(boolean rememberRecentFolder) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putBoolean(PREF_REMEMBER_RECENT_FOLDER, rememberRecentFolder).apply();
	}

	public static Locale getDisplayLocale() {
		String displayLanguage = PrefUtils.getDisplayLanguage();
		Locale locale = null;
		if ( !TextUtils.isEmpty(displayLanguage) ) {
			int idx = displayLanguage.indexOf('_');
			if (idx != -1) {
				String[] split = displayLanguage.split("_");
				locale = new Locale(split[0], split[1]);
			} else {
				locale = new Locale(displayLanguage);
			}
		}
		return locale;
	}

	public static String getDisplayLanguage() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(PREF_DISPLAY_LANGUAGE, null);
	}

	public static void setDisplayLanguage(String displayLanguage) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putString(PREF_DISPLAY_LANGUAGE, displayLanguage).apply();
	}

	public static boolean isAppOpenPassword() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getBoolean(PREF_APP_OPEN_PASSWORD, DEFAULT_VALUE_APP_OPEN_PASSWORD);
	}

	public static void setAppOpenPassword(boolean appOpenPassword) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putBoolean(PREF_APP_OPEN_PASSWORD, appOpenPassword).apply();
	}

	public static boolean isTransferPassword() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getBoolean(PREF_TRANSFER_PASSWORD, DEFAULT_VALUE_TRANSFER_PASSWORD);
	}

	public static void setTransferPassword(boolean transferPassword) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putBoolean(PREF_TRANSFER_PASSWORD, transferPassword).apply();
	}

	public static boolean isFileOpenPassword() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getBoolean(PREF_FILE_OPEN_PASSWORD, DEFAULT_VALUE_FILE_OPEN_PASSWORD);
	}

	public static void setFileOpenPassword(boolean fileOpenPassword) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putBoolean(PREF_FILE_OPEN_PASSWORD, fileOpenPassword).apply();
	}

	public static String getUploadPath(String accountName) {
		String key = PREF_UPLOAD_PATH;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(key, null);
	}

	public static void setUploadPath(String accountName, String uploadPath) {
		String key = PREF_UPLOAD_PATH;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		if ( TextUtils.isEmpty(uploadPath) ) {
			sp.edit().remove(key).apply();
		} else {
			sp.edit().putString(key, uploadPath).apply();
		}
	}

	public static String getUploadSubdirType(String accountName) {
		String key = PREF_UPLOAD_SUB_DIR_TYPE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(key, null);
	}

	public static void setUploadSubdirType(String accountName, String uploadSubdirType) {
		String key = PREF_UPLOAD_SUB_DIR_TYPE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		if ( TextUtils.isEmpty(uploadSubdirType) ) {
			sp.edit().remove(key).apply();
		} else {
			sp.edit().putString(key, uploadSubdirType).apply();
		}
	}

	public static String getUploadSubdirValue(String accountName) {
		String key = PREF_UPLOAD_SUB_DIR_VALUE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(key, null);
	}

	public static void setUploadSubdirValue(String accountName, String uploadSubdirValue) {
		String key = PREF_UPLOAD_SUB_DIR_VALUE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		if ( TextUtils.isEmpty(uploadSubdirValue) ) {
			sp.edit().remove(key).apply();
		} else {
			sp.edit().putString(key, uploadSubdirValue).apply();
		}
	}

	public static String getUploadDescriptionType(String accountName) {
		String key = PREF_UPLOAD_DESCRIPTION_TYPE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(key, null);
	}

	public static void setUploadDescriptionType(String accountName, String uploadDescriptionType) {
		String key = PREF_UPLOAD_DESCRIPTION_TYPE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		if ( TextUtils.isEmpty(uploadDescriptionType) ) {
			sp.edit().remove(key).apply();
		} else {
			sp.edit().putString(key, uploadDescriptionType).apply();
		}
	}

	public static String getUploadDescriptionValue(String accountName) {
		String key = PREF_UPLOAD_DESCRIPTION_VALUE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(key, null);
	}

	public static void setUploadDescriptionValue(String accountName, String uploadDescriptionValue) {
		String key = PREF_UPLOAD_DESCRIPTION_VALUE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		if ( TextUtils.isEmpty(uploadDescriptionValue) ) {
			sp.edit().remove(key).apply();
		} else {
			sp.edit().putString(key, uploadDescriptionValue).apply();
		}
	}

	public static String getUploadNotificationType(String accountName) {
		String key = PREF_UPLOAD_NOTIFICATION_TYPE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(key, null);
	}

	public static void setUploadNotificationType(String accountName, String uploadNotificationType) {
		String key = PREF_UPLOAD_NOTIFICATION_TYPE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		if ( TextUtils.isEmpty(uploadNotificationType) ) {
			sp.edit().remove(key).apply();
		} else {
			sp.edit().putString(key, uploadNotificationType).apply();
		}
	}

	public static String getDownloadPath(String accountName) {
		String key = PREF_DOWNLOAD_PATH;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(key, null);
	}

	public static void setDownloadPath(String accountName, String downloadPath) {
		String key = PREF_DOWNLOAD_PATH;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		if ( TextUtils.isEmpty(downloadPath) ) {
			sp.edit().remove(key).apply();
		} else {
			sp.edit().putString(key, downloadPath).apply();
		}
	}

	public static String getDownloadSubdirType(String accountName) {
		String key = PREF_DOWNLOAD_SUB_DIR_TYPE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(key, null);
	}

	public static void setDownloadSubdirType(String accountName, String downloadSubdirType) {
		String key = PREF_DOWNLOAD_SUB_DIR_TYPE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		if ( TextUtils.isEmpty(downloadSubdirType) ) {
			sp.edit().remove(key).apply();
		} else {
			sp.edit().putString(key, downloadSubdirType).apply();
		}
	}

	public static String getDownloadSubdirValue(String accountName) {
		String key = PREF_DOWNLOAD_SUB_DIR_VALUE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(key, null);
	}

	public static void setDownloadSubdirValue(String accountName, String downloadSubdirValue) {
		String key = PREF_DOWNLOAD_SUB_DIR_VALUE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		if ( TextUtils.isEmpty(downloadSubdirValue) ) {
			sp.edit().remove(key).apply();
		} else {
			sp.edit().putString(key, downloadSubdirValue).apply();
		}
	}

	public static String getDownloadDescriptionType(String accountName) {
		String key = PREF_DOWNLOAD_DESCRIPTION_TYPE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(key, null);
	}

	public static void setDownloadDescriptionType(String accountName, String downloadDescriptionType) {
		String key = PREF_DOWNLOAD_DESCRIPTION_TYPE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		if ( TextUtils.isEmpty(downloadDescriptionType) ) {
			sp.edit().remove(key).apply();
		} else {
			sp.edit().putString(key, downloadDescriptionType).apply();
		}
	}

	public static String getDownloadDescriptionValue(String accountName) {
		String key = PREF_DOWNLOAD_DESCRIPTION_VALUE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(key, null);
	}

	public static void setDownloadDescriptionValue(String accountName, String downloadDescriptionValue) {
		String key = PREF_DOWNLOAD_DESCRIPTION_VALUE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		if ( TextUtils.isEmpty(downloadDescriptionValue) ) {
			sp.edit().remove(key).apply();
		} else {
			sp.edit().putString(key, downloadDescriptionValue).apply();
		}
	}

	public static String getDownloadNotificationType(String accountName) {
		String key = PREF_DOWNLOAD_NOTIFICATION_TYPE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(key, null);
	}

	public static void setDownloadNotificationType(String accountName, String downloadNotificationType) {
		String key = PREF_DOWNLOAD_NOTIFICATION_TYPE;
		if ( !TextUtils.isEmpty(accountName) ) {
			key += accountName;
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		if ( TextUtils.isEmpty(downloadNotificationType) ) {
			sp.edit().remove(key).apply();
		} else {
			sp.edit().putString(key, downloadNotificationType).apply();
		}
	}

	public static String getActiveAccount() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(PREF_ACTIVE_ACCOUNT, null);
	}

	public static void setActiveAccount(String accountName) {
//		if ( Constants.DEBUG ) Log.d(TAG, "Set active account to: " + accountName);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		if ( TextUtils.isEmpty(accountName) ) {
			sp.edit().remove(PREF_ACTIVE_ACCOUNT).apply();
		} else {
			sp.edit().putString(PREF_ACTIVE_ACCOUNT, accountName).apply();
		}
	}

	public static void cleanActiveInfo(String activeAccountName) {
		String[] accounts = getAccounts();
		String accountsStr = "";
		if ( accounts != null && accounts.length > 0 ) {
			for ( int i=0; i<accounts.length; i++ ) {
				String account = accounts[i];
				if ( account.equals(activeAccountName) ) {
					cleanAccountPreference(activeAccountName);
					continue;
				}
				accountsStr += ( TextUtils.isEmpty(accountsStr) ? "" : AccountUtils.SEPARATOR_ACCOUNTS ) + account;
			}
		}
//		if ( Constants.DEBUG ) Log.d(TAG, "cleanActiveInfo(), New accounts string: " + accountsStr);
		setActiveAccount(null);
		setAccounts(accountsStr);
	}

	private static void cleanAccountPreference(String accountName) {
		setUploadPath(null, null);
		setUploadSubdirType(null, null);
		setUploadSubdirValue(null, null);
		setUploadDescriptionType(null, null);
		setUploadDescriptionValue(null, null);
		setUploadNotificationType(null, null);
		setDownloadPath(null, null);
		setDownloadSubdirType(null, null);
		setDownloadSubdirValue(null, null);
		setDownloadDescriptionType(null, null);
		setDownloadDescriptionValue(null, null);
		setDownloadNotificationType(null, null);

		setUploadPath(accountName, null);
		setUploadSubdirType(accountName, null);
		setUploadSubdirValue(accountName, null);
		setUploadDescriptionType(accountName, null);
		setUploadDescriptionValue(accountName, null);
		setUploadNotificationType(accountName, null);
		setDownloadPath(accountName, null);
		setDownloadSubdirType(accountName, null);
		setDownloadSubdirValue(accountName, null);
		setDownloadDescriptionType(accountName, null);
		setDownloadDescriptionValue(accountName, null);
		setDownloadNotificationType(accountName, null);
	}

	public static int getDownloadSearchType() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getInt(PREF_DLH_SEARCH_TYPE, -1);
	}

	public static void setDownloadSearchType(int searchType) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putInt(PREF_DLH_SEARCH_TYPE, searchType).apply();
	}

	public static int getUploadSearchType() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getInt(PREF_ULH_SEARCH_TYPE, -1);
	}

	public static void setUploadSearchType(int searchType) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putInt(PREF_ULH_SEARCH_TYPE, searchType).apply();
	}

	public static int getPushServiceType() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getInt(PREF_PUSH_SERVICE_TYPE, -1);
	}

	public static String getPushServiceTypeString() {
		String pushServiceTypeStr = null;
		int pushServiceType = getPushServiceType();
		if ( pushServiceType == PushService.PUSH_SERVICE_TYPE_SNS_MOBILE_SERVICE ) {
			pushServiceTypeStr = PushService.PUSH_SERVICE_TYPE_SNS_MOBILE_SERVICE_VALUE;
		} else if ( pushServiceType == PushService.PUSH_SERVICE_TYPE_GCM ) {
			pushServiceTypeStr = PushService.PUSH_SERVICE_TYPE_GCM_VALUE;
		} else if ( pushServiceType == PushService.PUSH_SERVICE_TYPE_BAIDU ) {
			pushServiceTypeStr = PushService.PUSH_SERVICE_TYPE_BAIDU_VALUE;
		}
		return pushServiceTypeStr;
	}

	public static void setPushServiceType(int pushServiceType) {
//		if ( Constants.DEBUG ) Log.d(TAG, "Set pushServiceType to: " + pushServiceType);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putInt(PREF_PUSH_SERVICE_TYPE, pushServiceType).apply();
	}

	public static String getPushServiceToken() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(PREF_PUSH_SERVICE_TOKEN, null);
	}

	public static void setPushServiceToken(String pushServiceToken) {
//		if ( Constants.DEBUG ) Log.d(TAG, "Set pushServiceToken to: " + pushServiceToken);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		if ( TextUtils.isEmpty(pushServiceToken) ) {
			sp.edit().remove(PREF_PUSH_SERVICE_TOKEN).apply();
		} else {
			sp.edit().putString(PREF_PUSH_SERVICE_TOKEN, pushServiceToken).apply();
		}
	}

	public static boolean getGCMTokenChanged() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getBoolean(PREF_GCM_TOKEN_CHANGED, false);
	}

	public static void setGCMTokenChanged(boolean gcmTokenChanged) {
//		if ( Constants.DEBUG ) Log.d(TAG, "Set gcmTokenChanged to: " + gcmTokenChanged);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putBoolean(PREF_GCM_TOKEN_CHANGED, gcmTokenChanged).apply();
	}

	public static boolean isAccountListGenerated() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getBoolean(PREF_ACCOUNT_LIST_GENERATED, false);
	}

	public static void markAccountListGenerated() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putBoolean(PREF_ACCOUNT_LIST_GENERATED, true).apply();
	}

	public static void generateAccounts() {
		String accounts = AccountUtils.getAccountsString();
//		if ( Constants.DEBUG ) Log.d(TAG, "generateAccounts(): accounts=" + accounts);
		PrefUtils.setAccounts(accounts);
		PrefUtils.markAccountListGenerated();
	}

	public static String[] getAccounts() {
		String[] accounts = null;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		String accountStr = sp.getString(PREF_ACCOUNTS, null);
		if ( !TextUtils.isEmpty(accountStr) ) {
			accounts = accountStr.split(AccountUtils.SEPARATOR_ACCOUNTS);
		}
		return accounts;
	}

	public static void setAccounts(String accountsStr) {
//		if ( Constants.DEBUG ) Log.d(TAG, "Set accountsStr to: " + accountsStr);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		if ( TextUtils.isEmpty(accountsStr) ) {
			sp.edit().remove(PREF_ACCOUNTS).apply();
		} else {
			sp.edit().putString(PREF_ACCOUNTS, accountsStr).apply();
		}
	}

	public static int getLocalDirSortBy() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getInt(PREF_LOCAL_DIR_SORT_BY, -1);
	}

	public static void setLocalDirSortBy(int localDirSortBy) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putInt(PREF_LOCAL_DIR_SORT_BY, localDirSortBy).apply();
	}

	public static int getLocalDirSortType() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getInt(PREF_LOCAL_DIR_SORT_TYPE, -1);
	}

	public static void setLocalDirSortType(int localDirSortType) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putInt(PREF_LOCAL_DIR_SORT_TYPE, localDirSortType).apply();
	}

	public static int getRemoteDirSortBy() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getInt(PREF_REMOTE_DIR_SORT_BY, -1);
	}

	public static void setRemoteDirSortBy(int remoteDirSortBy) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putInt(PREF_REMOTE_DIR_SORT_BY, remoteDirSortBy).apply();
	}

	public static int getRemoteDirSortType() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getInt(PREF_REMOTE_DIR_SORT_TYPE, -1);
	}

	public static void setRemoteDirSortType(int remoteDirSortType) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putInt(PREF_REMOTE_DIR_SORT_TYPE, remoteDirSortType).apply();
	}

	public static boolean isShowInitialPage() {
//		Log.d(TAG, "isShowInitialPage()");
		if ( !PrefUtils.isAccountListGenerated() ) {
			PrefUtils.generateAccounts();
		}
		boolean showInitPage = true;
		String[] accounts = getAccounts();
//		Log.d(TAG, "isShowInitialPage(): accounts=" + (accounts == null ? "" : TextUtils.join(";", accounts)));
		if ( accounts != null && accounts.length > 0 )
			showInitPage = false;
		return showInitPage;
	}

	public static void replaceAccountPreference(String oldAccountName, String newAccountName) {
//		Log.d(TAG, "replaceAccountPreference(): oldAccountName=" + oldAccountName + ", newAccountName=" + newAccountName);
		String uploadPath = getUploadPath(oldAccountName);
		String uploadSubdirType = getUploadSubdirType(oldAccountName);
		String uploadSubdirValue = getUploadSubdirValue(oldAccountName);
		String uploadDescriptionType = getUploadDescriptionType(oldAccountName);
		String uploadDescriptionValue = getUploadDescriptionValue(oldAccountName);
		String uploadNotificationType = getUploadNotificationType(oldAccountName);
		String downloadPath = getDownloadPath(oldAccountName);
		String downloadSubdirType = getDownloadSubdirType(oldAccountName);
		String downloadSubdirValue = getDownloadSubdirValue(oldAccountName);
		String downloadDescriptionType = getDownloadDescriptionType(oldAccountName);
		String downloadDescriptionValue = getDownloadDescriptionValue(oldAccountName);
		String downloadNotificationType = getDownloadNotificationType(oldAccountName);
//		Log.d(TAG, "replaceAccountPreference(): uploadPath=" + uploadPath + ", uploadSubdirType=" + uploadSubdirType + ", uploadSubdirValue=" + uploadSubdirValue + ", uploadDescriptionType=" + uploadDescriptionType + ", uploadDescriptionValue=" + uploadDescriptionValue + ", uploadNotificationType=" + uploadNotificationType + "\n" +
//				   ", downloadPath=" + downloadPath + ", downloadSubdirType=" + downloadSubdirType + ", downloadSubdirValue=" + downloadSubdirValue + ", downloadDescriptionType=" + downloadDescriptionType + ", downloadDescriptionValue=" + downloadDescriptionValue + ", downloadNotificationType=" + downloadNotificationType);
		if ( !TextUtils.isEmpty(uploadPath) ) {
			setUploadPath(newAccountName, uploadPath);
			setUploadPath(oldAccountName, null);
		}
		if ( !TextUtils.isEmpty(uploadSubdirType) ) {
			setUploadSubdirType(newAccountName, uploadSubdirType);
			setUploadSubdirType(oldAccountName, null);
		}
		if ( !TextUtils.isEmpty(uploadSubdirValue) ) {
			setUploadSubdirValue(newAccountName, uploadSubdirValue);
			setUploadSubdirValue(oldAccountName, null);
		}
		if ( !TextUtils.isEmpty(uploadDescriptionType) ) {
			setUploadDescriptionType(newAccountName, uploadDescriptionType);
			setUploadDescriptionType(oldAccountName, null);
		}
		if ( !TextUtils.isEmpty(uploadDescriptionValue) ) {
			setUploadDescriptionValue(newAccountName, uploadDescriptionValue);
			setUploadDescriptionValue(oldAccountName, null);
		}
		if ( !TextUtils.isEmpty(uploadNotificationType) ) {
			setUploadNotificationType(newAccountName, uploadNotificationType);
			setUploadNotificationType(oldAccountName, null);
		}
		if ( !TextUtils.isEmpty(downloadPath) ) {
			setDownloadPath(newAccountName, downloadPath);
			setDownloadPath(oldAccountName, null);
		}
		if ( !TextUtils.isEmpty(downloadSubdirType) ) {
			setDownloadSubdirType(newAccountName, downloadSubdirType);
			setDownloadSubdirType(oldAccountName, null);
		}
		if ( !TextUtils.isEmpty(downloadSubdirValue) ) {
			setDownloadSubdirValue(newAccountName, downloadSubdirValue);
			setDownloadSubdirValue(oldAccountName, null);
		}
		if ( !TextUtils.isEmpty(downloadDescriptionType) ) {
			setDownloadDescriptionType(newAccountName, downloadDescriptionType);
			setDownloadDescriptionType(oldAccountName, null);
		}
		if ( !TextUtils.isEmpty(downloadDescriptionValue) ) {
			setDownloadDescriptionValue(newAccountName, downloadDescriptionValue);
			setDownloadDescriptionValue(oldAccountName, null);
		}
		if ( !TextUtils.isEmpty(downloadNotificationType) ) {
			setDownloadNotificationType(newAccountName, downloadNotificationType);
			setDownloadNotificationType(oldAccountName, null);
		}
	}

}
