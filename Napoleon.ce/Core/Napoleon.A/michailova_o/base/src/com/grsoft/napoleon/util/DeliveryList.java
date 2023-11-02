package com.grsoft.napoleon.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DeliveryItem;

public class DeliveryList {
	
	static DeliveryList cached;
	
	String orgId;
	List<DeliveryEx> deliveries = new ArrayList<DeliveryEx>();
	
	public static DeliveryList open(String orgId) {
		if( cached != null && cached.orgId.equals(orgId) )
			return cached;
		
		cached = new DeliveryList();
		cached.load(orgId);
		return cached;
	}
	
	public static void clear() {
		cached = null;
	}
	
	public String getId() { return orgId; }
	
	public HashSet<String> getSaledItems() {
		HashSet<String> ret = new HashSet<String>();
		for( DeliveryEx d : deliveries ) {
			for(DeliveryItem i : d.items)
				ret.add(i.id);
		}
		return ret;
	}
	
	public List<DeliveryEx> getDocuments(String itemId) {
		List<DeliveryEx> ret = new ArrayList<DeliveryEx>();
		
		for( DeliveryEx d : deliveries ) {
			if(d.findItem(itemId) != null)
				ret.add(d);
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @param docDate
	 * @param docNumber если null то суммируем кол-во по всем документам
	 * @param itemId
	 * @return кол-во товара в документе или во всех
	 */
	public int getItemQty(Date docDate, String docNumber, String itemId) {
		int qty = 0;

		for( DeliveryEx d : deliveries ) {
			if( docNumber == null ) {
				DeliveryItem item = d.findItem(itemId);
				if(item != null)
					qty += item.qty;
			} else if(d.date.equals(docDate) && d.number.equals(docNumber)) {
				DeliveryItem item = d.findItem(itemId);
				if(item != null)
					qty = item.qty;
				break;
			}
		}
		return qty;
	}

	private void load(String orgId) {
		this.orgId = orgId;
		
		String table = DataObjectInfo.getInstance().getTableName(DeliveryEx.class);
		String where = "id='" + orgId + "'";
		
		DbReader r = new DbReader();
		DeliveryEx data = new DeliveryEx();
		
		boolean bdo = r.select(data, table, where);
		while(bdo) {
			deliveries.add(data);
			data = new DeliveryEx();
			bdo = r.selectNext(data);
		}
		
		r.close();
	}
}
