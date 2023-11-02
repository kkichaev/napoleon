package com.grsoft.napoleon;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.Price;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.view.View;
import android.widget.TextView;

public class WarehosueEx extends WarehouseNew {
	
	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View v = super.getPriceView(node, convertView); 
		TextView tv;
		
		Price p = price.getData();
		if(p.qtyInPack == 0)
			p.qtyInPack = Consts.QTY_SCALE;
		if(p.weight == 0)
			p.weight = Consts.WEIGHT_SCALE;
		
		int cost = CostStrategy.defaultInstance.getItemCost(p, document);
		
		int costPack = (int)((long)cost * p.qtyInPack / Consts.QTY_SCALE);  
		tv = (TextView)v.findViewById(R.id.tvPrc1);
		tv.setText(Util.IntToScaleStr(costPack, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		
		int costW = (int)((long)cost * Consts.WEIGHT_SCALE / p.weight);
		tv = (TextView)v.findViewById(R.id.tvPrc2);
		tv.setText(Util.IntToScaleStr(costW, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		
		return v;
	}
}
