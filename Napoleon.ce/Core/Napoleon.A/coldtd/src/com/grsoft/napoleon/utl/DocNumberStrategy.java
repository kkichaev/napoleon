package com.grsoft.napoleon.utl;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.database.Cursor;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.SalesImplEx;
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
		String tableName = table.getTableName();
		int count =  getDocsCount(tableName, docDate);
		if( DataObjectInfo.getInstance().getTableName(Sales.class).equals(tableName)) {
			int saveCount = SalesImplEx.getLastNumber();
			if( count < saveCount )
				count = saveCount;
		}
		String docNumber = makeNumber(ap, count, tableName, docDate);
		
		return docNumber;
	}
	
	final static int WAIT_PERCENT = 0;
	final static int READ_TOKEN = 1;
	final static String KOD_TOKEN = "Kod";
	final static String DATE_TOKEN = "Date";

	private String makeNumber(AgentPrefix agent, int count, String tableName, Date docDate) {
		String ret = "";
		int state = WAIT_PERCENT;
		ParseStream st = new ParseStream(agent.prefix);
		while(true) {
			String part = st.readTill('%');
			if( part == null )
				break;
			if(state == WAIT_PERCENT) {
				ret += part;
				state = READ_TOKEN;
			} else {
				if( part.equals(KOD_TOKEN) ) {
					ret += agent.id;
				} else if(part.equals(DATE_TOKEN)) {
					SimpleDateFormat sdf = new SimpleDateFormat("MMdd", Locale.getDefault());
					ret += sdf.format(docDate);
				} else {
					ret += part;
				}
				state = WAIT_PERCENT;
			}
		}
		st.close();
		
		ret += String.format("%02d", count+1);
		return ret;
	}

	private int getDocsCount(String table, Date now) {
		int count = 0;
		long nextDay = now.getTime() + 24 * 3600000;
		String sql = "select max(number) from [" + table + "] where created >= " + Long.toString(now.getTime()) + 
				" and created <" + Long.toString(nextDay);
		
		try {
			Cursor c = DataBaseManager.getDataBase().rawQuery(sql, null);
			if( c.moveToNext() ) {
				String number = c.getString(0);
				count = number.length() > 2 ? Integer.parseInt(number.substring(number.length() - 2)) : 0;
			}
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
