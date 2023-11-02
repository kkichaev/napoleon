package com.grsoft.ads.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="protocol", keyFields = "number")
public class Protocol extends DataObject {
	public String number = "";
	public Date assigned;
	public int writeof = 0;
}
