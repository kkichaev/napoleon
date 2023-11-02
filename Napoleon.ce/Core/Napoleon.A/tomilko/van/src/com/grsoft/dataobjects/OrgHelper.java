package com.grsoft.dataobjects;

import java.util.HashSet;

import android.database.Cursor;

import com.grsoft.database.DataBaseManager;
import com.grsoft.napoleon.DebetList;
import com.grsoft.napoleon.DocDebtData;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;

public class OrgHelper {
	static HashSet<String> orgs;
	
	public static void refresh() {
		orgs = null;
	}
	
	public static boolean cantLoad(String id) {
		if( orgs == null )
			loadOrgs();
		
		return orgs.contains(id);
	}
	
	static HashSet<String> loadIds() {
		HashSet<String> ret = new HashSet<String>();

		String org = DataObjectInfo.getInstance().getTableName(Org.class);
		String dlv = DataObjectInfo.getInstance().getTableName(Delivery.class);
		String sls = DataObjectInfo.getInstance().getTableName(Sales.class);
		String pay = DataObjectInfo.getInstance().getTableName(Payment.class);
		String incass = DataObjectInfo.getInstance().getTableName(Incass.class);
		
		String[] tables = {dlv, sls, pay, incass };
		
		for(String table : tables) {
			String sql = "select distinct o.id from " + org + " o, " + table + " d where o.id = d.id";
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
		orgs = new HashSet<String>();
		
		HashSet<String> ref = loadIds();

		DocType dt = DebtDoc.instance();
		for( String id : ref ) {
			
			DocList list = dt.docList(id);
			DebetList dl = new DebetList();
			dl.load(list);
			list.close();
			
			DocDebtData dd = dl.getFirstUnpayed();
			if( dd != null && dd.isOutOfPayLimit() )
				orgs.add(id);			
		}
	}
}
