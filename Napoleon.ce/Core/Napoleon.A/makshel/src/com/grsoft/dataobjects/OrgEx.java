package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	@Scale(value=Consts.SUM_SCALE)
	public int limit;
	
	@Scale(value=Consts.SUM_SCALE)
	public int balance;

	@Scale(value=Consts.SUM_SCALE)
	public int discount;
	
	public String mfr = "";
	public int pidx;
	public String cid = "";
	
	public List<OrgGroup> groups = new ArrayList<OrgGroup>();

	public boolean haveGroup(String code) {
		for(OrgGroup og : groups)
			if(og.id.equals(code))
				return true;
		return false;
	}
}
