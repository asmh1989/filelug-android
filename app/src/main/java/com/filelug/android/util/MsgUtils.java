package com.filelug.android.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

public class MsgUtils {

	private static final String TAG = MsgUtils.class.getSimpleName();

	public static void showInfoMessage(Context context, String message) {
		DialogUtils.createInfoDialog(context, message).show();
	}

	public static void showInfoMessage(Context context, String message, MaterialDialog.SingleButtonCallback singleButtonCallback) {
		DialogUtils.createInfoDialog(context, message, singleButtonCallback).show();
	}

	public static void showInfoMessage(Context context, int messageRes) {
		showInfoMessage(context, context.getResources().getString(messageRes));
	}

	public static void showInfoMessage(Context context, int messageRes, MaterialDialog.SingleButtonCallback singleButtonCallback) {
		showInfoMessage(context, context.getResources().getString(messageRes), singleButtonCallback);
	}

	public static void showWarningMessage(Context context, String message) {
		DialogUtils.createWarningDialog(context, message).show();
	}

	public static void showWarningMessage(Context context, String message, MaterialDialog.SingleButtonCallback singleButtonCallback) {
		DialogUtils.createWarningDialog(context, message, singleButtonCallback).show();
	}

	public static void showWarningMessage(Context context, int messageRes) {
		showWarningMessage(context, context.getResources().getString(messageRes));
	}

	public static void showWarningMessage(Context context, int messageRes, MaterialDialog.SingleButtonCallback singleButtonCallback) {
		showWarningMessage(context, context.getResources().getString(messageRes), singleButtonCallback);
	}

	public static void showErrorMessage(Context context, String message) {
		DialogUtils.createErrorDialog(context, message).show();
	}

	public static void showErrorMessage(Context context, String message, MaterialDialog.SingleButtonCallback singleButtonCallback) {
		DialogUtils.createErrorDialog(context, message, singleButtonCallback).show();
	}

	public static void showErrorMessage(Context context, int messageRes) {
		showErrorMessage(context, context.getResources().getString(messageRes));
	}

	public static void showErrorMessage(Context context, int messageRes, MaterialDialog.SingleButtonCallback singleButtonCallback) {
		showErrorMessage(context, context.getResources().getString(messageRes), singleButtonCallback);
	}

	public static void showToast(Context context, String message) {
		showToast(context, message, Toast.LENGTH_LONG);
	}

	public static void showToast(Context context, int messageRes) {
		showToast(context, context.getResources().getString(messageRes), Toast.LENGTH_LONG);
	}

	public static void showToast(final Context context, final String message, final int duration) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(context, message, duration);
				toast.setGravity(Gravity.BOTTOM, 0, 20);
				toast.show();
			}
		});
	}

	public static void showToast(Context context, int messageRes, int duration) {
		showToast(context, context.getResources().getString(messageRes), duration);
	}

}
