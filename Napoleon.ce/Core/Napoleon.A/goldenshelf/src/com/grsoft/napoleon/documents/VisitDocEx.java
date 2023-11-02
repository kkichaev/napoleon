package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.documents.CreateByScriptDef;


public class VisitDocEx extends VisitDoc implements CreateByScriptDef{
	public static DocType instance() {
		if( instance == null )
			instance = new VisitDocEx();
		return instance;
	}
	
	@Override
	public boolean outOfScript() { return true; }

	@Override
	public Document<?> create(ScriptDefItem item) {
		VisitImpl result = (VisitImpl)create();
		((VisitEx)result.getData()).def = item.condParam;
		return result;
	}
}
