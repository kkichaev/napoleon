package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="AgentGroupPlan")
@ServerInfo(name="AgentGroupPlan")
public class AgentGroupPlan extends DataObject {
	@Scale(value = Consts.WEIGHT_SCALE)
	public int weight = 0;
	
	public int needSell = 0;
	
	public List<AgentGroupPlanItem> items = new ArrayList<AgentGroupPlanItem>();

	public static AgentGroupPlan getPlan() {
		Trvl t = new Trvl();
		DataTraveler.travel(AgentGroupPlan.class, t, "");
		return t.ret;
	}
	
	/**
	 * В структуре возвращает результат сколько кг остальось продать, сколко групп и расклад по обязательным
	 * @param o
	 * @return
	 */
	public AgentNeedSell check(OrderEx o) {
		
		AgentNeedSell ret = new AgentNeedSell();
		
		
		if( weight > 0 && needSell > 0 ) {
			HashMap<String, Integer> orderWeight = o.weightByGroup();
			ret.needSell = needSell;
			if( ret.needSell < items.size())
				ret.needSell = items.size();
			
			int addedGroups = 0;
			for(AgentGroupPlanItem agi : items) {
				Integer val = orderWeight.get(agi.id);
				if( val == null) {
					val = 0;
					addedGroups++;
				} else 
					ret.needSell--;
				
				if(val < agi.weight) {
					val = agi.weight - val;
					ret.weight += val;
					
					AgentGroupPlanItem agpi = new AgentGroupPlanItem();
					agpi.id = agi.id;
					agpi.weight = val;
					
					ret.items.add(agpi);
				}
				
				orderWeight.remove(agi.id);
			}
			
			class ReverceWeight implements Comparable<ReverceWeight> {
				public Entry<String, Integer> src;
				public ReverceWeight(Entry<String, Integer> src) { this.src = src; }
				
				@Override public int compareTo(ReverceWeight arg0) { return arg0.src.getValue() - src.getValue(); }
			}
			
			// отсортируем в обратном порядке чтобы выбрать группы с максимальным весом
			List<ReverceWeight> rw = new ArrayList<ReverceWeight>();
			for(Entry<String, Integer> kv : orderWeight.entrySet())
				rw.add(new ReverceWeight(kv));
			Collections.sort(rw);

			for(ReverceWeight rwi : rw) {
				if( (ret.needSell - addedGroups) <= 0 )
					break;
				int value = rwi.src.getValue();
				if(value < weight)
					ret.weight += (weight - value);
				ret.needSell--;
			}
			
			if( (ret.needSell - addedGroups) > 0)
				ret.weight += (ret.needSell - addedGroups) * weight;
		}
		
		return ret;
	}
}

class Trvl extends DataTraveler.Travel<AgentGroupPlan> {
	public AgentGroupPlan ret = null;
	
	@Override
	public boolean travel(DataTraveler<AgentGroupPlan> item) {
		ret = item.data;
		return false;
	}
	
}