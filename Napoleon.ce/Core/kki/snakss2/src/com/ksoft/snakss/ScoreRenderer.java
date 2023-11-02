package com.ksoft.snakss;

import android.content.Context;
import android.graphics.Canvas;

public class ScoreRenderer extends FontRenderer{
	private Game game;
	private String score;
	
	public ScoreRenderer(Context context, Game game) {
		super(context);
		this.game = game;
		score = context.getString(R.string.scrore);
	}
	
	public void draw(MainRenderer scene) {
		Canvas c = scene.getCanvas(); 
		int sz = scene.getBorderSize();
		c.drawText(score + game.getScrore(), sz, c.getHeight() - sz / 2, getPaint());
	}

	@Override
	protected float getFontSize() { 
		return 20; 
	}
}
