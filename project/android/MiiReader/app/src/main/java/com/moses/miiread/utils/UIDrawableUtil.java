package com.moses.miiread.utils;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

@SuppressWarnings("ResourceType")
public class UIDrawableUtil
{

	public enum RadioBtnSide
	{
		SINGLE, LEFT, CENTRAL, RIGHT
	}


	public static Drawable getNormalStrokeRoundRectDrawable(int strokeWidth, float radius, int strokeColor, int solidColor)
	{
		GradientDrawable bgDrawable = new GradientDrawable();
		bgDrawable.setStroke(strokeWidth, strokeColor);
		bgDrawable.setColor(solidColor);
		bgDrawable.setCornerRadius(radius);
		return bgDrawable;
	}
}