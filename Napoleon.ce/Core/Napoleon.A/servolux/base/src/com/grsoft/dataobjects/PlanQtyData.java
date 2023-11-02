package com.grsoft.dataobjects;

public class PlanQtyData {
	/**
	 * количество плана
	 */
	public int qty;
	
	/**
	 * изменение плана
	 */
	public int changes;
	
	public int inPack;
	
	public String group;
	
	public PlanQtyData(int q, int c, String group, int inPack) {
		qty = q; changes = c; 
		this.group = group;
		this.inPack = inPack;
	}
}
