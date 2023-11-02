package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.DatePeriod;
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
	
	protected DocListAdapter createListAdapter(DocType docType){
		return new DocListAdapter(this, docType, saveDatePeriod, R.layout.docs_list_row2ex);
	}
	
	@Override
	protected void drawData(View view, Document<?> doc, int position) {
		super.drawData(view, doc, position);
		
		TextView tvPodRemark = (TextView) view.findViewById(R.id.tvPodRemark);
		
		if(doc instanceof CreatableDocument<?>){
			tvPodRemark.setText(((CreatableDocument<?>)doc).getPodRemark());
			tvPodRemark.setVisibility(View.VISIBLE);
		}else{
			tvPodRemark.setText("");
			tvPodRemark.setVisibility(View.GONE);
		}
		
	}
}
