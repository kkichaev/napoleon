package com.grsoft.dataobjects.impl;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import com.grsoft.dataobjects.Contract;
import com.grsoft.dataobjects.ContractItem;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.ContractDetail;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.InputNumberDlg.Decorator;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;

public class ContractImpl extends CreatableDocument<Contract> implements Itemsable {

	@Override
	public void open(Context context) { ContractDetail.open(context, getRowid()); }
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		if (super.init(context, orgId, gpsCoord))
			Warehouse.open(context, this, false);
		return false;
	}

	@Override
	public void editItem(final long itemRowid, final Context context) {
		PriceImpl priceImpl = new PriceImpl();
		priceImpl.read(itemRowid);
		priceImpl.close();
		final String pid = priceImpl.getData().id;
		
		InputNumberDlg.decorator = new Decorator(){
			@Override
			public int getContentView() { return R.layout.inputnumberdlgex; }
			
			@Override
			public void adjustView(AlertDialog dialog, View view, final KeypadHelper nh) {
				EditText edFace = (EditText) view.findViewById(R.id.edFace);
				edFace.setText("0");
				edFace.setInputType(InputType.TYPE_NULL);
				edFace.requestFocus();
				edFace.setOnFocusChangeListener(focusListener(nh));
				
				EditText edCount = (EditText) view.findViewById(R.id.edCount);
				edCount.setOnFocusChangeListener(focusListener(nh));
				
				int face = 0;
				
				ContractItem ci = (ContractItem) findItem(pid);
				
				if(ci != null)
					face = ci.face;
				
				edFace.setText(Util.IntToScaleStr(face, Consts.QTY_SCALE));
				edFace.selectAll();
				
				nh.setTargetID(edFace.getId());
				
				View btnComma = view.findViewById(R.id.btnComma); 
				btnComma.setVisibility(View.GONE);
			}

			protected OnFocusChangeListener focusListener(final KeypadHelper nh) {
				return new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if(hasFocus){
							((EditText)v).selectAll();
							nh.setTargetID(v.getId());
						}
					}
				};
			}
		};
		
		InputNumberDlg.open(context, new InputNumber() {
			
			@Override public boolean useComma() { return !Features.INTEGER_INPUTS_QTY; }
			@Override public boolean replaceCommaToPlus() { return Features.REPLACE_COMMA_TO_PLUS; }
			
			@Override
			public void applayInput(int value, Object... params) {
				if (isExported())
					return;
								
				Dialog dlg = (Dialog)params[1];
				EditText edFace = (EditText) dlg.findViewById(R.id.edFace);
				int face = Util.StrToScale(edFace.getText().toString().trim(), Consts.QTY_SCALE);
				
				ContractItem ci = (ContractItem) findItem(pid);
				
				if(ci == null){
					ci = new ContractItem();
					ci.id = pid;
					data.items.add(ci);
				}
					
				ci.qty = value;
				ci.face = face;
				
				if(ci.qty == 0 && ci.face == 0)
					data.items.remove(ci);
					
				write();
				
				((DataSetNotify)context).notifyDataSetChanged();
			}

			@Override
			public int getValue() {
				ContractItem ri = (ContractItem)findItem(pid);
				
				return ri == null ? 0 : ri.qty;
			}
		}, Consts.QTY_SCALE, true, context.getString(R.string.contract_input_val));
	}

	@Override
	public DataObject findItem(String itemId) {
		for(ContractItem ci : data.items)
			if(ci.id.equals(itemId))
				return ci;
		return null;
	}

	@Override
	public int getItemColor() { return Color.GREEN;	}

	@Override
	public int getItemValue(Price item) {
		ContractItem i = (ContractItem) findItem(item.id);
		
		if(i != null)
			return i.face;
		
		return 0;
	}

	@Override
	public int getItemQty(Price item) {
		ContractItem i = (ContractItem) findItem(item.id);
		
		if(i != null)
			return i.qty;
		
		return 0;
	}

	@Override
	public long getItemSum(Price item) { return 0; }

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		return false;
	}

	@Override
	public int qty(){
		int result = 0;
		
		for(ContractItem i : data.items)
			result += i.qty;
		
		return result;
	}
	
	public int face() {
		int result = 0;
		
		for(ContractItem i : data.items)
			result += i.face;
		
		return result;
	}

}
