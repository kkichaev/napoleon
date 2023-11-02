package com.grsoft.util;

public class BarcodeFilter extends PriceTextFilter {
	@Override
	protected void makeSearchStr(String cond, StringBuilder sbWhere) {
		sbWhere.append("(").append("barcode LIKE '%").append(cond).append("%' )");
	}
}
