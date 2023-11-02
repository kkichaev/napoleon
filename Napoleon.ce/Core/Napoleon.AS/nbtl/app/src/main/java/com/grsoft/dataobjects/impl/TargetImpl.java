package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Target;
import com.grsoft.napoleon.TargetEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class TargetImpl extends CreatableDocument<Target> {

	@Override
	public void open(Context context) {
		TargetEdit.open(context, getRowid());
	}

}
