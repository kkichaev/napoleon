package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.grsoft.dataobjects.ActionResult;
import com.grsoft.dataobjects.ActionSklad;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ItemActionData;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ServikoAction;
import com.grsoft.dataobjects.ServikoActionItem;
import com.grsoft.dataobjects.ServikoActionItems;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ServikoActionItemsImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.FoldersAdapter;

public class CostStrategyEx extends CostStrategy {
	
	static String id = "";
	static String sklad = "";
	static Map<String, ItemActionData> actions = new HashMap<String, ItemActionData>();
	
	public static void clearCache() {
		id = "";
		sklad = "";
		actions.clear();
	}
	
	static void loadActions(String orgId, String skladid) {
		if(id.equals(orgId) && sklad.equals(skladid))
			return;
		
		id = orgId;
		sklad = skladid;
		actions.clear();
		FoldersAdapter.resetCache();
		
		final Map<String, List<ServikoAction>> data = new HashMap<String, List<ServikoAction>>(); 
		final ServikoActionItemsImpl sii = new ServikoActionItemsImpl();
		final ServikoActionItems sais = sii.getData();
		
		DataTraveler.travel(ServikoAction.class, new DataTraveler.Travel<ServikoAction>() {

			@Override
			public boolean travel(DataTraveler<ServikoAction> item) {
				boolean samesklad = (item.data.sklads.size() == 0);
				for(ActionSklad as : item.data.sklads) {
					if(as.id.equals(sklad)) {
						samesklad = true;
						break;
					}
				}
				if(!samesklad)
					return true;
				sais.id = item.data.id;
				if(sii.read()) {
					for(ServikoActionItem sai : sais.items) {
						List<ServikoAction> list = data.get(sai.id);
						if(list == null) {
							list = new ArrayList<ServikoAction>();
							data.put(sai.id, list);
						}
						ServikoAction ai = new ServikoAction(item.data);
						if(sai.value != 0)
							ai.value = sai.value;
						list.add(ai);
					}
				}
				return true;
			}
		}, "idOrg='" + orgId + "'");
		
		sii.close();

		for(Entry<String, List<ServikoAction>> kv : data.entrySet()) {
			ItemActionData iad = makeActionData(kv.getValue());
			actions.put(kv.getKey(), iad);
		}
	}
	
	static boolean canAdd(List<ServikoAction> dest, ServikoAction action) {
//		if(action.isAuto == 0)
//			return true;

//		"auto" вытесн€ютс€ другими "auto"
//		"не auto" вытесн€ютс€ "auto"
//		"не auto" не вытесн€ютс€ "не auto"
//		"auto" не вытесн€ютс€ "не auto"

		for(ServikoAction check : dest) {
			if(check.isAuto == 0 && action.isAuto == 0)
				continue;

//			if(check.isAuto != 0) {
//				if(action.isAuto == 0)
//					return false;
//			} else {
//				if(action.isAuto == 0)
//					return true;
//			}

			//есть друга€ промоакци€ с полным вытеснением не по приоритету
			// если уже есть строка по номенклатуре с другой ѕј с ¬ытеснениејкций =-2
			// и ¬ытеснениеѕоѕриоритету¬ƒокументе = Ћожь то текуща€ акци€ не срабатывает
			if(check.sameValue(-2, 1))
				return false;
			
			//есть друга€ промоакци€ с вытеснением по номенклатуре
			if(check.sameValue(-1, -1))
				return false;
			
			//есть друга€ промоакци€ с вытеснением по приоритету в строке
			if(check.sameValue(action.priority, -1))
				return false;

			//есть друга€ промоакци€ с вытеснением по приоритету в документе
			if(check.sameValue(action.priority, 1))
				return false;
		}
		
		return true;
	}
	
	private static ItemActionData makeActionData(List<ServikoAction> src) {
		ItemActionData ret = new ItemActionData();
		
		Collections.sort(src);
		List<ServikoAction> dest = new ArrayList<ServikoAction>();
		// два прохода, авто и не авто
		for(ServikoAction sab : src) {
			if(sab.isAuto == 0) continue;
			if(canAdd(dest, sab)) {
				dest.add(sab);
				ret.actions.add(sab);
			}
		}
		for(ServikoAction sab : src) {
			if(sab.isAuto != 0) continue;
			if(canAdd(dest, sab)) {
				dest.add(sab);
				ret.actions.add(sab);
			}
		}

		return ret;
	}
	
	public ItemActionData getActionData(Price p, Document<?> doc) {
		if(!(doc instanceof OrderImpl))
			return null;
		
		OrderEx order = (OrderEx) doc.getData();
		loadActions(order.id, order.whCode);
		return actions.get(p.id);
	}
	
	public List<String> getActionItems(Document<?> doc) {
		List<String> ret = new ArrayList<String>();
		
		if(doc instanceof OrderImpl) {
			OrderEx order = (OrderEx) doc.getData();
			loadActions(order.id, order.whCode);
			for(Entry<String, ItemActionData> kv : actions.entrySet()) {
				if(kv.getValue().actions.size() > 0)
					ret.add(kv.getKey());
			}
		}
		
		return ret;
	}
	
	public long getPriceCost(Price p, Document<?> doc) {
		return super.getItemCost(p, doc);
	}
	
	public long getOrgCost(Price p, Document<?> doc) {
		long cost = super.getItemCost(p, doc);
		if(doc instanceof OrderImpl) {
			cost = costWithDiscount(cost, -((OrderEx)doc.getData()).nac, Consts.SUM_SCALE);
		}
		
		return cost;
	}

	public ActionResult getOrderItemCost(Price p, OrderImpl doc) {
		ActionResult res = null;
		int cost = (int)super.getItemCost(p, doc);
		ItemActionData iad = getActionData(p, doc);
		if(iad != null) {
			OrderItemEx oie = (OrderItemEx) ((OrderImpl)doc).findItem(p.id);
			res = iad.count(cost, (oie != null) ? oie.promoId : "");
		}
		return res;
	}
	
	@Override
	public long getItemCost(Price p, Document<?> doc) {
		long cost = super.getItemCost(p, doc);
		if(doc instanceof OrderImpl) {
			ItemActionData iad = getActionData(p, doc);
			if(iad != null) {
				OrderItemEx oie = (OrderItemEx) ((OrderImpl)doc).findItem(p.id);
				cost = iad.count((int)cost, (oie != null) ? oie.promoId : "").cost;
			}
			cost = costWithDiscount(cost, -((OrderEx)doc.getData()).nac, Consts.SUM_SCALE);
		}
		
		return cost;
	}
}
