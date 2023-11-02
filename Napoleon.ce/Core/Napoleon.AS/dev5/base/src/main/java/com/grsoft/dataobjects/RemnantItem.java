/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * RemnantsItems(Остатки - подпункты)
 *
 * kki   11/04/2011   creating
 */
package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class RemnantItem extends DataObject {
	
	/**
	 * Код из прайса
	 */
	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	@Scale(value=Consts.QTY_SCALE)
	public int qty; 
}
