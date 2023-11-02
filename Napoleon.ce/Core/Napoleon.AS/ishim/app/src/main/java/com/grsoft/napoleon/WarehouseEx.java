package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgPropImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WarehouseEx extends Warehouse {

	String costType = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		costType = OrderHelper.getSumType(document);
	}
	
	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }
	
	@Override
	protected FoldersAdapter createAdapterInstance() {
		return new FoldersAdapterEx(this);
	}
	
	@Override
	protected void postAdapterInit() {
		if(DocType.getCurDoc() == OrderDoc.instance() || DocType.getCurDoc() == RemnantsDoc.instance()) {
			OrgPropImpl p = new OrgPropImpl();
			if(p.read("id", document.getId()) && p.getData().matrix.length() > 0)
				applayMatrix(p.getData().matrix);
			else {
				StringBuilder sb = new StringBuilder();
				ConfigImpl cfg = new ConfigImpl();
				
				if (cfg.getValue(sb, "DefaultMatrix") && sb.length() > 0) {
					applayMatrix(sb.toString());
				}else
					super.postAdapterInit();
			}
		}else
			super.postAdapterInit();
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View ret = super.getPriceView(node, convertView);
		
		TextView tv = (TextView) ret.findViewById(R.id.tvCostType);
		tv.setText(costType);
		
		CostStrategyEx cs = (CostStrategyEx)CostStrategy.defaultInstance;
		Price p = price.getData();
		tv = (TextView)ret.findViewById(R.id.tvDiscount);
		int discount =  cs.getDiscount(p, document);
		String text = "";
		if(discount != 0)
			text = Util.IntToScaleStr(discount, Consts.SUM_SCALE) + " %";
		tv.setText(text);

		return ret;
	}
	
	class FoldersAdapterEx extends FoldersAdapter {

		public FoldersAdapterEx(WarehouseManager warehouse) {
			super(warehouse);
		}

		@Override
		public String getWhereStr() {
			String where = super.getWhereStr();
			if(where.length() > 0)
				where += " and ";
			where += "(folderid = 0 or folderid in (select id from " + (new Folder()).getTableName() + " where hidden is null or hidden = 0))";
			return where;
		}
		
		@Override
		public synchronized void buldProcess(AsyncTask<?, ?, ?> task) {
			super.buldProcess(task);
			if (!task.isCancelled() && !solidPrice) {
				ArrayList<TreeNode> ch = root.getChilds();
				ArrayList<PriceInfo> val = fprice.get(0);
				if( val != null ) {
					boolean havePriceNodes = false;
					for(TreeNode tn : ch)
						if(tn instanceof PriceTreeNode) {
							havePriceNodes = true;
							break;
						}
					if(!havePriceNodes)
						for(PriceInfo pi : val)
							ch.add(createPriceTreeNode(priceTop, pi.rowid, pi.name, pi.id));
				}
			}
		}
	}
}
