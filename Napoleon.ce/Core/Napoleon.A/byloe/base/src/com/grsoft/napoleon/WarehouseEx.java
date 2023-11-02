package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;

public class WarehouseEx extends Warehouse {

	int whMenuId = 146932;
	int prcMenuId = 146933;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if( !super.onCreateOptionsMenu(menu) )
			return false;
		
		if( OrderImplEx.class.isAssignableFrom(document.getClass()) ) {
			menu.add(Menu.NONE, whMenuId, Menu.NONE, "—клад");
			menu.add(Menu.NONE, prcMenuId, Menu.NONE, "“ип цены");
		}
		return true;
	}

	void makeChoice(final boolean whChoice) {
		CharSequence[] items = null;
		
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		c.key = whChoice ? "—клады" : "“ип÷ен";

		if( ci.read() ) {
			ArrayList<KeyValue> values = new ArrayList<KeyValue>();
			DialogHelper.makeListWithKey(c.value, values, null);
			
			int idx = 0;
			items = new CharSequence[values.size()];
			for(KeyValue kv : values)
				items[idx++] = kv.value.toString();
		}
		ci.close();
		
		if( items != null ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setItems(items, new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(whChoice)
						((OrderImplEx)document).setWhIndex(which);
					else
						((Order)document.getData()).sumType = which;
					notifyDataSetChanged();
				}			
			});
			b.create().show();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemID = item.getItemId(); 
		if( itemID != whMenuId && itemID != prcMenuId )
			return super.onOptionsItemSelected(item);
		
		makeChoice(item.getItemId() == whMenuId);
		return true;
	}

}
