package com.grsoft.napoleon;

public class SalesDetailEx extends SalesDetail {
	@Override
	protected String[] createPrintCaption() {
		return new String[] {"Накладная", "ТТН ТОРГ 12", "Счет-фактура" };
	}
}
