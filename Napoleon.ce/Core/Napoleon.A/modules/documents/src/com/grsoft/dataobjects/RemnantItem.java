/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * RemnantsItems(Остатки - подпункты)
 *
 * kki   11/04/2011   creating
 */
package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class RemnantItem extends DataObject implements QtyItem {
	
	/**
	 * Код из прайса
	 */
	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	@Scale(value=Consts.QTY_SCALE)
	public int qty;

	@Override public int getQty() { return qty; }
	@Override public int getFlags() { return 0; } 
}
