package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.DiscountObj;
import com.grsoft.napoleon.documents.Document;

public class DiscountImpl extends Document<DiscountObj> {

	@Override
	public void open(Context context) {
	}

	@Override
	public String getDescription(Context context) {
		String str = "";
//		for( int i=0; i< data.level; i++)
//			str += "&nbsp;";
		str += data.folder;
		return str;
	}
	
	@Override
	public long sum() {
		return data.discount;
	}
}
