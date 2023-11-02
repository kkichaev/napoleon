package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.HandledDocuments;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.modules.print.util.DocHelper;

public class SalesImplEx extends SalesImpl {
	
	@Override
	public String getDescription(Context context) {
		String num = super.getDescription(context); 
		HandledDocuments.loadCache();
		String exNum = HandledDocuments.getNumber(SalesDoc.OBJ_NAME, data.created);
		
		if( exNum.length() >  0) {
			num += " / " + exNum; 
		}
		return num;
	}
	
	@Override
	public void initDocNumber() {
		super.initDocNumber();
		String num = HandledDocuments.getLastNum(SalesDoc.OBJ_NAME);
		if( num.compareTo(data.number) >= 0 ) {
			try {
				String prefix = DocHelper.getAgentPrefix();
				long newNum = DocHelper.parseDocNumber(prefix, num);
				newNum++;
				data.number = String.format("%s%04d", prefix, newNum);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean isEditable() {
		return ((((SalesEx)data).printCount == 0) || super.isEditable());
	}
		
	@Override
	public boolean delete() {
		if( ((SalesEx)data).printCount > 0 && isExported() && data.items != null && data.items.size() > 0 )
			return false;
		
		return super.delete();
	}
}
