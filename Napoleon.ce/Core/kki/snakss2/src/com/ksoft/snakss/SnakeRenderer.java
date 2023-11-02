package com.ksoft.snakss;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

public class SnakeRenderer extends BaseSymbolRenderer{
	private Snake snake;
	private final String SYMBOL = "E";
	private final String[] BODY = new String[] {"S", "N", "A", "K", "E"};
	private Game game;
	
	public SnakeRenderer(Context context, Game game) {
		super(context);
		this.snake = game.getSnake();
		this.game = game;
	}
	
	@Override protected String getSymbol(int pos) {
		return pos < BODY.length ? BODY[pos] : SYMBOL; 
	}

	@Override
	protected Cell[] getCells() {
		Cell[] tail = snake.getBody();
		
		Cell[] result = new Cell[tail.length];
		
		for(int i = 0; i < tail.length; i++) {
			Cell tp = tail[i];
			result[i] = new Cell(tp.x, tp.y);
		}
		
		return result;
	}
	
	@Override
	protected Paint getPaint() {
		Paint p = super.getPaint();
		
		if(game.isGameOver())
			p.setColor(Color.RED);
		else
			p.setColor(Color.BLACK);
			
		return p;
	}
}
