package com.grsoft.napoleon;

import com.grsoft.util.MenuHandler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class NapoleonEx extends Napoleon {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if( mainMenu == null )
			mainMenu = createMainMenuList();
		
		for (MenuHandler h : mainMenu)
			menu.add(h.name);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		for(MenuHandler h : mainMenu)
			if(h.name.equals(item.getTitle())){
				h.handler.run();
				break;
			}
		
		return true;
	}
}
