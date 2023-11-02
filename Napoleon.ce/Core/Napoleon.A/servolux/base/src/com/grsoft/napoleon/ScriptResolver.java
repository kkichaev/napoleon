package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ScriptDefEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;

public class ScriptResolver implements ScriptDefImpl.ScriptResolver {

	String kind;
	
	public ScriptResolver(String kind) {
		this.kind = kind;
	}
	
	@Override
	public List<ScriptDef> getAvailableScripts(String orgId) {
		
		final List<ScriptDef> ret = new ArrayList<ScriptDef>();
		String where = "";
		if(orgId != null && orgId.length() > 0) {
			OrgImpl oi = new OrgImpl();
			OrgEx oe = (OrgEx) oi.getData();
			oe.id = orgId;
			oi.read();
			oi.close();

			where = "([kind] = ''";
			if(kind != null && kind.length() > 0) {
				where +=  " or [kind] = '" + kind + "'";
			}
			where += ")";
			if(oe.idChannel.length() > 0) {
				where += " and ([channel] = '' or [channel] = '" + oe.idChannel + "')";
			}
		}
		
		DataTraveler.travel(ScriptDefEx.class, new DataTraveler.Travel<ScriptDefEx>(true) {

			@Override
			public boolean travel(DataTraveler<ScriptDefEx> item) {
				if(item.data.kind.length() == 0 && ret.size() > 0)
				{
					if(((ScriptDefEx)ret.get(0)).kind.length() > 0)
						return false;
				}
				ret.add(item.data);
				return true;
			}
		}, where, "kind desc");

		Collections.sort(ret, new Comparator<ScriptDef>() {	@Override public int compare(ScriptDef lhs, ScriptDef rhs) { return lhs.name.compareTo(rhs.name); }});
		return ret;
	}


}
