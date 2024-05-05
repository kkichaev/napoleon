/*
 * Copyright (C), 2010, ������� �������������
 *
 * Delivery (���������)
 *
 * kki   19/11/2010   creating
 */
package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.types.Scale;

@TableInfo(name="Delivery", keyFields = "id,number")
public class Delivery extends DocDataObject
{
	/**
	 * ���� ��������
	 */
	public Date created;
	
	public Date payDate;
	
	public List<DeliveryItem> items = new ArrayList<DeliveryItem>();
	
	@Scale(value=100)
	public long sumD;
	
	public String number = "";
	public String userid = "";
	public String supplyercode = "";

	public long sum() {
		long result = 0;
		
		if(items != null)
			for(DeliveryItem item: items)
				result += item.sum;
		
		return result;
	}

	public boolean isOverdue() {
		return sumD > 0 && (new Date()).compareTo(payDate) > 0;
	}

	public Map<Integer, Integer> makeTaxEntries() {
		Map<Integer, Integer> nds = new HashMap<Integer, Integer>();
		PriceImpl priceImpl = new PriceImpl();
		Price price = priceImpl.getData();

		for(DeliveryItem oitem: items){
			DeliveryItem sitem = (DeliveryItem)oitem;
			price.id = sitem.id;
			if (priceImpl.read()){

				int tax = price.tax1;
				int isumtax = sitem.taxSum;

				if (tax > 0)
					if (nds.containsKey(tax))
						nds.put(tax, nds.get(tax) + isumtax);
					else
						nds.put(tax, isumtax);


			}
		}
		priceImpl.close();
		return nds;
	}
}
