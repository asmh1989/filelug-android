package com.filelug.android.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.filelug.android.Constants;
import com.filelug.android.R;
import com.filelug.android.ui.fragment.BaseFragment;
import com.filelug.android.ui.fragment.BrowseLocalDirectoryFragment;
import com.filelug.android.ui.fragment.BrowseRemoteDirectoryFragment;
import com.filelug.android.ui.fragment.TransferFragment;
import com.filelug.android.ui.model.DrawerItem;
import com.filelug.android.ui.widget.ScrimInsetsScrollView;
import com.filelug.android.util.AccountUtils;
import com.filelug.android.util.LocalFileUtils;
import com.filelug.android.util.MiscUtils;
import com.filelug.android.util.MsgUtils;
import com.filelug.android.util.NotificationUtils;
import com.filelug.android.util.PrefUtils;
import com.filelug.android.util.RemoteFileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	//	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final int ACCOUNT_BOX_EXPAND_ANIM_DURATION = 200;

	private BroadcastReceiver mDesktopConnStatusBroadcastReceiver;
	private ViewGroup mDrawerItemsListContainer;
	private ViewGroup mDrawerAccountItemsListContainer;
	private ImageView mExpandAccountBoxIndicator;
	private List<DrawerItem> mDrawerItems;
	private LinearLayout mDrawerList;
	private LinearLayout mDrawerCurrentAccountList;
	private List<DrawerItem> mDrawerCurrentAccountItems;
	private LinearLayout mDrawerOtherAccountList;
	private List<DrawerItem> mDrawerOtherAccountItems;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private DrawerLayout mDrawerLayout;
	private Toolbar mActionBarToolbar;
	private BaseFragment mFragment = null;
	private ScrimInsetsScrollView mNavDrawer;
	private ActionBarDrawerToggle mDrawerToggle;

	private boolean mAccountBoxExpanded = false;
	private int mItemSelected = DrawerItem.DRAWER_ITEM_BROWSE_LOCAL_DIRECTORY;
	private boolean mDoubleBackToExit = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if (Constants.DEBUG) Log.d(TAG, "onCreate()");
		mDesktopConnStatusBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
//				if (Constants.DEBUG) Log.d(TAG, "mDesktopConnStatusBroadcastReceiver.onReceive()");
				int status = intent.getIntExtra(Constants.PARAM_STATUS, -1);
				if ( status > -1 ) {
					loginOrConnectStatusChanged(status);
				}
			}
		};

		setContentView(R.layout.activity_main);

		initNavDrawer();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
//		if (Constants.DEBUG) Log.d(TAG, "onPostCreate()");
		mDrawerToggle.syncState();
		selectItem(mItemSelected, null);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		if (Constants.DEBUG) Log.d(TAG, "onResume()");
		LocalBroadcastManager.getInstance(this).registerReceiver(mDesktopConnStatusBroadcastReceiver,
				new IntentFilter(Constants.LOCAL_BROADCAST_DESKTOP_CONNECTION_STATUS));
	}

	@Override
	protected void onPause() {
//		if (Constants.DEBUG) Log.d(TAG, "onPause()");
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mDesktopConnStatusBroadcastReceiver);
		super.onPause();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
