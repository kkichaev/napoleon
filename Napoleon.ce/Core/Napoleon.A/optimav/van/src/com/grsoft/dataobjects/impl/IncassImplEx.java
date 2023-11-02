package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.HandledDocuments;
import com.grsoft.napoleon.documents.IncassDoc;

public class IncassImplEx extends IncassImpl {
	@Override
	public String getDescription(Context context) {
		String num = super.getDescription(context); 
		HandledDocuments.loadCache();
		String exNum = HandledDocuments.getNumber(IncassDoc.OBJ_NAME, data.created);
		
		if( exNum.length() >  0) {
			num += " / " + exNum; 
		}
		return num;
	}
}
