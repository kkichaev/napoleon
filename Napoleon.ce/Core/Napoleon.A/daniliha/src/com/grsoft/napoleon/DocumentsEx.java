package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
	@Override
	protected String orgInfo(Org o) {
		String ret = super.orgInfo(o);
		ret += "<br/>Сальдо: " + Util.IntToScaleStr(((OrgEx)o).balance, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		return ret;
	}
}
