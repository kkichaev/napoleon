package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="orgMatrix", keyFields="id,ida")
public class OrgMatrix extends DataObject {
	public String id;
	public String ida;
	public List<OrgMatrixItem> items;
}
