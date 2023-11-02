package com.grsoft.dataobjects.impl;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.InputNumberDlg.Decorator;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;


public class RemnantsImplEx extends RemnantsImpl {
	@Override
	public void editItem(final long itemRowid, final Context context) { 
		final PriceImpl priceImpl = new PriceImpl();
		priceImpl.read(itemRowid);
		priceImpl.close();
		
		InputNumberDlg.open(context, new InputNumber() {
			
			@Override public boolean useComma() { return !Features.INTEGER_INPUTS_QTY; }
			@Override public boolean replaceCommaToPlus() { return Features.REPLACE_COMMA_TO_PLUS; }
			
			@Override
			public void applayInput(int value, Object... params) {
				if (isExported())
					return;
				
				if(params.length > 1 && params[1] instanceof AlertDialog){
					AlertDialog view = (AlertDialog) params[1];
					EditText ed = (EditText) view.findViewById(R.id.edShelf);
					int qtyWh = value;
					int qtySh = 0;
					
					if(ed != null){
						qtySh = Util.StrToScale(ed.getText().toString().trim(), Consts.QTY_SCALE);
						value += qtySh;
					}
					
					if (updateQty(priceImpl, value, 0, false, qtyWh, qtySh ) && context instanceof DataSetNotify)
						((DataSetNotify)context).notifyDataSetChanged();
					
					RemnantsDoc.instance().refreshDocSum(data.id);
				}
			}
			
			@Override
			public int getValue() {return 0;}
			
		}, Consts.QTY_SCALE, true,  context.getString(R.string.value), false, new Decorator() {
			@Override public int getContentView() { return R.layout.inputremnant;	}
			@Override public void adjustView(AlertDialog dialog, final View view, final KeypadHelper kh) {
				class EditOnFocusChangeListener implements OnFocusChangeListener{	
					@Override public void onFocusChange(View v, boolean hasFocus) { 
					if(hasFocus){ 
						kh.setTargetID(v.getId()); 
					}
				}};
				
				RemnantItemEx item = (RemnantItemEx) findItem(priceImpl.getData().id);
				
				
				EditText ed = (EditText) view.findViewById(R.id.edCount);
				if(ed != null){
					if(item != null)
						ed.setText(Util.IntToScaleStr(item.qtyWh, Consts.QTY_SCALE));
					ed.setOnFocusChangeListener(new EditOnFocusChangeListener());
				}
				
				ed = (EditText) view.findViewById(R.id.edShelf);
				
				if(ed != null){
					if(item != null)
						ed.setText(Util.IntToScaleStr(item.qtySh, Consts.QTY_SCALE));
					ed.setOnFocusChangeListener(new EditOnFocusChangeListener());
					ed.setInputType(InputType.TYPE_NULL);
				}
			}
		});
	}
	
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack, int qtyWh, int qtySh) {	
		Price price = priceImpl.getData();
		RemnantItemEx item = (RemnantItemEx) findItem(price.id);

		boolean needUpdate = true;
		if( item == null ) // new item
		{
			if( qty > 0 )
			{
				item = new RemnantItemEx();
				item.id = price.id;
				item.qty = qty;
				item.qtySh = qtySh;
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
				item.qtySh = qtySh;
				item.qtyWh = qtyWh;
			}
		}
		
		if( needUpdate )
			write();
		
		return needUpdate;
	}
}
