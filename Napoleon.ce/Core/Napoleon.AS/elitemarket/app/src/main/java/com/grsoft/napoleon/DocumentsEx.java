package com.grsoft.napoleon;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Toast;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;

public class DocumentsEx extends DocumentsPrint {
	@Override
	protected void doCreate() {
		if(DocType.getCurDoc() == OrderDoc.instance() 
				&& NapoleonEx.outDebs.containsKey(org.getData().id))
			Toast.makeText(this, R.string.order_can_rejected, Toast.LENGTH_SHORT).show();
		super.doCreate();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		MenuItem item = menu.findItem(R.id.itMakeSale);
		
		if(item != null)
			item.setVisible(false);
	}
}
