package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.OffTakeHistory;
import com.grsoft.napoleon.documents.OffTakeHistoryEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	
	ReturnImpl retDoc = null;
	
	boolean loaded = false;

	int coef = 0;
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	@Override protected boolean isComplexSalesHistory() { return true; }	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		loaded = true;
		
		super.onCreate(savedInstanceState);
		
		PriceEx pe = (PriceEx)price.getData();
		TextView tv;
		tv = (TextView)findViewById(R.id.tvAvgWeight);
		tv.setText(Util.IntToScaleStr(pe.avgWeight, Consts.WEIGHT_SCALE, Util.DEC_DELIM, false));

		tv = (TextView)findViewById(R.id.tvUnit);
		tv.setText(pe.unitName);
		
		OrderItem docItem = null;
		int restQty = 0;
		if( document != null && document instanceof OrderImpl ) {
			docItem = (OrderItem) ((OrderImpl)document).findItem(pe.id);
			retDoc = ReturnImplEx.getAssociated((OrderImplBase<? extends Order>) document, false);
		}
		
		if( retDoc != null ) {
			OrderItem item = (OrderItem) retDoc.findItem(pe.id);
			if( item != null ) restQty = item.qty;
		}
		
		final EditText ed = (EditText)findViewById(R.id.edReturn);
		ed.setText(Util.IntToScaleStr(restQty, Consts.QTY_SCALE));
		ed.addTextChangedListener(getRestUpdateHandler());
		ed.setInputType(InputType.TYPE_NULL);
		ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {			
			@Override public void onFocusChange(View v, boolean hasFocus) {
				if( hasFocus ) {
					keypadHelper.setTargetID(R.id.edReturn);
					ed.selectAll();
				}
			}
		});	

		if( lastItem != null && docItem == null ) {
			edCount.setText(Util.IntToScaleStr(lastItem.qty, Consts.QTY_SCALE));
			edCount.selectAll();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loaded = false;
	}

//	@Override
//	protected void onPostResume() {
//		super.onPostResume();
////
////		Display display = getWindowManager().getDefaultDisplay(); 
////		if( display.getHeight() > 600 ) {
////			View scrl = findViewById(R.id.svScroll);
////			scrl.setMinimumHeight(400);
////		}
//	}
		
	@SuppressWarnings("unchecked")
	@Override protected boolean updateOrder() {
		EditText ed = (EditText)findViewById(R.id.edReturn);
		int retQty = Util.StrToScale(ed.getText().toString(), Consts.QTY_SCALE);
		if( retQty != 0 || retDoc != null) {
			if( retDoc == null ) {
				retDoc = new ReturnImplEx();
				((ReturnImplEx)retDoc).init((OrderImplBase<? extends Order>) document);
			}
			retDoc.updateQty(price, retQty, price.getData().cost.get(0).cost, false);			
			if( retDoc.getData().items.size() == 0 && retDoc.isExported() == false )
				retDoc.delete();
		}

		return super.updateOrder();
	}
	
	@Override
	protected OffTakeHistory getHistory(String docId, boolean fromOrders) {
		return new OffTakeHistoryEx(docId, fromOrders);
	}
	
	@Override
	protected void makeSaleHistory(Price p) {
		if( document == null )
			return;
		
		history = getHistory(document.getId(), Features.SALES_FROM_ORDERS);

		SimpleDateFormat sf = new SimpleDateFormat("dd.MM");

		ArrayList<Date> labels = history.getLabels();
		ArrayList<OffTakeHistory.Item> items = history.getHistory(p.id);
		LinearLayout ll = (LinearLayout) findViewById(R.id.llSilesHistory);
		
		for( int i=0; i<labels.size(); i++ ) {
			Date cd = labels.get(i);

			OffTakeHistory.Item item = items.get(i);
			TextView tv = new TextView(this);
			
			tv.setGravity(Gravity.RIGHT);
			tv.setTextColor(Color.BLACK);
			tv.setPadding(5, 3, 5, 3);

			String text = item.makeText((i==0)) + "<br>" + sf.format(cd);			
			tv.setText(Html.fromHtml(text));
			
			tv.setLines(getHistoryLines());
			ll.addView(tv);
			
			if( i == 0 ) {
				firstView = tv;
				lastItem = item;
			}
		}
		coef = ((OffTakeHistoryEx)history).getOffTakeCoef(p.id);
	}
	
	@Override protected int getHistoryLines() { return 5; }
	
	@Override protected RestUpdate getRestUpdateHandler() { return new RestUpdateEx(); }
	
	class RestUpdateEx extends RestUpdate {
		@Override
		public void afterTextChanged(Editable txt) {
			if( firstView == null || lastItem == null )
				return;
			
			EditText ed;
			ed = (EditText)findViewById(R.id.edRest);
			lastItem.rest = Util.StrToScale(ed.getText().toString(), Consts.QTY_SCALE);
			
			ed = (EditText)findViewById(R.id.edReturn);
			((OffTakeHistoryEx.ItemEx)lastItem).ret = Util.StrToScale(ed.getText().toString(), Consts.QTY_SCALE);

			lastItem.qty = ((OffTakeHistoryEx)history).calcQty(((OffTakeHistoryEx.ItemEx)lastItem), coef);
			
			if( !loaded ) {
				ed = (EditText)findViewById(R.id.edCount);
				String val = Util.IntToScaleStr(lastItem.qty, Consts.QTY_SCALE);
				ed.setText(val);
				ed.selectAll();
			}
			
			SimpleDateFormat sf = new SimpleDateFormat("dd.MM");
			String text = sf.format(lastItem.date);
			text = lastItem.makeText(true) + "<br>" + text;
			firstView.setText(Html.fromHtml(text));
		}
	}
}
