package com.filelug.android.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.filelug.android.R;

/**
 * Created on 2016/1/10.
 * Copy from com.csform.android.uiapptemplate.font.RobotoTextView (md-ui-template-app-2.0)
 */
public class RobotoTextView extends AppCompatTextView {

	public RobotoTextView(Context context) {
		super(context);
		if (isInEditMode()) return;
		parseAttributes(null);
	}

	public RobotoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) return;
		parseAttributes(attrs);
	}

	public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (isInEditMode()) return;
		parseAttributes(attrs);
	}
	
	private void parseAttributes(AttributeSet attrs) {
		int typeface;
		if (attrs == null) { //Not created from xml
			typeface = Roboto.ROBOTO_REGULAR;
		} else {
		    TypedArray values = getContext().obtainStyledAttributes(attrs, R.styleable.RobotoTextView);
		    typeface = values.getInt(R.styleable.RobotoTextView_typeface, Roboto.ROBOTO_REGULAR);
		    values.recycle();
		}
	    setTypeface(getRoboto(typeface));
	}
	
	public void setRobotoTypeface(int typeface) {
	    setTypeface(getRoboto(typeface));
	}
	
	private Typeface getRoboto(int typeface) {
		return getRoboto(getContext(), typeface);
	}
	
	public static Typeface getRoboto(Context context, int typeface) {
		switch (typeface) {
		case Roboto.ROBOTO_BLACK:
			if (Roboto.sRobotoBlack == null) {
				Roboto.sRobotoBlack = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Black.ttf");
			}
			return Roboto.sRobotoBlack;
		case Roboto.ROBOTO_BOLD:
			if (Roboto.sRobotoBold == null) {
				Roboto.sRobotoBold = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
			}
			return Roboto.sRobotoBold;
		case Roboto.ROBOTO_LIGHT:
			if (Roboto.sRobotoLight == null) {
				Roboto.sRobotoLight = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
			}
			return Roboto.sRobotoLight;
		case Roboto.ROBOTO_MEDIUM:
			if (Roboto.sRobotoMedium == null) {
				Roboto.sRobotoMedium = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
			}
			return Roboto.sRobotoMedium;
		case Roboto.ROBOTO_THIN:
			if (Roboto.sRobotoThin == null) {
				Roboto.sRobotoThin = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
			}
			return Roboto.sRobotoThin;
		default:
		case Roboto.ROBOTO_REGULAR:
			if (Roboto.sRobotoRegular == null) {
				Roboto.sRobotoRegular = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
			}
			return Roboto.sRobotoRegular;
		}
	}
	
	public static class Roboto {
		public static final int ROBOTO_REGULAR = 0;
		public static final int ROBOTO_BOLD = 1;
		public static final int ROBOTO_MEDIUM = 2;
		public static final int ROBOTO_THIN = 3;
		public static final int ROBOTO_BLACK = 4;
		public static final int ROBOTO_LIGHT = 5;

		private static Typeface sRobotoRegular;
		private static Typeface sRobotoBold;
		private static Typeface sRobotoMedium;
		private static Typeface sRobotoThin;
		private static Typeface sRobotoBlack;
		private static Typeface sRobotoLight;
	}

}
