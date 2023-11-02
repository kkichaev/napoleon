package com.grsoft.dataobjects;

public class OrderEx extends Order implements OrderBase {
	public String whName = "";	
	public int whIndex;

	public String getWhName() { return whName; }
	public void setWhName(String name) { whName = name; }

	public int getWhIndex() { return whIndex; }
	public void setWhIndex(int index) { whIndex = index; }
}
