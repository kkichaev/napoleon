package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.IncassDebDistr;
import com.grsoft.napoleon.IncassDebDistrEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

public class IncassDebDistrImpl extends CreatableDocument<IncassDebDistr> {

	@Override
	public void open(Context context) {
		IncassDebDistrEdit.open(context, this);
	}

	@Override public long sum() { return data.sum;	}
}
