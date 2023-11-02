package com.grsoft.napoleon.documents;

import java.util.ArrayList;

import com.grsoft.dataobjects.OrderImplEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.network.DocExportListener;

public class OrderDocEx extends OrderDoc {
	protected OrderDocEx() { super("Заявки", "Order", OrderImplEx.class);} 
	
	public static void init() {
		instance = new OrderDocEx();
	}

	@Override
	public DocExportListener getDirtyDocuments() {
		return new OrderDocSendListener();
	}
	
	class OrderDocSendListener extends DocSendListner{
		public OrderDocSendListener() {
			super("Order", OrderImplEx.class, "params", ParamState.ofExported);
			
			OrderImplEx order = new OrderImplEx();
			OrgImpl org = new OrgImpl();
			
			ArrayList<Long> idsFilter = new ArrayList<Long>();
			
			for(long rowid : list.ids){
				order.read(rowid);
				org.getData().id = order.getData().id;
				
				int minWeight = ((OrgEx)org.getData()).minWeight;
				
				if(minWeight == 0 || order.weight() > minWeight)
					idsFilter.add(rowid);
				
			}
			
			order.close();
			org.close();
			
			list.ids = idsFilter;
		}
	}
}
