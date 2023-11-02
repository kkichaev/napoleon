package com.grsoft.dataobjects;

import java.util.Date;

public class MemoProceeded extends DataObject {
	public Date created;
	public String remark;
	public String type;
	public String userid;
	
	public MemoProceeded(AgentManagerMemo data) {
		created = data.created;
		userid = data.userid;
		type = "AgentMemo";
		remark = data.isAllowed() ? "ÏÎÄÒÂÅĞÆÄÅÍÎ" : "ÎÒÊÀÇÀÍÎ";
		
		if(data.managerRemark.length() > 0) {
			remark += " " + data.managerRemark;
		}
	}
}
