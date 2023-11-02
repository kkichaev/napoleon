package com.grsoft.napoleon;

import com.grsoft.dataobjects.FocusRejectReason;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderRejectItem;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

public class PriceCountEx extends PriceCount {
	boolean haveFocusReject = false;
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }

	@Override
	protected int getCountValue() {
		int val = super.getCountValue();
		if(haveFocusReject && val > 0) {
			((Spinner)findViewById(R.id.spReason)).setSelection(0);
		}
		return val;
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		if(haveFocusReject) {
			if(qtyItems == 0) {
				Spinner sp = (Spinner)findViewById(R.id.spReason);
				if(sp.getSelectedItemPosition() == 0)
					return false;
				return true;
			}			
		}
		return super.isInputValid(r);
	}
	
	@Override
	protected void invalidInputValueHandler() {
		Toast.makeText(this, "Выберите причину отказа", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected boolean updateOrder() {
		if(haveFocusReject) {
			if(qtyItems == 0) {
				Spinner sp = (Spinner)findViewById(R.id.spReason);
				FocusRejectReason sel = (FocusRejectReason) sp.getSelectedItem();
				((OrderEx)document.getData()).addRejectItem(price.getData().id, sel.id);
				document.write();
				return false;
			}
		}
		return super.updateOrder();
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();

		if(document instanceof OrderImplEx) {
			OrderEx oe = (OrderEx)document.getData(); 
			if(oe.needCheckFocusItems != 0) {
				findViewById(R.id.llReason).setVisibility(View.VISIBLE);
				haveFocusReject = true;
				
				final OrderRejectItem ori = oe.findRejectItem(price.getData().id);
				if(ori != null)
					edCount.setText("0");
				
				Spinner sp = (Spinner)findViewById(R.id.spReason);
				DialogHelper.loadSpinnerFromDataObject(sp, FocusRejectReason.class, new DialogHelper.Selected<FocusRejectReason>() {
					@Override
					public boolean isSelected(FocusRejectReason object) {
						return ori != null && ori.reason.equals(object.id);
					}
				}, true, "name");
				
				sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						if(arg0.getSelectedItemPosition() > 0) {
							edCount.setText("0");
							edCount.selectAll();
						}
					}
					@Override public void onNothingSelected(AdapterView<?> arg0) {}
				});
			}
		}
	}
	
}
