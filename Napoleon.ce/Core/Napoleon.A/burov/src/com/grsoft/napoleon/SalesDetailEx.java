package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.view.Menu;
import android.view.MenuItem;

import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.FirmImpl;

public class SalesDetailEx extends SalesDetail {
	@Override
	protected String[] createPrintCaption() {
		List<String> list = new ArrayList<String>();
		
		if( isNDSFirm(((Sales)doc.getData()).supplyercode)){
			list.add("ÒÒÍ ÒÎÐÃ 12");
			list.add("Ñ÷åò-ôàêòóðà");
		}
		else
			list.add("ÒÒÍ ÒÎÐÃ 12");

		return list.toArray(new String[list.size()]);
	}
	
	public static boolean isNDSFirm(String supplyercode){
		boolean result = false;
		FirmImpl firmImpl = new FirmImpl();
		firmImpl.getData().id = supplyercode;
		
		if (firmImpl.read())
			result = ((FirmEx)firmImpl.getData()).nds > 0;
			
		firmImpl.close();
		
		return result;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem item = menu.findItem(MNU_PKO_ID);
		
		if (item != null)
			item.setVisible(!isNDSFirm(((Sales)doc.getData()).supplyercode));
		
		return true;
	}
}
