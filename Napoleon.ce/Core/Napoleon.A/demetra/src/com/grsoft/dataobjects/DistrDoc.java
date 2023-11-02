package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="DistrDoc", keyFields="created")
public class DistrDoc extends CreateDocDataObject {
	public List<DistrItem> items = new ArrayList<DistrItem>();
}
