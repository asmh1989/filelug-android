package com.filelug.android.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.StackingBehavior;
import com.filelug.android.R;
import com.filelug.android.ui.adapter.CustomMaterialSimpleListAdapter;
import com.filelug.android.ui.adapter.SectionRecyclerViewCursorAdapter;
import com.filelug.android.ui.model.LocalFile;
import com.filelug.android.ui.model.RemoteFile;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class DialogUtils {

	private static final String TAG = DialogUtils.class.getSimpleName();

	public static final int DIALOG_BUTTON_OK_RES = android.R.string.ok;
	public static final int DIALOG_BUTTON_CANCEL_RES = R.string.btn_label_cancel;
	public static final int DIALOG_BUTTON_CHOOSE_RES = R.string.btn_label_choose;

	public static MaterialDialog createInfoDialog(Context context, String message) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(R.string.title_info)
//			.iconRes(R.drawable.ic_information)
//			.limitIconToDefaultSize()
			.content(message)
			.positiveText(DIALOG_BUTTON_OK_RES);
		return builder.build();
	}

	public static MaterialDialog createInfoDialog(Context context, String message, MaterialDialog.SingleButtonCallback positiveButtonCallback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(R.string.title_info)
//			.iconRes(R.drawable.ic_information)
//			.limitIconToDefaultSize()
			.content(message)
			.positiveText(DIALOG_BUTTON_OK_RES)
			.onPositive(positiveButtonCallback)
			.cancelable(false);
		return builder.build();
	}

	public static MaterialDialog createWarningDialog(Context context, String message) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(R.string.title_warning)
//			.iconRes(R.drawable.ic_warning)
//			.limitIconToDefaultSize()
			.content(message)
			.positiveText(DIALOG_BUTTON_OK_RES);
		return builder.build();
	}

	public static MaterialDialog createWarningDialog(Context context, String message, MaterialDialog.SingleButtonCallback positiveButtonCallback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(R.string.title_warning)
//			.iconRes(R.drawable.ic_warning)
//			.limitIconToDefaultSize()
			.content(message)
			.positiveText(DIALOG_BUTTON_OK_RES)
			.onPositive(positiveButtonCallback)
			.cancelable(false);
		return builder.build();
	}

	public static MaterialDialog createErrorDialog(Context context, String message) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(R.string.title_error)
//			.iconRes(R.drawable.ic_error)
//			.limitIconToDefaultSize()
			.content(message)
			.positiveText(DIALOG_BUTTON_OK_RES);
		return builder.build();
	}

	public static MaterialDialog createErrorDialog(Context context, String message, MaterialDialog.SingleButtonCallback positiveButtonCallback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(R.string.title_error)
//			.iconRes(R.drawable.ic_error)
//			.limitIconToDefaultSize()
			.content(message)
			.positiveText(DIALOG_BUTTON_OK_RES)
			.onPositive(positiveButtonCallback)
			.cancelable(false);
		return builder.build();
	}

