package com.ksoft.snakss;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class Main extends Activity{
	private GameController game;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Point sz = new Point();
		Display d = getWindowManager().getDefaultDisplay();
		d.getSize(sz);
		
		game = new GameController(this, sz.x, sz.y);
		
		setContentView(game.getView());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		game.resume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		game.pause();
	}
}
