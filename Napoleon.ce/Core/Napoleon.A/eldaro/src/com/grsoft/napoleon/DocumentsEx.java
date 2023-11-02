package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
	@Override
	protected String orgInfo(Org o) {
		OrgEx oe = (OrgEx)o;
		String info = oe.name;
		info += "\nВремя работы: " + oe.wrkTime;
		info += "\nДолг: " + Util.IntToScaleStr(oe.balance, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		return info;
	}
}
