package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DebetData implements Comparable<DebetData> {
	public Date payDate;
	public int sum;
	
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static DebetData load(JsonElement el) {
		DebetData ret = null;
		if(el.isJsonObject()) {
			ret = new DebetData();
			JsonObject jo = el.getAsJsonObject();
			JsonElement dtel = jo.get("ДатаПлатежа");
			JsonElement dbel = jo.get("ОбщийДолгСумма");
			try {
				ret.sum = (int)(dbel.getAsDouble() * 100);
				ret.payDate = sdf.parse(dtel.getAsString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return ret;
	}

	@Override public int compareTo(DebetData arg0) { return payDate.compareTo(arg0.payDate); }
}