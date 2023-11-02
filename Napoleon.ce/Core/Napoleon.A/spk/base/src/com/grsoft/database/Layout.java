package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.CreateDocDataObject;

@TableInfo(name="layout", keyFields=("created"))
public class Layout extends CreateDocDataObject {
	public List<LayoutItem> items = new ArrayList<LayoutItem>();
}
