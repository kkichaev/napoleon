package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;


public class NapoleonEx extends Napoleon {
	DatePeriod period;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Calendar c = Calendar.getInstance();
		c.setTime(Util.getDate());
		Date begin = c.getTime();
		c.add(Calendar.DATE, 1);
		
		period = new DatePeriod(begin, c.getTime());
		
	}
	protected void drawOrg(OrgImpl oi, View view) {
		super.drawOrg(oi, view);
		
		if(DocType.getCurDoc() == OrderDoc.instance()){
			Org o = oi.getData();
			DocList orders = OrderDoc.instance().docList(o.id, null, period);
			DocList visits = VisitDoc.instance().docList(o.id, null, period);
			
			if (visits.getCount() > 0 && orders.getCount() == 0)
				((TextView)view.findViewById(R.id.tvOrgName)).setTextColor(Color.MAGENTA); 
		}
	};
}
