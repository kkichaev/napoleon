package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.UnitEx;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {

	ArrayList<UnitEx> units = new ArrayList<UnitEx>();
	UnitEx selected = null;
	protected Spinner spUnits;

	@Override protected int getContentViewId() {return R.layout.pricecountex; }
	
	@Override
	protected int getQtyInPack(Price p) {
		
		if(p != null && selected == null ) {
			String scode = "";
			
			List<UnitItem> units = ((PriceEx)p).units;
			if( document != null ) {
				OrderItemEx oi = (OrderItemEx) getDocItem(p);;
				if( oi != null )
					scode = oi.unit;
			} else if( units.size() > 0 ) {
				scode = units.get(0).id;
			}
			
			this.units.clear();
			for(UnitItem ui : units ) {
				UnitEx uex = new UnitEx(ui);
				if(ui.id.compareTo(scode) == 0 )
					selected = uex;
	
				this.units.add(uex);
			}
		}
		
		if( selected != null )
			return selected.inpack;

		return super.getQtyInPack(p);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ArrayAdapter<UnitEx> adapter = new ArrayAdapter<UnitEx>(this, R.layout.simple_spinner_layout, units);
		spUnits = (Spinner)findViewById(R.id.spUnits);
		spUnits.setAdapter(adapter);

		spUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { 
				onUnitChanged(units.get(pos));
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		TextView tv = (TextView)findViewById(R.id.tvPrice);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				InputNumberDlg.open(PriceCountEx.this, new InputNumber() {
					@Override public void applayInput(int value, Object... params) { onChangeCost(value); }
					@Override public long getValue() { return priceVal; }
				}, Consts.SUM_SCALE, false, "Цена"); 
			}
		});
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbPackets);
		cb.setVisibility(View.GONE);
		cb.setChecked(true);
		
		if(document instanceof OrderImpl)
			((OrderImpl)document).setUpdateQtyHandler(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if( document != null ) {
			OrderItemEx oie = (OrderItemEx) getDocItem(price.getData());
			
			if (oie != null) {
				for(UnitEx unit : units)
					if (unit.id.equals(oie.unit)){
						spUnits.setSelection(units.indexOf(unit));
						onUnitChanged(unit);
						break;
					}
			} else {
				if( spUnits.getCount() > 0)
					spUnits.setSelection(0);				
			}
		}
	}
	
	void onUnitChanged(UnitEx newUnit) {
		selected = newUnit;		
		qtyInPack = newUnit.inpack;
		if( qtyInPack == 0 )
			qtyInPack = Consts.QTY_SCALE;

		TextView tvQtyInPack = (TextView) findViewById(R.id.tvQtyInPack);
		tvQtyInPack.setText(Util.IntToScaleStr(qtyInPack, Consts.QTY_SCALE));

		updateSumTextView();
	}
	

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		if(selected != null) {
			((OrderItemEx)item).unit = selected.id;
		}
		
	}
}
