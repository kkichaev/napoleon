package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="invfrg", keyFields="created")
public class InvFrg extends CreateDocDataObject {
	public List<InvFrgItem> items = new ArrayList<InvFrgItem>();
	public int tenant = 0;
	public int retEquip = 0;
}
