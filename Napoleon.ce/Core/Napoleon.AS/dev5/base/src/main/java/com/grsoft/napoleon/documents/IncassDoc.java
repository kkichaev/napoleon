package com.grsoft.napoleon.documents;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.IncassDebDistrImpl;
import com.grsoft.dataobjects.impl.IncassImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.aceteam.R;
import com.grsoft.network.DocExportListener;


public class IncassDoc extends DocType {

	static public final String DOC_NAME = "Инкассация";
	static public final String OBJ_NAME = "Incass";
	static protected IncassDoc instance = null;
	
	/**
	 * Для переопределения при инициализации программы
	 */
	protected IncassDoc() { 
		super(DOC_NAME, OBJ_NAME, Features.INCASS_DEBET_DISTRIB ? IncassDebDistrImpl.class : IncassImpl.class);
	} 
	
	protected IncassDoc(String name, String objName, Class<? extends Document<?>> docClass) {
		super(name, objName, docClass);
	}

	static public DocType instance() {
		if( instance == null )
			instance = new IncassDoc();
		return instance;
	}
	
	static public DocType instance(Class<? extends Document<?>> type) {
		if( instance == null )
			instance = new IncassDoc(DOC_NAME, OBJ_NAME, type);
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DocExportListener getDirtyDocuments() {
		CreatableDocument<?> d = (CreatableDocument<?>)create();
		return new DocSendListner(objName, 
				(Class<? extends CreatableDocument<?>>) d.getClass(), 
				"params", ParamState.ofExported);
	}

	@Override public int getResurceId() { 
		return R.drawable.incass_doc; 
	}
	
	@Override public int getResurce2Id() { 
		return R.drawable.incass_doc_2;
	}
	
	@Override
	public int getDocTitle() {
		return R.string.incas_doc_title;
	}
	
	@Override
	public boolean outOfScript() { return false; }
}
