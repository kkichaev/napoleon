package com.grsoft.napoleon.util;

import java.util.Comparator;

import com.grsoft.dataobjects.OrgFolders;

public class OrgFoldersCmp  implements Comparator<OrgFolders> {
	@Override
	public int compare(OrgFolders object1, OrgFolders object2) {
		WeekDay wkObject1 = WeekDay.getWeekDay(object1.name);
		WeekDay wkObject2 = WeekDay.getWeekDay(object2.name);
		
		if (wkObject1 != null && wkObject2 != null)
			return WeekDay.compare(wkObject1, wkObject2);
			
		return 0;
	}
}
