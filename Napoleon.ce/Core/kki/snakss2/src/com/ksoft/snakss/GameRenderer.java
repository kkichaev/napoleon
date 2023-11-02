package com.ksoft.snakss;

import android.content.Context;

public class GameRenderer {
	private SnakeRenderer snakeRenderer;
	private RabbitRenderer rabbitRenderer;
	private ScoreRenderer scoreRenderer;
	private GameOverRenderer gameOverRenderer;
	private Game game;
	
	public GameRenderer(Context context, Game game) {
		this.game = game;
		this.snakeRenderer = new SnakeRenderer(context, game);
		this.rabbitRenderer = new RabbitRenderer(context, game.getRabbits());
		this.scoreRenderer = new ScoreRenderer(context, game);
		this.gameOverRenderer = new GameOverRenderer(context);
	}
	
	public void draw(MainRenderer scene) {
		snakeRenderer.draw(scene);
		rabbitRenderer.draw(scene);
		scoreRenderer.draw(scene);
		
		if(game.isGameOver()) {
			gameOverRenderer.draw(scene);
		}
	}
}
