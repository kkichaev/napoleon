package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.ScriptDefEx;
import com.grsoft.dataobjects.impl.RivalMonitoringImpl;
import com.grsoft.napoleon.R;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.documents.CreateByScriptDef;

public class RivalMntrDoc extends DocType implements CreateByScriptDef {
	private static String OBJ_NAME = "RivalMonitoring";
	private static RivalMntrDoc instance;
	
	protected RivalMntrDoc() {
		super(OBJ_NAME, OBJ_NAME, RivalMonitoringImpl.class);
	}
	
	@Override
	public int getDocTitle() {
		return R.string.rival_doc;
	}

	public static DocType instance() {
		if( instance == null )
			instance = new RivalMntrDoc();
		return instance;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.rival_doc;
	}

	@Override
	public Document<?> create(ScriptDef def, ScriptDefItem item) {
		RivalMonitoringImpl res = (RivalMonitoringImpl) create();
		res.getData().suppl = ((ScriptDefEx)def).suppl;
		
		return res;
	}

}
