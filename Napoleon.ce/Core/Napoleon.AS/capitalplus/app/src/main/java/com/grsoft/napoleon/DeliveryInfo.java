package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.Document;

public class DeliveryInfo{
	public int sum = 0; 
	public int count = 0;
	public boolean hasExceed = false;
	public List<Delivery> list;
	
	public static DeliveryInfo collectDelivery(String id) {

		DeliveryInfo result = new DeliveryInfo();
		result.list = new ArrayList<>();

		Calendar c = Calendar.getInstance();
		Date now = c.getTime();
		com.grsoft.napoleon.documents.DocList dl = DebtDoc.instance().docList(id);
		
		for(Document<?> d : dl){
			if(d instanceof DeliveryImpl){
				Delivery e = (Delivery) d.getData().clone();
				
				if(e.sumD > 0){
					result.sum += e.sumD;
					result.count += 1;

					if(!result.hasExceed && e.payDate.compareTo(now) < 0)
						result.hasExceed = true;

					if (e.payDate.compareTo(now) < 0)
						result.list.add(e);
				}
			}
		}
		
		return result;
	}
}