package com.grsoft.dataobjects;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.TableInfo;

@TableInfo(name="ManagerAgent",keyFields="id")
public class ManagerAgent extends DataObject implements Comparable<ManagerAgent> {
	public String id = "";
	public String name = "";
	public String phone = "";
	public String login = "";
	public String password = "";
	public Date date = null;
	
	@Override public int compareTo(ManagerAgent another) { return name.compareTo(another.name); }
	
	@Override public String toString() { return name; }
	
	public static Map<String, ManagerAgent> getAgents() {
		final Map<String, ManagerAgent> agents = new HashMap<String, ManagerAgent>();
		DataTraveler.travel(ManagerAgent.class, new DataTraveler.Travel<ManagerAgent>(true) {

			@Override
			public boolean travel(DataTraveler<ManagerAgent> item) {
				agents.put(item.data.id, item.data);
				return true;
			}
		}, "");
		return agents;
	}
}
