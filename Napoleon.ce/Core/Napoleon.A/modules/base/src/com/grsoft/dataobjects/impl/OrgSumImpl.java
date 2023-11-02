package com.grsoft.dataobjects.impl;

import java.util.HashSet;
import java.util.Map;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrgSum;

public class OrgSumImpl extends DbObject<OrgSum> {
	
	/**
	 * Сумма всех документов определенного типа
	 * @param type
	 * @return
	 */
	public static long docSum(String type) { return docSum(type, null); }
	
	public static long docSum(String type, HashSet<String> ids) {
		DbWriter.checkDBTable(OrgSum.class);
		String table = DataObjectInfo.getInstance().getTableName(OrgSum.class);
		String stmt = String.format("SELECT sum([sum]) FROM '%s' WHERE type = '%s' ", table, type);
		
		if( ids != null && ids.size() != 0) {
			stmt += " AND id in (";
			StringBuilder sb = new StringBuilder();
			for(String id : ids) {
				sb.append("'").append(id).append("',");
			}
			sb.deleteCharAt(sb.length() - 1);
			stmt += sb.toString() + ")";
		}
		SQLiteDatabase database = DataBaseManager.getDataBase();
		SQLiteStatement sum = database.compileStatement(stmt);

		long res = 0;
		
		try {
			res = (long) sum.simpleQueryForLong();
		} catch (Exception e) {}
		
		sum.close();
		
		return res;
		
	}
	
	public static Map<String, Long> periodSum = null;
}