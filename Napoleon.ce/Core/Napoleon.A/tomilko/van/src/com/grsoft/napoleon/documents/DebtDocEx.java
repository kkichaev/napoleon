package com.grsoft.napoleon.documents;

import android.app.Activity;

public class DebtDocEx extends com.grsoft.napoleon.modules.print.DebtDoc {
	@Override
	public DocList docList(String orgId, String order, String where) {
		String whereStr = (orgId == null) ? "" : "id='" + orgId + "'";
		
		if( where != null && where.length() > 0 ) {
			if( whereStr.length() > 0 )
				whereStr += " AND ";
			whereStr += where;
		}
		
		return new DebetDocListEx(whereStr, order, LoadDelivery);
	}
	
	@Override public void viewOpened(Activity documentsView) { }	
	@Override public void viewClosed(Activity documentsView) { }

	
}
