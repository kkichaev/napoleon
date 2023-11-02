package com.ksoft.race;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.RectF;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends FragmentActivity implements GameOverDialog.ResultListener{
	private Game game;
	public static final String GAME_OVER_ACTION = "game_over_action";
	
	private BroadcastReceiver gameOverRecv = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			FragmentManager fm =  MainActivity.this.getSupportFragmentManager();
			GameOverDialog dlg = new GameOverDialog();
			dlg.setResultListener(MainActivity.this);
			dlg.show(fm, dlg.getClass().toString());
		}
	};
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		registerReceiver(gameOverRecv, new IntentFilter(GAME_OVER_ACTION));
		
		try {
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			Display d = wm.getDefaultDisplay();
			game = new Game(this, new RectF(0, 0, d.getWidth(), d.getHeight()));

			setContentView(game);
		} catch (Exception e) {

		}
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		int pidx = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		int pcnt = event.getPointerCount();

		for (int i = 0; i < pcnt; i++) {
			if (event.getAction() != MotionEvent.ACTION_MOVE && i != pidx)
				continue;

			switch (action) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				game.touched(event.getX(i), event.getY(i));
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				game.untouched(event.getX(i), event.getY(i));
				break;
			case MotionEvent.ACTION_MOVE:
				game.untouched(event.getX(i), event.getY(i));
				game.touched(event.getX(i), event.getY(i));
				break;
			}
		}

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		game.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		game.resume();
	}

	@Override
	public void onResultSelect(int code) {
		switch (code) {
		case GameOverDialog.OK:
			game.restart();
			break;

		default:
			finish();
			break;
		}
		
	}

}
