package com.grsoft.napoleon;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.PriceEx;

import android.view.View;
import android.widget.ImageView;

public class WarehouseEx extends WarehouseNew {
	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View ret = super.getPriceView(node, convertView);
		PriceEx pe = (PriceEx)price.getData();
		ImageView iv = (ImageView)ret.findViewById(R.id.ivHref);
		HrefHelper.setImageView(iv, pe.href, false);
		return ret;
	}
}
