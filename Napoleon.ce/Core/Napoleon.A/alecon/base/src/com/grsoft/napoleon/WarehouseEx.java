package com.grsoft.napoleon;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;

public class WarehouseEx extends WarehouseNew {
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		if( type == COLUMN_QTY_WH || type == COLUMN_QTY_WH_ORD )
			type = COLUMN_QTY_ORD;
		super.setTextColumnValue(textView, type, price);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		boolean show_zero_filter = Features.SHOW_ZERO_FILTER;
		
		if(DocType.getCurDoc().equals(ReturnDoc.instance()) || 
				DocType.getCurDoc().equals(RemnantsDoc.instance()))
			Features.SHOW_ZERO_FILTER = false;
		BaseAdapter result =  super.createListAdapter();
		
		Features.SHOW_ZERO_FILTER = show_zero_filter;
		
		return result;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);
		
		MenuItem item = menu.findItem(R.id.itZeroFilter);
		
		if(item != null){
			if(DocType.getCurDoc().equals(ReturnDoc.instance()))
				item.setVisible(true);
			else
				item.setVisible(false);
		}
		
		return result;
	}
}
