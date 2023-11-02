package com.grsoft.napoleon;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.KeypadHelper;

public class ReturnCount extends BaseActivity implements OrderImplBase.UpdateQtyHandler {
	
	PriceImpl pi = new PriceImpl();
	ReturnImpl doc;
	KeypadHelper helper;
	
	public static void open(Context context, ReturnImpl doc, long item) {
		Intent i = new Intent(context, ReturnCount.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, item);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.return_count);
		
		doc = (ReturnImpl) ReturnDoc.instance().create();
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		doc.setUpdateQtyHandler(this);
		
		pi.read(b.getLong(ExtrasConst.PRICE_ROW_ID_STR, ExtrasConst.INVALID_ID));
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID));

		doc.setUpdateQtyHandler(new OrderImpl.UpdateQtyHandler() {

			@Override
			public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
				KeyValue kv = (KeyValue) ((Spinner)findViewById(R.id.spCause)).getSelectedItem();
				if( kv != null )
					((ReturnItemEx)item).cause = kv.key.toString();
			}
		});
		
		Price p = pi.getData();
		ReturnItemEx item = (ReturnItemEx) doc.findItem(p.id);
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvName);
		tv.setText(p.name);
		
		int qty = (item == null) ? Consts.QTY_SCALE : item.qty;
	
        ConfigImpl c = new ConfigImpl();
        ArrayList<KeyValue> values = new ArrayList<KeyValue>();        
		DialogHelper.loadSpinnerWithKey(c, "ПричиныВозврата", values, (Spinner)findViewById(R.id.spCause), 
				(item == null) ? "" : item.cause);
		c.close();
		
		
		
		EditText ed;
		ed = (EditText)findViewById(R.id.edCount);
		ed.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
		ed.setInputType(InputType.TYPE_NULL);
		ed.selectAll();
		
		helper = new KeypadHelper(this, R.id.edCount);
		
		String text = item == null ? "" : item.remark;
		ed = (EditText)findViewById(R.id.edRemark);
		ed.setText(text);

		((CheckBox)findViewById(R.id.cbKG)).setChecked(item != null && item.inKG > 0);
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText ed;
				ed = (EditText)findViewById(R.id.edCount);
				
				int qty = Util.StrToScale(ed.getText().toString(), Consts.QTY_SCALE);
				int cost = 0;
				if( Features.USE_COST_IN_RETURNS ) {
					CostStrategy cs = CostStrategy.getInstance(ReturnImpl.class);
					cost = cs.getItemCost(pi.getData(), doc);
				}
				
				
				doc.updateQty(pi, qty, cost, false);
				ReturnDoc.instance().refreshDocSum(doc.getId());
				finish();
			}
		});
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.PRICE_ROW_ID_STR, pi.getRowid());
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		ReturnItemEx re = (ReturnItemEx)item;
		re.remark = ((EditText)findViewById(R.id.edRemark)).getText().toString();
		re.inKG = ((CheckBox)findViewById(R.id.cbKG)).isChecked() ? 1 : 0;
	}
}
