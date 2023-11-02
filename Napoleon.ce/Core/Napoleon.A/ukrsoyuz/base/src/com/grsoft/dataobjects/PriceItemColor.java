package com.grsoft.dataobjects;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.FieldOrder;

@TableInfo(name="PriceItemColor",keyFields="id")
@ServerInfo(name="PriceItemColor")
public class PriceItemColor extends DataObject {
	@FieldOrder(order = 0)
	public String id = "";

	@FieldOrder(order = 1)
	public int color = 0;
	
	static Map<String, Integer> colors = null;
	
	public static void resetCach() {
		colors = null;
	}
	
	public static void updateCache() {
		if(colors == null) {
			colors = new HashMap<String, Integer>();
			DataTraveler.travel(PriceItemColor.class, new DataTraveler.Travel<PriceItemColor>() {
				@Override
				public boolean travel(DataTraveler<PriceItemColor> item) {
					colors.put(item.data.id, item.data.color);
					return true;
				}
			}, "");
		}
	}
	
	
	public static Integer getColor(String id) {
		updateCache();
		return colors.get(id);
	}
}
