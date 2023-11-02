package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

public class ReturnPriceCount extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	protected static final int DIALOG_BBFR_DATE = 0x10;

	private static final String NEW_ITEM = "NewItemTag";
	private static final String ITEM_INDEX = "ItemIndexTag";
	Spinner spQual;
	
	Date bbfrDate = null;
	int itemIndex = -1;
	
	public static void open(Context context, long priceRoid, ReturnImpl doc, boolean newItem) {
		Intent i = new Intent(context, ReturnPriceCount.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(NEW_ITEM, newItem);

		context.startActivity(i);		
	}
	
	public static void open(Context context, OrderItem item, ReturnImplEx doc) {
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		p.id = item.id;
		pi.read();
		pi.close();
		
		Intent i = new Intent(context, ReturnPriceCount.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, pi.getRowid());
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ITEM_INDEX, doc.getData().items.indexOf(item));

		context.startActivity(i);		
	}
	
	@Override
	protected DataObject getDocItem(Price p) {
		return itemIndex >= 0 ? ((ReturnImplEx)document).getData().items.get(itemIndex) : super.getDocItem(p);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		itemIndex = getIntent().getExtras().getInt(ITEM_INDEX, -1);
		
		super.onCreate(savedInstanceState);

		spQual = (Spinner) findViewById(R.id.spQual);
		findViewById(R.id.tvBestBefore).setOnClickListener(new View.OnClickListener() {			
			@Override 
			public void onClick(View arg0) { 
				Intent i = new Intent(ReturnPriceCount.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, (bbfrDate == null) ? new Date() :  bbfrDate.getTime());
				startActivityForResult(i, DIALOG_BBFR_DATE);
			}
		});
		
		
		ReturnImplEx ri = (ReturnImplEx)document;
		ri.setUpdateQtyHandler(this);
		
		boolean putNew = getIntent().getExtras().getBoolean(NEW_ITEM, false);
		if(putNew) {
			ri.setNewItem();
		}
		
		ReturnItemEx item = (ReturnItemEx) getDocItem(price.getData());
		String qual = item != null ? item.qual : "";
		
		ConfigImpl config = new ConfigImpl();
		DialogHelper.loadSpinnerFromConfig(config, "ПризнакКачества", new ArrayList<CharSequence>(), 
				spQual, qual);
	}
	
	private void refreshDate() {
		TextView tv = (TextView)findViewById(R.id.tvBestBefore);
		String text = bbfrDate == null ? "Введите дату" : Util.simpleDateFormat.format(bbfrDate); 
		tv.setText(Html.fromHtml("<u>" + text + "</u>"));
		
		if(itemIndex >= 0) {
			ReturnItemEx ri = (ReturnItemEx) ((ReturnImplEx)document).getData().items.get(itemIndex);
			if( priceVal != ri.cost ) {
				priceVal = ri.cost;
				updateCost();
			}
		}
	}
	
	@Override
	protected boolean updateQty(boolean inPack, int qty) {
		if(itemIndex >= 0) {
			ReturnItemEx re = (ReturnItemEx) ((ReturnImplEx)document).getData().items.get(itemIndex);
			re.qty = qty;
			re.cost = (int)getInputCost(price.getData());
			re.bestBefore = bbfrDate;
			re.qual = spQual.getSelectedItem() == null ? "" : spQual.getSelectedItem().toString();
			document.write();
			return false;
		}
		return super.updateQty(inPack, qty);
	}

	@Override
	protected void refreshData() {
		super.refreshData();
		
		ReturnItemEx ri = (ReturnItemEx) ((ReturnImpl)document).findItem(price.getData().id);
		if(ri != null ) {
			bbfrDate = ri.bestBefore;
		} else
			bbfrDate = null;
		
		refreshDate();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DIALOG_BBFR_DATE ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			bbfrDate = new Date(ct);
			refreshDate();
		}
	}
	
	@Override protected boolean getStartInPack() { return false; }
	@Override protected boolean isComplexSalesHistory() { return false; }
	@Override protected int getContentViewId() { return R.layout.returncount; }
	@Override protected boolean canChangeCost() { return true; }

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		((ReturnItemEx)item).bestBefore = bbfrDate;
		
		String q = spQual.getSelectedItem() != null ? spQual.getSelectedItem().toString() : ""; 
		((ReturnItemEx)item).qual = q;
	}

}
