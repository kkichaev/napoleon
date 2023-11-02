package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.TaskBegin;
import com.grsoft.napoleon.SVTaskEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

public class TaskBeginImpl extends CreatableDocument<TaskBegin>{

	@Override
	public void open(Context context) {
		SVTaskEdit.open(context, getData().created.getTime(), false, getId());
	}

}
