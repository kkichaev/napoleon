package com.grsoft.napoleon;

import java.util.List;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.OrgTaskListHelper;
import com.grsoft.util.gps.GPSUtilNew;


public class OrderDetailEx extends OrderDetail {
	@Override
	protected boolean keyBackPressed() {
		if (DocType.getCurDoc() == OrderDoc.instance()){
			List<Long> taskids = new OrgTaskListHelper().getTaskList(doc.getId(), true);
			
			if(taskids.size() > 0){
				OrgTaskExecImpl taskExec = new OrgTaskExecImpl(); 
				if(taskExec.init(this, doc.getId(), GPSUtilNew.getLastKnownLocation()))
					OrgTaskList.open(this, doc.getId(), taskExec.getRowid());
				
				return false;
			}
		}
		
		return true;
	}

}
