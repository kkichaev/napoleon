package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="OrgMatrix", keyFields="id")
@ServerInfo(name="OrgMatrix")
public class OrgMatrix extends DataObject {
	public String id;
	public List<OrgMatrixItem> items = new ArrayList<OrgMatrixItem>();
}
