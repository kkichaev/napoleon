package com.ksoft.snakss;

import java.util.List;

import com.ksoft.snakss.Game.GameEventListener;
import com.ksoft.snakss.GameSound.Sounds;
import com.ksoft.snakss.TouchHandler.TouchEvent;

import android.content.Context;
import android.view.View;

public class GameController implements GameEventListener{
	private Game game;
	private MainRenderer mainRenderer;
	private TouchHandler touchHandler;
	private final int WIDTH = 500;
	private final int HEIGHT = 380;
	private long saveTime = -1; 
	private final long STEP_TIME = 200000000;
	private final int BORDER_SZ = 30; 
	private int cellSize;
	private final long GAME_OVER_WAIT_TIME = 2000000000l;
	private long gameOverFiredTime = -1;
	private GameSound sound;
	
	public GameController(Context context, int w, int h) {
		this.game = new Game();
		this.game.setGameEventListenet(this);
		this.sound = new GameSound(context);
		this.mainRenderer = new MainRenderer(context, this, game, WIDTH, HEIGHT);
		this.touchHandler = new SingleTouchHandler(mainRenderer, (float)WIDTH / w, (float)HEIGHT / h);
		this.cellSize = (WIDTH - BORDER_SZ * 2) / game.getWidth();
	}
	
	public View getView() {
		return mainRenderer;
	}
	
	public void resume() {
		saveTime = System.nanoTime();
		mainRenderer.resume();
	}
	
	public void pause() {
		mainRenderer.pause();
	}
	
	public void gameStep() {
		long now = System.nanoTime();
		
		if((now - saveTime) >= (STEP_TIME - (game.getLevel() - 1) * 10000000)) {
			saveTime = now;
			handleInputEvent();
			game.step();
		}
		
		if (game.isGameOver() && (System.nanoTime() - gameOverFiredTime >= GAME_OVER_WAIT_TIME) && touchHandler.isTouchDown(0)) {
			touchHandler.getTouchEvents();
			game.restart();
		}
	}
	
	private void handleInputEvent() {
		TouchEvent e = getLastTouchDown();
		
		if (e != null) {
			int sx = worldToCell(e.x);
			int sy = worldToCell(e.y);
			
			game.pressed(sx, sy);
		}
	}
	
	private int worldToCell(int w) {
		return w <= BORDER_SZ ? 0 : ( w - BORDER_SZ) / cellSize;
	}

	private TouchEvent getLastTouchDown() {
		List<TouchEvent> events = touchHandler.getTouchEvents();
		TouchEvent result = null;
		
		for(int i = events.size() - 1; i >= 0; i--) {
			TouchEvent e = events.get(i);
			
			if (e.type == TouchEvent.TOUCH_DOWN) {
				result = e;
				break;
			}
		}
		
		return result;
	}
	
	public int getStep() { 
		return cellSize; 
	}
	
	@Override
	public void onEat() {
		sound.play(Sounds.Eat);
	}

	@Override
	public void onGameOver() {
		gameOverFiredTime = System.nanoTime();
		sound.play(Sounds.GameOver);
	}

	public int getBorderSize() {
		return BORDER_SZ;
	}
}
