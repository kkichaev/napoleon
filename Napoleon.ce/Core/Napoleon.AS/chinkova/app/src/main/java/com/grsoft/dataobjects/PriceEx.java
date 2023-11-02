package com.grsoft.dataobjects;

public class PriceEx extends Price {
	public String orgid = ""; 
	public String descr = "";
	public String article = ""; 

	public int cantdiv = 0;
	
	public String itemGroup = "";

	public String category = "";
	public String planCategory = "";
	
	public boolean inCateg() { return category.equals("1"); }
	public boolean inPlan() { return planCategory.equals("×"); }
}
