package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class PriceEx extends Price {
	public List<PriceWhData> whQty = new ArrayList<PriceWhData>();
	public List<UnitItem> units = new ArrayList<UnitItem>();
	public String info = "";

}
