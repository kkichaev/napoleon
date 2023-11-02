package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.grsoft.dataobjects.City;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Regions;
import com.grsoft.util.view.dialog_helper.KeyValue;


public class RegionStruct {
	public Map<String, City> cdt = new HashMap<String, City>();
	public Map<String, List<City>> sdt = new HashMap<String, List<City>>();
	public Map<String, Regions> rdt = new HashMap<String, Regions>();
	
 	public RegionStruct(){
 		DataTraveler.travel(Regions.class, new DataTraveler.Travel<Regions>(){

			@Override
			public boolean travel(DataTraveler<Regions> item) {
				rdt.put(item.data.id, item.data);
				item.data = new Regions();
				return true;
			}}, null);
 		
		DataTraveler.travel(City.class, new DataTraveler.Travel<City>(){

			@Override
			public boolean travel(DataTraveler<City> item) {
				cdt.put(item.data.id, item.data);
				
				if(!sdt.containsKey(item.data.idr))
					sdt.put(item.data.idr, new ArrayList<City>());
				
				sdt.get(item.data.idr).add(item.data);
				item.data = new City();
				return true;
			}}, null);
	}
 	
 	public List<KeyValue> regAdt(){
 		List<KeyValue> result = new ArrayList<KeyValue>();
		
		for(Regions r :  rdt.values())
			result.add(new KeyValue(r.id, r.name));
			
		Collections.sort(result, new Comparator<KeyValue>() {
			@Override public int compare(KeyValue lhs, KeyValue rhs) { return ((String) lhs.value).compareTo((String) rhs.value); }});
		
		return result;
 	}
 	
 	public List<KeyValue> regCities(String rid){
 		List<KeyValue> result = new ArrayList<KeyValue>();
 		
 		if(sdt.containsKey(rid))
 			for(City c : sdt.get(rid))
 				result.add(new KeyValue(c.id, c.name));
 		
 		Collections.sort(result, new Comparator<KeyValue>() {
			@Override public int compare(KeyValue lhs, KeyValue rhs) { return ((String) lhs.value).compareTo((String) rhs.value); }});
 		
 		return result;
 	}
}
