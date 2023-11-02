package com.grsoft.napoleon.util;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.OrgTask;
import com.grsoft.dataobjects.TaskDoneInfo;
import com.grsoft.util.Util;

public class OrgTaskListHelper {
	public List<Long> getTaskList(String orgid, boolean notExec) {
		final String QRY_FOR_TASK = getTaskSelectSmt(notExec);

		ArrayList<Long> result = new ArrayList<Long>();
		SQLiteDatabase db = DataBaseManager.getDataBase();
		DbWriter.checkDBTable(OrgTask.class);
		DbWriter.checkDBTable(TaskDoneInfo.class);
		try {
			Cursor c = db.rawQuery(
							QRY_FOR_TASK,
							new String[] { orgid, Long.toString(Util.getDate().getTime()) });

			while (c.moveToNext())
				result.add(c.getLong(c.getColumnIndex("rowid")));

			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	protected String getTaskSelectSmt(boolean notExec) {
		return "select rowid from agentOrgTask where orgid = ? and (finish >= ? "
				+ (notExec ? "and" : "or")
				+ " not id in (select id from orgtaskdone))";
	}
}
