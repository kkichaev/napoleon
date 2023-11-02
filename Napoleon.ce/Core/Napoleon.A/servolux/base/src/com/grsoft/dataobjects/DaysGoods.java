package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="DaysGoods", keyFields="id,firm")
public class DaysGoods extends DataObject {
	public String id = "";
	public int isOrg;
	public String firm = "";
	
	public List<MatrixItem> items = new ArrayList<MatrixItem>();
}
