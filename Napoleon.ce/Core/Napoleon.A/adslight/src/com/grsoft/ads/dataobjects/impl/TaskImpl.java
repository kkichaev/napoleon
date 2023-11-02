package com.grsoft.ads.dataobjects.impl;

import com.grsoft.ads.dataobjects.TaskQuery;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.DbObject;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TaskImpl extends DbObject<TaskQuery> {

	public static boolean rcvdNewTasks() {
		boolean result = false;
		Cursor c = null;
		try{
			SQLiteDatabase db = DataBaseManager.getDataBase();
			StringBuilder where = new StringBuilder();
			DbWriter.checkDBTable(TaskQuery.class);
			
			where.append("select rowid from ")
				.append(DataObjectInfo.getInstance().getTableName(getDataType(TaskQuery.class)))
				.append(" where uptoday = 1");
			
			c = db.rawQuery(where.toString(), null);
			
			result = c.moveToFirst();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (c != null)
				c.close();
		}
		
		return result;
	}
}
