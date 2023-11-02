package com.grsoft.ads.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="certificate", keyFields="number")
public class Certificate extends DataObject {
	public String number = "";
	public Date assigned;
	public int writeof = 0;
}
