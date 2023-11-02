package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;

public class Documents2Ex extends DocumentsEx {
	@Override
	protected String orgInfo(Org o) {
		String text = super.orgInfo(o);
		String info = ((OrgEx)o).info;
		if(info.length() > 0) {
			text += "<br/>" + info;
		}
		return text;
	}
}
