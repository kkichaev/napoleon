package com.grsoft.dataobjects;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.napoleon.DebetList;
import com.grsoft.napoleon.DocDebtData;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

import android.database.Cursor;

public class OrgHelper {
	static Map<String, Integer> orgs;
	
	public static void refresh() {
		orgs = null;
	}
	
	public static int getUnpayDays(String id) {
		if( orgs == null )
			loadOrgs();
		
		int res = -1;
		
		if (orgs.containsKey(id))
			res = orgs.get(id);
		
		return res; 
	}
	
	static HashSet<String> loadIds() {
		HashSet<String> ret = new HashSet<String>();

		DbWriter.checkDBTable(Org.class);
		DbWriter.checkDBTable(Delivery.class);
		DbWriter.checkDBTable(Sales.class);
		DbWriter.checkDBTable(Payment.class);
		DbWriter.checkDBTable(Incass.class);
		
		String org = DataObjectInfo.getInstance().getTableName(Org.class);
		String dlv = DataObjectInfo.getInstance().getTableName(Delivery.class);
		String sls = DataObjectInfo.getInstance().getTableName(Sales.class);
		String pay = DataObjectInfo.getInstance().getTableName(Payment.class);
		String incass = DataObjectInfo.getInstance().getTableName(Incass.class);
		
		String[] tables = {dlv, sls, pay, incass };
		
		for(String table : tables) {
			String sql = "select distinct o.id from " + org + " o, " + table + " d where o.id = d.id or o.ido = d.ido ";
			try {
				Cursor c = DataBaseManager.getDataBase().rawQuery(sql, null);
				
				while( c.moveToNext() )
					ret.add(c.getString(0));

				c.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return ret;
	}

	private static void loadOrgs() {
		orgs = new HashMap<String, Integer>();
		
		HashSet<String> ref = loadIds();

		DocType dt = DebtDoc.instance();
		for( String id : ref ) {
			
			DocList list = dt.docList(id);
			DebetList dl = new DebetList();
			dl.load(list);
			list.close();
			
			Date now = Util.getDate();
			
			DocDebtData dd = dl.getFirstUnpayed();
			if( dd != null && dd.sumD > 0) {
				int d = (int) DatePeriod.daysDiff(dd.payDate, now);
				
				if (d > 0)
					orgs.put(id, d);
			}
		}
	}
}
