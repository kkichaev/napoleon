package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.modules.print.util.BaseDocNumberStrategy;


public class DocNumberStrategy extends BaseDocNumberStrategy {
	@Override
	protected String buildNumber(String prefix, long num) {
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		
		if(cfg.getValue(sb, "ПоследнийНомерПродажи")){
			try{
				long cn = Long.parseLong(sb.toString());
				
				if(cn > num)
					num = ++cn;
			}catch(Exception e){}
		}
		
		return super.buildNumber(prefix, num);
	}
}
