package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="PriceOrgMon", keyFields = "created")
public class PriceOrgMon extends CreateDocDataObject {
	public List<PriceOrgMonItem> items = new ArrayList<PriceOrgMonItem>();
}
