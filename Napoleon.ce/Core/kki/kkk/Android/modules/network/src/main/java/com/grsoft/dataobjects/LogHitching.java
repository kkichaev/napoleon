package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.LogImpl;
import com.grsoft.network.ObjectExportListener;

public class LogHitching extends Hitching implements ObjectExportListener{

	private List<Long> list = new ArrayList<Long>();
	
	public LogHitching() {
		super(Log.class, "UserLog");
		list = new ArrayList<Long>();
		DbWriter.checkDBTable(DbObject.getDataType(Log.class));
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(dataObject), "", "");
	}
	
	@Override
	public void onEnd() {
		
		DataBaseManager.getDataBase().execSQL("DELETE FROM log");
	}
	
	public boolean needUpdate(){
		return list.size() > 0;
	}

	@Override
	public DataObject get(int i) {
		LogImpl impl = new LogImpl();
		impl.read(list.get(i));
		impl.close();
		return impl.getData();
	}

	@Override
	public int size() {
		return list.size();
	}
}
