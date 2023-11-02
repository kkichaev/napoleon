package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.PhoneCall;
import com.grsoft.napoleon.PhoneCallEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.PhoneCallDoc;

import android.content.Context;

public class PhoneCallImpl extends CreatableDocument<PhoneCall> {

	@Override public void open(Context context) { PhoneCallEdit.open(context, this); }

	public long write() {
		PhoneCallDoc.instance().refreshDocSum(data.id);
		return super.write();
	}
	
	public void addAction() {
		data.actions ++;
		write();
	}
	
	public boolean isEmpty() {
		return data.actions == 0;
	}
}
