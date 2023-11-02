package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.WSOrder;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.WSAddOrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	public static final String EDIT_MODE = "EDIT_MODE";
	boolean editMode = false;
	
	CostList values = new CostList();
	boolean started = true;
	int startQty = 0;
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		editMode = getIntent().getBooleanExtra(EDIT_MODE, DocType.getCurDoc() != WSAddOrderDoc.instance());
		
		if(document instanceof OrderImplBase)
			((OrderImplBase)document).setUpdateQtyHandler(this);
		
		((Spinner)findViewById(R.id.spPrices)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				CostData cd = values.get(arg2);
				changeCost(cd);
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		startQty = qtyItems;
		
		if (!editMode) { 
			edCount.setText(Util.IntToScaleStr(startQty, Consts.QTY_SCALE));
			edCount.selectAll();
		}
	}
	
	@Override
	protected boolean updateQty(boolean inPack, int qty) {
		if (!editMode && getDocItem(price.getData()) != null)
			qty = qty + startQty;
		
		return super.updateQty(inPack, qty);
	}
	
	void changeCost(CostData cd) {
		Price p = price.getData();
		int cost = p.cost.size() > cd.index ? p.cost.get(cd.index).cost : 0;
		onChangeCost(cost);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		if(document != null) {
			OrgImpl orgi = new OrgImpl();
			OrgEx org = (OrgEx) orgi.getData();
			org.id = document.getId();
			orgi.read();
			orgi.close();
			
			values.loadCost(org);		

			Spinner spCost = (Spinner)findViewById(R.id.spPrices);
			ArrayAdapter<CostData> aa = new ArrayAdapter<CostData>(this, R.layout.simple_spinner_layout, values);
			aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
			spCost.setAdapter(aa);
			
			int selected = document.getSumType(); 
			@SuppressWarnings("rawtypes")
			OrderItem oi = (OrderItem) ((OrderImplBase)document).findItem(price.getData().id);
			if(oi != null) {
				if(oi instanceof OrderItemEx) {
					OrderItemEx oie = (OrderItemEx)oi;
					selected = oie.costIndex;
				} else if(oi instanceof SalesItemEx) {
					SalesItemEx sie = (SalesItemEx)oi;
					selected = sie.costIndex;
				}
			}
			
			int index = 0;
			for(CostData cd : values) {
				if( cd.index == selected ) {
					spCost.setSelection(index, true);
					changeCost(cd);
					break;
				}
				index++;
			}
		}
		started = false;
	}

	@Override
	public void itemUpdated(OrderItem oi, Order order, boolean isNewItem) {
		Spinner spCost = (Spinner)findViewById(R.id.spPrices);		
		CostData cd = (CostData)spCost.getSelectedItem();
		if(cd != null) {
			if(oi instanceof OrderItemEx) {
				OrderItemEx oie = (OrderItemEx)oi;
				oie.costIndex = cd.index;
				oie.costCode = cd.id;
			} else if(oi instanceof SalesItemEx) {
				SalesItemEx sie = (SalesItemEx)oi;
				sie.costIndex = cd.index;
				sie.costCode = cd.id;
			}
		}
	}

	public static void openEditMode(Context context, long itemRowid, DbObject<?> doc) {
		Intent i = new Intent(context, activity);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, itemRowid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(EDIT_MODE, true);
		
		context.startActivity(i);
	}

}
