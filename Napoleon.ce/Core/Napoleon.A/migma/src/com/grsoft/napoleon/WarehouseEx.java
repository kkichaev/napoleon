package com.grsoft.napoleon;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseManager;

public class WarehouseEx extends WarehouseNew {

	PriceImpl priceCache = new PriceImpl();
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View v = super.getPriceView(node, convertView);
		TextView tv = (TextView)v.findViewById(R.id.tvRemark);
		if( tv != null ) {
			tv.setVisibility(View.VISIBLE);
			linesController.prepareTextView(tv);
			PriceEx p = (PriceEx) price.getData();

			int color = Util.GrServerColorToSystem(p.colorText);
			tv.setTextColor(color);
			color = Util.GrServerColorToSystem(p.colorBack);
			tv.setBackgroundColor(color);

			tv.setText(p.remark);

			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics metrics = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(metrics);

			int cellWidth = metrics.widthPixels / 3;
			tv.setWidth(cellWidth);
			((TextView)v.findViewById(R.id.tvPriceItemName)).setWidth(cellWidth);
		}
		return v;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		priceCache.close();
	}
	
	
	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		View v = super.getFolderView(node, convertView);
		TextView tv = (TextView)v.findViewById(R.id.tvRemark);
		if( tv != null ) {
			tv.setVisibility(View.GONE);
		}
		return v;
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = new Adapter(this);
		if( Features.SHOW_ZERO_FILTER )
			ret.putFilter(createZeroPositionFilter());
		return ret;
	}
	
	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }

	class Adapter extends FoldersAdapter {
		public Adapter(WarehouseManager wm) {
			super(wm);
		}
		
		@Override
		public PriceTreeNode createPriceTreeNode(TreeNode parent, long priceRowId, String name, String id) {
			priceCache.read(priceRowId);
			return new PriceTreeEx(parent, priceRowId, name, id, ((PriceEx)priceCache.getData()).order);
		}
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
