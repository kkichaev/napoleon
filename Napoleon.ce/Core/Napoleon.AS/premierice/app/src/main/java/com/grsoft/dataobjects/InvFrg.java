package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="invfrg", keyFields="created")
public class InvFrg extends CreateDocDataObject {
	public Date visitDoc = new Date(1000);

	public List<InvFrgItem> items = new ArrayList<InvFrgItem>();
}
