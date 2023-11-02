package com.grsoft.napoleon;

import com.grsoft.dataobjects.DiscountMonitoringItem;
import com.grsoft.dataobjects.MntrGoods;
import com.grsoft.dataobjects.impl.DiscountMonitoringImpl;
import com.grsoft.dataobjects.impl.MntrGoodsImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.KeypadHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MntrItemEdit extends BaseActivity {
	DiscountMonitoringImpl doc = new DiscountMonitoringImpl();
	MntrGoodsImpl price = new MntrGoodsImpl();
	KeypadHelper keypadHelper = null;
	
	public static void open(Context c, DiscountMonitoringImpl doc, String id) {
		Intent i = new Intent(c, MntrItemEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, id);
		c.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.monitor_item_edit);

		long orderRowId;
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState; 
		orderRowId = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		
		doc.read(orderRowId);
		
		String id = b.getString(ExtrasConst.PRICE_ROW_ID_STR); 
		MntrGoods item = price.getData();
		item.id = id;
		price.read();
		
		TextView tv = (TextView)findViewById(R.id.tvPriceName);
		tv.setText(item.name);
		
		keypadHelper = new KeypadHelper(this, R.id.edCost);
		
		DiscountMonitoringItem di = (DiscountMonitoringItem) doc.findItem(id);
		if(di == null)
			di = new DiscountMonitoringItem();
		
		EditText ed = (EditText)findViewById(R.id.edCost);
		ed.setOnFocusChangeListener(fcl);
		ed.setText(Util.IntToScaleStr(di.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ed.setInputType(InputType.TYPE_NULL);
		
		ed = (EditText)findViewById(R.id.edQty);
		ed.setOnFocusChangeListener(fcl);
		ed.setText(Util.IntToScaleStr(di.qty, Consts.QTY_SCALE, Util.DEC_DELIM, true));
		ed.setInputType(InputType.TYPE_NULL);

		ed = (EditText)findViewById(R.id.edFacing);
		ed.setOnFocusChangeListener(fcl);
		ed.setText(Util.IntToScaleStr(di.facing, Consts.QTY_SCALE, Util.DEC_DELIM, true));
		ed.setInputType(InputType.TYPE_NULL);
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbIsAction);
		cb.setChecked(di.isAction == 1);
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { save(); }
		});
	}
	
	protected void save() {
		if(doc.isEditable() == false)
			return;
		
		int qty = 0, facing = 0, cost = 0;
		EditText ed = (EditText)findViewById(R.id.edCost);
		cost = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);
		
		ed = (EditText)findViewById(R.id.edQty);
		qty = Util.StrToScale(ed.getText().toString(), Consts.QTY_SCALE);

		ed = (EditText)findViewById(R.id.edFacing);
		facing = Util.StrToScale(ed.getText().toString(), Consts.QTY_SCALE);
		CheckBox cb = (CheckBox)findViewById(R.id.cbIsAction);
		
		MntrGoods item = price.getData();
		doc.updateItem(item.id, qty, facing, cost, cb.isChecked());
		finish();		
	}

	View.OnFocusChangeListener fcl = new View.OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus) {
				keypadHelper.setTargetID(v.getId());
				((EditText)v).selectAll();
			}
		}
	};
	
	
	@Override
	protected void onDestroy() {
		price.close();
		doc.close();
		super.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		outState.putString(ExtrasConst.PRICE_ROW_ID_STR, price.getData().id);
	}
}
