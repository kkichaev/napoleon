package com.grsoft.napoleon.modules.print.util;

import java.text.SimpleDateFormat;
import java.util.Locale;
import android.annotation.SuppressLint;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DNum;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.DocNumberImpl;
import com.grsoft.util.Util;


@SuppressLint("SimpleDateFormat")
public class DocNumberStrategy extends BaseDocNumberStrategy {
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	
	DocNumberImpl docNumber = new DocNumberImpl();
	
	{
		FormatDocStr = "%s%s/%02d";
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("MMdd", Locale.getDefault());
	
	@Override
	public String makeNextDocNumber(DbObject<?> obj) {
		DbWriter.checkDBTable(DNum.class);
		
		DNum dn = docNumber.getData();
		dn.date = Util.getDate();
		dn.datestr = dateFormat.format(Util.getDateTime());
		dn.doc = obj.getTableName();
		
		if( !docNumber.read() )
			dn.number = 0;
		
		dn.number++;
		
		return String.format(FormatDocStr, DocHelper.getAgentPrefix(),sdf.format(dn.date),dn.number);
	}
	
	@Override
	public void saveDocNumber(String table, String number) {
		docNumber.write();
		docNumber.close();
	}
}
