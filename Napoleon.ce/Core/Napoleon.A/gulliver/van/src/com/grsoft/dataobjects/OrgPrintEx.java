package com.grsoft.dataobjects;

import java.util.List;

public class OrgPrintEx extends OrgPrint 
	implements OrgExtended{
	public List<OrgDogovor> dogovors;

	@Override
	public List<OrgDogovor> getDogovors() {
		return dogovors;
	}
	
	public String stopMsg = "";

	@Override
	public boolean isStopList() {
		return stopMsg.length() > 0;
	}
}
