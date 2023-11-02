package com.ksoft.snakss;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainRenderer extends SurfaceView implements Runnable{
	private volatile boolean running = false;
	private SurfaceHolder holder;
	private Thread rendered;
	private Bitmap frame;
	private Canvas canvas;
	private Game game;
	private GameRenderer gameRenderer;
	private FieldRenderer gridRenderer;
	private GameController controller;
	
	public MainRenderer(Context context, GameController controller, Game game, int width, int height) {
		super(context);
		this.game = game;
		this.controller = controller;
		this.holder = getHolder();
		this.frame = Bitmap.createBitmap(width, height, Config.RGB_565);
		this.canvas = new Canvas(frame);
		this.gameRenderer = new GameRenderer(context, game);
		this.gridRenderer = new FieldRenderer(context);
	}

	@Override
	public void run() {
		final Rect dstRect = new Rect();
		while(running) {
			if(holder.getSurface().isValid()) {
				drawGameObjects();
				drawBitmap(dstRect);
			}
		}
	}

	private void drawBitmap(Rect dstRect) {
		Canvas c = holder.lockCanvas();
		c.getClipBounds(dstRect);
		c.drawBitmap(frame, null,  dstRect, null);
		holder.unlockCanvasAndPost(c);
	}
	
	private void drawGameObjects() {
		canvas.drawRGB(255, 255, 255);
		gridRenderer.draw(this);
		controller.gameStep();
		gameRenderer.draw(this);
	}

	public void resume() {
		running = true;
		rendered = new Thread(this);
		rendered.start();
	}
	
	public void pause() {
		running = false;
		while(true) {
			try {
				rendered.join();
				return;
			}catch(Exception e) {}
		}
	}

	public Canvas getCanvas() { 
		return canvas; 
	}
	
	public int getBorderSize() { 
		return controller.getBorderSize(); 
	}
	
	public Game getGame() {
		return game;
	}

	public int getStep() { 
		return controller.getStep(); 
	}
}
