package com.grsoft.napoleon;

import java.util.Date;

import com.grsoft.dataobjects.impl.SalesBanImpl;
import com.grsoft.dataobjects.impl.ServerInfoObjectImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.util.Util;

import android.widget.Toast;

public class Documents2Ex extends DocumentsEx {
	@Override
	protected void doCreate() {
		if (DocType.getCurDoc() == SalesDoc.instance()) {
			if (SalesBanImpl.isOrgBanned(org.getData().id)) {
				Toast.makeText(this, R.string.sales_ban, Toast.LENGTH_SHORT).show();
				
				return;
			}
		}
		
		ServerInfoObjectImpl si = new ServerInfoObjectImpl();
		
		Date d = si.getValidDate();
		if (DocType.getCurDoc() == SalesDoc.instance() && (
				d == null || (Util.getDate().getTime() < Util.resetTime(d).getTime())))
			Toast.makeText(this, R.string.invalid_date, Toast.LENGTH_SHORT).show();
		else
			super.doCreate();
	}
}
