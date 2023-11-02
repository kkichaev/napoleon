package com.grsoft.ads.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="worktype", keyFields = "id")
public class WorkType extends DataObject {
	public String id = "";
	public String name = "";
}
