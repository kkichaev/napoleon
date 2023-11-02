package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;

import android.graphics.Typeface;
import android.text.Html;
import android.widget.TextView;

public class DocListEx extends DocList {
	protected void refreshTotalSum(boolean useFilter) {
		TextView tv = (TextView)findViewById(R.id.tvDocSum);
		if( tv != null ) {
			long sum = 0;
			if( countSumFromDocuments(useFilter) ) {
				for( int i=0; i<adapter.getCount(); i++ ) {
					Document<?> d = (Document<?>) adapter.getItem(i);
					sum += getDocSum(d);
				}
			} else
				sum = OrgSumImpl.docSum(DocType.getCurDoc().getName());
			
			tv.setTypeface(Typeface.create(tv.getTypeface(), Typeface.NORMAL));
			String sumstr = DocType.SumConverter.toString(sum);
			String text = Integer.toString(countDocs(adapter))+ "<br><b>" + sumstr + "</b>"; 
			tv.setText(Html.fromHtml(text));
		}
	}
}
