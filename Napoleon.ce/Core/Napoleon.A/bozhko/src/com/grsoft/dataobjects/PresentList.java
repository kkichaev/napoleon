package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="presentlist", keyFields="id")
public class PresentList extends DataObject {
	public int id = 0;
	public int col = 0;
	public int row = 0;
	public List<PresentItem> items = new ArrayList<PresentItem>();
}
