package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;


public class SkladHelper {
	public static int getWhIndex(String id) {
		int index = -1;
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		c.key = "Склады";
		if(ci.read()) {
			ArrayList<KeyValue> values = new ArrayList<KeyValue>();
			index = DialogHelper.makeListWithKey(c.value, values,id);
		}
		ci.close();
		
		if( index < 0 )
			index = 0;
		
		return index;
	}
	
	public static String getWhCode(int index) {
		String ret = "";
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		c.key = "Склады";
		if(ci.read()) {
			ArrayList<KeyValue> values = new ArrayList<KeyValue>();
			DialogHelper.makeListWithKey(c.value, values, "");
			if( index < 0 || index >= values.size())
				index = 0;
			if( values.size() > 0)
				ret = values.get(index).key.toString();
		}
		ci.close();
		return ret;
	}
	
	public static int getItemValue(Price item, int whIndex, String id) {
		if( whIndex == -1 ) 
			whIndex = SkladHelper.getWhIndex(id);
		
		int result = item.qty;
		
		if (whIndex > 0  && whIndex <= ((PriceEx)item).whQty.size())  
			result = ((PriceEx)item).whQty.get(whIndex - 1).qty;
		
		return result;
	}
}
