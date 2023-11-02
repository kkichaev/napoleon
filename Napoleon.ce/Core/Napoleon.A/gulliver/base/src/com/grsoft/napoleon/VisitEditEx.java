package com.grsoft.napoleon;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

public class VisitEditEx extends VisitEdit {
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuItem i = menu.findItem(R.id.itDelete);
		
		if(i != null)
			i.setVisible(false);
	}
}
