package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.PointDoc;
import com.grsoft.dataobjects.RoutePoint;


public class RoutePointImpl extends DbObject<RoutePoint> {
	public PointDoc findDoc(String id){
		PointDoc result = null;
		
		for(PointDoc i : data.docs)
			if(i.number.equals(id)){
				result = i;
				break;
			}
			
		return result;
	}
	
	public static boolean isRouteComplete(){
		boolean result = true;
		DispathImpl d = new DispathImpl();
	
		for(RoutePoint r : collectRoute())
			if(!d.readFromId(r.id) || !d.isReadyToSend()){
				result = false;
				break;
			}
			
		d.close();
		
		return result;
	}
	
	public static List<RoutePoint> collectRoute(){
		final List<RoutePoint> result = new ArrayList<RoutePoint>();
		
		DataTraveler.travel(RoutePoint.class, new DataTraveler.Travel<RoutePoint>() {

			@Override
			public boolean travel(DataTraveler<RoutePoint> item) {
				result.add(item.data);
				return true;
			}
			
			@Override public boolean isDataNewInstance() { return true; }}, null);
		
		return result;
	}

}
