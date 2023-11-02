package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.WorkTime;
import com.grsoft.dataobjects.impl.WorkTimeImpl;
import com.grsoft.network.ObjectExportListener;


public class WorkTimeExport extends Hitching implements ObjectExportListener{
	List<Long> list;
	public WorkTimeExport() {
		super(WorkTime.class, "WorkTime");
		
		list = new ArrayList<Long>();
		String where = "(([params] & " + ParamState.ofExported + " ) == 0)";
		DbWriter.checkDBTable(WorkTime.class);
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(WorkTime.class), where, "");
	}

	@Override
	public int size() {
		return list.size();
	}
	
	@Override
	public void onEnd() {
		for( int i=0; i<list.size(); i++ ) {
			WorkTimeImpl impl = new WorkTimeImpl();
			impl.read(list.get(i));
			impl.getData().params |=  ParamState.ofExported;
			impl.write();
			impl.close();
		}
	}

	@Override
	public DataObject get(int i) {
		WorkTimeImpl impl = new WorkTimeImpl();
		impl.read(list.get(i));
		impl.close();
		return impl.getData();
	}
}