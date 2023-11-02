package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.MenuHandler;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;

public class NapoleonMain extends NapoleonEx {
	private PricePrintHelper pph;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		pph = new PricePrintHelper(this); 
	}
	
	public static void open(Context context){
		Intent intent = new Intent(context, NapoleonMain.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK); 
		context.startActivity(intent);
	}
	
	@Override
	protected ArrayList<MenuHandler> createMainMenuList() {
		ArrayList<MenuHandler> ret = super.createMainMenuList();
		int pos = ret.size() - 2;
		if(pos < 0)
			pos = 0;
		
		ret.add(pos, pph.getMenuHandler());
		return ret;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.chooseorgdlg)
			return pph.createrOrgSelector();
		else if( id == R.id.wait_for_print_dlg)
			return SelectPrinFormDlg.createWaitDlg(this);
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
		
		if(cfg.simpleMode){
			SimpleMode.open(this);
			DocType.setCurDoc(SalesDoc.instance());
		}
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == R.id.chooseorgdlg)
			pph.updateOrgList(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}
}