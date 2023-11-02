package com.grsoft.napoleon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grsoft.util.Consts;

public class StripedLinearLayout extends LinearLayout {

	private ShapeDrawable mDrawable;
	private final int DEF_TOP_COLOR = Color.LTGRAY; 
	private final int DEF_TOP_PADDING = 2;
	boolean paintOdd = false;
	
	public StripedLinearLayout(Context context) {
		super(context);
		Log.d(Consts.D_TAG, "SalesHistoryLayout(Context context)");
		init();
	}
	
	public StripedLinearLayout(Context context, AttributeSet attrs){
		super(context, attrs);
		Log.d(Consts.D_TAG, "SalesHistoryLayout(Context context, AttributeSet attrs)");
		init();
	}

	private void init(){
		setWillNotDraw(false);
		mDrawable = new ShapeDrawable(new RectShape());
        mDrawable.getPaint().setColor(DEF_TOP_COLOR);
	}
	
	/**
	 * ѕо умолчанию серым крас€тс€ четные полоски. “ак делаем нечетные
	 */
	public void paintOdd() {
		paintOdd = true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if( getChildCount() > 0 ) {
			View v = getChildAt(0);
			int height = v.getHeight() - v.getPaddingTop() - v.getPaddingBottom();
			if( v instanceof TextView ) {
				TextView tv = (TextView)v;
				height = tv.getLineHeight();// /= tv.getLineCount();
			}
	        int width = getWidth();
	        int ch = v.getPaddingTop() + DEF_TOP_PADDING, mh = getHeight();
	        int stop = mh - height;
	        if( paintOdd ) {
	        	stop += height;
	        	ch += height;
	        }
	        // раскрашиваем каждую вторую полоску
	        for( ; ch < stop; ch += 2 * height ) {
	        	mDrawable.setBounds(0, ch, width, ch + height);
	        	mDrawable.draw(canvas);
	        }
	        
	        Log.d(Consts.D_TAG, "onDraw background: height=" + Integer.toString(height));
		}
        super.onDraw(canvas);   
	}
}
