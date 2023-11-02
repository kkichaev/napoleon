package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.SmartTaskStartImpl;
import com.grsoft.napoleon.R;


public class SmartTaskStartDoc extends DocType {
	static public final String DOC_NAME = "SmartTaskStart";
	static public final String OBJ_NAME = "SmartTaskStart";
	static protected SmartTaskStartDoc instance = null;
	
	protected SmartTaskStartDoc() {
		super(DOC_NAME, OBJ_NAME, SmartTaskStartImpl.class);
	}

	static public DocType instance() {
		if( instance == null )
			instance = new SmartTaskStartDoc();
		return instance;
	}
	
	@Override
	public int getDocTitle() { return R.string.smart_task_start_doc_title; }
	
	@Override
	public int getResurceId() {	return R.drawable.taskdoc; }
}
