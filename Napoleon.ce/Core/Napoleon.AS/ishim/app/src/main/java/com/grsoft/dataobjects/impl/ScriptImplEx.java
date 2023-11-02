package com.grsoft.dataobjects.impl;

import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.impl.ScriptImpl;

import android.content.Context;

public class ScriptImplEx extends ScriptImpl {
	@Override
	protected void scriptListProcess(Context context, String orgid) {
		super.scriptListProcess(context, orgid);
		
		boolean setDefaultScript = true;
		
		OrgPropImpl prop = new OrgPropImpl();
		
		if (prop.read("id", orgid)) {
			int sc = prop.getData().script;
			
			
			if (sc != 0) {
				for(ScriptDef d : scripts) {
					if (d.id == sc) {
						scripts.clear();
						scripts.add(d);
						setDefaultScript = false;
						break;
					}
				}
			}
		}
		
		
		if (setDefaultScript) {
			StringBuilder sb = new StringBuilder();
			ConfigImpl cfg = new ConfigImpl();
			if (cfg.getValue(sb, "DefaultScript")) {
				try {
					int id = Integer.parseInt(sb.toString());
					
					for(ScriptDef d : scripts) {
						if (d.id == id) {
							scripts.clear();
							scripts.add(d);
							break;
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
