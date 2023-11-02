package com.ksoft.snakss;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class BorderRenderer {
	private Paint paint;
	private final int CHAR_SZ = 9;
	
	public BorderRenderer() {
		this.paint = new Paint();
		this.paint.setStyle(Style.FILL_AND_STROKE);
		this.paint.setColor(Color.BLACK);
		this.paint.setTextSize(CHAR_SZ);
	}
	
	public void draw(MainRenderer surface) {
		Canvas canvas = surface.getCanvas();
		int w = canvas.getWidth();
		int h = canvas.getHeight();
		
		for (int i = CHAR_SZ; i < w - CHAR_SZ; i += CHAR_SZ) {
			canvas.drawText("+", i, CHAR_SZ, paint);
			canvas.drawText("+", i, h - 1, paint);
		};
		
		for (int i = CHAR_SZ; i < h; i += CHAR_SZ) {
			canvas.drawText("+", 0, i, paint);
			canvas.drawText("+", w-5, i, paint);
		};
	}
}
