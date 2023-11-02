package com.grsoft.util;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DocDataObject;
import com.grsoft.dataobjects.impl.DocNumberStockImpl;
import com.grsoft.napoleon.modules.print.util.BaseDocNumberStrategy;

public class DocNumberHelper {
	public static String geDocNumber(Class<? extends DocDataObject> type){
		DocNumberStockImpl impl = new DocNumberStockImpl();
		int num = impl.nextDocNumber(type.getCanonicalName());
		DbReader r = new DbReader();
		String prefix = com.grsoft.napoleon.modules.print.util.DocHelper.getAgentPrefix(r);
		r.close();
		
		return String.format(BaseDocNumberStrategy.FormatDocStr, prefix, num);
	}
}
