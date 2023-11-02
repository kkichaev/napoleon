package com.grsoft.napoleon.documents;

import android.database.SQLException;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Incass;
import com.grsoft.network.DocExportListener;

public class IncassDocEx extends IncassDoc {
	public static void init() {
		instance = new IncassDocEx();
	}
	
	@Override
	public DocExportListener getDirtyDocuments() {
		try {
			String table = DataObjectInfo.getInstance().getTableName(Incass.class);
			String sql = "delete from [" + table + "] where sum = 0";
			DataBaseManager.getDataBase().execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return super.getDirtyDocuments();
	}
}
