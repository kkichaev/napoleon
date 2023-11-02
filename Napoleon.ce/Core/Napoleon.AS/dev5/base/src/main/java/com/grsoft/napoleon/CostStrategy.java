package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategy {
	static Map<String, Integer> costData = new HashMap<>();
	static int costIndex = -1;

	public static CostStrategy defaultInstance = new CostStrategy();

	public static CostStrategy getInstance(Class<? extends Document<?>> doc){
		return defaultInstance;
	}

	/**
	 * Чтобы в каждом проекте не считать скидку - добавил в базоый класс
	 * @param cost
	 * @param discount
	 * @param discountScale
	 * @return
	 */
	public static long costWithDiscount(long cost, long discount, int discountScale) {
		double cd = (double)cost / Consts.SUM_SCALE;
		double dsc = (double)discount / (discountScale * 100.0);
		double sum = (cd * (1.0 - dsc) * Consts.SUM_SCALE) + 0.5;
		return (long) sum;
	}

	public long getItemCost(Price p, Document<?> doc){
		int sumType = doc != null ? doc.getSumType() : 0;
		return getCostInt(p, doc, sumType);
	}

	public long getCostInt(Price p, Document<?> doc, int sumType) {
		int result = 0;
		if( Features.CAN_CHANGE_COST && doc != null && doc instanceof OrderImplBase<?>) {
			OrderItem oi = (OrderItem)((OrderImplBase<?>)doc).findItem(p.id);
			if( oi != null )
				return oi.cost;
		}
		if( Features.COST_MANAGER != null ) {
			result = Features.COST_MANAGER.getCost(p.id, sumType);
		}

		if(result == 0){
			result = getPriceCost(p, sumType, doc);
		}
		return result;
	}

	public static void resetCache() {
		costData = new HashMap<>();
		costIndex = -1;
	}

	static void load(int ci) {
		if(ci != costIndex) {
			costIndex = ci;
			costData.clear();

			DataTraveler.travel(PriceCost.class, new DataTraveler.Travel<PriceCost>() {
				@Override
				public boolean travel(DataTraveler<PriceCost> item) {
					if(item.data.cost.length > costIndex && item.data.cost[costIndex] != 0)
						costData.put(item.data.id, item.data.cost[costIndex]);
					return true;
				}
			}, "");
		}
	}

	protected int getPriceCost(Price p, int sumType, Document<?> doc) {
		if(sumType < 0)
			return 0;
		load(sumType);
		Integer cost = costData.get(p.id);
		return cost == null ? 0 : cost;
	}
}
