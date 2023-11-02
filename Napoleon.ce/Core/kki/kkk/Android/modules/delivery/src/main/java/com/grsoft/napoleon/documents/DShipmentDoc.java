package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Dispatch;
import com.grsoft.dataobjects.impl.DShipmentImpl;

public class DShipmentDoc extends DocType{
	private  static String OBJECT_NAME = "DShipment";
	private static DShipmentDoc instance = null;
	
	protected DShipmentDoc() {
		super(OBJECT_NAME, OBJECT_NAME, DShipmentImpl.class);
	}
	
	public static DocTypeBase instance(){
		if (instance == null)
			instance = new DShipmentDoc();
		
		return instance;
	}

	@Override protected int getExportFlag() { return super.getExportFlag() | Dispatch.NOT_READY_TO_SEND;	}
}