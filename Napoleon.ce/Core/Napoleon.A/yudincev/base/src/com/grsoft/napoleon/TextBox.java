package com.grsoft.napoleon;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class TextBox extends View {
	private static final String DEF_TEXT = "X";
	private String text = DEF_TEXT;
	private int textSize = 15;
	private TextPaint paint = new TextPaint();
	
	public TextBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.TextBox, 0, 0);
		textSize = a.getDimensionPixelSize(R.styleable.TextBox_textSize,
				textSize);
		text = a.getString(R.styleable.TextBox_text);
		
		if(text == null || text.trim().length() == 0)
			text = DEF_TEXT;
		
		paint.setStrokeWidth(1);
		paint.setTextSize(textSize);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.GRAY);
		paint.setTextAlign(Paint.Align.CENTER);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();
		
		if(text != null)
			canvas.drawText(text, width / 2,
					(height - paint.ascent()) / 2, paint);
	}

	public void setText(String text) {
		setVisibility(View.VISIBLE);
		this.text = text; 
		invalidate();
	}

	public void resetText() {
		setVisibility(View.INVISIBLE);
	}
}
