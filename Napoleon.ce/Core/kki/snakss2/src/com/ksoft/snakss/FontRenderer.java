package com.ksoft.snakss;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;

public abstract class FontRenderer extends Renderer{
	
	public FontRenderer(Context context) {
		super(context);
	}

	@Override
	protected void initPaint(Context c, Paint p) {
		super.initPaint(c, p);
		
		Typeface tf = Typeface.createFromAsset(c.getAssets(), getFontFileName());
		p.setTextSize(getFontSize());
		p.setTypeface(Typeface.DEFAULT_BOLD);
		p.setTypeface(tf);
	}

	private String getFontFileName() {
		return "fonts/zxspectr.ttf";
	}
	
	protected abstract float getFontSize();
}
