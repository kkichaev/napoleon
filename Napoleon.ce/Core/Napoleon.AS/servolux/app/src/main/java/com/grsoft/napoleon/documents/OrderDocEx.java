package com.grsoft.napoleon.documents;

import java.util.HashSet;

import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.network.DocExportListener;

public class OrderDocEx extends OrderDoc {
	HashSet<String> disabledFirms;
	
	public OrderDocEx(Class<OrderImplEx> docClass) {
		super("Заявки", "Order", docClass);
	}

	public static void init(Class<OrderImplEx> docClass) {
		instance = new OrderDocEx(docClass);
	}

	public void setDiabledFirms(HashSet<String> firms) {
		disabledFirms = firms;
	}
	
	@Override
	public DocExportListener getDirtyDocuments() {
		DocExportListener ret =  super.getDirtyDocuments();
		if( disabledFirms == null || disabledFirms.size() == 0 )
			return ret;
		
		return new DocSendListner(ret.getObjectName(), new FiltredOrderList(disabledFirms, ret.getDocuments()));
	}
}
