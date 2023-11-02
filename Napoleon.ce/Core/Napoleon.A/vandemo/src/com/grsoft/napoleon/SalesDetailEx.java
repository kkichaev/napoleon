package com.grsoft.napoleon;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.modules.print.NPrinter;

public class SalesDetailEx extends SalesDetail {
	
	@Override
	protected String[] createPrintCaption() {
		boolean isBlack = (doc.getData().params & ParamState.ofCash) != 0;
		return isBlack ? new String[] { "Накладная" } : 
				new String[] {NPrinter.TORG_12_CAPTION, NPrinter.SCHET_FACT_CAPTION };
	}
}
