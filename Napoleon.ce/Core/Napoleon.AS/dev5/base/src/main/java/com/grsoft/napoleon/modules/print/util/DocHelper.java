package com.grsoft.napoleon.modules.print.util;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.impl.DbObject;

public class DocHelper {	
	public static MakeDocNumberStartegy makeDocNumberStrategy = new BaseDocNumberStrategy();
	
	public static String makeDocNumber(DbObject<?> dbobj) {
		return makeDocNumberStrategy.makeNextDocNumber(dbobj);
	}

	public static void saveDocNumber(String table, String number){
		makeDocNumberStrategy.saveDocNumber(table,number);
	}
	
	public static long parseDocNumber(String prefix, String number) {
		long result = 1;
		
		if (prefix.length() > 0)
			number = number.substring(prefix.length(), number.length());
						
		if( number.length() > 0 )
			result = Long.parseLong(number.toString());
		
		return result;
	}

	public static String getAgentPrefix(){
		AgentPrefix ap = AgentPrefix.get();
		return ap == null ? "" : ap.prefix;
	}
}

