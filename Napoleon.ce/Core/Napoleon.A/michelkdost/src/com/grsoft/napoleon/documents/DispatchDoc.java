package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Dispatch;
import com.grsoft.dataobjects.impl.DispatchImpl;

public class DispatchDoc extends DocTypeBase{
	private static String OBJECT_NAME = "Dispatch";
	private static DispatchDoc instance = null;
	
	protected DispatchDoc() {
		super(OBJECT_NAME, OBJECT_NAME, DispatchImpl.class);
	}
	
	public static DispatchDoc instance(){
		if (instance == null)
			instance = new DispatchDoc();
		
		return instance;
	}

	@Override protected int getExportFlag() { return super.getExportFlag() | Dispatch.NOT_READY_TO_SEND;	}
}
