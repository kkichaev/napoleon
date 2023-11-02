package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.Procuration;
import com.grsoft.napoleon.ProcurationEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

import java.util.Date;


public class ProcurationImpl extends CreatableDocument<Procuration> {

	@Override
	public void open(Context context) {
		ProcurationEdit.open(context, getRowid());
	}


	@Override
	public void postInit() {
		super.postInit();
		data.date = new Date(data.date.getTime() + 24 * 3600 * 1000); // add one day
	}
}
