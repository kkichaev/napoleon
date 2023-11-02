package com.grsoft.napoleon;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.PriceTreeNodeEx;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Agents;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;

import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {
	PriceImpl pi = new PriceImpl();
	boolean isDealer = false;
	
	@SuppressWarnings("unchecked")
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
		if(document != null && document.getRowid() != ExtrasConst.INVALID_ROWID && ret.getFilter(ZeroCostFilter.NAME) == null)
			ret.putFilter(new ZeroCostFilter(document.getId(), CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass())));
		return ret;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isDealer = Agents.isDealer();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void createDocument() {
		if(docRowId == ExtrasConst.INVALID_ROWID) {
			document = OrderDoc.instance().create();
		} else
			super.createDocument();
	}
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		if(isDealer && (type == COLUMN_QTY_WH || type == COLUMN_COST || type == COLUMN_SUM || type == COLUMN_QTY_WH_ORD || type == COLUMN_QTY_WH_PACK || type == COLUMN_COST_SUM) ) {
			textView.setText("");
		} else
			super.setTextColumnValue(textView, type, price);
	}
	
	@Override
	protected FoldersAdapter createAdapterInstance() {
		return new Adapter(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		pi.close();
	}
	
	class ZeroCostFilter extends Filter {
		static final String NAME = "ZERO_COST"; 
		
		CostStrategy costStrategy;
		public ZeroCostFilter(String id, CostStrategy cs) {
			super(NAME + id);
			costStrategy = cs;
		}
		
		@Override
		public boolean inset(long priceRowID, String id) {
			pi.read(priceRowID);
			int cost = costStrategy.getItemCost(pi.getData(), document);
			return cost > 0;
		}
	}

	class Adapter extends FoldersAdapter {

		public Adapter(WarehouseManager warehouse) {
			super(warehouse);
		}
		
		@Override
		public PriceTreeNode createPriceTreeNode(TreeNode parent, long priceRowId, String name, String id) {
			pi.read(priceRowId);
			PriceTreeNodeEx ret = new PriceTreeNodeEx(parent, priceRowId, name, id);
			ret.rang = ((PriceEx)pi.getData()).rang;
			return ret;
		}
	}
}
