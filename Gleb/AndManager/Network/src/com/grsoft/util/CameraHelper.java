/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   29/04/2011   creating
 */
package com.grsoft.util;

import java.lang.reflect.Method;
import java.util.List;

import android.hardware.Camera;

/***
 * Класс содержит методы класса android.hardware.Camera
 * которые вызываются через рефлекшен
 * и различные вспомогательные методы для работы с камерой 
 *
 * @author kki
 *
 */
public class CameraHelper {

	@SuppressWarnings("unchecked")
	public static List<Camera.Size> getSupportedPictureSizes(Camera.Parameters cameraParameters){
		
		try{
			Method getSupportedPictureSizes = Camera.Parameters.class.
				getMethod("getSupportedPictureSizes", (Class[])null);
		
			return(List<Camera.Size>) getSupportedPictureSizes.
				invoke(cameraParameters, (Object[])null);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static Size getMinCamSize(){
		Camera camera = Camera.open();
		Camera.Parameters cameraParameters = camera.getParameters();
		List<Camera.Size> allSupportedSizes = getSupportedPictureSizes(cameraParameters);
		Camera.Size minCamSize = null;
		
		if (allSupportedSizes != null &&
				allSupportedSizes.size() > 0){
			minCamSize =  allSupportedSizes.get(0);
			int w = minCamSize.width;
			int h = minCamSize.height;
			
			for (Camera.Size size : allSupportedSizes) {
				if (w > size.width && h > size.width)
					minCamSize = size;
			}
		}
		
		camera.release();
		return minCamSize != null ? new Size(minCamSize.width, minCamSize.height) : null;
	}
}
