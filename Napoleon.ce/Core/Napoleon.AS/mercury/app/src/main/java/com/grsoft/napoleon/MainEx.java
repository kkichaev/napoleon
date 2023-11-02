package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;

import android.content.Context;
import android.content.SharedPreferences;

public class MainEx extends Main {
	
	int totalQty = 0;
	
	@Override
	protected void processDocSumDocument(Document<?> d) {
		if(d instanceof OrderImpl)
			totalQty += ((OrderImpl)d).qty(); 
		if (d instanceof ScriptImpl) {
			ScriptImpl s = (ScriptImpl)d;
			
			for(CreatableDocument<?> i : s.getDocuments())
				processDocSumDocument(i);
		}
	}
	
	@Override
	protected void refreshDocSum(DocType docType) {
		if(OrderDoc.instance() == docType || ScriptDoc.instance() == docType) {
			totalQty = 0;
			OrgSumImpl.periodSum = null;
			SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
			int cur_type = pref.getInt(PERIOD_TYPE, 0);
			if(cur_type > 0){
				docType.updateTotalSum(this, getDocSumByPeriod(docType, cur_type), 0, totalQty);
				return;
			}
		}
		super.refreshDocSum(docType);
	}
}
