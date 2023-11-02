package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.MoneyProxyImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.DocExportListener;

public class MoneyProxyDoc extends DocType {
	static public final String DOC_NAME = "Доверенности";
	static public final String OBJ_NAME = "Proxy";
	static private MoneyProxyDoc instance = null;

	protected MoneyProxyDoc() { super(DOC_NAME, MoneyProxyImpl.class);} 
	
	static public DocType instance() {
		if( instance == null )
			instance = new MoneyProxyDoc();
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DocExportListener getDirtyDocuments() {
		CreatableDocument<?> d = (CreatableDocument<?>)create();
		return new DocSendListner(OBJ_NAME, 
				(Class<? extends CreatableDocument<?>>) d.getClass(), 
				"params", ParamState.ofExported);
	}

	@Override
	public int getResurceId() {
		return R.drawable.money_proxy;
	}
	
	@Override
	public boolean outOfScript() {
		return true;
	}
}
