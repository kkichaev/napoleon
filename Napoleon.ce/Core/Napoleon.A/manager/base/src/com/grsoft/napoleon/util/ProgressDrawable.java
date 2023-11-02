package com.grsoft.napoleon.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class ProgressDrawable extends Drawable {
	final static int RED_VALUE = 33;
	final static int YELLOW_VALUE = 67;
	int progress;
	Paint paint = new Paint(); 
	
	public ProgressDrawable(int progress) {
		this.progress = progress;
		
		paint.setColor(progress < RED_VALUE ? Color.RED : progress < YELLOW_VALUE ? Color.YELLOW : Color.GREEN);
		paint.setAlpha(100);
	}
	
	@Override
	public void draw(Canvas canvas) {
		Rect b = getBounds();
		Rect r = new Rect(b);
		double coef = progress / 100.0;
		if( coef > 1 ) coef = 1;
		r.right = (int) (r.left + b.width() * coef);
		canvas.drawRect(r, paint);
	}

	@Override public int getOpacity() { return 0; }

	@Override
	public void setAlpha(int arg0) { }

	@Override
	public void setColorFilter(ColorFilter arg0) { }

}
