package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.ReportsRequestImpl;
import com.grsoft.network.ObjectExportListener;

public class ReportsRequestHitching extends Hitching implements
		ObjectExportListener {
	private List<Long> list = new ArrayList<Long>();
	
	public ReportsRequestHitching() {
		super(ReportsRequest.class, "ReportsRequest");
		
		list = new ArrayList<Long>();
		DbWriter.checkDBTable(DbObject.getDataType(ReportsRequest.class));
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(dataObject), "", "");
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public DataObject get(int i) {
		ReportsRequestImpl impl = new ReportsRequestImpl();
		impl.read(list.get(i));
		impl.close();
		return impl.getData();
	}

	@Override
	public void onEnd() {
		DataBaseManager.getDataBase().execSQL("DELETE FROM [" + 
				DataObjectInfo.getInstance().getTableName(dataObject) + "]" );
	}
}
