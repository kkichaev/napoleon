package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Task;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.TaskImpl;
import com.grsoft.network.ObjectExportListener;

public class TaskSendHitching extends Hitching implements ObjectExportListener{
	private List<Long> list;
	private String orgID;
	
	public TaskSendHitching() {
		super(DbObject.getDataType(Task.class), "AgentOrgTask");
		try{
			DbWriter.checkDBTable(Task.class);
			list = new ArrayList<Long>();
			String where = "trim(done) != ''";
			list = DbReader.readIds(DataObjectInfo.getInstance()
					.getTableName(DbObject.getDataType(Task.class)), where, "");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public TaskSendHitching(String orgID){
		super(DbObject.getDataType(Task.class), "AgentOrgTask");
		try{
			if (orgID != null){
				DbWriter.checkDBTable(Task.class);
				this.orgID = orgID;
				list = new ArrayList<Long>();
				String where = "trim(done) != '' and id='" + orgID +"'";
				list = DbReader.readIds(DataObjectInfo.getInstance()
						.getTableName(DbObject.getDataType(Task.class)), where, "");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public DataObject get(int i) {
		TaskImpl impl = new TaskImpl();
		impl.read(list.get(i));
		impl.close();
		return impl.getData();
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public void onEnd() {
		super.onEnd();
		
		SQLiteDatabase database = DataBaseManager.getDataBase();
		String deleteSQL = "trim(done) != ''";
		
		if (orgID != null)
			deleteSQL += " AND id =?";
		
		Log.d("DELETE", Integer.toString(database.delete(DataObjectInfo.getInstance()
			.getTableName(DbObject.getDataType(Task.class)), deleteSQL, 
			orgID == null ? null : new String[]{orgID})));
	}
}
