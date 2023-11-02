package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class PriceEx extends Price {
	public String remark = "";
	public int order;
	public int colorText;
	public int colorBack;

	public List<PriceQty> whQty = new ArrayList<PriceQty>();
}
