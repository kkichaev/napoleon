package com.grsoft.util;

public abstract class Filter {
	protected String name;
	protected String where = "";
	
	public Filter(String name){
		this.name = name;
	}
	
	public boolean inset(long priceRowID, String id){ return true; }
	public String getWhereStr() { return where; }
	public String getName() { return name; } 
}
