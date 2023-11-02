package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.InputNumberDlg.Decorator;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

public class RemnantsImplEx extends RemnantsImpl {
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		return baseInit(context, orgId, coord);
	}
	
	@Override
	public void postInit() {
		super.postInit();
		
		GoodsHelper.fillDocItems(data.id, data.items, 
				DataObjectInfo.getInstance().getListType(data.getClass(), "items"));
	}
	
	void updateEdit(View view, final int id, int qty, final KeypadHelper kh) {
		EditText edit = (EditText) view.findViewById(id);
		edit.setText(qty == 0 ? "" : Util.IntToScaleStr(qty, Consts.QTY_SCALE));
		edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(arg1)
					kh.setTargetID(id);
			}
		});
	}
	
	@Override
	public void editItem(final long itemRowid, final Context context) {
		if (!isEditable())
			return;

		final PriceImpl priceImpl = new PriceImpl();
		priceImpl.read(itemRowid);
		priceImpl.close();

		Decorator olddecor = InputNumberDlg.decorator;

		InputNumberDlg.decorator = new Decorator() {

			@Override public int getContentView() { return R.layout.inputremnants; }

			@Override
			public void adjustView(AlertDialog dialog, final View view, final KeypadHelper kh) {
				RemnantItemEx item = (RemnantItemEx) findItem(priceImpl.data.id);
				updateEdit(view, R.id.edFact, item != null ? item.qtyFact : 0, kh);
				updateEdit(view, R.id.edInput, item != null ? item.qtyInput : 0, kh);
				updateEdit(view, R.id.edUnload, item != null ? item.qtyUnload : 0, kh);
				updateEdit(view, R.id.edBrak, item != null ? item.qtyBrak : 0, kh);

				
				view.findViewById(R.id.edCount).setOnFocusChangeListener(new View.OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if(hasFocus)
							kh.setTargetID(R.id.edCount);
					}
				});
			}
		};

		InputNumberDlg.open(context, new InputNumber() {

			@Override
			public void applayInput(int value, Object... params) {

				if (!isEditable())
					return;

				AlertDialog dlg = (AlertDialog) params[1];
				int factQty = Util.StrToScale(((EditText) dlg.findViewById(R.id.edFact)).getText().toString(), Consts.QTY_SCALE);
				int inputQty = Util.StrToScale(((EditText) dlg.findViewById(R.id.edInput)).getText().toString(), Consts.QTY_SCALE);
				int unloadQty = Util.StrToScale(((EditText) dlg.findViewById(R.id.edUnload)).getText().toString(), Consts.QTY_SCALE);
				int brakQty = Util.StrToScale(((EditText) dlg.findViewById(R.id.edBrak)).getText().toString(), Consts.QTY_SCALE);

				setQty(priceImpl.getData().id, value, factQty, inputQty, unloadQty, brakQty);
				if (context instanceof DataSetNotify)
					((DataSetNotify) context).notifyDataSetChanged();

				RemnantsDoc.instance().refreshDocSum(data.id);
			}

			@Override
			public int getValue() {
				RemnantItemEx item = (RemnantItemEx) findItem(priceImpl.data.id);
				return item == null ? 0 : item.qty;
			}


		}, Consts.QTY_SCALE, true, context.getString(R.string.value), false);

		InputNumberDlg.decorator = olddecor;
	}
	
	void setQty(String id, int qty, int qtyFact, int inputQty, int unloadQty, int brakQty) {
		RemnantItemEx item = (RemnantItemEx) findItem(id);
		if (item == null) {
			item = new RemnantItemEx();
			data.items.add(item);
		}
		
		item.qty = qty;
		item.qtyFact = qtyFact;
		item.qtyInput = inputQty;
		item.qtyUnload = unloadQty;
		item.qtyBrak = brakQty;
		write();
	}
	
	@Override
	public boolean isEmpty() {
		boolean result = true;
		
		for(RemnantItem i : data.items)
			if(i.qty > 0){
				result = false;
				break;
			}
		
		return result;
	}
}
