package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgProp;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl.ScriptResolver;

public class MorozkoScriptResolver implements ScriptResolver {

	@Override
	public List<ScriptDef> getAvailableScripts(String orgId) {
		String where = "";
		
		if (orgId.trim().length() > 0) {
			final StringBuilder sb = new StringBuilder();
			
			DataTraveler.travel(OrgProp.class, new DataTraveler.Travel<OrgProp>() {

				@Override
				public boolean travel(DataTraveler<OrgProp> item) {
					if(sb.length() > 0)
						sb.append(",");
					sb.append("'");
					sb.append(item.data.suppl);
					sb.append("'");
					
					return true;
				}
			}, String.format("\"id\"='%s'", orgId));
			
			if (sb.length() > 0)
				where = String.format("\"suppl\" in (%s)", sb.toString());
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
