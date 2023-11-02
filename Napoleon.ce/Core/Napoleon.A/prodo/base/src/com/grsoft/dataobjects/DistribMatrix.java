package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="DistribMatrix", keyFields="id")
public class DistribMatrix extends DataObject {
	public String id;
	
	public List<DistribMatrixItem> items = new ArrayList<DistribMatrixItem>();
}
