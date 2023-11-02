package com.grsoft.dataobjects;

import java.util.HashMap;

import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;

public class FolderSalesData extends HashMap<String, Long> {
	private static final long serialVersionUID = 1L;

	public long get(Folder f) {
		Long ret = super.get(f.fid);
		if(ret == null)
			ret = (long)0;
		return ret;
	}
	
	public void load(Order doc, PriceMap price, FolderTree folders) {
		for(OrderItem i : doc.items) {
			Price p = price.get(i.id);
			
			Folder f = folders.getFolder(p.folderID);
			if( f != null) {
				long weight = (long)i.qty * p.weight / Consts.QTY_SCALE;
				Long cur = get(f.fid);
				if(cur == null)
					cur = (long)0;
				cur += weight;
				put(f.fid, cur);
			}
		}
	}

	public long countTotal(DailyPlan doc) {
		long ret = 0;
		for(DailyPlanItem i : doc.items) {
			Long val = get(i.id);
			if(val != null)
				ret += val;
		}
		return ret;
	}
}
