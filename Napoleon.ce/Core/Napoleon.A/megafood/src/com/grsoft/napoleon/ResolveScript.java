package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgTypeBinding;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;

public class ResolveScript implements ScriptDefImpl.ScriptResolver {

	@Override
	public List<ScriptDef> getAvailableScripts(String orgId) {
		final List<ScriptDef> ret = new ArrayList<ScriptDef>();
		String where = "";
		if(orgId != null && orgId.length() != 0) {
			String type = OrgTypeBinding.getType(orgId);
			where =  "[type] = '' or [type] = '" + type + "'";
		}
		
		DataTraveler.travel(ScriptDef.class, new DataTraveler.Travel<ScriptDef>(true) {

			@Override
			public boolean travel(DataTraveler<ScriptDef> item) {
				ret.add(item.data);
				return true;
			}
		}, where);

		Collections.sort(ret, new Comparator<ScriptDef>() {	@Override public int compare(ScriptDef lhs, ScriptDef rhs) { return lhs.name.compareTo(rhs.name); }});
		return ret;
	}

}
