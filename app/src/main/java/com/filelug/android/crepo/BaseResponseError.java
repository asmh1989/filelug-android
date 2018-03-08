package com.filelug.android.crepo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.filelug.android.Constants;
import com.filelug.android.util.FilelugUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.MsgUtils;

public class BaseResponseError implements Response.ErrorListener {

	private static final String TAG = BaseResponseError.class.getSimpleName();

	public static final int MESSAGE_TYPE_TOAST = 1;
	public static final int MESSAGE_TYPE_INFO_MESSAGE = 2;
	public static final int MESSAGE_TYPE_WARNING_MESSAGE = 3;
	public static final int MESSAGE_TYPE_ERROR_MESSAGE = 4;

	protected boolean showMessage = false;
	protected Context context = null;
	private int messageType = MESSAGE_TYPE_TOAST;

//	public BaseResponseError(boolean showMessage) {
//		this(showMessage, MainApplication.getInstance().getBaseContext());
//	}

	public BaseResponseError(boolean showMessage, Context context) {
		this.showMessage = showMessage;
		this.context = context;
	}

	public BaseResponseError(boolean showMessage, Context context, int messageType) {
		this.showMessage = showMessage;
		this.context = context;
		this.messageType = messageType;
	}

	@Override
	public void onErrorResponse(VolleyError volleyError) {
		int statusCode = MiscUtils.getStatusCode(volleyError);
//		if ( Constants.DEBUG ) Log.d(TAG, "onErrorResponse(): statusCode=" + statusCode);
		if ( statusCode == Constants.HTTP_STATUS_CODE_FORBIDDEN ) {
			FilelugUtils.actionWhen403();
		} else if ( statusCode == Constants.HTTP_STATUS_CODE_NOT_IMPLEMENTED ) {
			FilelugUtils.actionWhen501();
		} else if ( statusCode == Constants.HTTP_STATUS_CODE_SERVICE_UNAVAILABLE ) {
			FilelugUtils.actionWhen503(context);
		}
		beforeShowErrorMessage(volleyError);
		showErrorMessage(volleyError);
	}

	protected void beforeShowErrorMessage(VolleyError volleyError) {
	}

	protected void showErrorMessage(VolleyError volleyError) {
		if ( messageType == MESSAGE_TYPE_TOAST ) {
			doShowToast(volleyError);
			afterShowErrorMessage(volleyError);
		} else {
			doShowMessage(volleyError);
		}
	}

	protected void afterShowErrorMessage(VolleyError volleyError) {
	}

	private void doShowToast(VolleyError volleyError) {
		String message = getMessage(volleyError);
		if ( this.showMessage && message != null && message.trim().length() > 0 ) {
			MsgUtils.showToast(context, message);
		}
	}

	private void doShowMessage(final VolleyError volleyError) {
		String message = getMessage(volleyError);
		if ( this.showMessage && message != null && message.trim().length() > 0 ) {
			MaterialDialog.SingleButtonCallback singleButtonCallback = new MaterialDialog.SingleButtonCallback() {
				@Override
				public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
					afterShowErrorMessage(volleyError);
				}
			};
			if ( messageType == MESSAGE_TYPE_INFO_MESSAGE ) {
				MsgUtils.showErrorMessage(context, message, singleButtonCallback);
			} if ( messageType == MESSAGE_TYPE_WARNING_MESSAGE ) {
				MsgUtils.showWarningMessage(context, message, singleButtonCallback);
			} if ( messageType == MESSAGE_TYPE_ERROR_MESSAGE ) {
				MsgUtils.showErrorMessage(context, message, singleButtonCallback);
			}
		} else {
			afterShowErrorMessage(volleyError);
		}
	}

	protected String getMessage(VolleyError volleyError) {
		return MiscUtils.getVolleyErrorMessage(volleyError);
	}

}
