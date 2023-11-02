package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;


@TableInfo(name="PrintForms", keyFields="id,name")
public class PrintFormLoader extends DataObject {
	public String id;
	public String name;
	public byte[] form;
}
