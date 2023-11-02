package com.grsoft.napoleon;

public class SalesDetailEx extends SalesDetail {
	@Override
	protected String[] createPrintCaption() {
		return new String[] {"ТТН ТОРГ 12", "Расх.накл.", "Счет-фактура", "Акт приема-передачи" };
	}
}
