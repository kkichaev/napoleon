package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.util.ConfigAgama;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class WarehouseEx extends WarehouseNew {
	private static final int SELECT_WH = 1200;
	PriceImpl price = new PriceImpl();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if( !super.onCreateOptionsMenu(menu) )
			return false;
		
		if( document instanceof OrderImplEx )
			menu.add(Menu.NONE, SELECT_WH, Menu.NONE, "Склады");
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if( document instanceof OrderImplEx && document.getRowid() == ExtrasConst.INVALID_ID )
			((OrderImplEx)document).setWh(((ConfigAgama)ConfigManager.getConfig()).whDefault);
	}
	
	@Override
	protected void onResume() {
		int wh = - 1;
		if( document instanceof OrderImplEx )
			wh = ((OrderEx)document.getData()).whIndex;
		super.onResume();
		if( wh != -1 )
			((OrderImplEx)document).setWh(wh);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		price.close();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == SELECT_WH)
			return createWhDialog();
		return super.onCreateDialog(id);
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		return new ZeroFilter();
	}
	
	private Dialog createWhDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Склады");
		
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		
		c.key = "Склад";
		if( ci.read() ) {
			List<CharSequence> list = new ArrayList<CharSequence>();
			DialogHelper.makeList(c.value, list);
			if( list.size() > 0 ) {
				CharSequence[] csa = new CharSequence[list.size()];
				list.toArray(csa);
				
				b.setItems(csa, new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) { selectSklad(which); }
				});
			}
		}
		
		ci.close();
		return b.create();
	}

	protected void selectSklad(int which) {
		((OrderImplEx)document).setWh(which);
		FoldersAdapter.resetCache();
		adapter.buildSet();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if( item.getItemId() == SELECT_WH ) {
			showDialog(SELECT_WH);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			if( !(document instanceof Itemsable) )
				return super.inset(priceRowID, id);
			
			boolean result = false; 
			
			if(price.read(priceRowID))
				result = (((Itemsable)document).getItemValue(price.getData()) > 0);			
			return result;
		}
	}
}