/*
	public static MaterialDialog createItemChooseDialog(Context context, int titleRes, int iconRes,  ListAdapter adapter, MaterialDialog.ListCallback callback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(titleRes)
			.iconRes(iconRes)
			.limitIconToDefaultSize()
			.adapter(adapter, callback);
		return builder.build();
	}

	public static MaterialDialog createItemChooseDialog(Context context, int titleRes, int iconRes,  ListAdapter adapter, MaterialDialog.ListCallback callback, DialogInterface.OnCancelListener cancelListener) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(titleRes)
			.iconRes(iconRes)
			.limitIconToDefaultSize()
			.adapter(adapter, callback)
			.cancelListener(cancelListener);
		return builder.build();
	}
*/

	public static MaterialDialog createSingleChoiceDialog(Context context, int titleRes, int iconRes, CharSequence[] items, MaterialDialog.ListCallback callback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(titleRes)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.items(items)
			.itemsCallback(callback);
		return builder.build();
	}

	public static MaterialDialog createSingleChoiceDialog(Context context, int titleRes, int iconRes, CharSequence[] items, int selectedIndex, MaterialDialog.ListCallbackSingleChoice callback) {
		return createSingleChoiceDialog(context, titleRes, iconRes, items, selectedIndex, false, -1, false, callback);
	}

	public static MaterialDialog createSingleChoiceDialog(Context context, int titleRes, int iconRes, CharSequence[] items, int selectedIndex, boolean showPromptCheckBox, int checkBoxTextRes, boolean isCheckBoxChecked, MaterialDialog.ListCallbackSingleChoice callback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(titleRes)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.items(items)
			.itemsCallbackSingleChoice(selectedIndex, callback)
			.positiveText(DIALOG_BUTTON_CHOOSE_RES);
		if (showPromptCheckBox) {
			builder.checkBoxPromptRes(checkBoxTextRes, isCheckBoxChecked, null);
		}
		return builder.build();
	}

	public static MaterialDialog createSingleChoiceDialog(Context context, int titleRes, int iconRes, CharSequence[] items, int selectedIndex, MaterialDialog.ListCallbackSingleChoice callback, DialogInterface.OnCancelListener cancelListener) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(titleRes)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.items(items)
			.itemsCallbackSingleChoice(selectedIndex, callback)
			.cancelListener(cancelListener)
			.positiveText(DIALOG_BUTTON_CHOOSE_RES);
		return builder.build();
	}

	public static MaterialDialog createSingleChoiceDialog(Context context, int titleRes, int iconRes, int positiveTextRes, int neutralTextRes, CharSequence[] items, int selectedIndex, MaterialDialog.SingleButtonCallback positiveButtonCallback, MaterialDialog.SingleButtonCallback neutralButtonCallback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(titleRes)
//			.iconRes(android.R.drawable.)
//			.limitIconToDefaultSize()
			.items(items)
			.itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
				@Override
				public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
					dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
					return true;
				}
			})
			.positiveText(positiveTextRes)
			.onPositive(positiveButtonCallback)
			.neutralText(neutralTextRes)
			.onNeutral(neutralButtonCallback)
			.autoDismiss(false)
			.alwaysCallSingleChoiceCallback();
		return builder.build();
	}

	public static MaterialDialog createSingleChoiceDialog(Context context, int titleRes, int iconRes, CustomMaterialSimpleListAdapter adapter) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
				.title(titleRes)
