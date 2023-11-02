package com.grsoft.napoleon;

import java.util.Date;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RejectActItem;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RejectActImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.KeypadHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RejectPriceCount extends BaseActivity {
	static String ITEM_ID = "item_id";
	static String DOC_NUMBER = "doc_number";
	static String DOC_DATE = "doc_date";
	static String RIGHT_OK = "right_ok";
	static String START_VALUE = "start_value";
	static String PARTY = "party";
	static String EXPIRED = "expired";
	
	RejectActImpl doc;
	RejectActItem item;
	boolean rightOK = true;
	KeypadHelper kh;
	int maxQty = 0;
	
	public static void open(Context context, RejectActImpl doc, String id, String number, Date date, int startValue, String party, Date expired) {
		Intent i = new Intent(context, RejectPriceCount.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ITEM_ID, id);
		i.putExtra(DOC_NUMBER, number);
		i.putExtra(DOC_DATE, date.getTime());
		i.putExtra(START_VALUE, startValue);
		i.putExtra(EXPIRED, expired.getTime());
		i.putExtra(PARTY, party);
		
		SharedPreferences sp = context.getSharedPreferences("RejectPriceCount", Context.MODE_PRIVATE);
		i.putExtra(RIGHT_OK, sp.getBoolean(RIGHT_OK, true));
		
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;

		rightOK = b.getBoolean(RIGHT_OK, true);
		setContentView(rightOK ? R.layout.reject_price_count : R.layout.reject_price_count_l);
		
		doc = new RejectActImpl();
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		String id = b.getString(ITEM_ID);
		String number = b.getString(DOC_NUMBER);
		Date docDate = new Date(b.getLong(DOC_DATE));
		Date expDate = new Date(b.getLong(EXPIRED));
		String party = b.getString(PARTY);
		
		DeliveryImpl di = new DeliveryImpl();
		DeliveryEx dlv = (DeliveryEx) di.getData();
		dlv.number = number;
		dlv.id  = doc.getData().id;
		di.read();
		di.close();
		
		for(DeliveryItem ditem : dlv.items) {
			if(!ditem.id.equals(id))
				continue;
			DeliveryItemEx diteme = (DeliveryItemEx)ditem;
			if(diteme.expired.equals(expDate) && diteme.party.equals(party)) {
				maxQty = diteme.qty;
				break;
			}
		}
		
		item = doc.findItem(id, number, docDate, party);
		if(item == null) {
			item = new RejectActItem();
			item.id = id;
			item.date = docDate;
			item.number = number;
			item.party = party;
			item.expired = expDate;
			
			doc.getData().items.add(item);
		}
		item.qty = b.getInt(START_VALUE, 0);
		
		PriceImpl pi = new PriceImpl();
		
		doc.setFormText(this);
		TextView tv = (TextView) findViewById(R.id.tvPrice);
		String text = "";
		if(pi.read("id", id))
			text = ((PriceEx)pi.getData()).getName();
		else
			text = "Код " + pi.getData().id;
		text += "<br/>Кол-во в партии: " + Util.IntToScaleStr(maxQty, Consts.QTY_SCALE);
		tv.setText(Html.fromHtml(text));
		
		EditText ed = (EditText) findViewById(R.id.edCount);
		ed.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE));
		ed.selectAll();
		
		kh = new KeypadHelper(this, R.id.edCount);
		
		findViewById(R.id.btnChangeOrient).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SharedPreferences sp = RejectPriceCount.this.getSharedPreferences("RejectPriceCount", Context.MODE_PRIVATE);
				SharedPreferences.Editor ed = sp.edit();
				ed.putBoolean(RIGHT_OK, !rightOK);
				ed.commit();
				
				open(RejectPriceCount.this, doc, item.id, item.number, item.date, getValue(), item.party, item.expired);
				finish();
			}
		});
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(doc.isEditable()) {
					int count = getValue();
					if(count == 0) {
						doc.getData().items.remove(item);
					} else {
						if(count > maxQty) {
							Toast.makeText(RejectPriceCount.this, "Количество больше доступного в партии", Toast.LENGTH_LONG).show();
							return;
						}
						item.qty = count;
					}
					doc.write();
				}
				finish();
			}
		});
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	int getValue() {
		EditText ed = (EditText) findViewById(R.id.edCount);
		return Util.StrToScale(ed.getText().toString(), Consts.QTY_SCALE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		outState.putString(ITEM_ID, item.id);
		outState.putString(DOC_NUMBER, item.number);
		outState.putLong(DOC_DATE, item.date.getTime());
		outState.putBoolean(RIGHT_OK, rightOK);
		outState.putInt(START_VALUE, getValue());
		outState.putLong(EXPIRED, item.expired.getTime());
		outState.putString(PARTY, item.party);
	}
}
