package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.HashMap;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.WhData;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class OrderImplEx extends OrderImpl {
	HashMap<String, Integer> qtys = null;
	
	public String getWhId() { return ((OrderEx)data).whCode; }
	
	public int getWhIndex() {		
		int index = ((OrderEx)data).whIndex;
		
		if( index < 0) {
			ConfigImpl ci = new ConfigImpl();
			Config c = ci.getData();
			c.key = "Склады";
			if(ci.read()) {
				ArrayList<KeyValue> values = new ArrayList<KeyValue>();
				index = DialogHelper.makeListWithKey(c.value, values, getWhId());
			}
			ci.close();
			
			if( index < 0 )
				index = 0;
			
			((OrderEx)data).whIndex = index;
			write();
		}
		
		return index;
	}
	
	@Override
	public int getItemValue(Price item) {
		int index = getWhIndex();
		if( index == 0 )
			return item.qty;
		
		if(qtys == null) {
			qtys = new HashMap<String, Integer>();
			DataTraveler.travel(WhData.class, new DataTraveler.Travel<WhData>() {

				@Override
				public boolean travel(DataTraveler<WhData> item) {
					qtys.put(item.data.id, item.data.qty);
					return true;
				}
			}, "whCode = '"+ getWhId() + "'");
		}
		
		Integer val = qtys.get(item.id);
		return val == null ? 0 : val;
	}

	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		int index = getWhIndex();
		if( index > 0 ) {
			WhDataImpl wd = new WhDataImpl();
			WhData wddata = wd.getData();
			
			wddata.id = price.getData().id;
			wddata.whCode = getWhId();
			
			wd.read();
			
			wddata.qty += qty;
			wd.write();
			wd.close();
			
		} else
			super.updatePrice(price, qty);
	}
}
