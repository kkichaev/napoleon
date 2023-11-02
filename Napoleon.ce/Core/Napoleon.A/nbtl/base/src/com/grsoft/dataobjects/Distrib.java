package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.TableInfo;

@TableInfo(name="Distrib", keyFields="created")
public class Distrib extends CreateDocDataObject{
	public List<DistribItem> items = new ArrayList<DistribItem>();
}
