package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class CostStrategyEx extends CostStrategy {
	static int costIndex = 0;

	static Map<String, Integer> costData = new HashMap<>();

	public static void resetCache() {
		costIndex = -1;
	}

	static void load(int ci) {
		if(ci != costIndex) {
			costIndex = ci;

			for(PriceCost pc : DbReader.fetch(PriceCost.class)) {
				if(pc.cost.length > costIndex && pc.cost[costIndex] != 0)
					costData.put(pc.id, pc.cost[costIndex]);
			}
		}
	}

	@Override
	protected int getPriceCost(Price p, int sumType, Document<?> doc) {
		if(sumType < 0)
			return 0;

		load(sumType);
		Integer cost = costData.get(p.id);
		if(cost == null)
			return 0;
		return cost;
	}
}
