package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class PriceEx extends Price {
	public String orgid = ""; 
	public String packname = "";
	public String descr = "";
	public String article = ""; 

	public List<PriceQtyItem> whQty = new ArrayList<PriceQtyItem>();
	public int cantdiv = 0;
	
	public String itemGroup = "";
}
