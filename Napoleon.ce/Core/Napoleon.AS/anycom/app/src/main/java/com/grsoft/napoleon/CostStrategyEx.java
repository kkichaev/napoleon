package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.discount.ClientCard;
import com.grsoft.dataobjects.discount.DiscountCalc;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class CostStrategyEx extends CostStrategy {
	static String id = null;
	static Date date = null;
	static int costIndex = 0;

	static Map<String, Integer> costData = new HashMap<>();
	static DiscountCalc discountCalc = new DiscountCalc();

	static void loadOrgData(String orgId, OrderEx doc) {
		refreshOrg(orgId);

		Date chkDate = ((OrgEx)org).getDiscountDate(doc);
		if(date == null || date.compareTo(chkDate) != 0) {
			date = chkDate;
			costData.clear();
			costIndex = -1;
		}
		if(discountCalc == null)
			discountCalc = new DiscountCalc();
		discountCalc.load((OrgEx) org, (OrderEx) doc);
	}

	public static void resetCache() {
		id = null;
		discountCalc = null;
	}

	public List<KeyValue> clientCards(String orgid, OrderEx doc) {
		loadOrgData(orgid, doc);
		return discountCalc.clientCards();
	}

	static void load(int ci) {
		if(ci != costIndex) {
			costIndex = ci;

			String where = String.format("date <= %d", Util.getDayEnd(date).getTime());
			for(PriceCost pc : DbReader.fetch(PriceCost.class, where)) {
				if(pc.cost.length > costIndex && pc.cost[costIndex] != 0)
					costData.put(pc.id, pc.cost[costIndex]);
			}
		}
	}

	@Override
	public long getItemCost(Price p, Document<?> doc) {
		if(doc instanceof OrderImpl) {
			OrderItem oi = (OrderItem) ((OrderImpl)doc).findItem(p.id);
			if(oi != null) {
				return oi.cost;
			}

			loadOrgData(doc.getId(), (OrderEx) doc.getData());
		}
		return super.getItemCost(p, doc);
	}

	@Override
	protected int getPriceCost(Price p, int sumType, Document<?> doc) {
		if(sumType < 0)
			return 0;

		load(sumType);
		Integer cost = costData.get(p.id);
		if(cost == null)
			return 0;

		cost = discountCalc.calc(p, cost);
		return cost;
	}
}
