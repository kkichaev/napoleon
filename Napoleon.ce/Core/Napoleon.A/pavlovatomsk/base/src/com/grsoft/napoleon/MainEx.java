package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.DocExportListener;

public class MainEx extends Main{
	
	@Override
	protected void onResume() {
		super.onResume();
		
		DocExportListener toExp = OrderDoc.instance().getDirtyDocuments();
		
		if(toExp != null){
			boolean haveBadOrder = false;
			OrgImpl oi = new OrgImpl();
			
			DocList ords = toExp.getDocuments();
			
			for(Document<?> d : ords){
				OrderImpl ord = (OrderImpl)d;
				
				long s = ord.sum();
				boolean b = ((OrderEx)ord.getData()).bonus == 1;
				
				if(oi.read("id", ord.getId())) {
					DeliveryInfo deliveryInfo = DeliveryInfo.collectDelivery(oi.getData().id);
					
					if(!b && s > 0){
						int os = ((OrgEx)oi.getData()).minSum;
						
						if(os > 0 && s < os){
							haveBadOrder = true;
							break;
						}
					}
					
					if(!b && s + deliveryInfo.sum >= ((OrgEx)oi.getData()).limitsum) {
						haveBadOrder = true;
						break;
					}
				}
			}
			
			oi.close();
			if(haveBadOrder) 
				OrderListM.openOrdList(this);
		}
	}
}
