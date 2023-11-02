package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.FolderDiscountItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceDiscountItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.FolderDiscountImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceDiscountImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	static Map<String, PriceDiscountItem> items = new HashMap<String, PriceDiscountItem>();
	static Map<String, Integer> folders = new HashMap<String, Integer>();
	static String orgId = null;
	static int costType = 0;
	
	public static void resetCache() {
		orgId = null;
	}
	
	static void load(Document<?> doc) {
		String id = doc.getId();
		if(orgId == null || !orgId.equals(id)) {
			items.clear();
			folders.clear();
			
			OrgImpl oi = new OrgImpl();
			OrgEx o =(OrgEx) oi.getData();
			o.id = id;
			if(oi.read()) {
				costType = o.costype;				
			} else {
				costType = doc.getSumType();
			}
			oi.close();
			
			orgId = id;
			PriceDiscountImpl pdi = new PriceDiscountImpl();
			if(pdi.read("agreeId", o.agreeId))
				for(PriceDiscountItem pi : pdi.getData().items) {
					items.put(pi.id, pi);
				}
			
			FolderDiscountImpl fdi = new FolderDiscountImpl();
			if(fdi.read("agreeId", o.agreeId))
				for(FolderDiscountItem fi : fdi.getData().items) {
					folders.put(fi.id, fi.discount);
				}
		}
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if(doc == null)
			doc = new OrderImplEx();
		
		if(doc instanceof OrderImplEx && ! ((OrderImplEx)doc).isEditable()) {
			OrderItemEx oi = (OrderItemEx) ((OrderImplEx)doc).findItem(p.id);
			if(oi != null) {
				int cs = oi.cost;
				if(cs == 0 && oi.qty > 0)
					cs = (int)((long)oi.sum  * Consts.QTY_SCALE/ oi.qty);
				if(cs != 0)
					return cs;
			}
		}
		
		load(doc);
		int cost = super.getCostInt(p, doc, costType);
		PriceEx pe = (PriceEx)p;
		if(pe.noDiscount == 0) {
			PriceDiscountItem pdi = items.get(p.id);
			if(pdi != null) {
				if(pdi.costype >= 0 && pdi.costype != doc.getSumType()) {
					cost = super.getCostInt(p, doc, pdi.costype);
				}
				if(pdi.discount > 0)
					cost = costWithDiscount(cost, pdi.discount, Consts.SUM_SCALE);
			} else {
				Integer dsc = folders.get(pe.grpDiscountId);
				if(dsc != null) {
					cost = costWithDiscount(cost, dsc, Consts.SUM_SCALE);
				}
			}
		}

		return cost;
	}
}
