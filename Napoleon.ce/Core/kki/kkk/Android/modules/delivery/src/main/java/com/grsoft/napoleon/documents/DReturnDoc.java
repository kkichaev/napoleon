package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Dispatch;
import com.grsoft.dataobjects.impl.DReturnImpl;

public class DReturnDoc extends DocType{
	private  static String OBJECT_NAME = "DReturn";
	private static DReturnDoc instance = null;
	
	protected DReturnDoc() {
		super(OBJECT_NAME, OBJECT_NAME, DReturnImpl.class);
	}
	
	public static DocTypeBase instance(){
		if (instance == null)
			instance = new DReturnDoc();
		
		return instance;
	}

	@Override protected int getExportFlag() { return super.getExportFlag() | Dispatch.NOT_READY_TO_SEND;	}
}