package com.grsoft.napoleon;

import com.grsoft.napoleon.modules.print.NPrinter;


public class SalesDetailEx extends SalesDetail {
	@Override
	protected String[] createPrintCaption() {
		return new String[] {NPrinter.TORG_12_CAPTION, NPrinter.SCHET_FACT_CAPTION, NPrinter.UPD_CAPTION };
	}
}
