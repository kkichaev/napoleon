package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	public String info = "";
	
	public List<OrgDogovor> dogovors = new ArrayList<OrgDogovor>();
	public List<OrgDiscount> discounts = new ArrayList<OrgDiscount>();
	
	public String salesChannel = "";
}
