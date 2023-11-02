package com.grsoft.dataobjects.impl;

import android.content.Context;
import android.database.SQLException;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DymovTask;
import com.grsoft.dataobjects.DymovTaskResult;
import com.grsoft.napoleon.DocumentsEx;
import com.grsoft.napoleon.documents.Document;

public class DymovTaskImpl extends Document<DymovTask> {

	@Override
	public void open(Context context) {
		if(context instanceof DocumentsEx)
			((DocumentsEx)context).openTask(this);
	}

	public void setData(DymovTask d) { this.data = d; }
	
	@Override
	public String getDescription(Context context) {
		String text = data.task + "<br/><small>" + data.manager + "</small>"; 
		return text;
	}
	
	@Override
	public boolean delete() {
		boolean ret = false;
		if(data.isOwn()) {
			ret = super.delete();
			if( ret ) {
				String sql = "delete from " + (new DymovTaskResult().getTableName()) + " where idTask='" + data.idTask + "'";
				try {
					DataBaseManager.getDataBase().execSQL(sql);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
			
		return ret;
	}
}
