package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.GPSPos;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.dataobjects.Org;
import com.grsoft.util.Consts;

public class MapData {
	public List<Object> points = new ArrayList<Object>();
	//public List<Object> stops = new ArrayList<Object>();
	public List<Object> executed = new ArrayList<Object>();
	//public List<Object> pendings = new ArrayList<Object>();
	//public List<Object> stepoints = new ArrayList<Object>();
	public List<Object> agentsinfields = new ArrayList<Object>();
	
	public List<Object> routePoints = new ArrayList<Object>();

	public static class GPSPosMap {
		public Date date;
		public double latitude;
		public double longitude;
		public double speed;
		public int isGSM;
		public int satellite;
		public int accuracy;
		public Date stltime;
		public int isMock = 0;

		public GPSPosMap(GPSPos p) {
			this.date = p.date;
			this.latitude = (double) p.latitude / Consts.GPS_SCALE;
			this.longitude = (double) p.longitude / Consts.GPS_SCALE;
			this.speed = (double) p.speed / 100;
			this.isGSM = p.isGSM;
			this.satellite = p.satellite;
			this.accuracy = p.accuracy;
			this.stltime = p.stltime;
			this.isMock = p.isMock;
		}
	}
	
	public static class RoutePoint {
		public int idx;
		public double latitude;
		public double longitude;
		public double speed;
		public String title;
		public Date date;
	}

	public static class Executed {
		public int idx;
		public Org org;
		public Date date;
		public double latitude;
		public double longitude;
	}
	
	public static class AgentInField {
		public int idx;
		public ManagerAgent agent;
		public Date date;
		public double latitude;
		public double longitude;
	}
}
