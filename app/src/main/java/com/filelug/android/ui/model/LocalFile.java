package com.filelug.android.ui.model;

import android.net.Uri;

import java.util.Date;

public interface LocalFile {

	// 本機裝置-資料夾
	public static enum FileType {

		ROOT(1),
		SYS_DIR_PICTURES(101),
		SYS_DIR_MUSIC(102),
		SYS_DIR_MOVIES(103),
		SYS_DIR_DOCUMENTS(104),
		SYS_DIR_DOWNLOADS(105),
		SYS_DIR_INTERNAL_STORAGE(106),
		SYS_DIR_SD_CARD_STORAGE(107),
		SYS_DIR_USB_STORAGE(108),
		SYS_DIR_DEVICE_ROOT(109),
		LOCAL_DIR(201),
		LOCAL_SYMBOLIC_LINK_DIR(202),
		MEDIA_DIR(203),
		LOCAL_FILE(301),
		LOCAL_SYMBOLIC_LINK_FILE(302),
		MEDIA_FILE(303),
		UNKNOWN(9999);

		private final int index;

		FileType(int idx) {
			index = idx;
		}

		public int getIndex() {
			return index;
		}

		public int getSortIndex() {
			if ( index >= LOCAL_FILE.getIndex() )
				return LOCAL_FILE.getIndex();
			else if ( index >= LOCAL_DIR.getIndex() )
				return LOCAL_DIR.getIndex();
			else
				return index;
		}

		public boolean isSystemDirectory() {
			return index <= SYS_DIR_DEVICE_ROOT.getIndex();
		}

		public boolean isDirectory() {
			return index <= MEDIA_DIR.getIndex();
		}

		public boolean isFile() {
			return index >= LOCAL_FILE.getIndex();
		}

	};

	public static enum MediaType {
		NONE,
		IMAGE,
		AUDIO,
		VIDEO,
		DOCUMENT,
		DOWNLOAD
	};

	// 從 java.io.File 物件取得的屬性

	public boolean isSymlink();

	public String getName();

	public String getParent();

	public boolean isReadable();

	public boolean isWritable();

	public boolean isHidden();

	public Date getLastModifiedDate();

	public FileType getType();

	public String getContentType();

	public String getRealName();

	public String getRealParent();

	public Uri getUri();

	public String getCacheFileName();

	public long getSize();

	public String getDisplayName();

	public String getDisplayCountOrSize();

	public String getDisplayLastModifiedDate();

	public String getFullName();

	public String getExtension();

	public int getFileCount();

	public MediaType getMediaType();

	// 從 android.provider.BaseColumns._ID 取得的屬性

	public long getMediaId();

	public long getMediaParentId();

}
