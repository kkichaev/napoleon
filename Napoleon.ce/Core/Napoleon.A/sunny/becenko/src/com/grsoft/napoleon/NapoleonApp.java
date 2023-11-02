/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

public class NapoleonApp extends ApplicationBase {
	public void initDocTypes() {
		super.initDocTypes();
		
		DocList.activity = DocListEx.class;
		
//		Features.LAST_SALED_ITEMS_PERIOD = 2;
		Features.START_STOP = true;
	}
}
