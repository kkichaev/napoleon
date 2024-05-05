package com.grsoft.dataobjects.impl;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grsoft.dataobjects.ActionBonusItem;
import com.grsoft.dataobjects.Actionable;
import com.grsoft.dataobjects.BonusItem;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.FolderEx;
import com.grsoft.dataobjects.OrderAction;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SimpleItem;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.util.Consts;

public class OrderImplEx extends OrderImpl implements Actionable {
	int whIndex = -1;

	int getWhIndex() {
		int index = -1;
		
		OrgImpl oi = new OrgImpl();
		oi.read("id", data.id);
		oi.close();
		
		index = ((OrgEx)oi.getData()).sklad;
		if( index < 0 )
			index = 0;
		
		return index;
	}
	
	@Override
	public int getItemValue(Price item) {
		if( whIndex == -1 )
			whIndex = getWhIndex();
		List<PriceQtyItem> whQty = ((PriceEx)item).whQty;
		
		int qty = ( whIndex == 0 || whIndex > whQty.size() ) ?  item.qty : whQty.get(whIndex-1).qty;
		if(((PriceEx)item).unitType == PriceEx.UNIT_PACK && item.qtyInPack != 0) {
			qty = (int)((long)qty * Consts.QTY_SCALE / item.qtyInPack);
		}
		return qty;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		if( whIndex == -1 ) 
			whIndex = getWhIndex();

		PriceEx pe = (PriceEx)price.getData();
		if( whIndex == 0 )
			super.updatePrice(price, qty);
		else if( whIndex <= pe.whQty.size() ) {
			pe.whQty.get(whIndex-1).qty += qty;
			price.write();
		}
	}

	@Override public List<SimpleItem> actions() {return ((OrderEx)data).actions;}

	@Override
	public void removeActions(Set<String> actions) {
		List<OrderItemEx> rmv = new ArrayList<>();

		OrderEx oe = (OrderEx) data;
		List<SimpleItem> rmvA = new ArrayList<>();
		for(SimpleItem si : oe.actions) {
			if(actions.contains(si.id)) rmvA.add(si);
		}
		oe.actions.removeAll(rmvA);

		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();

		for(OrderItem oi : data.items) {
			OrderItemEx oie = (OrderItemEx) oi;
			if(oie.action.length() == 0) continue;

			if(actions.isEmpty() || actions.contains(oie.action)) {
				p.id = oi.id;
				pi.read();

				if(oie.bonus > 0) {
					rmv.add(oie);
					if(data.whIndex ==0) p.qty += oie.qty;
					else p.whQty.get(data.whIndex - 1).qty += oie.qty;
					pi.write();
				} else {
					oie.cost = oie.costWOD;
					oie.action = "";
					oie.countSum();
				}
			}
		}
		data.items.removeAll(rmv);
		pi.close();
	}

	@Override
	public void commit() { write();}

	@Override
	public DataObject findItem(String itemId) {
		for(OrderItem oi : data.items) {
			if(oi.id.equals(itemId) && ((OrderItemEx)oi).bonus == 0)
				return oi;
		}
		return null;
	}

	void addAction(String actionid) {
		OrderEx se = (OrderEx) data;
		boolean have =false;
		for(SimpleItem si : se.actions) {
			if(si.id.equals(actionid)) {
				have = true;
				break;
			}
		}
		if(!have) {
			SimpleItem si = new SimpleItem();
			si.id = actionid;
			se.actions.add(si);
		}
	}

	@Override
	public void add(OrderAction action, List<ActionBonusItem> bonus) {
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		addAction(action.id);

		for(ActionBonusItem abi : bonus) {
			p.id = abi.id;
			pi.read();

			OrderItemEx oie = new OrderItemEx();
			oie.bonus = 1;
			oie.cost = 100;
			oie.sum = 100;
			oie.qty = abi.qty;
			oie.id = abi.id;
			oie.action = action.id;
			oie.costWOD = 100;
			data.items.add(oie);

			if(data.whIndex == 0) {
				p.qty -= abi.qty;
			} else {
				if(data.whIndex <= p.whQty.size()) {
					p.whQty.get(data.whIndex - 1).qty -= abi.qty;
				}
			}
			pi.write();
		}

		pi.close();
	}

	@Override
	public void setItem(String actId, PriceImpl pi, int qty, int cost, int discount) {
		addAction(actId);

		Price p = pi.getData();
		OrderItemEx oie = (OrderItemEx) findItem(p.id);
		if(oie == null) {
			oie = new OrderItemEx();
			data.items.add(oie);
		}
		if(oie.qty < qty) {
			int add = qty - oie.qty;
			oie.qty = qty;
			updatePrice(pi, -add);
		}
		oie.costWOD = cost;
		oie.action = actId;
		if(discount > 0) {
			oie.cost = (int)CostStrategy.costWithDiscount(cost, discount, Consts.SUM_SCALE);
		}
		oie.countSum();
	}

	private static class Res{
		public boolean val = false;
	}
	
	@Override
	public long write() {
		if(data.id.length() > 0){
			OrgImpl org = new OrgImpl();
			
			if(org.read("id", data.id)){
				OrgEx o = (OrgEx) org.getData();
				
				if(o.matrix == null || o.matrix.size() == 0){
					final Res f = new Res();
					final Map<Integer, FolderEx> folders = new HashMap<Integer, FolderEx>();
					DataTraveler.travel(FolderEx.class, new DataTraveler.Travel<FolderEx>(){

						@Override
						public boolean travel(DataTraveler<FolderEx> item) {
							if(!folders.containsKey(item.data.id)){
								folders.put(item.data.id, item.data);
								
								if(!f.val)
									f.val = item.data.required > 0;
									
								item.data = new FolderEx();	
							}
							return true;
						}}, null);
					
					if(f.val){
						boolean notcomplete = true;
						PriceImpl price = new PriceImpl();
						
						for(OrderItem i : data.items){
							if(price.read("id", i.id) && folders.containsKey(price.getData().folderID))
								notcomplete = folders.get(price.getData().folderID).required == 0;
								
							if (!notcomplete)
								break;
						}
						
						((OrderEx)data).notcomplete = notcomplete ? 1 : 0;
					}
				}
			}
		}
		
		return super.write();
	}
}
