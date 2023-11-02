package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="quality", keyFields="name")
public class Quality extends DataObject {
	public String name;
	public List<QualityItem> items = new ArrayList<QualityItem>();
}
