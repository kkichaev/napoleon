package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.IncassDebDistrEx;
import com.grsoft.napoleon.IncassDebDistrEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

public class IncassDebDistrImplEx extends CreatableDocument<IncassDebDistrEx> {
	@Override
	public void open(Context context) {
		IncassDebDistrEdit.open(context, this);
	}

	@Override public long sum() { return data.sum;	}
	
	@Override
	public String getDescription(Context context) {
		if( data.docNumber.length() > 0 )
			return data.docNumber;
		return super.getDescription(context);
	}
}
