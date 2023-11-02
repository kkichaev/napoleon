package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class DocumentsEx extends Documents {
	protected String getNonBlockingMessage(){
		return getString(R.string.debts, Util.IntToScaleStr(((OrgEx)org.getData()).debt, Consts.SUM_SCALE));
	}
}
