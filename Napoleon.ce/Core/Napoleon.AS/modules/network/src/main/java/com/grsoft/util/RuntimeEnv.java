/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   25/04/2011   creating
 */

package com.grsoft.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/***
 * Параметры программы 
 * @author kki
 *
 */
public class RuntimeEnv {
	/***
	 * Поддержка камеры
	 * @return
	 */
	public static boolean isPhotoSupported(){
		return true;
//		final int MIN_VERSION_TO_PHOTO = 7;
//		return Util.getPlatformVersion() >= MIN_VERSION_TO_PHOTO;
	}
	
	public static Class<?> getMainActivity(Context context){
		PackageManager pm = context.getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
		Class<?> result = null;
		
		try {
			result = Class.forName(intent.getComponent().getClassName());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
}
