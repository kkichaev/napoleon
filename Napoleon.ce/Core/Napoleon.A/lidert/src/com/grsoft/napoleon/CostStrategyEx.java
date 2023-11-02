package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.dataobjects.Card;
import com.grsoft.dataobjects.Discount;
import com.grsoft.dataobjects.DiscountItem;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DiscountImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	
	String id = "";
	int costIndex;
	HashMap<Integer, Integer> discounts = new HashMap<Integer, Integer>();
	static FolderTree tree = new FolderTree();
	
	public static void clearCache() { 
		tree.clear();
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		String docId = (doc == null || !(doc instanceof OrderImpl) ) ? "" : doc.getId();
		if( docId.length() == 0 )
			return super.getItemCost(p, doc);
		
		if( tree.size() == 0 )
			tree.load();
		
		if(id.equals(docId) == false) {
			loadData(doc);
		}
		
		int stdCost = p.cost.size() > 0 ? p.cost.get(0).cost : 0;
		if( doc != null && doc instanceof OrderImpl ) {
			if( costIndex >= 0 ) {
				int cost = Features.COST_MANAGER.getCost(p.id, costIndex);
				if( cost != 0 )
					return cost;
			}
			
			int dsc = 0;
			int fd = tree.findFolder(p.folderID);
			if( fd >= 0 ) {
				Folder f = tree.get(fd);
				do {
					if( discounts.containsKey((Integer)f.id) ) {
						dsc = discounts.get(f.id);
						break;
					}
					f = tree.getParent(f);
				} while (f != null);
			}
			if( dsc != 0 ) {
		         stdCost += (((long)stdCost * dsc) / Consts.SUM_SCALE + Consts.DISCOUNT_SCALE / 2) / Consts.DISCOUNT_SCALE;
			}
		}
		return stdCost;
	}

	protected void loadData(Document<?> doc) {
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx) oi.getData();
		oe.id = doc.getId();
		oi.read();
		oi.close();
		
		String card = ((OrderEx)doc.getData()).card;
		for(Card c : oe.cards) {
			if( c.id.equals(card)) {
				costIndex = Features.COST_MANAGER.getCostIndex(c.costype);
				break;
			}
		}
		
		discounts.clear();
		DiscountImpl di = new DiscountImpl();
		Discount d = di.getData();
		d.id = card;
		di.read();
		di.close();

		if( d.items != null ) {
			for(DiscountItem it : d.items) {
				discounts.put(it.id, -it.discount);
			}
		}
	}
}
