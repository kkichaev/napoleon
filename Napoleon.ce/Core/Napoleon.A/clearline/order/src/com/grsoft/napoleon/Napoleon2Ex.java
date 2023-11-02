package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.impl.GPSGatherImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.gps.GPSUtilNew;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Napoleon2Ex extends NapoleonEx {
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		View view = ((AdapterContextMenuInfo)menuInfo).targetView;
		Object tag =  view.getTag();
		if( tag instanceof OrgFolders )
			return;
		
		Long rowid = (Long) tag;
		
		if (rowid != null ) {
			menu.add(R.string.gpsgathermenu);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		CharSequence title = item.getTitle();
		Long rid = (Long)((AdapterContextMenuInfo)item.getMenuInfo()).targetView.getTag();
		if( title.equals(getString(R.string.gpsgathermenu))) {
			OrgImpl org = new OrgImpl();
			org.read(rid);
			org.close();
			
			GPSGatherImpl impl = new GPSGatherImpl();
			if (impl.init(this, org.getData().id, GPSUtilNew.getLastKnownLocation()))
				impl.open(this);
			
			return true;
		}else
			return super.onContextItemSelected(item);
	}
}	
