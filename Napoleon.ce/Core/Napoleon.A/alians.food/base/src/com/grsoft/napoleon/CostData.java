package com.grsoft.napoleon;

class CostData {
	public String name;
	public String id;
	public int index;
	
	public CostData(String name, String id, int index) {
		this.name = name;
		this.id = id;
		this.index = index;
	}
	
	@Override
	public String toString() {
		return name;
	}
}