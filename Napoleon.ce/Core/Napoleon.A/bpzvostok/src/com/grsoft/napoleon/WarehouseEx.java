package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.RezervQty;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	
	HashMap<String, Integer> rezQty = new HashMap<String, Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		DataTraveler.travel(RezervQty.class, new DataTraveler.Travel<RezervQty>() {

			@Override
			public boolean travel(DataTraveler<RezervQty> item) {
				rezQty.put(item.data.id, item.data.qty);
				return true;
			}
		}, "");
	}
	
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
	@Override
	protected BaseAdapter createListAdapter() {
		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx)oi.getData();
		o.id = document.getId();
		oi.read();
		oi.close();
		return new Adapter(this, o.base);
	}
	
	@Override protected int getItemLayoutId() {
		if (DocType.getCurDoc() == OrderDoc.instance())
			return R.layout.priceitemrowex;
		else
			return super.getItemLayoutId();
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		if (DocType.getCurDoc() == OrderDoc.instance()) {
			View res = super.getPriceView(node, convertView);
			price.read(node.getRowid());
			price.close();
			Integer qty = ((OrderImplEx)document).getItem2Value(price.getData());
			TextView tv = (TextView)res.findViewById(R.id.tvRezerv);
			tv.setText(qty == null ? "" : Util.IntToScaleStr(qty, Consts.QTY_SCALE));
			return res;
		}else
			return super.getPriceView(node, convertView);
	}
}

class Adapter extends FoldersAdapter {
	int base;
	
	public Adapter(WarehouseManager warehouse, int base) {
		super(warehouse);
		this.base = base;
		
		resetCache();
	}
	
	@Override
	public String getWhereStr() {
		String res = super.getWhereStr();
		if( base > 0 ) {
			if(res.length()>0)
				res += " AND ";
			res += "base=" + Integer.toString(base);
		}
		return res;
	}
}
