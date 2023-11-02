/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Delivery (Накладная)
 *
 * kki   19/11/2010   creating
 */
package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;

@TableInfo(name="Delivery", keyFields = "id,number")
public class Delivery extends DocDataObject
{
	/**
	 * Дата создания
	 */
	public Date created;
	
	public Date payDate;
	
	public List<DeliveryItem> items = new ArrayList<DeliveryItem>();
	
	@Scale(value=100)
	public long sumD;
	
	public String number = "";
	public String userid = "";
	
	public long sum() {
		long result = 0;
		
		if(items != null)
			for(DeliveryItem item: items)
				result += item.sum;
		
		return result;
	}
}
