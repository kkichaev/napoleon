package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.Procuration;
import com.grsoft.napoleon.ProcurationEdit;
import com.grsoft.napoleon.documents.CreatableDocument;


public class ProcurationImpl extends CreatableDocument<Procuration> {

	@Override
	public void open(Context context) {
		ProcurationEdit.open(context, getRowid());
	}

}
