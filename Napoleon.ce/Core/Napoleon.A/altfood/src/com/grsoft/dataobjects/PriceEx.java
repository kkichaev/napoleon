package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class PriceEx extends Price {
	public List<PriceQty> whQty = new ArrayList<PriceQty>();
	public List<PriceUnit> units  = new ArrayList<PriceUnit>();
	
	public String level = "";
}
