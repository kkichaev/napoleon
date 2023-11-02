package com.grsoft.database;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ReportRequest;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.ReportRequestImpl;
import com.grsoft.network.ObjectExportListener;

public class ReportRequestHitching extends Hitching implements ObjectExportListener {
	List<Long> list = new ArrayList<Long>();
	
	public ReportRequestHitching() {
		super(ReportRequest.class, "ReportRequest");
		
		list = new ArrayList<Long>();
		DbWriter.checkDBTable(DbObject.getDataType(ReportRequest.class));
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(dataObject), "[sent] = 0 or [sent] is null", "");
	}
	
	@Override public int size() { return list.size(); }

	@Override
	public DataObject get(int i) {
		ReportRequestImpl impl = new ReportRequestImpl();
		impl.read(list.get(i));
		impl.close();
		return impl.getData();
	}

	@Override
	public void onEnd() {
		
		DataBaseManager.getDataBase().execSQL("update [" + DataObjectInfo.getInstance().getTableName(dataObject) + "] set [sent]=" +
				Long.toString((new Date()).getTime()) + " where [sent] = 0 or [sent] is null");
	}
}
