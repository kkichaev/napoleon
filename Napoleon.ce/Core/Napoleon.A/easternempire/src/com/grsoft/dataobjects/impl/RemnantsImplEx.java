package com.grsoft.dataobjects.impl;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;

public class RemnantsImplEx extends RemnantsImpl {
	class Decorator implements InputNumberDlg.Decorator {
		
		EditText ed;
		int qtyWh = 0;
		
		@Override public int getContentView() { return R.layout.input_remnants_qty; }
	
		@Override
		public void adjustView(AlertDialog dialog, View view, final KeypadHelper nh) {
			View v = view.findViewById(R.id.edQtyWh);
			ed = ((EditText)v);
			ed.setInputType(InputType.TYPE_NULL);
			ed.setText(Util.IntToScaleStr(qtyWh, Consts.QTY_SCALE));
			v.setOnFocusChangeListener(new View.OnFocusChangeListener() {				
				@Override
				public void onFocusChange(View arg0, boolean arg1) {
					if( arg1 ) {
						nh.setTargetID(arg0.getId());
						((EditText)arg0).selectAll();
					}
				}
			});
			
			v = view.findViewById(R.id.edCount);
			v.setOnFocusChangeListener(new View.OnFocusChangeListener() {				
				@Override
				public void onFocusChange(View arg0, boolean arg1) {
					if( arg1 ) {
						nh.setTargetID(arg0.getId());
						((EditText)arg0).selectAll();
					}
				}
			});
			
		}
	
		public void updateQty(RemnantItemEx ri) {
			qtyWh = ri.qtyWh;
		}
		
		public int getQtyWh() {
			return Util.StrToScale(ed.getText().toString(), Consts.QTY_SCALE);
		}
	}
	
	@Override
	public void editItem(final long itemRowid, final Context context) {
		final Decorator decorator = new Decorator();
		
		InputNumberDlg.open(context, new InputNumber() {
			@Override public boolean useComma() { return !Features.INTEGER_INPUTS_QTY; }
			@Override public boolean replaceCommaToPlus() { return Features.REPLACE_COMMA_TO_PLUS; }
			
			@Override
			public void applayInput(int value, Object... params) {
				
				if (isExported())
					return;
				
				PriceImpl priceImpl = new PriceImpl();
				priceImpl.read(itemRowid);
				
				if (updateQty2(priceImpl, value, decorator.getQtyWh()) && context instanceof DataSetNotify)
					((DataSetNotify)context).notifyDataSetChanged();
				
				priceImpl.close();
				
				RemnantsDoc.instance().refreshDocSum(data.id);
			}
	
			@Override
			public int getValue() {
				PriceImpl priceImpl = new PriceImpl();
				priceImpl.read(itemRowid);
				priceImpl.close();
				RemnantItemEx ri = (RemnantItemEx)findItem(priceImpl.data.id);
				
				if( ri != null ) {
					decorator.updateQty(ri);
					return ri.qtyBoard;
				}
				return 0;
			}
		}, Consts.QTY_SCALE, true, context.getString(R.string.value), false, decorator);
	}
	
	public boolean updateQty2(PriceImpl priceImpl, int qtyBoard, int qtyWh) {	
		Price price = priceImpl.getData();
		RemnantItemEx item = (RemnantItemEx) findItem(price.id);

		int qty = qtyBoard + qtyWh;
		boolean needUpdate = true;
		if( item == null ) // new item
		{
			if( qty > 0 )
			{
				item = new RemnantItemEx();
				item.id = price.id;
				item.qty = qty;
				item.qtyBoard = qtyBoard;
				item.qtyWh = qtyWh;
				data.items.add(item);
			}
			else
				needUpdate = false;
		} else
		{
			if( qty == 0 )
				data.items.remove(item);
			else {
				item.qty = qty;
				item.qtyBoard = qtyBoard;
				item.qtyWh = qtyWh;
			}
		}
		
		if( needUpdate )
			write();
		
		return needUpdate;
	}	
}
