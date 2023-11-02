package com.grsoft.napoleon;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.dataobjects.ExchangeItem;
import com.grsoft.dataobjects.impl.ExchangeImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.InputNumberHelper;

public class ExchangeCount extends BaseActivity {
	
	private static final String QTY_TAG = "QTY_TAG";
	ExchangeImpl doc = new ExchangeImpl();
	Date date = new Date();
	
	PriceImpl price = new PriceImpl();
	EditText edCount;
	
	public static void open(Context context, ExchangeImpl doc, long itemRowID) {
		Intent i = new Intent(context, ExchangeCount.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, itemRowID);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID));
		price.read(b.getLong(ExtrasConst.PRICE_ROW_ID_STR, ExtrasConst.INVALID_ID));
		
		int qty = Consts.QTY_SCALE;
		if(savedInstanceState == null) {
			ExchangeItem item = (ExchangeItem) doc.findItem(price.getData().id);
			if( item != null ) {
				date = item.date;
				qty = item.qty;
			}
		} else {
			qty = b.getInt(QTY_TAG);
			date = new Date(b.getLong(ExtrasConst.DATE_TAG));
		}
		
		setContentView(R.layout.exch_dlg);
		
		edCount = (EditText) findViewById(R.id.edCount);
		edCount.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE, Util.DEC_DELIM, true));
		
		InputNumberHelper nh = new InputNumberHelper((EditText)findViewById(R.id.edCount));
		nh.makeNumericKeypad(findViewById(R.id.llKeyboard));
		
		findViewById(R.id.btnDel).setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				edCount.setText("");
				return false;
			}
		});
	
		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { changeDate(); }
		});
		refreshDate();
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) { 
				save();
				finish();
			}
		});
	}
	
	protected void save() { 
		int qty = Util.StrToScale(edCount.getText().toString(), Consts.QTY_SCALE);
		doc.updateQty(price, qty, date);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( resultCode == RESULT_OK ) {
			Date curDate = new Date();
			if( data != null ) {
				date =  new Date(data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime()));
				refreshDate();
			}
		}
	}

	private void refreshDate() {
		TextView tv = (TextView)findViewById(R.id.tvDate);
		String value = Util.simpleDateFormat.format(date);
		SpannableString ss = new SpannableString(value);
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setTextColor(Color.BLUE);
		tv.setText(ss);
	}

	protected void changeDate() {
		Intent i = new Intent(this, CalendarActivity.class);
		i.putExtra(ExtrasConst.DATE_TAG, date);
		startActivityForResult(i, 0);
	}

	@Override
	protected void onStop() {
		super.onStop();
		price.close();
		doc.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		outState.putLong(ExtrasConst.PRICE_ROW_ID_STR, price.getRowid());
		
		outState.putLong(ExtrasConst.DATE_TAG, date.getTime());
		outState.putInt(QTY_TAG, Util.StrToScale(edCount.getText().toString(), Consts.QTY_SCALE));
	}
}
