package com.grsoft.napoleon.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Agent;
import com.grsoft.dataobjects.DataObjectInfo;

@SuppressWarnings("serial")
public class ConfigImpl2Ex extends ConfigImplEx{
	public String userid = "";
	public String userlogin = "";
	public String userpassword = "";
	
	public String getSuperId() {
		String result = "";
		final String ID_CLMN = "id";
		
		SQLiteDatabase db = DataBaseManager.getDataBase();
		try{
			Cursor c = db.query(DataObjectInfo.getInstance().getTableName(Agent.class), 
					new String[]{ID_CLMN}, "login = ? and password = ?", 
					new String[]{login, passw}, null, null, null);
			
			if (c.moveToFirst())
				result = c.getString(c.getColumnIndex(ID_CLMN));
			
			c.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
}
