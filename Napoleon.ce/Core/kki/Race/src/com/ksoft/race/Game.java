package com.ksoft.race;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ksoft.race.Controller.Control;

public class Game extends SurfaceView implements Runnable {
	private volatile boolean running;
	private Thread thread;
	private SurfaceHolder holder;
	private Car car;
	private Paint paint = new Paint();
	private Controller controller;
	private Flag flag;
	private int flags = 0;
	private Random rnd = new Random();
	private RectF screen;
	private boolean gameover = false;
	private int gameTime = START_TIME;
	private static final int START_TIME = 50;
	private static final int BONUS_TIME = 15;
	private SoundPool soundPool;
	private int carDrvId = -1;
	private int flagFndId = -1;
	
	public Game(Context context) {
		super(context);
	}

	public Game(Context context, RectF screen) throws IOException {
		super(context);
		this.screen = screen;
		holder = getHolder();
		InputStream input = context.getAssets().open("pic/race.png");
		Bitmap bitmap = BitmapFactory.decodeStream(input);
		car = new Car(bitmap);
		car.moveTo(400, 200, screen);
		paint.setTextSize(22);
		paint.setColor(Color.WHITE);

		input = context.getAssets().open("pic/speedctrl.png");
		bitmap = BitmapFactory.decodeStream(input);
		input = context.getAssets().open("pic/wheel.png");
		Bitmap wheel = BitmapFactory.decodeStream(input);
		controller = new Controller(bitmap, wheel, screen);

		input = context.getAssets().open("pic/flag.png");
		bitmap = BitmapFactory.decodeStream(input);
		flag = new Flag(bitmap);
		flag.moveTo(200, 200, screen);
		
		soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
		AssetFileDescriptor afd = context.getAssets().openFd("sound/car.wav");
		carDrvId = soundPool.load(afd, 1);
		afd = context.getAssets().openFd("sound/bum.ogg");
		flagFndId = soundPool.load(afd, 1);
	}

	@Override
	public void run() {
		long lastTime = 0;  
		long playDrvSndTime = 0;
		
		StringBuilder text = new StringBuilder();
		while (running) {

			if (!gameover) {
				car.move(screen);
			
				if(car.speed != 0){
					if((SystemClock.elapsedRealtime() - playDrvSndTime) > 4500){
						playDrvSndTime = SystemClock.elapsedRealtime();
						soundPool.play(carDrvId, 1, 1, 0, 0, 1);
					}
				}
				
				if (RectF.intersects(car.getRect(), flag.getRect())) {
					flags++;
					flag.moveTo(rnd.nextInt(800), rnd.nextInt(370), screen);
					gameTime += BONUS_TIME;
					soundPool.play(flagFndId, 1, 1, 0, 0, 1);
				}
				
				if(lastTime == 0)
					lastTime = SystemClock.elapsedRealtime();
				
				if(SystemClock.elapsedRealtime() - lastTime > 300){
					gameTime--;
					lastTime = SystemClock.elapsedRealtime();
				}
				
				if(gameTime <= 0){
					gameover = true;
					Intent intent = new Intent(MainActivity.GAME_OVER_ACTION);
					getContext().sendBroadcast(intent);
				}
			}
			
			if (holder.getSurface().isValid()) {
				Canvas canvas = holder.lockCanvas();
				canvas.drawRGB(0, 0, 0);

				car.draw(canvas);
				flag.draw(canvas);

				controller.draw(canvas);

				text.setLength(0);
				text.append("Флаги: ").append(flags).append(" Время: ").append(gameTime);
				canvas.drawText(text.toString(), 10, 30, paint);

				// text.setLength(0);
				// text.append("car: ").append(car.getRect());
				// canvas.drawText(text.toString(), 10, 55, paint);
				//
				// text.setLength(0);
				// text.append("speed: ").append(car.speed).append(" angle: ").append(car.angle);
				// canvas.drawText(text.toString(), 10, 80, paint);
				//
				// text.setLength(0);
				// text.append("flag: ").append(flag.getRect());
				// canvas.drawText(text.toString(), 10, 105, paint);
				//
				// text.setLength(0);
				// text.append("carw: ").append(car.bitmap.getWidth()).append(" carh: ").append(car.bitmap.getHeight());
				// canvas.drawText(text.toString(), 10, 130, paint);
				//
				// text.setLength(0);
				// text.append("flagw: ").append(flag.bitmap.getWidth()).append(" flagh: ").append(flag.bitmap.getHeight());
				// canvas.drawText(text.toString(), 10, 155, paint);

				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	public void resume() {
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public void pause() {
		running = false;

		while (true) {
			try {
				thread.join();
				return;
			} catch (Exception e) {
			}
		}
	}

	public void touched(float x, float y) {
		Control ctrl = controller.getControl(x, y);

		if (ctrl == Control.THROTTLE)
			car.throttle = true;
		else if (ctrl == Control.REVERS)
			car.revers = true;

		if (ctrl == Control.RIGHT)
			car.right = true;
		else if (ctrl == Control.LEFT)
			car.left = true;
	}

	public void untouched(float x, float y) {
		car.throttle = false;
		car.revers = false;
		car.right = false;
		car.left = false;
	}

	public void restart() {
		gameTime = START_TIME;
		car.speed = 0;
		gameover = false;
		flags = 0;
	}
}
