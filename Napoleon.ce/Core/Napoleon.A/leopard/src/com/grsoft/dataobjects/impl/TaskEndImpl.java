package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.TaskEnd;
import com.grsoft.napoleon.SVTaskEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

public class TaskEndImpl extends CreatableDocument<TaskEnd> {

	@Override
	public void open(Context context) {
		SVTaskEdit.open(context, getData().created.getTime(), true, getId());
	}

}
