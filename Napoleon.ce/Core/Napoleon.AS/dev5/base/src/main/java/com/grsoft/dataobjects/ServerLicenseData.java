package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ServerLicenseData extends DataObject {
	public int id = 0;
	public String type = "";
	public int count = 0;
	public Date start = new Date();
	public Date end = new Date();
	
	public static ServerLicenseData load(JsonObject src) {
		ServerLicenseData ret = new ServerLicenseData();
		
		JsonElement el = src.get("id");
		if(el == null)
			return null;
		ret.id = el.getAsInt();

		el = src.get("type");
		if(el == null)
			return null;
		ret.type = el.getAsString();

		el = src.get("count");
		if(el == null)
			return null;
		ret.count = el.getAsInt();
		
		el = src.get("start");
		if(el == null)
			return null;
		ret.start = new Date(el.getAsLong());
		
		el = src.get("end");
		if(el == null)
			return null;
		ret.end = new Date(el.getAsLong());

		return ret;
	}
}
