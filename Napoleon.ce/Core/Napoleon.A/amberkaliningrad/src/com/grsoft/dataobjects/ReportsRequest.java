package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.database.TableInfo;

@TableInfo(name="repreq", keyFields="date")
public class ReportsRequest extends DataObject {
	public Date date;
}
