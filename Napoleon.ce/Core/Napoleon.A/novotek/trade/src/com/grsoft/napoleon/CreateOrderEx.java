package com.grsoft.napoleon;

import com.grsoft.dataobjects.Sklad;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

public class CreateOrderEx extends CreateOrder {
	private Spinner spPrices;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		spPrices = (Spinner) findViewById(R.id.spPrices); 
		
		spPrices.setEnabled(false);
		
		((Spinner)findViewById(R.id.spWh)).setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Sklad s = (Sklad) parent.getItemAtPosition(position);
				
				if(s != null) {
					if (spPrices.getCount() > s.cosType)
						spPrices.setSelection(s.cosType, true);
				}
			}
		});;
	}
}
