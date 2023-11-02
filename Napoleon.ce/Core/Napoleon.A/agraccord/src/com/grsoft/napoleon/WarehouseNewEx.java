package com.grsoft.napoleon;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.ZeroPositionFilter;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class WarehouseNewEx extends WarehouseNew{
	private static final int COST_TYPE_DLG = 124;
	public static int sumType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sumType = 0;
	}
	
	@Override
	protected int getOptionsMenuId() {
		if(docRowId == ExtrasConst.INVALID_ID)
			return R.menu.warehouse_opt_menuex;
		else
			return super.getOptionsMenuId();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.itCostType){
			showDialog(COST_TYPE_DLG);
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == COST_TYPE_DLG)
			return createCostTypeDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createCostTypeDlg() {
		ArrayList<CharSequence> items = new ArrayList<CharSequence>();
		
		ConfigImpl config = new ConfigImpl(); 
		Config c = config.getData();
		c.key = "¬ид÷ены";
		if( config.read())
			DialogHelper.makeList(c.value, items);
				
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.cost_type);
		
		final String[] items_array = new String[items.size()];
		int sel_item = document.getSumType();
		items.toArray(items_array);
		
		builder.setSingleChoiceItems(items_array, sel_item, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        Toast.makeText(getApplicationContext(), items_array[item], Toast.LENGTH_SHORT).show();

		        if(document instanceof OrderImpl)
		        	((OrderImpl)document).getData().sumType = item;
		        
		        sumType = item;
		        adapter.notifyDataSetChanged();
		        dialog.dismiss();
		    }
		});
		
		return builder.create();
	}
	
	@Override
	protected void createDocument() {
		if(docRowId == ExtrasConst.INVALID_ID)
			document = OrderDoc.instance().create();
		else{
			document = DocType.getCurDoc().create();
			if (!(document instanceof Itemsable)) 
				document = OrderDoc.instance().create();
		}
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		return new Filter(ZeroPositionFilter.NAME) {
			@Override
			public boolean inset(long priceRowID) {
				boolean result = false; 
				PriceImpl price = new PriceImpl();
				if(price.read(priceRowID))
					result = ((Itemsable)document)
						.getItemValue(price.getData()) > 0;
				price.close();
				
				return result;
			}
		};
	}

}
