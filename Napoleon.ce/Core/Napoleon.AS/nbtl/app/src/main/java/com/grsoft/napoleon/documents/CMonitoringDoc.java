package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.CMonitoringImpl;
import com.grsoft.napoleon.R;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.documents.CreateByScriptDef;


public class CMonitoringDoc extends DateDocType implements CreateByScriptDef{
	static private CMonitoringDoc instance = null;
	
	private static final String DOC_NAME = "Мониторинг";
	private static final String OBJ_NAME = "CMonitoring";
	
	public static DocType instance() {
		if( instance == null )
			instance = new CMonitoringDoc();
		return instance;
	}
	
	protected CMonitoringDoc() {
		super(DOC_NAME, OBJ_NAME, CMonitoringImpl.class);
	}

	@Override
	public Document<?> create(ScriptDef def,  ScriptDefItem item) {
		CMonitoringImpl result = (CMonitoringImpl)create();
		result.getData().def = item.condParam;
		return result;
	}

	@Override
	public int getDocTitle() { return R.string.monitoring_doc_title; }
	
	@Override
	public int getResurceId() { return R.drawable.monitor_doc ;}

	@Override
	public int getResurce2Id() {
		return R.drawable.monitor_doc_2;
	}
}
