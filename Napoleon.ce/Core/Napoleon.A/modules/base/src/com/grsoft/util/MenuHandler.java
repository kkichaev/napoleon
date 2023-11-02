package com.grsoft.util;

import android.content.Context;
import android.view.MenuItem;

public class MenuHandler {
	public MenuHandler(String name, Runnable handler) {
		this.name = name;
		this.handler = handler;
	}
	
	public String name;
	public Runnable handler;
	
	public void initMenu(Context context, MenuItem item) {
		
	}
}
