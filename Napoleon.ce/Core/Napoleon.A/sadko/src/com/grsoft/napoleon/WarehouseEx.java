package com.grsoft.napoleon;

import android.view.View;
import android.widget.TextView;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;

public class WarehouseEx extends WarehouseNew {
	
	CostStrategyEx cs = null;
	
	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View v = super.getPriceView(node, convertView);
		CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
		int visible = View.GONE;
		TextView tv = (TextView) v.findViewById(R.id.tvSuppl);
		if( cfg.showSupplier ) {
			tv.setText(((PriceEx)price.getData()).supplier);
			visible = View.VISIBLE;
		} 
		tv.setVisibility(visible);
			
		return v;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected int getCost(Price price) {
		if( cs == null )
			cs = (CostStrategyEx) CostStrategy .getInstance((Class<? extends Document<?>>)((document == null) ? null : document.getClass()));

		return cs.getPriceCose(price, document);
	}
}
