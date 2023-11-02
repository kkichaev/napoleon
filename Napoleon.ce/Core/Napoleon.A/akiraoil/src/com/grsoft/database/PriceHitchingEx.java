package com.grsoft.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.grsoft.dataobjects.CostItem;
import com.grsoft.dataobjects.CostItemEx;
import com.grsoft.dataobjects.Price;

public class PriceHitchingEx extends PriceHitching {
	@Override
	protected void beforeInsert(Price dobj) {
		super.beforeInsert(dobj);
		
		List<CostItemEx> cs = new ArrayList<CostItemEx>();
		for(CostItem c : dobj.cost) {
			cs.add((CostItemEx) c);
		}
		Collections.sort(cs);
		dobj.cost.clear();
		
		int index = 0;
		for(CostItemEx ce : cs) {
			while(ce.ctype > index) {
				CostItemEx newItem = new CostItemEx();
				newItem.ctype = index;
				dobj.cost.add(newItem);
				index++;
			}
			dobj.cost.add(ce);
			index++;
		}
	}
}
