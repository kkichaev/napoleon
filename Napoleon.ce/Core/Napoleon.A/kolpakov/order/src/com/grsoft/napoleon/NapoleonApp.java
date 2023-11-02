/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.network.ServerCommand;

public class NapoleonApp extends NapoleonAppBase {
	
	@Override
	protected void init() {
		super.initDocTypes(false);
		ServerCommand.Category = "pda";
	}
}
