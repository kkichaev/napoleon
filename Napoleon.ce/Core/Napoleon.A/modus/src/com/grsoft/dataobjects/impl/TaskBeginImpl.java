package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.TaskBegin;
import com.grsoft.napoleon.TaskViewer;
import com.grsoft.napoleon.documents.CreatableDocument;


public class TaskBeginImpl extends CreatableDocument<TaskBegin> {

	@Override
	public void open(Context context) { TaskViewer.open(context, getRowid());}

}
