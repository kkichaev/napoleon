package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.documents.CreateByScriptDef;


public class VisitDocEx extends VisitDoc implements CreateByScriptDef{
	
	public static DocType initilize() {
		if( instance == null )
			instance = new VisitDocEx();
		return instance;
	}
	
	protected VisitDocEx() {
		super(DOC_NAME, OBJ_NAME, VisitImplEx.class);
	}
	
	@Override
	public boolean outOfScript() { return true; }

	@Override
	public Document<?> create(ScriptDef def,  ScriptDefItem item) {
		VisitImpl result = (VisitImpl)create();
		((VisitEx)result.getData()).def = item.condParam;
		return result;
	}
}
