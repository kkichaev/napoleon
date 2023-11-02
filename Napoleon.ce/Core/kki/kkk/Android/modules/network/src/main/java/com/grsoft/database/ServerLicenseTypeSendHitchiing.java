package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.ServerLicenseType;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class ServerLicenseTypeSendHitchiing implements ObjectExportListener {

	List<ServerLicenseType> array = new ArrayList<ServerLicenseType>();
	
	@Override public void onStart() {}
	@Override public void onRead(RawObject rawObject) throws RuntimeException {}
	@Override public void onSave() { }
	@Override public void onEnd() {}

	@Override public String getObjectName() { return "LicenseType"; }
	@Override public int size() { return array.size(); }
	@Override public DataObject get(int i) { return array.get(i); }

	public void add(JsonElement elData) {
		array.clear();
		
		JsonArray a = elData.getAsJsonArray();
		if(a == null)
			return;
		
		for(int i=0; i<a.size(); i++) {
			JsonObject el = a.get(i).getAsJsonObject();
			if(el == null)
				continue;
			ServerLicenseType data = ServerLicenseType.load(el);
			if(data != null) {
				array.add(data);
			}
		}
	}
}
