package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.SmartTaskEndImpl;
import com.grsoft.napoleon.R;


public class SmartTaskEndDoc extends DocType {
	static public final String DOC_NAME = "SmartTaskEnd";
	static public final String OBJ_NAME = "SmartTaskEnd";
	static protected SmartTaskEndDoc instance = null;
	
	protected SmartTaskEndDoc() {
		super(DOC_NAME, OBJ_NAME, SmartTaskEndImpl.class);
	}

	static public DocType instance() {
		if( instance == null )
			instance = new SmartTaskEndDoc();
		return instance;
	}
	
	@Override
	public int getDocTitle() { return R.string.smart_task_start_doc_title; }
	
	@Override
	public int getResurceId() {	return R.drawable.taskdoc; }
}
