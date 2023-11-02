package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	public List<OrgPriceItem> price = new ArrayList<OrgPriceItem>();
	public List<OrgCostModifyItem> costmodify = new ArrayList<OrgCostModifyItem>();

	public int paytype = 0;
	public String info = "";
}
