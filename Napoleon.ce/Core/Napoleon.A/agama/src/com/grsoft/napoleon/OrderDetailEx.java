package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.OrgTaskListHelper;
import com.grsoft.util.gps.GPSUtilNew;


public class OrderDetailEx extends OrderDetail {
	@Override
	protected boolean keyBackPressed() {
		if (DocType.getCurDoc() == OrderDoc.instance()){
			final OrderEx order = (OrderEx) doc.getData();
			List<Long> curTaskList = new OrgTaskListHelper().getTaskList(order.id, true);
			
			if(curTaskList.size() > 0){
				OrgTaskExecImpl taskExec = new OrgTaskExecImpl(); 
				if(taskExec.init(this, doc.getId(), GPSUtilNew.getLastKnownLocation()))
					OrgTaskList.open(this, order.id, taskExec.getRowid());
			}
		}
		return true;
	}
}
