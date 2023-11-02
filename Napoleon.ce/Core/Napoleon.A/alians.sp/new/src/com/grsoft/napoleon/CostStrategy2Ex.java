package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.ContractItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ContractImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategy2Ex extends CostStrategyEx {
	static Map<String, HashMap<String, Integer>> disc = new HashMap<String, HashMap<String, Integer>>();
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int d = getDiscount(p, doc); 
			
		if (d != 0) {
			double discount = (double)d / Consts.SUM_SCALE;
			double cost = (double)super.getItemCost(p, doc) / Consts.SUM_SCALE;
			double newCost = cost / (discount * 0.01 + 1) * Consts.SUM_SCALE;
			
			return (int) Math.round(newCost);
		}else
			return super.getItemCost(p, doc);
	}
	
	private void buildCashDisc(String id) {
		if (!disc.containsKey(id)) {
			HashMap<String, Integer> discMap = new HashMap<String, Integer>();

			ContractImpl di = new ContractImpl();
			if (di.read("id",id)){
				for(ContractItem i : di.getData().items)
					if(!discMap.containsKey(i.id))
						discMap.put(i.id, i.discount);
			}
			
			disc.put(id,  discMap);
		}
	}
	
	public static void resetCash(){
		disc.clear();
		cash.clear();
	}
	
	public int getDiscount(Price p, Document<?> doc) {
		int res = 0;
		
		if (doc instanceof OrderImpl) {
			String contract = ((OrderEx)doc.getData()).contract; 
			buildCashDisc(contract);
			
			HashMap<String, Integer> discMap = disc.get(contract);
			
			if (discMap.containsKey(p.id))
				res = discMap.get(p.id);
		}
		
		return res;
	}
}
