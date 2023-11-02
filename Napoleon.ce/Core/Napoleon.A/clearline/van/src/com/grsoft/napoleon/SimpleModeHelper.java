package com.grsoft.napoleon;

import java.util.ArrayList;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.util.ReceiveRemnants;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.MenuHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SimpleModeHelper {
	private static final int DLG_MAIN_MENU = 1;
	private static final int DLG_DOC = 2;
	private ArrayList<MenuHandler> mainMenu = null;
	private ArrayList<MenuHandler> docMenu = null;
	private Context context;
	private PricePrintHelper pph;
	
	public SimpleModeHelper(Activity activity) {
		this.context = activity;
		pph = new PricePrintHelper(activity);
	}

	public Dialog onCreateDialog(int id){
		switch(id){
		case DLG_MAIN_MENU:
			return createMainMenuDlg();
		case DLG_DOC:
			return createDocMenuDlg();
		case R.id.chooseorgdlg:
			return pph.createrOrgSelector();
		case R.id.wait_for_print_dlg:
			return SelectPrinFormDlg.createWaitDlg(context);
			
		default: return null;
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		if( mainMenu == null )
			mainMenu = createMainMenuList();
		
		for (MenuHandler h : mainMenu)
			menu.add(h.name);
		
		return true;
	}
	
	ReceiveRemnants remnantsReceiver = null;
	void updateRemnants() {
		if( remnantsReceiver == null ) {
			remnantsReceiver = new ReceiveRemnants((Activity) context, new ReceiveRemnants.TaskDoneHandler() {
				@Override public void finish(NetworkAsyncTask task) {
					if( remnantsReceiver == task )
						remnantsReceiver = null;
				}

				@Override
				public void start() {
				}
			});
			remnantsReceiver.execute((Void[])null);
		}
	}
	
	protected ArrayList<MenuHandler> createDocMenuList() {
		docMenu = new ArrayList<MenuHandler>();

		docMenu.add(new MenuHandler(context.getString(R.string.doc_list), new Runnable() {			
			@Override public void run() { DocList.open(context); }
		}));
		
		docMenu.add(new MenuHandler(context.getString(R.string.msg_list), new Runnable() {			
			@Override public void run() { Messages.open(context); }
		}));
		
		if(Features.REPORT_REQUEST)
			docMenu.add(new MenuHandler(context.getString(R.string.report_list), new Runnable() {			
				@Override public void run() { ReportList.open(context); }
			}));
		
		docMenu.add(new MenuHandler(context.getString(R.string.sales_report), new Runnable() {			
			@Override public void run() { SalesList.open(context); }
		}));
		
		Napoleon.docMenuPrepared.menuPrepared(docMenu, (Activity) context);
		return docMenu;
	}
	
	protected ArrayList<MenuHandler> createMainMenuList() {
		mainMenu = new ArrayList<MenuHandler>();
		
		mainMenu.add(new MenuHandler(context.getString(R.string.setting), new Runnable() {			
			@Override public void run() { Setting.open(context); }
		}));
		
		mainMenu.add(new MenuHandler(context.getString(R.string.sync), new Runnable() {			
			@Override public void run() { doSync(); }
		}));

		mainMenu.add(new MenuHandler(context.getString(R.string.docs), new Runnable() {			
			@Override public void run() { ((Activity)context).showDialog(DLG_DOC); }
		}));
		
		mainMenu.add(pph.getMenuHandler());

		mainMenu.add(new MenuHandler(context.getString(R.string.about), new Runnable() {			
			@Override public void run() { Napoleon.showAbout((Activity) context); }
		}));

		mainMenu.add(new MenuHandler(context.getString(R.string.exit), new Runnable() {	@Override public void run() { exit();	}}));
		Napoleon.mainMenuPrepared.menuPrepared(mainMenu, (Activity) context);
		return mainMenu;
	}
	
	protected void doSync() {
		if(Features.CHECK_UNCOMPLETE_SCRIPTS && ScriptImpl.hasUncomplete()){
			Toast.makeText(context, R.string.has_uncomplete_scripts, Toast.LENGTH_LONG).show();
			openScriptList();
		}else	
			openUpdateActivity();
	}
	
	private void openScriptList() { ScriptsList.open(context); }
	
	protected void openUpdateActivity() {
		UpdateDBW.open(context);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			if(Features.SHOW_EXIT_WARNING)
				Toast.makeText(context, R.string.ask_to_exit, 
					Toast.LENGTH_LONG).show();
			
			return true;
//		} if( keyCode == KeyEvent.KEYCODE_MENU ) {
//			((Activity)context).showDialog(DLG_MAIN_MENU);
//			return true;
		} else
			return false;
	}
	
	protected Dialog createMainMenuDlg(){
		if( mainMenu == null )
			mainMenu = createMainMenuList();
		
		return createMenuDlg(context.getString(R.string.menu), mainMenu);
	}
	
	protected Dialog createMenuDlg(String title, final ArrayList<MenuHandler> items){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
						
		int idx = 0;
		CharSequence[] titles = new CharSequence[items.size()];
		
		for(MenuHandler mh : items)
			titles[idx++] = mh.name;
		
		builder.setItems(titles, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which){
					items.get(which).handler.run();
			}
		});
		
		return builder.create();
	}
	
	protected Dialog createDocMenuDlg() {
		if (docMenu == null)
			docMenu = createDocMenuList();
				
		return createMenuDlg(context.getString(R.string.docs), docMenu);
	}
	
	protected void exit() {
		if (!((CfgNplW)ConfigManager.getConfig()).isAutostart) {
			Intent intent = new Intent(context, Napoleon.serviceType);
			boolean stopped = context.stopService(intent);
			Log.d(Consts.D_TAG, "Service has been stopped:" + Boolean.toString(stopped));
		}
		
		((Activity)context).finish();
	}

	public void onPrepareDialog(int id, Dialog dialog) {
		if (id == R.id.chooseorgdlg)
			pph.updateOrgList(dialog);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		for(MenuHandler h : mainMenu)
			if(h.name.equals(item.getTitle())){
				h.handler.run();
				break;
			}
		
		return true;
	}
}
