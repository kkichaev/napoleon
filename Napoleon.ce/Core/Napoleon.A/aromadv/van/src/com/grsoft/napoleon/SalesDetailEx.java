package com.grsoft.napoleon;

import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.napoleon.modules.print.NPrinter;

public class SalesDetailEx extends SalesDetail {
	@Override
	protected String[] createPrintCaption() {
		FirmImpl fi = new FirmImpl();
		FirmEx fe =(FirmEx) fi.getData();
		fe.id = ((Sales)doc.getData()).supplyercode;
		boolean readed = fi.read(); 
		fi.close();
		if(readed) {
			return fe.upd > 0 ? new String[] { NPrinter.UPD_CAPTION } : 
				new String[] {NPrinter.TORG_12_CAPTION, NPrinter.SCHET_FACT_CAPTION};
		}
		return super.createPrintCaption();
	}
}
