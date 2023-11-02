package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.PPay;
import com.grsoft.napoleon.PPayEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

public class PPayImpl extends CreatableDocument<PPay> {

	@Override
	public void open(Context context) {
		PPayEdit.open(context, this);
	}
	
	@Override
	public int sum() { return data.sum; }

}
