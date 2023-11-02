package com.grsoft.napoleon;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.KeypadHelper;

public class ReturnCount extends BaseActivity {
	
	ReturnImpl doc;
	PriceImpl price;
	String cause;
	
	KeypadHelper kh;
	
	public static void open(Context context, ReturnImpl doc, long priceId) {
		Intent i = new Intent(context, ReturnCount.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceId);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.returncount);
		
		doc = (ReturnImpl) ReturnDoc.instance().create();
		price = new PriceImpl();
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID));
		price.read(b.getLong(ExtrasConst.PRICE_ROW_ID_STR, ExtrasConst.INVALID_ID));
		
		Price p = price.getData();
		ReturnItem item = (ReturnItem) doc.findItem(p.id);
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvPriceName);
		tv.setText(p.name);
		
		int qty = (item == null) ? Consts.QTY_SCALE : item.qty;
		cause = (item == null) ? "" : item.cause;
		
		EditText ed = (EditText)findViewById(R.id.edCount);
		ed.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
		ed.setInputType(InputType.TYPE_NULL);
		ed.selectAll();
		
		ConfigImpl c = new ConfigImpl();
		Spinner sp = (Spinner)findViewById(R.id.spCause);
		DialogHelper.loadSpinnerFromConfig(c, "ПричиныВозврата", new ArrayList<CharSequence>(), sp, cause);
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				save();
				finish();
			}
		});
		
		doc.setUpdateQtyHandler(new OrderImplBase.UpdateQtyHandler() {

			@Override
			public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
				((ReturnItem)item).cause = cause;
			}
		});
		
		kh = new KeypadHelper(this, R.id.edCount);
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
		doc.updateQty(price, qty, 0, false);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doc.close();
		price.close();
	}
}
