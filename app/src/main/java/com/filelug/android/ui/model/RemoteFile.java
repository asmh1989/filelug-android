package com.filelug.android.ui.model;

/**
 * Created by Vincent Chang on 2015/7/31.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public interface RemoteFile {

	// 遠端裝置-資料夾
	public static enum FileType {

		ROOT(1),
		REMOTE_ROOT_USER_HOME(101),
		REMOTE_ROOT_LOCAL_DISK(102),
		REMOTE_ROOT_DVD_PLAYER(103),
		REMOTE_ROOT_NETWORK_DISK(104),
		REMOTE_ROOT_EXTERNAL_DISK(105),
		REMOTE_ROOT_TIME_MACHINE(106),
		REMOTE_ROOT_DIRECTORY(121),
		REMOTE_ROOT_WINDOWS_SHORTCUT_DIRECTORY(122),
		REMOTE_ROOT_UNIX_SYMBOLIC_LINK_DIRECTORY(123),
		REMOTE_ROOT_MAC_ALIAS_DIRECTORY(124),
		REMOTE_DIR(201),
		REMOTE_WINDOWS_SHORTCUT_DIR(202),
		REMOTE_UNIX_SYMBOLIC_LINK_DIR(203),
		REMOTE_MAC_ALIAS_DIR(204),
		REMOTE_FILE(301),
		REMOTE_WINDOWS_SHORTCUT_FILE(302),
		REMOTE_UNIX_SYMBOLIC_LINK_FILE(303),
		REMOTE_MAC_ALIAS_FILE(304),
		UNKNOWN(9999);

		private final int index;

		FileType(int idx) {
			index = idx;
		}

		public int getIndex() {
			return index;
		}

		public int getSortIndex() {
			if ( index >= REMOTE_FILE.getIndex() )
				return REMOTE_FILE.getIndex();
			else if ( index >= REMOTE_DIR.getIndex() )
				return REMOTE_DIR.getIndex();
			else
				return index;
		}

		public boolean isSystemDirectory() {
			return index < REMOTE_DIR.getIndex();
		}

		public boolean isDirectory() {
			return index <= REMOTE_MAC_ALIAS_DIR.getIndex();
		}

		public boolean isFile() {
			return index >= REMOTE_FILE.getIndex();
		}

	};

	// 從 Repository 取得 Desktop 檔案物件的屬性
	public boolean isSymlink();

	public String getName();

	public String getParent();

	public boolean isReadable();

	public boolean isWritable();

	public boolean isHidden();

	public String getLastModified();

	public FileType getType();

	public String getContentType();

	public String getRealName();

	public String getRealParent();

	public long getSize();

	public String getDisplayName();

	public String getDisplayCountOrSize();

	public String getDisplayLastModifiedDate();

	public String getFullName();

	public String getFullRealName();

	public String getExtension();

	public boolean isSelectable();

}
