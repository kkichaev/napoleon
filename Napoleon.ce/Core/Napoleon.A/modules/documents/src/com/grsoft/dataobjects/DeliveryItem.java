/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * DeliveryItem
 *
 * kki   19/11/2010   creating
 */

package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class DeliveryItem extends DataObject
{
	@FieldOrder(order=0)
	public String id;
	
	@FieldOrder(order=1)
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
	
	@FieldOrder(order=2)
	@Scale(value=Consts.SUM_SCALE)
	public int sum;
}
