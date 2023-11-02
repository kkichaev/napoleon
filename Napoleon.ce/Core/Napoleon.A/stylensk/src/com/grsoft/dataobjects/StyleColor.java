package com.grsoft.dataobjects;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="style_color", keyFields="id")
@ServerInfo(name="StyleColors")
public class StyleColor extends DataObject {
	public String id = "";
	public String name= "";

	public static Map<String, StyleColor> get() {
		final Map<String, StyleColor> ret = new HashMap<String, StyleColor>();
		
		DataTraveler.travel(StyleColor.class, new DataTraveler.Travel<StyleColor>(true) {

			@Override
			public boolean travel(DataTraveler<StyleColor> item) {
				ret.put(item.data.id, item.data);
				return true;
			}
		}, "");
		
		return ret;
	}
}
