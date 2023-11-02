package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Dispatch;
import com.grsoft.dataobjects.impl.DispathImpl;

public class DispatchDoc extends DocTypeBase{
	public static String OBJECT_NAME = "Dispatch";
	public static DispatchDoc instance = null;
	
	protected DispatchDoc() {
		super(OBJECT_NAME, OBJECT_NAME, DispathImpl.class);
	}
	
	public static DispatchDoc instance(){
		if (instance == null)
			instance = new DispatchDoc();
		
		return instance;
	}

	@Override protected int getExportFlag() { return super.getExportFlag() | Dispatch.NOT_READY_TO_SEND;	}
}
