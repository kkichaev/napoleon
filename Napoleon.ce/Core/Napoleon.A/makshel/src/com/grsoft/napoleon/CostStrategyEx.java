package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;


public class CostStrategyEx extends CostStrategy {
//	public static Set<String> actSet = null;
//	public static Map<String, Integer> discMap = new HashMap<String, Integer>();
	
	public int getItemCost(Price p, Document<?> doc) {
		int result = super.getItemCost(p, doc);
		
		if(doc != null && DocType.getCurDoc() == OrderDoc.instance()){
			int d = ((OrderImplEx)doc).getDisc(p);
			
			result -= (int)((long)result * d + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE);
		}

		return result;
	};
	
	public int getBaseItemCost(Price p, Document<?> doc) {
		return super.getItemCost(p, doc);
	};
	
//	private void initActionItems(){
//		actSet = new HashSet<String>();
//		
//		DataTraveler.travel(Action.class, new DataTraveler.Travel<Action>() {
//
//			@Override
//			public boolean travel(DataTraveler<Action> item) {
//				for(ActionItem i : item.data.items)
//					actSet.add(i.id);
//				return true;
//			}}, null);
//		
//		discMap.clear();
//		
//		DataTraveler.travel(OrgEx.class, new DataTraveler.Travel<OrgEx>() {
//
//			@Override
//			public boolean travel(DataTraveler<OrgEx> item) {
//				if(!discMap.containsKey(item.data.id))
//					discMap.put(item.data.id, item.data.discount);
//				return true;
//			}}, null);
//	}
}
