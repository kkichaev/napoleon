package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="RestOut", keyFields="created")
public class RestOut extends CreateDocDataObject {
	public List<RestOutItem> items;
}
