package com.grsoft.dataobjects;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="StyleSizes", keyFields="id")
@ServerInfo(name="StyleSizes")
public class StyleSizes extends DataObject {
	public String id = "";
	public String name= "";

	public static Map<String, StyleSizes> get() {
		final Map<String, StyleSizes> ret = new HashMap<String, StyleSizes>();
		
		DataTraveler.travel(StyleSizes.class, new DataTraveler.Travel<StyleSizes>(true) {

			@Override
			public boolean travel(DataTraveler<StyleSizes> item) {
				ret.put(item.data.id, item.data);
				return true;
			}
		}, "");
		
		return ret;
	}
}
