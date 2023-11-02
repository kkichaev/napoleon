package com.grsoft.napoleon;
import android.view.KeyEvent;


public class CameraPreviewEx extends CameraPreview {
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if( keyCode == 27 ) {
			save();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
