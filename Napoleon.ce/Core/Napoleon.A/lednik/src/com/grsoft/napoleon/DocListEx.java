package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;

public class DocListEx extends DocList {
	@Override
	protected void refreshTotalSum(boolean useFilter) {
		if(DocType.getCurDoc() instanceof OrderDoc){
			
			int sum = 0;
			int weight = 0;

			for( int i=0; i<adapter.getCount(); i++ ) {
				Document<?> d = (Document<?>) adapter.getItem(i);
				sum += getDocSum(d);
				weight += ((OrderImplBase<?>)d).weight();
			}
			
			DocType.getCurDoc().updateTotalSum(this, sum, weight, 0, R.id.tvDocSum);
			
		}else 
			super.refreshTotalSum(useFilter);
			
	}
}
