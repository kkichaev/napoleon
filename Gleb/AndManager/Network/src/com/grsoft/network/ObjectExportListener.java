/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   03/05/2011   creating
 */
package com.grsoft.network;

import com.grsoft.dataobjects.DataObject;

/***
 * Экспорт простых объектов 
 * @author kki
 *
 */
public interface ObjectExportListener extends ObjectListener {
	int size();
	DataObject get(int i);
}
