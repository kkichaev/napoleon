package com.grsoft.napoleon.documents;

import java.util.ArrayList;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.network.DocExportListener;

public class OrderDocEx extends OrderDoc {
	public static void init() {
		instance = new OrderDocEx();
	}
	
	OrderDocEx() {
		super("Заявки", "Order", OrderImplEx.class);
	}
	
	@Override
	public DocExportListener getDirtyDocuments() {
		DocExportListener dl =  new DocSendListner(getObjectName(), OrderImplEx.class, "params", ParamState.ofExported);
		
		ArrayList<Long> needRemove = new ArrayList<Long>();
		DocList docs = dl.getDocuments();
		for(Document<?> d : docs) {
			if( ((OrderImplEx)d).isValid() == false ) {
				needRemove.add(d.getRowid());
			}
		}
		docs.removeDocuments(needRemove);
		docs.close();

		return dl;
	}
}
