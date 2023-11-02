package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.database.TableInfo;

@TableInfo(name="repreq", keyFields="id")
public class ReportsRequest extends DataObject {
	public Date created;
	public String id;
	public Date start;
	public Date finish;
}
