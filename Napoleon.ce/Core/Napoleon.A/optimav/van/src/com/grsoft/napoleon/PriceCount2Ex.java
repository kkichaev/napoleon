package com.grsoft.napoleon;

import android.widget.Toast;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;


public class PriceCount2Ex extends PriceCountEx {
	@Override
	protected boolean isInputValid(Runnable r) {
		long sum = getSumValue() + document.sum();
		int maxsum = PrgCfgHelper.getMaxDocSum();
		if(DocType.getCurDoc() == SalesDoc.instance() &&  sum > 0 && maxsum > 0 && maxsum <= sum)
			return false;
		else
			return super.isInputValid(r);
	}
	
	@Override
	protected void invalidInputValueHandler() {
		long sum = getSumValue() + document.sum();
		
		if(sum > 0 && PrgCfgHelper.getMaxDocSum() <= sum)
			Toast.makeText(this, R.string.sum_doc_exceed, Toast.LENGTH_SHORT).show();
	}
}
