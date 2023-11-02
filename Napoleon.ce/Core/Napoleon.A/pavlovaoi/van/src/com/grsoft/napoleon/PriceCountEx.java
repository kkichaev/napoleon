package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;
import android.widget.Toast;


public class PriceCountEx extends PriceCount {
	@Override
	protected boolean isInputValid(Runnable r) {
		boolean result = true;
		
		if(priceVal <= 0 && DocType.getCurDoc() == SalesDoc.instance()){
			result = false;
			Toast.makeText(this, R.string.sales_null_cost_reject, Toast.LENGTH_SHORT).show();
		}
			
		return result;
	}
}