//				.iconRes(iconRes)
//				.limitIconToDefaultSize()
				.adapter(adapter, null);
		return builder.build();
	}

	public static MaterialDialog createMultiChoiceDialog(Context context, int titleRes, int iconRes, int positiveTextRes, int negativeTextRes, int neutralTextRes, RecyclerView.Adapter<?> adapter, MaterialDialog.ListCallback listCallback, MaterialDialog.SingleButtonCallback buttonCallback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(titleRes)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.positiveText(positiveTextRes)
			.negativeText(negativeTextRes)
			.neutralText(neutralTextRes)
			.adapter(adapter, null)
			.itemsCallback(listCallback)
			.onAny(buttonCallback)
			.autoDismiss(false);
		return builder.build();
	}

	public static MaterialDialog createListDialog(Context context, int titleRes, int iconRes, int positiveTextRes, SectionRecyclerViewCursorAdapter adapter, MaterialDialog.SingleButtonCallback buttonCallback, DialogInterface.OnShowListener showListener, DialogInterface.OnDismissListener dismissListener) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(titleRes)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.positiveText(positiveTextRes)
			.adapter(adapter, null)
			.onAny(buttonCallback)
			.showListener(showListener)
			.dismissListener(dismissListener)
			.cancelable(false)
			.autoDismiss(false);
		return builder.build();
	}

	public static MaterialDialog createStackedButtonsDialog(Context context, String title, int iconRes, String content, int positiveTextRes, int negativeTextRes, int neutralTextRes, MaterialDialog.SingleButtonCallback positiveButtonCallback, MaterialDialog.SingleButtonCallback negativeButtonCallback, MaterialDialog.SingleButtonCallback neutralButtonCallback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(title)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.content(content)
			.positiveText(positiveTextRes)
			.onPositive(positiveButtonCallback)
			.negativeText(negativeTextRes)
			.onNegative(negativeButtonCallback)
			.neutralText(neutralTextRes)
			.onNeutral(neutralButtonCallback)
			.stackingBehavior(StackingBehavior.ALWAYS);
		MaterialDialog dialog = builder.build();
		TextView contentView = dialog.getContentView();
		contentView.setText(Html.fromHtml(content));
		return dialog;
	}

	public static MaterialDialog createStackedButtonsDialog(Context context, int titleRes, int iconRes, String content, int positiveTextRes, int negativeTextRes, int neutralTextRes, MaterialDialog.SingleButtonCallback positiveButtonCallback, MaterialDialog.SingleButtonCallback negativeButtonCallback, MaterialDialog.SingleButtonCallback neutralButtonCallback, DialogInterface.OnCancelListener cancelListener) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(titleRes)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.content(content)
			.positiveText(positiveTextRes)
			.onPositive(positiveButtonCallback)
			.negativeText(negativeTextRes)
			.onNegative(negativeButtonCallback)
			.neutralText(neutralTextRes)
			.onNeutral(neutralButtonCallback)
			.cancelListener(cancelListener)
			.stackingBehavior(StackingBehavior.ALWAYS);
		MaterialDialog dialog = builder.build();
		TextView contentView = dialog.getContentView();
		contentView.setText(Html.fromHtml(content));
		return dialog;
	}

	public static MaterialDialog createStackedButtonsDialog2(Context context, String titleText, int iconRes, String content, int positiveTextRes, MaterialDialog.SingleButtonCallback positiveButtonCallback, int negativeTextRes, MaterialDialog.SingleButtonCallback negativeButtonCallback, int neutralTextRes, MaterialDialog.SingleButtonCallback neutralButtonCallback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(titleText)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.content(content)
			//.stackingBehavior(StackingBehavior.ALWAYS)
			;
		if ( positiveButtonCallback != null ) {
			builder.positiveText(positiveTextRes)
					.onPositive(positiveButtonCallback);
		}
		if ( negativeButtonCallback != null ) {
			builder.negativeText(negativeTextRes)
					.onNegative(negativeButtonCallback);
		}
		if ( neutralButtonCallback != null ) {
			builder.neutralText(neutralTextRes)
					.onNeutral(neutralButtonCallback);
		}
		MaterialDialog dialog = builder.build();
		TextView contentView = dialog.getContentView();
		contentView.setText(Html.fromHtml(content));
		return dialog;
	}

	public static MaterialDialog createButtonsDialog321(Context context, int titleRes, int iconRes, String content, int positiveTextRes, int negativeTextRes, int neutralTextRes, MaterialDialog.SingleButtonCallback positiveButtonCallback, MaterialDialog.SingleButtonCallback negativeButtonCallback, MaterialDialog.SingleButtonCallback neutralButtonCallback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(titleRes)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.content(content)
			.positiveText(positiveTextRes)
			.onPositive(positiveButtonCallback)
			.negativeText(negativeTextRes)
			.onNegative(negativeButtonCallback)
			.neutralText(neutralTextRes)
			.onNeutral(neutralButtonCallback);
		MaterialDialog dialog = builder.build();
//		TextView contentView = dialog.getContentView();
//		contentView.setText(Html.fromHtml(content));
		return dialog;
	}

	public static MaterialDialog createButtonsDialog31(Context context, int titleRes, int iconRes, int contentRes, int positiveTextRes, int neutralTextRes, MaterialDialog.SingleButtonCallback positiveButtonCallback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.content(contentRes)
			.positiveText(positiveTextRes)
			.neutralText(neutralTextRes)
			.onPositive(positiveButtonCallback);
		if ( titleRes >= 0 ) {
			builder.title(titleRes);
		}
		MaterialDialog dialog = builder.build();
		return dialog;
	}

	public static MaterialDialog createButtonsDialog31(Context context, int titleRes, int iconRes, String content, int positiveTextRes, int neutralTextRes, MaterialDialog.SingleButtonCallback positiveButtonCallback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.content(content)
			.positiveText(positiveTextRes)
			.neutralText(neutralTextRes)
			.onPositive(positiveButtonCallback);
		if ( titleRes >= 0 ) {
			builder.title(titleRes);
		}
		MaterialDialog dialog = builder.build();
		TextView contentView = dialog.getContentView();
		contentView.setText(Html.fromHtml(content));
		return dialog;
	}

	public static MaterialDialog createButtonsDialog31(Context context, String title, int iconRes, String content, int positiveTextRes, int neutralTextRes, MaterialDialog.SingleButtonCallback positiveButtonCallback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(title)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.content(content)
			.positiveText(positiveTextRes)
			.neutralText(neutralTextRes)
			.onPositive(positiveButtonCallback);
		MaterialDialog dialog = builder.build();
		TextView contentView = dialog.getContentView();
		contentView.setText(Html.fromHtml(content));
		return dialog;
	}

	public static MaterialDialog createButtonsDialog21(Context context, int titleRes, int iconRes, String content, int positiveTextRes, int negativeTextRes, MaterialDialog.SingleButtonCallback positiveButtonCallback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(titleRes)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.content(content)
			.positiveText(positiveTextRes)
			.negativeText(negativeTextRes)
			.onPositive(positiveButtonCallback);
		if ( titleRes >= 0 ) {
			builder.title(titleRes);
		}
		MaterialDialog dialog = builder.build();
		TextView contentView = dialog.getContentView();
		contentView.setText(Html.fromHtml(content));
		return dialog;
	}

	public static MaterialDialog createButtonsDialog21(Context context, int titleRes, int iconRes, int contentRes, int positiveTextRes, int negativeTextRes, MaterialDialog.SingleButtonCallback positiveButtonCallback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.content(contentRes)
			.positiveText(positiveTextRes)
			.negativeText(negativeTextRes)
			.onPositive(positiveButtonCallback);
		if ( titleRes >= 0 ) {
			builder.title(titleRes);
		}
		MaterialDialog dialog = builder.build();
		return dialog;
	}

	public interface EmailCallback {
		void validEmail(String email);
	}

	public static MaterialDialog createLocalFileObjectDetailDialog(Context context, LocalFile localFile) {
		LinkedHashMap<String, Object> info = new LinkedHashMap<String, Object>();
		info.put(context.getResources().getString(R.string.fileItem_display_name), localFile.getDisplayName());
		info.put(context.getResources().getString(R.string.fileItem_path), localFile.getParent());
		LocalFile.FileType type = localFile.getType();
		String typeStr = FormatUtils.formatLocalFileObjectType(context, type);
		info.put(context.getResources().getString(R.string.fileItem_object_type), typeStr);
		if ( localFile.isSymlink() ) {
			info.put(context.getResources().getString(R.string.fileItem_real_name), localFile.getRealName());
			info.put(context.getResources().getString(R.string.fileItem_real_path), localFile.getRealParent());
		}
		if ( type == LocalFile.FileType.LOCAL_FILE || type == LocalFile.FileType.LOCAL_SYMBOLIC_LINK_FILE ||
			 type == LocalFile.FileType.MEDIA_FILE || type == LocalFile.FileType.UNKNOWN ) {
			long fileSize = localFile.getSize();
			String fileSizeStr = FormatUtils.formatFileSize(context, fileSize);
			info.put(context.getResources().getString(R.string.fileItem_size), fileSizeStr);
		}
		Date lastModifiedDate = localFile.getLastModifiedDate();
		String lastModifiedDateStr = FormatUtils.formatDate2(context, lastModifiedDate);
		info.put(context.getResources().getString(R.string.fileItem_modified_date), lastModifiedDateStr);
		info.put(context.getResources().getString(R.string.fileItem_isHidden), FormatUtils.formatBooleanToYN(context, localFile.isHidden()));
		info.put(context.getResources().getString(R.string.fileItem_canRead), FormatUtils.formatBooleanToYN(context, localFile.isReadable()));
		info.put(context.getResources().getString(R.string.fileItem_canWrite), FormatUtils.formatBooleanToYN(context, localFile.isWritable()));

		LinkedHashMap<String, Map<String, Object>> details = new LinkedHashMap<String, Map<String, Object>>();
		details.put("", info);

		return DialogUtils.createDetailDialog(context, R.string.action_details, details);
	}

	public static MaterialDialog createDetailDialog(Context context, int titleRes, Map<String, Map<String, Object>> map) {
		MaterialDialog dialog = new MaterialDialog.Builder(context)
			.title(titleRes)
//			.iconRes(R.drawable.ic_information)
//			.limitIconToDefaultSize()
			.customView(R.layout.dialog_details, true)
			.positiveText(DIALOG_BUTTON_OK_RES).build();

		View view = dialog.getCustomView();
		LinearLayout detailLayout = (LinearLayout)view.findViewById(R.id.layout_detail_list);
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		for ( String section : map.keySet() ) {

			if ( !TextUtils.isEmpty(section) ) {
				View sectionView = vi.inflate(R.layout.rowitem_detail_section, null);
				TextView sectionText = (TextView)sectionView.findViewById(R.id.section_text);
				sectionText.setText(section);
				detailLayout.addView(sectionView, detailLayout.getChildCount());
			}

			Map<String, Object> details = map.get(section);

			View lastItemView = null;
			for ( String key : details.keySet() ) {
				Object value = details.get(key);
				View itemView = vi.inflate(R.layout.rowitem_detail_item, null);
				TextView labelText = (TextView)itemView.findViewById(R.id.label_text);
				TextView descriptionText = (TextView)itemView.findViewById(R.id.description_text);
				labelText.setText(key);
				descriptionText.setText(value.toString());
				detailLayout.addView(itemView, detailLayout.getChildCount());
				lastItemView = itemView;
			}
			if ( lastItemView != null ) {
				View dividerLine = lastItemView.findViewById(R.id.divider_line);
				dividerLine.setVisibility(View.GONE);
			}

		}

		return dialog;
	}

	public interface SortSettingCallback {
		public void selected(int sortBy, int sortType);
	}

	public static MaterialDialog createSortSettingDialog(Context context, int sortBy, int sortType, final SortSettingCallback callback) {

		final ArrayList<String> sortByList = new ArrayList<String>();
		sortByList.add(context.getResources().getString(R.string.sort_by_name));
		sortByList.add(context.getResources().getString(R.string.sort_by_date));
		sortByList.add(context.getResources().getString(R.string.sort_by_size));
		sortByList.add(context.getResources().getString(R.string.sort_by_type));

		MaterialDialog.SingleButtonCallback positiveButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				if ( !callComplete(dialog, 1, callback) ) {
					return;
				}
				dialog.dismiss();
			}
		};
		MaterialDialog.SingleButtonCallback negativeButtonCallback = new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				if ( !callComplete(dialog, 0, callback) ) {
					return;
				}
				dialog.dismiss();
			}
		};

		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(R.string.sortDialog_title)
