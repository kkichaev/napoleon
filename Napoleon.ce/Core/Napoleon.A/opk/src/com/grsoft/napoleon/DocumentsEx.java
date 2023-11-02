package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgBalance;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
	@Override
	protected String orgInfo(Org o) {
		OrgEx oe = (OrgEx)o;
		String str = o.name;
		if( oe.balance.size() > 0 ) {
			OrgBalance ob = oe.balance.get(0);
			str += "\nДолг: " + Util.IntToScaleStr(ob.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			if( ob.sumOut > 0 )
				str += " Просрочено: " + Util.IntToScaleStr(ob.sumOut, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		}
		return str;
	}
}
