/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   21/04/2011   creating
 */
package com.grsoft.util;
import com.grsoft.aceteam.R;

import android.app.Activity;

/***
 * Интерфейс, который должна поддерживать вкладка в конфигурации(настройках)
 * @author kki
 *
 */
public abstract class SettingActivity extends Activity {

	public abstract void save();
	
	public abstract void update();

	/**
	 * 
	 * @return ресурс с именем 
	 */
	public abstract int getName();
	
	/**
	 * 
	 * @return ресурс с картинкой
	 */
	public abstract int getIcon();
	
	/**
	 * 
	 * @return true - только админу
	 */
	public boolean isAdminSettings() { return false; }
}
