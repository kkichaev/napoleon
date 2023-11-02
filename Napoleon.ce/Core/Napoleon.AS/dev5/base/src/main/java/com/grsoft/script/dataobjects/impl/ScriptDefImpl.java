package com.grsoft.script.dataobjects.impl;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.script.dataobjects.ScriptDef;

public class ScriptDefImpl extends DbObject<ScriptDef> {
	public static String OBJECT_NAME = "ScriptDef";

	/**
	 * Документы которые показываются вместе со сценарием
	 */
	public static List<DocTypeBase> docInScript = new ArrayList<DocTypeBase>();
	
	public interface ScriptResolver {
		List<ScriptDef> getAvailableScripts(String orgId);
	}
	
	public static ScriptResolver resolver = null;
	
	private static Boolean isScriptOn = null;	
	public static void setCanScripting(boolean canScripting) { isScriptOn = canScripting; }
	public static void setCanScriptingOff() { isScriptOn = null; }
	
	public static boolean canScripting() {
		if( isScriptOn != null )
			return isScriptOn;
		
		boolean result = false;
		
//		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
//		if (!cfg.scriptOff){
			ConfigImpl c = new ConfigImpl();
			c.getData().key = "AllowScripting";
			c.close();
			boolean noScript = false;
			if(!Features.NO_SCRIPT_CONFIG)
				noScript = (c.read() == false || Integer.parseInt(c.getData().value) == 0);
			
			if( !noScript ){
				List<ScriptDef> sd = getAvailableScripts("");
				result = (sd.size() > 0);
			}
//		}
		
		return result;
	}
	
	public static List<ScriptDef> getAvailableScripts(String orgId) {
		if(resolver != null)
			return resolver.getAvailableScripts(orgId);
		
		List<ScriptDef> scripts = new ArrayList<ScriptDef>();

		DbReader r = new DbReader();
		ScriptDef sd = new ScriptDef();
		String table = DataObjectInfo.getInstance().getTableName(ScriptDef.class);
		DbWriter.checkDBTable(ScriptDef.class);
		
		boolean ret = r.select(sd, table, "");
		
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
