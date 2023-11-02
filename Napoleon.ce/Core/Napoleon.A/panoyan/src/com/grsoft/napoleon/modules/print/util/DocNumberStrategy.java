package com.grsoft.napoleon.modules.print.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.grsoft.dataobjects.DNum;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.DocNumberImpl;
import com.grsoft.util.Util;


public class DocNumberStrategy extends BaseDocNumberStrategy {
	DocNumberImpl docNumber = new DocNumberImpl();
	
	{
		FormatDocStr = "%s%s/%02d";
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("MMdd", Locale.getDefault());
	
	@Override
	public String makeNextDocNumber(DbObject<?> obj) {
		DNum dn = docNumber.getData();
		dn.date = Util.getDate();
		dn.doc = obj.getTableName();
		
		if( !docNumber.read() )
			dn.number = 0;
		
		dn.number++;
		
		String prefix = DocHelper.getAgentPrefix();
//		if(obj != null && obj.getData() instanceof SalesEx) {
//			SalesEx se = (SalesEx) obj.getData();
//			if( se.isBlack > 0 )
//				prefix = "Заказ-" + prefix;
//		}
		return String.format(FormatDocStr, prefix, sdf.format(dn.date),dn.number);
	}
	
	@Override
	public void saveDocNumber(String table, String number) {
		docNumber.write();
		docNumber.close();
	}
}
