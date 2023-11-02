package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="tare", keyFields="created")
public class Tare extends CreateDocDataObject {
	public List<TareItem> items = new ArrayList<TareItem>();
}
