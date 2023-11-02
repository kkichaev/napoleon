package com.grsoft.napoleon;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.UnitEx;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;

public class PriceCountEx extends PriceCount {

	ArrayList<UnitEx> units = new ArrayList<UnitEx>();
	int priceCost;

	@Override protected int getContentViewId() {return R.layout.pricecountex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PriceEx p = (PriceEx) price.getData();
		int selected = -1;

		OrderItemEx oi = null;
		if( document != null )
			oi = (OrderItemEx) ((Itemsable)document).findItem(p.id);

		for(UnitItem ui : p.units ) {
			if( oi != null && ui.id.compareTo(oi.unit) == 0 )
				selected = units.size();
			units.add(new UnitEx(ui));
		}
		
		priceCost = Features.COST_MANAGER.getCost(p.id, document == null ? 0 :document.getSumType());

		ArrayAdapter<UnitEx> adapter = new ArrayAdapter<UnitEx>(this, R.layout.simple_spinner_layout, units);		
		Spinner s = (Spinner)findViewById(R.id.spUnits);
		s.setAdapter(adapter);
		if( selected >= 0 )
			s.setSelection(selected);
		
		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				UnitEx u = (UnitEx) parent.getItemAtPosition(position);
				if( u != null ) {
					priceVal = priceCost * u.coef / Consts.SUM_SCALE;
					updateCost();
					updateSumTextView();
				}
			}

			@Override public void onNothingSelected(AdapterView<?> parent) { }
		});
	}
	
	@Override
	protected boolean updateOrder() {
		boolean res =  super.updateOrder();

		OrderItemEx oi = null;
		oi = (OrderItemEx) ((Itemsable)document).findItem(price.getData().id);
		
		if( oi != null ) {
			Spinner s = (Spinner)findViewById(R.id.spUnits);
			UnitEx u = (UnitEx)s.getSelectedItem();
			if( u != null ) {
				oi.unit = u.id;
				document.write();
			}
		}
		
		return res;
	}
}
