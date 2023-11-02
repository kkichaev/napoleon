package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Dispatch;
import com.grsoft.dataobjects.impl.DIncassImpl;

public class DIncassDoc extends DocTypeBase{
	private  static String OBJECT_NAME = "DIncass";
	private static DIncassDoc instance = null;
	
	protected DIncassDoc() {
		super(OBJECT_NAME, OBJECT_NAME, DIncassImpl.class);
	}
	
	public static DocTypeBase instance(){
		if (instance == null)
			instance = new DIncassDoc();
		
		return instance;
	}

	@Override protected int getExportFlag() { return super.getExportFlag() | Dispatch.NOT_READY_TO_SEND;	}
}
