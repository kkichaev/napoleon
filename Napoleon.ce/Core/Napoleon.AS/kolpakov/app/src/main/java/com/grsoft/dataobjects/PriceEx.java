package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price implements Comparable<PriceEx> {
//	public String ido = "";

	public int marked = 0;

	public String barcode = "";

	@Scale(value = Consts.SUM_SCALE)
	public int mrc = 9;

	@Override
	public int compareTo(PriceEx o) {
		return name.compareTo(o.name);
	}

	@Override
	public String toString() { return name; }

	public void putQty(int sqty, int sklad) {
		if(sklad == 0) qty = sqty;
		else {
			while(whQty.size() < sklad) {
				PriceQtyItem pqi = new PriceQtyItem();
				whQty.add(pqi);
			}
			whQty.get(sklad-1).qty = sqty;
		}
	}
}
