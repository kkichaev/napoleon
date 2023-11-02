package com.grsoft.napoleon;

import java.util.Date;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ReturnPriceCount extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	
	protected static final int DIALOG_MFR_DATE = 0;
	protected static final int DIALOG_END_DATE = 1;
	Date mfrDate, endDate;
	
	public static void open(Context context, long priceRoid, ReturnImplEx doc) {
		Intent i = new Intent(context, ReturnPriceCount.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);		
	}
	
	@Override protected int getContentViewId() { return R.layout.returncount; }
	
	@Override protected boolean isComplexSalesHistory() { return false; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((ReturnImplEx)document).setUpdateQtyHandler(this);
	
		findViewById(R.id.tvMfrDate).setOnClickListener(new View.OnClickListener() {			
			@Override 
			public void onClick(View arg0) { 
				Intent i = new Intent(ReturnPriceCount.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, (mfrDate == null) ? new Date() :  mfrDate.getTime());
				startActivityForResult(i, DIALOG_MFR_DATE);
			}
		});

		findViewById(R.id.tvEndDate).setOnClickListener(new View.OnClickListener() {			
			@Override 
			public void onClick(View arg0) { 
				Intent i = new Intent(ReturnPriceCount.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, (endDate == null) ? new Date() :  endDate.getTime());
				startActivityForResult(i, DIALOG_END_DATE);
			}
		});
	}
	
	private void refreshDate() {
		TextView tv = (TextView)findViewById(R.id.tvMfrDate);
		String text = mfrDate == null ? "¬ведите дату" : Util.simpleDateFormat.format(mfrDate); 
		tv.setText(Html.fromHtml("<u>" + text + "</u>"));
		
		tv = (TextView)findViewById(R.id.tvEndDate);
		text = endDate == null ? "¬ведите дату" : Util.simpleDateFormat.format(endDate); 
		tv.setText(Html.fromHtml("<u>" + text + "</u>"));		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data == null || resultCode != RESULT_OK)
			return;
		Date curDate = new Date();
		long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
		if( requestCode == DIALOG_MFR_DATE ) 
			mfrDate = new Date(ct);
		else if( requestCode == DIALOG_END_DATE)
			endDate = new Date(ct);
		refreshDate();
	}
	
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		ReturnItemEx re = (ReturnItemEx) ((ReturnImplEx) document).findItem(price.getData().id);
		
		EditText ed = (EditText)findViewById(R.id.edRemark);
		ed.setText(re == null ? "" : re.remark);
		
		if( re == null ) {
			mfrDate = null;
			endDate = null;
		} else {
			mfrDate = re.mfrDate;
			endDate = re.endDate;
		}
		
		refreshDate();
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		ReturnItemEx rie = (ReturnItemEx)item;
		
		rie.mfrDate = mfrDate == null ? new Date(70, 1, 1) : mfrDate;
		rie.endDate = endDate == null ? new Date(70, 1, 1) : endDate;
		
		rie.remark = ((EditText)findViewById(R.id.edRemark)).getText().toString();
	}
}
