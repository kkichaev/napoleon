package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="ActiveActions", keyFields="created")
public class ActiveOrgActions extends CreateDocDataObject {
	public List<ActiveOrgActionItem> items = new ArrayList<ActiveOrgActionItem>();
}
