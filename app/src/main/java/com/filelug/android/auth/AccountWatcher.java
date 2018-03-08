package com.filelug.android.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.DocumentsContract;

import com.filelug.android.docsprovider.RemoteFilesProvider;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.FileCache;
import com.filelug.android.util.PrefUtils;

public class AccountWatcher extends BroadcastReceiver {

	private static final String TAG = AccountWatcher.class.getSimpleName();

	@Override
	public void onReceive(final Context context, Intent intent) {
//		if ( Constants.DEBUG ) Log.d(TAG, "onReceive()");
		if ( intent.getAction().equals(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION) ) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					resetPrefAccounts(context);
				}
			}).start();
		}
	}

	private void resetPrefAccounts(Context context) {
//		if ( Constants.DEBUG ) Log.d(TAG, "resetPrefAccounts()");
		if ( !PrefUtils.isAccountListGenerated() ) {
			PrefUtils.generateAccounts();
		}

		String[] oldAccounts = PrefUtils.getAccounts();
		Account[] newAccounts = AccountUtils.getFilelugAccounts();

//		if ( Constants.DEBUG ) Log.d(TAG, "resetPrefAccounts(): oldAccounts=" + (oldAccounts == null ? "" : TextUtils.join(";", oldAccounts)) + ", newAccounts.length=" + newAccounts.length);

		// Check account removed
		if ( oldAccounts != null && oldAccounts.length > 0 ) {
			for ( String oldAccount : oldAccounts ) {
//				if ( Constants.DEBUG ) Log.d(TAG, "resetPrefAccounts(), oldAccount=" + oldAccount);
				boolean isRemoved = true;
				if ( newAccounts != null && newAccounts.length > 0 ) {
					for ( Account newAccount : newAccounts ) {
//                        if ( Constants.DEBUG ) Log.d(TAG, "resetPrefAccounts(), newAccount=" + newAccount.name);
						if ( oldAccount.equals(newAccount.name) ) {
							isRemoved = false;
							break;
						}
					}
				}
				if ( isRemoved ) {
//					if ( Constants.DEBUG ) Log.d(TAG, "resetPrefAccounts(), isRemoved=" + isRemoved);
					// Clean cache files & remove SQLite data
					FileCache.cleanActiveAccountCache(oldAccount);
					// Remove preference data
					PrefUtils.cleanActiveInfo(oldAccount);
				}
			}
		}

		// Set accounts
		String accounts = AccountUtils.getAccountsString();
		PrefUtils.setAccounts(accounts);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			context.getContentResolver().notifyChange(DocumentsContract.buildRootsUri(RemoteFilesProvider.AUTHORITY), null);
		}
	}

}
