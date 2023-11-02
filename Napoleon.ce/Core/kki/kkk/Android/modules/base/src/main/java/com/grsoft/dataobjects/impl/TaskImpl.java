package com.grsoft.dataobjects.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Task;
import com.grsoft.napoleon.TaskForm;

public class TaskImpl extends DbObject<Task> {
	
	public static final String SEND_NAME = "AgentOrgTask";
	
	public static boolean haveTask(String orgId) {
		boolean res = false;
		try {
			SQLiteDatabase db = DataBaseManager.getDataBase();
			String table = DataObjectInfo.getInstance().getTableName(Task.class);
			String filter = "id = ?";
			
			Cursor c = db.query(table, new String[] {"task"}, filter, new String[] { orgId }, null, null, null);
			res = c.moveToNext();
			c.close();
		} catch (Exception e) {
			
		}
		return res;
	}
	
	public static void editTask(String orgId, Context context) { TaskForm.open(orgId, context); }
}
