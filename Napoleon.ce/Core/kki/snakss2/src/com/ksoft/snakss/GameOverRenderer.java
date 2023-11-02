package com.ksoft.snakss;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class GameOverRenderer extends FontRenderer{
	private String gameOver;
	
	public GameOverRenderer(Context context) {
		super(context);
		gameOver = context.getString(R.string.gameover);
	}
	
	public void draw(MainRenderer scene) {
		Canvas c = scene.getCanvas();
		Rect r = new Rect();
		Paint p = getPaint();
		p.getTextBounds(gameOver, 0, gameOver.length(), r);
		c.drawText(gameOver, c.getWidth() / 2 - r.centerX(), c.getHeight() / 2, p);
	}

	@Override
	protected float getFontSize() {
		return 28;
	}
}
