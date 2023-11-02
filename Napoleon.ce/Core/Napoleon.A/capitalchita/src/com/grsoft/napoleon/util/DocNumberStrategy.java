package com.grsoft.napoleon.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import android.database.Cursor;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.modules.print.util.MakeDocNumberStartegy;
import com.grsoft.util.Util;

public class DocNumberStrategy implements MakeDocNumberStartegy {

	@Override
	public String makeNextDocNumber(DbObject<?> table) {
		AgentPrefix ap = new AgentPrefix();
		String agentTable = DataObjectInfo.getInstance().getTableName(ap.getClass());
		DbReader r = new DbReader(); 
		r.select(ap, agentTable, "id=userid" );
		r.close();

		Date docDate = Util.getDate();
		int count =  getDocsCount(table.getTableName(), docDate);
		String docNumber = makeNumber(ap, count, docDate);
		
		return docNumber;
	}
	
	final static int WAIT_PERCENT = 0;
	final static int READ_TOKEN = 1;
	final static String KOD_TOKEN = "Kod";
	final static String DATE_TOKEN = "Date";

	private String makeNumber(AgentPrefix agent, int count, Date docDate) {
		StringBuilder result = new StringBuilder();
		result.append(agent.prefix);
		result.append(String.format("%02d", docDate.getMonth() + 1));
		result.append(String.format("%02d", docDate.getDate()));
		result.append(String.format("%02d", count+1));
		return result.toString();
	}

	private int getDocsCount(String table, Date now) {
		int count = 0;
		String sql = "select count(*) from [" + table + "] where created >= " + Long.toString(now.getTime());
		
		try {
			Cursor c = DataBaseManager.getDataBase().rawQuery(sql, null);
			if( c.moveToNext() )
				count = c.getInt(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return count;
	}

	@Override
	public void saveDocNumber(String table, String number) {
	}

}

class ParseStream extends StringReader {

	public ParseStream(String str) {
		super(str);
	}

	public String readTill(char sym) {
		String ret = null;
		try {
			int cch = read();
			if( cch != -1 ) {
				StringBuffer sb = new StringBuffer();
				while( cch != sym && cch != -1 ) {
					sb.append((char)cch);
					cch = read();
				}
				ret = sb.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
