package com.grsoft.dataobjects;

public class OrgEx extends Org {
	public String stopMsg = "";
	
	@Override
	public boolean isStopList() {
		return stopMsg.length() > 0;
	}
}
