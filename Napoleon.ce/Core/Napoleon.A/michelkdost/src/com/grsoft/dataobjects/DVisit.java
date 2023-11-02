package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.TableInfo;

@TableInfo(name="dvisit", keyFields="created")
public class DVisit extends DispatchDocDataObject{
	public List<VisitItem> items = new ArrayList<VisitItem>();
}
