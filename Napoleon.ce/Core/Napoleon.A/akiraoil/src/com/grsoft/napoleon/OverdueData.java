package com.grsoft.napoleon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class OverdueData implements Comparable<OverdueData> {
	public int dueDays;
	public int sum;
	
	public static OverdueData load(JsonElement el) {
		OverdueData ret = null;
		if(el.isJsonObject()) {
			ret = new OverdueData();
			JsonObject jo = el.getAsJsonObject();
			JsonElement dtel = jo.get("ДнейПросрочки");
			JsonElement dbel = jo.get("ПросДолгСумма");
			try {
				ret.sum = (int)(dbel.getAsDouble() * 100);
				ret.dueDays = dtel.getAsInt();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	@Override public int compareTo(OverdueData arg0) { return dueDays - arg0.dueDays; }
}