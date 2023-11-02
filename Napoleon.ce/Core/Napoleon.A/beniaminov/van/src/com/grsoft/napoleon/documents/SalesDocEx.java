package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.Date;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.SalesFake;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.Features;
import com.grsoft.network.DocExportListener;

public class SalesDocEx extends SalesDoc {
	public static void initilize(Class<SalesImplEx> doc){
		instance = new SalesDocEx(doc);
	}
	
	public SalesDocEx(Class<SalesImplEx> doc) {
		super(doc);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DocExportListener getDirtyDocuments() {
		SalesFake f = SalesFake.getInstance(null);
		long bt = 0;
		Date buddy = ((SalesEx)f.getData()).buddy;
		
		if(buddy != null)
			bt = buddy.getTime();
		
		String where = String.format("(([params] & 1) == 0) and [created] > 0 and [created] != %d", bt);
		
		CreatableDocument<?> cd = (CreatableDocument<?>)create();
		DocExportListener dl =  new DocSendListner(getObjectName(), 
				(Class<? extends CreatableDocument<?>>) cd.getClass(), where);
		
		if( Features.REMOVE_EMPTY_ORDERS ){
			ArrayList<Long> needRemove = new ArrayList<Long>();
			DocList docs = dl.getDocuments();
			for(Document<?> d : docs) {
				OrderImplBase<? extends Order> doc = (OrderImplBase<? extends Order>) d;
				if( doc.isEmpty() ) {
					needRemove.add(doc.getRowid());
				}
			}
			docs.removeDocuments(needRemove);
			docs.close();
		}
		
		return dl;
	}
}
