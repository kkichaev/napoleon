package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.RequestChek;
import com.grsoft.napoleon.RequestChekEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class RequestChekImpl extends CreatableDocument<RequestChek> {

	@Override public void open(Context context) { RequestChekEdit.Open(context, this); }

	@Override public long sum() { return data.sum; }
	
	public boolean canCreateReturnChek() {
		return isExported() && data.handleStatus == 1;
	}

	@Override
	public String getDescription(Context context) {
		String text = data.getStatus();
		if(text.length() > 0)
			return text;
		return super.getDescription(context);
	}
}
