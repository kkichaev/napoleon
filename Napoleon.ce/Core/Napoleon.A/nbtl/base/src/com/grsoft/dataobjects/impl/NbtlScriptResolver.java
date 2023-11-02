package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.napoleon.NapoleonAppBase;
import com.grsoft.napoleon.NapoleonEx;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl.ScriptResolver;

import android.content.Context;
import android.content.SharedPreferences;

public class NbtlScriptResolver implements ScriptResolver {

	@Override
	public List<ScriptDef> getAvailableScripts(String orgId) {
		List<ScriptDef> res = new ArrayList<ScriptDef>();
		
		SharedPreferences sp = NapoleonAppBase.context.getSharedPreferences(NapoleonEx.CUR_ROTE, Context.MODE_PRIVATE);
		String sc = sp.getString(NapoleonEx.AVAIL_SCRIPTS, "");
		if( sc.length() > 0 ) {
			
			for(String id : sc.split(",")) {
				ScriptDefImpl sdi = new ScriptDefImpl();
				ScriptDef sd = sdi.getData();
				sd.id = Integer.parseInt(id);
				if( sdi.read() ) {
					res.add(sd);
				}
				sdi.close();
			}
		}else {
			DbReader r = new DbReader();
			ScriptDef sd = new ScriptDef();
			String table = DataObjectInfo.getInstance().getTableName(ScriptDef.class);
			DbWriter.checkDBTable(ScriptDef.class);
			
			boolean ret = r.select(sd, table, "");
			
			while( ret ) {
				res.add(sd);
				sd = new ScriptDef();
				ret = r.selectNext(sd);
			}
			
			r.close();
			
			Collections.sort(res, new Comparator<ScriptDef>() {	@Override public int compare(ScriptDef lhs, ScriptDef rhs) { return lhs.name.compareTo(rhs.name); }});
		}
			
		
		return res;
	}

}
