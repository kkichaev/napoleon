package com.grsoft.napoleon;

import java.util.Date;

import android.view.View;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.DatePeriod;

public class NapoleonEx extends Napoleon {
	@Override
	protected void drawOrg(OrgImpl oi, View view) {
		Org o = oi.getData();
		String address = o.address;
		int pos = address.indexOf('\t');
		if( pos >= 0 )
			o.address = address.substring(0, pos);
		
		super.drawOrg(oi, view);
		o.address = address;
	}
	
	@Override
	protected void refreshDocSum(DocType docType) {
		if( docType != OrderDoc.instance() )
			super.refreshDocSum(docType);
		else {
			Date now = new Date();
			Date begin = new Date(now.getYear(), now.getMonth(), now.getDate());
			now.setTime(begin.getTime() + 1000l * 3600 * 24);
			Date end = new Date(now.getYear(), now.getMonth(), now.getDate()); 
			DatePeriod dp = new DatePeriod(begin, end);
			
			int sum = 0, weight = 0;
			DocList docs = OrderDoc.instance().docList(null, null, dp);
			for( int i=0; i<docs.getCount(); i++ ) {
				OrderImpl oi = (OrderImpl)docs.get(i);
				sum += oi.sum();
				weight += oi.weight();
			}
			docs.close();
			updateTotalSum(sum, weight);
		}
	}
}
