package com.grsoft.dataobjects;

import com.grsoft.util.Util;

public class DeliveryEx extends Delivery {
//	public Date payDate;
	
	public DeliveryItem findItem(String id) {
		for(DeliveryItem i : items)
			if(i.id.equals(id))
				return i;
		
		return null;
	}
	
	@Override
	public String toString() {
		return "№ " + number + " от " + Util.simpleDateFormat.format(date);
	}
}
