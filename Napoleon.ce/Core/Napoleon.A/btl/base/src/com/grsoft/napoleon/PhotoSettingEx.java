package com.grsoft.napoleon;

import android.hardware.Camera;

public class PhotoSettingEx extends PhotoSetting {
	private static final int MIN_WIDTH = 1024;
	private static final int MIN_HEIGHT = 768;
	
	@Override
	protected boolean isAvailResolution(Camera.Size cameraSize){
		return cameraSize.width >= MIN_WIDTH && cameraSize.height >= MIN_HEIGHT;
	}
}
