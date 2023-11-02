package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.TaskInfo;
import com.grsoft.dataobjects.impl.TaskInfoImpl;
import com.grsoft.network.ObjectExportListener;

public class TaskInfoHitching extends Hitching implements ObjectExportListener {
	private List<Long> list;
	private TaskInfoImpl impl = new TaskInfoImpl();
	
	public TaskInfoHitching() {
		super(TaskInfo.class, "TaskInfo");
		
		list = new ArrayList<Long>();
		String where = "(([params] & " + ParamState.ofExported + " ) == 0)";
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(TaskInfo.class), where, "");
	}

	@Override
	public int size() { return list.size(); }

	@Override
	public void onEnd() {
		for( int i=0; i<list.size(); i++ ) {
			impl.read(list.get(i));
			impl.getData().params |= ParamState.ofExported;
			impl.write();
		}
		impl.close();
	}

	@Override
	public DataObject get(int i) {
		impl.read(list.get(i));
		return impl.getData();
	}

}
