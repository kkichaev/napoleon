package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.CreateDocDataObject;

@TableInfo(name="layout", keyFields=("created"))
public class Layout extends CreateDocDataObject {
	public List<LayoutItem> items = new ArrayList<LayoutItem>();
	public int inwork;
}
