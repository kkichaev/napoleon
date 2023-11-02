package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.ViewDebug.IntToString;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DiscountItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DiscountImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount implements UpdateQtyHandler {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		keypadHelper.setTargetID(R.id.edCount);
	}
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		if( document instanceof OrderImplBase<?> ) {
			TextView tvPriceNotDiscounted = (TextView) findViewById(R.id.tvPriceNotDiscounted);
			DataObject oe = (DataObject) document.getData();
			OrderImplBase<?> ord = (OrderImplBase<?>)document;
			DataObject oie = ord.findItem(price.getData().id);
			onChangeCost(getDiscountedPrice(getInitialDiscount(oe, oie)));
			
			Spinner sp = (Spinner)findViewById(R.id.spDiscount);
			
			tvPriceNotDiscounted.setText(Util.IntToScaleStr(CostStrategy.defaultInstance.getItemCost(price.getData(), document), Consts.SUM_SCALE));
			DocHelper.refreshDiscounts(sp, 
					DocHelper.getFieldVal(oe, "iddog").toString(), oie == null ? 
							DocHelper.getFieldVal(oe, "discid").toString(): 
								DocHelper.getFieldVal(oie, "discid").toString() );
			sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					DiscountItem di = (DiscountItem) arg0.getAdapter().getItem(arg2);
					int newCost = getDiscountedPrice(di.val);
					onChangeCost(newCost);
				}

				@Override public void onNothingSelected(AdapterView<?> arg0) {}
			});
			ord.setUpdateQtyHandler(this);
		}
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		Spinner sp = (Spinner)findViewById(R.id.spDiscount);
		DiscountItem di = (DiscountItem) sp.getSelectedItem();
		DocHelper.setFieldVal(item, "discid", di.id);
		DocHelper.setFieldVal(item, "discount",  di.val);
	}
	
	@Override
	protected void updateCost() {
		super.updateCost();
		
		
	}
	
	private int getDiscountedPrice (int val){
		return getItemCost(price.getData(), document, val);
	}
	
	private int getItemCost(Price p, Document<?> doc, int discount) {
		int cost=CostStrategy.defaultInstance.getItemCost(p, doc);
		if( discount != 0 )
			cost -= (int) (((long) cost * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
		return cost;
	}
	
	private int getInitialDiscount(DataObject oe, DataObject oie){
		
		String selected=oie == null ? 
				DocHelper.getFieldVal(oe, "discid").toString(): 
					DocHelper.getFieldVal(oie, "discid").toString();
				
		String iddog=DocHelper.getFieldVal(oe, "iddog").toString();
		
		HashMap<String, DiscountItem> discMap = DiscountImpl.loadFromDogovor(iddog);
		DiscountItem di = new DiscountItem();
		di.id = "";
		di.name = getResources().getString(R.string.no_discount);
		di.val = 0;
		discMap.put("", di);
		
		int sel = -1;
		int value = -1;
		ArrayList<DiscountItem> items = new ArrayList<DiscountItem>();
		for(DiscountItem i : discMap.values()) {
			if( selected != null && i.id.equals(selected) && i.id.length() == selected.length() )
				//sel = items.size();
				value=i.val;
			items.add(i);
		}
		return value;
		//TODO: из DocHelper.refreshDiscounts выше по примеру - забрать из самого док’елпера нужный эл-т по селектед
	}
}
