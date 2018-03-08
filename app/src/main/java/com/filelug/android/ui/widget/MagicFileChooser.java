package com.filelug.android.ui.widget;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import com.filelug.android.service.ContentType;
import com.filelug.android.util.LocalFileUtils;

import java.io.File;
import java.util.List;

public class MagicFileChooser {

	public static final int ACTIVITY_FILE_CHOOSER = 9973;
	private final Activity activity;
	private boolean choosing = false;
	private boolean mustCanRead;
	private File[] chosenFiles;

	public MagicFileChooser(final Activity activity) {
		this.activity = activity;
	}

	public boolean showFileChooser() {
		return showFileChooser(ContentType.ALL);
	}

	public boolean showFileChooser(final String mimeType) {
		return showFileChooser(mimeType, null);
	}

	public boolean showFileChooser(final String mimeType, final String chooserTitle) {
		return showFileChooser(mimeType, chooserTitle, false);
	}

	public boolean showFileChooser(final String mimeType, final String chooserTitle, final boolean allowMultiple) {
		return showFileChooser(mimeType, chooserTitle, allowMultiple, false);
	}

	public boolean showFileChooser(final String mimeType, final String chooserTitle, final boolean allowMultiple, final boolean mustCanRead) {
		if (mimeType == null || choosing) {
			return false;
		}
		choosing = true;
		// 檢查是否有可用的Activity
		final PackageManager packageManager = activity.getPackageManager();
		final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType(mimeType);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		if (list.size() > 0) {
			this.mustCanRead = mustCanRead;
			// 如果有可用的Activity
			Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
			picker.setType(mimeType);
			picker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple);
			picker.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
			// 使用Intent Chooser
			Intent destIntent = Intent.createChooser(picker, chooserTitle);
			activity.startActivityForResult(destIntent, ACTIVITY_FILE_CHOOSER);
			return true;
		} else {
			return false;
		}
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTIVITY_FILE_CHOOSER) {
			choosing = false;
			if (resultCode == Activity.RESULT_OK) {
				Uri uri = data.getData();
				if (uri != null) {
					// 單選
					chosenFiles = LocalFileUtils.getFilesFromUris(activity, new Uri[]{uri}, mustCanRead);
					return true;
				} else if (Build.VERSION.SDK_INT >= 16) {
					// 複選
					ClipData clipData = data.getClipData();
					if (clipData != null) {
						int count = clipData.getItemCount();
						if (count > 0) {
							Uri[] uris = new Uri[count];
							for (int i = 0; i < count; i++) {
								uris[i] = clipData.getItemAt(i).getUri();
							}
							chosenFiles = LocalFileUtils.getFilesFromUris(activity, uris, mustCanRead);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public File[] getChosenFiles() {
		return chosenFiles;
	}

/*
	public static void createCachedFile(Context context, String userId, int computerId, String fileName, String content) throws IOException {
		File cacheFolder = createCachedDir(context, userId, computerId);
		File cacheFile = new File(cacheFolder, fileName);
		if ( !cacheFile.exists() ) {
			cacheFile.createNewFile();

			FileOutputStream fos = new FileOutputStream(cacheFile);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
			PrintWriter pw = new PrintWriter(osw);
			pw.println(content);
			pw.flush();
			pw.close();
		}
	}

	private static File createCachedDir(Context context, String userId, int computerId) throws IOException {
		File cacheDir = null;
		boolean success = false;

		File openFromCacheFolder = new File(context.getExternalCacheDir(), "open-from");
		if ( !openFromCacheFolder.exists() ) {
			success = openFromCacheFolder.mkdir();
		}

		File userCacheFolder = new File(openFromCacheFolder, userId);
		if ( !userCacheFolder.exists() ) {
			success = userCacheFolder.mkdir();
		}

		cacheDir = new File(userCacheFolder, String.valueOf(computerId));
		if ( !cacheDir.exists() ) {
			success = cacheDir.mkdir();
		}

		return cacheDir;
	}

	private void onFinished(Uri... uris) {
		Log.d(TAG, "onFinished() " + Arrays.toString(uris));

		final Intent intent = new Intent();
		if (uris.length == 1) {
			intent.setData(uris[0]);
		} else if (uris.length > 1) {
			final ClipData clipData = new ClipData(
					null, mState.acceptMimes, new ClipData.Item(uris[0]));
			for (int i = 1; i < uris.length; i++) {
				clipData.addItem(new ClipData.Item(uris[i]));
			}
			intent.setClipData(clipData);
		}

		if (mState.action == ACTION_GET_CONTENT) {
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		} else {
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
					| Intent.FLAG_GRANT_WRITE_URI_PERMISSION
					| Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
		}

		setResult(Activity.RESULT_OK, intent);
		finish();
	}
*/

}
