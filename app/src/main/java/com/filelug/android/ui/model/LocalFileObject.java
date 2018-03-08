package com.filelug.android.ui.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.filelug.android.MainApplication;
import com.filelug.android.R;
import com.filelug.android.util.FileCache;
import com.filelug.android.util.FormatUtils;
import com.filelug.android.util.LocalFileUtils;
import com.filelug.android.util.MiscUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class LocalFileObject implements LocalFile, Parcelable {

	private static final String TAG = LocalFileObject.class.getSimpleName();

	// parcel keys
	private static final String KEY_SYMLINK = "symlink";
	private static final String KEY_NAME = "name";
	private static final String KEY_PARENT = "parent";
	private static final String KEY_READABLE = "readable";
	private static final String KEY_WRITABLE = "writable";
	private static final String KEY_HIDDEN = "hidden";
	private static final String KEY_LAST_MODIFIED_DATE = "lastModifiedDate";
	private static final String KEY_TYPE = "type";
	private static final String KEY_CONTENT_TYPE = "contentType";
	private static final String KEY_REAL_NAME = "realName";
	private static final String KEY_REAL_PARENT = "realParent";
	private static final String KEY_SIZE = "size";
	private static final String KEY_DISPLAY_NAME = "displayName";
	private static final String KEY_DISPLAY_COUNT_OR_SIZE = "displayCountOrSize";
	private static final String KEY_DISPLAY_LAST_MODIFIED_DATE = "displayLastModifiedDate";
	private static final String KEY_FULL_NAME = "fullName";
	private static final String KEY_URI = "uri";
	private static final String KEY_CACHE_FILE_NAME = "cacheFileName";
	private static final String KEY_EXTENSION = "extension";
	private static final String KEY_FILE_COUNT = "fileCount";
	private static final String KEY_MEDIA_TYPE = "mediaType";
	private static final String KEY_MEDIA_ID = "mediaId";
	private static final String KEY_MEDIA_PARENT_ID = "mediaParentId";

	private boolean symlink = false;
	private String name = null;
	private String parent = null;
	private boolean readable = false;
	private boolean writable = false;
	private boolean hidden = false;
	private Date lastModifiedDate = null;
	private FileType type = FileType.UNKNOWN;
	private String contentType = null;
	private String realName = null;
	private String realParent = null;
	private long size = 0;
	private String displayName = null;
	private String displayCountOrSize = null;
	private String displayLastModifiedDate = null;
	private String fullName = null;
	private Uri uri = null;
	private String cacheFileName = null;
	private String extension = null;
	private int fileCount = 0;
	private MediaType mediaType = MediaType.NONE;
	private long mediaId = -1L;
	private long mediaParentId = -1L;

	public LocalFileObject(File file) {
		checkCanonicalFile(file);
		init(file);
	}

	public LocalFileObject(FileType type, String displayName, File file) {
		this.type = type;
		this.displayName = displayName;
		init(file);
	}

	public LocalFileObject(FileType type, String displayName) {
		this.type = type;
		this.displayName = displayName;
		this.name = displayName;
		this.fullName = displayName;
		this.readable = true;
	}

	public LocalFileObject(Bundle bundle) {
		this.symlink = bundle.getBoolean(KEY_SYMLINK);
		this.name = bundle.getString(KEY_NAME);
		this.parent = bundle.getString(KEY_PARENT);
		this.readable = bundle.getBoolean(KEY_READABLE);
		this.writable = bundle.getBoolean(KEY_WRITABLE);
		this.hidden = bundle.getBoolean(KEY_HIDDEN);
		long tmpDate = bundle.getLong(KEY_LAST_MODIFIED_DATE, 0l);
		this.lastModifiedDate = tmpDate != 0 ? new Date(bundle.getLong(KEY_LAST_MODIFIED_DATE)) : null;
		this.type = (FileType)bundle.get(KEY_TYPE);
		this.contentType = bundle.getString(KEY_CONTENT_TYPE);
		this.realName = bundle.getString(KEY_REAL_NAME);
		this.realParent = bundle.getString(KEY_REAL_PARENT);
		this.size = bundle.getLong(KEY_SIZE);
		this.displayName = bundle.getString(KEY_DISPLAY_NAME);
		this.displayCountOrSize = bundle.getString(KEY_DISPLAY_COUNT_OR_SIZE);
		this.displayLastModifiedDate = bundle.getString(KEY_DISPLAY_LAST_MODIFIED_DATE);
		this.fullName = bundle.getString(KEY_FULL_NAME);
		this.uri = bundle.getParcelable(KEY_URI);
		this.cacheFileName = bundle.getString(KEY_CACHE_FILE_NAME);
		this.extension = bundle.getString(KEY_EXTENSION);
		this.fileCount = bundle.getInt(KEY_FILE_COUNT);
		this.mediaType = (MediaType)bundle.get(KEY_MEDIA_TYPE);
		this.mediaId = bundle.getLong(KEY_MEDIA_ID);
		this.mediaParentId = bundle.getLong(KEY_MEDIA_PARENT_ID);
	}

	public LocalFileObject(MediaType mediaType, long mediaId, long mediaParentId, FileType type, String displayName, File file) {
		this.mediaType = mediaType;
		this.mediaId = mediaId;
		this.mediaParentId = mediaParentId;
		this.type = type;
		this.displayName = displayName;
		init(file);
	}

	// Uri with "content" scheme
	public LocalFileObject(Uri uri) {
		this.uri = uri;
		uriInit(uri);
	}

	private void uriInit(Uri uri) {

		if ( uri == null ) return;
		this.type = FileType.LOCAL_FILE;
		this.fullName = uri.toString();

		Context ctx = MainApplication.getInstance().getApplicationContext();
		ContentResolver contentResolver = ctx.getContentResolver();
		this.contentType = contentResolver.getType(uri);
		Cursor metadataCursor;
		try {
			metadataCursor = contentResolver.query(uri, new String[] {
				OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
			}, null, null, null);
		} catch (SQLiteException e) {
			// some content providers don't support the DISPLAY_NAME or SIZE columns
			metadataCursor = null;
			Log.e(TAG, "uriInit(), DISPLAY_NAME or SIZE column not found!");
		}
		if (metadataCursor != null) {
			try {
				if (metadataCursor.moveToFirst()) {
					this.name = metadataCursor.getString(0);
					this.size = metadataCursor.getLong(1);
//					if ( Constants.DEBUG ) Log.d(TAG, "uriInit(), name=" + name + ", size=" + size);
				}
			} finally {
				metadataCursor.close();
			}
		}
		if ( this.name == null ) {
			// use last segment of URI if DISPLAY_NAME query fails
			this.name = uri.getLastPathSegment();
		}
		this.displayName = TextUtils.isEmpty(this.name) ? this.fullName : this.name;

		File cacheFile = new File(FileCache.OUT_CACHE_DIR, UUID.randomUUID().toString());
		this.cacheFileName = cacheFile.getAbsolutePath();
	}

	private void init(File file) {

		if ( file == null ) return;

		Context ctx = MainApplication.getInstance().getApplicationContext();

		this.name = file.getName();
		this.parent = file.getParent();
		this.readable = file.canRead();
		this.writable = file.canWrite();
		this.hidden = file.isHidden();

		this.fullName = file.getAbsolutePath();
		long tmpDate = file.lastModified();
		if ( tmpDate != 0 ) {
			this.lastModifiedDate = new Date(tmpDate);
			this.displayLastModifiedDate = FormatUtils.formatDate1(ctx, this.lastModifiedDate);
		} else {
			this.lastModifiedDate = null;
			this.displayLastModifiedDate = "";
		}
		if ( this.displayName == null ) this.displayName = this.name;
		this.fileCount = 0;

		if ( this.type == FileType.SYS_DIR_INTERNAL_STORAGE ||
			 this.type == FileType.SYS_DIR_SD_CARD_STORAGE ||
			 this.type == FileType.SYS_DIR_USB_STORAGE ) {
			String usableSpace = FormatUtils.formatFileSize(ctx, file.getUsableSpace());
			String totalSpace = FormatUtils.formatFileSize(ctx, file.getTotalSpace());
			this.displayCountOrSize = String.format( ctx.getResources().getString(R.string.message_storage_usage), usableSpace, totalSpace );
		} else if ( this.type == FileType.MEDIA_DIR ) {
			if ( this.mediaType == MediaType.IMAGE ) {
				this.fileCount = LocalFileUtils.getLocalPictureCountById(ctx, this.mediaId);
			} else if ( this.mediaType == MediaType.AUDIO ) {
				this.fileCount = LocalFileUtils.getLocalMusicCountById(ctx, this.mediaId);
			} else if ( this.mediaType == MediaType.VIDEO ) {
				this.fileCount = LocalFileUtils.getLocalMovieCountById(ctx, this.mediaId);
			} else if ( this.mediaType == MediaType.DOCUMENT ) {
				this.fileCount = LocalFileUtils.getLocalDocumentCountById(ctx, this.mediaId);
			} else if ( this.mediaType == MediaType.DOWNLOAD ) {
				String[] lists = file.list();
				this.fileCount = lists != null ? lists.length : 0;
			}
			this.displayCountOrSize = FormatUtils.formatFileCount(ctx, this.fileCount);
		} else if ( this.type == FileType.LOCAL_DIR ||
					this.type == FileType.LOCAL_SYMBOLIC_LINK_DIR ) {
			String[] lists = file.list();
			this.fileCount = lists != null ? lists.length : 0;
			this.displayCountOrSize = FormatUtils.formatFileCount(ctx, this.fileCount);
		} else if ( this.type == FileType.LOCAL_FILE ||
					this.type == FileType.LOCAL_SYMBOLIC_LINK_FILE ||
					this.type == FileType.MEDIA_FILE ||
					this.type == FileType.UNKNOWN ) {
			this.size = file.length();
			this.displayCountOrSize = FormatUtils.formatFileSize(ctx, this.size);
		}

		if ( this.type == FileType.LOCAL_FILE ||
			 this.type == FileType.LOCAL_SYMBOLIC_LINK_FILE ||
			 this.type == FileType.MEDIA_FILE ) {
			this.extension = MiscUtils.getExtension(this.name);
			MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
			if ( mimeTypeMap.hasExtension(this.extension) ) {
				this.contentType = mimeTypeMap.getMimeTypeFromExtension(extension);
			}
		}

	}

	private void checkCanonicalFile(File file) {
		if ( file == null ) {
			return;
		}

		if ( file.isDirectory() ) {
			this.type = FileType.LOCAL_DIR;
		} else if ( file.isFile() ) {
			this.type = FileType.LOCAL_FILE;
		}
		this.displayName = file.getName();

		try {
			File canonicalFile = file.getCanonicalFile();
			if ( canonicalFile == null ) {
				return;
			}
			String absolutePath = file.getAbsolutePath();
			String canonicalPath = canonicalFile.getAbsolutePath();
			if ( TextUtils.equals(absolutePath, canonicalPath) ) {
				return;
			}
			this.symlink = true;
			this.realName = canonicalFile.getName();
			this.realParent = canonicalFile.getParent();
			if ( file.isDirectory() ) {
				this.type = FileType.LOCAL_SYMBOLIC_LINK_DIR;
			} else if ( file.isFile() ) {
				this.type = FileType.LOCAL_SYMBOLIC_LINK_FILE;
			}
			this.displayName = file.getName();
		} catch (IOException e) {
		}
	}

	@Override
	public boolean isSymlink() {
		return this.symlink;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getParent() {
		return this.parent;
	}

	@Override
	public boolean isReadable() {
		return this.readable;
	}

	@Override
	public boolean isWritable() {
		return this.writable;
	}

	@Override
	public boolean isHidden() {
		return this.hidden;
	}

	@Override
	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	@Override
	public FileType getType() {
		return this.type;
	}

	public void setType(FileType type) {
		this.type = type;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public String getRealName() {
		return this.realName;
	}

	@Override
	public String getRealParent() {
		return this.realParent;
	}

	@Override
	public long getSize() {
		return this.size;
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	@Override
	public String getDisplayCountOrSize() {
		return this.displayCountOrSize;
	}

	@Override
	public String getDisplayLastModifiedDate() {
		return this.displayLastModifiedDate;
	}

	@Override
	public String getFullName() {
		return this.fullName;
	}

	@Override
	public Uri getUri() {
		return this.uri;
	}

	@Override
	public String getCacheFileName() {
		return this.cacheFileName;
	}

	@Override
	public String getExtension() {
		return this.extension != null ? this.extension : "";
	}

	@Override
	public int getFileCount() {
		return this.fileCount;
	}

	@Override
	public MediaType getMediaType() {
		return this.mediaType;
	}

	@Override
	public long getMediaId() {
		return this.mediaId;
	}

	@Override
	public long getMediaParentId() {
		return this.mediaParentId;
	}

	@Override
	public String toString() {
		return this.getDisplayName();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// create a bundle for the key value pairs
		Bundle bundle = new Bundle();
		// insert the key value pairs to the bundle
		bundle.putBoolean(KEY_SYMLINK, symlink);
		bundle.putString(KEY_NAME, name);
		bundle.putString(KEY_PARENT, parent);
		bundle.putBoolean(KEY_READABLE, readable);
		bundle.putBoolean(KEY_WRITABLE, writable);
		bundle.putBoolean(KEY_HIDDEN, hidden);
		bundle.putLong(KEY_LAST_MODIFIED_DATE, lastModifiedDate != null ? lastModifiedDate.getTime() : 0l);
		bundle.putSerializable(KEY_TYPE, type);
		bundle.putString(KEY_CONTENT_TYPE, contentType);
		bundle.putString(KEY_REAL_NAME, realName);
		bundle.putString(KEY_REAL_PARENT, realParent);
		bundle.putLong(KEY_SIZE, size);
		bundle.putString(KEY_DISPLAY_NAME, displayName);
		bundle.putString(KEY_DISPLAY_COUNT_OR_SIZE, displayCountOrSize);
		bundle.putString(KEY_DISPLAY_LAST_MODIFIED_DATE, displayLastModifiedDate);
		bundle.putString(KEY_FULL_NAME, fullName);
		bundle.putParcelable(KEY_URI, uri);
		bundle.putString(KEY_CACHE_FILE_NAME, cacheFileName);
		bundle.putString(KEY_EXTENSION, extension);
		bundle.putInt(KEY_FILE_COUNT, fileCount);
		bundle.putSerializable(KEY_MEDIA_TYPE, mediaType);
		bundle.putLong(KEY_MEDIA_ID, mediaId);
		bundle.putLong(KEY_MEDIA_PARENT_ID, mediaParentId);
		// write the key value pairs to the parcel
		dest.writeBundle(bundle);
	}

	/**
	 * Creator required for class implementing the parcelable interface.
	 */
	public static final Parcelable.Creator<LocalFileObject> CREATOR = new Creator<LocalFileObject>() {

		@Override
		public LocalFileObject createFromParcel(Parcel source) {
			// read the bundle containing key value pairs from the parcel
			Bundle bundle = source.readBundle(getClass().getClassLoader());
			// instantiate a person using values from the bundle
			return new LocalFileObject(bundle);
		}

		@Override
		public LocalFileObject[] newArray(int size) {
			return new LocalFileObject[size];
		}

	};

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LocalFileObject that = (LocalFileObject) o;

		if (symlink != that.symlink) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
		if (readable != that.readable) return false;
		if (writable != that.writable) return false;
		if (hidden != that.hidden) return false;
		if (lastModifiedDate != null ? !lastModifiedDate.equals(that.lastModifiedDate) : that.lastModifiedDate != null) return false;
		if (type != null ? !type.equals(that.type) : that.type != null) return false;
		if (contentType != null ? !contentType.equals(that.contentType) : that.contentType != null) return false;
		if (realName != null ? !realName.equals(that.realName) : that.realName != null) return false;
		if (realParent != null ? !realParent.equals(that.realParent) : that.realParent != null) return false;
		if (size != that.size) return false;
		if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
		if (displayCountOrSize != null ? !displayCountOrSize.equals(that.displayCountOrSize) : that.displayCountOrSize != null) return false;
		if (displayLastModifiedDate != null ? !displayLastModifiedDate.equals(that.displayLastModifiedDate) : that.displayLastModifiedDate != null) return false;
		if (fullName != null ? !fullName.equals(that.fullName) : that.fullName != null) return false;
		if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;
		if (cacheFileName != null ? !cacheFileName.equals(that.cacheFileName) : that.cacheFileName != null) return false;
		if (extension != null ? !extension.equals(that.extension) : that.extension != null) return false;
		if (fileCount != that.fileCount) return false;
		if (mediaType != null ? !mediaType.equals(that.mediaType) : that.mediaType != null) return false;
		if (mediaId != that.mediaId) return false;
		if (mediaParentId != that.mediaParentId) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (symlink ? 1 : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (parent != null ? parent.hashCode() : 0);
		result = 31 * result + (readable ? 1 : 0);
		result = 31 * result + (writable ? 1 : 0);
		result = 31 * result + (hidden ? 1 : 0);
		result = 31 * result + (lastModifiedDate != null ? lastModifiedDate.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
		result = 31 * result + (realName != null ? realName.hashCode() : 0);
		result = 31 * result + (realParent != null ? realParent.hashCode() : 0);
		result = 31 * result + (int) (size ^ (size >>> 32));
		result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
		result = 31 * result + (displayCountOrSize != null ? displayCountOrSize.hashCode() : 0);
		result = 31 * result + (displayLastModifiedDate != null ? displayLastModifiedDate.hashCode() : 0);
		result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
		result = 31 * result + (uri != null ? uri.hashCode() : 0);
		result = 31 * result + (cacheFileName != null ? cacheFileName.hashCode() : 0);
		result = 31 * result + (extension != null ? extension.hashCode() : 0);
		result = 31 * result + (int) (fileCount ^ (fileCount >>> 32));
		result = 31 * result + (mediaType != null ? mediaType.hashCode() : 0);
		result = 31 * result + (int) (mediaId ^ (mediaId >>> 32));
		result = 31 * result + (int) (mediaParentId ^ (mediaParentId >>> 32));
		return result;
	}

}
