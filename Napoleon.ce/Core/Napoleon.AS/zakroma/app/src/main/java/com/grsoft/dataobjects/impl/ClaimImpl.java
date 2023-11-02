package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Claim;
import com.grsoft.napoleon.ClaimEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class ClaimImpl extends CreatableDocument<Claim> {

	@Override
	public void open(Context context) {
		ClaimEdit.open(context, this);
	}

}
