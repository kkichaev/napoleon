package com.grsoft.napoleon;

import java.util.HashSet;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DisabledItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.util.BarcodeFilter;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.InputNumber;
import com.grsoft.util.PriceTextFilter;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {	
	
	static int whIndex = 0;
	
	HashSet<String> disabledItems = new HashSet<String>();
	HashSet<String> disabledFolders = new HashSet<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		findViewById(R.id.btnAutoOrder).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { 
				InputNumberDlg.open(WarehouseEx.this, new InputNumber() {
					@Override public int getValue() { return 0; }
					@Override public boolean replaceCommaToPlus() { return true; }
					@Override public void applayInput(int value, Object... params) { setQty(value); }
				}, Consts.QTY_SCALE, true, getString(R.string.value), false);
			}
		});
		
		OrgImpl org = new OrgImpl();
		OrgEx oe = (OrgEx)org.getData();
		oe.id = document.getId();
		org.read();
		org.close();
		
		for(DisabledItem di : oe.disabled) {
			if( di.isFolder == 1)
				disabledFolders.add(di.id);
			else
				disabledItems.add(di.id);
		}
	}
	
	protected void updateRemnants(PriceTreeNode node) {
		if( remnantsDoc == null ) {
			remnantsDoc = (RemnantsImpl) RemnantsDoc.instance().create();
			remnantsDoc.init(document);
		}
		
		remnantsDoc.editItem(node.getRowid(), this);
	}

	@SuppressWarnings("unchecked")
	protected void setQty(int value) {
		if( !adapter.isTop() ) {
			Itemsable id = (Itemsable)document;

			CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();
			
			FolderTreeNode top = adapter.getFolderTop();
			for(TreeNode n : top.getChilds()) {
				if( n instanceof PriceTreeNode &&  pi.read(n.getRowid()) ) {
					int qty = id.getItemQty(p);
					if( qty == 0 ) {
						int rest = (remnantsDoc == null) ? 0 : remnantsDoc.getItemQty(p);
						if( rest < value )
							id.updateQty(pi, value - rest, cs.getItemCost(p, document), false);
					}
				}
			}
			
			pi.close();
			adapter.notifyDataSetChanged();
		}
	}

	@Override protected int getItemLayoutId() { 
		return R.layout.priceitemrowex; 
//		return linesController.isMinLines() ? R.layout.priceitemrowex : R.layout.priceitemrowex2; 
	}	
	@Override protected int getLayoutId() { return R.layout.warehouseex; }
	
	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		int id = getFolderLayoutId();
		View result;
		if( convertView != null && convertView.getTag(id) != null)
			result = convertView;
		else {
			result = View.inflate(this, id, null);
			result.setTag(id, true);
		}

		TextView tvOrgName = (TextView)result.findViewById(R.id.tvItemSelectRowName);
		tvOrgName.setText(node.name);
		tvOrgName.setTag(node);
		
		return result;
	}
	
	@Override
	public void editItem(long rowid) {
		price.read(rowid);
		PriceEx p = (PriceEx) price.getData();
		if(!disabledFolders.contains(p.fid) && !disabledItems.contains(p.id)) 
			super.editItem(rowid);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		price.read(node.getRowid());
		PriceEx p = (PriceEx) price.getData();
		
		View view;
		int id = getItemLayoutId();
		if( convertView != null && convertView.getTag(id) != null)
			view = convertView;
		else {
			view = View.inflate(this, id, null);
			view.setTag(id, true);
		}

		// set name
		TextView tvPriceItemName = (TextView) view.findViewById(R.id.tvPriceItemName);		
		setColor(tvPriceItemName, p);
		tvPriceItemName.setText(getItemName(p));		
		tvPriceItemName.setTag(node);
		
		int bkcolor = Color.TRANSPARENT;
		if(disabledFolders.contains(p.fid) || disabledItems.contains(p.id))
			bkcolor = Color.LTGRAY;
		tvPriceItemName.setBackgroundColor(bkcolor);

		CfgNpl config = (CfgNpl) ConfigManager.getConfig();
		int val = config.priceClmn2Type;

		TextView tv;
		tv = (TextView) view.findViewById(R.id.tvClmn1);
		if( (val & 1) != 0 ) {
			tv.setVisibility(View.VISIBLE);
			tv.setText(p.nesting);
			tv.setBackgroundColor(bkcolor);
		} else {
			tv.setVisibility(View.GONE);
		}
		
		tv = (TextView) view.findViewById(R.id.tvClmn2);
		if( (val & 2) != 0 ) {
			tv.setVisibility(View.VISIBLE);
			int qty = getWhQty((Itemsable) document, p);
			tv.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
			tv.setBackgroundColor(bkcolor);
		} else {
			tv.setVisibility(View.GONE);
		}
		
		tv = (TextView) view.findViewById(R.id.tvClmn5);
		if( (val & 4) != 0 && OrderImpl.class.isAssignableFrom(document.getClass()) && document.getRowid() != ExtrasConst.INVALID_ID) {
			tv.setTag(node);
			tv.setVisibility(View.VISIBLE);
			tv.setBackgroundColor(bkcolor);
			
			if( remnantsDoc != null) {
				remnantsDoc.getItemQty(p);
				int qty = remnantsDoc.getItemQty(p);
				tv.setText(qty == 0 ? "" : Util.IntToScaleStr(qty, Consts.QTY_SCALE));
			}
			
			tv.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { updateRemnants((PriceTreeNode)v.getTag()); }
			});
		} else
			tv.setVisibility(View.GONE);
		

		val = config.priceClmn3Type;
		tv = (TextView) view.findViewById(R.id.tvClmn3);
		if( (val & 1) != 0 ) {
			tv.setBackgroundColor(bkcolor);
			tv.setVisibility(View.VISIBLE);
			int cost = CostStrategy.getInstance( (Class<? extends Document<?>>) document.getClass())
				.getItemCost(p, (Document<?>) document); 
			tv.setText(Util.IntToScaleStr(cost, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " ð.");
		} else {
			tv.setVisibility(View.GONE);
		}
		
		tv = (TextView) view.findViewById(R.id.tvClmn4);
		if( (val & 2) != 0 && document.getRowid() != ExtrasConst.INVALID_ID ) {
			tv.setBackgroundColor(bkcolor);
			tv.setVisibility(View.VISIBLE);
			int qty = ((Itemsable)document).getItemQty(p);
			tv.setText(qty == 0 ? "" : Util.IntToScaleStr(qty, Consts.QTY_SCALE));
		} else {
			tv.setVisibility(View.GONE);
		}
		
		return view;
	}
	
	@Override
	public void applySearchFilter(String value) {
		if (value.trim().length() > 0 ) {
			boolean srchById = false;
			Spinner sp = (Spinner) findViewById(R.id.spFind);
			if( sp != null )
				srchById = (sp.getSelectedItemPosition() == 1);
			
			PriceTextFilter filter = (srchById) ? new BarcodeFilter() : new PriceTextFilter();			
			adapter.putFilter(filter);
			
			filter.build(adapter, value);
			adapter.buildSet(true);
		} else 
			((FilterAdapter)adapter).resetFilter();
	}

	@Override
	protected Filter createZeroPositionFilter() {
		if( document instanceof OrderImplEx ) {
			if( whIndex != ((OrderEx)document.getData()).whIndex ) {
				whIndex = ((OrderEx)document.getData()).whIndex;
				FoldersAdapter.resetCache();
			}
		} else if( whIndex != 0 ) {
			whIndex = 0;
			FoldersAdapter.resetCache();			
		}
		return new ZeroFilter();
	}

	class ZeroFilter extends ZeroPositionFilter {
		
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			if( !(document instanceof Itemsable) )
				return super.inset(priceRowID, id);
			
			boolean result = false; 			
			if(price.read(priceRowID))
				result = (((Itemsable)document).getItemValue(price.getData()) > 0);			
			return result;
		}
	}
}
