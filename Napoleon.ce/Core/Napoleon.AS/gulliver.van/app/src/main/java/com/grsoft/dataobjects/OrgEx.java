package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	public List<OrgDogovor> dogovors = new ArrayList<>();

	public String stopMsg = "";
	@Override
	public boolean isStopList() {
		return stopMsg.length() > 0;
	}
}
