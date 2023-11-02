package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.OrgFolderItem;

import android.view.Menu;
import android.view.MenuItem;

public class MainEx extends Main {
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		if(mode == FOLDER_VIEW && !((FoldersMainAdapter)foldersMainAdapter).isTopLevel()) {
			MenuItem mi = menu.add(Menu.NONE, R.id.map_id, Menu.NONE, "map");
			mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			mi.setIcon(getResources().getDrawable(R.drawable.globus));
		}
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.map_id) {
			ArrayList<String> ids = new ArrayList<String>();
			
			for(OrgFolderItem i : ((FoldersMainAdapter)foldersMainAdapter).currentFolder().items)
				ids.add(i.name);
			
			MapActivity.open(this, ids);
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void setAdapterMode() {
		super.setAdapterMode();
		
		invalidateOptionsMenu(); 
	}
	
	@Override
	public void onAdapterViewAdjusted() {
		super.onAdapterViewAdjusted();
		
		invalidateOptionsMenu();
	}
}
