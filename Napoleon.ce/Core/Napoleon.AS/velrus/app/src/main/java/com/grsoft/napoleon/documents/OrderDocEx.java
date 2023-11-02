package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.network.DocExportListener;


public class OrderDocEx extends OrderDoc {
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("OrderDoc уже создан!");
		instance = new OrderDocEx();
	}
	
	private OrderDocEx() {
		super("Заявки", "Order", OrderImplEx.class);
	}

	@Override
	public DocExportListener getDirtyDocuments() {
		DocExportListener result =  super.getDirtyDocuments();
		
		ArrayList<Long> needRemove = new ArrayList<Long>();
		DocList docs = result.getDocuments();
		for(Document<?> d : docs) {
			OrderEx o = (OrderEx) d.getData();
			if( o.notcomplete > 0 ) {
				needRemove.add(d.getRowid());
			}
		}
		docs.removeDocuments(needRemove);
		docs.close();
		
		return result;
	}
}
