package com.ksoft.snakss;

import android.content.Context;
import android.graphics.Canvas;

public abstract class CellRenderer extends FontRenderer{
	
	public CellRenderer(Context context) {
		super(context);
	}
	
	@Override
	public void draw(MainRenderer scene) {
		Canvas canvas = scene.getCanvas();
		int bsz = scene.getBorderSize();
		int step = scene.getStep();
		final int MARGIN_LEFT = 2;
		final int MARGIN_BOTTOM = 0;
		
		drawProcess(canvas, bsz + MARGIN_LEFT, bsz + (int)getFontSize() - MARGIN_BOTTOM, step);
	}
	
	private void drawProcess(Canvas canvas, int shX, int shY, int step) {
		Cell[] cells = getCells();
		
		if(cells != null)
			for(int i = 0; i < cells.length; i++) {
				Cell c = cells[i];
				drawSymbol(canvas, i, c.x * step + shX, c.y * step + shY);
			}
	}
	
	private void drawSymbol(Canvas canvas, int pos, int x, int y) { 
		canvas.drawText(getSymbol(pos), x, y, getPaint()); 
	}
	
	protected abstract String getSymbol(int pos);
	
	protected abstract Cell[] getCells();
}
