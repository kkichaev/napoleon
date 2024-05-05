package com.grsoft.dataobjects;

public class OrderEx extends Order {
	public int cashToDriver = 0;
	public String shipping = "";
	public int priceCost = 0;

	public int vanSell = 0;
	public int vanPrn = 0;
	public int desTime = 0;

	public boolean isVan() { return vanSell > 0 || vanPrn > 0;}
}
