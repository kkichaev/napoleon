package com.grsoft.napoleon.plans.documents;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.plans.dataobjects.impl.PlanImpl;
import com.grsoft.util.DatePeriod;

public class PlanDoc extends DocType{
	static public final String DOC_NAME = "Планы";
	static public final String OBJ_NAME = "Plan";
	private static PlanDoc instance;
	
	protected PlanDoc() {
		super(OBJ_NAME, PlanImpl.class);
	}

	public static PlanDoc instance() {
		if( instance == null )
			instance = new PlanDoc();
		return instance;
	}
	
	public void refreshFact(){
		DocList docList = docList(null);
		
		for(int i = 0; i < docList.getCount(); i++){
			PlanImpl planImpl = (PlanImpl) docList.get(i);
			
			DatePeriod datePeriod = new DatePeriod(planImpl.getData().from, planImpl.getData().till); 
			DocList orderDocList = OrderDoc.instance().docList(null, null, datePeriod);
			int sum = 0;
			
			for(int y = 0; y < orderDocList.getCount(); y++){
				OrderImpl orderImpl = (OrderImpl) orderDocList.get(y);
				sum += orderImpl.sum();
				orderImpl.close();
			}
			
			planImpl.getData().fact = sum;
			planImpl.write();
			planImpl.close();
		}
	}
}
