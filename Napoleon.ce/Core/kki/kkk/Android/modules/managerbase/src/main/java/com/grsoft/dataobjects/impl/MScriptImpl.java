package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.script.dataobjects.Script;

public class MScriptImpl extends CreatableDocument<Script> {

	@Override
	public void open(Context context) {
		// TODO Auto-generated method stub

	}

	@Override
	public long sum() {
		return data.sum;
	}
}
