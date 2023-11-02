package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.Util;

public class DocListEx extends DocList {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Date begin = Util.getDate();
		Calendar c = Calendar.getInstance();
		c.setTime(begin);
		c.add(Calendar.DAY_OF_MONTH, 1);
		Date end = c.getTime();
		DatePeriod dp = new DatePeriod(begin, end);
		dp.periodType = DatePeriod.CREATED;
		applyFilter(dp, null, null);
	}
	
	@Override
	protected void drawData(View view, Document<?> doc, int position) {
		super.drawData(view, doc, position);
	
		TextView tv = (TextView) view.findViewById(R.id.tvOrder);
		tv.setText(Integer.toString(position+1));
	}
	
	@Override
	protected DocListAdapter createListAdapter(DocType docType) {
		return new DocListAdapter(this, docType, saveDatePeriod, R.layout.docs_list_row_ex);
	}
	
	@Override
	protected DocFilterOnClickListener createDocListFilter() {
		return new DocFilterOnClickListener(this, true, false){
			@Override
			protected void initData(boolean creatableFilter) {
				super.initData(creatableFilter);
				if( !data.contains(WSOrderDoc.instance()) )
					data.add(WSOrderDoc.instance());
			}
		};
	}
}
