package com.grsoft.napoleon.documents;

import android.database.SQLException;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.ArchIncassImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.DocExportListener;

public class ArchIncassDoc extends IncassDoc {
	static ArchIncassDoc archInstance = null;
	
	static public DocType instance() {
		if( archInstance == null )
			archInstance = new ArchIncassDoc();
		return archInstance;
	}

	ArchIncassDoc() {
		super("Арх.инкассации", "ArchIncass", ArchIncassImpl.class);
	}
	
	@Override public int getResurceId() { return R.drawable.arch_doc; }

	@Override
	public int getResurce2Id() {
		return R.drawable.arch_doc_2;
	}

	@Override
	public int getDocTitle() {
		return -1;
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
