package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
	@Override
	protected String orgInfo(Org o) {
		OrgEx oe = (OrgEx)o;
		String orgText = super.orgInfo(o);
		if( oe.balance != 0 )
			orgText += "\nДолг: " + Util.IntToScaleStr(oe.balance, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " руб.";

		return orgText;
	}
}
