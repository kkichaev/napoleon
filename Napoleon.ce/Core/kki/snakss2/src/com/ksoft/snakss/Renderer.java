package com.ksoft.snakss;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public abstract class Renderer {
	private Paint paint;
	private Context context;
	
	public Renderer(Context context) {
		this.context = context;
		this.paint = new Paint();
		
		initPaint(context, paint);
	}
	
	protected void initPaint(Context c, Paint p) {
		p.setStyle(Style.FILL_AND_STROKE);
		p.setColor(getPaintColor());
	}

	private int getPaintColor() {
		return Color.BLACK;
	}
	
	protected Context getContext() {
		return context;
	}
	
	protected Paint getPaint() {
		return paint;
	}
	
	public abstract void draw(MainRenderer scene);
}
