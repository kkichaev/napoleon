package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.SmartTaskStart;
import com.grsoft.napoleon.TaskStartEdit;
import com.grsoft.napoleon.documents.CreatableDocument;


public class SmartTaskStartImpl extends CreatableDocument<SmartTaskStart> {

	@Override
	public void open(Context context) { TaskStartEdit.open(context,  getRowid());}

}
