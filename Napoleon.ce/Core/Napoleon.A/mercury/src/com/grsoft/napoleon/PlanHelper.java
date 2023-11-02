package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.AgentPlanItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;
import com.grsoft.util.Pair;
import com.grsoft.util.Util;

public class PlanHelper {
	private static Map<String, Pair<Integer, Integer>> ords = new HashMap<String, Pair<Integer, Integer>>();
	private static Map<String, Pair<Integer, Integer>> dlvs = new HashMap<String, Pair<Integer, Integer>>();
	private static Map<String, Pair<Integer, Integer>> plans = new HashMap<String, Pair<Integer, Integer>>();
	private static Date created = null;
	
	public static AgentPlan getPlan(){
		DbReader reader = new DbReader();

		long now = Util.getDate().getTime();
		StringBuilder where = new StringBuilder();
		where.append("begin <= ").append(now).append(" and end >= ").append(now);
		
		AgentPlan data = new AgentPlan();
		boolean bdo = reader.select(data, DataObjectInfo.getInstance().getTableName(data.getClass()), where.toString());
		
		return bdo ? data : null;
	}
	
	private static void collectPlans(AgentPlan p){
		if(p != null && plans.size() == 0)
			for (AgentPlanItem item : p.items)
				plans.put(item.id, new Pair<Integer, Integer>(item.valueSum, item.valueWeight));
	}
	
	private static void collectOrders(Date begin, Date end, final Date skip) {
		ords.clear();
		
		DbReader reader = new DbReader();
		StringBuilder where = new StringBuilder();
		where.append("created >= ").append(begin.getTime()).append(" and created <= ").append(end.getTime());
		DataTraveler.travel(Order.class, new DataTraveler.Travel<Order>(){

		@Override
		public boolean travel(DataTraveler<Order> item) {
			Order data = item.data;
			
			if (data.created != skip)
				for (OrderItem i : data.items) {
					int q = i.qty;
					int s = (int) FPOperation.itemMul(i.cost, i.qty, Consts.QTY_SCALE);
	
					String id = i.id;
					Pair<Integer, Integer> pair = null;
	
					if (ords.containsKey(id))
						pair = ords.get(id);
					else
						pair = new Pair<Integer, Integer>(0, 0);
	
					s += pair.first;
					q += pair.second;
	
					ords.put(id, new Pair<Integer, Integer>(s, q));
				}
			
			return true;
		}}, where.toString());

		reader.close();
	}
	
	private static void collectDlvs(Date begin, Date end) {
		dlvs.clear();
		
		DbReader reader = new DbReader();
		StringBuilder where = new StringBuilder();
		where.append("created >= ").append(begin.getTime()).append(" and created <= ").append(end.getTime());
		DataTraveler.travel(Delivery.class, new DataTraveler.Travel<Delivery>(){

		@Override
		public boolean travel(DataTraveler<Delivery> item) {
			Delivery data = item.data;
			
			for (DeliveryItem i : data.items) {
				int q = i.qty;
				int s = i.sum;

				String id = i.id;
				Pair<Integer, Integer> pair = null;

				if (ords.containsKey(i.id))
					pair = ords.get(id);
				else
					pair = new Pair<Integer, Integer>(0, 0);

				s += pair.first;
				q += pair.second;

				ords.put(id, new Pair<Integer, Integer>(s, q));
			}
			return true;
		}}, where.toString());

		reader.close();
	}
	
	public static void init(){
		AgentPlan p = getPlan();
		
		if(p != null){
			collectPlans(p);
			collectDlvs(p.begin, p.end);
		}
	}
	
	public static void init(Date skip){
		AgentPlan p = getPlan();
		
		if(p != null){
			collectPlans(p);
			
			if(skip != created)
				collectOrders(p.begin, p.end, skip);
		}
	}
	
	public static int getPlanQty(String id){
		int result = 0;

		if (plans.containsKey(id))
			result = plans.get(id).second;

		return result;
	}
	
	public static int getOrdQty(String id){
		int result = 0;

		if (ords.containsKey(id))
			result = ords.get(id).second;

		return result;
	}
	
	public static int getDlvQty(String id){
		int result = 0;

		if (dlvs.containsKey(id))
			result = dlvs.get(id).second;

		return result;
	}

	
	public static int getPlanSum(String id){
		int result = 0;

		if (plans.containsKey(id))
			result = plans.get(id).first;

		return result;
	}
	
	public static int getOrdSum(String id){
		int result = 0;

		if (ords.containsKey(id))
			result = ords.get(id).first;

		return result;
	}
	
	public static int getDlvSum(String id){
		int result = 0;

		if (dlvs.containsKey(id))
			result = dlvs.get(id).first;

		return result;
	}
}
