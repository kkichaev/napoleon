package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.StringCause;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class RemnantsImplEx extends RemnantsImpl {
	
	@Override
	public void editItem(long itemRowid, final Context context) {
		if( !isEditable() )
			return;
		
		final PriceImpl price = new PriceImpl();
		price.read(itemRowid);
		price.close();

		InputNumberDlg.open(context, new InputNumber() {
			
			@Override
			public void applayInput(int value, Object... params) {
				if (isEditable() && params.length > 1){
					applayDocInput(context, price.getData().id, params[1]);
				}
			}

			protected void applayDocInput(final Context context, final String id, Object param) {
				Dialog view = (Dialog) param; 
				
				if (view != null) {
					EditText edCount = (EditText) view.findViewById(R.id.edCount);
					Spinner spCause = (Spinner) view.findViewById(R.id.spCause);
					
					int qty = Util.StrToScale(edCount.getText().toString().trim(), Consts.QTY_SCALE);
					
					boolean refresh = false;
					
					if( Features.REST_IN_PACK )
						qty = (int)((long)qty * price.getData().qtyInPack / Consts.QTY_SCALE);
					refresh = updateQty(price, qty, 0, false);
					
					RemnantItemEx i = (RemnantItemEx) findItem(price.getData().id);
					i.cause = "";
					
					if (i != null) {
						if (qty == 0){
							StringCause s = (StringCause) spCause.getSelectedItem();
							
							if (s != null && s.text.trim().length() > 0) {
								i.cause = s.id;
							}else
								deleteItem(price.getData());
						}
					}
						
					write();
					close();
					
					if (refresh && context instanceof DataSetNotify)
						((DataSetNotify)context).notifyDataSetChanged();
					
					RemnantsDoc.instance().refreshDocSum(data.id);
				}
			}
	
			@Override
			public int getValue() {				
				return 0;
			}
				
			}, Consts.SUM_SCALE, true, context.getString(R.string.input_value), false, 
			new InputNumberDlg.Decorator() {
				
				@Override public int getContentView() { return R.layout.inputnumberdlgex; }
					
				private void edInit(EditText ed, final KeypadHelper nh) {
					ed.setInputType(InputType.TYPE_NULL);
					ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {				
						@Override
						public void onFocusChange(View arg0, boolean arg1) {
							if( arg1 ) {
								nh.setTargetID(arg0.getId());
								((EditText)arg0).selectAll();
							}
						}
					});
				}
				
				@Override
				public void adjustView(AlertDialog dialog, View view, final KeypadHelper nh) {
					EditText edCount = (EditText) view.findViewById(R.id.edCount);
					Spinner spCause = (Spinner) view.findViewById(R.id.spCause);
					
					for(EditText tv : new EditText[] {edCount})
						edInit(tv, nh);
					
					final List<StringCause> list = new ArrayList<StringCause>();
					
					DataTraveler.travel(StringCause.class, new DataTraveler.Travel<StringCause>(true) {

						@Override
						public boolean travel(DataTraveler<StringCause> item) {
							list.add(item.data);
							return true;
						}
					}, null);
					
					Collections.sort(list, new Comparator<StringCause>() {

						@Override
						public int compare(StringCause lhs, StringCause rhs) {
							return lhs.text.compareTo(rhs.text);
						}});
					
					list.add(0, new StringCause());
					
					spCause.setAdapter(new ArrayAdapter<StringCause>(context, R.layout.simple_spinner_layout, list));
					
					RemnantItemEx i = (RemnantItemEx) findItem(price.getData().id);
					
					if (i != null) {
						edCount.setText(Util.IntToScaleStr(i.qty, Consts.QTY_SCALE));
						
						for(int idx = 0; idx < spCause.getCount(); idx++) {
							StringCause c = (StringCause) spCause.getItemAtPosition(idx);
							
							if (c.id.equals(i.cause)) {
								spCause.setSelection(idx, true);
								break;
							}
						}
					}
					
					edCount.selectAll();
				}
			}
		);
	}
}
