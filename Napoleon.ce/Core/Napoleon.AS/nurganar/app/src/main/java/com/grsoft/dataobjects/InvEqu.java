package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="invequ", keyFields="created")
public class InvEqu extends CreateDocDataObject {
	public List<InvEquItem> items = new ArrayList<InvEquItem>();
	public Date visitDoc;
}
