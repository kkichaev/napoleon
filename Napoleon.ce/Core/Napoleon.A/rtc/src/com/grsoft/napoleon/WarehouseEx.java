package com.grsoft.napoleon;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;

import android.view.View;
import android.widget.ImageView;

public class WarehouseEx extends WarehouseNew {
	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }

	@Override
	protected void setName(View view, Price p, int linesCount, PriceTreeNode node) {
		super.setName(view, p, linesCount, node);

		ImageView iv = (ImageView)view.findViewById(R.id.iAction);
		if( iv != null ) {
			iv.setImageResource( ((PriceEx)p).top > 0  ? R.drawable.action : R.drawable.empty );
		}
	}
}
