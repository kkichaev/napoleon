package com.grsoft.dataobjects.impl;
import com.grsoft.aceteam.R;

import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Present;

public class PresentImpl extends DbObject<Present>{
	static public int count(){
		int result = 0;

		try{
			DbWriter.checkDBTable(DbObject.getDataType(Present.class));
			SQLiteDatabase db = DataBaseManager.getDataBase();

			android.database.Cursor c = db.rawQuery(
				"SELECT COUNT(*) FROM presentation WHERE photoPath NOT NULL", null);
		
			if (c.moveToFirst())
				result = c.getInt(0);
		
			c.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
}
