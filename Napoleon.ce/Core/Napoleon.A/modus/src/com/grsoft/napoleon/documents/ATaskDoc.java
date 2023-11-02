package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ATaskImpl;
import com.grsoft.network.DocExportListener;


public class ATaskDoc extends DocType {
	private static final String OBJ_NAME = "ATask";
	private static DocType instance;
	
	protected ATaskDoc() {
		super(OBJ_NAME, OBJ_NAME, ATaskImpl.class);
	}

	public static DocTypeBase instance() {
		if(instance == null)
			instance = new ATaskDoc();
		
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DocExportListener getDirtyDocuments() {
		Document<?> d = create();

		if (d instanceof CreatableDocument) {
			StringBuilder where = new StringBuilder();
			where.append("(([params] & ").append(ParamState.ofExported).append(" ) == 0)").append(" and manager=0");
			
			return new DocSendListner(getObjectName(), (Class<? extends CreatableDocument<?>>) d.getClass(),where.toString());
		} else
			return null;
	}
}
