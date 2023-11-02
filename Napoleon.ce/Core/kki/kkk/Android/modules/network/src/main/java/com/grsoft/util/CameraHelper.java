/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   29/04/2011   creating
 */
package com.grsoft.util;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
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
		return getRequstCamSize(cmpAsc);
	}
	
	public static Size getMaxCamSize(){
		return getRequstCamSize(cmpDesc);
	}
	
	public static Size getRequstCamSize(Comparator<Camera.Size> cmp) {
		Camera camera = Camera.open();
		
		if(camera == null)
			return new Size(0,0);
		
		Camera.Parameters cameraParameters = camera.getParameters();
		List<Camera.Size> allSupportedSizes = getSupportedPictureSizes(cameraParameters);
		
		Collections.sort(allSupportedSizes, cmp);
		Camera.Size minCamSize = null;
		
		if (allSupportedSizes != null &&
				allSupportedSizes.size() > 0){
			minCamSize =  allSupportedSizes.get(0);
		}
		
		camera.release();
		
		return minCamSize != null ? new Size(minCamSize.width, minCamSize.height) : null;
	}
	
	static Comparator<Camera.Size> cmpAsc = new Comparator<Camera.Size>() {
		
		@Override
		public int compare(Camera.Size lhs, Camera.Size rhs) {
			long x = lhs.height * lhs.width;
			long y = rhs.height * rhs.width;
			return (int)(x-y);
		}
	};
	
	static Comparator<Camera.Size> cmpDesc = new Comparator<Camera.Size>() {
		
		@Override
		public int compare(Camera.Size lhs, Camera.Size rhs) {
			long x = lhs.height * lhs.width;
			long y = rhs.height * rhs.width;
			return (int)(y-x);
		}
	};
	
}
