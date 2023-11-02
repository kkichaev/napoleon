package com.grsoft.napoleon.modules.print.util;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.impl.DbObject;

public class BaseDocNumberStrategy implements MakeDocNumberStartegy{
	public static String FormatDocStr = "%s%04d";
	
	@Override
	public String makeNextDocNumber(DbObject<?> obj) {
		String table = obj.getTableName();
		DbReader r = new DbReader();
		
		String prefix = makePrefix(r, obj);

		long num = getStartNumber(obj);
		DocNumber ri = new DocNumber();
		ri.setPrefix(prefix);
		boolean bdo = r.select(ri, table, null, "created desc");

		while( bdo ) {
			adjustNumber(ri);
			
			if( ri.number.length() == 0 ) {
				bdo = r.selectNext(ri);
				continue;
			}
			try {
				num = DocHelper.parseDocNumber(prefix, ri.number);
				num++;
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		
		r.close();
		
		return buildNewNumber(table, prefix, num);
	}

	protected String buildNewNumber(String table, String prefix, long num) {
		String number;
		boolean exs = true;
		SQLiteDatabase db = DataBaseManager.getDataBase();
		SQLiteStatement stm = db.compileStatement("SELECT COUNT(rowid) FROM "+table+" WHERE number=?");
		
		do{
			number = buildNumber(prefix, num);
			num++;
			stm.bindString(1, number);
			exs = stm.simpleQueryForLong() > 0;
		}while(exs);
		
		stm.close();
		return number;
	}

	protected long getStartNumber(DbObject<?> obj) { return 1;	}

	protected String buildNumber(String prefix, long num) {
		return String.format(FormatDocStr, prefix, num);
	}

	protected String makePrefix(DbReader r, DbObject<?> doc) {
		return DocHelper.getAgentPrefix();
	}

	protected void adjustNumber(DocNumber ri) { }

	@Override
	public void saveDocNumber(String table, String number) {}
	
	
}