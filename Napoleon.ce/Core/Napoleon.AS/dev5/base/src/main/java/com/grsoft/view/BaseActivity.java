package com.grsoft.view;
import com.grsoft.aceteam.R;


import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GestureStroke;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;
import com.grsoft.aceteam.R;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;

public class BaseActivity extends Activity //implements OnGesturePerformedListener
{
	private GestureOverlayView gestureOverlayView;
	protected boolean canChangeOrientation = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * Устанавливает значение "цена" компонента
	 * tvTotalSum у Activity
	 * @param sum Немасштабированное значение цены
	 */
	public void updateTotalSum(long sum, int weight) {
		DocType.getCurDoc().updateTotalSum(this, sum, weight, 0);
	}
	
	public void updateTotalSum(long sum, int weight, int count) {
		DocType.getCurDoc().updateTotalSum(this, sum, weight, count);
	}

	public int getPrefValue(String name, int defValue)
	{
		SharedPreferences sp = getPreferences(MODE_PRIVATE);
		return sp.getInt(name, defValue);
	}
	
	public void setPrefValue(String name, int value)
	{
		SharedPreferences sp = getPreferences(MODE_PRIVATE);
		sp.edit().putInt(name, value).commit();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(canChangeOrientation){
			Config c = ConfigManager.getConfig();
			if( c instanceof com.grsoft.napoleon.util.CfgNplW ) {
				com.grsoft.napoleon.util.CfgNplW config = (com.grsoft.napoleon.util.CfgNplW)c;
				config.setOrientation(this);
			}
		}

//		gestureOverlayView = (GestureOverlayView) findViewById(R.id.gestures);
//
//		if (gestureOverlayView != null)
//			gestureOverlayView.addOnGesturePerformedListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (gestureOverlayView != null){
			gestureOverlayView.removeAllOnGesturePerformedListeners();
			gestureOverlayView = null;
		}
			
	}

//	@Override
//	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
//		ArrayList<GestureStroke> strokes = gesture.getStrokes();
//
//		if (strokes.size() > 0 && IsSwipeLeftToRight(strokes.get(0).points))
//			dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
//	}
	
	private boolean IsSwipeLeftToRight(float[] points){
		if (points.length >= 4){
			float x1 = points[0];
			float x2 = points[points.length-2];
			float y1 = points[1];
			float y2 = points[points.length-1];
			
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			
			if (display != null){
				float min_x_distance_for_swipe = display.getWidth() / 2;
				float max_y_distance_for_swipe = display.getHeight() / 4;
				
				if (x1 < x2 &&
						Math.abs(y1-y2) < max_y_distance_for_swipe && 
						Math.abs(x1-x2) > min_x_distance_for_swipe)
					return true;
				else
					return false;
			}else
				return false;
		}else
			return false;
	}
}
