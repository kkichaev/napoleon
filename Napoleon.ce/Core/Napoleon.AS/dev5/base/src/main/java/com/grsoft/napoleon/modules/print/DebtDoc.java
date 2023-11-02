package com.grsoft.napoleon.modules.print;
import com.grsoft.aceteam.R;

import com.grsoft.napoleon.documents.DocList;

public class DebtDoc extends com.grsoft.napoleon.documents.DebtDoc {
	
	public static Class<? extends 
			com.grsoft.napoleon.documents.DebtDoc> DebtDocType = DebtDoc.class;
	
	public static void init() {
		if( instance == null )
			try{
				instance = DebtDocType.newInstance();
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	@Override
	public DocList docList(String orgId, String order, String where) {
		String whereStr = (orgId == null) ? "" : "id='" + orgId + "'";
		if( where != null && where.length() > 0 ) {
			if( whereStr.length() > 0  )
				whereStr += " AND ";
			whereStr += where;
		}
		
		return createDebtDocList(whereStr, order, LoadDelivery);
	}
}
