package com.grsoft.napoleon;

import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
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
			if(fe.black > 0)
				return new String[] {"nakl_black"};
			return fe.upd > 0 ? new String[] { NPrinter.UPD_CAPTION } : 
				new String[] {NPrinter.TORG_12_CAPTION, NPrinter.SCHET_FACT_CAPTION};
		}
		return super.createPrintCaption();
	}

	@Override
	public void onBackPressed() {
		((SalesEx)doc.getData()).compleete = (((SalesImplEx)doc).isCompleete()) ? 1 : 0;
		doc.write();

		super.onBackPressed();
	}

	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new Adapter());
	}

	@Override
	public void send() {
		if(!((SalesImplEx)doc).isCompleete())
			return;
		super.send();
	}

	class Adapter extends OrderItemsAdapter {
		@Override int getResourceID() { return R.layout.salesdetail_row; }
	}
}
