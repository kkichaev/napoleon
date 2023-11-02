package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgFolders;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class NapoleonEx extends Napoleon {
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		View view = ((AdapterContextMenuInfo)menuInfo).targetView;
		Object tag =  view.getTag();
		if( tag instanceof OrgFolders )
			return;
		
		Long rowid = (Long) tag;		
		if (rowid != null && !isPotencialOrg(rowid) ) {
			menu.add(Menu.NONE, R.id.sku_report, Menu.NONE, "Отчет по SKU");
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.sku_report) {
			Long rid = (Long)((AdapterContextMenuInfo)item.getMenuInfo()).targetView.getTag();
			SunnySKUReport.open(this, rid);
			return true;
		}
		return super.onContextItemSelected(item);
	}
}
