package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="Sklads", keyFields="id")
public class Sklads extends DataObject {
	public String id;
	public String name;
}
