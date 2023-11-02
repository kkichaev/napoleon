package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	public String parent = "";
	public String cheif = "";
	public String cheifPhone = "";
	public String contact = "";
	public String contactPhone = "";
	public List<OrgDealerItem> dealers = new ArrayList<OrgDealerItem>();
	public String orgType = "";
	public int license = 0;
	public int avgTraff = 0;
	public String email = "";
}
