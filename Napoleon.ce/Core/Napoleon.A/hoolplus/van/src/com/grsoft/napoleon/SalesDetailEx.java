package com.grsoft.napoleon;

import java.util.List;

public class SalesDetailEx extends SalesDetail {
	@Override
	protected void addPrintItems(List<String> items) {
		super.addPrintItems(items);
		
		items.add("Удостоверение качества");
	}
}
