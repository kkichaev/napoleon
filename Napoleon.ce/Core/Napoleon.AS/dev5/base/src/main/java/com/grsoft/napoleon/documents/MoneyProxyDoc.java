package com.grsoft.napoleon.documents;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.MoneyProxyImpl;
import com.grsoft.aceteam.R;
import com.grsoft.network.DocExportListener;

public class MoneyProxyDoc extends DocType {
	static public final String DOC_NAME = "Доверенность";
	static public String OBJ_NAME = "Proxy";
	static private MoneyProxyDoc instance = null;

	protected MoneyProxyDoc() { super(DOC_NAME, OBJ_NAME, MoneyProxyImpl.class);}
	
	static public DocType instance() {
		if( instance == null )
			instance = new MoneyProxyDoc();
		return instance;
	}

	@Override public int getResurceId() {
		return R.drawable.money_proxy;
	}
	@Override public int getResurce2Id() {
		return R.drawable.money_proxy_2;
	}

	@Override
	public boolean outOfScript() {
		return true;
	}
}
