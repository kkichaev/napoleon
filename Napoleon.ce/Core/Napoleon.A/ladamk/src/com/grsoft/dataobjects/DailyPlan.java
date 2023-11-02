package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="DailyPlan", keyFields="created")
public class DailyPlan extends CreateDocDataObject {
	public List<DailyPlanItem> items = new ArrayList<DailyPlanItem>();
	
	public long getPlan(Folder f) {

		for(DailyPlanItem i : items)
			if(i.id.equals(f.fid))
				return i.weight;
		
		return 0;
	}
}
