package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.MenuItem;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class WarehouseEx extends WarehouseNew {
	
	static int whIndex = 0;
	
	@Override
	protected Filter createZeroPositionFilter() {
		if( document instanceof OrderImplEx ) {
			if( whIndex != ((OrderEx)document.getData()).whIndex ) {
				whIndex = ((OrderEx)document.getData()).whIndex;
				FoldersAdapter.resetCache();
			}
		} else if( whIndex != 0 ) {
			whIndex = 0;
			FoldersAdapter.resetCache();			
		}
		return new ZeroFilter();
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
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		super.onCreateOptionsMenu(menu);
//		
//		if( document instanceof OrderImplEx && document.getRowid() == ExtrasConst.INVALID_ROWID )
//			menu.add(Menu.NONE, R.id.select_sklad_dlg, Menu.NONE, getString(R.string.select_sklad));
//		return true;
//	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.select_sklad_dlg){
			showDialog(R.id.select_sklad_dlg);
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.select_sklad_dlg)
			return createSkladDialog();
		return super.onCreateDialog(id);
	}
	
	private Dialog createSkladDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Склады");
		
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		
		c.key = "Склады";
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
		OrderEx ordex = (OrderEx) document.getData();
		ordex.whIndex= which;
		FoldersAdapter.resetCache();
		adapter.buildSet();
	}
}
