package com.grsoft.util;

import android.content.Context;
import android.view.MenuItem;

public class MenuActionHandler extends MenuHandler {
	public int idres;
	
	public MenuActionHandler(String name, Runnable handler, int idres) {
		super(name, handler);
		this.idres = idres;
	}
	
	@Override
	public void initMenu(Context context, MenuItem item) {
		super.initMenu(context, item);
		
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		item.setIcon(context.getResources().getDrawable(idres));
	}

}
