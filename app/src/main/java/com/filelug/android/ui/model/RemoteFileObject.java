package com.filelug.android.ui.model;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.filelug.android.Constants;
import com.filelug.android.MainApplication;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteHierarchicalModelCursor;
import com.filelug.android.provider.remotehierarchicalmodel.RemoteObjectType;
import com.filelug.android.util.FormatUtils;
import com.filelug.android.util.MiscUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vincent Chang on 2015/7/31.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class RemoteFileObject implements RemoteFile, Parcelable {

	private static final String TAG = RemoteFileObject.class.getSimpleName();

	// parcel keys
	private static final String KEY_SYMLINK = "symlink";
	private static final String KEY_NAME = "name";
	private static final String KEY_PARENT = "parent";
	private static final String KEY_READABLE = "readable";
	private static final String KEY_WRITABLE = "writable";
	private static final String KEY_HIDDEN = "hidden";
	private static final String KEY_LAST_MODIFIED = "lastModified";
	private static final String KEY_TYPE = "type";
	private static final String KEY_CONTENT_TYPE = "contentType";
	private static final String KEY_REAL_NAME = "realName";
	private static final String KEY_REAL_PARENT = "realParent";
	private static final String KEY_SIZE = "size";
	private static final String KEY_DISPLAY_NAME = "displayName";
	private static final String KEY_DISPLAY_COUNT_OR_SIZE = "displayCountOrSize";
	private static final String KEY_DISPLAY_LAST_MODIFIED_DATE = "displayLastModifiedDate";
	private static final String KEY_FULL_NAME = "fullName";
	private static final String KEY_FULL_REAL_NAME = "fullRealName";
	private static final String KEY_EXTENSION = "extension";
	private static final String KEY_SELECTABLE = "selectable";

	private static final String FILE_TYPE_FILE = "FILE";
	private static final String FILE_TYPE_DIRECTORY = "DIRECTORY";
	private static final String FILE_TYPE_WINDOWS_SHORTCUT_FILE = "WINDOWS_SHORTCUT_FILE";
	private static final String FILE_TYPE_WINDOWS_SHORTCUT_DIRECTORY = "WINDOWS_SHORTCUT_DIRECTORY";
	private static final String FILE_TYPE_UNIX_SYMBOLIC_LINK_FILE = "UNIX_SYMBOLIC_LINK_FILE";
	private static final String FILE_TYPE_UNIX_SYMBOLIC_LINK_DIRECTORY = "UNIX_SYMBOLIC_LINK_DIRECTORY";
	private static final String FILE_TYPE_MAC_ALIAS_FILE = "MAC_ALIAS_FILE";
	private static final String FILE_TYPE_MAC_ALIAS_DIRECTORY = "MAC_ALIAS_DIRECTORY";

	private boolean symlink = false;
	private String name = null;
	private String parent = null;
	private boolean readable = false;
	private boolean writable = false;
	private boolean hidden = false;
	private String lastModified = null;
	private FileType type = FileType.UNKNOWN;
	private String contentType = null;
	private String realName = null;
	private String realParent = null;
	private long size = 0;
	private String displayName = null;
	private String displayCountOrSize = null;
	private String displayLastModifiedDate = null;
	private String fullRealName = null;
	private String fullName = null;
	private String extension = null;
	private boolean selectable = true;

	public RemoteFileObject(JSONObject jso, String fileSeparator) {
		try {
			this.symlink = jso.getBoolean(Constants.PARAM_SYMLINK);
			this.name = jso.getString(Constants.PARAM_NAME);
			this.parent = jso.getString(Constants.PARAM_PARENT);
			this.readable = jso.getBoolean(Constants.PARAM_READABLE);
			this.writable = jso.getBoolean(Constants.PARAM_WRITABLE);
			this.hidden = jso.getBoolean(Constants.PARAM_HIDDEN);
			this.lastModified = jso.getString(Constants.PARAM_LAST_MODIFIED);
			this.size = jso.getLong(Constants.PARAM_SIZE_IN_BYTES);
			this.realParent = jso.getString(Constants.PARAM_REAL_PARENT);
			this.realName = jso.getString(Constants.PARAM_REAL_NAME);
			String typeStr = jso.getString(Constants.PARAM_TYPE);
			if ( FILE_TYPE_FILE.equals(typeStr) ) {
				this.type = FileType.REMOTE_FILE;
			} else if ( FILE_TYPE_DIRECTORY.equals(typeStr) ) {
				this.type = FileType.REMOTE_DIR;
			} else if ( FILE_TYPE_WINDOWS_SHORTCUT_FILE.equals(typeStr) ) {
				this.type = FileType.REMOTE_WINDOWS_SHORTCUT_FILE;
			} else if ( FILE_TYPE_WINDOWS_SHORTCUT_DIRECTORY.equals(typeStr) ) {
				this.type = FileType.REMOTE_WINDOWS_SHORTCUT_DIR;
			} else if ( FILE_TYPE_UNIX_SYMBOLIC_LINK_FILE.equals(typeStr) ) {
				this.type = FileType.REMOTE_UNIX_SYMBOLIC_LINK_FILE;
			} else if ( FILE_TYPE_UNIX_SYMBOLIC_LINK_DIRECTORY.equals(typeStr) ) {
				this.type = FileType.REMOTE_UNIX_SYMBOLIC_LINK_DIR;
			} else if ( FILE_TYPE_MAC_ALIAS_FILE.equals(typeStr) ) {
				this.type = FileType.REMOTE_MAC_ALIAS_FILE;
			} else if ( FILE_TYPE_MAC_ALIAS_DIRECTORY.equals(typeStr) ) {
				this.type = FileType.REMOTE_MAC_ALIAS_DIR;
			}
			this.fullName = this.parent + fileSeparator + this.name;
			this.fullRealName = this.realParent + fileSeparator + this.realName;
			this.extension = MiscUtils.getExtension(this.name);
			this.displayName = this.name;
			this.displayLastModifiedDate = this.lastModified;
			Context ctx = MainApplication.getInstance().getApplicationContext();
			this.displayCountOrSize = FormatUtils.formatFileSize(ctx, this.size);

			this.contentType = jso.getString(Constants.PARAM_CONTENT_TYPE);
			if ( TextUtils.isEmpty(this.contentType) ) {
				MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
				if ( mimeTypeMap.hasExtension(this.extension) ) {
					this.contentType = mimeTypeMap.getMimeTypeFromExtension(extension);
				}
			}
		} catch (JSONException e) {
			Log.e(TAG, "RemoteList json object parsing error!");
		}
	}

	public RemoteFileObject(RemoteHierarchicalModelCursor cursor, String fileSeparator) {
		this.symlink = cursor.getSymlink();
		this.name = cursor.getName();
		this.parent = cursor.getParent();
		this.readable = cursor.getReadable();
		this.writable = cursor.getWritable();
		this.hidden = cursor.getHidden();
		this.lastModified = cursor.getLastModified();
		this.size = cursor.getSize();
		this.realParent = cursor.getRealParent();
		this.realName = cursor.getRealName();
		RemoteObjectType type = cursor.getType();
		if ( RemoteObjectType.FILE.equals(type) ) {
			this.type = FileType.REMOTE_FILE;
		} else if ( RemoteObjectType.DIRECTORY.equals(type) ) {
			this.type = FileType.REMOTE_DIR;
		} else if ( RemoteObjectType.WINDOWS_SHORTCUT_FILE.equals(type) ) {
			this.type = FileType.REMOTE_WINDOWS_SHORTCUT_FILE;
		} else if ( RemoteObjectType.WINDOWS_SHORTCUT_DIRECTORY.equals(type) ) {
			this.type = FileType.REMOTE_WINDOWS_SHORTCUT_DIR;
		} else if ( RemoteObjectType.UNIX_SYMBOLIC_LINK_FILE.equals(type) ) {
			this.type = FileType.REMOTE_UNIX_SYMBOLIC_LINK_FILE;
		} else if ( RemoteObjectType.UNIX_SYMBOLIC_LINK_DIRECTORY.equals(type) ) {
			this.type = FileType.REMOTE_UNIX_SYMBOLIC_LINK_DIR;
		} else if ( RemoteObjectType.MAC_ALIAS_FILE.equals(type) ) {
			this.type = FileType.REMOTE_MAC_ALIAS_FILE;
		} else if ( RemoteObjectType.MAC_ALIAS_DIRECTORY.equals(type) ) {
			this.type = FileType.REMOTE_MAC_ALIAS_DIR;
		}
		this.fullName = this.parent + fileSeparator + this.name;
		this.fullRealName = this.realParent + fileSeparator + this.realName;
		this.extension = MiscUtils.getExtension(this.name);
		this.displayName = this.name;
		this.displayLastModifiedDate = this.lastModified;
		Context ctx = MainApplication.getInstance().getApplicationContext();
		this.displayCountOrSize = FormatUtils.formatFileSize(ctx, this.size);

		this.contentType = cursor.getContentType();
		if ( TextUtils.isEmpty(this.contentType) ) {
			MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
			if ( mimeTypeMap.hasExtension(this.extension) ) {
				this.contentType = mimeTypeMap.getMimeTypeFromExtension(extension);
			}
		}
	}

	public RemoteFileObject(FileType type, String displayName) {
		this.type = type;
		this.displayName = displayName;
		this.name = displayName;
		this.fullName = displayName;
		this.readable = true;
	}

	public RemoteFileObject(FileType type, String displayName, String fullName, String fullRealName) {
		// Remote roots
		this.type = type;
		this.displayName = displayName;
		this.name = displayName;
		this.fullName = fullName;
		this.displayCountOrSize = fullName;
		this.fullRealName = fullRealName;
		this.readable = true;
		if ( type == FileType.REMOTE_ROOT_WINDOWS_SHORTCUT_DIRECTORY ||
			 type == FileType.REMOTE_ROOT_UNIX_SYMBOLIC_LINK_DIRECTORY ||
			 type == FileType.REMOTE_ROOT_MAC_ALIAS_DIRECTORY ) {
			this.symlink = true;
		}
	}

	public RemoteFileObject(Bundle bundle) {
		this.symlink = bundle.getBoolean(KEY_SYMLINK);
		this.name = bundle.getString(KEY_NAME);
		this.parent = bundle.getString(KEY_PARENT);
		this.readable = bundle.getBoolean(KEY_READABLE);
		this.writable = bundle.getBoolean(KEY_WRITABLE);
		this.hidden = bundle.getBoolean(KEY_HIDDEN);
		this.lastModified = bundle.getString(KEY_LAST_MODIFIED);
		this.type = (FileType)bundle.get(KEY_TYPE);
		this.contentType = bundle.getString(KEY_CONTENT_TYPE);
		this.realName = bundle.getString(KEY_REAL_NAME);
		this.realParent = bundle.getString(KEY_REAL_PARENT);
		this.size = bundle.getLong(KEY_SIZE);
		this.displayName = bundle.getString(KEY_DISPLAY_NAME);
		this.displayCountOrSize = bundle.getString(KEY_DISPLAY_COUNT_OR_SIZE);
		this.displayLastModifiedDate = bundle.getString(KEY_DISPLAY_LAST_MODIFIED_DATE);
		this.fullRealName = bundle.getString(KEY_FULL_REAL_NAME);
		this.fullName = bundle.getString(KEY_FULL_NAME);
		this.extension = bundle.getString(KEY_EXTENSION);
		this.selectable = bundle.getBoolean(KEY_SELECTABLE);
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
	public String getLastModified() {
		return this.lastModified;
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
	public String getFullRealName() {
		return this.fullRealName;
	}

	@Override
	public String getExtension() {
		return this.extension != null ? this.extension : "";
	}

	@Override
	public boolean isSelectable() {
		return this.selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public void setFileSeparator(String fileSeparator) {
		this.fullName = this.parent + fileSeparator + this.name;
		this.fullRealName = this.realParent + fileSeparator + this.realName;
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
		bundle.putString(KEY_LAST_MODIFIED, lastModified);
		bundle.putSerializable(KEY_TYPE, type);
		bundle.putString(KEY_CONTENT_TYPE, contentType);
		bundle.putString(KEY_REAL_NAME, realName);
		bundle.putString(KEY_REAL_PARENT, realParent);
		bundle.putLong(KEY_SIZE, size);
		bundle.putString(KEY_DISPLAY_NAME, displayName);
		bundle.putString(KEY_DISPLAY_COUNT_OR_SIZE, displayCountOrSize);
		bundle.putString(KEY_DISPLAY_LAST_MODIFIED_DATE, displayLastModifiedDate);
		bundle.putString(KEY_FULL_REAL_NAME, fullRealName);
		bundle.putString(KEY_FULL_NAME, fullName);
		bundle.putString(KEY_EXTENSION, extension);
		bundle.putBoolean(KEY_SELECTABLE, selectable);
		// write the key value pairs to the parcel
		dest.writeBundle(bundle);
	}

	/**
	 * Creator required for class implementing the parcelable interface.
	 */
	public static final Parcelable.Creator<RemoteFileObject> CREATOR = new Parcelable.Creator<RemoteFileObject>() {

		@Override
		public RemoteFileObject createFromParcel(Parcel source) {
			// read the bundle containing key value pairs from the parcel
			Bundle bundle = source.readBundle(getClass().getClassLoader());
			// instantiate a person using values from the bundle
			return new RemoteFileObject(bundle);
		}

		@Override
		public RemoteFileObject[] newArray(int size) {
			return new RemoteFileObject[size];
		}

	};

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RemoteFileObject that = (RemoteFileObject) o;

		if (symlink != that.symlink) return false;
		if (name != null ? !name.equals(that.name) : that.name != null)	return false;
		if (parent != null ? !parent.equals(that.parent) : that.parent != null)	return false;
		if (readable != that.readable) return false;
		if (writable != that.writable) return false;
		if (hidden != that.hidden) return false;
		if (lastModified != null ? !lastModified.equals(that.lastModified) : that.lastModified != null)	return false;
		if (type != null ? !type.equals(that.type) : that.type != null)	return false;
		if (contentType != null ? !contentType.equals(that.contentType) : that.contentType != null)	return false;
		if (realName != null ? !realName.equals(that.realName) : that.realName != null) return false;
		if (realParent != null ? !realParent.equals(that.realParent) : that.realParent != null) return false;
		if (size != that.size) return false;
		if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
		if (displayCountOrSize != null ? !displayCountOrSize.equals(that.displayCountOrSize) : that.displayCountOrSize != null) return false;
		if (displayLastModifiedDate != null ? !displayLastModifiedDate.equals(that.displayLastModifiedDate) : that.displayLastModifiedDate != null) return false;
		if (fullRealName != null ? !fullRealName.equals(that.fullRealName) : that.fullRealName != null) return false;
		if (fullName != null ? !fullName.equals(that.fullName) : that.fullName != null) return false;
		if (extension != null ? !extension.equals(that.extension) : that.extension != null) return false;
		if (selectable != that.selectable) return false;

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
		result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
		result = 31 * result + (realName != null ? realName.hashCode() : 0);
		result = 31 * result + (realParent != null ? realParent.hashCode() : 0);
		result = 31 * result + (int) (size ^ (size >>> 32));
		result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
		result = 31 * result + (displayCountOrSize != null ? displayCountOrSize.hashCode() : 0);
		result = 31 * result + (displayLastModifiedDate != null ? displayLastModifiedDate.hashCode() : 0);
		result = 31 * result + (fullRealName != null ? fullRealName.hashCode() : 0);
		result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
		result = 31 * result + (extension != null ? extension.hashCode() : 0);
		result = 31 * result + (selectable ? 1 : 0);
		return result;
	}

}
