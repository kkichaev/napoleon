/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Order
 *
 * kki   25/10/2010   creating
 */
package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Feature;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="orders", keyFields = "created")
@ServerInfo(name="Order")
public class Order extends CreateDocDataObject
{
	public final static int OUT_OF_PLAN = 0x40000;
	/**
	 * Код организаии из конфигурации
	 */
	@Scale(value=1)
	public int supplyer;
	
	/**
	 * Код цены из конфигурации
	 */
	@Scale(value=1)
	public int sumType;
	
	/**
	 * Содержание заявки
	 * 
	 */
	public List<OrderItem> items = new ArrayList<OrderItem>();
	
	/**
	 * Задержка в днях
	 */
	@Scale(value=1)
	public int delay;
		
	/**
	 * Номер накладной, если была
	 */
	public String number = "";

	public List<OrderFocusedFolder> focusedFolders = new ArrayList<OrderFocusedFolder>();
	
	public List<OrderFocusedItem> focusedItems = new ArrayList<OrderFocusedItem>();

	/**
	 * код адреса доставки
	 */
	@Feature(feature="DELIVERY_ADDRESS")
	public String adrCode = "";	
	
	public String firmCode = "";
	public String prcType = "";
	
	public OrderItem findItem(String id) {
		if(items != null) {
			for(OrderItem oi : items) {
				if( oi.id.compareTo(id) == 0 )
					return oi;
			}
		}
		return null;
	}
	
	public long sum() {
		long res = 0;
		
		if(items != null) {
			for (OrderItem oi: items)
				res += ((long)oi.cost * oi.qty + Consts.QTY_SCALE/2) / Consts.QTY_SCALE;
		}
		
		return res;
	}
}
