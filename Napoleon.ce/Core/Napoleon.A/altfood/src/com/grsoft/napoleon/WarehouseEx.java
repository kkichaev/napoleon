package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
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

public class WarehouseEx extends WarehouseNew {
	private static final String COSTYPE = "costype"; 
	private static final String RESET_FILTER = "rest_filter"; 
	
	static int whIndex = 0;

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
			if( restZeroFilter )
				return true;
			
			if( !(document instanceof Itemsable) )
				return super.inset(priceRowID, id);
			
			boolean result = false; 			
			if(price.read(priceRowID))
				result = (((Itemsable)document).getItemValue(price.getData()) > 0);			
			return result;
		}
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
