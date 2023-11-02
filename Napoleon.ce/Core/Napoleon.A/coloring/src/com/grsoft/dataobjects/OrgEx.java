package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrgEx extends Org {
	public Date license;
	public List<OrgDog> dogovors = new ArrayList<OrgDog>();
}
