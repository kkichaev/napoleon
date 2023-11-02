package com.grsoft.napoleon;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.Document;

public class DocumentsPrintEx extends DocumentsPrint {
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
			super.onCreateContextMenu(menu, v, menuInfo);
			AdapterView.AdapterContextMenuInfo aMenuInfo = (AdapterContextMenuInfo) menuInfo;
			Document<?> doc = (Document<?>) adapter.getItem(aMenuInfo.position);
			if (doc != null && (doc instanceof SalesImpl)){
				MenuItem item = menu.findItem(R.id.itMakePKO);
				if( item != null )
					item.setVisible(!SalesDetailEx.isNDSFirm(((SalesImpl)doc).getData().supplyercode));
			}
	}
}
