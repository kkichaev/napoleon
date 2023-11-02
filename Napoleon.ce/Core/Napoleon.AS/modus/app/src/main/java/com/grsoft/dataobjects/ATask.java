package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="atask",keyFields="taskid")
public class ATask extends CreateDocDataObject{
	public String taskid = "";
	public int manager = 0;
}
