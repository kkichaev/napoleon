package com.grsoft.dataobjects;

import java.util.List;

public class OrgEx extends Org implements OrgExtended{
	public List<OrgDogovor> dogovors;

	@Override
	public List<OrgDogovor> getDogovors() {
		return dogovors;
	}
}