//		if (Constants.DEBUG) Log.d(TAG, "onNewIntent()");

		Bundle extras = intent.getExtras();
		if (extras == null) {
//			if (Constants.DEBUG) Log.d(TAG, "onNewIntent(), extras=null");
			return;
		}

		if ( extras.containsKey(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE) ) {
			int notificationId = extras.getInt(Constants.EXT_PARAM_NOTIFICATION_ID, -1);
			if ( notificationId != -1l ) {
				NotificationUtils.removeNotification(this, notificationId);
			}

			int type = extras.getInt(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, -1);
			long rowId = extras.getLong(Constants.EXT_PARAM_ROW_ID, -1l);

			if ( type >= Constants.NOTIFICATION_CALLBACK_TYPE_UPLOAD_PING_ERROR && type <= Constants.NOTIFICATION_CALLBACK_TYPE_GCM_RESUME_UPLOAD_FILE ) {
				Bundle bundle = new Bundle();
				bundle.putInt(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, type);
				bundle.putLong(Constants.EXT_PARAM_ROW_ID, rowId);
				selectItem(DrawerItem.DRAWER_ITEM_UPLOAD_FILE, bundle);
			} else if ( type >= Constants.NOTIFICATION_CALLBACK_TYPE_DOWNLOAD_PING_ERROR && type <= Constants.NOTIFICATION_CALLBACK_TYPE_GCM_RESUME_DOWNLOAD_FILE ) {
				Bundle bundle = new Bundle();
				bundle.putInt(Constants.EXT_PARAM_NOTIFICATION_CALLBACK_TYPE, type);
				bundle.putLong(Constants.EXT_PARAM_ROW_ID, rowId);
				selectItem(DrawerItem.DRAWER_ITEM_DOWNLOAD_FILE, bundle);
			} else if ( type == Constants.NOTIFICATION_CALLBACK_TYPE_OPEN_DOWNLOADED_FILE ) {
				if ( rowId > 0 ) {
					MiscUtils.openFile(this, rowId);
				}
			} else if ( type == Constants.NOTIFICATION_CALLBACK_TYPE_STOP_DOWNLOAD_FILE ) {
				if ( rowId > 0 ) {
					RemoteFileUtils.stopDownloadTask(this, rowId);
				}
			} else if ( type == Constants.NOTIFICATION_CALLBACK_TYPE_STOP_UPLOAD_FILE ) {
				if ( rowId > 0 ) {
					LocalFileUtils.stopUploadTask(this, rowId);
				}
			}
		}
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		getActionBarToolbar();
	}

	protected Toolbar getActionBarToolbar() {
		if (mActionBarToolbar == null) {
			mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
			if (mActionBarToolbar != null) {
				setSupportActionBar(mActionBarToolbar);
			}
		}
		return mActionBarToolbar;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// selectItem ==>
	// initList ==>
	private void selectItem(int drawerTag, String itemTitle, Bundle bundle) {
		BaseFragment fragment = getFragmentByDrawerTag(drawerTag, itemTitle, bundle);
		if ( fragment != null ) {
			commitFragment(fragment);
			mItemSelected = drawerTag;
			setTitle(itemTitle);
			mFragment = fragment;
		}
		noticeLoginOrConnectStatusChanged(Constants.MESSAGE_SELECT_ITEM_CHANGED);
		mDrawerLayout.closeDrawer(mNavDrawer);
	}

	// onPostCreate ==>
	// onNewIntent( type >= Constants.NOTIFICATION_CALLBACK_TYPE_UPLOAD_PING_ERROR && type <= Constants.NOTIFICATION_CALLBACK_TYPE_GCM_RESUME_UPLOAD_FILE ) ==>
	// onNewIntent( type >= Constants.NOTIFICATION_CALLBACK_TYPE_DOWNLOAD_PING_ERROR && type <= Constants.NOTIFICATION_CALLBACK_TYPE_GCM_RESUME_DOWNLOAD_FILE ) ==>
	// onActivityResult( requestCode == Constants.REQUEST_UPLOAD_FILES ) ==>
	// onActivityResult( requestCode == Constants.REQUEST_DOWNLOAD_FILES ) ==>
	// selectBrowseLocalDirItem
	// selectBrowseRemoteDirItem
	private void selectItem(int drawerTag, Bundle bundle) {
		String itemTitle = getDrawerItemTitle(drawerTag);
		selectItem(drawerTag, itemTitle, bundle);
	}

	// mDesktopConnStatusBroadcastReceiver.onReceive ==>
	// checkActiveAccount ==>
	// selectItem ==>
	private void reformatAllItems(int status) {
		AccountManager accountManager = AccountManager.get(MainActivity.this);
		Account mActiveAccount = AccountUtils.getActiveAccount();
		boolean loggedIn = false;
		boolean socketConnected = false;
		String computerName = null;
		if ( mActiveAccount != null ) {
			loggedIn = AccountUtils.isLoggedIn(mActiveAccount);
			socketConnected = AccountUtils.isSocketConnected(mActiveAccount);
			computerName = accountManager.getUserData(mActiveAccount, Constants.PARAM_COMPUTER_NAME);
		}

		boolean isServiceRunning = MiscUtils.isUploadOrDownloadOrNotificationServiceRunning();
		isServiceRunning = status == Constants.MESSAGE_FILELUG_SERVICE_DESTROY ? false : isServiceRunning;

//		if (Constants.DEBUG) Log.d(TAG, "reformatAllItems(), status=" + status + ", activeAccount=" + mActiveAccount + ", loggedIn=" + loggedIn + ", socketConnected=" + socketConnected + ", computerName=" + computerName + ", isServiceRunning=" + isServiceRunning);

		LinearLayout[] containers = new LinearLayout[] { mDrawerList, mDrawerCurrentAccountList, mDrawerOtherAccountList };
		for ( LinearLayout container : containers ) {
			for ( int i=0; i<container.getChildCount(); i++ ) {
				View view = container.getChildAt(i);
				TextView tagView = (TextView) view.findViewById(R.id.tag);
				boolean isSelect = false;
				boolean isEnable = true;
				boolean isNeedHighlight = false;
				String loginTitleText = null;
				int tag = Integer.parseInt((String) tagView.getText());
				if ( tag == DrawerItem.DRAWER_ITEM_SECTION_HEADER ) {
					continue;
				} else if ( isServiceRunning && tag >= DrawerItem.DRAWER_ACCOUNT_ITEM_CURRENT_ACCOUNT) {
					isEnable = false;
				} else if ( tag == mItemSelected ) {
					isSelect = true;
				} else {
					switch (tag) {
						case DrawerItem.DRAWER_ITEM_DOWNLOAD_FILE:
						case DrawerItem.DRAWER_ITEM_UPLOAD_FILE:
						case DrawerItem.DRAWER_ITEM_BROWSE_REMOTE_DIRECTORY:
							//if ( !loggedIn || !socketConnected ) isEnable = false;
							if ( !loggedIn ) isEnable = false;
							break;
//						case DrawerItem.DRAWER_ITEM_DOWNLOADED_FILES_HISTORY:
//						case DrawerItem.DRAWER_ITEM_UPLOADED_FILES_HISTORY:
//							if ( !loggedIn ) isEnable = false;
//							break;
						case DrawerItem.DRAWER_ACCOUNT_ITEM_MANAGE_CURRENT_ACCOUNT:
							if ( !loggedIn ) isEnable = false;
							break;
						case DrawerItem.DRAWER_ACCOUNT_ITEM_MANAGE_CURRENT_COMPUTER:
							if ( !loggedIn || TextUtils.isEmpty(computerName) ) isEnable = false;
							break;
						case DrawerItem.DRAWER_ACCOUNT_ITEM_CURRENT_COMPUTER:
							if ( !loggedIn ) {
								isEnable = false;
							} else if ( !socketConnected ) {
								isNeedHighlight = true;
							}
							break;
						case DrawerItem.DRAWER_ACCOUNT_ITEM_CURRENT_ACCOUNT:
							if ( !loggedIn ) {
								isNeedHighlight = true;
								loginTitleText = getResources().getString(R.string.drawer_account_title_login);
							} else {
								loginTitleText = getResources().getString(R.string.drawer_account_title_current_account);
							}
							break;
						default:
							break;
					}
				}
				view.setEnabled(isEnable);
				formatNavDrawerItem(view, isSelect, isEnable, isNeedHighlight, loginTitleText);
			}
		}
	}

	private void formatNavDrawerItem(View view, boolean selected, boolean enabled, boolean needHighlight, String loginTitleText) {
		int textRes = enabled ? ( selected ? R.color.main_color_500 : R.color.main_color_grey_700 ) : R.color.main_color_grey_400;
		textRes = needHighlight ? R.color.material_red_600 : textRes;
		ImageView iconView = (ImageView) view.findViewById(R.id.icon);
		if ( iconView != null ) {
			int iconRes = enabled ? R.color.main_color_500 : R.color.main_color_grey_400;
			iconView.setColorFilter(getResources().getColor(iconRes));
		}
		TextView titleView = (TextView) view.findViewById(R.id.title);
		titleView.setTextColor(getResources().getColor(textRes));
		if ( loginTitleText != null ) {
			titleView.setText(loginTitleText);
		}
	}

	private BaseFragment getFragmentByDrawerTag(int drawerTag, String itemTitle, Bundle bundle) {
		BaseFragment fragment = null;
		if (drawerTag == DrawerItem.DRAWER_ITEM_DOWNLOAD_FILE) {
			fragment = new TransferFragment();
			Bundle args = ( bundle != null ? (Bundle)bundle.clone() : new Bundle() );
			args.putInt(Constants.EXT_PARAM_TRANSFER_TYPE, Constants.TRANSFER_TYPE_DOWNLOAD);
			fragment.setArguments(args);
//		} else if (drawerTag == DrawerItem.DRAWER_ITEM_DOWNLOADED_FILES_HISTORY) {
//			fragment = new HistoryFragment();
//			Bundle args = new Bundle();
//			args.putInt(Constants.EXT_PARAM_HISTORY_TYPE, HistoryFragment.HISTORY_TYPE_DOWNLOAD);
//			fragment.setArguments(args);
		} else if (drawerTag == DrawerItem.DRAWER_ITEM_UPLOAD_FILE) {
			fragment = new TransferFragment();
			Bundle args = ( bundle != null ? (Bundle)bundle.clone() : new Bundle() );
			args.putInt(Constants.EXT_PARAM_TRANSFER_TYPE, Constants.TRANSFER_TYPE_UPLOAD);
			fragment.setArguments(args);
//		} else if (drawerTag == DrawerItem.DRAWER_ITEM_UPLOADED_FILES_HISTORY) {
//			fragment = new HistoryFragment();
//			Bundle args = new Bundle();
//			args.putInt(Constants.EXT_PARAM_HISTORY_TYPE, HistoryFragment.HISTORY_TYPE_UPLOAD);
//			fragment.setArguments(args);
		} else if (drawerTag == DrawerItem.DRAWER_ITEM_BROWSE_REMOTE_DIRECTORY) {
			fragment = new BrowseRemoteDirectoryFragment();
		} else if (drawerTag == DrawerItem.DRAWER_ITEM_BROWSE_LOCAL_DIRECTORY) {
			fragment = new BrowseLocalDirectoryFragment();
		} else if (drawerTag == DrawerItem.DRAWER_ITEM_SETTINGS) {
			openSettings();
		} else if (drawerTag == DrawerItem.DRAWER_ACCOUNT_ITEM_CURRENT_ACCOUNT) {
			doCurrentAccount();
		} else if (drawerTag == DrawerItem.DRAWER_ACCOUNT_ITEM_CURRENT_COMPUTER) {
			doCurrentComputer();
		} else if (drawerTag == DrawerItem.DRAWER_ACCOUNT_ITEM_MANAGE_CURRENT_ACCOUNT) {
			manageCurrentAccount();
		} else if (drawerTag == DrawerItem.DRAWER_ACCOUNT_ITEM_MANAGE_CURRENT_COMPUTER) {
			manageCurrentComputer();
		} else if (drawerTag == DrawerItem.DRAWER_ACCOUNT_ITEM_LOGIN_TO_OTHER) {
			doLoginToOtherAccount(itemTitle);
		} else {
			fragment = new BaseFragment();
		}
		return fragment;
	}

	private void openSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivityForResult(intent, Constants.REQUEST_SETTINGS);
	}

	// getFragmentByDrawerTag ==> drawerTag == DrawerItem.DRAWER_ACCOUNT_ITEM_CHANGE_PHONE_NUMBER ==>
	private void doCurrentAccount() {
		Intent intent = new Intent(this, ChangeAccountActivity.class);

		Account mActiveAccount = AccountUtils.getActiveAccount();
		if ( mActiveAccount != null ) {
			String accountName = mActiveAccount.name;
			boolean loggedIn = AccountUtils.isLoggedIn(mActiveAccount);
			intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
			intent.putExtra(Constants.EXT_PARAM_LOGGED_IN, loggedIn);
		}

		startActivityForResult(intent, Constants.REQUEST_CHANGE_ACCOUNT);
	}

	// getFragmentByDrawerTag ==> drawerTag == DrawerItem.DRAWER_ACCOUNT_ITEM_CHANGE_COMPUTER ==>
	private void doCurrentComputer() {
		Account mActiveAccount = AccountUtils.getActiveAccount();
		if ( mActiveAccount != null ) {
			AccountManager mAccountManager = AccountManager.get(MainActivity.this);
			String tmpComputerId = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_COMPUTER_ID);
			int computerId = TextUtils.isEmpty(tmpComputerId) ? -1 : Integer.valueOf(tmpComputerId);
			String computerName = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_COMPUTER_NAME);
			String tmpSocketConnected = mAccountManager.getUserData(mActiveAccount, Constants.PARAM_SOCKET_CONNECTED);
			boolean socketConnected = tmpSocketConnected == null ? false : Boolean.valueOf(tmpSocketConnected);

			Intent intent = new Intent(this, ChangeComputerActivity.class);
			intent.putExtra(Constants.PARAM_COMPUTER_ID, computerId);
			intent.putExtra(Constants.PARAM_COMPUTER_NAME, computerName);
			intent.putExtra(Constants.PARAM_SOCKET_CONNECTED, socketConnected);
			startActivityForResult(intent, Constants.REQUEST_CHANGE_COMPUTER);
		}
	}

	// getFragmentByDrawerTag ==> drawerTag == DrawerItem.DRAWER_ACCOUNT_ITEM_MANAGE_CURRENT_ACCOUNT ==>
	private void manageCurrentAccount() {
		Intent intent = new Intent(this, ManageCurrentAccountActivity.class);
		startActivityForResult(intent, Constants.REQUEST_MANAGE_CURRENT_ACCOUNT);
	}

	// getFragmentByDrawerTag ==> drawerTag == DrawerItem.DRAWER_ACCOUNT_ITEM_MANAGE_CURRENT_COMPUTER ==>
	private void manageCurrentComputer() {
		Intent intent = new Intent(this, ManageCurrentComputerActivity.class);
		startActivityForResult(intent, Constants.REQUEST_MANAGE_CURRENT_COMPUTER);
	}

	// getFragmentByDrawerTag ==> drawerTag == DrawerItem.DRAWER_ACCOUNT_ITEM_LOGIN_TO_OTHER ==>
	private void doLoginToOtherAccount(final String accountName) {
		final Account account = AccountUtils.getAccount(accountName);
		if ( account != null ) {
			PrefUtils.setActiveAccount(accountName);
			accountLogin(account, true);
		} else {
			String msg = String.format(getResources().getString(R.string.message_account_has_been_deleted), accountName);
			MsgUtils.showToast(MainActivity.this, msg);
			AccountManagerCallback callback = new AccountManagerCallback<Boolean>() {
				public void run(AccountManagerFuture<Boolean> future) {
					try {
						boolean result = false;
						Object o = future.getResult();
						if ( o instanceof Bundle ) {
							result = ((Bundle)o).getBoolean("booleanResult");
						} else if ( o instanceof Boolean ) {
							result = ((Boolean)o).booleanValue();
						}
						if ( result ) {
							PrefUtils.cleanActiveInfo(accountName);
							noticeLoginOrConnectStatusChanged(Constants.MESSAGE_ACCOUNT_HAS_BEEN_DELETED);
						}
					} catch (OperationCanceledException e) {
					} catch (IOException e) {
					} catch (AuthenticatorException e) {
					} catch (Exception e) {
					}
				}
			};
			AccountUtils.removeAccount(MainActivity.this, account, callback);
		}
	}

	private class CommitFragmentRunnable implements Runnable {

		private Fragment fragment;

		public CommitFragmentRunnable(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void run() {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment)
				.commit();
		}
	}

	public void commitFragment(Fragment fragment) {
		//Using Handler class to avoid lagging while
		//committing fragment in same time as closing
		//navigation drawer
		mHandler.post(new CommitFragmentRunnable(fragment));
	}

	@Override
	public void setTitle(int titleId) {
		setTitle(getString(titleId));
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	private int getFragmentAfterLoggedIn() {
		int tag = DrawerItem.DRAWER_ITEM_BROWSE_LOCAL_DIRECTORY;

		Account mActiveAccount = AccountUtils.getActiveAccount();
		boolean loggedIn = false;
		boolean socketConnected = false;
		if ( mActiveAccount != null ) {
			loggedIn = AccountUtils.isLoggedIn(mActiveAccount);
			socketConnected = AccountUtils.isSocketConnected(mActiveAccount);
		}

		if ( mItemSelected == DrawerItem.DRAWER_ITEM_DOWNLOAD_FILE ||
			 mItemSelected == DrawerItem.DRAWER_ITEM_UPLOAD_FILE ||
			 mItemSelected == DrawerItem.DRAWER_ITEM_BROWSE_REMOTE_DIRECTORY ) {
//			if ( socketConnected ) tag = mItemSelected;
			if ( loggedIn ) tag = mItemSelected;
		}
//		else if ( mItemSelected == DrawerItem.DRAWER_ITEM_DOWNLOADED_FILES_HISTORY ||
//			 mItemSelected == DrawerItem.DRAWER_ITEM_UPLOADED_FILES_HISTORY ) {
//			if ( loggedIn ) tag = mItemSelected;
//		}

		return tag;
	}

	// onCreate ==>
	private void initNavDrawer() {
		mDrawerItemsListContainer = (ViewGroup) findViewById(R.id.drawer_menu_container);
		mDrawerAccountItemsListContainer = (ViewGroup) findViewById(R.id.drawer_account_menu_container);
		initAccountHeader();
		initDrawerMainFunctionList();
		initDrawerCurrentAccountList();
		initDrawerOtherAccountList();
		initDrawerLayout();
	}

	protected void initUIObjects(int status) {
		initDrawerItemState(status);
		reformatAllItems(status);
	}

	@Override
	public void loginOrConnectStatusChanged(int status) {
//		if (Constants.DEBUG) Log.d(TAG, "loginOrConnectStatusChanged(), status=" + status);

//		int oldIndex = mItemSelected;
//		int newIndex = getFragmentAfterLoggedIn();
//		if (Constants.DEBUG) Log.d(TAG, "loginOrConnectStatusChanged(), oldIndex=" + oldIndex + ", newIndex=" + newIndex);
//		if ( oldIndex != newIndex ) {
//			selectItem(newIndex, null);
//			return;
//		}

		initUIObjects(status);

		refreshFragment(status, getFragment());


//		BaseFragment fragment = getFragment();
//		if ( ( fragment instanceof BrowseRemoteDirectoryFragment ) && PrefUtils.isReloadRemoteRootDir() ) {
//			if (Constants.DEBUG) Log.d(TAG, "loginOrConnectStatusChanged(): Force to refresh!");
//			((BrowseRemoteDirectoryFragment)fragment).doRefreshAction();
//		}
	}

	private void refreshFragment(int status, BaseFragment fragment) {
		if ( fragment instanceof BrowseLocalDirectoryFragment ) {
			return;
		}

//		if (Constants.DEBUG) Log.d(TAG, "refreshFragment(), status=" + status + ", fragment=" + fragment.getClass().getName());

		switch (status) {
			case Constants.MESSAGE_LOGGED_IN_AND_SOCKET_CONNECTED:
			case Constants.MESSAGE_COMPUTER_CHANGED_AND_SOCKET_CONNECTED:
			case Constants.MESSAGE_LOGGED_IN_BUT_COMPUTER_NOT_CONNECTED:
			case Constants.MESSAGE_LOGGED_IN_BUT_CONNECTION_FAILED:
			case Constants.MESSAGE_COMPUTER_CHANGED_BUT_NOT_CONNECTED:
			case Constants.MESSAGE_COMPUTER_CHANGE_ERROR:
				if ( fragment instanceof BrowseRemoteDirectoryFragment ) {
					PrefUtils.setReloadRemoteRootDir(true);
					((BrowseRemoteDirectoryFragment)fragment).doRefreshAction();
//				} else if ( fragment instanceof TransferFragment ) {
//					((TransferFragment)fragment).doRefreshAction();
				}
				break;
			case Constants.MESSAGE_LOGIN_FAILED:
			case Constants.MESSAGE_LOGGED_IN_BUT_COMPUTER_NOT_SET:
			case Constants.MESSAGE_FIND_AVAILABLE_COMPUTERS_GET_AUTH_TOKEN_ERROR:
			case Constants.MESSAGE_LOGGED_IN_BUT_COMPUTER_LIST_NOT_FOUND:
			case Constants.MESSAGE_CHANGE_COMPUTER_GET_AUTH_TOKEN_ERROR:
			case Constants.MESSAGE_ACCOUNT_DELETED:
			case Constants.MESSAGE_COMPUTER_DELETED:
			case Constants.MESSAGE_ACCOUNT_HAS_BEEN_DELETED:
				selectBrowseLocalDirItem();
				break;
			default:
				break;
		}
	}

	private void initAccountHeader() {
		final View chosenAccountView = findViewById(R.id.accountLayout);

		TextView tvAccountInfo = (TextView) chosenAccountView.findViewById(R.id.accountInfo);
		tvAccountInfo.setText("(" + getResources().getString(R.string.message_not_set) + ")");

		mExpandAccountBoxIndicator = (ImageView) findViewById(R.id.expand_account_box_indicator);
		chosenAccountView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mAccountBoxExpanded = !mAccountBoxExpanded;
				initAccountBoxToggle();
			}
		});

		initAccountBoxToggle();
	}

	private void initAccountBoxToggle() {
		mExpandAccountBoxIndicator.setImageResource(mAccountBoxExpanded
			? R.drawable.drawer_accounts_collapse
			: R.drawable.drawer_accounts_expand);
		int hideTranslateY = -mDrawerAccountItemsListContainer.getHeight() / 4; // last 25% of animation
		if (mAccountBoxExpanded && mDrawerAccountItemsListContainer.getTranslationY() == 0) {
			// initial setup
			mDrawerAccountItemsListContainer.setAlpha(0);
			mDrawerAccountItemsListContainer.setTranslationY(hideTranslateY);
		}

		AnimatorSet set = new AnimatorSet();
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mDrawerItemsListContainer.setVisibility(mAccountBoxExpanded ? View.GONE : View.VISIBLE);
				mDrawerAccountItemsListContainer.setVisibility(mAccountBoxExpanded ? View.VISIBLE : View.GONE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				onAnimationEnd(animation);
			}
		});

		if (mAccountBoxExpanded) {
			mDrawerAccountItemsListContainer.setVisibility(View.VISIBLE);
			AnimatorSet subSet = new AnimatorSet();
			subSet.playTogether(
				ObjectAnimator
					.ofFloat(mDrawerAccountItemsListContainer, View.ALPHA, 1)
					.setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION),
				ObjectAnimator
					.ofFloat(mDrawerAccountItemsListContainer, View.TRANSLATION_Y, 0)
					.setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION)
			);
			set.playSequentially(
				ObjectAnimator
					.ofFloat(mDrawerItemsListContainer, View.ALPHA, 0)
					.setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION),
				subSet);
		} else {
			mDrawerItemsListContainer.setVisibility(View.VISIBLE);
			AnimatorSet subSet = new AnimatorSet();
			subSet.playTogether(
				ObjectAnimator
					.ofFloat(mDrawerAccountItemsListContainer, View.ALPHA, 0)
					.setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION),
				ObjectAnimator
					.ofFloat(mDrawerAccountItemsListContainer, View.TRANSLATION_Y, hideTranslateY)
					.setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION)
			);
			set.playSequentially(
				subSet,
				ObjectAnimator
					.ofFloat(mDrawerItemsListContainer, View.ALPHA, 1)
					.setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION)
			);
		}

		set.start();
	}

	private void initDrawerMainFunctionList() {
		mDrawerList = (LinearLayout) findViewById(R.id.drawer_list);
		mDrawerItems = prepareNavDrawerMainItems();
		initList(mDrawerList, mDrawerItems);
	}

	private List<DrawerItem> prepareNavDrawerMainItems() {
		List<DrawerItem> items = new ArrayList<DrawerItem>();

		items.add(
			new DrawerItem( getResources().getString(R.string.drawer_section_main_task) ) );
		items.add(
			new DrawerItem(
				R.drawable.menu_ic_download_file,
				getResources().getString(R.string.drawer_title_download_file),
				DrawerItem.DRAWER_ITEM_DOWNLOAD_FILE
			)
		);
		items.add(
			new DrawerItem(
				R.drawable.menu_ic_upload_file,
				getResources().getString(R.string.drawer_title_upload_file),
				DrawerItem.DRAWER_ITEM_UPLOAD_FILE
			)
		);
		items.add(
			new DrawerItem(
				R.drawable.menu_ic_remote_root,
				getResources().getString(R.string.drawer_title_browse_remote_root_folder),
				DrawerItem.DRAWER_ITEM_BROWSE_REMOTE_DIRECTORY
			)
		);
		items.add(
			new DrawerItem(
				R.drawable.menu_ic_local_root,
				getResources().getString(R.string.drawer_title_browse_local_folder),
				DrawerItem.DRAWER_ITEM_BROWSE_LOCAL_DIRECTORY
			)
		);
		items.add(
			new DrawerItem( getResources().getString(R.string.drawer_section_others) ) );
		items.add(
			new DrawerItem(
				R.drawable.menu_ic_settings,
				getResources().getString(R.string.drawer_title_settings),
				DrawerItem.DRAWER_ITEM_SETTINGS
			)
		);

		return items;
	}

	private void initDrawerCurrentAccountList() {
		mDrawerCurrentAccountList = (LinearLayout) findViewById(R.id.drawer_current_account_list);
		mDrawerCurrentAccountItems = prepareNavDrawerCurrentAccountItems();
		initList(mDrawerCurrentAccountList, mDrawerCurrentAccountItems);
	}

	private List<DrawerItem> prepareNavDrawerCurrentAccountItems() {
		String msgNotSet = "("+getResources().getString(R.string.message_not_set)+")";
		List<DrawerItem> items = new ArrayList<DrawerItem>();

		items.add(
			new DrawerItem(
				getResources().getString(R.string.drawer_account_section_account_and_computer)
			)
		);
		items.add(
			new DrawerItem(
				R.drawable.ic_human,
				getResources().getString(R.string.drawer_account_title_current_account),
				msgNotSet,
				DrawerItem.DRAWER_ACCOUNT_ITEM_CURRENT_ACCOUNT
			)
		);
		items.add(
			new DrawerItem(
				R.drawable.menu_ic_computer,
				getResources().getString(R.string.drawer_account_title_current_computer),
				msgNotSet,
				DrawerItem.DRAWER_ACCOUNT_ITEM_CURRENT_COMPUTER
			)
		);
		items.add(
			new DrawerItem(
				R.drawable.menu_ic_manage_current_account,
				getResources().getString(R.string.drawer_account_title_manage_current_account),
				DrawerItem.DRAWER_ACCOUNT_ITEM_MANAGE_CURRENT_ACCOUNT
			)
		);
		items.add(
			new DrawerItem(
				R.drawable.menu_ic_manage_current_computer,
				getResources().getString(R.string.drawer_account_title_manage_current_computer),
				DrawerItem.DRAWER_ACCOUNT_ITEM_MANAGE_CURRENT_COMPUTER
			)
		);

		return items;
	}

	private void initDrawerOtherAccountList() {
		mDrawerOtherAccountList = (LinearLayout) findViewById(R.id.drawer_other_account_list);
		mDrawerOtherAccountItems = prepareNavDrawerOtherAccountItems();
		initList(mDrawerOtherAccountList, mDrawerOtherAccountItems);
	}

	private List<DrawerItem> prepareNavDrawerOtherAccountItems() {
		return new ArrayList<DrawerItem>();
	}

	private void initList(final LinearLayout drawerList, List<DrawerItem> items) {
		drawerList.removeAllViews();
		for ( int i=0; i<items.size(); i++ ) {
			DrawerItem item = items.get(i);
			final int itemTag = item.getTag();
			final String itemTitle = item.getTitle();
			View view = null;
			if ( itemTag == DrawerItem.DRAWER_ITEM_SECTION_HEADER ) {
				view = getLayoutInflater().inflate(R.layout.rowitem_navdrawer_section, drawerList, false);
				TextView titleView = (TextView) view.findViewById(R.id.title);
				titleView.setText(itemTitle);
				TextView tagView = (TextView) view.findViewById(R.id.tag);
				tagView.setText(Integer.toString(itemTag));
			} else if ( itemTag == DrawerItem.DRAWER_ACCOUNT_ITEM_CURRENT_ACCOUNT ||
					itemTag == DrawerItem.DRAWER_ACCOUNT_ITEM_CURRENT_COMPUTER ) {
				view = getLayoutInflater().inflate(R.layout.rowitem_navdrawer_item_with_subtitle, drawerList, false);
				ImageView iconView = (ImageView) view.findViewById(R.id.icon);
				iconView.setImageResource(item.getIcon());
				TextView titleView = (TextView) view.findViewById(R.id.title);
				titleView.setText(itemTitle);
				final String itemSubtitle = item.getSubTitle();
				TextView subTitleView = (TextView) view.findViewById(R.id.subTitle);
				subTitleView.setText(itemSubtitle);
				TextView tagView = (TextView) view.findViewById(R.id.tag);
				tagView.setText(Integer.toString(itemTag));
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						selectItem(itemTag, itemTitle, null);
					}
				});
			} else {
				view = getLayoutInflater().inflate(R.layout.rowitem_navdrawer_item, drawerList, false);
				ImageView iconView = (ImageView) view.findViewById(R.id.icon);
				iconView.setImageResource(item.getIcon());
				TextView titleView = (TextView) view.findViewById(R.id.title);
				titleView.setText(itemTitle);
				TextView tagView = (TextView) view.findViewById(R.id.tag);
				tagView.setText(Integer.toString(itemTag));
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						selectItem(itemTag, itemTitle, null);
					}
				});
			}
			drawerList.addView(view);
		}
	}

	private void initDrawerItemState(int status) {
		Account activeAccount = AccountUtils.getActiveAccount();
		boolean loggedIn = false;
		boolean socketConnected = false;
		if ( activeAccount != null ) {
			loggedIn = AccountUtils.isLoggedIn(activeAccount);
			socketConnected = AccountUtils.isSocketConnected(activeAccount);
		}
		initAccountDrawerItemState(status, activeAccount, loggedIn, socketConnected);
	}

	private void initAccountDrawerItemState(int status, Account activeAccount, boolean loggedIn, boolean socketConnected) {
//		if (Constants.DEBUG) Log.d(TAG, "initAccountDrawerItemState(), status=" + status + ", activeAccount=" + activeAccount + ", loggedIn=" + loggedIn + ", socketConnected=" + socketConnected);

		final View chosenAccountView = findViewById(R.id.accountLayout);
		// Header
		TextView tvAccountInfo = (TextView) chosenAccountView.findViewById(R.id.accountInfo);
		ImageView ivConnStatus = (ImageView) chosenAccountView.findViewById(R.id.img_status_desktop_connection);

		String headerText;
		String accountName = null;
		String accountNameStr;
		String computerName;
		String computerNameStr;
		String nicknameStr;
		String email;
		String emailStr;
		String msgNotSet = "("+getResources().getString(R.string.message_not_set)+")";
		int connStatusRes = -1;

		AccountManager accountManager = AccountManager.get(MainActivity.this);

		if ( activeAccount != null ) {
			accountName = activeAccount.name;
			headerText = accountName;
			nicknameStr = accountManager.getUserData(activeAccount, Constants.PARAM_NICKNAME);
			computerName = accountManager.getUserData(activeAccount, Constants.PARAM_COMPUTER_NAME);
			if ( !loggedIn ) {
				String msgNotLoggedIn = " ("+getResources().getString(R.string.message_computer_not_logged_in)+")";
				accountNameStr = accountName + msgNotLoggedIn;
				computerNameStr = computerName;
				connStatusRes = R.drawable.img_status_desktop_conn_not_login;
			} else {
				if ( socketConnected ) {
					accountNameStr = accountName;
					computerNameStr = computerName;
					connStatusRes = R.drawable.img_status_desktop_conn_socket_connected;
				} else {
					String msgNotConnected = " ("+getResources().getString(R.string.message_computer_not_connected)+")";
					accountNameStr = accountName;
					if ( computerName == null ) {
						computerNameStr = " ("+getResources().getString(R.string.message_not_set)+")";
					} else {
						computerNameStr = computerName + msgNotConnected;
					}
					connStatusRes = R.drawable.img_status_desktop_conn_logged_in;
				}
			}
			String msgEmailNotVerified = " ("+getResources().getString(R.string.message_email_not_verified)+")";
			email = accountManager.getUserData(activeAccount, Constants.PARAM_EMAIL);
			String tmp = accountManager.getUserData(activeAccount, Constants.PARAM_EMAIL_IS_VERIFIED);
			boolean emailIsVerified = tmp == null ? false : Boolean.valueOf(tmp);
			if ( TextUtils.isEmpty(email) ) {
				emailStr = msgNotSet;
			} else {
				if ( emailIsVerified ) {
					emailStr = email;
				} else {
					emailStr = email + msgEmailNotVerified;
				}
			}
			ivConnStatus.setVisibility(View.VISIBLE);
			ivConnStatus.setImageResource(connStatusRes);
		} else {
			headerText = msgNotSet;
			accountNameStr = msgNotSet;
			nicknameStr = msgNotSet;
			computerNameStr = msgNotSet;
			emailStr = msgNotSet;
			ivConnStatus.setVisibility(View.INVISIBLE);
		}

//		if (Constants.DEBUG) Log.d(TAG, "initAccountDrawerItemState(), headerText=" + headerText + ", accountNameStr=" + accountNameStr + ", computerNameStr=" + computerNameStr);

		// Header
		tvAccountInfo.setText(Html.fromHtml(headerText), TextView.BufferType.SPANNABLE);

		// Current Account
		for ( int i=0; i<mDrawerCurrentAccountList.getChildCount(); i++ ) {
			View view = mDrawerCurrentAccountList.getChildAt(i);
			TextView subTitleView = (TextView) view.findViewById(R.id.subTitle);
			TextView tagView = (TextView) view.findViewById(R.id.tag);
			int tag = Integer.parseInt((String) tagView.getText());
			if ( tag == DrawerItem.DRAWER_ACCOUNT_ITEM_CURRENT_ACCOUNT) {
				subTitleView.setText(accountNameStr);
			} else if ( tag == DrawerItem.DRAWER_ACCOUNT_ITEM_CURRENT_COMPUTER ) {
				subTitleView.setText(computerNameStr);
			}
		}

		mDrawerOtherAccountItems.clear();
		Account[] accounts = AccountUtils.getFilelugAccounts();
		if ( !( accounts == null || accounts.length == 0 || ( accounts.length == 1 && accounts[0].name.equals(accountName) ) ) ) {
			mDrawerOtherAccountItems.add(new DrawerItem(getResources().getString(R.string.drawer_account_section_connect_to_others)));
			for ( Account account : accounts ) {
				if ( !account.name.equals(accountName) ) {
					mDrawerOtherAccountItems.add( new DrawerItem( R.drawable.ic_human, account.name, DrawerItem.DRAWER_ACCOUNT_ITEM_LOGIN_TO_OTHER) );
				}
			}
		}
		mDrawerOtherAccountList = (LinearLayout) findViewById(R.id.drawer_other_account_list);
		initList(mDrawerOtherAccountList, mDrawerOtherAccountItems);
	}

	private void initDrawerLayout() {
		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.main_color_700));
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mActionBarToolbar, R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				if ( mFragment != null ) {
					mFragment.setMenuVisibility(true);
				}
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
				if ( mAccountBoxExpanded ) {
					mAccountBoxExpanded = false;
					initAccountBoxToggle();
				}
				mNavDrawer.scrollTo(0, 0);
			}
			public void onDrawerOpened(View drawerView) {
				if ( mFragment != null ) {
					mFragment.setMenuVisibility(false);
				}
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mNavDrawer = (ScrimInsetsScrollView)findViewById(R.id.navdrawer);
	}

	private DrawerItem getDrawerItem(int tag) {
		DrawerItem drawerItem = null;
		for ( DrawerItem item : mDrawerItems ) {
			if ( tag == item.getTag() ) {
				drawerItem = item;
				break;
			}
		}
		return drawerItem;
	}

	private String getDrawerItemTitle(int tag) {
		String title = null;
		DrawerItem item = getDrawerItem(tag);
		if ( item != null ) {
			title = item.getTitle();
		}
		return title;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ( resultCode == RESULT_OK ) {
			Bundle extras = data.getExtras();
			String message = null;
			if ( requestCode == Constants.REQUEST_CHANGE_ACCOUNT ) {
				if ( extras != null ) {
					String oldAccountName = extras.getString(Constants.EXT_PARAM_OLD_ACCOUNT, null);
					String newAccountName = extras.getString(Constants.EXT_PARAM_NEW_ACCOUNT);
					PrefUtils.setActiveAccount(newAccountName);
					if ( !TextUtils.isEmpty(newAccountName) ) {
						Account newAccount = AccountUtils.getAccount(newAccountName);
						accountLogin(newAccount, true);
					}
				}
			} else if ( requestCode == Constants.REQUEST_CHANGE_COMPUTER ) {
				if ( extras != null ) {
					int oldComputerId = extras.getInt(Constants.EXT_PARAM_OLD_COMPUTER_ID, -1);
					String oldComputerName = extras.getString(Constants.EXT_PARAM_OLD_COMPUTER_NAME, null);
					int newComputerId = extras.getInt(Constants.EXT_PARAM_NEW_COMPUTER_ID);
					String newComputerName = extras.getString(Constants.EXT_PARAM_NEW_COMPUTER_NAME);

					Account activeAccount = AccountUtils.getActiveAccount();
					AccountManager accountManager = AccountManager.get(MainActivity.this);
					accountManager.setUserData(activeAccount, Constants.PARAM_COMPUTER_ID, Integer.toString(newComputerId));
					accountManager.setUserData(activeAccount, Constants.PARAM_COMPUTER_NAME, newComputerName);
					accountManager.setUserData(activeAccount, Constants.PARAM_SOCKET_CONNECTED, Boolean.FALSE.toString());

//					BaseFragment fragment = getFragment();
//					if ( fragment instanceof BrowseRemoteDirectoryFragment ) {
//						PrefUtils.setReloadRemoteRootDir(true);
//					}

					beforeDoComputerChanged(activeAccount, newComputerId, newComputerName);
				}
			} else if ( requestCode == Constants.REQUEST_SETTINGS ||
						requestCode == Constants.REQUEST_MANAGE_CURRENT_ACCOUNT ||
						requestCode == Constants.REQUEST_MANAGE_CURRENT_COMPUTER ) {
				if (extras != null) {
					HashMap<String, Object> mChangedMap = (HashMap<String, Object>)extras.get(Constants.EXT_PARAM_CHANGED_PREFERENCES);
					if ( mChangedMap.containsKey(getResources().getString(R.string.pref_show_hidden_files)) ||
						 mChangedMap.containsKey(getResources().getString(R.string.pref_show_local_system_folder)) ) {
						BaseFragment fragment = getFragment();
						if ( fragment instanceof BrowseLocalDirectoryFragment ) {
							((BrowseLocalDirectoryFragment)fragment).doRefreshAction();
						} else if ( fragment instanceof BrowseRemoteDirectoryFragment ) {
							((BrowseRemoteDirectoryFragment)fragment).doRefreshAction();
						}
					} else if ( mChangedMap.containsKey(getResources().getString(R.string.pref_computer_name))  ) {
						noticeLoginOrConnectStatusChanged(Constants.MESSAGE_COMPUTER_NAME_CHANGED);
					} else if ( mChangedMap.containsKey(getResources().getString(R.string.pref_email)) ) {
						noticeLoginOrConnectStatusChanged(Constants.MESSAGE_EMAIL_CHANGED);
					} else if ( mChangedMap.containsKey(getResources().getString(R.string.pref_nickname)) ) {
						noticeLoginOrConnectStatusChanged(Constants.MESSAGE_NICKNAME_CHANGED);
					} else if ( mChangedMap.containsKey(getResources().getString(R.string.pref_delete_account)) ) {
						noticeLoginOrConnectStatusChanged(Constants.MESSAGE_ACCOUNT_DELETED);
					} else if ( mChangedMap.containsKey(getResources().getString(R.string.pref_delete_computer)) ) {
						noticeLoginOrConnectStatusChanged(Constants.MESSAGE_COMPUTER_DELETED);
					}
				}
			} else if ( requestCode == Constants.REQUEST_UPLOAD_FILES ) {
				selectItem(DrawerItem.DRAWER_ITEM_UPLOAD_FILE, null);
			} else if ( requestCode == Constants.REQUEST_DOWNLOAD_FILES ) {
				selectItem(DrawerItem.DRAWER_ITEM_DOWNLOAD_FILE, null);
			}
		}

	}

	private void setSubtitleText(int tag, String subTitleText) {
		for ( int i=0; i<mDrawerCurrentAccountList.getChildCount(); i++ ) {
			View view = mDrawerCurrentAccountList.getChildAt(i);
			TextView tagView = (TextView) view.findViewById(R.id.tag);
			String tmpTag = Integer.toString(tag);
			if ( tmpTag.equals(tagView.getText()) ) {
				TextView subTitleView = (TextView) view.findViewById(R.id.subTitle);
				subTitleView.setText(subTitleText);
				break;
			}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		if ( mFragment != null && mFragment.backToParent() ) {
			return;
		} else if ( mDoubleBackToExit ) {
			super.onBackPressed();
			return;
		}

		mDoubleBackToExit = true;
		MsgUtils.showToast(this, R.string.message_press_back_again_to_exit, Toast.LENGTH_SHORT);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mDoubleBackToExit = false;
			}
		}, 2000);
	}

	public BaseFragment getFragment() {
		return mFragment;
	}

	public void selectBrowseLocalDirItem() {
		selectItem(DrawerItem.DRAWER_ITEM_BROWSE_LOCAL_DIRECTORY, null);
	}

	public void selectBrowseRemoteDirItem() {
		selectItem(DrawerItem.DRAWER_ITEM_BROWSE_REMOTE_DIRECTORY, null);
	}

}
