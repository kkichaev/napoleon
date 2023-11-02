/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Контакты организации
 *
 * kki   09/11/2010   creating
 */
package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class Contact extends DataObject
{
	@FieldOrder(order=0)
	public String name = "";
	
	@FieldOrder(order=1)
	public String phone = "";
}
