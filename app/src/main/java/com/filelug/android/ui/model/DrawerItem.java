package com.filelug.android.ui.model;

public class DrawerItem {

	/* Commented tags are expected in future updates.
	 */
	public static final int DRAWER_ITEM_SECTION_HEADER = 1;

	public static final int DRAWER_ITEM_DOWNLOAD_FILE = 10;
//	public static final int DRAWER_ITEM_DOWNLOADED_FILES_HISTORY = 11;
	public static final int DRAWER_ITEM_UPLOAD_FILE = 12;
//	public static final int DRAWER_ITEM_UPLOADED_FILES_HISTORY = 13;
	public static final int DRAWER_ITEM_BROWSE_REMOTE_DIRECTORY = 14;
	public static final int DRAWER_ITEM_BOOKMARK = 15;
	public static final int DRAWER_ITEM_BROWSE_LOCAL_DIRECTORY = 16;
	public static final int DRAWER_ITEM_SETTINGS = 17;

	public static final int DRAWER_ACCOUNT_ITEM_CURRENT_ACCOUNT = 100;
	public static final int DRAWER_ACCOUNT_ITEM_CURRENT_COMPUTER = 101;
	public static final int DRAWER_ACCOUNT_ITEM_MANAGE_CURRENT_ACCOUNT = 102;
	public static final int DRAWER_ACCOUNT_ITEM_MANAGE_CURRENT_COMPUTER = 103;
	public static final int DRAWER_ACCOUNT_ITEM_LOGIN_TO_OTHER = 200;

	private int icon;
//	private int title;
//	private int subTitle;
	private String title;
	private String subTitle;
	private int tag;

/*
	public DrawerItem(int title) {
		this.icon = icon;
		this.title = title;
		this.tag = DRAWER_ITEM_SECTION_HEADER;
	}

	public DrawerItem(int icon, int title, int tag) {
		this.icon = icon;
		this.title = title;
		this.tag = tag;
	}

	public DrawerItem(int icon, int title, int subTitle, int tag) {
		this.icon = icon;
		this.title = title;
		this.subTitle = subTitle;
		this.tag = tag;
	}
*/

	public DrawerItem(String title) {
		this.icon = icon;
		this.title = title;
		this.tag = DRAWER_ITEM_SECTION_HEADER;
	}

	public DrawerItem(int icon, String title, int tag) {
		this.icon = icon;
		this.title = title;
		this.tag = tag;
	}

	public DrawerItem(int icon, String title, String subTitle, int tag) {
		this.icon = icon;
		this.title = title;
		this.subTitle = subTitle;
		this.tag = tag;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

/*
	public int getTitle() {
		return title;
	}

	public void setTitle(int title) {
		this.title = title;
	}

	public int getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(int subTitle) {
		this.subTitle = subTitle;
	}
*/

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

}
