package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.ModifyOrg;
import com.grsoft.napoleon.ChangeFaceEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class ChangeFaceImpl extends CreatableDocument<ModifyOrg> {

	@Override
	public void open(Context context) {
		ChangeFaceEdit.open(context);
	}

}
