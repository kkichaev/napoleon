package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.KeypadHelper;

public class ReturnCount extends BaseActivity {
	
	protected static final int PRD_DATE_PICKER_ID = 10;
	
	static final String ROW_INDEX = "item_row_index";
	
	ReturnImpl doc;
	PriceImpl price;
	String cause;
	Date prdDate = null;
	
	ReturnItem editItem = null;
	
	KeypadHelper kh;
	
	public static void open(Context context, ReturnImpl doc, long priceId) {
		Intent i = new Intent(context, ReturnCount.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceId);
		context.startActivity(i);
	}

	public static void openByIndex(Context context, ReturnImpl doc, int index) {
		Intent i = new Intent(context, ReturnCount.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ROW_INDEX, index);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.returncount);
		
		doc = (ReturnImpl) ReturnDoc.instance().create();
		price = new PriceImpl();
		Price p = price.getData();
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID));
		
		long pid = b.getLong(ExtrasConst.PRICE_ROW_ID_STR, ExtrasConst.INVALID_ID);
		if( pid != ExtrasConst.INVALID_ID) {
			price.read(pid);
			editItem = (ReturnItem) doc.findItem(p.id);
		} else {
			int index = b.getInt(ROW_INDEX, -1);
			if(index >= 0) {
				editItem = (ReturnItem) doc.getData().items.get(index);
				p.id = editItem.id;
				price.read();
			}
		}
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvPriceName);
		tv.setText(p.name);
		
		int qty = (editItem == null) ? Consts.QTY_SCALE : editItem.qty;
		if( editItem != null ) {
			Calendar c = Calendar.getInstance(Locale.getDefault());
			c.set(1900, 1, 1);
			if(c.getTime().compareTo(editItem.prdDate) < 0)
				prdDate = editItem.prdDate;
		}
		cause = (editItem == null) ? "" : editItem.cause;
		refreshDate();
		
		EditText ed = (EditText)findViewById(R.id.edCount);
		ed.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
		ed.setInputType(InputType.TYPE_NULL);
		ed.selectAll();
		
		ConfigImpl c = new ConfigImpl();
		Spinner sp = (Spinner)findViewById(R.id.spCause);
		DialogHelper.loadSpinnerFromConfig(c, "ПричиныВозврата", new ArrayList<CharSequence>(), sp, cause);

		DialogHelper.loadSpinnerWithKey(c, "Смены", new ArrayList<KeyValue>(), 
				(Spinner)findViewById(R.id.spTurn), editItem == null? "" : editItem.turn);
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				if(checkValid()) {
					save();
					finish();
				}
			}
		});
		
		findViewById(R.id.tvPrdDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ReturnCount.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, prdDate == null ? new Date() : prdDate);
				startActivityForResult(i, PRD_DATE_PICKER_ID);
			}
		});
		
		doc.setUpdateQtyHandler(new OrderImplBase.UpdateQtyHandler() {

			@Override
			public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
				ReturnItem ri = (ReturnItem)item; 
				ri.cause = cause;
				Spinner sp = (Spinner)findViewById(R.id.spTurn);
				KeyValue sel = (KeyValue) sp.getSelectedItem();
				if( sel != null)
					ri.turn = sel.key.toString();
				
				if(prdDate != null)
					ri.prdDate = prdDate;
				else {
					Calendar c = Calendar.getInstance(Locale.getDefault());
					c.set(1900, 0, 1);
					ri.prdDate= c.getTime();
				}
			}
		});
		
		kh = new KeypadHelper(this, R.id.edCount);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == PRD_DATE_PICKER_ID ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			prdDate = new Date(ct);
			refreshDate();
		}
	}
	
	private void refreshDate() {
		TextView tv;
		String text;

		tv = (TextView)findViewById(R.id.tvPrdDate);
		if(prdDate == null)
			text = "<u><font color='blue'><i>введите дату...</i></font></u>";
		else
			text = "<u><font color='blue'>" + Util.simpleDateFormat.format(prdDate) + "</font></u>";
		tv.setText(Html.fromHtml(text));
		
		text = "";
		if(prdDate != null) {
			Calendar c = Calendar.getInstance(Locale.getDefault());
			c.setTime(prdDate);
			c.add(Calendar.DAY_OF_MONTH, ((PriceEx)price.getData()).expiration);
			text = Util.simpleDateFormat.format(c.getTime());
		}
		tv = (TextView)findViewById(R.id.tvExpDate);
		tv.setText(text);
	}

	protected boolean checkValid() {
//		if(prdDate == null) {
//			Toast.makeText(this, "Введите дату производства", Toast.LENGTH_SHORT).show();
//			return false;
//		}
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		outState.putLong(ExtrasConst.PRICE_ROW_ID_STR, price.getRowid());
	}
	
	protected void save() {
		if( doc.isExported() )
			return;
		String qs = ((EditText)findViewById(R.id.edCount)).getText().toString();		
		int qty = Util.StrToScale(qs, Consts.QTY_SCALE);
		
		Spinner sp = (Spinner)findViewById(R.id.spCause);
		cause = (String)sp.getSelectedItem();
		if(cause == null)
			cause = "";
		if(editItem == null)
			doc.updateQty(price, qty, 0, false);
		else
			((ReturnImplEx)doc).updateQty(editItem, qty, 0, false);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doc.close();
		price.close();
	}
}
