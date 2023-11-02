package com.grsoft.util.view;
import com.grsoft.aceteam.R;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;


public class ViewUtil {
	public static float dipToPixels(Context context, float val) {
	    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
	    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, metrics);
	}
	
	public static float spToPixel(Context context, float val) {
	    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
	    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, val, metrics);
	}
}
