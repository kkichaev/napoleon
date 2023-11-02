package com.ksoft.viewtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomView extends View {
	private Paint paint = new Paint();
	private int val = 33;
	
	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.GRAY);
		canvas.drawRect(0, 0, getWidth() - 1, getHeight() - 1,  paint);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawText(Integer.toString(val), 
				getWidth() / 2, getHeight() / 2, paint);
	}

}
