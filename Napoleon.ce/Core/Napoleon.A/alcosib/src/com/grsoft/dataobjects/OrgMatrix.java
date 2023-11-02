package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="OrgMatrix", keyFields = "name")
public class OrgMatrix extends DataObject {
	public String name;
	public List<OrgMatrixItem> items = new ArrayList<OrgMatrixItem>();

}
