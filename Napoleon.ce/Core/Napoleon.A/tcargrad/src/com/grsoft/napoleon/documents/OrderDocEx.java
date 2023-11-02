package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.CostTypes;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.network.DocExportListener;

public class OrderDocEx extends OrderDoc {
	
	protected OrderDocEx() {
		super("Заявки", "Order", OrderImplEx.class);
	}
	
	public static void initialize() {
		instance = new OrderDocEx();
	}
	
	@Override
	public DocExportListener getDirtyDocuments() {
		return new DocSendListner(getObjectName(), new EmptyDocList(docClass));
	}
	
	void addHistoryData(List<SalesHistoryData> list, SalesHistoryData data) {
		for(SalesHistoryData i : list) {
			if( i.date.equals(data.date) && i.taxName.equals(data.taxName)) {
				i.qty += data.qty;
				return;
			}
		}
		
		list.add(data);
	}
	
	public List<SalesHistoryData> getHistoryData(String orgId, String priceId) {
		List<SalesHistoryData> ret = new ArrayList<SalesHistoryData>();
		
		HashMap<String, String> costTypes = new HashMap<String, String>();
		CostTypes ct = new CostTypes();
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(ct.getClass());
		boolean bdo = r.select(ct, table, "");
		while( bdo ) {
			costTypes.put(ct.id, ct.name);
			bdo = r.selectNext(ct);
		}
		r.close();
		
		DocList list = docList(orgId);
		for( int i=0; i<list.getCount(); i++ ) {
			OrderImplEx doc = (OrderImplEx)list.get(i);
			if( doc != null ) {
				OrderEx o = (OrderEx) doc.getData();
				for (DataObject dataObject: o.items) {
					OrderItemEx orderItem = (OrderItemEx) dataObject;
					
					if (orderItem.id.equals(priceId)) {
						Date maskDate = new Date(o.date.getYear(), o.date.getMonth(), o.date.getDate());
						int qty = orderItem.qty;
						String tax = costTypes.get(orderItem.taxType);
						if( tax == null )
							tax = orderItem.taxType;
						
						addHistoryData(ret, new SalesHistoryData(maskDate, qty, tax));
					}
				}
				
			}
		}
		
		list.close();
		Collections.sort(ret);
		
		return ret;
	}
}
