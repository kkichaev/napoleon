package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="Display", keyFields="created")
public class Display extends CreateDocDataObject {
	public List<DisplayItem> items = new ArrayList<DisplayItem>();
}
