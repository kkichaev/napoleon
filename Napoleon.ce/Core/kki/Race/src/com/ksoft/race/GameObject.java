package com.ksoft.race;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

public abstract class GameObject {
	public Bitmap bitmap;
	public RectF rect = new RectF();
	public RectF testRect = new RectF();
	private RectF tmpRect = new RectF();
	Matrix mtx;
	Matrix tmpMtx = new Matrix();
	float angle = 0.0f;
	private float width;
	private float height;
	
	public GameObject(Bitmap bitmap){
		this.bitmap = bitmap;
		mtx = new Matrix();
		mtx.setRotate(angle);
		
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		tmpRect.set(0, 0, width, height);
		mtx.mapRect(tmpRect);
	}
	
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap,  mtx,  null);
	}
	
	public RectF getRect() {
		return rect;
	}
	
	public boolean moveTo(float x, float y, RectF bound) {
		boolean result = true; 
		testRect.set(rect);
		tmpMtx.set(mtx);
		
		mtx.setTranslate(x - width / 2, y - height / 2);
		mtx.mapRect(rect, tmpRect);
		mtx.postRotate(angle, rect.centerX(), rect.centerY());
		mtx.mapRect(rect, tmpRect);

		if(!bound.contains(rect)){
			rect.set(testRect);
			mtx.set(tmpMtx);
			result = false;
		}
		
		return result;
	}
}
