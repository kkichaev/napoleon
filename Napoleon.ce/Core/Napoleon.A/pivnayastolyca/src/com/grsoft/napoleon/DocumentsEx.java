package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;

public class DocumentsEx extends Documents {
	@Override
	protected String orgInfo(Org o) {
		String ret = super.orgInfo(o);
		String info = ((OrgEx)o).info;
		if(info.length() > 0) {
			ret += "<br/>" + info;
		}
		return ret;
	}
	
	@Override
	protected String getStopMessage() {
		return ((OrgEx)org.getData()).blockMsg;
	}
}
