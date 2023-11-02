package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="applywsorder", keyFields="created", indexes="number")
public class ApplyWSOrder extends CreateDocDataObject {
	public String number;
}
