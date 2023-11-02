package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="Divisions",keyFields="id")
public class Division extends DataObject {
	public int id;
	public int parent;
	public String name;
	public String description;
	
	public List<DivisionAgent> agents;
}
