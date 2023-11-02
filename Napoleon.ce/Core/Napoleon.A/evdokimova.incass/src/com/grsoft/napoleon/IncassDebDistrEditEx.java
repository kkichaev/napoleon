package com.grsoft.napoleon;

import java.util.Map.Entry;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.IncassDebDistrItem;
import com.grsoft.dataobjects.IncassItemEx;

public class IncassDebDistrEditEx extends IncassDebDistrEdit {
	@Override
	protected IncassDebDistrItem createItem(Entry<DlvKey, Long> e) {
		IncassItemEx ii = new IncassItemEx();
		DlvKeyEx key = (DlvKeyEx) e.getKey();
		
		ii.date = key.date;
		ii.number = key.number;
		ii.sum = (int)((long)e.getValue());
		ii.tag = key.tag;
		return ii;
	}
	
	@Override protected DlvKey createKey(Delivery d) { return new DlvKeyEx(d); }
	@Override protected DlvKey createKey(IncassDebDistrItem item) { return new DlvKeyEx(item); }
	
	class DlvKeyEx extends DlvKey {
		String tag = "";
		
		public DlvKeyEx(Delivery d) {
			super(d);
			tag = ((DeliveryEx)d).tag;
		}
		
		public DlvKeyEx(IncassDebDistrItem item) {
			super(item);
			tag = ((IncassItemEx)item).tag;
		}
	}
}
