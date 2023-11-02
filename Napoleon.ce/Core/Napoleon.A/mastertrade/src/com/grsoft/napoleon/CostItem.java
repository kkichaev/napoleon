package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.impl.ConfigImpl;

public class CostItem {
	public String name = "";
	public int index;
	
	public CostItem() {}
	
	public CostItem(String name, int index) {
		this.name = name;
		this.index = index;
	}
	
	@Override public String toString() { return name; }
	
	public static List<CostItem> getItems(boolean appendBlank) {
		List<CostItem> ret = new ArrayList<CostItem>();
		
		ConfigImpl ci = new ConfigImpl();
		Config cfg = ci.getData();
		
		cfg.key = "–азрешенные÷ены";
		ci.read();
		
		String used = cfg.value;
		cfg.key = "¬ид÷ены";
		ci.read();
		
		ci.close();
		
		if(appendBlank)
			ret.add(new CostItem("", -1));
		
		String[] prices = cfg.value.split(";");
		for(int i=0; i<prices.length; i++) {
			String cost = prices[i];
			if(used.contains(cost + ";"))
				ret.add(new CostItem(cost, i));
		}
		
		return ret;
	}
	
	public static int getSelected(List<CostItem> values, int index) {
		for(int i=0; i<values.size(); i++)
			if(values.get(i).index == index)
				return i;
		
		return -1;
	}
}
