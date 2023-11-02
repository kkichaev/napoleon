package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class PriceEx extends Price {
	public int base;
	public List<PriceUnit> units;
	public List<PriceWhData> whQty = new ArrayList<PriceWhData>();
}
