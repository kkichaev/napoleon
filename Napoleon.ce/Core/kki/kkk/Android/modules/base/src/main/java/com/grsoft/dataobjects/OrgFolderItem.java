/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Пункт из списка маршрута
 *
 * kki   16/02/2011   creating
 */
package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrgFolderItem extends DataObject
{	
	@FieldOrder(order=0)
	public String name = "";
	
	@FieldOrder(order=1)
	public int pos = 0;
}
