package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.database.TableInfo;

@TableInfo(name="dnum", keyFields="date,doc")
public class DNum extends DataObject {
	public Date date;
	public String doc = "";
	public int number;
}
