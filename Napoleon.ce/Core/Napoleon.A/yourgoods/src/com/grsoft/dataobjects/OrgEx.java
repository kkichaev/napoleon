package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends OrgPrint {
	public String stopMsg = "";
	
	public String ido = "";
	
	public String dogovor = "";
	
	public String divName = "";
	
	@Scale(value=Consts.SUM_SCALE)
	public int balance; 
	
	@Override
	public boolean isStopList() {
		return stopMsg.length() > 0; 
	}
}
