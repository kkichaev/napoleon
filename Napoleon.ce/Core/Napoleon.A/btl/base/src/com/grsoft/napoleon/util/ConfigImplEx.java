package com.grsoft.napoleon.util;

import java.util.List;

import android.hardware.Camera;

import com.grsoft.util.CameraHelper;
import com.grsoft.util.Size;


@SuppressWarnings("serial")
public class ConfigImplEx extends CfgNpl {
	@Override
	public void resetToDefault() {
		Size result = new Size(cameraWidth, cameraHeight);
		
		if (result.width == 0 || result.hight == 0){
			try{
				Camera camera = Camera.open();
				Camera.Parameters cameraParameters = camera.getParameters();

				List<Camera.Size> allSupportedSizes = 
						CameraHelper.getSupportedPictureSizes(cameraParameters);
				Camera.Size camSize = null;
				
				if (allSupportedSizes != null &&
						allSupportedSizes.size() > 0){
					camSize =  allSupportedSizes.get(0);
					int w = camSize.width;
					int h = camSize.height;
					
					for (Camera.Size size : allSupportedSizes) {
						if (w > size.width && h > size.height &&
								size.width > 1024 && size.height > 768)
							camSize = size;
					}
				}
				
				if (camSize != null){
					result = new Size(camSize.width, camSize.height);
					cameraHeight = camSize.height;
					cameraWidth = camSize.width;
					ConfigManager.save();
				}
				
				camera.release();
				camera = null;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
