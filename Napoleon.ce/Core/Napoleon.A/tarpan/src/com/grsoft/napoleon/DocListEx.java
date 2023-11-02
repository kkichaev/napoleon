package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;

import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

public class DocListEx extends DocList {
	@Override
	protected int getDocStatusResource(CreatableDocument<?> doc) {
		if( doc instanceof OrderImpl ) {
			int params = doc.getData().params;
			if( (params & OrderProceededEx.APPROVED) != 0 )
				return R.drawable.doc_held;
			if( doc.isProceeded() )
				return R.drawable.doc_unheld;
		}
		return super.getDocStatusResource(doc);
	}
	
	@Override
	protected DocListAdapter createListAdapter(DocType docType) {
		Calendar calendar = Calendar.getInstance();
		Date now = Util.getDate();
		calendar.setTime(now);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		saveDatePeriod =  new DatePeriod(now, calendar.getTime());
		saveDatePeriod.periodType = DatePeriod.CREATED;
		return super.createListAdapter(docType);
	}
}
