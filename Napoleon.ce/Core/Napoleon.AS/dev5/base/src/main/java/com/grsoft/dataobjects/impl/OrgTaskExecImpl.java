package com.grsoft.dataobjects.impl;
import com.grsoft.aceteam.R;

import android.content.Context;
import com.grsoft.dataobjects.OrgTask;
import com.grsoft.dataobjects.TaskDoneItem;
import com.grsoft.dataobjects.impl.OrgTaskExecImplW;
import com.grsoft.util.GpsCoord;

public class OrgTaskExecImpl extends OrgTaskExecImplW {

	public boolean init(Context context, OrgTask task, GpsCoord gpsCoord) {
		data.idTask = task.id;
		TaskDoneItem tdi = new TaskDoneItem();
		tdi.done = 1;
		tdi.id = task.id;
		data.items.add(tdi);
		
		return super.init(context, task.orgid, gpsCoord);
	}
}
