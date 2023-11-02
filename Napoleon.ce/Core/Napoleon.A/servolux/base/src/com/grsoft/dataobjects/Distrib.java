package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="distribDoc", keyFields="created")
public class Distrib extends CreateDocDataObject {
	public String priceType = "";
	public String thermalState = "";
	
	public List<DistribItem> items = new ArrayList<DistribItem>();
}
