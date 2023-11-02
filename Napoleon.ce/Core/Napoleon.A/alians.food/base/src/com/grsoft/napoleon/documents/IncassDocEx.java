package com.grsoft.napoleon.documents;

import android.database.SQLException;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.IncassImplEx;
import com.grsoft.network.DocExportListener;

public class IncassDocEx extends IncassDoc {
	static public void init() {
		instance = new IncassDocEx();
	}
	
	IncassDocEx() {
		super(DOC_NAME, OBJ_NAME, IncassImplEx.class);
	}
	
	@Override
	public DocExportListener getDirtyDocuments() {
		try {
			Class<? extends DataObject> cls = create().getData().getClass();
			String tableName = DataObjectInfo.getInstance().getTableName(cls);
			DataBaseManager.getDataBase().execSQL("DELETE FROM '" + tableName + "' WHERE sum = 0");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return super.getDirtyDocuments();
	}
}
