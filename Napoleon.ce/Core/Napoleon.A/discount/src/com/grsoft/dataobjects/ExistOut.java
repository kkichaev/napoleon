package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="ExistOut", keyFields="created")
public class ExistOut extends CreateDocDataObject {
	public List<ExistItem> items = new ArrayList<ExistItem>();
}
