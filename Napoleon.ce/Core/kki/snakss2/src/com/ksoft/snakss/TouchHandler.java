package com.ksoft.snakss;

import java.util.List;

import android.view.View.OnTouchListener;

public interface TouchHandler extends OnTouchListener {
	public static class TouchEvent{
		public static final int TOUCH_DOWN = 0;
		public static final int TOUCH_UP = 1;
		public static final int TOUCH_DRAGGED = 2;
		
		public int type;
		public int x;
		public int y;
		public int pointer;
	}
	
	boolean isTouchDown(int pointer);
	public int getTouchX();
	public int getTouchY();
	public List<TouchEvent> getTouchEvents();
}
