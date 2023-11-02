/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 *
 * kki   26/04/2011   creating
 */
package com.grsoft.util;
import com.grsoft.aceteam.R;

import android.content.Context;
import android.content.SharedPreferences;

import com.grsoft.network.exception.InstanceNotInit;

/***
 * Счетчик для бинарных данных,
 * которые не хранятся в БД
 * @author kki
 *
 */
public class SrcDataCounter {
	private static SrcDataCounter instance;
	private SharedPreferences preferences;
	private final String NAME = "SrcDataCounter";
	
	private SrcDataCounter(Context context){
		 this.preferences = context.getApplicationContext().getSharedPreferences(Consts.PREF_KEY, 
				 Context.MODE_PRIVATE);
	}
	
	public static int getValue(){
		if (instance == null)
			return 0;
		
		int value = 0;
		int result = 0;
		
		if (instance.preferences.contains(instance.NAME))
			value = instance.preferences.getInt(instance.NAME, 0);
		
		result = value;
		value++;
		SharedPreferences.Editor editor = instance.preferences.edit();
		editor.putInt(instance.NAME, value);
		editor.commit();
		
		return result;
	}
	
	public static void init(Context context){
		if (instance == null)
			instance = new SrcDataCounter(context);
	}
	
	
	
}
