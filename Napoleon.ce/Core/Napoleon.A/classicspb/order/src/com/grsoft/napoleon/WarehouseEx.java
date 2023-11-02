package com.grsoft.napoleon;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.napoleon.documents.BarcodeDoc;
import com.grsoft.napoleon.documents.DocType;

import android.view.Menu;
import android.view.View;

public class WarehouseEx extends WarehouseBase {

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		if (DocType.getCurDoc() == BarcodeDoc.instance()) {
			for(int i = 0; i < menu.size(); i++)
				menu.getItem(i).setVisible(false);
		}
		
		return true;
	}
	
	@Override
	protected View  getPriceView(PriceTreeNode node, View convertView) {
		View view = super.getPriceView(node, convertView);
		
		if (DocType.getCurDoc() == BarcodeDoc.instance())
			view.findViewById(R.id.llQuant).setVisibility(View.GONE);
		
		return view;
	}
}
