package com.ksoft.race;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;

public class Controller {
	Bitmap bmpThrottle;
	Bitmap bmpWheel;

	RectF throttle = new RectF();
	RectF revers = new RectF();
	RectF right = new RectF();
	RectF left = new RectF();
	RectF screen = new RectF();

	Point throtlePlace = new Point();
	Point wheelPlace = new Point();

	public enum Control {
		NONE, THROTTLE, RIGHT, LEFT, REVERS
	};

	public Controller(Bitmap bmpThrottle, Bitmap wheel, RectF screen) {
		this.bmpThrottle = bmpThrottle;
		this.bmpWheel = wheel;
		this.screen = screen;

		final int MARGIN = 10;
		throtlePlace.set(MARGIN,
				(int) (screen.bottom - bmpThrottle.getHeight() - MARGIN));
		wheelPlace.set(
				(int) (screen.right - bmpWheel.getWidth() - MARGIN),
				(int) (screen.bottom - bmpThrottle.getHeight() + bmpThrottle
						.getHeight() / 4) - MARGIN);

		throttle.set(throtlePlace.x, throtlePlace.y, throtlePlace.x
				+ bmpThrottle.getWidth(),
				throtlePlace.y + bmpThrottle.getHeight() / 2);
		revers.set(throtlePlace.x,
				throtlePlace.y + bmpThrottle.getHeight() / 2, throtlePlace.x
						+ bmpThrottle.getWidth(),
				throtlePlace.y + bmpThrottle.getHeight());
		left.set(wheelPlace.x, wheelPlace.y, wheelPlace.x + bmpWheel.getWidth() / 2,
				wheelPlace.y + bmpWheel.getHeight());
		right.set(wheelPlace.x + bmpWheel.getWidth() / 2, wheelPlace.y,
				wheelPlace.x + bmpWheel.getWidth(), wheelPlace.y + bmpWheel.getHeight());
		
	}

	public void draw(Canvas canvas) {
		canvas.drawBitmap(bmpThrottle, throtlePlace.x, throtlePlace.y, null);
		canvas.drawBitmap(bmpWheel, wheelPlace.x, wheelPlace.y, null);
	}

	public Control getControl(float x, float y) {
		if (throttle.contains((int) x, (int) y))
			return Control.THROTTLE;
		else if (right.contains((int) x, (int) y))
			return Control.RIGHT;
		else if (left.contains((int) x, (int) y))
			return Control.LEFT;
		else if (revers.contains((int) x, (int) y))
			return Control.REVERS;
		return Control.NONE;
	}
}
