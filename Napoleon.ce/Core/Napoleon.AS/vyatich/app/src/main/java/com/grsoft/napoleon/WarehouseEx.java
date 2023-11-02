package com.grsoft.napoleon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.grsoft.database.DbReader;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.ActionItem;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.StockOrg;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.StockOrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WarehouseEx extends Warehouse {

	Set<String> actions = new HashSet<>();
	PriceImpl priceCache = new PriceImpl();

	@Override protected int getLayoutId() {
		return R.layout.warehouseex;
	}
	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		findViewById(R.id.btnScan).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				IntentIntegrator ii = new IntentIntegrator(WarehouseEx.this);
				ii.initiateScan();
			}
		});
	}

	@Override
	protected void setName(View view, Price p, int linesCount, PriceTreeNode node) {
		super.setName(view, p, linesCount, node);
		ImageView iv = (ImageView)view.findViewById(R.id.iAction);
		if( iv != null ) {
			iv.setImageResource( actions.contains(p.id) ? R.drawable.action : R.drawable.empty );
		}
	}

	@Override
	protected void readDocument() {
		super.readDocument();
		StockOrgImpl soi = new StockOrgImpl();
		StockOrg so = soi.getData();
		so.id = document.getId();
		soi.read();
		soi.close();

		actions.clear();
		for(ActionItem ai : so.items)
			actions.add(ai.id);
	}

	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		if( type == COLUMN_COST ){
			@SuppressWarnings("unchecked")
			long value =  CostStrategy.getInstance(
					(Class<? extends Document<?>>) document.getClass()).getItemCost(price, (Document<?>) document);
			
			value = (value * price.qtyInPack / Consts.QTY_SCALE);
			textView.setText(Util.IntToScaleStr(value, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			return;
		}
		super.setTextColumnValue(textView, type, price);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null) {
			String bc = scanResult.getContents();

			Class<? extends DataObject> type  = PriceEx.class;

			List<Long> ids = DbReader.readIds(DataObjectInfo.getInstance().getTableName(type), "barcode LIKE '%" + bc + "%'", null);

			if( ids.size() > 0 ) {
				((Itemsable)document).editItem(ids.get(0), this);
			}
		}

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
