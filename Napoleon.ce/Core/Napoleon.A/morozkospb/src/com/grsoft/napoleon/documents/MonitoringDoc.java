package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.ScriptDefEx;
import com.grsoft.dataobjects.impl.MonitoringImpl;
import com.grsoft.napoleon.R;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.documents.CreateByScriptDef;

public class MonitoringDoc extends DocType implements CreateByScriptDef{
	private static String OBJ_NAME = "Monitoring";
	private static MonitoringDoc instance;
	
	protected MonitoringDoc() {
		super(OBJ_NAME, OBJ_NAME, MonitoringImpl.class);
	}
	
	@Override
	public int getDocTitle() {
		return R.string.monitoring_doc;
	}

	public static DocType instance() {
		if( instance == null )
			instance = new MonitoringDoc();
		return instance;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.monitor_doc;
	}

	@Override
	public Document<?> create(ScriptDef def, ScriptDefItem item) {
		MonitoringImpl res = (MonitoringImpl) create();
		res.getData().suppl = ((ScriptDefEx)def).suppl;
		
		return res;
	}

}
