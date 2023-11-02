package com.grsoft.napoleon;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.VisitDoc;

public class DocumentsPrintEx extends DocumentsPrint {
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		MenuItem item = menu.findItem(R.id.itMakePKO);
		if(item != null)
			item.setVisible(false);
		
		if(DocType.getCurDoc() == VisitDoc.instance())
			menu.findItem(R.id.itDelete).setVisible(false);
	}
	
	@Override
	protected String getStopMessage() {
		return ((OrgEx)org.getData()).stopMsg;
	}
}
