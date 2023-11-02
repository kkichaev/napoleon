package com.grsoft.dataobjects;

public class RouteItemRow  {
	public int routeIndex;
	public RouteItem item;
	public boolean isFinished = false;
	
	public RouteItemRow(RouteItem item, int routeIndex) {
		this.item = item;
		this.routeIndex = routeIndex;
	}
}
