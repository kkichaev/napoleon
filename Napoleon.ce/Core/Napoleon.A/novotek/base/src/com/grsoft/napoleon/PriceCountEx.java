package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.UnitEx;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {

	ArrayList<UnitEx> units = new ArrayList<UnitEx>();
	UnitEx selected = null;
	protected Spinner spUnits;
	boolean enablePiece = false;

	@Override protected int getContentViewId() {return R.layout.pricecountex; }
	
	@Override
	protected int getQtyInPack(Price p) {
		if(p != null && selected == null ) {
			String scode = "";
			
			List<UnitItem> units = ((PriceEx)p).units;
			if( document instanceof OrderImplEx ) {
				enablePiece = (((OrderEx)document.getData()).canDiv == 2);
				OrderItemEx oi = (OrderItemEx) getDocItem(p);;
				if( oi != null )
					scode = oi.unit;
			} else if( units.size() > 0 ) {
				scode = units.get(0).id;
			}

			for(UnitItem ui : units ) {
				UnitEx uex = new UnitEx(ui);
				if(ui.id.compareTo(scode) == 0 )
					selected = uex;
	
				if( (enablePiece || ((PriceEx)price.getData()).piece != 0) || ui.inpack != 1 * Consts.QTY_SCALE)
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
					@Override public int getValue() { return priceVal; }		
				}, Consts.SUM_SCALE, false, "Цена"); 
			}
		});
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbPackets);
		cb.setVisibility(View.GONE);
		cb.setChecked(true);
		
		cb = (CheckBox) findViewById(R.id.cbPiece);
		cb.setOnClickListener(null);
		
		if(document instanceof OrderImplEx)
			((OrderImplEx)document).setUpdateQtyHandler(this);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		PriceEx pe = (PriceEx)price.getData();

		CheckBox cb = (CheckBox) findViewById(R.id.cbPiece);
		cb.setVisibility(enablePiece ? View.INVISIBLE : View.VISIBLE);
		cb.setChecked(pe.piece != 0);
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvKgCost);
		int weight = pe.weight;
		if( weight == 0 )
			weight = Consts.WEIGHT_SCALE;
		int kgCost = priceVal * Consts.WEIGHT_SCALE / weight;
		tv.setText(Util.IntToScaleStr(kgCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
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
		OrderItemEx oie = (OrderItemEx)item;
		if(selected != null)
			oie.unit = selected.id;
		oie.outQty = ((OrderImplEx)document).outItemQty;
	}
}
