package com.grsoft.dataobjects;

import java.util.Date;
import java.util.List;

public class IncassEx extends Incass {
	public String dovNumber;
	public Date dovDate = new Date();
	
	public List<IncassItem> items;

	public int getItemSum(PaymentEx p) {
		int ci = 0;
		
		for(IncassItem item : items) {
			if(item.date.equals(p.date) && item.number.equals(p.number)) {
				ci = item.sum;
				break;
			}
		}
		
		return ci;
	}
	
	public void putItemSum(PaymentEx p, int sum) {
		for(IncassItem item : items) {
			if(item.date.equals(p.date) && item.number.equals(p.number)) {
				item.sum = sum;
				return;
			}
		}
		
		IncassItem i = new IncassItem();
		i.date = p.date;
		i.number = p.number;
		i.sum = sum;
		items.add(i);
	}
}
