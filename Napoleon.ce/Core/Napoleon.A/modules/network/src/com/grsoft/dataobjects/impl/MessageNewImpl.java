package com.grsoft.dataobjects.impl;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.MessageNew;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;


public class MessageNewImpl extends DbObject<MessageNew> {
	
	public boolean hasUnread(){
		boolean result = false;
		checkDBTable();
		SQLiteDatabase db = DataBaseManager.getDataBase();
		SQLiteStatement stm = null;
		
		try{
			stm = db.compileStatement("select count(*) from [" + getTableName() + "] where read = 0");
			result = stm.simpleQueryForLong() > 0;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(stm != null)
				stm.close();
		}
		
		return result;
	}
}
