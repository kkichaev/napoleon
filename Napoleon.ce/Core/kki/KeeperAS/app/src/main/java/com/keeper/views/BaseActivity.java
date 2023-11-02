package com.keeper.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;

import com.keeper.KeeperApp;
import com.keeper.R;

public abstract class BaseActivity extends Activity
	implements OnGestureListener, OnDoubleTapListener{
	
	private static final int RESOURCE_ID = 999;
	private static final String TAG  = "BaseActivity";
	private static final String PASSWORD = "password";
	
	private static final int DLG_ABOUT_ID = RESOURCE_ID;
	
	private GestureDetector gestureDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		gestureDetector = new GestureDetector(this, this);
	    gestureDetector.setOnDoubleTapListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		gestureDetector = null;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.itSetting:
			Setting.open(this);
			return true;
		case R.id.itAbout:
			showDialog(DLG_ABOUT_ID);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(PASSWORD, KeeperApp.masterPassword);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		KeeperApp.masterPassword = savedInstanceState.getString(PASSWORD);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		Log.d(TAG, "onDown");
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Log.d(TAG, String.format("onFling X=%f Y=%f",velocityX,velocityY));
		if(velocityX > 0)
			finish();
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		Log.d(TAG, "onLongPress");
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		Log.d(TAG, "onScroll");
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		Log.d(TAG, "onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Log.d(TAG, "onSingleTapUp");
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Log.d(TAG, "onDoubleTap");
		openOptionsMenu();
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		Log.d(TAG, "onDoubleTapEvent");
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		Log.d(TAG, "onSingleTapConfirmed");
		return false;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case DLG_ABOUT_ID:
			return createAboutDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createAboutDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.about);
		String version = "unknown";
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		builder.setMessage(getResources().getString(R.string.about_text, version));
		builder.setPositiveButton(R.string.ok, null);
		return builder.create();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return true;
	}
}
