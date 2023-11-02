package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.SalesEx;
import com.grsoft.napoleon.modules.print.NPrinter;

import android.os.Bundle;
import android.view.View;

public class SalesDetailEx extends SalesDetail {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnSend.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected String[] createPrintCaption() {
		List<String> items = new ArrayList<String>();
		
		items.add(NPrinter.TORG_12_CAPTION);
		SalesEx s = (SalesEx) doc.getData();
		
		if(s.schFactNumber.length() > 0) {
			items.add(NPrinter.SCHET_FACT_CAPTION);
			if (Features.UPD)
				items.add(NPrinter.UPD_CAPTION);
		}
		
		addPrintItems(items);
		String[] result = new String[items.size()];
		result = items.toArray(result);
		
		return result;
	}
	
	@Override
	public void postSendExecute(boolean result) {
		if(result)
			doc.read(doc.getRowid(), false);
	}
}
