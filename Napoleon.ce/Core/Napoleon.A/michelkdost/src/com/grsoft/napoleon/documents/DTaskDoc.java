package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Dispatch;
import com.grsoft.dataobjects.impl.DTaskImpl;

public class DTaskDoc extends DocTypeBase{
	private  static String OBJECT_NAME = "DTask";
	private static DTaskDoc instance = null;
	
	protected DTaskDoc() {
		super(OBJECT_NAME, OBJECT_NAME, DTaskImpl.class);
	}
	
	public static DocTypeBase instance(){
		if (instance == null)
			instance = new DTaskDoc();
		
		return instance;
	}

	@Override protected int getExportFlag() { return super.getExportFlag() | Dispatch.NOT_READY_TO_SEND;}
}
