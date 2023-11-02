package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategy {
	public static CostStrategy defaultInstance = new CostStrategy(); 
	
	private static Map<Class<? extends Document<?>>, CostStrategy> strategies = 
		new HashMap<Class<? extends Document<?>>,CostStrategy>();
	
	public static CostStrategy getInstance(Class<? extends Document<?>> doc){
		CostStrategy result = strategies.get(doc);
		
		if (result == null)
			result = defaultInstance;
		
		return result;
	}
	
	public static void register(Class<? extends Document<?>> doc, CostStrategy strategy){
		strategies.put(doc, strategy);
	}
	
	/**
	 * Чтобы в каждом проекте не считать скидку - добавил в базоый класс
	 * @param cost
	 * @param discount
	 * @param discountScale
	 * @return
	 */
	public static int costWithDiscount(int cost, int discount, int discountScale) {
		double cd = (double)cost / Consts.SUM_SCALE;
		double dsc = (double)discount / (discountScale * 100.0);
		double sum = (cd * (1.0 - dsc) * Consts.SUM_SCALE) + 0.5;
		return (int) sum;
//		int scale = discountScale * Consts.SUM_SCALE; 
//		return cost - (int) (((long) cost * discount + scale / 2) / scale);		
	}

	public static long costWithDiscount(long cost, int discount, int discountScale) {
		double cd = (double)cost / Consts.SUM_SCALE;
		double dsc = (double)discount / (discountScale * 100.0);
		double sum = (cd * (1.0 - dsc) * Consts.SUM_SCALE) + 0.5;
		return (long) sum;
//		int scale = discountScale * Consts.SUM_SCALE; 
//		return cost - ((cost * discount + scale / 2) / scale);		
	}
	
	public int getItemCost(Price p, Document<?> doc){
		int sumType = doc != null ? doc.getSumType() : 0;
		return getCostInt(p, doc, sumType);
	}

	public int getCostInt(Price p, Document<?> doc, int sumType) {
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

	protected int getPriceCost(Price p, int sumType, Document<?> doc) {
		return (p.cost != null && p.cost.size() > sumType && sumType >= 0) ? 
				p.cost.get(sumType).cost : 0;
	}
}
