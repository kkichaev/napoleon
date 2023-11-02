package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;

public class DocumentsEx extends Documents {
	protected String orgInfo(com.grsoft.dataobjects.Org o) {
		String ret = super.orgInfo(o);
		if(((OrgEx)o).balance.length() > 0) {
			ret += "<br/>" + ((OrgEx)o).balance;
		}
		return ret;
	}
}
