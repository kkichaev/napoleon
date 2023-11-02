package com.grsoft.napoleon;

import java.util.Date;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.DatePeriod;
import android.os.Bundle;

public class DocumentsEx extends Documents {
	private boolean blocked = false;
	private static final int BLOCKED_DAYS_CNT = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//blocked = hasNotPayedDelivery(org);
	}

//	public static boolean hasNotPayedDelivery(OrgImpl org) {
//		boolean result = false;
//		com.grsoft.napoleon.documents.DocList dl = DebtDoc.instance().docList(org.getData().id);
//		
//		Date now = new Date();
//		
//		for(Document<?> d : dl){
//			if(d instanceof DeliveryImpl)
//				result = DatePeriod.daysDiff(((DeliveryImpl)d).getData().payDate, now) >= BLOCKED_DAYS_CNT; 
//			
//			if (result)
//				break;
//		}
//		
//		return result;
//	}
	
//	@Override protected boolean isOrgBlocked(Org o, DocType dt) { 
//		return (dt == OrderDoc.instance()) && blocked;	
//	}
//	
//	@Override protected boolean isBlocked() { return blocked; }
}
