package com.grsoft.dataobjects.impl;

import java.util.UUID;

import com.grsoft.dataobjects.DymovTaskResult;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class DymovTaskResultImpl extends DbObject<DymovTaskResult> {
	public boolean createNewTask(String orgId, String text) {
		rowid = ExtrasConst.INVALID_ROWID;
		
		data.id = orgId;
		data.idTask = UUID.randomUUID().toString().replace("-", "");
		data.created = Util.getDateTime();
		data.done = null;
		data.task = text;
		
		return (write() != ExtrasConst.INVALID_ROWID);
	}
}
