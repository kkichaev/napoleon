package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.MatrixAdapter;
import com.grsoft.util.TreeNodeCmp;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {

	static final String MATRIX_KEY = "Matrix"; 
	
	String matrix = "";
	long selectedRowid = -1;
	PriceTreeNode selectedItem = null;
	
	static public void open(Context context, String matrix) { 
		Intent i = new Intent(context, WarehouseEx.class);
		i.putExtra(MATRIX_KEY, matrix);
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		matrix = b.getString(MATRIX_KEY);
		if( matrix == null)
			matrix = "";
		
		super.onCreate(savedInstanceState);
		
		lvItemSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Object o = adapter.getItem(arg2);
				if( o instanceof PriceTreeNode )
					selectedItem = (PriceTreeNode)o;
				else
					selectedItem = null;
				
				adapter.onClick(arg2);
			}
		});

		findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { finish(); }
		});
		
		
		findViewById(R.id.btnPls1).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { changeDoc(1); }
		});
		findViewById(R.id.btnPls5).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { changeDoc(5); }
		});
		findViewById(R.id.btnPls10).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { changeDoc(10); }
		});
		findViewById(R.id.btnPls20).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { changeDoc(20); }
		});
		findViewById(R.id.btnPls50).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { changeDoc(50); }
		});
		findViewById(R.id.btnMns1).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { changeDoc(-1); }
		});
		findViewById(R.id.btnMns5).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { changeDoc(-5); }
		});
		findViewById(R.id.btnMns10).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { changeDoc(-10); }
		});
		findViewById(R.id.btnMns20).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { changeDoc(-20); }
		});
		findViewById(R.id.btnMns50).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { changeDoc(-50); }
		});
	}
	
	@Override protected void updateTotalSum() { }
	
	void changeDoc(int amount) {
		if( selectedItem == null )
			return;
		
		amount *= Consts.QTY_SCALE;
		
		OrderImpl order = (OrderImpl)document;
		if( document.getRowid() == ExtrasConst.INVALID_ROWID) {
			long rid = getEditableDoc();
			if( rid != ExtrasConst.INVALID_ROWID) {
				document.read(rid, false);
				docRowId = rid;
			} else {
				CfgNplEx cfg = (CfgNplEx)ConfigManager.getConfig();
				OrderEx o = (OrderEx) document.getData();
				o.matrix = matrix;
				order.initSilent(cfg.orgId, new GpsCoord(0, 0));
			}
		}
		PriceImpl pi = new PriceImpl();
		pi.read(selectedItem.getRowid());
		OrderItem item = (OrderItem)order.findItem(pi.getData().id);
		if( item != null )
			amount += item.qty;
		
		if(amount < 0)
			amount = 0;
		
		order.updateQty(pi, amount, 0, false);
		notifyDataSetChanged();
	}
	
	@Override protected int getOptionsMenuId() { return R.menu.wh_menu_ex; }
	@Override protected int getLayoutId() { return R.layout.warehouseex; }
	@Override public boolean isPriceExpand() { return true; }
	@Override protected void initZeroFilter() { }
	@Override protected BaseAdapter createListAdapter() { return new Adapter(this, matrix); }
	
	@Override
	public void editItem(long rowid) {
		selectedRowid = rowid;
		notifyDataSetChanged();
	}
	
	@Override
	protected int getItemLayoutId() {
		return R.layout.priceitemrowex;
	}
	
	@Override
	public void onBackPressed() {
		if( ((Order)document.getData()).items.size() == 0 )
			document.delete();
		
		finish();
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View v = super.getPriceView(node, convertView);
		v.findViewById(R.id.llQuant).setVisibility(View.GONE);
		TextView qty = (TextView)v.findViewById(R.id.tvQty);
		
		OrderItem item = (OrderItem)((OrderImpl)document).findItem(node.getId());
		if( item != null )
			qty.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE));
		else 
			qty.setText("");
		
		return v;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(MATRIX_KEY, matrix);
	}
	
	long getEditableDoc() {
		long rid = ExtrasConst.INVALID_ROWID;
		
//		String filter = "params = 0 and created > " + Long.toString(Util.getDate().getTime()) + " and matrix='" + matrix + "'";
		String filter = "params = 0 and matrix='" + matrix + "'";
		List<Long> ret = DbReader.readIds(DataObjectInfo.getInstance().getTableName(Order.class), filter, "created desc");
		if( ret.size() > 0 )
			rid = ret.get(0);

		return rid;
	}
	
	@Override
	protected void createDocument() {
		document = OrderDoc.instance().create();
		
		docRowId = getEditableDoc();
//		String filter = "params = 0 and created > " + Long.toString(Util.getDate().getTime()) + " and matrix='" + matrix + "'";
//		List<Long> ret = DbReader.readIds(DataObjectInfo.getInstance().getTableName(Order.class), filter, "created desc");
//		if( ret.size() > 0 )
//			docRowId = ret.get(0);
	}
	
	class Cmp extends TreeNodeCmp {
		HashMap<String, Integer> weights;
		
		public Cmp(HashMap<String, Integer> weights) { this.weights = weights; }
		
		@Override
		public int compare(TreeNode lhs, TreeNode rhs) {
			if( lhs instanceof PriceTreeNode && rhs instanceof PriceTreeNode ) {
				Integer lw = weights.get(((PriceTreeNode)lhs).getId());
				Integer rw = weights.get(((PriceTreeNode)rhs).getId());
				
				if( lw != null || rw != null && lw != rw)
					return lw - rw;
			}
			
			return super.compare(lhs, rhs);
		}
	}
	
	@Override
	public void sortingPriceList(ArrayList<TreeNode> price) {
		HashMap<String, Integer> weights = new HashMap<String, Integer>();
		
		Date end = Util.getDateTime();
		Calendar c = Calendar.getInstance();
		c.setTime(end);
		c.add(Calendar.MONTH, -1);

		DatePeriod dp = new DatePeriod(c.getTime(), end);
		
		DocList dl = OrderDoc.instance().docList("", "", dp);
		for(Document<?> doc : dl) {
			OrderImpl ord = (OrderImpl)doc;
			if(ord.isExported() == false)
				continue;
			for(OrderItem oi : ord.getData().items) {
				Integer count = weights.get(oi.id);
				if( count == null )
					count = 1;
				else
					count ++;
				weights.put(oi.id, count);
			}
		}
		dl.close();
		
		Collections.sort(price, new Cmp(weights));
	}
	
	
	class Adapter extends MatrixAdapter {

		public Adapter(WarehouseNew warehouse, String matrix) {
			super(warehouse, matrix);
		}

		@Override
		protected void postUpdateView(View view, TreeNode node) {
			if( selectedItem == node ) {
				view.setBackgroundResource(R.drawable.sel_matrix);
			}
		}
	}
}
