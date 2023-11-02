package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgGroupItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	private Map<String, Map<String, Integer>> orgGroupCash = new HashMap<String, Map<String, Integer>>();
	private Map<String, Integer> orgCash = new HashMap<String, Integer>();

	public CostStrategyEx() {
		resetCash();
	}

	public void resetCash() {
		orgGroupCash.clear();
		orgCash.clear();

		DbReader reader = new DbReader();
		OrgEx data = new OrgEx();

		boolean bdo = reader.select(data, DataObjectInfo.getInstance()
				.getTableName(data.getClass()), null);

		while (bdo) {
			if (!orgCash.containsKey(data.id))
				orgCash.put(data.id, data.disc);

			Map<String, Integer> discMap = null;
			if (!orgGroupCash.containsKey(data.id) && data.group.size() > 0) {
				discMap = new HashMap<String, Integer>();
				orgGroupCash.put(data.id, discMap);

				for (OrgGroupItem item : data.group)
					discMap.put(item.group, item.disc);
			}

			bdo = reader.selectNext(data);
		}
		reader.close();
	}

	@Override
	public int getItemCost(Price p, Document<?> doc) {

		if (doc != null) {
			int discount = getDiscount(p, doc);
			int cost = super.getItemCost(p, doc);

			if (discount != 0) {
				cost = calcDiscount(discount, cost);
			}

			return cost;
		} else
			return super.getItemCost(p, doc);
	}

	public static int calcDiscount(int discount, int cost) {
		int sign = (int) Math.signum(discount);
		cost += ((int) ((long) cost * Math.abs(discount) + Consts.SUM_SCALE
				* Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE))
				* sign;
		return cost;
	}

	public int getDiscount(Price p, Document<?> doc) {
		int discount = 0;

		if (doc != null) {
			if (orgGroupCash.containsKey(doc.getId())
					&& orgGroupCash.get(doc.getId()).containsKey(
							((PriceEx) p).group))
				discount = orgGroupCash.get(doc.getId()).get(
						((PriceEx) p).group);
			else if (orgCash.containsKey(doc.getId()))
				discount = orgCash.get(doc.getId());
		}

		return discount;
	}

	public int getBaseCost(Price p, Document<?> doc) {
		return super.getItemCost(p, doc);
	}
}
