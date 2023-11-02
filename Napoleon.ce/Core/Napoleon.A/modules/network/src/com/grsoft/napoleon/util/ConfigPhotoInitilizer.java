package com.grsoft.napoleon.util;

import com.grsoft.util.CameraHelper;
import com.grsoft.util.Size;

public class ConfigPhotoInitilizer {
	public void init(Config config) {
		Size size = new Size(config.cameraWidth, config.cameraHeight);
		
		if (size == null || size.width == 0 || size.hight == 0) {
			try {
				Size newSize = getCamSize();
				config.cameraWidth = newSize.width;
				config.cameraHeight = newSize.hight;
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}
	
	protected Size getCamSize() {
		return CameraHelper.getMinCamSize();
	}
}
