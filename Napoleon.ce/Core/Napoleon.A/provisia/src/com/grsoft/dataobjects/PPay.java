package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="PPay", keyFields="created")
public class PPay extends CreateDocDataObject {
	@Scale(value=Consts.SUM_SCALE)
	public int sum;
	
	public List<PPayItem> items;
	
	public boolean haveItem(PaymentEx p) {
		for(PPayItem item : items)
			if( item.date.equals(p.dlvDate) && item.number.equals(p.number))
				return true;
		
		return false;
	}
	
	public void reverseItem(PaymentEx p) {
		for(PPayItem item : items)
			if( item.date.equals(p.dlvDate) && item.number.equals(p.number)) {
				items.remove(item);
				return;
			}
		
		PPayItem i = new PPayItem();
		i.date = p.dlvDate;
		i.number = p.number;
		items.add(i);
	}
}
