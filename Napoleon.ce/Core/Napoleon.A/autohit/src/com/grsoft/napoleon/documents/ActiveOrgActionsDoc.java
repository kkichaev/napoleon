package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.ActiveOrgActionsImpl;
import com.grsoft.napoleon.R;

public class ActiveOrgActionsDoc extends DateDocType {
	static ActiveOrgActionsDoc instance;
	
	public static ActiveOrgActionsDoc instance() {
		if( instance == null )
			instance = new ActiveOrgActionsDoc();
		return instance;
	}
	
	ActiveOrgActionsDoc() {
		super("Акции точки", "ActiveOrgActions", ActiveOrgActionsImpl.class);
	}
	
	@Override public int getResurceId() { return R.drawable.actions_doc; }
}
