package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PayData {
	public int limit = 0;
	public List<DebetData> debet = new ArrayList<DebetData>();
	public List<OverdueData> overdue = new ArrayList<OverdueData>();
	
	public int debetSum() {
		int sum = 0;
		for(DebetData dd : debet)
			sum += dd.sum;
		
		return sum;
	}
	
	public int overdueSum() {
		int sum = 0;
		
		for(OverdueData dd : overdue)
			sum += dd.sum;
		return sum;
	}
	
	public int limit() {
		int sum = limit;
		sum -= debetSum();
		//sum -= overdueSum();
		return sum;
	}
	
	public boolean read(JsonElement root) {
		if(!root.isJsonObject())
			return false;
		JsonObject ro = root.getAsJsonObject();
		JsonElement del = ro.get("ОбщийДолг");
		if(del.isJsonArray()) {
			JsonArray dar = del.getAsJsonArray();
			for(int i=0; i<dar.size(); i++) {
				DebetData dd = DebetData.load(dar.get(i));
				if(dd != null)
					debet.add(dd);
			}
		}
		
		del = ro.get("ПросроченныйДолг");
		if(del.isJsonArray()) {
			JsonArray dar = del.getAsJsonArray();
			for(int i=0; i<dar.size(); i++) {
				OverdueData dd = OverdueData.load(dar.get(i));
				if(dd != null)
					overdue.add(dd);
			}
		}
		return true;
	}
}
