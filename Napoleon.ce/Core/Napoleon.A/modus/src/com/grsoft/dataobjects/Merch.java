package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.TableInfo;

@TableInfo(name="merch", keyFields="created")
public class Merch extends CreateDocDataObject{
	public List<MerchItem> items = new ArrayList<MerchItem>();
}
