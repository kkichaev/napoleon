package com.grsoft.database;

import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.TaskRemark;
import com.grsoft.dataobjects.impl.TaskRemarkImpl;
import com.grsoft.network.ObjectExportListener;

public class TaskRemarkExporter extends Hitching implements ObjectExportListener {
	List<Long> list;
	TaskRemarkImpl impl = new TaskRemarkImpl();

	public TaskRemarkExporter() {
		super(TaskRemark.class, "TaskRemark");
		
		DbWriter.checkDBTable(TaskRemark.class);
		
		String where = "(([params] & " + ParamState.ofExported + " ) == 0)";
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(TaskRemark.class), where, "");
	}
	
	@Override public int size() { return list.size(); }
	
	@Override
	public void onEnd() {
		for( int i=0; i<list.size(); i++ ) {
			impl.read(list.get(i));
			impl.getData().params |=  ParamState.ofExported;
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
