package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="orgasmmtx", keyFields="category,id")
public class OrgAssortimentMatrix extends DataObject {
	public String category;
	public String id;
	
	public List<MatrixItem> items = new ArrayList<MatrixItem>();
}
