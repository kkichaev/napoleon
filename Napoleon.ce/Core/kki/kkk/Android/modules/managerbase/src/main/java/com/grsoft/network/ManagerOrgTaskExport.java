package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ManagerOrgTask;
import com.grsoft.network.exception.RuntimeException;

public class ManagerOrgTaskExport implements ObjectExportListener {

	List<ManagerOrgTask> data = new ArrayList<ManagerOrgTask>();
	
	
	public ManagerOrgTaskExport(ManagerOrgTask task) {
		data.add(task);
	}
	
	public ManagerOrgTaskExport() {
		DataTraveler.travel(ManagerOrgTask.class, new DataTraveler.Travel<ManagerOrgTask>() {

			@Override
			public boolean travel(DataTraveler<ManagerOrgTask> item) {
				data.add(item.data);
				item.data = new ManagerOrgTask();
				return true;
			}
		}, "(params&" + Integer.toString(ManagerOrgTask.DIRTY) + ") <> 0");
	}
	
	@Override public void onStart() { }
	@Override public void onRead(RawObject rawObject) throws RuntimeException { }
	@Override public void onSave() { }

	@Override
	public void onEnd() {
		String ids = "";
		
		for(ManagerOrgTask ot : data) {
			ids += "'" + ot.id + "',";
			ot.params = 0;
		}
		
		if( ids.length() > 0 ) {
			ids = ids.substring(0, ids.length() - 1);
			String sql = "update  \"" + data.get(0).getTableName() + "\" set params = params & \"0x" + Integer.toHexString(~ManagerOrgTask.DIRTY) + 
					"\" where id in (" + ids + ")"; 
			try {
				DataBaseManager.getDataBase().execSQL(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override public String getObjectName() { return "OrgTask"; }
	@Override public DataObject get(int i) { return i < data.size() ? data.get(i) : null; }

	@Override public int size() { return data.size(); }

}
