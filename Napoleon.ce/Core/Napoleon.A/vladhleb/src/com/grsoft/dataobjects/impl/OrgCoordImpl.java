package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.OrgCoord;
import com.grsoft.napoleon.DocumentsBase;
import com.grsoft.napoleon.documents.CreatableDocument;

public class OrgCoordImpl extends CreatableDocument<OrgCoord> {

	@Override
	public void open(Context context) {
		if(context instanceof DocumentsBase)
			((DocumentsBase)(context)).doGPSScan();
	}

}
