package com.ksoft.race;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.SystemClock;

public class Car extends GameObject {
	private static final long ENGINE_UPDATE_TIME = 400;
	private static final long WHEEL_UPDATE_TIME = 400;
	private static final long WASTE_SPEED_TIME = 900;
	private static final float SPEED_INC = 1.5f;

	public float speed;
	boolean throttle;
	boolean revers;
	boolean left;
	boolean right;

	private long lastEngineUpdateTime;
	private long lastWasteSpeedTime;
	private long lastWasteWheelTime;

	public Car(Bitmap bitmap) {
		super(bitmap);
	}

	public boolean move(RectF screen) {
		boolean result = false;
		float x = rect.centerX();
		float y = rect.centerY();
		float a = angle;

		if (throttle
				&& (SystemClock.elapsedRealtime() - lastEngineUpdateTime) > ENGINE_UPDATE_TIME) {
			speed += SPEED_INC;
			lastEngineUpdateTime = SystemClock.elapsedRealtime();
		}

		if (!throttle
				&& !revers
				&& (SystemClock.elapsedRealtime() - lastWasteSpeedTime) > WASTE_SPEED_TIME) {
			if (speed > 0) {
				speed -= SPEED_INC;

				if (speed < 0)
					speed = 0;
			} else if (speed < 0) {
				speed += SPEED_INC;

				if (speed > 0)
					speed = 0;
			}
			lastWasteSpeedTime = SystemClock.elapsedRealtime();
		}

		if (revers
				&& (SystemClock.elapsedRealtime() - lastEngineUpdateTime) > ENGINE_UPDATE_TIME) {
			speed--;
			lastEngineUpdateTime = SystemClock.elapsedRealtime();
		}

		if (right
				&& (SystemClock.elapsedRealtime() - lastWasteWheelTime) > WHEEL_UPDATE_TIME) {
			angle++;

			if (angle < 0)
				angle = 359;
			if (angle > 360)
				angle = 1;
		}

		if (left
				&& (SystemClock.elapsedRealtime() - lastWasteWheelTime) > WHEEL_UPDATE_TIME) {
			angle--;

			if (angle < 0)
				angle = 359;
			if (angle > 360)
				angle = 1;
		}

		if (speed != 0) {
			x += (float) (Math.cos(Math.toRadians(angle - 90)) * speed);
			y += (float) (Math.sin(Math.toRadians(angle - 90)) * speed);
		}

		result = moveTo(x, y, screen);
		
		if (!result) {
			speed = 0;
			angle = a;
		}

		return result;
	}
}
