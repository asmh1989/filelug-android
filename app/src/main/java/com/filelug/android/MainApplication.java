package com.filelug.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.accountkit.AccountKit;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.PrefUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

//import com.android.volley.toolbox.ImageLoader;
//import com.filelug.android.util._LruBitmapCache;
//import com.filelug.android.util._LruBitmapCache;

public class MainApplication extends Application {

	private static final String TAG = MainApplication.class.getSimpleName();

	public static final boolean DEBUG = true;

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	private static MainApplication mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
//		setLocale();
		Context context = getApplicationContext();
		Account[] accounts = AccountUtils.getFilelugAccounts();
		if ( accounts != null && accounts.length > 0 ) {
			convertOldAccountName(context, accounts);
		}
		if ( !PrefUtils.isAccountListGenerated() ) {
			PrefUtils.generateAccounts();
		}
		initImageLoader(context);
//		LocalFileUtils._analysisDeviceFS(context);
//		LocalFileUtils._queryMediaTypes(context);
//		PrefUtils.setShowLocalHiddenFiles(false);
//		PrefUtils.setShowRemoteHiddenFiles(false);

		AccountKit.initialize(context, new AccountKit.InitializeCallback() {
			@Override
			public void onInitialized() {
				boolean isInitialized = AccountKit.isInitialized();
//				if ( Constants.DEBUG ) Log.d(TAG, "onCreate(): isInitialized=" + isInitialized);
			}
		});
	}

	private void convertOldAccountName(Context context, Account[] accounts) {
		String activeAccountName = PrefUtils.getActiveAccount();
		String newActiveAccountName = null;

//		Log.d(TAG, "convertOldAccountName(): activeAccountName=" + activeAccountName);

		AccountManager accountManager = AccountManager.get(context);

		for ( Account account : accounts ) {
			String countryId = accountManager.getUserData(account, Constants.PARAM_COUNTRY_ID);
			String phone = accountManager.getUserData(account, Constants.PARAM_PHONE);
			String filelugAccount = accountManager.getUserData(account, Constants.EXT_PARAM_FILELUG_ACCOUNT);
			String sessionId = accountManager.getUserData(account, Constants.PARAM_SESSION_ID);
			final String oldAccountName = String.format(Constants.OLD_USER_ACCOUNT_PATTERN, countryId, phone);

//			Log.d(TAG, "convertOldAccountName(): account=" + account.name + ", oldAccountName=" + oldAccountName + ", countryId=" + countryId + ", phone=" + phone + ", filelugAccount=" + countryId + ", sessionId=" + sessionId);

			if ( !account.name.equals(oldAccountName) ) {
				continue;
			}

			String newAccountName = String.format(Constants.USER_ACCOUNT_PATTERN, "886", phone.substring(1, phone.length()));
			Account newAccount = AccountUtils.getAccount(newAccountName);
//			Log.d(TAG, "convertOldAccountName(): newAccountName=" + newAccountName);

			if ( account.name.equals(activeAccountName) ) {
				newActiveAccountName = newAccountName;
			}

			if ( newAccount == null ) {
//				Log.d(TAG, "convertOldAccountName(): Add new account " + newAccountName);

				Bundle userData = new Bundle();
				userData.putString(Constants.PARAM_COUNTRY_ID, countryId);
				userData.putString(Constants.PARAM_PHONE, phone);
				userData.putString(Constants.EXT_PARAM_FILELUG_ACCOUNT, filelugAccount);
				userData.putString(Constants.PARAM_SESSION_ID, sessionId);
				userData.putString(Constants.EXT_PARAM_LOGGED_IN, Boolean.FALSE.toString());

				newAccount = new Account(newAccountName, Constants.ACCOUNT_TYPE_FILELUG);
				AccountUtils.addAccount(newAccount, Constants.AUTH_TOKEN_TYPE_GENERAL_SERVICE, sessionId, userData);

				PrefUtils.replaceAccountPreference(oldAccountName, newAccountName);
			}
		}

		PrefUtils.generateAccounts();

		if ( !TextUtils.isEmpty(newActiveAccountName) ) {
			PrefUtils.setActiveAccount(newActiveAccountName);
		}
	}

	public static synchronized MainApplication getInstance() {
		return mInstance;
	}

/*
	public void setLocale() {
		Locale configLocale = null;
		Locale prefLocale = PrefUtils.getDisplayLocale();
		Configuration config = getBaseContext().getResources().getConfiguration();
		Locale systemLocale = config.locale;
		if ( prefLocale == null ) {
			if ( Locale.ENGLISH.equals(systemLocale) ||
				 Locale.SIMPLIFIED_CHINESE.equals(systemLocale) ||
				 Locale.TRADITIONAL_CHINESE.equals(systemLocale) ) {
				configLocale = systemLocale;
			} else {
				configLocale = Locale.ENGLISH;
			}
			PrefUtils.setDisplayLanguage(configLocale.toString());
		} else if ( !systemLocale.equals(prefLocale) ) {
			configLocale = prefLocale;
		}
		if ( configLocale != null ) {
			overwriteConfigurationLocale(config, configLocale);
		}
	}

	private void overwriteConfigurationLocale(Configuration config, Locale locale) {
		Locale.setDefault(locale);
		Configuration newConfig = new Configuration();
		//config.locale = locale; //用這個config, 畫面會一直閃燦
		newConfig.locale = locale;
		Resources res = getBaseContext().getResources();
		res.updateConfiguration(newConfig, res.getDisplayMetrics());
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Locale prefLocale = PrefUtils.getDisplayLocale();
		Locale systemLocale = newConfig.locale;
		if ( !systemLocale.equals(prefLocale) ) {
			overwriteConfigurationLocale(newConfig, prefLocale);
		}
		super.onConfigurationChanged(newConfig);
	}
*/

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		return mRequestQueue;
	}

//	public ImageLoader getImageLoader() {
//		getRequestQueue();
//		if (mImageLoader == null) {
//			mImageLoader = new ImageLoader(this.mRequestQueue, new _LruBitmapCache());
//		}
//		return this.mImageLoader;
//	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

	public static void initImageLoader(Context context) {
		// Get list item height
		TypedArray arrayHeight = context.getTheme().obtainStyledAttributes(new int[] { android.R.attr.listPreferredItemHeightSmall });
		int height = Math.round(arrayHeight.getDimension(0, 32) * context.getResources().getDisplayMetrics().density + 0.5f);
		arrayHeight.recycle();

		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context)
			.threadPriority(Thread.NORM_PRIORITY - 2)
			.denyCacheImageMultipleSizesInMemory()
			.memoryCacheExtraOptions(height, height)
			.diskCacheFileNameGenerator(new Md5FileNameGenerator())
			.diskCacheExtraOptions(height, height, null)
			.diskCacheSize(50 * 1024 * 1024) // 50 MiB
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}

}
