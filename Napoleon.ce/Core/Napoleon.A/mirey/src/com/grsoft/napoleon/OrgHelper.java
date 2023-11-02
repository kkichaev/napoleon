package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

public class OrgHelper {
	static Map<String, Boolean> data = new HashMap<String, Boolean>();
	static List<OrgFolderItem> route = new ArrayList<OrgFolderItem>();
	static List<String> routeHash = new ArrayList<String>();
	static Map<String, Integer> routePos = new HashMap<String, Integer>();
	
	public static void refresh(List<OrgFolderItem> fi) {
		data.clear();
		route.clear();
		routeHash.clear();
		routePos.clear();
		
		for(OrgFolderItem f : fi) {
			route.add(f);
			routeHash.add(f.name);
		}
		
		DatePeriod p = DatePeriod.createRange(Util.getDate(), 24*60);
		data.clear();
		
		for(OrgFolderItem f : route) {
			if(!data.containsKey(f.name))
				data.put(f.name, findTodayDoc(f.name, p));
		}
		
		Collections.sort(route, new Comparator<OrgFolderItem>() {
			@Override
			public int compare(OrgFolderItem lhs, OrgFolderItem rhs) {
				return lhs.pos - rhs.pos;
			}
		});
		
		for(int i = 0; i < route.size(); i++) {
			String id = route.get(i).name;
			if(!routePos.containsKey(id))
				routePos.put(id, i);
		}
	}
	
	public static boolean findTodayDoc(String id, DatePeriod range) {
		boolean result = false;
		
		for(DocTypeBase d : DocType.docTypes) {
			if (d.isCreatable()) {
				result = d.docList(id, null, range).getCount() > 0;
				
				if(result)
					break;
			}
		}
		
		return result;
	}
	
	public static boolean isEnabled(String id) {
		boolean result = routeHash.contains(id);
		
		if(result) {
			if (routePos.containsKey(id)) {
				int pos = routePos.get(id);
				
				result = pos == 0;
				
				if(!result) {
					OrgFolderItem f = route.get(pos - 1);
					
					if (data.containsKey(f.name))
						result = data.get(f.name); 
				}
			}
		}
		
		return result;
	}
}
