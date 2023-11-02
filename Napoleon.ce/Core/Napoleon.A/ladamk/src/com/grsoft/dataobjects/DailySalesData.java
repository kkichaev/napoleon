package com.grsoft.dataobjects;

import java.util.Date;
import java.util.HashMap;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.FolderTree;
import com.grsoft.util.Util;

public class DailySalesData extends HashMap<Date, FolderSalesData>{
	private static final long serialVersionUID = 1L;

	void loadDoc(Order doc, PriceMap price, FolderTree folders) {
		Date d = Util.getDayStart(doc.created);
		FolderSalesData fd = super.get(d);
		if(fd == null) {
			fd = new FolderSalesData();
			super.put(d, fd);
		}
		fd.load(doc, price, folders);
	}
	
	public long countTotal(DailyPlan doc) {
		long ret = 0;
		
		Date d = Util.getDayStart(doc.date);
		FolderSalesData fd = super.get(d);
		if(fd != null)
			ret = fd.countTotal(doc);
		return ret;
	}
	
	/**
	 * 
	 * @param created - null по всему периоду
	 * @return
	 */
	public static DailySalesData load(String orgId, Date created) {
		PriceMap price = new PriceMap();
		FolderTree folders = new FolderTree();
		folders.load();
		
		DailySalesData ret = new DailySalesData();
		
		String where = "";
		if(created != null) {
			where += "created >= " + Long.toString(Util.getDayStart(created).getTime()) + " and created <= " + Long.toString(Util.getDayEnd(created).getTime());
		}
		
		DocList dl = OrderDoc.instance().docList(orgId, "", where);
		for(Document<?> doc : dl) {
			Order o  = ((OrderImpl)doc).getData();
			ret.loadDoc(o, price, folders);
		}
		dl.close();
		price.close();
		return ret;
	}
}
