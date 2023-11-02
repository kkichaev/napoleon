package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl.ScriptResolver;

public class AMGScriptResolver implements ScriptResolver {

	@Override
	public List<ScriptDef> getAvailableScripts(String orgId) {
		String where = "";
		if(orgId != null && orgId.length() > 0) {
			OrgPropImpl prop = new OrgPropImpl();
			
			if (prop.read("id", orgId)) {
				where = "id=" + Integer.toString(prop.getData().script); 
			}
		}
		
		List<ScriptDef> scripts = new ArrayList<ScriptDef>();

		DbReader r = new DbReader();
		ScriptDef sd = new ScriptDef();
		String table = DataObjectInfo.getInstance().getTableName(ScriptDef.class);
		DbWriter.checkDBTable(ScriptDef.class);
		
		
		boolean ret = r.select(sd, table, where);
		
		while( ret ) {
			scripts.add(sd);
			sd = new ScriptDef();
			ret = r.selectNext(sd);
		}
		
		r.close();
		
		Collections.sort(scripts, new Comparator<ScriptDef>() {	@Override public int compare(ScriptDef lhs, ScriptDef rhs) { return lhs.name.compareTo(rhs.name); }});
		
		return scripts;
	}

}
