package com.grsoft.napoleon;

import com.grsoft.napoleon.modules.print.NPrinter;

public class SalesDetailEx extends SalesDetail {
	@Override
	protected String[] createPrintCaption() {
		return new String[] {"Накладная", "ТТН ТОРГ 12", "Счет-фактура", NPrinter.UPD_CAPTION };
	}
}
