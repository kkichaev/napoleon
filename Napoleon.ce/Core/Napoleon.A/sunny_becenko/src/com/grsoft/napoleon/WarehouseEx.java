package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class WarehouseEx extends WarehouseNew {
	
	private static final int DOCS_INTERVAL = 2; // интервал в месяцах для выборки проданного
	
	HashMap<String, Integer> lastOrderQty = new HashMap<String, Integer>(); 
	public Set<String> itemGR = new HashSet<String>(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if( document instanceof OrderImpl ) {
			remnantsDoc = new RemnantsImpl();
			long rc = RemnantsImpl.find(document.getId(), ((OrderImpl)document).getData().created);
			if( rc != ExtrasConst.INVALID_ID ){
				remnantsDoc.read(rc);
				remnantsDoc.close();
			}else {
				remnantsDoc.init(document);
			}
		}
	}
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		if( remnantsDoc != null && (type == COLUMN_QTY_WH || type == COLUMN_QTY_ORD || type ==  COLUMN_QTY_WH_ORD) ) {
			Itemsable id = (Itemsable) document;
			long value = 0;
			if( type == COLUMN_QTY_ORD || type ==  COLUMN_QTY_WH_ORD ) {
				value = id.getItemQty(price);
			}
			if( type == COLUMN_QTY_WH || (type ==  COLUMN_QTY_WH_ORD && value == 0) ) {
				value = getWhQty(id, price);
			}
			
			long rval = remnantsDoc.getItemQty(price);
			String text = "";
			if( rval != 0 ) {
				text += Util.IntToScaleStr(rval, Consts.QTY_SCALE) + "/";
			}
			text += Util.IntToScaleStr(value, Consts.QTY_SCALE);
			textView.setText(text);
		} else
			super.setTextColumnValue(textView, type, price);
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View v = super.getPriceView(node, convertView);
		if( v != null && remnantsDoc != null && document instanceof OrderImpl ) {
			View vq = v.findViewById(R.id.llQuant);
			vq.setTag(node.getRowid());
			vq.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Long itemRowid = (Long)v.getTag();
					if( itemRowid == null )
						return;
						
					remnantsDoc.editItem(itemRowid, WarehouseEx.this);
				}
			});

			Integer val = lastOrderQty.get(price.getData().id);
			if( val != null )
				((TextView)v.findViewById(R.id.tvLastQty)).setText(Util.IntToScaleStr(val, Consts.QTY_SCALE));
		}
		return v;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if( remnantsDoc != null && remnantsDoc.getData().items.size() == 0 )
			remnantsDoc.delete();
	}
	
	@Override
	public void setColor(TextView textView, Price price) {
		super.setColor(textView, price);
		
		if(itemGR.contains(price.id))
			textView.setTypeface(Typeface.DEFAULT_BOLD);
	};

	@Override
	protected void onStop() {
		super.onStop();
		if( remnantsDoc != null )
			remnantsDoc.close();
	}
	
	@Override
	protected int getItemLayoutId() { return R.layout.priceitemrow_ex; }
	
	@Override
	protected void loadLastBuyingItems(String orgId) {
		DocType dt = DocType.getCurDoc();
		if( dt == OrderDoc.instance() ) {
			HashSet<String> idPrice = new HashSet<String>();

			Date end = new Date();
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, -DOCS_INTERVAL);
			Date begin = c.getTime();
			DatePeriod dp = new DatePeriod(begin, end);
			dp.periodType = DatePeriod.CREATED;
			
			DocList dl = dt.docList(orgId, "created desc", dp);
			for (Document<?> d : dl) {
				if( d.getRowid() == document.getRowid() ) // exclude current
					continue;
				
				OrderImpl oi = (OrderImpl) d;
				for (OrderItem item : oi.getData().items) {
					if (idPrice.contains(item.id) == false) {
						idPrice.add(item.id);
						lastOrderQty.put(item.id, item.qty);
					}
				}
			}
			dl.close();

			lastBuyingItems.clear();
			lastBuyingItems.addAll(idPrice);
		} else
			super.loadLastBuyingItems(orgId);
	}
}
