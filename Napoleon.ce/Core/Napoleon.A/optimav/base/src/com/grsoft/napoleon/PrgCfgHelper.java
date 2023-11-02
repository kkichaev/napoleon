package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class PrgCfgHelper {
	private static final String MAX_DOC_SUM = "МаксСумма";
	
	public static int getMaxDocSum(){
		int result = 0;
		
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();

		if(cfg.getValue(sb, MAX_DOC_SUM))
			result = Util.StrToScale(sb.toString(), Consts.SUM_SCALE);
		
		return result;
	}
}
