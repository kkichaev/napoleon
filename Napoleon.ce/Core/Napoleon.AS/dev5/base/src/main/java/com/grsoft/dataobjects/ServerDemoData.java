package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ServerDemoData extends DataObject {
	public int id = 0;
	public String type = "";
	public int allowCount = 0;
	public int timeSpan = 0;
	
	public static ServerDemoData load(JsonObject src) {
		
		ServerDemoData ret = new ServerDemoData();
		
		JsonElement el = src.get("id");
		if(el == null)
			return null;
		ret.id = el.getAsInt();
		
		el = src.get("type");
		if(el == null)
			return null;
		ret.type = el.getAsString();
		
		el = src.get("allowCount");
		if(el == null)
			return null;
		ret.allowCount = el.getAsInt();
		
		el = src.get("timespan");
		if(el == null)
			return null;
		ret.timeSpan = el.getAsInt();
		return ret;
	}
}
