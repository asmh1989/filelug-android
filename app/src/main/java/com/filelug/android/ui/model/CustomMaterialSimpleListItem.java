package com.filelug.android.ui.model;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import com.afollestad.materialdialogs.util.DialogUtils;

// Copy from MaterialDialogs v.0.9.0.2, com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem
public class CustomMaterialSimpleListItem {

	private Builder mBuilder;

	//Change here!
	//private MaterialSimpleListItem(Builder builder) {
	private CustomMaterialSimpleListItem(Builder builder) {
		mBuilder = builder;
	}

	public Drawable getIcon() {
		return mBuilder.mIcon;
	}

	public CharSequence getContent() {
		return mBuilder.mContent;
	}

	public int getIconPadding() {
		return mBuilder.mIconPadding;
	}

	@ColorInt
	public int getBackgroundColor() {
		return mBuilder.mBackgroundColor;
	}

	public static class Builder {

		private Context mContext;
		protected Drawable mIcon;
		protected CharSequence mContent;
		protected int mIconPadding;
		protected int mBackgroundColor;

		public Builder(Context context) {
			mContext = context;
			mBackgroundColor = Color.parseColor("#BCBCBC");
		}

		public Builder icon(Drawable icon) {
			this.mIcon = icon;
			return this;
		}

		public Builder icon(@DrawableRes int iconRes) {
			return icon(ContextCompat.getDrawable(mContext, iconRes));
		}

		public Builder iconPadding(@IntRange(from = 0, to = Integer.MAX_VALUE) int padding) {
			this.mIconPadding = padding;
			return this;
		}

		public Builder iconPaddingDp(@IntRange(from = 0, to = Integer.MAX_VALUE) int paddingDp) {
			this.mIconPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDp,
					mContext.getResources().getDisplayMetrics());
			return this;
		}

		public Builder iconPaddingRes(@DimenRes int paddingRes) {
			return iconPadding(mContext.getResources().getDimensionPixelSize(paddingRes));
		}

		public Builder content(CharSequence content) {
			this.mContent = content;
			return this;
		}

		public Builder content(@StringRes int contentRes) {
			return content(mContext.getString(contentRes));
		}

		public Builder backgroundColor(@ColorInt int color) {
			this.mBackgroundColor = color;
			return this;
		}

		public Builder backgroundColorRes(@ColorRes int colorRes) {
			return backgroundColor(DialogUtils.getColor(mContext, colorRes));
		}

		public Builder backgroundColorAttr(@AttrRes int colorAttr) {
			return backgroundColor(DialogUtils.resolveColor(mContext, colorAttr));
		}

		//Change here!
		//public MaterialSimpleListItem build() {
		public CustomMaterialSimpleListItem build() {
			//	return new MaterialSimpleListItem(this);
			return new CustomMaterialSimpleListItem(this);
		}

	}

	@Override
	public String toString() {
		if (getContent() != null)
			return getContent().toString();
		else
			return "(no content)";
	}

}
