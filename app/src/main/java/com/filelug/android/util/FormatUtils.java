package com.filelug.android.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import com.filelug.android.MainApplication;
import com.filelug.android.R;
import com.filelug.android.ui.model.LocalFile;
import com.filelug.android.ui.model.RemoteFile;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {

	private static final String TAG = FormatUtils.class.getSimpleName();

	private static final int SECONDS_PER_MINUTE = 60;
	private static final int SECONDS_PER_HOUR = 60 * 60;
	private static final int SECONDS_PER_DAY = 24 * 60 * 60;
	private static final int MILLIS_PER_MINUTE = 1000 * 60;

	public static String formatLocalFileObjectType(Context context, LocalFile.FileType type) {
		int resId = -1;
		switch (type) {
			case LOCAL_DIR:
				resId = R.string.fileType_local_dir;
				break;
			case LOCAL_SYMBOLIC_LINK_DIR:
				resId = R.string.fileType_local_symbolic_link_dir;
				break;
			case LOCAL_FILE:
				resId = R.string.fileType_local_file;
				break;
			case LOCAL_SYMBOLIC_LINK_FILE:
				resId = R.string.fileType_local_symbolic_link_file;
				break;
//			case LocalFile.FILE_TYPE_REMOTE_DIR:
//				resId = R.string.fileType_remote_dir;
//				break;
//			case LocalFile.FILE_TYPE_REMOTE_FILE:
//				resId = R.string.fileType_remote_file;
//				break;
			case UNKNOWN:
				resId = R.string.fileType_unknown;
				break;
			default:
				break;
		}
		String typeStr = "";
		if ( resId > 0 ) {
			Resources resources = context.getResources();
			typeStr = resources.getString(resId);
		}
		return typeStr;
	}

	public static String formatRemoteFileObjectType(Context context, RemoteFile.FileType type) {
		int resId = -1;
		switch (type) {
			case REMOTE_DIR:
				resId = R.string.fileType_remote_dir;
				break;
			case REMOTE_FILE:
				resId = R.string.fileType_remote_file;
				break;
			case REMOTE_WINDOWS_SHORTCUT_DIR:
				resId = R.string.fileType_remote_windows_shortcut_dir;
				break;
			case REMOTE_WINDOWS_SHORTCUT_FILE:
				resId = R.string.fileType_remote_windows_shortcut_file;
				break;
			case REMOTE_UNIX_SYMBOLIC_LINK_DIR:
				resId = R.string.fileType_remote_unix_symbolic_link_dir;
				break;
			case REMOTE_UNIX_SYMBOLIC_LINK_FILE:
				resId = R.string.fileType_remote_unix_symbolic_link_file;
				break;
			case REMOTE_MAC_ALIAS_DIR:
				resId = R.string.fileType_remote_mac_alias_dir;
				break;
			case REMOTE_MAC_ALIAS_FILE:
				resId = R.string.fileType_remote_mac_alias_file;
				break;
			case UNKNOWN:
				resId = R.string.fileType_unknown;
				break;
			default:
				break;
		}
		String typeStr = "";
		if ( resId > 0 ) {
			Resources resources = context.getResources();
			typeStr = resources.getString(resId);
		}
		return typeStr;
	}

	public static String formatDate1(Context context, Date lastModifiedDate) {
		Resources resources = context.getResources();
		SimpleDateFormat formatter = new SimpleDateFormat(resources.getString(R.string.const_format_dateTime_YMDHM));
		String dateStr = formatter.format(lastModifiedDate);
		return dateStr;
	}

	public static String formatDate2(Context context, Date lastModifiedDate) {
		Resources resources = context.getResources();
		SimpleDateFormat formatter = new SimpleDateFormat();
		String dateStr = formatter.format(lastModifiedDate);
		return dateStr;
	}

	public static String formatFileCount(Context context, int fileCount) {
		Resources resources = context.getResources();
		String format = fileCount <= 1 ? resources.getString(R.string.format_file_count_item) : resources.getString(R.string.format_file_count_items);
		String fileCountStr = String.format(format, fileCount);
		return fileCountStr;
	}

	public static String formatFileSize(Context context, long fileSize) {
		String fileSizeStr = Formatter.formatFileSize(context, fileSize);
		return fileSizeStr;
	}

	public static String formatBooleanToYN(Context context, boolean value) {
		int resId = -1;
		if ( value ) {
			resId = R.string.yn_yes;
		} else {
			resId = R.string.yn_no;
		}
		String str = "";
		if ( resId > 0 ) {
			Resources resources = context.getResources();
			str = resources.getString(resId);
		}
		return str;
	}

	// Copy from android.text.format.Formatter.formatShortElapsedTime()
	public static String formatShortElapsedTime(Context context, long millis) {
		long secondsLong = millis / 1000;

		int days = 0, hours = 0, minutes = 0;
		if (secondsLong >= SECONDS_PER_DAY) {
			days = (int)(secondsLong / SECONDS_PER_DAY);
			secondsLong -= days * SECONDS_PER_DAY;
		}
		if (secondsLong >= SECONDS_PER_HOUR) {
			hours = (int)(secondsLong / SECONDS_PER_HOUR);
			secondsLong -= hours * SECONDS_PER_HOUR;
		}
		if (secondsLong >= SECONDS_PER_MINUTE) {
			minutes = (int)(secondsLong / SECONDS_PER_MINUTE);
			secondsLong -= minutes * SECONDS_PER_MINUTE;
		}
		int seconds = (int)secondsLong;

		if (days >= 2) {
			days += (hours+12)/24;
			return context.getString(R.string.durationDays, days);
		} else if (days > 0) {
			if (hours == 1) {
				return context.getString(R.string.durationDayHour, days, hours);
			}
			return context.getString(R.string.durationDayHours, days, hours);
		} else if (hours >= 2) {
			hours += (minutes+30)/60;
			return context.getString(R.string.durationHours, hours);
		} else if (hours > 0) {
			if (minutes == 1) {
				return context.getString(R.string.durationHourMinute, hours,
						minutes);
			}
			return context.getString(R.string.durationHourMinutes, hours,
					minutes);
		} else if (minutes >= 2) {
			minutes += (seconds+30)/60;
			return context.getString(R.string.durationMinutes, minutes);
		} else if (minutes > 0) {
			if (seconds == 1) {
				return context.getString(R.string.durationMinuteSecond, minutes,
						seconds);
			}
			return context.getString(R.string.durationMinuteSeconds, minutes,
					seconds);
		} else if (seconds == 1) {
			return context.getString(R.string.durationSecond, seconds);
		} else {
			return context.getString(R.string.durationSeconds, seconds);
		}
	}

	// Copy from android.text.format.Formatter.formatShortElapsedTimeRoundingUpToMinutes()
	public static String formatShortElapsedTimeRoundingUpToMinutes(Context context, long millis) {
		long minutesRoundedUp = (millis + MILLIS_PER_MINUTE - 1) / MILLIS_PER_MINUTE;

		if (minutesRoundedUp == 0) {
			return context.getString(R.string.durationMinutes, 0);
		} else if (minutesRoundedUp == 1) {
			return context.getString(R.string.durationMinute, 1);
		}

		return formatShortElapsedTime(context, minutesRoundedUp * MILLIS_PER_MINUTE);
	}

	public static long convertRemoteFileLastModifiedToTimestamp(Context context, String timeStr) {
		if ( context == null ) {
			context = MainApplication.getInstance().getBaseContext();
		}
		SimpleDateFormat formatter = new SimpleDateFormat(context.getResources().getString(R.string.const_format_dateTime_remote_file));
		Date dateTime = null;
		try {
			dateTime = formatter.parse(timeStr);
		} catch (ParseException e) {
			Log.e(TAG, "convertRemoteFileLastModifiedToTimestamp(), [" + timeStr + "] Date string parsing error!");
		}
		return dateTime != null ? dateTime.getTime() : -1L;
	}

	public static long convertDownloadResponseLastModifiedToTimestamp(Context context, String timeStr) {
		SimpleDateFormat formatter = new SimpleDateFormat(context.getResources().getString(R.string.const_format_dateTime_download_response));
		Date dateTime = null;
		try {
			dateTime = formatter.parse(timeStr);
		} catch (ParseException e) {
			Log.e(TAG, "convertDownloadResponseLastModifiedToTimestamp(), [" + timeStr + "] Date string parsing error!");
		}
		return dateTime != null ? dateTime.getTime() : -1L;
	}

	public static String convertTimestampToDownloadRequestIfRange(Context context, long time) {
		SimpleDateFormat formatter = new SimpleDateFormat(context.getResources().getString(R.string.const_format_dateTime_download_response), Locale.US);
		String dateTimeStr = formatter.format(new Date(time));
		return dateTimeStr;
	}

}
