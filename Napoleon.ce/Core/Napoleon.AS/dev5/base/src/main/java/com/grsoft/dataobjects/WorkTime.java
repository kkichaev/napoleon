package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="WorkTime", keyFields="id,start")
public class WorkTime extends DataObject {
	public String id;
	public Date start;
	public Date stop;
	public int params;
}
