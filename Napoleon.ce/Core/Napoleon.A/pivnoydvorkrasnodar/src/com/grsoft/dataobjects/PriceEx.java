package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class PriceEx extends Price {
	public int firm;
	public List<PriceWhData> whQty = new ArrayList<PriceWhData>();
	public int box = 0;
}
