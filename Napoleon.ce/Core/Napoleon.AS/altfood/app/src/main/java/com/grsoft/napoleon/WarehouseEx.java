package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class WarehouseEx extends Warehouse {
	private static final String COSTYPE = "costype"; 
	private static final String RESET_FILTER = "rest_filter"; 
	
	boolean restZeroFilter = false;
	int costType;
	
	static public void open(Context context,  int costype) {
		Intent i = new Intent(context, WarehouseEx.class);
		i.putExtra(COSTYPE, costype);
		i.putExtra(RESET_FILTER, true);
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent i = getIntent();
		costType = i.getIntExtra(COSTYPE, -1);
		restZeroFilter = i.getBooleanExtra(RESET_FILTER, false);
		
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void postDocInited() {
		super.postDocInited();
		
		if(document.getRowid() == ExtrasConst.INVALID_ROWID && document instanceof OrderImpl && costType >= 0){
			((OrderImpl)document).getData().sumType = costType;
		}
	}
	
	@Override
	protected void postAdapterInit() {
		OrgImpl org = new OrgImpl();
		org.read("id", document.getData().id);
		String level = ((OrgEx)org.getData()).priceLevel;
		
		if (level.length() > 0 && DocType.getCurDoc() == OrderDoc.instance())
			adapter.putFilter(new PriceLevelFilter(level));
		
		super.postAdapterInit();
	}
	
	static class PriceLevelFilter extends Filter{
		final static String NAME = "PriceLevelFilter";
		public String level;
		
		public PriceLevelFilter(String level) {
			super(NAME);
			this.level = level;
		}
		
		@Override
		public String getWhereStr() {
			return String.format("level='%s'", level);
		}
	}
}
