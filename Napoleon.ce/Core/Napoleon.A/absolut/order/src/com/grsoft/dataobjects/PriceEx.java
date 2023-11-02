package com.grsoft.dataobjects;

import java.util.List;

public class PriceEx extends Price implements PriceBase {
	public List<PriceQtyItem> whQty;

	public List<PriceQtyItem> getWhQty() { return whQty; }
}
