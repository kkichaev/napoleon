package com.grsoft.napoleon;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class NumberBox extends View {
	int val = 0;
	TextPaint paint = new TextPaint();
	int textSize = 15;
	boolean limit = false;
	Rect bounds = new Rect();
	boolean selected = false;
	boolean notused=true;

	public NumberBox(Context context) {
		super(context, null);
	}

	public NumberBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.NumberBox, 0, 0);
		textSize = a.getDimensionPixelSize(R.styleable.NumberBox_textSize,
				textSize);
		val = a.getInteger(R.styleable.NumberBox_value, 0);
		paint.setStrokeWidth(1);
		paint.setTextSize(textSize);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();
		
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(0, 0, width - 1, height - 1, paint);

		if (selected) {
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.GREEN);
			canvas.drawRect(2, 2, width - 2, height - 2, paint);
		}else if(limit){
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.LTGRAY);
			canvas.drawRect(2, 2, width - 2, height - 2, paint);
		}
		
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setTextAlign(Paint.Align.CENTER);
		
		String text = Util.IntToScaleStr(val, Consts.QTY_SCALE);
		canvas.drawText(text, width / 2,
				(height - paint.ascent()) / 2, paint);
	}

	public boolean isActive() {
		return limit;
	}

	public void setLimit(boolean limit) {
		this.limit = limit;
		invalidate();
	}

	public int getVal() {
		return val;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
		invalidate();
	}

	public void setVal(int val) {
		this.val = val;
		invalidate();
	}
	
	public void setNotUsed(boolean val){
		setVisibility(val ? View.INVISIBLE : View.VISIBLE);
	}
	
	public boolean isNotUsed(){
		return this.notused;
	}
}
