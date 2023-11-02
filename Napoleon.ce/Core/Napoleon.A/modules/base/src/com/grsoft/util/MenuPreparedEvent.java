package com.grsoft.util;

import java.util.ArrayList;

import android.app.Activity;

public class MenuPreparedEvent extends ArrayList<MenuPrepareHitching> {

	private static final long serialVersionUID = 1L;

	public void menuPrepared(ArrayList<MenuHandler> menu, Activity activity) {
		for(MenuPrepareHitching h : this)
			h.menuPrepared(menu, activity);
	}
}
