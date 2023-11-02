package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.MenuHandler;

import android.view.Menu;
import android.view.MenuItem;

public class DocumentsEx extends Documents {
	protected List<MenuHandler> mainMenu = null;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
		if(cfg.simpleMode > 0 && cfg.simpleModeOrg.length() > 0) {
			if( mainMenu == null )
				mainMenu = createMainMenuList();
			
			for (MenuHandler h : mainMenu) {
				MenuItem i = menu.add(h.name);
				h.initMenu(this, i);
			}
			
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
		if(cfg.simpleMode > 0 && cfg.simpleModeOrg.length() > 0) {		
			for(MenuHandler h : mainMenu)
				if(h.name.equals(item.getTitle())){
					h.handler.run();
					break;
				}
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

	protected List<MenuHandler> createMainMenuList() {
		mainMenu = new ArrayList<MenuHandler>();
		
		mainMenu.add(new MenuHandler(getString(R.string.setting), new Runnable() {			
			@Override public void run() { Setting.open(DocumentsEx.this); }
		}));
		
		mainMenu.add(new MenuHandler(getString(R.string.sync), new Runnable() {			
			@Override public void run() { UpdateDBW.open(DocumentsEx.this); }
		}));

//		mainMenu.add(new MenuHandler(getString(R.string.docs), new Runnable() {			
//			@Override public void run() { showDialog(DLG_DOC); }
//		}));

		mainMenu.add(new MenuHandler(getString(R.string.about), new Runnable() {			
			@Override public void run() { Main.showAbout(DocumentsEx.this); }
		}));

		mainMenu.add(new MenuHandler(getString(R.string.exit), new Runnable() {	@Override public void run() { exit();	}}));
		
		return mainMenu;
	}

	protected void exit() {
		finish();
		((NapoleonAppBase)getApplication()).exit();
		
	}
}
