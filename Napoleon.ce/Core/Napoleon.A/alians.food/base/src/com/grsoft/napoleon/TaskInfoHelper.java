package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgTask;
import com.grsoft.dataobjects.TaskDone;
import com.grsoft.util.Util;

public class TaskInfoHelper {
	public enum TaskStatus{EXPIRED, PENDING, DONE, MULTI_DONE, NONE}
	private Map<String, TaskStatus> data = new HashMap<String, TaskStatus>();
	
	public void refresh() {
		data.clear();
		final Map<String, OrgTask> tasks = new HashMap<String, OrgTask>();
		final Map<String, TaskDone> done = new HashMap<String, TaskDone>();
		
		DataTraveler.travel(OrgTask.class, new DataTraveler.Travel<OrgTask>(true){

			@Override
			public boolean travel(DataTraveler<OrgTask> item) {
				if (!tasks.containsKey(item.data.id))
					tasks.put(item.data.id,item.data);
				
				return true;
			}
			
		}, null);
		
		DataTraveler.travel(TaskDone.class, new DataTraveler.Travel<TaskDone>(true){

			@Override
			public boolean travel(DataTraveler<TaskDone> item) {
				if (!done.containsKey(item.data.idTask))
					done.put(item.data.idTask,item.data);
				
				return true;
			}
			
		}, null);
		
		long now = Util.getDate().getTime();
		
		for(OrgTask t : tasks.values()) {
			TaskStatus ts = TaskStatus.NONE;
			
			if (data.containsKey(t.orgid))
				ts = data.get(t.orgid);
			
			if (ts == TaskStatus.EXPIRED)
				continue;
			
			if (t.finish.getTime() < now && !done.containsKey(t.id)  ) { 
				data.put(t.orgid, TaskStatus.EXPIRED);
				continue;
			}
			
			if (ts == TaskStatus.PENDING )
				continue;
			
			if (done.containsKey(t.id)) {
				if (ts == TaskStatus.DONE)
					data.put(t.orgid, TaskStatus.MULTI_DONE);
				else if (ts != TaskStatus.MULTI_DONE)
					data.put(t.orgid, TaskStatus.DONE);
			} else   
				data.put(t.orgid, TaskStatus.PENDING);
		}
	}
	
	public TaskStatus getStatus(String orgid) {
		TaskStatus res = TaskStatus.NONE;
		
		if (data.containsKey(orgid))
			res = data.get(orgid);
		
		return res;
	}
}
