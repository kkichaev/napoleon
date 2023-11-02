package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.DocExportListener;

public class NapoleonEx extends Napoleon{
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
				
				if(s > 0 && oi.read("id", ord.getId())){
					int os = ((OrgEx)oi.getData()).minSum;
					
					if(os > 0 && s < os){
						haveBadOrder = true;
						break;
					}
				}
			}
			
			if(haveBadOrder)
				OrderListEx.openOrdList(this);
		}
	}
}
