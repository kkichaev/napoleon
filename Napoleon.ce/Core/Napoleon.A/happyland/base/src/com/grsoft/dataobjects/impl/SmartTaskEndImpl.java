package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.SmartTaskEnd;
import com.grsoft.napoleon.TaskEndEdit;
import com.grsoft.napoleon.documents.CreatableDocument;


public class SmartTaskEndImpl extends CreatableDocument<SmartTaskEnd> {

	@Override
	public void open(Context context) { TaskEndEdit.open(context, getRowid());}

}
