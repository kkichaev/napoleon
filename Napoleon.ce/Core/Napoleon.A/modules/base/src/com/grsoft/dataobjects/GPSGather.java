package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="orgcoord", keyFields="created")
public class GPSGather extends CreateDocDataObject {
	public int accuracy;
}
