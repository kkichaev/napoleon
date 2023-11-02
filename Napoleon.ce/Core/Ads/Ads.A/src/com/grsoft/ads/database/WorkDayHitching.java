package com.grsoft.ads.database;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.ads.dataobjects.WorkDay;
import com.grsoft.ads.dataobjects.impl.WorkDayImpl;
import com.grsoft.database.DbReader;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.network.ObjectExportListener;

public class WorkDayHitching extends Hitching  implements ObjectExportListener{

	List<Long> list;
	public WorkDayHitching() {
		super(WorkDay.class, "WorkDay");
		
		list = new ArrayList<Long>();
		String where = "(([params] & " + ParamState.ofExported + " ) == 0)";
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(WorkDay.class), where, "");
	}

	@Override
	public int size() {
		return list.size();
	}
	
	@Override
	public void onEnd() {
		for( int i=0; i<list.size(); i++ ) {
			WorkDayImpl impl = new WorkDayImpl();
			impl.read(list.get(i));
			impl.getData().params |=  ParamState.ofExported;
			impl.write();
			impl.close();
		}
	}

	@Override
	public DataObject get(int i) {
		WorkDayImpl impl = new WorkDayImpl();
		impl.read(list.get(i));
		impl.close();
		return impl.getData();
	}

}
