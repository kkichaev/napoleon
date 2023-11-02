package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	public String blockMsg = "";
	public String info = "";
	public String ido = "";
	
	public List<OrgDiscount> discounts = new ArrayList<OrgDiscount>();
	
	@Override
	public boolean isBlocked() {
		return blockMsg.length() > 0;
	}
}
