package com.grsoft.dataobjects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ServerLicenseType extends DataObject {
	public String type = "";
	public int forAgents = 0;
	public String title = "";
	
	public static ServerLicenseType load(JsonObject src) {
		ServerLicenseType ret = new ServerLicenseType();
		
		JsonElement el = src.get("type");
		if(el == null)
			return null;
		ret.type = el.getAsString();
		
		el = src.get("forAgents");
		if(el == null)
			return null;
		ret.forAgents = el.getAsInt();
		
		el = src.get("title");
		if(el == null)
			return null;
		ret.title = el.getAsString();
		return ret;
	}
}
