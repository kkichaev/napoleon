package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.ExistItem;
import com.grsoft.dataobjects.ExistMatrix;
import com.grsoft.dataobjects.ExistOut;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.ExistOutEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class ExistOutImpl extends CreatableDocument<ExistOut> {

	@Override public void open(Context context) { ExistOutEdit.open(context, this); }
	
	@Override
	public void postInit() {
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx) oi.getData();
		oe.id = data.id;
		oi.read();
		oi.close();
		
		for(ExistMatrix em : oe.matrixExist) {
			ExistItem i = new ExistItem(em);
			data.items.add(i);
		}
	}

}
