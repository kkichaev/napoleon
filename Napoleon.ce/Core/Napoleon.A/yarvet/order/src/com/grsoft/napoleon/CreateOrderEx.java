package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Order2Ex;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CreateOrderEx extends CreateOrder {
	Spinner spWh;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		spWh = (Spinner) findViewById(R.id.spWh);
		
		Order2Ex o = (Order2Ex)order.getData();
		ConfigImpl config = new ConfigImpl();
		loadSklads(o.whCode, spWh, config);
	}
	
	void loadSklads(String selected, Spinner spWh, ConfigImpl config) {
		ArrayList<KeyValue> values = new ArrayList<KeyValue>();
		Config c = config.getData();
		c.key = "Склады";
		config.read();
		
		int sel = DialogHelper.makeListWithKey(c.value, values, selected);
		int selectedIndex = -1;
		ArrayList<KeyValueIndex> indexs = new ArrayList<KeyValueIndex>();
		int index  = 0;
		for(KeyValue kv:values) {
			if(sel == index)
				selectedIndex = indexs.size();
			indexs.add(new KeyValueIndex(kv, index));
			
			index++;
		}
	
		ArrayAdapter<KeyValueIndex> aa = new ArrayAdapter<KeyValueIndex>(spWh.getContext(), R.layout.simple_spinner_layout, indexs);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spWh.setAdapter(aa);
		if( selectedIndex >= 0 && selectedIndex < spWh.getCount())
			spWh.setSelection(selectedIndex);
	}
	
	@Override
	protected int getLayoutID() {
		return R.layout.createorderex;
	}
	
	@Override
	protected void updateOrder(){
		Order2Ex o = (Order2Ex) order.getData();
		
		Spinner spWh = (Spinner) findViewById(R.id.spWh);
		KeyValueIndex kv = (KeyValueIndex)spWh.getSelectedItem();
		if( kv != null ) {
			o.whCode = kv.key;
			o.whIndex = kv.index;
		}

	}
}

class KeyValueIndex {
	public String key;
	public String value;
	public int index;
	
	public KeyValueIndex(KeyValue v, int index) {
		this.key = v.key.toString();
		this.value = v.value.toString();
		this.index = index;
	}
	
	@Override
	public String toString() {
		return value;
	}
}