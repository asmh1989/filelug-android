package com.filelug.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.filelug.android.R;

public class NetworkUtils {

	public static boolean isNetworkAvailable(Context context, String warningMessage) {
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		boolean isAvailable = false;
		if(networkInfo != null && networkInfo.isConnected()) {
			isAvailable = true;
		} else if ( warningMessage != null ) {
			MsgUtils.showToast(context, warningMessage);
		}
		return isAvailable;
	}

	public static boolean isNetworkAvailable(Context context) {
		return isNetworkAvailable(context, context.getString(R.string.message_network_error));
	}

}
