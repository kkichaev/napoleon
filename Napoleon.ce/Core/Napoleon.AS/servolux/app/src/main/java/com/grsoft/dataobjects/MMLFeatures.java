package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="MMLFeatures", keyFields="id,kind")
@ServerInfo(name="MMLFeatures")
public class MMLFeatures extends DataObject {
    public static final String ORG_TYPE_KIND = "orgType";
    public static final String SALES_PLACE_KIND = "salesPlace";
	
	
	public String id = "";
	public String kind = "";
	
	public List<MMLFItem> items = new ArrayList<MMLFItem>();
	
	public static Set<String> orgMML(OrgEx o) {
		Set<String> ret = new HashSet<String>();
		
		
		final Set<String> orgItem = new HashSet<String>();
		MMLFeatures data = new MMLFeatures();
		DbReader r = new DbReader();
		if(r.select(data, data.getTableName(), "id = '" + o.formatTT + "' and kind = '" + ORG_TYPE_KIND + "'")) {
			for(MMLFItem i : data.items)
				orgItem.add(i.id);
		}
		r.close();
		
		for(OrgSalesPlace osp : o.salesPlaces) {
			if(r.select(data, data.getTableName(), "id = '" + osp.id + "' and kind = '" + SALES_PLACE_KIND + "'")) {
				for(MMLFItem i : data.items)
					if(orgItem.contains(i.id))
						ret.add(i.id);
			}
			
		}
		
		return ret;
	}
}