//			.iconRes(R.drawable.ic_action_sort)
//			.limitIconToDefaultSize()
			.items(sortByList.toArray(new String[0]))
			.itemsCallbackSingleChoice(sortBy, new MaterialDialog.ListCallbackSingleChoice() {
				@Override
				public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
					return true;
				}
			})
			.positiveText(R.string.sort_type_descending)
			.onPositive(positiveButtonCallback)
			.negativeText(R.string.sort_type_ascending)
			.onNegative(negativeButtonCallback)
				.autoDismiss(false)
				.alwaysCallSingleChoiceCallback();

		if ( context.getResources().getInteger(R.integer.const_sort_type_ascending) == sortType ) { // ascending
			builder.negativeColor(context.getResources().getColor(R.color.main_color_600));
		} else if ( context.getResources().getInteger(R.integer.const_sort_type_descending) == sortType ) { // descending
			builder.positiveColor(context.getResources().getColor(R.color.main_color_600));
		}

		MaterialDialog dialog = builder.build();

		return dialog;
	}

	private static boolean callComplete(MaterialDialog dialog, int selectedSortTypeIndex, SortSettingCallback callback) {
		int selectedIndex = dialog.getSelectedIndex();
		if (selectedIndex < 0) {
			return false;
		}
		if (callback != null) {
			callback.selected(selectedIndex, selectedSortTypeIndex);
		}
		return true;
	}

	public static MaterialDialog createRemoteFileObjectDetailDialog(Context context, RemoteFile remoteFile) {
		LinkedHashMap<String, Object> info = new LinkedHashMap<String, Object>();
		info.put(context.getResources().getString(R.string.fileItem_display_name), remoteFile.getDisplayName());
		info.put(context.getResources().getString(R.string.fileItem_path), remoteFile.getParent());
		RemoteFile.FileType type = remoteFile.getType();
		String typeStr = FormatUtils.formatRemoteFileObjectType(context, type);
		info.put(context.getResources().getString(R.string.fileItem_object_type), typeStr);
		if ( remoteFile.isSymlink() ) {
			info.put(context.getResources().getString(R.string.fileItem_real_name), remoteFile.getRealName());
			info.put(context.getResources().getString(R.string.fileItem_real_path), remoteFile.getRealParent());
		}
		if ( type == RemoteFile.FileType.REMOTE_FILE || type == RemoteFile.FileType.REMOTE_WINDOWS_SHORTCUT_FILE ||
			 type == RemoteFile.FileType.REMOTE_UNIX_SYMBOLIC_LINK_FILE || type == RemoteFile.FileType.REMOTE_MAC_ALIAS_FILE ||
			 type == RemoteFile.FileType.UNKNOWN ) {
			long fileSize = remoteFile.getSize();
			String fileSizeStr = FormatUtils.formatFileSize(context, fileSize);
			info.put(context.getResources().getString(R.string.fileItem_size), fileSizeStr);
		}
		info.put(context.getResources().getString(R.string.fileItem_modified_date), remoteFile.getLastModified());
		info.put(context.getResources().getString(R.string.fileItem_isHidden), FormatUtils.formatBooleanToYN(context, remoteFile.isHidden()));
		info.put(context.getResources().getString(R.string.fileItem_canRead), FormatUtils.formatBooleanToYN(context, remoteFile.isReadable()));
		info.put(context.getResources().getString(R.string.fileItem_canWrite), FormatUtils.formatBooleanToYN(context, remoteFile.isWritable()));

		LinkedHashMap<String, Map<String, Object>> details = new LinkedHashMap<String, Map<String, Object>>();
		details.put("", info);

		return DialogUtils.createDetailDialog(context, R.string.action_details, details);
	}

	public static MaterialDialog createTextInputDialog(Context context, int titleRes, int hintRes, String prefill, int inputType, boolean autoDismiss, InputFilter[] filters, MaterialDialog.SingleButtonCallback positiveButtonCallback) {
		String hintStr = context.getResources().getString(hintRes);
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(titleRes)
//			.iconRes(R.drawable.ic_action_sort)
//			.limitIconToDefaultSize()
			.inputType(inputType)
			.positiveText(android.R.string.ok)
			.onPositive(positiveButtonCallback)
			.autoDismiss(autoDismiss)
			.alwaysCallInputCallback() // this forces the callback to be invoked with every input change
			.input(hintStr, prefill, false, new MaterialDialog.InputCallback() {
				@Override
				public void onInput(MaterialDialog dialog, CharSequence input) {
					if (TextUtils.isEmpty(input.toString())) {
						dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
					} else {
						dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
					}
				}
			});

		MaterialDialog dialog = builder.build();
		if ( filters != null ) {
			dialog.getInputEditText().setFilters(filters);
		}

		return dialog;
	}

	public static MaterialDialog createFolderDialog(Context context, String currentPath, RecyclerView.Adapter<?> adapter, MaterialDialog.SingleButtonCallback positiveButtonCallback, MaterialDialog.SingleButtonCallback neutralButtonCallback) {
		return createFolderDialog(context, currentPath, false, -1, false, adapter, positiveButtonCallback, neutralButtonCallback);
	}

	public static MaterialDialog createFolderDialog(Context context, String currentPath, boolean showPromptCheckBox, int checkBoxTextRes, boolean isCheckBoxChecked, RecyclerView.Adapter<?> adapter, MaterialDialog.SingleButtonCallback positiveButtonCallback, MaterialDialog.SingleButtonCallback neutralButtonCallback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(currentPath)
			.limitIconToDefaultSize()
			.adapter(adapter, null)
			.onPositive(positiveButtonCallback)
			.onNeutral(neutralButtonCallback)
			.autoDismiss(false)
			.positiveText(R.string.btn_label_choose)
			.neutralText(R.string.btn_label_reload);
		if (showPromptCheckBox) {
			builder.checkBoxPromptRes(checkBoxTextRes, isCheckBoxChecked, null);
		}
		MaterialDialog dialog = builder.build();

		return dialog;
	}

	public static MaterialDialog createSaveDefaultValueDialog(Context context, int titleRes, int iconRes, String content, int positiveTextRes, int neutralTextRes, MaterialDialog.SingleButtonCallback buttonCallback) {
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
			.title(titleRes)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.content(content)
			.positiveText(positiveTextRes)
			.negativeText(neutralTextRes)
			.onAny(buttonCallback);
		MaterialDialog dialog = builder.build();
		TextView contentView = dialog.getContentView();
		contentView.setText(Html.fromHtml(content));
		return dialog;
	}

	public static MaterialDialog createPermissionsDialog(Context context, int titleRes, int iconRes, int positiveTextRes, int neutralTextRes, MaterialDialog.SingleButtonCallback positiveButtonCallback, MaterialDialog.SingleButtonCallback neutralButtonCallback) {
		MaterialDialog dialog = new MaterialDialog.Builder(context)
			.title(titleRes)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.customView(R.layout.layout_current_permissions, true)
			.positiveText(positiveTextRes)
			.neutralText(neutralTextRes)
			.onPositive(positiveButtonCallback)
			.onNeutral(neutralButtonCallback)
			.cancelable(false)
			.autoDismiss(false)
			.build();
		return dialog;
	}

	public static MaterialDialog createPermissionsSetupTodoDialog(Context context, int titleRes, int iconRes, int positiveTextRes, MaterialDialog.SingleButtonCallback buttonCallback) {
		MaterialDialog dialog = new MaterialDialog.Builder(context)
			.title(titleRes)
//			.iconRes(iconRes)
//			.limitIconToDefaultSize()
			.customView(R.layout.layout_permission_setup_description, true)
			.positiveText(positiveTextRes)
			.onPositive(buttonCallback)
			.cancelable(false)
			.autoDismiss(false)
			.build();
		return dialog;
	}

	public static MaterialDialog createProgressDialog(Context context, String title, String message) {
		NumberFormat numberFormat = new NumberFormat() {
			@Override
			public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {
				return buffer;
			}
			@Override
			public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
				return buffer;
			}
			@Override
			public Number parse(String string, ParsePosition position) {
				return null;
			}
		};
		int reqConnectTimeout = context.getResources().getInteger(R.integer.req_connect_timeout) / 1000;
		String timeoutFormat = context.getResources().getString(R.string.format_req_connect_timeout);
		MaterialDialog dialog = new MaterialDialog.Builder(context)
			.title(title)
			.content(message)
			.cancelable(false)
			.autoDismiss(false)
			.progress(false, reqConnectTimeout, true)
			.progressNumberFormat(timeoutFormat)
			.progressPercentFormat(numberFormat)
			.showListener(new DialogInterface.OnShowListener() {
				@Override
				public void onShow(DialogInterface dialogInterface) {
					final MaterialDialog dialog = (MaterialDialog) dialogInterface;
					new Thread(new Runnable() {
						@Override
						public void run() {
							while (dialog.getCurrentProgress() != dialog.getMaxProgress() &&
									!Thread.currentThread().isInterrupted()) {
								if (dialog.isCancelled())
									break;
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									break;
								}
								dialog.incrementProgress(1);
							}
						}
					}).start();
				}
			})
			.build();
		return dialog;
	}

	public static MaterialDialog createProgressDialog2(Context context, String message) {
		MaterialDialog dialog = new MaterialDialog.Builder(context)
			.content(message)
			.cancelable(false)
			.autoDismiss(false)
			.progress(true, 0)
			.progressIndeterminateStyle(false)
			.build();
		return dialog;
	}

}
