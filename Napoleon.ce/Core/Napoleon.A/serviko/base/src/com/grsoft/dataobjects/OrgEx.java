package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public String firmCode = "";
	public String prcType = "";
	public String whCode = "";
	public int firmDisabled = 0;
	public int mark = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public int nac = 0;
	
	public List<OrgInfo> information = new ArrayList<OrgInfo>();
	
	public String getInfo() {
		String ret = "";
		
		for(OrgInfo oi : information) {
			ret = oi.info;
			break;
		}
		
		return ret;
	}
}
