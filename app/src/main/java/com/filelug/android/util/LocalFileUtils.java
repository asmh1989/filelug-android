package com.filelug.android.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.mtp.MtpConstants;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Files.FileColumns;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.crepo.RepositoryUtility;
import com.filelug.android.provider.assetfile.AssetFileColumns;
import com.filelug.android.provider.assetfile.AssetFileContentValues;
import com.filelug.android.provider.assetfile.AssetFileCursor;
import com.filelug.android.provider.assetfile.AssetFileSelection;
import com.filelug.android.provider.assetfile.UploadStatusType;
import com.filelug.android.provider.uploadgroup.UploadGroupCursor;
import com.filelug.android.provider.uploadgroup.UploadGroupSelection;
import com.filelug.android.service.ContentType;
import com.filelug.android.service.UploadRequest;
import com.filelug.android.service.UploadService;
import com.filelug.android.ui.model.LocalFile;
import com.filelug.android.ui.model.LocalFileObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class LocalFileUtils {

	private static final String TAG = LocalFileUtils.class.getSimpleName();

	public interface FileArrayCallback {
		public void loaded(boolean success, File[] files);
	}
	public interface FileObjectListCallback {
		public void loaded(boolean success, List<LocalFile> localFileList);
	}

	private static String externalStorage;
	private static List<String> sdcardStorageList;
	private static List<String> usbStorageList;

	static {
		scanExternalStorageMap();
	}

	private static void scanExternalStorageMap() {
//		if ( Constants.DEBUG ) Log.d(TAG, "scanExternalStorageMap()");

		externalStorage = null;
		sdcardStorageList = new ArrayList<String>();
		usbStorageList = new ArrayList<String>();

		ArrayList<String> storages = new ArrayList<String>();
		Map<String, String> systemEnv = System.getenv();
		ArrayList<String> keyList = new ArrayList<String>();
		keyList.addAll(systemEnv.keySet());

		for ( String envKey : systemEnv.keySet() ) {
			String envValue = systemEnv.get(envKey);
			keyList.add(envKey);
//			if ( Constants.DEBUG ) Log.d(TAG, "scanExternalStorageMap(), env: " + envKey + "=" + envValue);
		}

		// External Storage
		if ( keyList.contains(Constants.ENV_PARAM_EXTERNAL_STORAGE) ) {
			externalStorage = systemEnv.get(Constants.ENV_PARAM_EXTERNAL_STORAGE);
//			if ( Constants.DEBUG ) Log.d(TAG, "scanExternalStorageMap(), Add external storage: " + externalStorage);
			storages.add(externalStorage);
			keyList.remove(Constants.ENV_PARAM_EXTERNAL_STORAGE);
		}
		// Secondary Storage
		if ( keyList.contains(Constants.ENV_PARAM_SECONDARY_STORAGE) ) {
			String secondaryStorage = systemEnv.get(Constants.ENV_PARAM_SECONDARY_STORAGE);
//			if ( Constants.DEBUG ) Log.d(TAG, "scanExternalStorageMap(), Add secondary storages: " + secondaryStorage);
			addToStorageList(storages, secondaryStorage);
			keyList.remove(Constants.ENV_PARAM_SECONDARY_STORAGE);
		}
		// Other sdcard and usb Storage
		for ( String key : keyList ) {
			if ( key.startsWith(Constants.ENV_PARAM_EXTERNAL_STORAGE) || key.startsWith(Constants.ENV_PARAM_EXTERNAL_ADD_STORAGE) ||
					key.startsWith(Constants.ENV_PARAM_EXTERNAL_ADD_USB_STORAGE) || key.startsWith(Constants.ENV_PARAM_SECOND_VOLUME_STORAGE) ||
					key.startsWith(Constants.ENV_PARAM_SECOND_VOLUME_STORAGE) || key.startsWith(Constants.ENV_PARAM_USB_OTG_STORAGE) ) {
				String value = systemEnv.get(key);
//				if ( Constants.DEBUG ) Log.d(TAG, "scanExternalStorageMap(), Add sdcard and usb storages: " + value);
				addToStorageList(storages, value);
			}
		}
	}

	private static void addToStorageList(ArrayList<String> storages, String value) {
		if ( value == null || value.trim().length() <= 0 ) {
			return;
		}
		for ( String path : value.split(":") ) {
			if ( storages.contains(path) ) {
				continue;
			}
			String lpath = path.toLowerCase();
			if ( lpath.contains("usb") || lpath.contains("udisk") ) {
//				if ( Constants.DEBUG ) Log.d(TAG, "addToStorageList(), Add usb storage: " + path);
				usbStorageList.add(path);
			} else {
//				if ( Constants.DEBUG ) Log.d(TAG, "addToStorageList(), Add sdcard storage: " + path);
				sdcardStorageList.add(path);
			}
			storages.add(path);
		}
	}

	public static LocalFileObject getDeviceRoot(Context context) {
		return new LocalFileObject(LocalFile.FileType.ROOT, context.getResources().getString(R.string.fileType_device_root));
	}

	public static void findLocalFileObjectList(final Context context, LocalFile folder, final LocalFileUtils.FileObjectListCallback callback) {
		AsyncTask<LocalFile, Void, List<LocalFile>> task = new AsyncTask<LocalFile, Void, List<LocalFile>>() {
			protected List<LocalFile> doInBackground(LocalFile... parents) {
				LocalFile parentFolder = parents[0];
				List<LocalFile> files = null;
				LocalFile.FileType type = parentFolder.getType();
				if ( type == LocalFile.FileType.ROOT) {
					files = getLocalRoot(context);
				} else if ( type == LocalFile.FileType.SYS_DIR_PICTURES) {
					files = getLocalPicturesRoot(context);
				} else if ( type == LocalFile.FileType.SYS_DIR_MUSIC) {
					files = getLocalMusicRoot(context);
				} else if ( type == LocalFile.FileType.SYS_DIR_MOVIES) {
					files = getLocalMoviesRoot(context);
				} else if ( type == LocalFile.FileType.SYS_DIR_DOCUMENTS) {
					files = getLocalDocumentsRoot(context);
				} else if ( type == LocalFile.FileType.SYS_DIR_DOWNLOADS) {
					files = getLocalDownloadsRoot(context);
				} else if ( type.isDirectory() ) {
					LocalFile.MediaType mediaType = parentFolder.getMediaType();
					if ( mediaType == LocalFile.MediaType.IMAGE ) {
						files = getLocalPicturesById(context, parentFolder.getMediaId());
					} else if ( mediaType == LocalFile.MediaType.AUDIO ) {
						files = getLocalMusicById(context, parentFolder.getMediaId());
					} else if ( mediaType == LocalFile.MediaType.VIDEO ) {
						files = getLocalMoviesById(context, parentFolder.getMediaId());
					} else if ( mediaType == LocalFile.MediaType.DOCUMENT ) {
						files = getLocalDocumentsById(context, parentFolder.getMediaId());
					} else {
						String fullName = parentFolder.getFullName();
						File file = new File(fullName);
						files = convertFileObjects(listLocalFiles(file, false));
					}
				}
				return files;
			}
			protected void onPostExecute(List<LocalFile> result) {
				if ( callback != null ) {
					callback.loaded(true, result);
				}
			}
		};
		task.execute(folder);
	}

	public static List<LocalFile> getLocalRoot(Context context) {
		scanExternalStorageMap();

		ArrayList<LocalFile> localRootDirectories = new ArrayList<LocalFile>();

		LocalFileObject picturesRootDirectory = new LocalFileObject(LocalFile.FileType.SYS_DIR_PICTURES, context.getResources().getString(R.string.fileType_local_images));
		localRootDirectories.add(picturesRootDirectory);

		LocalFileObject musicRootDirectory = new LocalFileObject(LocalFile.FileType.SYS_DIR_MUSIC, context.getResources().getString(R.string.fileType_local_music));
		localRootDirectories.add(musicRootDirectory);

		LocalFileObject moviesRootDirectory = new LocalFileObject(LocalFile.FileType.SYS_DIR_MOVIES, context.getResources().getString(R.string.fileType_local_movies));
		localRootDirectories.add(moviesRootDirectory);

		LocalFileObject documentsRootDirectory = new LocalFileObject(LocalFile.FileType.SYS_DIR_DOCUMENTS, context.getResources().getString(R.string.fileType_local_documents));
		localRootDirectories.add(documentsRootDirectory);

		LocalFileObject downloadsRootDirectory = new LocalFileObject(LocalFile.FileType.SYS_DIR_DOWNLOADS, context.getResources().getString(R.string.fileType_local_downloads));
		localRootDirectories.add(downloadsRootDirectory);

		LocalFileObject internalStorageDirectory = getLocalInternalStorageDirectory(context);
		if ( internalStorageDirectory != null ) localRootDirectories.add(internalStorageDirectory);

		LocalFileObject[] sdcardStorageDirectories = getLocalSDCardStorageDirectory(context);
		if ( sdcardStorageDirectories != null && sdcardStorageDirectories.length > 0 ) {
			for ( LocalFileObject sdcardStorageDirectory : sdcardStorageDirectories ) {
				localRootDirectories.add(sdcardStorageDirectory);
			}
		}

		LocalFileObject[] usbStorageDirectories = getLocalUSBStorageDirectory(context);
		if ( usbStorageDirectories != null && usbStorageDirectories.length > 0 ) {
			for ( LocalFileObject usbStorageDirectory : usbStorageDirectories ) {
				localRootDirectories.add(usbStorageDirectory);
			}
		}

        boolean showLocalSystemFolder = PrefUtils.isShowLocalSystemFolder();
        if ( showLocalSystemFolder ) {
            LocalFileObject localRootDirectory = getLocalRootDirectory(context);
            if ( localRootDirectory != null ) localRootDirectories.add(localRootDirectory);
        }

		return localRootDirectories;
	}

	public static List<LocalFile> getLocalPicturesRoot(Context context) {
//		if ( Constants.DEBUG ) Log.d(TAG, "getLocalPicturesRoot");

		ArrayList<LocalFile> directoryList = new ArrayList<LocalFile>();

		ContentResolver cr = context.getContentResolver();
		Uri uri = MediaStore.Files.getContentUri("external");

		// image folders
		String[] projection = {
				BaseColumns._ID,
				MediaColumns.DATA,
				FileColumns.PARENT
		};
		String selection = BaseColumns._ID + " IN ( SELECT DISTINCT " + FileColumns.PARENT + " FROM files WHERE " +
				"( " + FileColumns.MEDIA_TYPE + " = " + FileColumns.MEDIA_TYPE_IMAGE +
				" OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ? ) AND ( " +
				MediaColumns.WIDTH + " > 0 AND " +
				MediaColumns.HEIGHT + " > 0 )" +
				" )";
		String[] selectionArgs = new String[]{ "image%" };
		String sortOrder = null;

//		if ( Constants.DEBUG ) {
//			Log.d(TAG, "selection="+selection);
//			Log.d(TAG, "selectionArgs="+selectionArgs[0]);
//		}

		Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder);

		while (c.moveToNext()) {
			String fileName = c.getString(c.getColumnIndex(FileColumns.DATA));
			File file = new File(fileName);
			long mediaId = c.getLong(c.getColumnIndex(FileColumns._ID));
			long mediaParentId = c.getInt(c.getColumnIndex(FileColumns.PARENT));
			directoryList.add(new LocalFileObject(LocalFile.MediaType.IMAGE, mediaId, mediaParentId, LocalFile.FileType.MEDIA_DIR, file.getName(), file));
		}
		c.close();

		return directoryList;
	}

	public static int getLocalPictureCountById(Context context, long parentId) {
//		if ( Constants.DEBUG ) Log.d(TAG, "getLocalPictureCountById");

		ContentResolver cr = context.getContentResolver();
		Uri uri = MediaStore.Files.getContentUri("external");

		// images count
		String[] projection = { "count(*) as cnt" };
		String selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE +
				" OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ? ) AND ( " +
				FileColumns.PARENT + " = " + String.valueOf(parentId) + " ) AND ( " +
				MediaStore.Files.FileColumns.WIDTH + " > 0 AND " +
				MediaStore.Files.FileColumns.HEIGHT + " > 0 )";
		String[] selectionArgs = new String[] { "image%" };
		String sortOrder = null;

//		if ( Constants.DEBUG ) {
//			Log.d(TAG, "selection="+selection);
//			Log.d(TAG, "selectionArgs=" + selectionArgs[0].toString());
//		}

		int cnt = 0;
		Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		if (c.moveToFirst()) {
			cnt = c.getInt(c.getColumnIndex("cnt"));
		}
		c.close();

		return cnt;
	}

	public static List<LocalFile> getLocalPicturesById(Context context, long parentId) {
//		if ( Constants.DEBUG ) Log.d(TAG, "getLocalPicturesById");

		ArrayList<LocalFile> directoryList = new ArrayList<LocalFile>();

		ContentResolver cr = context.getContentResolver();
		Uri uri = MediaStore.Files.getContentUri("external");

		// images
		String[] projection = {
				BaseColumns._ID,
				MediaColumns.DATA,
				FileColumns.PARENT
		};
		String selection = "( " + FileColumns.MEDIA_TYPE + " = " + FileColumns.MEDIA_TYPE_IMAGE +
				" OR " + FileColumns.MIME_TYPE + " LIKE ? ) AND ( " +
				FileColumns.PARENT + " = " + String.valueOf(parentId) + " ) AND ( " +
				FileColumns.WIDTH + " > 0 AND " +
				FileColumns.HEIGHT + " > 0 )";
		String[] selectionArgs = new String[] { "image%" };
		String sortOrder = null;

//		if ( Constants.DEBUG ) {
//			Log.d(TAG, "selection="+selection);
//			Log.d(TAG, "selectionArgs=" + selectionArgs[0].toString());
//		}

		Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder);

		while (c.moveToNext()) {
			String fileName = c.getString(c.getColumnIndex(FileColumns.DATA));
			File file = new File(fileName);
			long mediaId = c.getLong(c.getColumnIndex(FileColumns._ID));
			long mediaParentId = c.getInt(c.getColumnIndex(FileColumns.PARENT));
			directoryList.add(new LocalFileObject(LocalFile.MediaType.IMAGE, mediaId, mediaParentId, LocalFile.FileType.MEDIA_FILE, file.getName(), file));
		}
		c.close();

		return directoryList;
	}

	public static List<LocalFile> getLocalMusicRoot(Context context) {
//		if ( Constants.DEBUG ) Log.d(TAG, "getLocalMusicRoot");

		ArrayList<LocalFile> directoryList = new ArrayList<LocalFile>();

		ContentResolver cr = context.getContentResolver();
		Uri uri = MediaStore.Files.getContentUri("external");

		// audio folders
		String[] projection = {
				BaseColumns._ID,
				MediaColumns.DATA,
				FileColumns.PARENT
		};
		String selection =  BaseColumns._ID + " IN ( SELECT DISTINCT " + FileColumns.PARENT + " FROM files WHERE " +
				FileColumns.MEDIA_TYPE + " = " + FileColumns.MEDIA_TYPE_AUDIO +
				" OR " + MediaColumns.MIME_TYPE + " LIKE ? " +
				" )";
		String[] selectionArgs = new String[]{ "audio%" };
		String sortOrder = null;

//		if ( Constants.DEBUG ) {
//			Log.d(TAG, "selection="+selection);
//			Log.d(TAG, "selectionArgs="+selectionArgs.toString());
//		}

		Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder);

		while (c.moveToNext()) {
			String fileName = c.getString(c.getColumnIndex(FileColumns.DATA));
			File file = new File(fileName);
			long mediaId = c.getLong(c.getColumnIndex(FileColumns._ID));
			long mediaParentId = c.getInt(c.getColumnIndex(FileColumns.PARENT));
			directoryList.add(new LocalFileObject(LocalFile.MediaType.AUDIO, mediaId, mediaParentId, LocalFileObject.FileType.MEDIA_DIR, file.getName(), file));
		}
		c.close();

		return directoryList;
	}

	public static int getLocalMusicCountById(Context context, long parentId) {
//		if ( Constants.DEBUG ) Log.d(TAG, "getLocalMusicCountById");

		ContentResolver cr = context.getContentResolver();
		Uri uri = MediaStore.Files.getContentUri("external");

		// audio count
		String[] projection = { "count(*) as cnt" };
		String selection = "( " + FileColumns.MEDIA_TYPE + " = " + FileColumns.MEDIA_TYPE_AUDIO +
				" OR " + MediaColumns.MIME_TYPE + " LIKE ? ) AND ( " +
				FileColumns.PARENT + " = " + String.valueOf(parentId) + " )";
		String[] selectionArgs = new String[]{ "audio%" };
		String sortOrder = null;

//		if ( Constants.DEBUG ) {
//			Log.d(TAG, "selection="+selection);
//			Log.d(TAG, "selectionArgs="+selectionArgs.toString());
//		}

		int cnt = 0;
		Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		if (c.moveToFirst()) {
			cnt = c.getInt(c.getColumnIndex("cnt"));
		}
		c.close();

		return cnt;
	}

	public static List<LocalFile> getLocalMusicById(Context context, long parentId) {
//		if ( Constants.DEBUG ) Log.d(TAG, "getLocalMusicById");

		ArrayList<LocalFile> directoryList = new ArrayList<LocalFile>();

		ContentResolver cr = context.getContentResolver();
		Uri uri = MediaStore.Files.getContentUri("external");

		// audio
		String[] projection = {
				BaseColumns._ID,
				MediaColumns.DATA,
				FileColumns.PARENT
		};
		String selection = "( " + FileColumns.MEDIA_TYPE + " = " + FileColumns.MEDIA_TYPE_AUDIO +
				" OR " + MediaColumns.MIME_TYPE + " LIKE ? ) AND ( " +
				FileColumns.PARENT + " = " + String.valueOf(parentId) + " )";
		String[] selectionArgs = new String[]{ "audio%" };
		String sortOrder = null;

//		if ( Constants.DEBUG ) {
//			Log.d(TAG, "selection="+selection);
//			Log.d(TAG, "selectionArgs="+selectionArgs.toString());
//		}

		Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder);

		while (c.moveToNext()) {
			String fileName = c.getString(c.getColumnIndex(FileColumns.DATA));
			File file = new File(fileName);
			long mediaId = c.getLong(c.getColumnIndex(FileColumns._ID));
			long mediaParentId = c.getInt(c.getColumnIndex(FileColumns.PARENT));
			directoryList.add(new LocalFileObject(LocalFile.MediaType.AUDIO, mediaId, mediaParentId, LocalFileObject.FileType.MEDIA_FILE, file.getName(), file));
		}
		c.close();

		return directoryList;
	}

	public static List<LocalFile> getLocalMoviesRoot(Context context) {
//		if ( Constants.DEBUG ) Log.d(TAG, "getLocalMoviesRoot");

		ArrayList<LocalFile> directoryList = new ArrayList<LocalFile>();

		ContentResolver cr = context.getContentResolver();
		Uri uri = MediaStore.Files.getContentUri("external");

		// video folders
		String[] projection = {
				BaseColumns._ID,
				MediaColumns.DATA,
				FileColumns.PARENT
		};
		String selection = BaseColumns._ID + " IN ( SELECT DISTINCT " + FileColumns.PARENT + " FROM files WHERE " +
				FileColumns.MEDIA_TYPE + " = " + FileColumns.MEDIA_TYPE_VIDEO +
				" OR " + FileColumns.MIME_TYPE + " LIKE ? " +
				" )";
		String[] selectionArgs = new String[]{ "video%" };
		String sortOrder = null;

//		if ( Constants.DEBUG ) {
//			Log.d(TAG, "selection="+selection);
//			Log.d(TAG, "selectionArgs="+selectionArgs.toString());
//		}

		Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder);

		while (c.moveToNext()) {
			String fileName = c.getString(c.getColumnIndex(FileColumns.DATA));
			File file = new File(fileName);
			long mediaId = c.getLong(c.getColumnIndex(FileColumns._ID));
			long mediaParentId = c.getInt(c.getColumnIndex(FileColumns.PARENT));
			directoryList.add(new LocalFileObject(LocalFile.MediaType.VIDEO, mediaId, mediaParentId, LocalFileObject.FileType.MEDIA_DIR, file.getName(), file));
		}
		c.close();

		return directoryList;
	}

	public static int getLocalMovieCountById(Context context, long parentId) {
//		if ( Constants.DEBUG ) Log.d(TAG, "getLocalMovieCountById");

		ContentResolver cr = context.getContentResolver();
		Uri uri = MediaStore.Files.getContentUri("external");

		// video count
		String[] projection = { "count(*) as cnt" };
		String selection = "( " + FileColumns.MEDIA_TYPE + " = " + FileColumns.MEDIA_TYPE_VIDEO +
				" OR " + FileColumns.MIME_TYPE + " LIKE ? ) AND ( " +
				FileColumns.PARENT + " = " + String.valueOf(parentId) + " )";
		String[] selectionArgs = new String[]{ "video%" };
		String sortOrder = null;

//		if ( Constants.DEBUG ) {
//			Log.d(TAG, "selection="+selection);
//			Log.d(TAG, "selectionArgs="+selectionArgs.toString());
//		}

		int cnt = 0;
		Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		if (c.moveToFirst()) {
			cnt = c.getInt(c.getColumnIndex("cnt"));
		}
		c.close();

		return cnt;
	}

	public static List<LocalFile> getLocalMoviesById(Context context, long parentId) {
//		if ( Constants.DEBUG ) Log.d(TAG, "getLocalMoviesById");

		ArrayList<LocalFile> directoryList = new ArrayList<LocalFile>();

		ContentResolver cr = context.getContentResolver();
		Uri uri = MediaStore.Files.getContentUri("external");

		// video
		String[] projection = {
				BaseColumns._ID,
				MediaColumns.DATA,
				FileColumns.PARENT
		};
		String selection = "( " + FileColumns.MEDIA_TYPE + " = " + FileColumns.MEDIA_TYPE_VIDEO +
				" OR " + FileColumns.MIME_TYPE + " LIKE ? ) AND ( " +
				FileColumns.PARENT + " = " + String.valueOf(parentId) + " )";
		String[] selectionArgs = new String[]{ "video%" };
		String sortOrder = null;

//		if ( Constants.DEBUG ) {
//			Log.d(TAG, "selection="+selection);
//			Log.d(TAG, "selectionArgs="+selectionArgs.toString());
//		}

		Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder);

		while (c.moveToNext()) {
			String fileName = c.getString(c.getColumnIndex(FileColumns.DATA));
			File file = new File(fileName);
			long mediaId = c.getLong(c.getColumnIndex(FileColumns._ID));
			long mediaParentId = c.getInt(c.getColumnIndex(FileColumns.PARENT));
			directoryList.add(new LocalFileObject(LocalFile.MediaType.VIDEO, mediaId, mediaParentId, LocalFileObject.FileType.MEDIA_FILE, file.getName(), file));
		}
		c.close();

		return directoryList;
	}

	public static List<LocalFile> getLocalDocumentsRoot(Context context) {
//		if ( Constants.DEBUG ) Log.d(TAG, "getLocalDocumentsRoot");

		ArrayList<LocalFile> directoryList = new ArrayList<LocalFile>();

		ContentResolver cr = context.getContentResolver();
		Uri uri = MediaStore.Files.getContentUri("external");

		// documents folders
		String[] projection = {
				BaseColumns._ID,
				MediaColumns.DATA,
				FileColumns.PARENT
		};
		String selection = BaseColumns._ID + " IN ( SELECT DISTINCT " + FileColumns.PARENT + " FROM files WHERE " +
				FileColumns.MEDIA_TYPE + " = " + FileColumns.MEDIA_TYPE_NONE +
				" AND ( " + FileColumns.MIME_TYPE + " LIKE ? OR " +
				FileColumns.MIME_TYPE + " LIKE ? OR " +
				FileColumns.MIME_TYPE + " LIKE ? )" +
				" )";
		String[] selectionArgs = new String[]{ "text%", "application%", "xml%" };
		String sortOrder = null;

//		if ( Constants.DEBUG ) {
//			Log.d(TAG, "selection="+selection);
//			Log.d(TAG, "selectionArgs="+selectionArgs.toString());
//		}

		Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder);

		while (c.moveToNext()) {
			String fileName = c.getString(c.getColumnIndex(FileColumns.DATA));
			File file = new File(fileName);
			long mediaId = c.getLong(c.getColumnIndex(FileColumns._ID));
			long mediaParentId = c.getInt(c.getColumnIndex(FileColumns.PARENT));
			directoryList.add(new LocalFileObject(LocalFile.MediaType.DOCUMENT, mediaId, mediaParentId, LocalFileObject.FileType.MEDIA_DIR, file.getName(), file));
		}
		c.close();

		return directoryList;
	}

	public static int getLocalDocumentCountById(Context context, long parentId) {
//		if ( Constants.DEBUG ) Log.d(TAG, "getLocalDocumentCountById");

		ContentResolver cr = context.getContentResolver();
		Uri uri = MediaStore.Files.getContentUri("external");

		// documents count
		String[] projection = { "count(*) as cnt" };
		String selection = "( " + FileColumns.MEDIA_TYPE + " = " + FileColumns.MEDIA_TYPE_NONE +
				" AND ( " + FileColumns.MIME_TYPE + " LIKE ? OR " +
				FileColumns.MIME_TYPE + " LIKE ? ) ) AND ( " +
				FileColumns.PARENT + " = " + String.valueOf(parentId) + " )";
		String[] selectionArgs = new String[]{ "text%", "application%" };
		String sortOrder = null;

//		if ( Constants.DEBUG ) {
//			Log.d(TAG, "selection="+selection);
//			Log.d(TAG, "selectionArgs="+selectionArgs.toString());
//		}

		int cnt = 0;
		Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		if (c.moveToFirst()) {
			cnt = c.getInt(c.getColumnIndex("cnt"));
		}
		c.close();

		return cnt;
	}

	public static List<LocalFile> getLocalDocumentsById(Context context, long parentId) {
//		if ( Constants.DEBUG ) Log.d(TAG, "getLocalDocumentCountById");

		ArrayList<LocalFile> directoryList = new ArrayList<LocalFile>();

		ContentResolver cr = context.getContentResolver();
		Uri uri = MediaStore.Files.getContentUri("external");

		// documents
		String[] projection = {
				BaseColumns._ID,
				MediaColumns.DATA,
				FileColumns.PARENT
		};
		String selection = "( " + FileColumns.MEDIA_TYPE + " = " + FileColumns.MEDIA_TYPE_NONE +
				" AND ( " + FileColumns.MIME_TYPE + " LIKE ? OR " +
				FileColumns.MIME_TYPE + " LIKE ? ) ) AND ( " +
				FileColumns.PARENT + " = " + String.valueOf(parentId) + " )";
		String[] selectionArgs = new String[]{ "text%", "application%" };
		String sortOrder = null;

//		if ( Constants.DEBUG ) {
//			Log.d(TAG, "selection="+selection);
//			Log.d(TAG, "selectionArgs="+selectionArgs.toString());
//		}

		Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder);

		while (c.moveToNext()) {
			String fileName = c.getString(c.getColumnIndex(FileColumns.DATA));
			File file = new File(fileName);
			long mediaId = c.getLong(c.getColumnIndex(FileColumns._ID));
			long mediaParentId = c.getInt(c.getColumnIndex(FileColumns.PARENT));
			directoryList.add(new LocalFileObject(LocalFile.MediaType.DOCUMENT, mediaId, mediaParentId, LocalFileObject.FileType.MEDIA_FILE, file.getName(), file));
		}
		c.close();

		return directoryList;
	}

	public static List<LocalFile> getLocalDownloadsRoot(Context context) {
//		if ( Constants.DEBUG ) Log.d(TAG, "getLocalDownloadsRoot");

		ArrayList<LocalFile> directoryList = new ArrayList<LocalFile>();

		ContentResolver cr = context.getContentResolver();
		Uri uri = MediaStore.Files.getContentUri("external");

		String[] projection = {
				BaseColumns._ID,
				MediaColumns.DATA,
				FileColumns.PARENT
		};
		// downloads
		String selection = FileColumns.PARENT + " IN ( SELECT " + BaseColumns._ID +
				" FROM files WHERE " + FileColumns.TITLE + " = '" + Environment.DIRECTORY_DOWNLOADS +
				"' AND format = " + MtpConstants.FORMAT_ASSOCIATION + " ) ";
		String[] selectionArgs = null; // there is no ? in selection so null here
		String sortOrder = null;

//		if ( Constants.DEBUG ) Log.d(TAG, "selection="+selection);

		Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder);

		while (c.moveToNext()) {
			String fileName = c.getString(c.getColumnIndex(FileColumns.DATA));
			File file = new File(fileName);
			long mediaId = c.getLong(c.getColumnIndex(FileColumns._ID));
			long mediaParentId = c.getInt(c.getColumnIndex(FileColumns.PARENT));
			LocalFile.FileType type = file.isDirectory() ? LocalFileObject.FileType.MEDIA_DIR : LocalFileObject.FileType.MEDIA_FILE;
			directoryList.add(new LocalFileObject(LocalFile.MediaType.DOWNLOAD, mediaId, mediaParentId, type, file.getName(), file));
		}
		c.close();

		return directoryList;
	}

	private static List<LocalFile> convertFileObjects(List<File> fileList) {
		if ( fileList == null ) return null;
		List<LocalFile> localFileList = new ArrayList<LocalFile>();
		for ( File file : fileList ) {
			localFileList.add(new LocalFileObject(file));
		}
		return localFileList;
	}

	private static List<File> listLocalFiles(File parentFolder, boolean showFoldersOnly) {
		if ( parentFolder == null || !parentFolder.isDirectory() || !parentFolder.canRead() )
			return null;
		List<File> results = new ArrayList<>();
		File[] contents = parentFolder.listFiles();
		if ( contents != null ) {
			boolean showHidden = PrefUtils.isShowHiddenFiles();
			for (File fi : contents) {
//				if ( fi.canRead() ) {
				if ( !showFoldersOnly || ( showFoldersOnly && fi.isDirectory() ) ) {
					boolean isHiddenFile = fi.isHidden() || fi.getName().startsWith(".");
					if ( showHidden || ( !showHidden && !isHiddenFile ) )
						results.add(fi);
				}
//				}
			}
		}
		return results;
	}

	public static LocalFileObject getLocalInternalStorageDirectory(Context context) {
		File directory = null;
		if ( externalStorage != null ) {
			directory = new File(externalStorage);
		}
		return isAvailableDir(directory) ? new LocalFileObject(LocalFile.FileType.SYS_DIR_INTERNAL_STORAGE, context.getResources().getString(R.string.fileType_local_internal_storage), directory) : null;
	}

	public static LocalFileObject[] getLocalSDCardStorageDirectory(Context context) {
		if ( sdcardStorageList == null || sdcardStorageList.size() == 0 ) {
			return null;
		}
		int idx = 0;
		ArrayList<LocalFileObject> fileList = new ArrayList<LocalFileObject>();
		for ( String sdcardStorage : sdcardStorageList ) {
			File directory = new File(sdcardStorage);
			if ( isAvailableDir(directory) ) {
				String displayName = context.getResources().getString(R.string.fileType_local_sdcard_storage) + ( idx == 0 ? "" : (" " + idx) );
				fileList.add( new LocalFileObject(LocalFile.FileType.SYS_DIR_SD_CARD_STORAGE, displayName, directory) );
			}
		}
		return fileList.toArray(new LocalFileObject[0]);
	}

	public static LocalFileObject[] getLocalUSBStorageDirectory(Context context) {
		if ( usbStorageList == null || usbStorageList.size() == 0 ) {
			return null;
		}
		int idx = 0;
		ArrayList<LocalFileObject> fileList = new ArrayList<LocalFileObject>();
		for ( String usbStorage : usbStorageList ) {
			File directory = new File(usbStorage);
			if ( isAvailableDir(directory) ) {
				String displayName = context.getResources().getString(R.string.fileType_local_usb_storage) + ( idx == 0 ? "" : (" " + idx) );
				fileList.add( new LocalFileObject(LocalFile.FileType.SYS_DIR_USB_STORAGE, displayName, directory) );
			}
		}
		return fileList.toArray(new LocalFileObject[0]);
	}

	public static LocalFileObject getLocalRootDirectory(Context context) {
		File localRootDirectory = null;
		File parentDirectory = null;
		File systemRootDirectory = Environment.getRootDirectory();
		if ( isAvailableDir(systemRootDirectory) ) {
			parentDirectory = systemRootDirectory.getParentFile();
			if ( isAvailableDir(parentDirectory) ) {
				localRootDirectory = parentDirectory;
			} else {
				localRootDirectory = systemRootDirectory;
			}
		}
		return localRootDirectory == null ? null : new LocalFileObject(LocalFile.FileType.SYS_DIR_DEVICE_ROOT, context.getResources().getString(R.string.fileType_local_root), localRootDirectory);
	}

	public static boolean isAvailableDir(File directory) {
		boolean ret = false;
		if ( directory == null ) {
			return ret;
		}
		if ( directory.isDirectory() && directory.canRead() ) {
			ret = true;
		}
		return ret;
	}

	public static String getImageThumbnail(Context context, int id) {
		Uri uri = Images.Thumbnails.EXTERNAL_CONTENT_URI;
		String[] projection = { Images.Thumbnails.DATA };
		String selection = Images.Thumbnails.IMAGE_ID + " = " + id + " AND " + Images.Thumbnails.KIND + " = " + Images.Thumbnails.MICRO_KIND;
		Cursor thumbCursor = context.getContentResolver().query(uri, projection, selection, null, null);

		String thumbPath = null;
		if (thumbCursor != null && thumbCursor.getCount() > 0) {
			thumbCursor.moveToFirst();
			thumbPath = thumbCursor.getString(thumbCursor.getColumnIndex(Images.Thumbnails.DATA));
		}
		thumbCursor.close();

//		if ( Constants.DEBUG ) Log.d(TAG, "getImageThumbnail(), id=" + id + ", thumbPath=" + thumbPath);

		return thumbPath;
	}

	public static String getVideoThumbnail(Context context, int id) {
		Uri uri = Video.Thumbnails.EXTERNAL_CONTENT_URI;
		String[] projection = { Video.Thumbnails.DATA };
		String selection = Video.Thumbnails.VIDEO_ID + " = " + id + " AND " + Video.Thumbnails.KIND + " = " + Video.Thumbnails.MICRO_KIND;
		Cursor thumbCursor = context.getContentResolver().query(uri, projection, selection, null, null);

		String thumbPath = null;
		if (thumbCursor != null && thumbCursor.getCount() > 0) {
			thumbCursor.moveToFirst();
			thumbPath = thumbCursor.getString(thumbCursor.getColumnIndex(Video.Thumbnails.DATA));
		}
		thumbCursor.close();

//		if ( Constants.DEBUG ) Log.d(TAG, "getVideoThumbnail(), id=" + id + ", thumbPath=" + thumbPath);

		return thumbPath;
	}

	public static void findLocalFolderList(File parentFolder, final FileArrayCallback callback) {
		AsyncTask<File, Void, File[]> task = new AsyncTask<File, Void, File[]>() {
			protected File[] doInBackground(File... parents) {
				if ( parents == null || parents.length <= 0 ) {
					return null;
				}
				File parent = parents[0];
				List<File> results = listLocalFiles(parent, true);
				if ( results != null ) {
					Collections.sort(results, new SortUtils.FolderComparator());
					return results.toArray(new File[0]);
				} else {
					return null;
				}
			}
			protected void onPostExecute(File[] result) {
				if ( callback != null ) {
					callback.loaded(true, result);
				}
			}
		};
		task.execute(parentFolder);
	}

	public static void _queryMediaTypes(Context context) {
		String sortOrder1 = MediaStore.Files.FileColumns.MIME_TYPE + " ASC, " +
				MediaStore.Files.FileColumns.PARENT + " ASC, " +
				MediaStore.Files.FileColumns.TITLE + " ASC";

		String sortOrder2 = MediaStore.Files.FileColumns.PARENT + " ASC, " +
				MediaStore.Files.FileColumns.TITLE + " ASC";

		String sortOrder3 = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC, " +
				MediaStore.Files.FileColumns.TITLE + " ASC";

		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String fileName = null;

		Uri uri = MediaStore.Files.getContentUri("external");

		projection = new String[] {

			MediaStore.Files.FileColumns._ID,
			/*MediaStore.Files.FileColumns._COUNT,*/

			MediaStore.Files.FileColumns.DATA,
			MediaStore.Files.FileColumns.SIZE,
			MediaStore.Files.FileColumns.DISPLAY_NAME,
			MediaStore.Files.FileColumns.DATE_ADDED,
			MediaStore.Files.FileColumns.DATE_MODIFIED,
			/*"media_scanner_new_object_id",*/
			"is_drm",
			MediaStore.Files.FileColumns.WIDTH,
			MediaStore.Files.FileColumns.HEIGHT,

			"storage_id",
			"format",
			MediaStore.Files.FileColumns.PARENT,
			MediaStore.Files.FileColumns.MIME_TYPE,
			MediaStore.Files.FileColumns.TITLE,
			MediaStore.Files.FileColumns.MEDIA_TYPE

		};

//		// all
//		selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " != " + MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST;
//		selectionArgs = null; // there is no ? in selection so null here
//		fileName = "all_";
//		_queryMediaTypeFiles(context, selection, selectionArgs, fileName, sortOrder1);
//
//		// mimeType - none
//		selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;
//		selectionArgs = null; // there is no ? in selection so null here
//		fileName = "mediaType_none_";
//		_queryMediaTypeFiles(context, selection, selectionArgs, fileName, sortOrder1);
//
//		// playlist
//		selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST;
//		selectionArgs = null;
//		fileName = "mediaType_playlist_";
//		_queryMediaTypeFiles(context, selection, selectionArgs, fileName, sortOrder2);

		// only pdf
//		selection = MediaStore.Files.FileColumns.MIME_TYPE + " = ?";
//		selectionArgs = new String[]{ MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf") };
//		fileName = "mimeType_pdf_";
//		_queryMediaTypeFiles(context, selection, selectionArgs, fileName, sortOrder2);

		// mimetype not null
//		selection = MediaStore.Files.FileColumns.MIME_TYPE + " IS NOT NULL";
//		selectionArgs = null;
//		fileName = "mimeType_isNotNull_";
//		_queryMediaTypeFiles(context, selection, selectionArgs, fileName, sortOrder1);

//		// image folders
//		projection = new String[] {
//			"*"
//		};
//		selection = MediaStore.Files.FileColumns._ID + " IN ( SELECT DISTINCT " + MediaStore.Files.FileColumns.PARENT + " FROM files WHERE " +
//			"( " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE +
//			" OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ? ) AND ( " +
//			MediaStore.Files.FileColumns.WIDTH + " > 0 AND " +
//			MediaStore.Files.FileColumns.HEIGHT + " > 0 )" +
//			" )";
//		selectionArgs = new String[]{ "image%" };
//		fileName = "images_folders_";
//		_queryMediaTypeFiles(context, uri, projection, selection, selectionArgs, fileName, sortOrder3);
//
//		// image 1
//		projection = new String[] {
//			"*"
//		};
//		selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE +
//			" OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ? ) AND ( " +
//			MediaStore.Files.FileColumns.WIDTH + " > 0 AND " +
//			MediaStore.Files.FileColumns.HEIGHT + " > 0 )";
//		selectionArgs = new String[]{ "image%" };
//		fileName = "images_1_";
//		_queryMediaTypeFiles(context, uri, projection, selection, selectionArgs, fileName, sortOrder2);
//
//		// image 2
//		uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//		projection = new String[] {
//			"*"
//		};
//		selection = null;
//		selectionArgs = null;
//		fileName = "images_2_";
//		_queryMediaTypeFiles(context, uri, projection, selection, selectionArgs, fileName, null);
//
//		// image thumbnails
//		uri = Images.Thumbnails.EXTERNAL_CONTENT_URI;
//		projection = new String[] {
//			"*"
//		};
//		selection = null;
//		selectionArgs = null;
//		fileName = "images_thumbnails_";
//		_queryMediaTypeFiles(context, uri, projection, selection, selectionArgs, fileName, null);
//
//		// audio folders
//		selection =  MediaStore.Files.FileColumns._ID + " IN ( SELECT DISTINCT " + MediaStore.Files.FileColumns.PARENT + " FROM files WHERE " +
//				MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO +
//				" OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ? " +
//				" )";
//		selectionArgs = new String[]{ "audio%" };
//		fileName = "audio_folders_";
//		_queryMediaTypeFiles(context, selection, selectionArgs, fileName, sortOrder2);
//

/*
		projection = new String[] {

				MediaStore.Files.FileColumns._ID,
			*/
/*MediaStore.Files.FileColumns._COUNT,*//*


				MediaStore.Files.FileColumns.DATA,
				MediaStore.Files.FileColumns.SIZE,
				MediaStore.Files.FileColumns.DISPLAY_NAME,
				MediaStore.Files.FileColumns.DATE_ADDED,
				MediaStore.Files.FileColumns.DATE_MODIFIED,
			*/
/*"media_scanner_new_object_id",*//*

				"is_drm",
				MediaStore.Files.FileColumns.WIDTH,
				MediaStore.Files.FileColumns.HEIGHT,

				"storage_id",
				"format",
				MediaStore.Files.FileColumns.PARENT,
				MediaStore.Files.FileColumns.MIME_TYPE,
				MediaStore.Files.FileColumns.TITLE,
				MediaStore.Files.FileColumns.MEDIA_TYPE,
				AudioColumns.IS_ALARM,
				AudioColumns.IS_MUSIC,
				AudioColumns.IS_NOTIFICATION,
				AudioColumns.IS_PODCAST,
				AudioColumns.IS_RINGTONE

		};
		// audio
		selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO +
				" OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ?";
		selectionArgs = new String[]{ "audio%" };
		fileName = "audio_";
		_queryMediaTypeFiles(context, projection, selection, selectionArgs, fileName, sortOrder2);
*/


//		// video folders
//		projection = new String[] {
//				"*"
//		};
//		selection = MediaStore.Files.FileColumns._ID + " IN ( SELECT DISTINCT " + MediaStore.Files.FileColumns.PARENT + " FROM files WHERE " +
//				MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO +
//				" OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ? " +
//				" )";
//		selectionArgs = new String[]{ "video%" };
//		fileName = "video_folders_";
//		_queryMediaTypeFiles(context, uri, projection, selection, selectionArgs, fileName, sortOrder2);
//
//		// video 1
//		projection = new String[] {
//			"*"
//		};
//		selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO +
//				" OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ?";
//		selectionArgs = new String[]{ "video%" };
//		fileName = "video_1_";
//		_queryMediaTypeFiles(context, uri, projection, selection, selectionArgs, fileName, sortOrder2);

		// image 2
		uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		projection = new String[] {
				"*"
		};
		selection = null;
		selectionArgs = null;
		fileName = "video_2_";
		_queryMediaTypeFiles(context, uri, projection, selection, selectionArgs, fileName, null);

		// image thumbnails
		uri = Video.Thumbnails.EXTERNAL_CONTENT_URI;
		projection = new String[] {
				"*"
		};
		selection = null;
		selectionArgs = null;
		fileName = "video_thumbnails_";
		_queryMediaTypeFiles(context, uri, projection, selection, selectionArgs, fileName, null);
//
//		// all text & application folders
//		selection = MediaStore.Files.FileColumns._ID + " IN ( SELECT DISTINCT " + MediaStore.Files.FileColumns.PARENT + " FROM files WHERE " +
//				MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE +
//				" AND ( " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ? OR " +
//				MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ? )" +
//				" )";
//		selectionArgs = new String[]{ "text%", "application%" };
//		fileName = "documents_folders_";
//		_queryMediaTypeFiles(context, selection, selectionArgs, fileName, sortOrder1);
//
//		// all text & application
//		selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE +
//				" AND ( " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ? OR " +
//				MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ? )";
//		selectionArgs = new String[]{ "text%", "application%" };
//		fileName = "documents_";
//		_queryMediaTypeFiles(context, selection, selectionArgs, fileName, sortOrder1);
//
//		// downloads
//		selection = MediaStore.Files.FileColumns.PARENT + " IN ( SELECT " + MediaStore.Files.FileColumns._ID +
//				" FROM files WHERE " + MediaStore.Files.FileColumns.TITLE + " = '" + Environment.DIRECTORY_DOWNLOADS +
//				"' AND format = " + MtpConstants.FORMAT_ASSOCIATION + " ) ";
//		selectionArgs = null; // there is no ? in selection so null here
//		fileName = "downloads_";
//		_queryMediaTypeFiles(context, selection, selectionArgs, fileName, sortOrder3);

	}

	private static void _queryMediaTypeFiles(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String fileName, String sortOrder) {
		StringBuffer sb = new StringBuffer();

		ContentResolver cr = context.getContentResolver();

		Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder);

		if ( Constants.DEBUG ) {
			String colNames[] = c.getColumnNames();
			for ( int i=0; i<colNames.length; i++ ) {
				sb.append(colNames[i]);
				if ( i == colNames.length-1 ) {
					sb.append('\n');
				} else {
					sb.append('\t');
				}
			}
			while (c.moveToNext()) {
				int colCount = c.getColumnCount();
				for ( int i=0; i<colCount; i++ ) {
					sb.append(c.getString(i));
					if ( i == colCount-1 ) {
						sb.append('\n');
					} else {
						sb.append('\t');
					}
				}
			}
			_writeLog(context, sb, fileName);
		}

		c.close();
	}

	private static void _writeLog(Context context, StringBuffer sb, String fileName) {
		try {
			File myFile = new File("/storage/emulated/0/Download/" + fileName + new Date().getTime() + ".txt");
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(sb.toString());
			myOutWriter.close();
			fOut.close();
			Toast.makeText(context, "Done writing SD '" + myFile.getPath() + "'", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	public static void _analysisDeviceFS(Context context) {
		StringBuffer sb = new StringBuffer();

		try {

			sb.append("\nSystem.getenv()");
			sb.append("\n===============================================================");
			Map<String, String> envs = System.getenv();
			for ( String key : envs.keySet() ) {
				String value = envs.get(key);
				sb.append("\n" + key + "=" + value);
			}
			sb.append("\n===============================================================");

			sb.append("\nSystem.getProperties()");
			sb.append("\n===============================================================");
			Properties properties = System.getProperties();
			for ( String property : properties.stringPropertyNames() ) {
				String value = properties.getProperty(property);
				sb.append("\n" + property + "=" + value);
			}
			sb.append("\n===============================================================");

			File externalStorageDir = Environment.getExternalStorageDirectory();

			sb.append("\nExternalStorageState: "+externalStorageDir.getPath());
			sb.append("\n===============================================================");
			String externalStorageState = Environment.getExternalStorageState();
			boolean isExternalStorageEmulated = Environment.isExternalStorageEmulated();
			boolean isExternalStorageRemovable = Environment.isExternalStorageRemovable();
			sb.append("\nEnvironment.getExternalStorageState(): " + externalStorageState);
			sb.append("\nEnvironment.isExternalStorageEmulated(): " + isExternalStorageEmulated);
			sb.append("\nEnvironment.isExternalStorageRemovable(): " + isExternalStorageRemovable);
			sb.append("\n===============================================================");

			sb.append("\nEnvironment.getExternalStorageDirectory()");
			sb.append(_showFileAttributes(externalStorageDir));

			File parent = externalStorageDir.getParentFile().getParentFile();
			File[] files = parent.listFiles();
			for ( File storageObj : files ) {
/*
				sb.append("\nExternalStorageState: "+storageObj.getPath());
				sb.append("\n===============================================================");
				String storageState = Environment.getExternalStorageState(storageObj);
				boolean isStorageEmulated = Environment.isExternalStorageEmulated(storageObj);
				boolean isStorageRemovable = Environment.isExternalStorageRemovable(storageObj);
				sb.append("\nEnvironment.getExternalStorageState(): " + storageState);
				sb.append("\nEnvironment.isExternalStorageEmulated(): " + isStorageEmulated);
				sb.append("\nEnvironment.isExternalStorageRemovable(): " + isStorageRemovable);
				sb.append("\n===============================================================");
*/
				sb.append(_showFileAttributes(storageObj));
			}

			sb.append("\nEnvironment.getRootDirectory()");
			File rootDir = Environment.getRootDirectory();
			sb.append(_showFileAttributes(rootDir));

			sb.append("\nEnvironment.getRootDirectory().getParentFile()");
			File realRootDir = rootDir.getParentFile();
			sb.append(_showFileAttributes(realRootDir));

			sb.append("\nEnvironment.DIRECTORY_ALARMS");
			File alarmDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
			sb.append(_showFileAttributes(alarmDir));

			sb.append("\nEnvironment.DIRECTORY_DCIM");
			File dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			sb.append(_showFileAttributes(dcimDir));

			sb.append("\nEnvironment.DIRECTORY_DOCUMENTS");
			if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
				File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
				sb.append(_showFileAttributes(documentsDir));
			}

			sb.append("\nEnvironment.DIRECTORY_DOWNLOADS");
			File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			sb.append(_showFileAttributes(downloadsDir));

			sb.append("\nEnvironment.DIRECTORY_MOVIES");
			File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
			sb.append(_showFileAttributes(moviesDir));

			sb.append("\nEnvironment.DIRECTORY_MUSIC");
			File musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
			sb.append(_showFileAttributes(musicDir));

			sb.append("\nEnvironment.DIRECTORY_NOTIFICATIONS");
			File notificationsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS);
			sb.append(_showFileAttributes(notificationsDir));

			sb.append("\nEnvironment.DIRECTORY_PICTURES");
			File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			sb.append(_showFileAttributes(picturesDir));

			sb.append("\nEnvironment.DIRECTORY_PODCASTS");
			File podcastsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS);
			sb.append(_showFileAttributes(podcastsDir));

			sb.append("\nEnvironment.DIRECTORY_RINGTONES");
			File ringtonsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES);
			sb.append(_showFileAttributes(ringtonsDir));

		} catch (IOException e) {
			e.printStackTrace();
		}

		_writeLog(context, sb, "log_filesystem_");
	}

	private static String _showFileAttributes(File file) throws IOException {
		String ret = "";
		if ( file != null ) {
			ret += "\n===============================================================";
			ret += "\npath\twrite\tread\thidden\tparent\tname\tasolutePath\tcanonicalPath\tabsolute\tdirectory\tfile\texecute\tlastModified\tlength\tfreeSpace\ttotalSpace\tusableSpace";
			ret += "\n"+file.getPath()+"\t"+file.canWrite()+"\t"+file.canRead()+"\t"+file.isHidden()+"\t"+file.getParent()+"\t"+file.getName()+"\t"+file.getAbsolutePath()+"\t"+file.getCanonicalPath()+"\t"+file.isAbsolute()+"\t"+file.isDirectory()+"\t"+file.isFile()+"\t"+file.canExecute()+"\t"+file.lastModified()+"\t"+file.length()+"\t"+file.getFreeSpace()+"\t"+file.getTotalSpace()+"\t"+file.getUsableSpace();
			ret += "\n===============================================================";
		}
		return ret;
	}

	public static void uploadFiles(Context context, String userId, String computerIdStr, String lugServerId, String authToken, String groupId, boolean fromAnotherApp, String updir, Map<String, LocalFile> localFileMap) {
		int computerId = Integer.valueOf(computerIdStr);
		for ( String transferKey : localFileMap.keySet() ) {
			LocalFile localFile = localFileMap.get(transferKey);
			String fileName = localFile.getName();
			String fullName = localFile.getFullName();
			String cacheFileName = localFile.getCacheFileName();
			long fileSize = localFile.getSize();
			String contentType = localFile.getContentType() != null ? localFile.getContentType() : ContentType.APPLICATION_OCTET_STREAM;
			Date lastModifiedDate = localFile.getLastModifiedDate();
			// Files from other app has no lastModifiedDate
			long lastModified = lastModifiedDate != null ? localFile.getLastModifiedDate().getTime() : new Date().getTime();

			AssetFileContentValues values = new AssetFileContentValues()
				.putUserId(userId)
				.putComputerId(computerId)
				.putGroupId(groupId)
				.putTransferKey(transferKey)
				.putAssetUrl(fullName)
				.putServerFileName(fileName)
				.putCacheFileName(cacheFileName)
				.putTotalSize(fileSize)
				.putTransferredSize(0l)
				.putContentType(contentType)
				.putLastModifiedTimestamp(lastModified)
				.putStatus(UploadStatusType.wait)
				.putWaitToConfirm(false);

			values.insert(context.getContentResolver());

			UploadRequest request = new UploadRequest(context, userId, computerId, groupId, transferKey, lugServerId, authToken, fromAnotherApp);
			request.addFileToUpload(fullName, fileName, cacheFileName, contentType, fileSize, false, 0l);
			request.setNotificationConfig(fileName);
			//You can add your own custom headers
			request.addHeader(Constants.HTTP_HEADER_AUTHORIZATION, authToken);
			request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
			request.addHeader(Constants.PARAM_UP_KEY, transferKey);
			request.addHeader(Constants.PARAM_UP_DIR, RepositoryUtility.encrypt2Base64(updir));
			request.addHeader(Constants.PARAM_UP_NAME, RepositoryUtility.encrypt2Base64(fileName));
			request.addHeader(Constants.PARAM_UP_SIZE, String.valueOf(fileSize));
			request.addHeader(Constants.PARAM_FILE_LAST_MODIFIED, String.valueOf(lastModified));

			// if you comment the following line, the system default user-agent will be used
			request.setCustomUserAgent("FilelugUploadService-Android/1.0");

			try {
//				if ( Constants.DEBUG ) Log.d(TAG, "UploadService.startUpload(request), fileName=" + fileName + ", transferKey=" + transferKey);
				UploadService.startUpload(request);
			} catch (Exception exc) {
				Toast.makeText(context, "Malformed upload request. " + exc.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			}

		}
	}

	public static void uploadFile(Context context, String userId, int computerId, String lugServerId, String authToken, String groupId, String transferKey, boolean fromAnotherApp) {
		UploadGroupSelection uploadGroupSelection = new UploadGroupSelection()
			.userId(userId).and()
			.computerId(computerId).and()
			.groupId(groupId);
		UploadGroupCursor c1 = uploadGroupSelection.query(context.getContentResolver(), null, null);
		if ( !c1.moveToFirst() ) {
			c1.close();
			Log.e(TAG, "uploadFile(), Group ID: " + groupId + " not found!");
			return;
		}

		String uploadDir = c1.getUploadDirectory();
		c1.close();

		AssetFileSelection assetFileSelection = new AssetFileSelection()
			.userId(userId).and()
			.computerId(computerId).and()
			.groupId(groupId).and()
			.transferKey(transferKey);
		AssetFileCursor c2 = assetFileSelection.query(context.getContentResolver(), null, null);
		if ( !c2.moveToFirst() ) {
			c2.close();
			Log.e(TAG, "uploadFile(), Transfer key: " + transferKey + " not found!");
			return;
		}
		long totalSize = c2.getTotalSize();
		long lastModified = c2.getLastModifiedTimestamp();
		String fileName = c2.getServerFileName();
		String fullName = c2.getAssetUrl();
		String cacheFileName = c2.getCacheFileName();
		String contentType = c2.getContentType();
		c2.close();

		AssetFileContentValues values = new AssetFileContentValues()
			.putTotalSize(totalSize)
			.putTransferredSize(0l)
			.putStartTimestampNull()
			.putEndTimestampNull()
			.putWaitToConfirm(false)
			.putStatus(UploadStatusType.wait);
		values.update(context.getContentResolver(), assetFileSelection);

		UploadRequest request = new UploadRequest(context, userId, computerId, groupId, transferKey, lugServerId, authToken, fromAnotherApp);
		request.addFileToUpload(fullName, fileName, cacheFileName, contentType, totalSize, false, 0l);
		request.setNotificationConfig(fileName);
		//You can add your own custom headers
		request.addHeader(Constants.HTTP_HEADER_AUTHORIZATION, authToken);
		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
		request.addHeader(Constants.PARAM_UP_KEY, transferKey);
		String updirStr = RepositoryUtility.encrypt2Base64(uploadDir);
		String upnameStr = RepositoryUtility.encrypt2Base64(fileName);
		request.addHeader(Constants.PARAM_UP_DIR, updirStr);
		request.addHeader(Constants.PARAM_UP_NAME, upnameStr);
		request.addHeader(Constants.PARAM_UP_SIZE, String.valueOf(totalSize));
		request.addHeader(Constants.PARAM_FILE_LAST_MODIFIED, String.valueOf(lastModified));

		// if you comment the following line, the system default user-agent will be used
		request.setCustomUserAgent("FilelugUploadService-Android/1.0");

		try {
//			if ( Constants.DEBUG ) Log.d(TAG, "UploadService.startUpload(request), fileName=" + fileName + ", transferKey=" + transferKey);
			UploadService.startUpload(request);
		} catch (Exception exc) {
			Toast.makeText(context, "Malformed upload request. " + exc.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	public static void uploadFile_resume(Context context, String userId, int computerId, String lugServerId, String authToken, String groupId, String transferKey, long responseTransferredSize, long responseFileSize, long responseFileLastModifiedDate) {
//		if ( Constants.DEBUG ) Log.d(TAG, "uploadFile_resume(), responseTransferredSize=" + responseTransferredSize + ", responseFileSize=" + responseFileSize + ", responseFileLastModifiedDate=" + responseFileLastModifiedDate);
		UploadGroupSelection uploadGroupSelection = new UploadGroupSelection()
			.userId(userId).and()
			.computerId(computerId).and()
			.groupId(groupId);
		UploadGroupCursor c1 = uploadGroupSelection.query(context.getContentResolver(), null, null);
		if ( !c1.moveToFirst() ) {
			c1.close();
			Log.e(TAG, "uploadFile(), Group ID: " + groupId + " not found!");
			return;
		}

		String uploadDir = c1.getUploadDirectory();
		c1.close();

		AssetFileSelection assetFileSelection = new AssetFileSelection()
			.userId(userId).and()
			.computerId(computerId).and()
			.groupId(groupId).and()
			.transferKey(transferKey);
		AssetFileCursor c2 = assetFileSelection.query(context.getContentResolver(), null, null);
		if ( !c2.moveToFirst() ) {
			c2.close();
			Log.e(TAG, "uploadFile(), Transfer key: " + transferKey + " not found!");
			return;
		}
		String fileName = c2.getServerFileName();
		String fullName = c2.getAssetUrl();
		String cacheFileName = c2.getCacheFileName();
		String contentType = c2.getContentType();
		c2.close();

		long tmpFileSize = responseFileSize;
		long tmpCachedSize = 0L;
		long tmpLastModified = responseFileLastModifiedDate;
		boolean fileChanged = false;

		if ( !TextUtils.isEmpty(cacheFileName) ) {
			File newFile = new File(cacheFileName);
			if ( !newFile.exists() ) {
				MsgUtils.showErrorMessage(context, context.getString(R.string.message_specified_file_not_found));
				return;
			}
			if ( !newFile.canRead() ) {
				MsgUtils.showErrorMessage(context, context.getString(R.string.message_unable_to_read_file));
				return;
			}

		} else {
			File newFile = new File(fullName);
			if ( !newFile.exists() ) {
				MsgUtils.showErrorMessage(context, context.getString(R.string.message_specified_file_not_found));
				return;
			}
			if ( !newFile.canRead() ) {
				MsgUtils.showErrorMessage(context, context.getString(R.string.message_unable_to_read_file));
				return;
			}

			LocalFileObject newFileObject = new LocalFileObject(newFile);
			long newFileLastModified = newFileObject.getLastModifiedDate().getTime();
			long newFileSize = newFileObject.getSize();
			if ( responseFileLastModifiedDate != newFileLastModified || responseFileSize != newFileSize ) {
				fileChanged = true;
				tmpFileSize = newFileSize;
				tmpLastModified = newFileLastModified;
			}
		}

//		if ( Constants.DEBUG ) Log.d(TAG, "uploadFile_resume(), newFile=" + newFile.getName() + ", newFileSize=" + newFileSize + ", newFileLastModified=" + newFileLastModified + ", fileChanged=" + fileChanged);

		AssetFileContentValues values = new AssetFileContentValues()
			.putEndTimestampNull()
			.putStatus(UploadStatusType.wait)
			.putWaitToConfirm(false);
		if ( fileChanged ) {
			values.putTotalSize(tmpFileSize)
				.putTransferredSize(tmpCachedSize)
				.putLastModifiedTimestamp(tmpLastModified);
//			if ( Constants.DEBUG ) Log.d(TAG, "uploadFile_resume(), fileChanged=" + fileChanged + ", UPDATE AssetFile SET totalSize=" + tmpFileSize + ", transferredSize=" + tmpCachedSize + ", lastModifiedTimestamp=" + tmpLastModified);
		} else {
			if ( responseTransferredSize == 0 ) {
				values.putTransferredSize(tmpCachedSize);
//				if ( Constants.DEBUG ) Log.d(TAG, "uploadFile_resume(), fileChanged=" + fileChanged + ", UPDATE AssetFile SET transferredSize=" + tmpCachedSize);
			} else if ( responseTransferredSize != responseFileSize ) {
				tmpCachedSize = responseTransferredSize;
				values.putTransferredSize(responseTransferredSize);
//				if ( Constants.DEBUG ) Log.d(TAG, "uploadFile_resume(), fileChanged=" + fileChanged + ", UPDATE AssetFile SET transferredSize=" + tmpCachedSize);
			} else {
				tmpCachedSize = responseFileSize;
				values.putTransferredSize(responseTransferredSize);
//				if ( Constants.DEBUG ) Log.d(TAG, "uploadFile_resume(), fileChanged=" + fileChanged + ", UPDATE AssetFile SET transferredSize=" + tmpCachedSize);
			}
		}
		values.update(context.getContentResolver(), assetFileSelection);

		UploadRequest request = new UploadRequest(context, userId, computerId, groupId, transferKey, lugServerId, authToken, false);
		request.addFileToUpload(fullName, fileName, cacheFileName, contentType, tmpFileSize, true, tmpCachedSize);
		request.setNotificationConfig(fileName);
		//You can add your own custom headers
		request.addHeader(Constants.HTTP_HEADER_AUTHORIZATION, authToken);
		request.addHeader(Constants.REQ_HEADER_ACCEPT_ENCODING, Constants.GZIP_DEFLATE);
		request.addHeader(Constants.PARAM_UP_KEY, transferKey);
		String updirStr = RepositoryUtility.encrypt2Base64(uploadDir);
		String upnameStr = RepositoryUtility.encrypt2Base64(fileName);
		request.addHeader(Constants.PARAM_UP_DIR, updirStr);
		request.addHeader(Constants.PARAM_UP_NAME, upnameStr);
		request.addHeader(Constants.PARAM_UP_SIZE, String.valueOf(tmpFileSize));
		request.addHeader(Constants.PARAM_FILE_LAST_MODIFIED, String.valueOf(tmpLastModified));
		if ( responseTransferredSize != responseFileSize ) {
			request.addHeader(Constants.PARAM_FILE_RANGE, "bytes=" + String.valueOf(tmpCachedSize)+"-");
		} else {
			request.addHeader(Constants.PARAM_UPLOADED_BUT_UNCOMFIRMED, "1");
		}

		// if you comment the following line, the system default user-agent will be used
		request.setCustomUserAgent("FilelugUploadService-Android/1.0");

		try {
//			if ( Constants.DEBUG ) Log.d(TAG, "UploadService.startUpload(request), fileName=" + fileName + ", transferKey=" + transferKey);
			UploadService.startUpload(request);
		} catch (Exception exc) {
			Toast.makeText(context, "Malformed upload request. " + exc.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	public static String[] getAbsolutePathsFromUris(final Context context, final Uri[] uris) {
		return getAbsolutePathsFromUris(context, uris, false);
	}

	public static String[] getAbsolutePathsFromUris(final Context context, final Uri[] uris, final boolean mustCanRead) {
		File[] files = getFilesFromUris(context, uris, mustCanRead);
		int filesLength = files.length;
		String[] paths = new String[filesLength];
		for (int i = 0; i < filesLength; i++) {
			paths[i] = files[i].getAbsolutePath();
		}
		return paths;
	}

	public static File[] getFilesFromUris(final Context context, final Uri[] uris) {
		return getFilesFromUris(context, uris, false);
	}

	public static File[] getFilesFromUris(final Context context, final Uri[] uris, final boolean mustCanRead) {
		ArrayList<File> fileList = new ArrayList<File>();
		int urisLength = uris.length;
		for (int i = 0; i < urisLength; i++) {
			Uri uri = uris[i];
			File file = getFileFromUri(context, uri, mustCanRead);
			if (file != null) {
				fileList.add(file);
			}
		}
		File[] files = new File[fileList.size()];
		fileList.toArray(files);
		return files;
	}

	public static String getAbsolutePathFromUri(final Context context, final Uri uri) {
		return getAbsolutePathFromUri(context, uri, false);
	}

	public static String getAbsolutePathFromUri(final Context context, final Uri uri, final boolean mustCanRead) {
		File file = getFileFromUri(context, uri, mustCanRead);
		if (file != null) {
			return file.getAbsolutePath();
		} else {
			return null;
		}
	}

	public static File getFileFromUri(final Context context, final Uri uri) {
		return getFileFromUri(context, uri, false);
	}

	@SuppressLint("NewApi")
	public static File getFileFromUri(final Context context, final Uri uri, final boolean mustCanRead) {
		if (uri == null) {
			return null;
		}
		// 判斷是否為Android 4.4之後的版本
		final boolean after44 = Build.VERSION.SDK_INT >= 19;
		if (after44 && DocumentsContract.isDocumentUri(context, uri)) {
			// 如果是Android 4.4之後的版本，而且屬於文件URI
			final String authority = uri.getAuthority();
			// 判斷Authority是否為本地端檔案所使用的
			if ("com.android.externalstorage.documents".equals(authority)) {
				// 外部儲存空間
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] divide = docId.split(":");
				final String type = divide[0];
				if ("primary".equals(type)) {
					String path = Environment.getExternalStorageDirectory() + "/" + divide[1];
					return createFileObjFromPath(path, mustCanRead);
				}
			} else if ("com.android.providers.downloads.documents".equals(authority)) {
				// 下載目錄
				final String docId = DocumentsContract.getDocumentId(uri);
				final Uri downloadUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
				String path = queryAbsolutePath(context, downloadUri);
				return createFileObjFromPath(path, mustCanRead);
			} else if ("com.android.providers.media.documents".equals(authority)) {
				// 圖片、影音檔案
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] divide = docId.split(":");
				final String type = divide[0];
				Uri mediaUri = null;
				if ("image".equals(type)) {
					mediaUri = Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					mediaUri = Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				} else {
					return null;
				}
				mediaUri = ContentUris.withAppendedId(mediaUri, Long.parseLong(divide[1]));
				String path = queryAbsolutePath(context, mediaUri);
				return createFileObjFromPath(path, mustCanRead);
			}
		} else {
			// 如果是一般的URI
			final String scheme = uri.getScheme();
			String path = null;
			if ("content".equals(scheme)) {
				// 內容URI
				path = queryAbsolutePath(context, uri);
			} else if ("file".equals(scheme)) {
				// 檔案URI
				path = uri.getPath();
			}
			return createFileObjFromPath(path, mustCanRead);
		}
		return null;
	}

	public static File createFileObjFromPath(final String path) {
		return createFileObjFromPath(path, false);
	}

	public static File createFileObjFromPath(final String path, final boolean mustCanRead) {
		if (path != null) {
			try {
				File file = new File(path);
				if (mustCanRead) {
					file.setReadable(true);
					if (!file.canRead()) {
						return null;
					}
				}
				return file.getAbsoluteFile();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	public static String queryAbsolutePath(final Context context, final Uri uri) {
		final String[] projection = { MediaColumns.DATA };
		Cursor cursor = null;
		String path = null;
		try {
			cursor = context.getContentResolver().query(uri, projection, null, null, null);
			if ( cursor != null && cursor.moveToFirst() ) {
				final int index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
				path = cursor.getString(index);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return path;
	}

	@SuppressLint("NewApi")
	public static LocalFileObject getLocalFileObjectFromUri(final Context context, final Uri uri, final boolean notFolder, final boolean mustCanRead) {
		if ( uri == null ) {
			return null;
		}
		final String scheme = uri.getScheme();
		if ( scheme == null || !( "content".equals(scheme) || "file".equals(scheme) ) ) {
			return null;
		}

		File file = null;
		LocalFileObject localFileObject = null;

		// 判斷是否為Android 4.4之後的版本
		final boolean after44 = Build.VERSION.SDK_INT >= 19;

		if ( after44 && DocumentsContract.isDocumentUri(context, uri) ) {
			// 如果是Android 4.4之後的版本，而且屬於文件URI
			final String authority = uri.getAuthority();

			// 判斷Authority是否為本地端檔案所使用的
			if ( "com.android.externalstorage.documents".equals(authority) ) {
				// 外部儲存空間
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] divide = docId.split(":");
				final String type = divide[0];
				if ( "primary".equals(type) ) {
					String path = Environment.getExternalStorageDirectory() + "/" + divide[1];
					file = createFileObjFromPath(path, mustCanRead);
				}
			} else if ( "com.android.providers.downloads.documents".equals(authority) ) {
				// 下載目錄
				final String docId = DocumentsContract.getDocumentId(uri);
				final Uri downloadUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
				String path = queryAbsolutePath(context, downloadUri);
				file = createFileObjFromPath(path, mustCanRead);
			} else if ( "com.android.providers.media.documents".equals(authority) ) {
				// 圖片、影音檔案
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] divide = docId.split(":");
				final String type = divide[0];
				Uri mediaUri = null;
				if ( "image".equals(type) ) {
					mediaUri = Images.Media.EXTERNAL_CONTENT_URI;
				} else if ( "video".equals(type) ) {
					mediaUri = Video.Media.EXTERNAL_CONTENT_URI;
				} else if ( "audio".equals(type) ) {
					mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				} else {
					return null;
				}
				mediaUri = ContentUris.withAppendedId(mediaUri, Long.parseLong(divide[1]));
				String path = queryAbsolutePath(context, mediaUri);
				file = createFileObjFromPath(path, mustCanRead);
			}
		} else if ( "file".equals(scheme) ) {
			String path = uri.getPath();
			file = createFileObjFromPath(path, mustCanRead);
		} else { // Schema 為 "content" 的 Uri
			localFileObject = new LocalFileObject(uri);
		}

		if ( file != null ) {
			if ( notFolder && !file.isDirectory() ) {
				localFileObject = new LocalFileObject(file);
			}
		}

		return localFileObject;
	}

	public static long getMediaIdFromUri(final Context context, final Uri uri) {
		String[] projection = { BaseColumns._ID };
		Cursor imageCursor = context.getContentResolver().query(uri, projection, null, null, null);
		long mediaId = -1 ;
		if ( imageCursor != null && imageCursor.moveToFirst() ){
			int mediaIdCol = imageCursor.getColumnIndex( BaseColumns._ID );
			mediaId = imageCursor.getLong( imageCursor.getColumnIndex(BaseColumns._ID) );
		}
		imageCursor.close();
		return mediaId;
	}

	public static void stopUploadTask(Context context, long rowId) {
		String[] projection = new String[] { AssetFileColumns.USER_ID, AssetFileColumns.COMPUTER_ID, AssetFileColumns.GROUP_ID, AssetFileColumns.TRANSFER_KEY };
		AssetFileSelection selection = new AssetFileSelection()
			.id(rowId);

		AssetFileCursor c = selection.query(context.getContentResolver(), projection);
		if ( !c.moveToFirst() ) {
			c.close();
			return;
		}
		String userId = c.getUserId();
		int computerId = c.getComputerId();
		String groupId = c.getGroupId();
		String transferKey = c.getTransferKey();
		c.close();

		UploadService.stopUpload(userId, computerId, groupId, transferKey);
	}

}
