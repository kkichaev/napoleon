package com.grsoft.napoleon.modules.print.util;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.FirmImpl;


public class DocNumberStrategy extends BaseDocNumberStrategy{
	public interface ISupplyer{
		String getSupplyer();
	}
	
	@Override
	protected String makePrefix(DbReader r, DbObject<?> obj) {
		StringBuilder result = new StringBuilder();
		
		if(obj instanceof ISupplyer){
			ISupplyer suppl = (ISupplyer) obj;
			FirmImpl f = new FirmImpl();
			
			f.read("id", suppl.getSupplyer());
			
			result.append(((FirmEx)f.getData()).prefix);
			result.append( DocHelper.getAgentPrefix(r));
		}
		
		return result.toString();
	}
}
