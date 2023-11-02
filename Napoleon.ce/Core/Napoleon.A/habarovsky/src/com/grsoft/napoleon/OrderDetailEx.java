package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;

import com.grsoft.dataobjects.Order;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.util.DatePeriod;

public class OrderDetailEx extends OrderDetail {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// проверим, если есть визит, то заявка по плану, иначе нет
		if( doc.isExported() == false ) {
			Order o = doc.getData();
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(o.created);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			Date begin = calendar.getTime();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			DatePeriod dp = new DatePeriod(begin, calendar.getTime());
			dp.periodType = DatePeriod.CREATED;
			
			DocList docs = VisitDoc.instance().docList(o.id, "", dp);
			if( docs.getCount() == 0 )
				o.params |= Order.OUT_OF_PLAN;
			else
				o.params &= (~Order.OUT_OF_PLAN);
			
			doc.write();
		}
	}
	
	@Override
	protected boolean disableSendWithoutFocusedGroup() { return false; }
}
