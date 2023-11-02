package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="OrgDistrib", keyFields="created")
public class OrgDistrib extends CreateDocDataObject {
	public List<DistribMatrixItem> items = new ArrayList<DistribMatrixItem>();
}
