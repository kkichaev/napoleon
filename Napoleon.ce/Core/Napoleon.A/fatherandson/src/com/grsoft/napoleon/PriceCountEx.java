package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceUnit;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }

	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if( document instanceof OrderImplBase )
			((OrderImplBase)document).setUpdateQtyHandler(this);
		
		((Spinner)findViewById(R.id.spUnits)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				qtyInPack = ((PriceUnit)arg0.getAdapter().getItem(arg2)).inPack;
				
				TextView tvQtyInPack = (TextView) findViewById(R.id.tvQtyInPack);
				tvQtyInPack.setText(Util.IntToScaleStr(qtyInPack, Consts.QTY_SCALE));
				updateSumTextView();
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) { }
		});
		
		if(document instanceof ReturnImplEx)
			findViewById(R.id.edRemark).setVisibility(View.GONE);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		PriceEx p = (PriceEx) price.getData();
		Spinner sp = (Spinner)findViewById(R.id.spUnits);
		ArrayAdapter<PriceUnit> adapter = new ArrayAdapter<PriceUnit>(this, R.layout.simple_spinner_layout, p.units);
		sp.setAdapter(adapter);
		
		if(document instanceof OrderImpl) {
			OrderItemEx oe = (OrderItemEx)((OrderImpl)document).findItem(p.id);
			if( oe != null ) {
				((EditText)findViewById(R.id.edRemark)).setText(oe.remark);
				
				int sel = 0;
				for(PriceUnit pu : p.units) {
					if(pu.id.equals(oe.unitId)) {
						qtyItems = (int)((long)oe.qty * Consts.QTY_SCALE/ pu.inPack);
						
						edCount.setText(Util.IntToScaleStr(qtyItems, Consts.QTY_SCALE));
						edCount.selectAll();						
						sp.setSelection(sel);
						break;
					}
					sel++;
				}
			}
		} else if(document instanceof ReturnImplEx) {
			@SuppressWarnings("rawtypes")
			ReturnItemEx oe = (ReturnItemEx)((OrderImplBase)document).findItem(p.id);
			if( oe != null ) {
				int sel = 0;
				for(PriceUnit pu : p.units) {
					if(pu.id.equals(oe.unitId)) {
						qtyItems = (int)((long)oe.qty * Consts.QTY_SCALE/ pu.inPack);
						
						edCount.setText(Util.IntToScaleStr(qtyItems, Consts.QTY_SCALE));
						edCount.selectAll();						
						sp.setSelection(sel);
						break;
					}
					sel++;
				}
			}
		}
	}
	
	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		PriceUnit pu = (PriceUnit)((Spinner)findViewById(R.id.spUnits)).getSelectedItem();
		if(document instanceof ReturnImplEx) {
			ReturnItemEx oie = (ReturnItemEx)item;
			if( pu != null )
				oie.unitId = pu.id;
		} else {
			OrderItemEx oie = (OrderItemEx)item;
			if( pu != null )
				oie.unitId = pu.id;
			oie.remark = ((EditText)findViewById(R.id.edRemark)).getText().toString();
		}
	}
}
