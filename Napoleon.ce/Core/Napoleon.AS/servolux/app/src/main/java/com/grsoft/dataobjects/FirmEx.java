package com.grsoft.dataobjects;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class FirmEx extends Firm implements Comparable<FirmEx>{
	@Scale(value=Consts.SUM_SCALE)
	public int dropSize;
	
	public String shortName = "";
	
	public static Map<String, FirmEx> get() {
		final Map<String, FirmEx> ret = new HashMap<String, FirmEx>();
		
		DataTraveler.travel(FirmEx.class, new DataTraveler.Travel<FirmEx>(true) {

			@Override
			public boolean travel(DataTraveler<FirmEx> item) {
				ret.put(item.data.id, item.data);
				return true;
			}
		}, "");
		
		return ret;
	}

	@Override
	public int compareTo(FirmEx arg0) {
		return name.compareTo(arg0.name);
	}
}
