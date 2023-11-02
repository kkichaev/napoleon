package com.ksoft.snakss;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

public class FieldRenderer extends Renderer{
	private static final int STROKE_WITDH = 2;
	
	public FieldRenderer(Context context) {
		super(context);
	}
	
	@Override
	protected void initPaint(Context c, Paint p) {
		super.initPaint(c, p);
		
		p.setStyle(Style.STROKE);
		p.setStrokeWidth(STROKE_WITDH);
		p.setColor(Color.BLUE);
	}
	
	public void draw(MainRenderer surface) {
		int bsz = surface.getBorderSize(); 
		
		Canvas canvas = surface.getCanvas();
		int step = surface.getStep();
		
		Game g = surface.getGame();
		int wc = g.getWidth();
		int hc = g.getHeight();
		
		Rect r = new Rect();
		r.top = bsz;
		r.left = bsz;
		r.right = bsz + wc * step;
		r.bottom = bsz + hc * step;
		
		Paint p = getPaint();
		canvas.drawRect(r, p);
	}

}
