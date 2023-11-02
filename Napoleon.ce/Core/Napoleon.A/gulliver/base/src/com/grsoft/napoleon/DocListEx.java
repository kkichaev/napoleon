package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.VisitDoc;

public class DocListEx extends DocList {

	@Override
	protected void loadConfig(Bundle b) {
		DocType.setCurDoc(OrderDoc.instance());
		super.loadConfig(b);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		if(DocType.getCurDoc() == VisitDoc.instance())
			menu.findItem(R.id.itDelete).setVisible(false);
	}
	
	@Override
	public void selectedType(DocType newDocType) {
		super.selectedType(newDocType);
		
		if(newDocType == VisitDoc.instance())
			btnDelete.setEnabled(false);
		else
			btnDelete.setEnabled(true);
	}
}
