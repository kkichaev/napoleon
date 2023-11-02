package com.grsoft.napoleon;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ActionPriceImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	ActionPriceImpl apimpl = new ActionPriceImpl();
	TextView tvAQty;
	TextView tvACost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tvAQty = (TextView) findViewById(R.id.tvAQty);
		tvACost = (TextView) findViewById(R.id.tvACost);
		
		apimpl.getData().orgid = document.getId();
		apimpl.getData().priceid = price.getData().id;
		
		apimpl.read();
		apimpl.close();
		
		tvAQty.setText(Util.IntToScaleStr(apimpl.getData().qty, Consts.QTY_SCALE));
		tvACost.setText(Util.IntToScaleStr(apimpl.getData().cost, Consts.SUM_SCALE));
		
		if (apimpl.getRowid() == ExtrasConst.INVALID_ROWID) {
			findViewById(R.id.trACost).setVisibility(View.GONE);
			findViewById(R.id.trAQty).setVisibility(View.GONE);
		}
	}
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	protected boolean updateQty(boolean inPack, int qty) {
		if (DocType.getCurDoc() == OrderDoc.instance())
			return !((OrderImplEx)document).updateQty(price, apimpl, qty, getInputCost(price.getData()), inPack);
		else
			return !((Itemsable)document).updateQty(price, 
				qty, getInputCost(price.getData()), inPack);
	}
	
	protected DataObject getDocItem(Price p) {
		DataObject ret =  super.getDocItem(p);
		
		if (ret != null && DocType.getCurDoc() == OrderDoc.instance()) {
			OrderItemEx item = new OrderItemEx();
			item.id = p.id;
			item.qty = 0;
			
			for(OrderItem i : ((OrderImplEx)document).getData().items)
				item.qty += i.qty;
			
			return item;
		}
		
		return ret;
			
	}
}
