package com.grsoft.manager;

import com.grsoft.napoleon.util.CfgMgr;
import com.grsoft.napoleon.util.ConfigManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Menu;
import android.widget.ListView;

public class MapTypeSelectorHelper {
	public Dialog createSelectMapTypeDlg(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setSingleChoiceItems(R.array.mapTypeNames, 0, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String[] vals = context.getResources().getStringArray(R.array.mapTypeValues);
				
				CfgMgr cfg = (CfgMgr) ConfigManager.getConfig();
				cfg.maptype = vals[which];
				ConfigManager.save();
				
				dialog.dismiss();
			}
		});
		
		return builder.create();
	}
	
	public void addMenuItem(Menu menu){
		menu.add(Menu.NONE, R.id.itMapType, Menu.NONE, R.string.maptype);
	}
	
	public void prepareSelectMapTypeDlg(Dialog dialog){
		ListView lv = ((AlertDialog)dialog).getListView();
		String[] vals = dialog.getContext().getResources().getStringArray(R.array.mapTypeValues);
		CfgMgr cfg = (CfgMgr) ConfigManager.getConfig();
		
		int i = 0;
		
		for(;i < vals.length; i++ ){
			if(vals[i].equals(cfg.maptype))
				break;
		}
		
		lv.setItemChecked(i, true);

	}
}
