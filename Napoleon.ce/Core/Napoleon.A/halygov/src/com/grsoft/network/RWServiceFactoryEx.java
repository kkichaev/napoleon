package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.WorkTime;
import com.grsoft.dataobjects.impl.WorkTimeImpl;
import com.grsoft.napoleon.RWServiceFactoryNapoleon;

public class RWServiceFactoryEx extends RWServiceFactoryNapoleon {
	@Override
	public WriteServiceBase createWriteService(
			List<? extends ObjectListener> objectsToSend, boolean rcvRemnants) {
		ArrayList<ObjectListener> list = new ArrayList<ObjectListener>();
		
		list.addAll(objectsToSend);
		list.add(new WorkTimeExport());
		
		return super.createWriteService(list, rcvRemnants);
	}
}

class WorkTimeExport extends Hitching implements ObjectExportListener{
	List<Long> list;
	public WorkTimeExport() {
		super(WorkTime.class, "WorkTime");
		
		list = new ArrayList<Long>();
		String where = "(([params] & " + ParamState.ofExported + " ) == 0)";
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