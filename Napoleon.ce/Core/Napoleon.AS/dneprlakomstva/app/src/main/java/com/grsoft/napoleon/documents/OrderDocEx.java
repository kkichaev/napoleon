package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.HashMap;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.OrgUtils;
import com.grsoft.network.DocExportListener;

public class OrderDocEx extends OrderDoc {
	
	public OrderDocEx(Class<OrderImplEx> doc) {
		super("Заявки", "Order", doc);
	}

	public static void initialize() {
		instance = new OrderDocEx(OrderImplEx.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DocExportListener getDirtyDocuments() {
		CreatableDocument<?> cd = (CreatableDocument<?>)create();
		DocExportListener dl =  new DocSendListner(getObjectName(), 
				(Class<? extends CreatableDocument<?>>) cd.getClass(), 
				"params", ParamState.ofExported);
		
		ArrayList<Long> needRemove = new ArrayList<Long>();
		HashMap<String, Long> sums = new HashMap<String, Long>();
		DocList docs = dl.getDocuments();
		for(Document<?> d : docs) {
			OrderImplEx doc = (OrderImplEx) d;
			String id = doc.getId();
			Long sum = sums.get(id);
			if( sum == null ) {
				sum = OrgUtils.getOutDebt(id);
				sums.put(id, sum);
			}
			if( doc.isEmpty() || !doc.isGood(sum)) {
				needRemove.add(doc.getRowid());
				doc.delete();
			}
		}
		docs.removeDocuments(needRemove);
		docs.close();
		return dl;
	}
}
