package com.grsoft.database;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.OrgBalance;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class OrgBalanceHitching extends Hitching {
	
	protected SQLiteStatement statement;
	HashMap<String, Long> orgBalance = new HashMap<String, Long>();

	public OrgBalanceHitching() {
		super(OrgBalance.class, "OrgBalance");
	}
	
	@Override
	public void onStart() {
		try{
			String dlvTable = DataObjectInfo.getInstance().getTableName(Delivery.class);
			SQLiteDatabase database = DataBaseManager.getDataBase();
			database.execSQL("UPDATE " + dlvTable + " set sumd=0");
			statement = database.compileStatement("UPDATE " + dlvTable + " SET sumd = ? WHERE number=?");
		}catch(Exception e){}
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		OrgBalance dobj = (OrgBalance)rawObject.createDataObject(dataObject);
		orgBalance.put(dobj.id, dobj.sum);

		if (statement != null && dobj.num.length() > 0){
			statement.clearBindings();
			statement.bindLong(1, dobj.sumd);
			statement.bindString(2, dobj.num);
			
			try{
				statement.execute();
			}catch(Exception e){}
		}
	}
	
	@Override
	public void onEnd() {
		if (statement != null)
			statement.close();
		
		((DebtDocEx)DebtDoc.instance()).updateFromCache(orgBalance);
	}
}
