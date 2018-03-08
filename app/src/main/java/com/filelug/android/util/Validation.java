package com.filelug.android.util;

import android.content.Context;
import android.widget.EditText;

import com.filelug.android.MainApplication;
import com.filelug.android.R;
import com.marvinlabs.widget.floatinglabel.itempicker.FloatingLabelItemPicker;

import java.util.regex.Pattern;

public class Validation {

	// Regular Expression
	// you can change the expression based on your need
	private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
//	private static final String PHONE_REGEX = "\\d{4}-\\d{3}-\\d{3}";
	private static final String PHONE_REGEX = "\\d{10}";

	// Error Messages
/*
	private static final String REQUIRED_MSG = "required";
	private static final String EMAIL_MSG = "invalid email";
	private static final String PHONE_MSG = "####-###-###";
*/
	private static final String PHONE_MSG = "##########";

	// call this method when you need to check email validation
	public static boolean isEmailAddress(EditText editText, boolean required) {
		Context ctx = MainApplication.getInstance().getBaseContext();
		String invalidEmailMsg = ctx.getResources().getString(R.string.message_invalid_email);
		return isValid(editText, EMAIL_REGEX, invalidEmailMsg, required);
	}

	// call this method when you need to check phone number validation
	public static boolean isPhoneNumber(EditText editText, boolean required) {
		return isValid(editText, PHONE_REGEX, PHONE_MSG, required);
	}

	// return true if the input field is valid, based on the parameter passed
	public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

		String text = editText.getText().toString().trim();
		// clearing the error, if it was previously set by some other values
		editText.setError(null);

		// text required and editText is blank, so return false
		if ( required && !hasText(editText) ) return false;

		// pattern doesn't match so returning false
		if (text.length() > 0 && !Pattern.matches(regex, text)) {
			editText.setError(errMsg);
			return false;
		};

		return true;
	}

	// check the input field has any text or not
	// return true if it contains text otherwise false
	public static boolean hasText(EditText editText) {

		String text = editText.getText().toString().trim();
		editText.setError(null);

		// length 0 means there is no text
		if (text.length() == 0) {
			Context ctx = MainApplication.getInstance().getBaseContext();
			String requiredMsg = ctx.getResources().getString(R.string.message_field_can_not_be_empty);
			editText.setError(requiredMsg);
			return false;
		}

		return true;
	}

	public static boolean hasSelect(FloatingLabelItemPicker itemPicker) {
		int[] selectedIndices = itemPicker.getSelectedIndices();
		if ( selectedIndices != null && selectedIndices.length > 0 ) {
			return true;
		}
		return false;
	}

}
