package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Incass;
import com.grsoft.napoleon.documents.CreatableDocument;

public class MIncassImpl extends CreatableDocument<Incass> {

	@Override
	public void open(Context context) {
		// TODO Auto-generated method stub
		
	}

	@Override public long sum() { return data.sum; }
}
