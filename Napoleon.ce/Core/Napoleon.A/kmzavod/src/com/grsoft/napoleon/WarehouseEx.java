package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DeliverItemEx;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.Consts;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.TreeNodeCmp;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseManager;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter.resetCache();
		
		if( document instanceof ReturnImplEx)
			return new ReturnAdapter(this, (ReturnEx) document.getData());
		
		return super.createListAdapter();
	}
	
	class ReturnAdapter extends FoldersAdapter {

		HashSet<String> ids = new HashSet<String>();
		
		public ReturnAdapter(WarehouseManager warehouse, ReturnEx doc) {
			super(warehouse);
			
			Calendar c = Calendar.getInstance();
			c.setTime(Util.getDate());
			c.add(Calendar.DATE, -AssortmentMatrixAdapterEx.PERIOD);
			
			String where = String.format("dogovor ='%s'", doc.dogovor);
			
			DocList dl = DeliveryDoc.instance().docList(doc.id, "", where);
			for(Document<?> d : dl) {
				for(DeliveryItem di : ((DeliveryImpl)d).getData().items){
					DeliverItemEx de = (DeliverItemEx)di;
					
					//Log.d("BEST_BFR", String.format("%s  : %s", de.bestBefore.toString(), doc.date.toString()));
					
					Date dt =  Util.resetTime(de.bestBefore);
					if(dt.compareTo(doc.date) >=0 )
						ids.add(di.id);
				}
			}
			dl.close();
		}
		
		@Override public boolean inset(long rowid, String id) { return ids.contains(id); }
	}
	
	@Override
	protected void updateChildPriceView(View view, Price p) {
		if (DocType.getCurDoc() == RemnantsDoc.instance()) {
			TextView tv = (TextView) view.findViewById(R.id.tvClmn1);
			String text = "";
			Itemsable i = (Itemsable)document;
			
			if (i.findItem(p.id) != null) {
				int qty = i.getItemQty(p);
				
				text = Util.IntToScaleStr(qty, Consts.QTY_SCALE, Util.DEC_DELIM, true);
			}
			
			tv.setText(text);
			
			view.findViewById(R.id.tvClmn2).setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		if (DocType.getCurDoc() == OrderDoc.instance() || DocType.getCurDoc() == RemnantsDoc.instance())
			return new AssortmentMatrixAdapterEx(this, document.getId());
		else
			return super.createAssortementMatrixAdapter();
	}
	
	@Override
	protected void postAdapterInit() {
		if (!editMode && 
				(DocType.getCurDoc() == OrderDoc.instance() || DocType.getCurDoc() == RemnantsDoc.instance()))
			applayMatrix(AssortmentMatrixAdapter.TITLE);
		else
			super.postAdapterInit();
	}
	
	@Override
	public void sortingPriceList(ArrayList<TreeNode> price) {
		DocType dt = DocType.getCurDoc();
		
		if ((dt == OrderDoc.instance() || dt == RemnantsDoc.instance()) 
				&& adapter.isExpanded() && adapter instanceof AssortmentMatrixAdapterEx) 
			sortPriceWithProperty(price);
		else
			super.sortingPriceList(price);
	}
	
	PriceImpl pi = new PriceImpl();
	HashMap<Long, Integer> priceOrders = new HashMap<Long, Integer>();
	
	int getPriceOrder(long rid) {
		Integer r = priceOrders.get(rid);
		if( r == null ) {
			pi.read(rid);
			r = ((PriceEx)pi.getData()).type;
			priceOrders.put(rid, r);
		}
		
		return r;
	}

	private void sortPriceWithProperty(ArrayList<TreeNode> price) {
		Collections.sort(price, new TreeNodeCmp() {

			@Override
			public int compare(TreeNode lhs, TreeNode rhs) {
				if (!(lhs instanceof PriceTreeNode) || !(rhs instanceof PriceTreeNode))
					return super.compare(lhs, rhs);
				
				int l = getPriceOrder(lhs.getRowid());
				int r = getPriceOrder(rhs.getRowid());
				
				if(l == r)
					return super.compare(lhs, rhs);
				
				return l > r ? 1 : -1;
			}
		});
	}
}
