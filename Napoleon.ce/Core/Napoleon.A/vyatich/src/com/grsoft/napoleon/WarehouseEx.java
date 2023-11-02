package com.grsoft.napoleon;

import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseManager;

public class WarehouseEx extends WarehouseNew {
	
	PriceImpl priceCache = new PriceImpl();

	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		if( type == COLUMN_COST ){
			@SuppressWarnings("unchecked")
			int value =  CostStrategy.getInstance(
					(Class<? extends Document<?>>) document.getClass()).getItemCost(price, (Document<?>) document);
			
			value = (int)((long)value * price.qtyInPack / Consts.QTY_SCALE);
			textView.setText(Util.IntToScaleStr(value, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			return;
		}
		super.setTextColumnValue(textView, type, price);
	}

	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = new Adapter(this);
		if( Features.SHOW_ZERO_FILTER )
			ret.putFilter(createZeroPositionFilter());
		return ret;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		priceCache.close();
	}

	class Adapter extends FoldersAdapter {
		public Adapter(WarehouseManager wm) {
			super(wm);
		}
		
		@Override
		public PriceTreeNode createPriceTreeNode(TreeNode parent, long priceRowId, String name, String id) {
			priceCache.read(priceRowId);
			return new PriceTreeEx(parent, priceRowId, name, id, ((PriceEx)priceCache.getData()).volume);
		}
	}

	class PriceTreeEx extends PriceTreeNode {
		
		int cmp;

		public PriceTreeEx(TreeNode parent, long rid, String name, String id, int cmp) {
			super(parent, rid, name, id);
			this.cmp = cmp;
		}
		
		@Override
		public int compareTo(TreeNode treeNode) {
			if( treeNode instanceof PriceTreeEx )
				return cmp - ((PriceTreeEx)treeNode).cmp;
			return super.compareTo(treeNode);
		}
	}
}
