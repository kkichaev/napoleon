/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * OrderItem
 *
 * kki   25/10/2010   creating
 */
package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

/**
 * В порожденных объектах надо проставлять FieldOrder!!! 
 * При изменении числа полей обязательно проверять порожденные объекты
 * @author 1111
 *
 */
public class OrderItem extends DataObject implements QtyItem {
	/**
	 * пользовательские флаги начинаем с 0x100 
	 */
	static public final int IN_PACK = 1;
	
	/**
	 * Это первый индекс для порожденных классов
	 */
	static public final int USER_FIELDS = 10;
	
	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	@Scale(value=Consts.SUM_SCALE)
	public long cost;
	
	@FieldOrder(order=2)
	@Scale(value=1)
	public int flags;

	@FieldOrder(order=3)
	@Scale(value=Consts.QTY_SCALE)
	public int qty;

	@FieldOrder(order = 4)
	public String unit = "";

	@FieldOrder(order=5)
	@FieldVersion(version = 2)
	@Scale(value=Consts.QTY_SCALE)
	public int qtyPack = 0;

	@Override public int getQty() { return qty; }
	@Override public int getFlags() { return flags; }
	
	public boolean inPack() { return ((flags & IN_PACK) != 0); }
}
