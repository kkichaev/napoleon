package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.util.MenuHandler;

public class NapoleonEx extends Napoleon {

	@Override
	protected ArrayList<MenuHandler> createDocMenuList() {
		ArrayList<MenuHandler> ret = super.createDocMenuList(); 
		ret.add(new MenuHandler("Планы", new Runnable() {
			@Override public void run() { Plans.open(NapoleonEx.this); }
		}));
		return ret;
	}
}
