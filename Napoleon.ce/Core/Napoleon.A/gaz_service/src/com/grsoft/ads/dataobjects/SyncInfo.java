package com.grsoft.ads.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="syncinfo")
public class SyncInfo extends DataObject{
	public Date date;
	public int traffic = 0;
}