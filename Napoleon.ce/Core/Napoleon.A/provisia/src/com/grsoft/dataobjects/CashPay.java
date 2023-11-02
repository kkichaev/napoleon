package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="CashPay", keyFields="created")
public class CashPay extends CreateDocDataObject {
	public String supplier;
	public String number;
	
	@Scale(value=Consts.SUM_SCALE)
	public int sum;
	
	public List<CashPayItem> items;
	
	public int getItemSum(PaymentEx p) {
		int ci = 0;
		
		for(CashPayItem item : items) {
			if(item.date.equals(p.dlvDate) && item.number.equals(p.number)) {
				ci = item.sum;
				break;
			}
		}
		
		return ci;
	}
	
	public void putItemSum(PaymentEx p, int sum) {
		for(CashPayItem item : items) {
			if(item.date.equals(p.dlvDate) && item.number.equals(p.number)) {
				item.sum = sum;
				return;
			}
		}
		
		CashPayItem i = new CashPayItem();
		i.date = p.dlvDate;
		i.number = p.number;
		i.sum = sum;
		items.add(i);
	}
}
