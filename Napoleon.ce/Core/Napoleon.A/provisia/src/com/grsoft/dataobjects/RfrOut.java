package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="rfrOut", keyFields="created")
public class RfrOut extends CreateDocDataObject {
	public List<RfrItem> items;
}
