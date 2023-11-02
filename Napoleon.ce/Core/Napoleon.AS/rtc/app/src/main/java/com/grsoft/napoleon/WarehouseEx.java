package com.grsoft.napoleon;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.FoldersAdapter;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class WarehouseEx extends Warehouse {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		FoldersAdapter.resetCache();
		super.onCreate(savedInstanceState);
	}

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
